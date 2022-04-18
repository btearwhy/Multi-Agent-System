package agent.behavior.charge.change;

import agent.behavior.BehaviorChange;

public class FromChargeToExecution extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if (getAgentState().getBatteryState()>750) {
            return true;
        }
        return false;
    }
}
