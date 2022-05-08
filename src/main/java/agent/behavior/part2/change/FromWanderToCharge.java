package agent.behavior.part2.change;

import agent.behavior.BehaviorChange;
import agent.behavior.part2.Utils;

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
