package agent.behavior.basic;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 03:00
 * @description：Some utils methods
 * @modified By：
 * @version: $
 */

import agent.AgentAction;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.destination.DestinationRep;
import environment.world.generator.PacketGeneratorRep;
import environment.world.packet.PacketRep;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            String coordinate = cell.getX() + "," + cell.getY();
            if(cell.getRepOfType(PacketRep.class) != null){
                updateMemoryFragment(agentState, "packet|" + cell.getRepOfType(PacketRep.class).getColor().getRGB(), coordinate);
            }
            else{
                removeMemoryAtCorOnPrefix(agentState, "packet", getCoordinateFromString(coordinate));
            }
            if(cell.getRepOfType(PacketGeneratorRep.class) != null){
                updateMemoryFragment(agentState, "generator|" + cell.getRepOfType(PacketGeneratorRep.class).getColor().getRGB(), coordinate);
            }
            if(cell.getRepOfType(DestinationRep.class) != null){
                updateMemoryFragment(agentState, "destination|" + cell.getRepOfType(DestinationRep.class).getColor().getRGB(), coordinate);
            }
        }
    }

    public static void removeMemoryAtCorOnPrefix(AgentState agentState, String prefix, Coordinate coordinate){
        String cor = coordinate.getX() + "," + coordinate.getY();
        Set<String> keys = new HashSet<>(agentState.getMemoryFragmentKeys());
        for (String key:keys){
            if(key.startsWith(prefix)){
                String[] cors = agentState.getMemoryFragment(key).split(";");
                List<String> newRes = new ArrayList<>();
                for(String c : cors){
                    if(!c.equals(cor))
                        newRes.add(c);
                }
                agentState.removeMemoryFragment(key);
                if(!newRes.isEmpty()){
                    agentState.addMemoryFragment(key, String.join(";", newRes));
                }
            }
        }
    }


    public static void updateMemoryFragment(AgentState agentState, String key, String data){
        if(agentState.getMemoryFragment(key) != null){
            if(!agentState.getMemoryFragment(key).contains(data)){
                data = agentState.getMemoryFragment(key) + ";" + data;
                agentState.removeMemoryFragment(key);
                agentState.addMemoryFragment(key, data);
            }
        }
        else{
            if(agentState.getNbMemoryFragments() == agentState.getMaxNbMemoryFragments()){
                if(!key.startsWith("packet"))
                    forget(agentState);
            }
            agentState.addMemoryFragment(key, data);
        }
    }

    public static void forget(AgentState agentState){
        String chosenOne = null;
        int len = Integer.MAX_VALUE;
        for (String key:agentState.getMemoryFragmentKeys()){
            if (key.startsWith("packet") && agentState.getMemoryFragment(key).length() < len){
                len = agentState.getMemoryFragment(key).length();
                chosenOne = key;
            }
        }
        if(chosenOne != null) agentState.removeMemoryFragment(chosenOne);
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

    public static void updatePreviousDistanceFragment(AgentState agentState, String s){
        String pre = agentState.getMemoryFragment("previous");
        String data = pre.substring(0, pre.indexOf(";") + 1);
        data += s;
        updatePreviousMemoryFragment(agentState, data);
    }


    public static void updatePreviousCoordinate(AgentState agentState, String s){
        String pre = agentState.getMemoryFragment("previous");
        String data = pre.substring(pre.indexOf(";"));
        data = s + data;
        updatePreviousMemoryFragment(agentState, data);
    }
    public static void updatePreviousMemoryFragment(AgentState agentState, String s){
        agentState.removeMemoryFragment("previous");
        agentState.addMemoryFragment("previous", s);
    }

    public static void step(AgentState agentState, AgentAction agentAction, int x, int y){
        updatePreviousCoordinate(agentState, agentState.getX() + "," + agentState.getY());
        if(!agentState.getMemoryFragment("goal").equals("") && agentState.getMemoryFragment("previous").endsWith(";")){
            Coordinate goal = getCoordinateFromGoal(agentState.getMemoryFragment("goal"));
            updatePreviousDistanceFragment(agentState, String.valueOf(Perception.manhattanDistance(goal.getX(), goal.getY(), agentState.getX(), agentState.getY())));
        }
        agentAction.step(x, y);
    }


    public static String getTargetFromGoal(String goal){
        return goal.substring(goal.indexOf(";") + 1);
    }

    public static Coordinate getCoordinateFromGoal(String s){
        String ss = s.substring(0, s.indexOf(";"));
        return getCoordinateFromString(ss);
    }

    public static Coordinate getCoordinateFromString(String s){
        String[] coordinate = s.split(",");
        return new Coordinate(Integer.valueOf(coordinate[0]), Integer.valueOf(coordinate[1]));
    }

    public static void setGoal(AgentState agentState, String content){
        agentState.removeMemoryFragment("goal");
        agentState.addMemoryFragment("goal", content);
    }

    public static boolean hasGoal(AgentState agentState){
        return !agentState.getMemoryFragment("goal").equals(";");
    }



    public static String searchGoal(AgentState agentState){
        return searchGoalInMemory(agentState);

    }

    public static String searchNearestDestination(AgentState agentState, Color color){
        String goal = null;
        int minDis = Integer.MAX_VALUE;
        String target = "destination|" + color.getRGB();
        String destination = agentState.getMemoryFragment(target);
        String[] cors = destination.split(";");
        for(String cor:cors){
            Coordinate c = getCoordinateFromString(cor);
            int distance = Perception.distance(c.getX(), c.getY(), agentState.getX(), agentState.getY());
            if(distance < minDis){
                goal = cor + ";" + target;
                minDis = distance;
            }
        }
        return goal;
    }

    @Nullable
    public static String searchGoalInMemory(AgentState agentState){
        Set<String> targets = agentState.getMemoryFragmentKeys();
        String goal = null;
        int minDis = Integer.MAX_VALUE;
        for (String target:targets){
            if(target.equals("goal") || target.equals("previous"));
            else{
                if((target.startsWith("packet")) && !agentState.getMemoryFragment(target).equals("")){
                    Coordinate targetCor = getaTargetCorFromString(agentState.getMemoryFragment(target));
                    String destination = "destination|" + getColorFromTargetString(target).getRGB();
                    if(targets.contains(destination)){
                        int distance = Perception.distance(agentState.getX(), agentState.getY(), targetCor.getX(), targetCor.getY());
                        if(distance < minDis){
                            goal = targetCor.getX() + "," + targetCor.getY() + ";" + target;
                            minDis = distance;
                        }
                    }
                }
            }
        }
        return goal;
    }

    public static Coordinate getaTargetCorFromString(String cors){
        String oldestOne = cors.split(";")[0];
        String[] cor = oldestOne.split(",");
        return new Coordinate(Integer.valueOf(cor[0]), Integer.valueOf(cor[1]));
    }

    public static boolean isInReach(AgentState agentState, Coordinate coordinate){
        return agentState.getPerception().getCellPerceptionOnAbsPos(coordinate.getX(), coordinate.getY()) != null &&
                Perception.distance(agentState.getX(), agentState.getY(), coordinate.getX(), coordinate.getY()) < 2;
    }

    public static Color getColorFromTargetString(String target){
        return new Color(Integer.parseInt(target.substring(target.indexOf("|") + 1)));
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

