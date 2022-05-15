package agent.behavior.energy.change;

import agent.behavior.BehaviorChange;
import agent.behavior.energy.Utils;

public class FromWanderToCharge extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if (getAgentState().getBatteryState() < Utils.chargeThreshold(getAgentState())) {
            return true;
        }
        return false;
    }
}
