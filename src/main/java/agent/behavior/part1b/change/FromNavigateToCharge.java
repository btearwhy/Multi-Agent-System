package agent.behavior.part1b.change;

import agent.behavior.BehaviorChange;

public class FromNavigateToCharge extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if (getAgentState().getBatteryState() < 400) {
            return true;
        }
        return false;
    }
}
