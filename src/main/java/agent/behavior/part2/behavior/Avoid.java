package agent.behavior.part2.behavior;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.part2.Utils;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.agent.AgentRep;

import java.util.*;

/**
 * @author ：mmzs
 * @date ：Created in 2022/5/5 21:26
 * @description：
 * @modified By：
 * @version: $
 */

public class Avoid extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        agentState.updateMapMemory();
        // No communication
        Perception perception = agentState.getPerception();
        List<AgentRep> agents = new ArrayList<>();
        for (CellPerception c:perception.getAllCells()){
            if(c.getAgentRepresentation().isPresent() && !(c.getX() == agentState.getX() && c.getY() == agentState.getY())){
                agents.add(c.getAgentRepresentation().get());
            }
        }
        String message = "avoid";
        Coordinate cur = new Coordinate(agentState.getX(), agentState.getY());
        List<Coordinate> trajectory = agentState.getMapMemory().getTrajectory(cur);
        Iterator<Coordinate> it = trajectory.iterator();
        Coordinate first = it.next();
        message += "|" + first.getX() + "," + first.getY();
        while(it.hasNext()){
            Coordinate c = it.next();
            message += ";" + c.getX() + "," + c.getY();
        }

        for (AgentRep agent:agents){
            agentCommunication.sendMessage(agent, message);
        }
        agentCommunication.clearMessages();
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        Coordinate cur = new Coordinate(agentState.getX(), agentState.getY());
        String[] cors = agentState.getMemoryFragment("avoid").split(",");
        Coordinate goal = new Coordinate(Integer.parseInt(cors[0]), Integer.parseInt(cors[1]));

        if(cur.equals(goal)) agentAction.skip();
        else{
            List<CellPerception> cellPerceptions = agentState.getPerception().getAllCells();
            Collections.sort(cellPerceptions, new Comparator<CellPerception>() {
                @Override
                public int compare(CellPerception o1, CellPerception o2) {
                    return Perception.distance(o1.getX(), o1.getY(), agentState.getX(), agentState.getY())
                            - Perception.distance(o2.getX(), o2.getY(), agentState.getX(), agentState.getY());
                }
            });
            List<Coordinate> trajectory = agentState.getMapMemory().getTrajectory(cur);
            for (CellPerception c:cellPerceptions){
                Coordinate cor = new Coordinate(c.getX(), c.getY());
                if(c.isWalkable() && !trajectory.contains(cor)){
                    agentState.removeMemoryFragment("avoid");
                    agentState.addMemoryFragment("avoid", c.getX() + "," + c.getY());
                    goal = cor;
                }
            }
            Coordinate next = agentState.getMapMemory().getNextMove(cur, goal);
            if(next.getX() == -1 && next.getY() == -1){
                agentAction.skip();
            }
            else{
                agentAction.step(next.getX(), next.getY());
            }
        }
        Utils.updateAgentNum(agentState);
    }
}
