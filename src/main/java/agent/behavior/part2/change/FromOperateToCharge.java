package agent.behavior.part2.change;

import agent.behavior.BehaviorChange;

public class FromOperateToCharge extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if (getAgentState().getBatteryState()<400) {
            return true;
        }
        return false;
    }
}
