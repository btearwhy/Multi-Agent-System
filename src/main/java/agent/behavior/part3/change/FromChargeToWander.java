package agent.behavior.part3.change;

import agent.behavior.BehaviorChange;
import environment.EnergyValues;

public class FromChargeToWander extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if (!getAgentState().hasCarry() && getAgentState().getBatteryState()>EnergyValues.BATTERY_SAFE_MAX) {
            return true;
        }
        return false;
    }
}
