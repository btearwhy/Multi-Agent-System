package agent.behavior.deliver;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/12 17:16
 * @description：An agent which pick up package and drop it at corresponding place
 * @modified By：
 * @version: $
 */

import java.awt.*;
import java.util.*;
import java.util.List;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.agent.Agent;
import environment.world.destination.Destination;
import environment.world.destination.DestinationRep;
import environment.world.generator.PacketGenerator;
import environment.world.generator.PacketGeneratorRep;
import environment.world.packet.Packet;
import environment.world.packet.PacketRep;
import util.Pair;

public class Deliver extends Behavior {
    static final List<Coordinate> moves = new ArrayList<>(List.of(
            new Coordinate(0, 1), new Coordinate(1, 1),
            new Coordinate(1, 0), new Coordinate(1, -1),
            new Coordinate(0, -1), new Coordinate(-1, -1),
            new Coordinate(-1, 0), new Coordinate(-1, 1)
    ));

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication

    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        var perception = agentState.getPerception();
        List<CellPerception> allCells = perception.getAllCells();
        CellPerception[] neighbors = perception.getNeighboursInOrder();
        if(!agentState.getMemoryFragmentKeys().contains("goal") && !agentState.getMemoryFragmentKeys().contains("previous")){
            agentState.addMemoryFragment("goal", "");
            agentState.addMemoryFragment("previous", ";");
        }


        if(!agentState.getMemoryFragment("goal").equals("")){
            if(agentState.hasCarry()){
                boolean findone = false;
                for(CellPerception neighbor:neighbors){
                    if(neighbor != null){
                        var destinationRep = neighbor.getRepOfType(DestinationRep.class);
                        if (destinationRep != null && agentState.getCarry().get().getColor().equals(destinationRep.getColor())){
                            agentAction.putPacket(destinationRep.getX(), destinationRep.getY());
                            findone = true;
                        }
                    }
                }
                if(findone){
                    setGoal(agentState, "");
                }
                else{
                    moveTowardsGoal(agentState, agentAction);
                }
            }
            else{
                String goal = agentState.getMemoryFragment("goal");
                Coordinate goalCor = getCoordinateFromGoal(goal);
                if(Perception.distance(goalCor.getX(), goalCor.getY(), agentState.getX(), agentState.getY()) >= 2)
                    moveTowardsGoal(agentState, agentAction);
                else{
                    String goalItem = goal.substring(goal.indexOf(":") + 1);
                    if(perception.getCellPerceptionOnAbsPos(goalCor.getX(), goalCor.getY()).containsPacket()){
                        if(goalItem.equals("packet:" + perception.getCellPerceptionOnAbsPos(goalCor.getX(), goalCor.getY()).getRepOfType(PacketRep.class).getColor())){
                            agentAction.pickPacket(goalCor.getX(), goalCor.getY());
                        }
                        else{
                            plan(agentAction, agentState);
                        }
                    }
                    else{
                        plan(agentAction, agentState);
                    }
                }
            }
        }
        else{
            boolean find = false;
            for (String key:agentState.getMemoryFragmentKeys()
                 ) {
                find = false;
                String data = agentState.getMemoryFragment(key);
                if(data.startsWith("destination")){
                    Color color = new Color(Integer.parseInt(data.substring(data.indexOf(":")+ 1)));
                    for (CellPerception cell:allCells) {
                        if(cell.containsPacket() && cell.getRepOfType(PacketRep.class).getColor().equals(color)){
                            if(Perception.distance(agentState.getX(), agentState.getY(), cell.getX(), cell.getY()) < 2){
                                agentAction.pickPacket(cell.getX(), cell.getY());
                                setGoal(agentState, key + ":" + data);
                                Coordinate goal = getCoordinateFromString(key);
                                updatePreviousDistanceFragment(agentState, String.valueOf(Perception.manhattanDistance(agentState.getX(), agentState.getY(), goal.getX(), goal.getY())));
                            }
                            else{
                                setGoal(agentState,  cell.getX() + "," + cell.getY() + ":packet");
                                updatePreviousDistanceFragment(agentState, String.valueOf(Perception.manhattanDistance(agentState.getX(), agentState.getY(), cell.getX(), cell.getY())));
                                moveTowardsGoal(agentState, agentAction);
                            }
                            find = true;
                            break;
                        }
                    }
                }
                if(find) break;
            }
            if(!find) wander(agentState, agentAction);
        }



