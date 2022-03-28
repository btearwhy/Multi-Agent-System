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
import com.google.gson.JsonObject;
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
        Coordinate goalCor = Utils.getCoordinateFromGoal(agentState);
        String target = Utils.getTargetFromGoal(agentState);
        Color color = Utils.getTargetColorFromGoal(agentState);
        CellPerception goalCell = perception.getCellPerceptionOnAbsPos(goalCor.getX(), goalCor.getY());
        if(target.equals("packet") || target.equals("generator")){
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
