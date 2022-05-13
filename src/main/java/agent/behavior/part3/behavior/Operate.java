package agent.behavior.part3.behavior;/**
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
import agent.behavior.part3.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.Perception;
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
        if (agentCommunication.getNbMessages() > 0){
            Collection<Mail> mails = agentCommunication.getMessages();
            for (Mail m : mails){
                JsonObject packet_request = new Gson().fromJson(m.getMessage(), JsonObject.class);

                // doesn't have memory fragment "be_requested", create
                if (agentState.getMemoryFragment("be_requested") == null){
                    JsonArray be_requested = new JsonArray();



                    be_requested.add(packet_request);
                    agentState.addMemoryFragment("be_requested",be_requested.toString());
                }

                // "be_requested" exists in memory
                JsonArray be_requested = new Gson().fromJson(agentState.getMemoryFragment("be_requested"),
                        JsonArray.class);
                // if agent already remember the packet don't put it into memory; if not add into memory
                if (!Utils.jsonarray_contain(be_requested, packet_request)){
                    be_requested.add(packet_request);
                }
            }

            // process all messages, clear
            agentCommunication.clearMessages();

        }
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentState.updateMapMemory();
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
        Utils.updateAgentNum(agentState);
    }
}
