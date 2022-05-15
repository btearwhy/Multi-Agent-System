package agent.behavior.tast_delegation;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 03:00
 * @description：Some utils methods
 * @modified By：
 * @version: $
 */

import agent.AgentState;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.Representation;
import environment.ActiveItemID;
import environment.EnergyValues;
import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;
import environment.world.agent.AgentRep;
import environment.Mail;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 03:00
 * @description：Some utils methods
 * @modified By：
 * @version: $
 */

public class Utils {
    public static final List<Coordinate> moves = new ArrayList<>(List.of(
            new Coordinate(0, 1), new Coordinate(1, 1),
            new Coordinate(1, 0), new Coordinate(1, -1),
            new Coordinate(0, -1), new Coordinate(-1, -1),
            new Coordinate(-1, 0), new Coordinate(-1, 1)
    ));

    public static final int trapTimes = 5;


    public static int getDir(int x1, int y1, int x2, int y2){
        int dir = 0;
        if(x2 == x1 && y2 > y1) dir = 0;
        else if(x2 > x1 && y2 > y1) dir = 1;
        else if(x2 > x1 && y2 == y1) dir = 2;
        else if(x2 > x1 && y2 < y1) dir = 3;
        else if(x2 == x1 && y2 < y1) dir = 4;
        else if(x2 < x1 && y2 < y1) dir = 5;
        else if(x2 < x1 && y2 == y1) dir = 6;
        else if(x2 < x1 && y2 > y1) dir = 7;
        else dir = 8;
        return dir;
    }

    public static int getClockwiseDirectionIfBlocked(AgentState agentState, int dir){
        int i = 0;
        int x = agentState.getX();
        int y = agentState.getY();
        while(agentState.getPerception().getCellPerceptionOnAbsPos(x + moves.get((dir + i) % 8).getX(), y + moves.get((dir + i) % 8).getY()) == null ||
                !agentState.getPerception().getCellPerceptionOnAbsPos(x + moves.get((dir + i) % 8).getX(), y + moves.get((dir + i) % 8).getY()).isWalkable()){
            i++;
        }
        return (dir + i) % 8;
    }



    public static int getPreviousDir(AgentState agentState){
        CellPerception pre = agentState.getPerceptionLastCell();
        return getDir(pre.getX(), pre.getY(), agentState.getX(), agentState.getY());
    }



    public static String getTargetFromGoal(AgentState agentState){
        JsonObject goal = new Gson().fromJson(agentState.getMemoryFragment("goal"), JsonObject.class);
        return goal.get("target").getAsString();
    }

    public static Color getTargetColorFromGoal(AgentState agentState){
        JsonObject goal = new Gson().fromJson(agentState.getMemoryFragment("goal"), JsonObject.class);
        return new Color(Integer.parseInt(goal.get("color").getAsString()));
    }

    public static JsonObject getGoalJsonObj(AgentState agentState){
        return new Gson().fromJson(agentState.getMemoryFragment("goal"), JsonObject.class);
    }

    public static Coordinate getCoordinateFromGoal(AgentState agentState){
        JsonObject goalObject = new Gson().fromJson(agentState.getMemoryFragment("goal"), JsonObject.class);
        JsonObject corObject = goalObject.getAsJsonObject("coordinate");
        return new Coordinate(Integer.parseInt(corObject.get("x").getAsString()), Integer.parseInt(corObject.get("y").getAsString()));
    }



    public static void setGoal(AgentState agentState, JsonObject goalObject){
        agentState.addMemoryFragment("goal", goalObject.toString());
    }

    public static boolean hasGoal(AgentState agentState){
        return agentState.getMemoryFragment("goal") != null;
    }


    public static JsonObject searchGoal(AgentState agentState){
        return searchGoalInMemory(agentState);

    }

