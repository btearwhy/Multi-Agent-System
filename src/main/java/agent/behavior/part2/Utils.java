package agent.behavior.part2;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 03:00
 * @description：Some utils methods
 * @modified By：
 * @version: $
 */

import agent.AgentAction;
import agent.AgentState;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import environment.CellPerception;
import agent.behavior.part2.Cor;
import environment.Perception;
import environment.Representation;
import environment.ActiveItemID;
import environment.EnergyValues;
import environment.world.agent.Agent;
import environment.world.destination.DestinationRep;
import environment.world.energystation.EnergyStation;
import environment.world.generator.PacketGeneratorRep;
import environment.world.packet.PacketRep;
import environment.world.agent.AgentRep;
import util.Pair;
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
    public static final List<Cor> moves = new ArrayList<>(List.of(
            new Cor(0, 1), new Cor(1, 1),
            new Cor(1, 0), new Cor(1, -1),
            new Cor(0, -1), new Cor(-1, -1),
            new Cor(-1, 0), new Cor(-1, 1)
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



    public static String getTargetFromGoal(AgentState agentState){
        JsonObject goal = new Gson().fromJson(agentState.getMemoryFragment("goal"), JsonObject.class);
        return goal.get("target").getAsString();
    }

    public static Color getTargetColorFromGoal(AgentState agentState){
        JsonObject goal = new Gson().fromJson(agentState.getMemoryFragment("goal"), JsonObject.class);
        return new Color(Integer.parseInt(goal.get("color").getAsString()));
    }

    public static Cor getCoordinateFromGoal(AgentState agentState){

        JsonObject goalObject = new Gson().fromJson(agentState.getMemoryFragment("goal"), JsonObject.class);
        JsonObject corObject = goalObject.getAsJsonObject("coordinate");
        Cor cor = new Cor(Integer.parseInt(corObject.get("x").getAsString()), Integer.parseInt(corObject.get("y").getAsString()));
        return cor;
    }


    public static void updateGoalCor(AgentState agentState, Cor cor){
        JsonObject goalObject = new Gson().fromJson(agentState.getMemoryFragment("goal"), JsonObject.class);
        goalObject.remove("coordinate");
        JsonObject corObject = new JsonObject();
        corObject.addProperty("x", String.valueOf(cor.getX()));
        corObject.addProperty("y", String.valueOf(cor.getY()));
        goalObject.add("coordinate", corObject);
        setGoal(agentState, goalObject);

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

    public static void updateTime(AgentState agentState){
        if(agentState.getMemoryFragment("time") == null){
            agentState.addMemoryFragment("time", String.valueOf(0));
        }
        else{
            agentState.addMemoryFragment("time", String.valueOf(Integer.parseInt(agentState.getMemoryFragment("time")) + 1));
        }
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

    public static boolean trapped(AgentState agentState){
        if(agentState.getMemoryFragment("stay") == null){
            agentState.addMemoryFragment("stay", String.valueOf(0));
        }
        int times = Integer.parseInt(agentState.getMemoryFragment("stay"));
        agentState.addMemoryFragment("stay", String.valueOf(++times));
        if(times == trapTimes) {
            agentState.removeMemoryFragment("stay");
            return true;
        }
        return false;
    }

    public static boolean isInReach(AgentState agentState, Cor coordinate){
        return agentState.getPerception().getCellPerceptionOnAbsPos(coordinate.getX(), coordinate.getY()) != null &&
                Perception.distance(agentState.getX(), agentState.getY(), coordinate.getX(), coordinate.getY()) < 2;
    }


}

