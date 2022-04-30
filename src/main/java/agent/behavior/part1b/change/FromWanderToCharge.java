package agent.behavior.part1b.change;

import agent.behavior.BehaviorChange;

public class FromWanderToCharge extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if (!getAgentState().hasCarry() && getAgentState().getBatteryState()<200) {
            return true;
        }
        return false;
    }
}
