package agent.behavior.part1b.change;

import agent.behavior.BehaviorChange;

public class FromExecutionToCharge extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if (getAgentState().getBatteryState()<450) {
            return true;
        }
        return false;
    }
}