        for(CellPerception cell : allCells){
            String coordinate = cell.getX() + "," + cell.getY();
            List<String> dataL = new ArrayList<>();

            if(cell.containsPacket()) dataL.add("packet:" + Integer.toString(cell.getRepOfType(PacketRep.class).getColor().getRGB()));
            PacketGeneratorRep packetGeneratorRep = cell.getRepOfType(PacketGeneratorRep.class);
            if(packetGeneratorRep != null){
                if (cell.containsPacket()){
                    dataL.add("packetGenerator:" + Integer.toString(packetGeneratorRep.getColor().getRGB()));
                }
            }
            if(cell.containsAnyDestination()){
                dataL.add("destination:" + Integer.toString(cell.getRepOfType(DestinationRep.class).getColor().getRGB()));
            }
            if(!dataL.isEmpty()){
                updateMemoryFragment(agentState, coordinate, String.join(";", dataL));
            }
            else {
                agentState.removeMemoryFragment(coordinate);
            }
        }

//        for (String k:agentState.getMemoryFragmentKeys()){
//            System.out.println(k + " :::: " + agentState.getMemoryFragment(k));
//        }
    }

    private Coordinate getCoordinateFromGoal(String s){
        String ss = s.substring(0, s.indexOf(":"));
        return getCoordinateFromString(ss);
    }

    private Coordinate getCoordinateFromString(String s){
        String[] coordinate = s.split(",");
        return new Coordinate(Integer.valueOf(coordinate[0]), Integer.valueOf(coordinate[1]));
    }

    private void setGoal(AgentState agentState, String content){
        agentState.removeMemoryFragment("goal");
        agentState.addMemoryFragment("goal", content);
    }

    private void plan(AgentAction agentAction, AgentState agentState){
        setGoal(agentState, "");
        clearPrevious(agentState);
        agentAction.skip();
    }


    private void wander(AgentState agentState, AgentAction agentAction){
        int dir;
        if(agentState.getMemoryFragment("previous").equals(";")){
            Random ra = new Random();
            dir = ra.nextInt(8);
            dir = getClockwiseDirection(agentState, dir);
            updatePreviousDistanceFragment(agentState, "1");
        }
        else{
            String previous = agentState.getMemoryFragment("previous");
            Coordinate preCor = getCoordinateFromString(previous.substring(0, previous.indexOf(";")));
            int preDir = getDir(preCor.getX(), preCor.getY(), agentState.getX(), agentState.getY());
            Random rb = new Random();
            int ran = rb.nextInt(100);
            int t = 7;
            if(ran < t) dir = (preDir + 3) % 8;
            else if(ran < 2 * t) dir = (preDir + 5) % 8;
            else if(ran < 4 * t) dir = (preDir + 1) % 8;
            else if(ran < 6 * t) dir = (preDir + 2) % 8;
            else if(ran < 8 * t) dir = (preDir + 6) % 8;
            else if(ran < 10 * t) dir = (preDir + 7) % 8;
            else dir = preDir;
            dir = getClockwiseDirection(agentState, dir);
        }
        step(agentState, agentAction, agentState.getX() + moves.get(dir).getX(), agentState.getY() + moves.get(dir).getY());

//        int dir;
//        if(agentState.getMemoryFragment("previous").equals(";")){
//            Random ra = new Random();
//            dir = ra.nextInt(8);
//            dir = getClockwiseDirection(agentState, dir);
//            updatePreviousDistanceFragment(agentState, "1");
//        }
//        else{
//            String previous = agentState.getMemoryFragment("previous");
//            int preDis = Integer.valueOf(previous.substring(previous.indexOf(";") + 1));
//            Coordinate preCor = getCoordinateFromString(previous.substring(0, previous.indexOf(";")));
//            dir = getDir(preCor.getX(), preCor.getY(), agentState.getX(), agentState.getY());
//            int newDir = getClockwiseDirection(agentState, dir);
//            if(preDis * preDis > agentState.getPerception().getPerceptionSize() || newDir != dir){
//                updatePreviousDistanceFragment(agentState, "1");
//                dir = newDir;
//            }
//            else{
//                updatePreviousDistanceFragment(agentState, String.valueOf(preDis + 1));
//            }
//        }
//        step(agentState, agentAction, agentState.getX() + moves.get(dir).getX(), agentState.getY() + moves.get(dir).getY());
    }

    private void updateMemoryFragment(AgentState agentState, String key, String data){
        if(agentState.getMemoryFragment(key) != null){
            agentState.removeMemoryFragment(key);
            agentState.addMemoryFragment(key, data);
        }
        else if(agentState.getNbMemoryFragments() == agentState.getMaxNbMemoryFragments()){
            for (String memorykey:agentState.getMemoryFragmentKeys()
                 ) {
                if (!memorykey.equals("goal") && !memorykey.equals("previous")&& agentState.getMemoryFragment(memorykey).startsWith("packet")) {
                    agentState.removeMemoryFragment(memorykey);
                    break;
                }
            }
            // When memory is full and if it doesn't find any memory about packet or generator, just remain and give up memorizing new stuff.
            // Or later code can be changed such that different memory contents can be weighted and forgotten based on comparison.
            agentState.addMemoryFragment(key,data);
        }
        else agentState.addMemoryFragment(key, data);
    }

    private int getDir(int x1, int y1, int x2, int y2){
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

    private int getClockwiseDirection(AgentState agentState, int dir){
        int i = 0;
        int x = agentState.getX();
        int y = agentState.getY();
        while(agentState.getPerception().getCellPerceptionOnAbsPos(x + moves.get((dir + i) % 8).getX(), y + moves.get((dir + i) % 8).getY()) == null ||
                !agentState.getPerception().getCellPerceptionOnAbsPos(x + moves.get((dir + i) % 8).getX(), y + moves.get((dir + i) % 8).getY()).isWalkable()){
            i++;
        }
        return (dir + i) % 8;
    }

    private void updatePreviousDistanceFragment(AgentState agentState, String s){
        String pre = agentState.getMemoryFragment("previous");
        String data = pre.substring(0, pre.indexOf(";") + 1);
        data += s;
        updatePreviousMemoryFragment(agentState, data);
    }

    private void clearPrevious(AgentState agentState){
        updatePreviousMemoryFragment(agentState, ";");
    }

    private void updatePreviousCoordinate(AgentState agentState, String s){
        String pre = agentState.getMemoryFragment("previous");
        String data = pre.substring(pre.indexOf(";"));
        data = s + data;
        updatePreviousMemoryFragment(agentState, data);
    }
    private void updatePreviousMemoryFragment(AgentState agentState, String s){
        agentState.removeMemoryFragment("previous");
        agentState.addMemoryFragment("previous", s);
    }

    private void step(AgentState agentState, AgentAction agentAction, int x, int y){
        updatePreviousCoordinate(agentState, agentState.getX() + "," + agentState.getY());
        if(!agentState.getMemoryFragment("goal").equals("") && agentState.getMemoryFragment("previous").endsWith(";")){
            Coordinate goal = getCoordinateFromGoal(agentState.getMemoryFragment("goal"));
            updatePreviousDistanceFragment(agentState, String.valueOf(Perception.manhattanDistance(goal.getX(), goal.getY(), agentState.getX(), agentState.getY())));
        }
        agentAction.step(x, y);
    }

    private void moveTowardsGoal(AgentState agentState, AgentAction agentAction){

        int x = agentState.getX();
        int y = agentState.getY();
        Coordinate goal = getCoordinateFromGoal(agentState.getMemoryFragment("goal"));
        int dir = getDir(x, y, goal.getX(), goal.getY());

        String previous = agentState.getMemoryFragment("previous");
        if(Integer.valueOf(previous.substring(previous.indexOf(";") + 1)) >= Perception.manhattanDistance(x, y, goal.getX(), goal.getY())){
            updatePreviousDistanceFragment(agentState, String.valueOf(Perception.manhattanDistance(x, y, goal.getX(), goal.getY())));
            dir = getClockwiseDirection(agentState, dir);
            step(agentState, agentAction, x + moves.get(dir).getX(), y + moves.get(dir).getY());
        }
        else{
            Coordinate preCor = getCoordinateFromString(previous.substring(0, previous.indexOf(";")));
            int prevDir = getDir(preCor.getX(), preCor.getY(), agentState.getX(), agentState.getY());
            int checkDir = (prevDir + 7) % 8;
            if(agentState.getPerception().getCellPerceptionOnAbsPos(x + moves.get(checkDir).getX(), y + moves.get(checkDir).getY()) == null ||
                    !agentState.getPerception().getCellPerceptionOnAbsPos(x + moves.get(checkDir).getX(), y + moves.get(checkDir).getY()).isWalkable()){
                dir = getClockwiseDirection(agentState, prevDir);
                step(agentState, agentAction, x + moves.get(dir).getX(), y + moves.get(dir).getY());
            }
            else{
                step(agentState, agentAction, x + moves.get(checkDir).getX(), y + moves.get(checkDir).getY());
            }
        }
    }
}