package agent.behavior.part2.change;

import agent.behavior.BehaviorChange;
import agent.behavior.part2.Utils;

public class FromOperateToCharge extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
//        if (getAgentState().getBatteryState() < Utils.chargeThreshold(getAgentState())) {
//            return true;
//        }
        return false;
    }
}
