package agent.behavior.part3.behavior;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.part3.Utils;
import environment.*;

import java.util.ArrayList;
import java.util.List;

public class ToStation extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        Utils.updateAgentNum(agentState);

        var perception = agentState.getPerception();

        var neighbours = perception.getNeighbours();

        var last_perception = agentState.getPerceptionLastCell();

        // put down the packet
        if (agentState.hasCarry()){
            if (last_perception != null){
                agentAction.putPacket(last_perception.getX(), last_perception.getY());
                return;
            }
            else{
                for (var n : neighbours){
                    if (n != null && n.isWalkable()){
                        agentAction.putPacket(n.getX(),n.getY());
                        return;
                    }
                }
            }

        }

        int min_grad = Integer.MAX_VALUE;
        // find the min gradient value
        for (var n : neighbours) {
            if (n != null && n.isWalkable()) {
                if ( n.containsGradient() &&
                        n.getGradientRepresentation().get().getValue() < min_grad){
                    min_grad = n.getGradientRepresentation().get().getValue();
                }
            }
        }

        // extract all neighbors' coordinates with min gradient value
        List<CellPerception> min_neighbours = new ArrayList<CellPerception>();
        for (var n : neighbours) {
            if (n != null && n.isWalkable()){
                if (n.containsGradient() &&
                        n.getGradientRepresentation().get().getValue() == min_grad){
                    min_neighbours.add(n);

                }
            }
        }

        // find the step with the max number of neighbors
        CellPerception target_cell = null;
        if (min_neighbours.size() == 0){
            agentAction.skip();
            return;
        }
        else{
            int max_num_neighbors = 0;
            for (CellPerception cell : min_neighbours){
                int current_num_neighbors = Utils.count_walkable_neighbors(perception, cell);
                if (current_num_neighbors >= max_num_neighbors){
                    max_num_neighbors = current_num_neighbors;
                    target_cell = cell;
                }
            }
        }
        
        agentAction.step(target_cell.getX(),target_cell.getY());
        return;
    }


}
