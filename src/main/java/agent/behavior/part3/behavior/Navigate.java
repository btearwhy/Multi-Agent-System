package agent.behavior.part3.behavior;/**
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
import agent.behavior.part3.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import environment.*;
import environment.world.packet.PacketRep;

import java.awt.*;
import java.util.*;
import java.util.List;

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


        // receive request message and add into memory
        if (agentCommunication.getNbMessages() > 0){
            Collection<Mail> mails = agentCommunication.getMessages();
            for (Mail m : mails){
                JsonObject packet_request = new Gson().fromJson(m.getMessage(), JsonObject.class);
                Utils.pushRequestedQueue(agentState, packet_request);
            }
            // process all messages, clear
            agentCommunication.clearMessages();
        }


    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentState.updateMapMemory();
        Coordinate cur = new Coordinate(agentState.getX(), agentState.getY());
        Coordinate goal = Utils.getCoordinateFromGoal(agentState);
        Coordinate next = agentState.getMapMemory().getNextMove(cur, goal);
        if(next.getX() != -1 && next.getY() != -1){
            if(agentState.getMapMemory().trajContainsPacket(cur)){
                PacketRep obstacle = null;
                for (Coordinate traj : agentState.getMapMemory().getTrajectory(cur)){
                    CellPerception obj = agentState.getPerception().getCellPerceptionOnAbsPos(traj.getX(), traj.getY());
                    if(obj != null && obj.getPacketRepresentation().isPresent()){
                        obstacle = obj.getPacketRepresentation().get();
                        break;
                    }
                }
                if(obstacle != null){
                    JsonObject jsonCoordinate = new JsonObject();
                    jsonCoordinate.addProperty("x", obstacle.getX());
                    jsonCoordinate.addProperty("y", obstacle.getY());
                    if (obstacle.getColor().equals(agentState.getColor().get())) {
                        // add packet to be_requested
                        Utils.pushRequestedQueue(agentState, jsonCoordinate);
                        if(agentState.hasCarry()){
                            Utils.setGoal(agentState, Utils.getSafeDropPlaceAsGoal(agentState));
                            agentAction.skip();
                        }
                        else agentAction.skip();

                    }
                    else {
                        // add packet to request
                        if(Utils.asked(agentState, jsonCoordinate)){
                            if(!Utils.requestedQueueEmpty(agentState)){
                                if(agentState.hasCarry()){
                                    Utils.setGoal(agentState, Utils.getSafeDropPlaceAsGoal(agentState));
                                }
                                else{
                                    Utils.setGoal(agentState, Utils.popRequestedQueue(agentState));
                                }
                            }
                        }
                        else{
                            JsonObject object = new JsonObject();
                            object.addProperty("color", obstacle.getColor().getRGB());
                            object.add("coordinate", jsonCoordinate);
                            agentState.addMemoryFragment("request", object.toString());

                        }
                        agentAction.skip();
                    }
                }
                else {
                    agentAction.step(next.getX(), next.getY());
                }
            }
            else {
                agentAction.step(next.getX(), next.getY());
            }
        }
        else {
            agentAction.skip();
        }


        Utils.updateAgentNum(agentState);
    }
}
