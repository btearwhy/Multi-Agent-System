package agent.behavior.tast_delegation.behavior;/**
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
import agent.behavior.tast_delegation.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.Perception;
import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;

import java.awt.*;
import java.util.Collection;

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
        // receive request message and add into memory
        Collection<Mail> mails = agentCommunication.getMessages();
        for (Mail m : mails){
            JsonObject packetInfo = new Gson().fromJson(m.getMessage(), JsonObject.class);
            Utils.pushRequestedQueue(agentState, packetInfo);
        }

        // process all messages, clear
        agentCommunication.clearMessages();
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentState.updateMapMemory();


        Perception perception = agentState.getPerception();
        Coordinate goalCor = Utils.getCoordinateFromGoal(agentState);
        String target = Utils.getTargetFromGoal(agentState);
        CellPerception goalCell = perception.getCellPerceptionOnAbsPos(goalCor.getX(), goalCor.getY());
        if(agentState.hasCarry()){
            if(goalCell.getRepOfType(DestinationRep.class) == null && !goalCell.isWalkable()){
                agentAction.skip();
                JsonObject jsonCoordinate = new JsonObject();
                jsonCoordinate.addProperty("x", Utils.getCoordinateFromGoal(agentState).getX());
                jsonCoordinate.addProperty("y", Utils.getCoordinateFromGoal(agentState).getY());
                Utils.pushRequestedQueue(agentState, jsonCoordinate);
                Utils.setGoal(agentState, Utils.getSafeDropPlaceAsGoal(agentState));
                return;
            }
            else
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