    public static JsonObject searchNearestDestination(AgentState agentState, Color color){
        JsonObject goal = null;
        List<CellMemory> destination = new ArrayList<>();
        List<CellMemory> cells = agentState.getAllCellsMemory();
        for (CellMemory c:cells){
            DestinationRep d = c.getRepOfType(DestinationRep.class);
            if(d != null && d.getColor().equals(color)){
                destination.add(c);
            }
        }
        if(destination.isEmpty()) return null;
        CellMemory m = Collections.min(destination, new Comparator<CellMemory>() {
            @Override
            public int compare(CellMemory o1, CellMemory o2) {
                return Perception.distance(agentState.getX(), agentState.getY(), o1.getX(), o1.getY())
                        - Perception.distance(agentState.getX(), agentState.getY(), o2.getX(), o2.getY());
            }
        });
        goal = new JsonObject();
        goal.addProperty("target", "destination");
        goal.addProperty("color", color.getRGB());
        JsonObject corObject = new JsonObject();
        corObject.addProperty("x", String.valueOf(m.getX()));
        corObject.addProperty("y", String.valueOf(m.getY()));
        goal.add("coordinate", corObject);
        return goal;
    }

    @Nullable
    public static JsonObject searchGoalInMemory(AgentState agentState){
        JsonObject goal = null;
        List<CellMemory> cells = agentState.getAllCellsMemory();
        Map<Color, Boolean> destination = new HashMap<>();
        List<CellMemory> packet = new ArrayList<>();
        for (CellMemory cell:cells){
            PacketRep pr = cell.getRepOfType(PacketRep.class);
            DestinationRep dr = cell.getRepOfType(DestinationRep.class);
            if(pr != null){
                packet.add(cell);
            }
            else if(dr != null){
                destination.put(dr.getColor(), true);
            }
        }
        Collections.sort(packet, new Comparator<CellMemory>() {
            @Override
            public int compare(CellMemory o1, CellMemory o2) {
                return Perception.distance(agentState.getX(), agentState.getY(), o1.getX(), o1.getY())
                        - Perception.distance(agentState.getX(), agentState.getY(), o2.getX(), o2.getY());
            }
        });


        if(agentState.getColor().isEmpty()){
            for(CellMemory c:packet){
                Color color = c.getRepOfType(PacketRep.class).getColor();
                if(destination.containsKey(color)){
                    JsonObject corObject = new JsonObject();
                    corObject.addProperty("x", String.valueOf(c.getX()));
                    corObject.addProperty("y", String.valueOf(c.getY()));
                    goal = new JsonObject();
                    goal.addProperty("target", "packet");
                    goal.addProperty("color", color.getRGB());
                    goal.add("coordinate", corObject);
                    break;
                }
            }
        }
        else{
            Color agentColor = agentState.getColor().get();
            for(CellMemory c:packet){
                Color color = c.getRepOfType(PacketRep.class).getColor();
                if(agentColor.equals(color) && destination.containsKey(color)){
                    JsonObject corObject = new JsonObject();
                    corObject.addProperty("x", String.valueOf(c.getX()));
                    corObject.addProperty("y", String.valueOf(c.getY()));
                    goal = new JsonObject();
                    goal.addProperty("target", "packet");
                    goal.addProperty("color", color.getRGB());
                    goal.add("coordinate", corObject);
                    break;
                }
            }
        }
        return goal;
    }

    public static boolean isInReach(AgentState agentState, Coordinate coordinate){
        return agentState.getPerception().getCellPerceptionOnAbsPos(coordinate.getX(), coordinate.getY()) != null &&
                Perception.distance(agentState.getX(), agentState.getY(), coordinate.getX(), coordinate.getY()) < 2;
    }



    public static boolean jsonarray_contain (JsonArray list, JsonObject object){
        for (int i = 0; i < list.size(); i ++){
            JsonObject packet = list.get(i).getAsJsonObject();
            if (packet.get("x").getAsInt() == object.get("x").getAsInt() &&
                packet.get("y").getAsInt() == object.get("y").getAsInt()){
                return true;
            }
        }

        return false;
    }
    


