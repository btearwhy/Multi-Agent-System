package agent.behavior.memory.behavior;/**
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
import agent.behavior.memory.Utils;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.packet.PacketRep;

import java.awt.*;

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
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentState.updateMapMemory();


        Perception perception = agentState.getPerception();
        Coordinate goalCor = Utils.getCoordinateFromGoal(agentState);
        String target = Utils.getTargetFromGoal(agentState);
        CellPerception goalCell = perception.getCellPerceptionOnAbsPos(goalCor.getX(), goalCor.getY());
        if(agentState.hasCarry()){
            agentAction.putPacket(goalCor.getX(), goalCor.getY());
        }
        else {
            if(target.equals("packet")){
                Color color = Utils.getTargetColorFromGoal(agentState);
                if(goalCell.getRepOfType(PacketRep.class) != null && color.equals(goalCell.getRepOfType(PacketRep.class).getColor())){
                    agentAction.pickPacket(goalCor.getX(), goalCor.getY());
                }
                else{
                    agentAction.skip();
                }
            }
            else agentAction.skip();
        }
        agentState.removeMemoryFragment("goal");
    }
}
