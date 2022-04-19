package agent.behavior.part1b.change;

import agent.behavior.BehaviorChange;

public class GoToCharge extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if (!getAgentState().hasCarry() && getAgentState().getBatteryState()<300) {
            return true;
        }
        else if (getAgentState().hasCarry() && getAgentState().getBatteryState()<400) {
            return true;
        }
        return false;
    }
}
