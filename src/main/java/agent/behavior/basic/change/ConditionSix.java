package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;
import environment.CellPerception;
import environment.Perception;

public class ConditionSix extends BehaviorChange {

    @Override
    public void updateChange() {
        // do not update

    }

    @Override
    public boolean isSatisfied() {
        // always true
        return true;
    }
}
