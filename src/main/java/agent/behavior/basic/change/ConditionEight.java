package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;

public class ConditionEight extends BehaviorChange {

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
