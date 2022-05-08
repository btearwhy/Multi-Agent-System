package agent.behavior.part2.behavior;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.part2.Utils;
import com.google.common.collect.Table;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.Perception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Charging extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        var perception = agentState.getPerception();

        var neighbours = perception.getNeighbours();

        Utils.updateAgentNum(agentState);

        // Wait: is charging
        if (perception.getCellPerceptionOnRelPos(0, 0).containsGradient()
                && perception.getCellPerceptionOnRelPos(0, 0).getGradientRepresentation().get().getValue() == 0) {
            agentAction.skip();
            return;
        }

        // Walk to charging station
        int min_grad = Integer.MAX_VALUE;
        CellPerception target_cell = null;
        // find the min gradient value
        for (var n : neighbours) {
            if (n != null && n.isWalkable()) {
                if ( n.containsGradient() &&
                        n.getGradientRepresentation().get().getValue() < min_grad){
                    min_grad = n.getGradientRepresentation().get().getValue();
                    target_cell = n;
                }
            }
        }
        agentAction.step(target_cell.getX(), target_cell.getY());
        return;

    }

}
