package agent.behavior.part1b;/**
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
import com.google.gson.JsonObject;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.Representation;
import environment.world.destination.DestinationRep;
import environment.world.generator.PacketGeneratorRep;
import environment.world.packet.PacketRep;
import util.Pair;

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

    public static void updateMemoryFragment(AgentState agentState){
        Perception perception = agentState.getPerception();
        List<CellPerception> allCells = perception.getAllCells();
        for(CellPerception cell : allCells){
            Coordinate coordinate = new Coordinate(cell.getX(), cell.getY());
            if(cell.getRepOfType(PacketRep.class) != null){
                updateMemoryFragment(agentState, cell.getRepOfType(PacketRep.class), coordinate);
            }
            else{
                removeMemoryAtCor(agentState, "packet", coordinate);
            }
            if(cell.getRepOfType(PacketGeneratorRep.class) != null){
                updateMemoryFragment(agentState, cell.getRepOfType(PacketGeneratorRep.class), coordinate);
            }
            if(cell.getRepOfType(DestinationRep.class) != null){
                updateMemoryFragment(agentState, cell.getRepOfType(DestinationRep.class), coordinate);
            }
        }
    }

    public static void removeMemoryAtCor(AgentState agentState, String key, Coordinate coordinate){
        JsonObject obj = new Gson().fromJson(agentState.getMemoryFragment(key), JsonObject.class);
        if(obj == null) return;
        JsonArray packets = obj.get(key).getAsJsonArray();
        for(int i = 0; i < packets.size(); i++){
            JsonArray cors = packets.get(i).getAsJsonObject().get("coordinate").getAsJsonArray();
//            JsonObject cor = new JsonObject();
//            cor.addProperty("x", String.valueOf(coordinate.getX()));
//            cor.addProperty("y", String.valueOf(coordinate.getY()));
//            cors.remove(cor);
            for(int j = 0; j < cors.size(); j++){
                if(cors.get(j).getAsJsonObject().get("x").getAsInt() == coordinate.getX() && cors.get(j).getAsJsonObject().get("y").getAsInt() == coordinate.getY()){
                    if(cors.size() == 1){
                        packets.remove(i);
                    }
                    else {
                        cors.remove(j);
                    }
                    agentState.removeMemoryFragment(key);
                    agentState.addMemoryFragment(key, obj.toString());
                    return;
                }
            }
        }
    }


    public static void updateMemoryFragment(AgentState agentState, Representation representation, Coordinate coordinate){
        String key = "";
        Color color = null;
        if(representation instanceof PacketRep){
            key = "packet";
            color = ((PacketRep) representation).getColor();
        }
        else if(representation instanceof PacketGeneratorRep){
            key = "generator";
            color = ((PacketGeneratorRep) representation).getColor();
        }
        else if(representation instanceof DestinationRep){
            key = "destination";
            color = ((DestinationRep) representation).getColor();
        }
        if(agentState.getMemoryFragment(key) != null){
            JsonObject data = new Gson().fromJson(agentState.getMemoryFragment(key), JsonObject.class);
            JsonArray array = data.get(key).getAsJsonArray();
            boolean mark = false;
            for(int i = 0;i < array.size(); i++){
                JsonArray corsArray = array.get(i).getAsJsonObject().getAsJsonArray("coordinate");
                int colorJson = array.get(i).getAsJsonObject().get("color").getAsInt();
                if(colorJson == color.getRGB()){
                    mark = true;
                    for(int j = 0; j < corsArray.size(); j++){
                        JsonObject corObj = corsArray.get(j).getAsJsonObject();
                        int x = corObj.get("x").getAsInt();
                        int y = corObj.get("y").getAsInt();
                        if(x == coordinate.getX() && y == coordinate.getY()){
                            return;
                        }
                    }
                    JsonObject newCorObj = new JsonObject();
                    newCorObj.addProperty("x", String.valueOf(coordinate.getX()));
                    newCorObj.addProperty("y", String.valueOf(coordinate.getY()));
                    corsArray.add(newCorObj);
                }
            }
            if(!mark){
                JsonArray corsArray = new JsonArray();
                JsonObject newCorObj = new JsonObject();
                newCorObj.addProperty("x", String.valueOf(coordinate.getX()));
                newCorObj.addProperty("y", String.valueOf(coordinate.getY()));
                corsArray.add(newCorObj);
                JsonObject newObj = new JsonObject();
                newObj.addProperty("color", color.getRGB());
                newObj.add("coordinate", corsArray);
                array.add(newObj);
            }
            agentState.removeMemoryFragment(key);
            agentState.addMemoryFragment(key, data.toString());
        }
        else{
            if(agentState.getNbMemoryFragments() == agentState.getMaxNbMemoryFragments()){
                if(!key.equals("packet"))
                    forget(agentState);
            }
            JsonObject basicObj = new JsonObject();
            JsonArray basicArray = new JsonArray();
            JsonObject itemObj = new JsonObject();
            JsonArray corArray = new JsonArray();
            JsonObject corObject = new JsonObject();
            corObject.addProperty("x", String.valueOf(coordinate.getX()));
            corObject.addProperty("y", String.valueOf(coordinate.getY()));
            corArray.add(corObject);
            itemObj.addProperty("color", color.getRGB());
            itemObj.add("coordinate", corArray);
            basicArray.add(itemObj);
            basicObj.add(key, basicArray);
            agentState.addMemoryFragment(key, basicObj.toString());
        }
    }

    public static void forget(AgentState agentState){
        String chosenOne = null;
        agentState.removeMemoryFragment("packet");
//        int len = Integer.MAX_VALUE;
//        for (String key:agentState.getMemoryFragmentKeys()){
//            if (key.equals("packet") && agentState.getMemoryFragment(key).length() < len){
//                len = agentState.getMemoryFragment(key).length();
//                chosenOne = key;
//            }
//        }
//        if(chosenOne != null) agentState.removeMemoryFragment(chosenOne);
    }

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

    public static int getClockwiseDirection(AgentState agentState, int dir){
        int i = 0;
        int x = agentState.getX();
        int y = agentState.getY();
        while(agentState.getPerception().getCellPerceptionOnAbsPos(x + moves.get((dir + i) % 8).getX(), y + moves.get((dir + i) % 8).getY()) == null ||
                !agentState.getPerception().getCellPerceptionOnAbsPos(x + moves.get((dir + i) % 8).getX(), y + moves.get((dir + i) % 8).getY()).isWalkable()){
            i++;
        }
        return (dir + i) % 8;
    }

    public static void updatePreviousDistance(AgentState agentState, String s){
        JsonObject status = new Gson().fromJson(agentState.getMemoryFragment("status"), JsonObject.class);
        status.getAsJsonObject("previous").remove("distance");
        status.getAsJsonObject("previous").addProperty("distance", s);
        agentState.removeMemoryFragment("status");
        agentState.addMemoryFragment("status", status.toString());
    }


    public static void updatePreviousDir(AgentState agentState, int dir){
        JsonObject status = new Gson().fromJson(agentState.getMemoryFragment("status"), JsonObject.class);
        status.getAsJsonObject("previous").remove("direction");
        status.getAsJsonObject("previous").addProperty("direction", String.valueOf(dir));
        agentState.removeMemoryFragment("status");
        agentState.addMemoryFragment("status", status.toString());
    }


    public static int getPreviousDir(AgentState agentState){
        JsonObject previousObject = new Gson().fromJson(agentState.getMemoryFragment("status"), JsonObject.class);
        return Integer.valueOf(previousObject.getAsJsonObject("previous").get("direction").getAsString());
    }
    public static void step(AgentState agentState, AgentAction agentAction, int x, int y){
        int prevDir = getDir(agentState.getX(), agentState.getY(), x, y);
        updatePreviousDir(agentState, prevDir);
        agentAction.step(x, y);
    }

    public static int getPreviousDis(AgentState agentState){
        JsonObject status = new Gson().fromJson(agentState.getMemoryFragment("status"), JsonObject.class);
        return status.getAsJsonObject("previous").get("distance").getAsInt();
    }

    public static String getTargetFromGoal(AgentState agentState){
        JsonObject goal = new Gson().fromJson(agentState.getMemoryFragment("status"), JsonObject.class).getAsJsonObject("goal");
        return goal.get("target").getAsString();
    }

    public static Color getTargetColorFromGoal(AgentState agentState){
        JsonObject goal = new Gson().fromJson(agentState.getMemoryFragment("status"), JsonObject.class).getAsJsonObject("goal");
        return new Color(Integer.parseInt(goal.get("color").getAsString()));
    }

    public static Coordinate getCoordinateFromGoal(AgentState agentState){
        JsonObject statusObject = new Gson().fromJson(agentState.getMemoryFragment("status"), JsonObject.class);
        JsonObject corObject = statusObject.getAsJsonObject("goal").getAsJsonObject("coordinate");
        Coordinate cor = new Coordinate(Integer.valueOf(corObject.get("x").getAsString()), Integer.valueOf(corObject.get("y").getAsString()));
        return cor;
    }



    public static void setGoal(AgentState agentState, JsonObject goalObject){
        JsonObject statusObject = new Gson().fromJson(agentState.getMemoryFragment("status"), JsonObject.class);
        if(statusObject.has("goal")){
            statusObject.remove("goal");
        }
        statusObject.add("goal", goalObject);
        agentState.removeMemoryFragment("status");
        agentState.addMemoryFragment("status", statusObject.toString());
    }

    public static boolean hasGoal(AgentState agentState){
        return agentState.getMemoryFragment("status").contains("goal");
    }

    public static boolean hasPreviousDis(AgentState agentState){
        JsonObject status = new Gson().fromJson(agentState.getMemoryFragment("status"), JsonObject.class);
        return status.has("previous") && status.getAsJsonObject("previous").has("distance");
    }


    public static JsonObject searchGoal(AgentState agentState){
        return searchGoalInMemory(agentState);

    }

    public static JsonObject searchNearestDestination(AgentState agentState, Color color){
        JsonObject goal = null;
        int minDis = Integer.MAX_VALUE;
        String target = "destination";
        String destination = agentState.getMemoryFragment(target);
        JsonObject desObj = new Gson().fromJson(destination, JsonObject.class);
        JsonArray desArray = desObj.getAsJsonArray("destination");
        for (int i = 0; i < desArray.size(); i++){
            JsonObject des = desArray.get(i).getAsJsonObject();
            if(des.get("color").getAsInt() == color.getRGB()){
                JsonArray cors = des.getAsJsonArray("coordinate");
                for (int j = 0; j < cors.size(); j++){
                    int distance = Perception.distance(cors.get(j).getAsJsonObject().get("x").getAsInt(), cors.get(j).getAsJsonObject().get("y").getAsInt(), agentState.getX(), agentState.getY());
                    if(distance < minDis){
                        goal = new JsonObject();
                        goal.addProperty("target", "destination");
                        goal.addProperty("color", color.getRGB());
                        goal.add("coordinate", cors.get(j).getAsJsonObject());
                        minDis = distance;
                    }
                }
            }
        }
        return goal;
    }

    @Nullable
    public static JsonObject searchGoalInMemory(AgentState agentState){
        JsonObject goal = null;
        int minCost = Integer.MAX_VALUE;
        String target = agentState.getMemoryFragment("packet");
        String destination = agentState.getMemoryFragment("destination");
        if(target == null || destination == null) return null;
        JsonArray packetArray = new Gson().fromJson(target, JsonObject.class).getAsJsonArray("packet");
        JsonArray destiArray = new Gson().fromJson(destination, JsonObject.class).getAsJsonArray("destination");
        for (int i = 0; i < packetArray.size(); i++){
            Color pacColor = getColorFromTarget(packetArray.get(i).getAsJsonObject());
            for (int j = 0; j < destiArray.size(); j++){
                Color destiColor = getColorFromTarget(destiArray.get(j).getAsJsonObject());
                if(pacColor.equals(destiColor)){
                    Pair<Coordinate, Coordinate> minimal = getMinimalDistancePair(packetArray.get(i).getAsJsonObject().get("coordinate").getAsJsonArray(),
                            destiArray.get(j).getAsJsonObject().get("coordinate").getAsJsonArray());
                    if(minimal != null){
                        int cost = Perception.distance(minimal.first.getX(), minimal.first.getY(), agentState.getX(), agentState.getY()) +
                                2 * Perception.distance(minimal.first.getX(), minimal.first.getY(), minimal.second.getX(), minimal.second.getY());
                        if(cost < minCost){
                            minCost = cost;
                            JsonObject corObject = new JsonObject();
                            corObject.addProperty("x", String.valueOf(minimal.first.getX()));
                            corObject.addProperty("y", String.valueOf(minimal.first.getY()));
                            goal = new JsonObject();
                            goal.addProperty("target", "packet");
                            goal.addProperty("color", pacColor.getRGB());
                            goal.add("coordinate", corObject);
                        }
                    }
                }
            }
        }
        return goal;
    }

    public static Pair<Coordinate, Coordinate> getMinimalDistancePair(JsonArray from, JsonArray to){
        Pair<Coordinate, Coordinate> res = null;
        int min_dis = Integer.MAX_VALUE;
        for (int i = 0; i < from.size(); i++){
            for (int j = 0; j < to.size(); j++){
                JsonObject fromObj = from.get(i).getAsJsonObject();
                JsonObject toObj = to.get(j).getAsJsonObject();
                Coordinate cor1 = new Coordinate(Integer.valueOf(fromObj.get("x").getAsString()), Integer.valueOf(fromObj.get("y").getAsString()));
                Coordinate cor2 = new Coordinate(Integer.valueOf(toObj.get("x").getAsString()), Integer.valueOf(toObj.get("y").getAsString()));
                int dis = Perception.distance(cor1.getX(), cor1.getY(), cor2.getX(), cor2.getY());
                if(dis < min_dis){
                    min_dis = dis;
                    res = new Pair<>(cor1, cor2);
                }
            }
        }
        return res;
    }

    public static boolean isInReach(AgentState agentState, Coordinate coordinate){
        return agentState.getPerception().getCellPerceptionOnAbsPos(coordinate.getX(), coordinate.getY()) != null &&
                Perception.distance(agentState.getX(), agentState.getY(), coordinate.getX(), coordinate.getY()) < 2;
    }

    public static Color getColorFromTarget(JsonObject target){
        return new Color(Integer.parseInt(target.get("color").getAsString()));
    }



//    public static void memorizeForcely(AgentState agentState, String key, Coordinate coordinate){
//
//    }
//
//    public static String searchDesInMemAndPacketInEnv(AgentState agentState){
//        Set<String> targets = agentState.getMemoryFragmentKeys();
//        String goal = null;
//        int minDis = Integer.MAX_VALUE;
//        for (String target:targets){
//            if(target.equals("goal") || target.equals("previous"));
//            else{
//                if(target.startsWith("destination")){
//                    Color desColor = getColorFromTargetString(target);
//
//                }
//            }
//        }
//        return goal;
//    }
//
//    @Nullable
//    public static String searchGoalInPerception(AgentState agentState){
//        int minDis = Integer.MAX_VALUE;
//        String goal = null;
//        List<CellPerception> cells = agentState.getPerception().getAllCells();
//        for (CellPerception cell:cells){
//            Color color = null;
//            if(cell.getRepOfType(PacketGeneratorRep.class) != null){
//                color = cell.getRepOfType(PacketGeneratorRep.class).getColor();
//            }
//            else if(cell.getRepOfType(PacketRep.class) != null){
//                color = cell.getRepOfType(PacketRep.class).getColor();
//            }
//            if (color != null){
//                CellPerception finalDes = null;
//                for(CellPerception des:cells){
//                    DestinationRep destinationRep = des.getRepOfType(DestinationRep.class);
//                    if(destinationRep != null && destinationRep.getColor().equals(color)){
//                        int distance = Perception.distance(agentState.getX(), agentState.getY(), cell.getX(), cell.getY());
//                        if(minDis > distance){
//                            minDis = distance;
//                            goal = cell.getX() + "," + cell.getY() + ";packet|" + color.getRGB();
//                            finalDes = des;
//                        }
//                    }
//                }
//                if(finalDes != null){
//                    memorizeForcely(agentState, "destination|" + finalDes.getRepOfType(DestinationRep.class).getColor().getRGB(), new Coordinate(finalDes.getX(), finalDes.getY()));
//                }
//            }
//        }
//        return goal;
//    }
}

