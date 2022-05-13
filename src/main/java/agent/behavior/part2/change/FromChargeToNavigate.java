package agent.behavior.part2.change;

import agent.behavior.BehaviorChange;
import agent.behavior.part2.Cor;
import agent.behavior.part2.Utils;
import environment.Coordinate;
import environment.EnergyValues;

public class FromChargeToNavigate extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        if (getAgentState().hasCarry() && getAgentState().getBatteryState()>EnergyValues.BATTERY_SAFE_MAX) {
            Cor goal = Utils.getCoordinateFromGoal(getAgentState());
            getAgentState().getMapMemory().getDstarLite().startOver(new Cor(getAgentState().getX(), getAgentState().getY()), goal);
            return true;
        }
        return false;
    }
}