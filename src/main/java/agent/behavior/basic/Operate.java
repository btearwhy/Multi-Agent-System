package agent.behavior.basic;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:38
 * @description：Agent picks up, drops or do anything except moving
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
 * @date       ：Created in 2022/3/19 18:38
 * @description：Agent picks up, drops or do anything except moving
 * @modified By：
 * @version: $
 */

public class Operate extends Behavior{
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        String goal = agentState.getMemoryFragment("goal");
        Coordinate goalCor = Utils.getCoordinateFromGoal(goal);
        String target = Utils.getTargetFromGoal(goal);
        Color color = Utils.getColorFromTargetString(target);
        CellPerception goalCell = perception.getCellPerceptionOnAbsPos(goalCor.getX(), goalCor.getY());
        if(target.startsWith("packet") || target.startsWith("generator")){
            if(goalCell.getRepOfType(PacketRep.class) != null && color.equals(goalCell.getRepOfType(PacketRep.class).getColor())){
                agentAction.pickPacket(goalCor.getX(), goalCor.getY());
            }
            else{
                agentAction.skip();
            }
        }
        else if(target.startsWith("destination")){
            agentAction.putPacket(goalCor.getX(), goalCor.getY());
        }
        else agentAction.skip();
        Utils.updateMemoryFragment(agentState);
    }
}
