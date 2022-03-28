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
import java.util.Random;

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
        Coordinate goal = Utils.getCoordinateFromGoal(agentState);
        int dir = Utils.getDir(x, y, goal.getX(), goal.getY());
        if(!Utils.hasPreviousDis(agentState)) Utils.updatePreviousDistance(agentState, String.valueOf(Perception.manhattanDistance(x, y, goal.getX(), goal.getY())));
        if(Utils.getPreviousDis(agentState) >= Perception.manhattanDistance(x, y, goal.getX(), goal.getY())){
            Utils.updatePreviousDistance(agentState, String.valueOf(Perception.manhattanDistance(x, y, goal.getX(), goal.getY())));
            collideHandle(agentState, agentAction, dir);
        }
        else{
            int prevDir = Utils.getPreviousDir(agentState);
            int checkDir = (prevDir + 7) % 8;
            if(agentState.getPerception().getCellPerceptionOnAbsPos(x + Utils.moves.get(checkDir).getX(), y + Utils.moves.get(checkDir).getY()) == null ||
                    !agentState.getPerception().getCellPerceptionOnAbsPos(x + Utils.moves.get(checkDir).getX(), y + Utils.moves.get(checkDir).getY()).isWalkable()){
                collideHandle(agentState, agentAction, prevDir);
            }
            else{
                collideHandle(agentState, agentAction, checkDir);
                Utils.updatePreviousDir(agentState, (checkDir + 7) % 8);
            }
        }
        Utils.updateMemoryFragment(agentState);
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
        intendedDir = Utils.getClockwiseDirection(agentState, intendedDir);
        Utils.step(agentState, agentAction, x + Utils.moves.get(intendedDir).getX(), y + Utils.moves.get(intendedDir).getY());
    }
}
