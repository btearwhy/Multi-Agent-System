package agent.behavior.part2.behavior;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:15
 * @description：An agent has a goal and move towards the goal until the goal is in reach
 * @modified By：
 * @version: $
 */

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.part2.CellMemory;
import agent.behavior.part2.DstarLite;
import agent.behavior.part2.MapMemory;
import agent.behavior.part2.Utils;
import environment.*;
import environment.world.agent.AgentRep;
import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;
import environment.world.wall.SolidWallRep;
import environment.world.wall.WallRep;

import java.util.*;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 18:15
 * @description：An agent has a goal and move towards the goal until the goal is in reach
 * @modified By：
 * @version: $
 */

public class Navigate extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        agentState.updateMapMemory();

        //生成轨迹
//        Coordinate cur = new Coordinate(agentState.getX(), agentState.getY());
//        List<Coordinate> trajectory = agentState.getMapMemory().getTrajectory(cur);
//        Iterator<Coordinate> it = trajectory.iterator();
//        Coordinate first = it.next();
//        String message = "navigate";
//        message += "|" + first.getX() + "," + first.getY();
//        while(it.hasNext()){
//            Coordinate c = it.next();
//            message += ";" + c.getX() + "," + c.getY();
//        }
//        //Handle Messages
//        Map<String, String> statusMap = new HashMap<>();
//        Map<String, List<Coordinate>> otherTrajectories = new HashMap<>();
//        for (Mail mail:agentCommunication.getMessages()){
//            String msg = mail.getMessage().split("|")[1];
//            String status = mail.getMessage().split("|")[0];
//            String sender = mail.getFrom();
//            statusMap.put(sender, status);
//            List<Coordinate> trajs = new ArrayList<>();
//            for (String cor : msg.split(";")){
//                String[] c = cor.split(",");
//                trajs.add(new Coordinate(Integer.parseInt(c[0]), Integer.parseInt(c[1])));
//            }
//            otherTrajectories.put(sender, trajs);
//        }
//        List<String> agentsConflict = new ArrayList<>();
//        for (Map.Entry<String, List<Coordinate>> entry:otherTrajectories.entrySet()){
//            List<Coordinate> otherTrajectory = entry.getValue();
//            for(int i = 0; i < trajectory.size() && i + 1< otherTrajectory.size(); i++){
//                if(trajectory.get(i).equals(otherTrajectory.get(i + 1)) || (i >= 1 && trajectory.get(i).equals(otherTrajectory.get(i - 1)))){
//                    agentsConflict.add(entry.getKey());
//                }
//            }
//        }
//
//
//        if(!agentsConflict.isEmpty()){
//            String nameOnPro = null;
//            for (Map.Entry<String, String> entry:statusMap.entrySet()){
//                if(entry.getValue().equals("avoid")){
//                    nameOnPro = entry.getKey();
//                }
//            }
//            if(nameOnPro == null){
//                agentsConflict.add(agentState.getName());
//                Collections.sort(agentsConflict);
//                nameOnPro = agentsConflict.get(0);
//            }
//            if(!nameOnPro.equals(agentState.getName())){
//                List<Coordinate> privilegeTraj = otherTrajectories.get(nameOnPro);
//                Coordinate desti = privilegeTraj.get(privilegeTraj.size() - 1);
//                agentState.addMemoryFragment("avoid", desti.getX() + "," + desti.getY());
//            }
//        }
//        agentCommunication.clearMessages();

        //send Messages


//        Perception perception = agentState.getPerception();
//        List<AgentRep> agents = new ArrayList<>();
//        for (CellPerception c:perception.getAllCells()){
//            if(c.getAgentRepresentation().isPresent() && !(c.getX() == agentState.getX() && c.getY() == agentState.getY())){
//                agents.add(c.getAgentRepresentation().get());
//            }
//        }
//        for (AgentRep agent:agents){
//            agentCommunication.sendMessage(agent, message);
//        }


    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {


        Coordinate cur = new Coordinate(agentState.getX(), agentState.getY());
        Coordinate goal = Utils.getCoordinateFromGoal(agentState);
        Coordinate next = agentState.getMapMemory().getNextMove(cur, goal);
        //Coordinate next = agentState.getDstarLite().getNextMove(cur, goal);

        if(next.equals(new Coordinate(-1, -1))) {
            if(Utils.trapped(agentState)){
                agentState.getMapMemory().getDstarLite().startOver(cur, goal);
                Coordinate n = agentState.getMapMemory().getNextMove(cur, goal);
                CellPerception m = agentState.getPerception().getCellPerceptionOnAbsPos(n.getX(), n.getY());
                if(m != null && m.isWalkable())
                    agentAction.step(n.getX(), n.getY());
                else agentAction.skip();
            }
            else{
                agentAction.skip();
            }
        }
        else{

            CellPerception m = agentState.getPerception().getCellPerceptionOnAbsPos(next.getX(), next.getY());
            if(m != null && m.isWalkable())
                agentAction.step(next.getX(), next.getY());
            else agentAction.skip();
        }
    }

    private void collideHandle(AgentState agentState, AgentAction agentAction, int intendedDir){
        int x = agentState.getX();
        int y = agentState.getY();
        if(agentState.getPerception().getCellPerceptionOnAbsPos(x + Utils.moves.get(intendedDir).getX(), y + Utils.moves.get(intendedDir).getY()) != null &&
                agentState.getPerception().getCellPerceptionOnAbsPos(x + Utils.moves.get(intendedDir).getX(), y + Utils.moves.get(intendedDir).getY()).containsAgent()) {
            Random ra = new Random();
            if (ra.nextInt(2) < 1) {
                agentAction.skip();
                return;
            }
        }
        intendedDir = Utils.getClockwiseDirectionIfBlocked(agentState, intendedDir);
        agentAction.step(x + Utils.moves.get(intendedDir).getX(), y + Utils.moves.get(intendedDir).getY());
    }
}