     public static boolean requestedQueueEmpty(AgentState agentState){
        if(agentState.getMemoryFragment("requested") == null) return true;
        else{
            JsonArray requested = new Gson().fromJson(agentState.getMemoryFragment("requested"),
                    JsonArray.class);
            return requested.size() == 0;
        }
     }

     public static JsonObject topRequestedQueue(AgentState agentState){
         JsonObject goal = new JsonObject();
         JsonArray requested = new Gson().fromJson(agentState.getMemoryFragment("requested"),
                 JsonArray.class);

         // add into goal
         goal.addProperty("target", "packet");
         goal.addProperty("color", agentState.getColor().get().getRGB());
         goal.add("coordinate", requested.get(0).getAsJsonObject());

         return goal;
     }
     public static JsonObject popRequestedQueue(AgentState agentState){
        JsonObject goal = new JsonObject();
        JsonArray requested = new Gson().fromJson(agentState.getMemoryFragment("requested"),
                 JsonArray.class);

         // add into goal
         goal.addProperty("target", "packet");
         goal.addProperty("color", agentState.getColor().get().getRGB());
         goal.add("coordinate", requested.get(0).getAsJsonObject());
         requested.remove(0);

         agentState.addMemoryFragment("requested", requested.toString());
         // no more requested goals, remove memory
         //if (requested.size() == 0) agentState.removeMemoryFragment("requested");
         return goal;
     }

     public static void pushRequestedQueue(AgentState agentState, JsonObject packetInfo){

         // doesn't have memory fragment "requested", create
         if (requestedQueueEmpty(agentState)){
             JsonArray requested = new JsonArray();

             requested.add(packetInfo);
             agentState.addMemoryFragment("requested",requested.toString());
         }

         // "requested" exists in memory
         JsonArray requested = new Gson().fromJson(agentState.getMemoryFragment("requested"),
                 JsonArray.class);
         // if agent already remember the packet don't put it into memory; if not add into memory
         if (!Utils.jsonarray_contain(requested, packetInfo)){
             requested.add(packetInfo);
             agentState.addMemoryFragment("requested",requested.toString());
         }
     }

     public static JsonObject getSafeDropPlaceAsGoal(AgentState agentState){
        CellPerception cell = null;
        int max = 0;
        for (CellPerception c:agentState.getPerception().getAllCells()){
            if(!c.isFree()) continue;
            List<CellPerception> neighbors = agentState.getPerception().getNeighbors(c);
            neighbors.removeIf(t -> t == null || !t.isWalkable() && !t.containsAgent());
            if(neighbors.size() > max){
                max = neighbors.size();
                cell = c;
            }
        }
         JsonObject goal = new JsonObject();
         JsonObject corObject = new JsonObject();
         corObject.addProperty("x", String.valueOf(cell.getX()));
         corObject.addProperty("y", String.valueOf(cell.getY()));
         goal.addProperty("target", "empty");
         goal.add("coordinate", corObject);
         return goal;

     }

     public static void addToAsked(AgentState agentState, JsonObject corObj){

         if (agentState.getMemoryFragment("asked") == null){
             JsonArray asked = new JsonArray();
             asked.add(corObj);
             agentState.addMemoryFragment("asked",asked.toString());
         }


         JsonArray asked = new Gson().fromJson(agentState.getMemoryFragment("asked"),
                 JsonArray.class);

         if (!Utils.jsonarray_contain(asked, corObj)){
             asked.add(corObj);
             agentState.addMemoryFragment("asked",asked.toString());
         }
     }

     public static boolean asked(AgentState agentState, JsonObject corObj){
        if(agentState.getMemoryFragment("asked") == null) return false;


         JsonArray asked = new Gson().fromJson(agentState.getMemoryFragment("asked"),
                 JsonArray.class);
         return jsonarray_contain(asked, corObj);
     }
}
