package agent.behavior.basic;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Delivering extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        // get perception
        var perception = agentState.getPerception();

        // get neighbors
        CellPerception[] neighbors = perception.getNeighbours();

        // get packet color
        Color packetColor = agentState.getCarry().get().getColor();

        // visit all directions to find the cell with destination
        for (var neighbor : neighbors){

                if (neighbor != null && neighbor.containsDestination(packetColor)) {
                    agentAction.putPacket( neighbor.getX() , neighbor.getY() );
                    return;
                }


        }
    }
}
