package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;
import java.awt.Color;

// change from DoNothing to MoveToDestination
public class ConditionThree extends BehaviorChange {
    private boolean hasPacket = false;
    private boolean hasTarget = false;

    @Override
    public void updateChange() {

        this.hasPacket = this.getAgentState().hasCarry();
        if (hasPacket) {
            this.hasTarget = this.getAgentState().seesDestination(this.getAgentState().getCarry().get().getColor());
        }

    }

    @Override
    public boolean isSatisfied() {
        // Decide when the Behaviour change is triggered
        // if the agent has a packet, it will change to BehaviourTwo
        if (this.hasPacket && this.hasTarget) {
            return true;
        }

        return false;
    }
}
