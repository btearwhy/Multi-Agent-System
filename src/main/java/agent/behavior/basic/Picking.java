package agent.behavior.basic;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Picking extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        // get agent perception
        var perception = agentState.getPerception();

        // get neighbor perception
        CellPerception[] neighbors = perception.getNeighbours();

        // visit all direction to find the cell with packet
        for (var neighbor : neighbors) {

            if (neighbor != null && neighbor.containsPacket()) {
                agentAction.pickPacket( neighbor.getX() , neighbor.getY() );
                return;
            }
        }
    }
}
