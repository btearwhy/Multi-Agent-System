package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;
import environment.CellPerception;
import environment.Perception;

import java.awt.Color;

public class ConditionFive extends BehaviorChange {
    //private Color packetColor = null;
    private CellPerception[] neighbors = null;

    @Override
    public void updateChange() {
        // check all neighbors of the agent
        this.neighbors = this.getAgentState().getPerception().getNeighbours();

    }

    @Override
    public boolean isSatisfied() {
        // Decide when the Behaviour change is triggered
        // if the agent has a packet, it will change to BehaviourTwo
        for (var neighbor : this.neighbors ) {
            if (neighbor != null && neighbor.containsDestination(this.getAgentState().getCarry().get().getColor())) {

                return true;

            }

        }

        return false;
    }
}
