package agent.behavior.basic;/**
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
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.packet.PacketRep;

import java.awt.*;

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
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        int x = agentState.getX();
        int y = agentState.getY();
        Coordinate goal = Utils.getCoordinateFromGoal(agentState.getMemoryFragment("goal"));
        int dir = Utils.getDir(x, y, goal.getX(), goal.getY());

        String previous = agentState.getMemoryFragment("previous");
        if(previous.endsWith(";")) Utils.updatePreviousDistanceFragment(agentState, String.valueOf(Perception.manhattanDistance(x, y, goal.getX(), goal.getY())));
        if(Integer.valueOf(previous.substring(previous.indexOf(";") + 1)) >= Perception.manhattanDistance(x, y, goal.getX(), goal.getY())){
            Utils.updatePreviousDistanceFragment(agentState, String.valueOf(Perception.manhattanDistance(x, y, goal.getX(), goal.getY())));
            dir = Utils.getClockwiseDirection(agentState, dir);
            Utils.step(agentState, agentAction, x + Utils.moves.get(dir).getX(), y + Utils.moves.get(dir).getY());
        }
        else{
            Coordinate preCor = Utils.getCoordinateFromString(previous.substring(0, previous.indexOf(";")));
            int prevDir = Utils.getDir(preCor.getX(), preCor.getY(), agentState.getX(), agentState.getY());
            int checkDir = (prevDir + 7) % 8;
            if(agentState.getPerception().getCellPerceptionOnAbsPos(x + Utils.moves.get(checkDir).getX(), y + Utils.moves.get(checkDir).getY()) == null ||
                    !agentState.getPerception().getCellPerceptionOnAbsPos(x + Utils.moves.get(checkDir).getX(), y + Utils.moves.get(checkDir).getY()).isWalkable()){
                dir = Utils.getClockwiseDirection(agentState, prevDir);
                Utils.step(agentState, agentAction, x + Utils.moves.get(dir).getX(), y + Utils.moves.get(dir).getY());
            }
            else{
                Utils.step(agentState, agentAction, x + Utils.moves.get(checkDir).getX(), y + Utils.moves.get(checkDir).getY());
            }
        }
        Utils.updateMemoryFragment(agentState);
    }
}
