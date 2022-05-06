package agent.behavior.part2.change;

import agent.behavior.BehaviorChange;

public class FromChargeToNavigate extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if (getAgentState().hasCarry() && getAgentState().getBatteryState()>980) {
            return true;
        }
        return false;
    }
}
