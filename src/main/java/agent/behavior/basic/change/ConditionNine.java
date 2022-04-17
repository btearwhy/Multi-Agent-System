package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;

public class ConditionNine extends BehaviorChange {
    private boolean hasPacketTarget = false;
    @Override
    public void updateChange() {
        this.hasPacketTarget = this.getAgentState().seesPacket();

    }

    @Override
    public boolean isSatisfied() {
        // always true
        return !this.hasPacketTarget;
    }
}
