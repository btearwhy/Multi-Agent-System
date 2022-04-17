package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;
import environment.CellPerception;
import environment.Perception;

// change from MoveToPacket to Picking
public class ConditionFour extends BehaviorChange {
    private CellPerception[] neighbors = null;

    @Override
    public void updateChange() {

        this.neighbors = this.getAgentState().getPerception().getNeighbours();



    }

    @Override
    public boolean isSatisfied() {
        // Decide when the Behaviour change is triggered
        // if the agent has a packet in neighbor cells, it will change to Picking

        for (var neighbor : this.neighbors) {
            if (neighbor != null && neighbor.containsPacket()) {

                return true;

            }

        }

        return false;

    }
}
