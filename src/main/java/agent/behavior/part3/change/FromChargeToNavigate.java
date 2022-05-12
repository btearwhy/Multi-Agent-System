package agent.behavior.part3.change;

import agent.behavior.BehaviorChange;
import agent.behavior.part3.Utils;
import environment.Coordinate;
import environment.EnergyValues;

public class FromChargeToNavigate extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if (getAgentState().hasCarry() && getAgentState().getBatteryState()>EnergyValues.BATTERY_SAFE_MAX) {
            Coordinate goal = Utils.getCoordinateFromGoal(getAgentState());
            getAgentState().getMapMemory().getDstarLite().startOver(new Coordinate(getAgentState().getX(), getAgentState().getY()), goal);
            return true;
        }
        return false;
    }
}
