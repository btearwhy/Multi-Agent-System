package agent.behavior.part2.change;

import agent.AgentState;
import agent.behavior.BehaviorChange;
import agent.behavior.part2.Utils;
import agent.behavior.part2.behavior.Navigate;
import environment.CellPerception;
import environment.Coordinate;

public class FromNavigateToCharge extends BehaviorChange {
    @Override
    public void updateChange() {
    }

    @Override
    public boolean isSatisfied() {
        if (getAgentState().getBatteryState() < Utils.chargeThreshold(getAgentState())) {
            getAgentState().clearGoal();
            return true;
        }
        return false;
    }
}
