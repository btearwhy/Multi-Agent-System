package agent.behavior.memory.behavior;/**
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
import agent.behavior.memory.Utils;
import environment.*;

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
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentState.updateMapMemory();
        Coordinate cur = new Coordinate(agentState.getX(), agentState.getY());
        Coordinate goal = Utils.getCoordinateFromGoal(agentState);
        Coordinate next = agentState.getMapMemory().getNextMove(cur, goal);
        if(next.getX() != -1 && next.getY() != -1){
            agentAction.step(next.getX(), next.getY());
        }
        else {
            agentAction.skip();
        }
    }
}
