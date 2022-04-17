package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;

// change from DoNothing to MoveToPacket
public class ConditionOne extends BehaviorChange {
    private boolean hasPacket = false;
    private boolean hasTarget = false;

    @Override
    public void updateChange() {

        this.hasPacket = this.getAgentState().hasCarry();
        this.hasTarget = this.getAgentState().seesPacket();

    }

    @Override
    public boolean isSatisfied() {
        // Decide when the Behaviour change is triggered
        // if the agent has a packet, it will change to BehaviourTwo

        if (!this.hasPacket && this.hasTarget) {
            return true;
        }

        return false;
    }
}
