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
import agent.behavior.part2.MapMemory;
import agent.behavior.part2.Utils;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.Representation;
import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;
import environment.world.wall.SolidWallRep;
import environment.world.wall.WallRep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        Coordinate cur = new Coordinate(x, y);
        Coordinate goal = Utils.getCoordinateFromGoal(agentState);
        int dir = agentState.getMapMemory().getDirection(cur, goal);
        if(dir == -1) agentAction.skip();
        else{
            CellPerception next = agentState.getPerception().getCellPerceptionOnAbsPos(x + Utils.moves.get(dir).getX(), y + Utils.moves.get(dir).getY());
            while(next == null){
                List<CellPerception> cellPerception = new ArrayList<>();
                CellMemory cellMemory = new CellMemory(x + Utils.moves.get(dir).getX(), y + Utils.moves.get(dir).getY(), true);
                cellPerception.add(cellMemory);
                agentState.recalculate(new Coordinate(agentState.getX(), agentState.getY()), agentState.getPerception().getAllCells());
                dir = agentState.getMapMemory().getDirection(cur, goal);
                if(dir == -1){
                    agentAction.skip();
                    return;
                }
                next = agentState.getPerception().getCellPerceptionOnAbsPos(x + Utils.moves.get(dir).getX(), y + Utils.moves.get(dir).getY());
            }
            if(next.containsAgent()){
                collideHandle(agentState, agentAction, dir);
            }
            else{
                agentAction.step(x + Utils.moves.get(dir).getX(), y + Utils.moves.get(dir).getY());
            }
        }
        agentState.recalculate(new Coordinate(agentState.getX(), agentState.getY()), agentState.getPerception().getAllCells());

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
