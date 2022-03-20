package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;

public class ConditionTen extends BehaviorChange {
    private boolean hasDestinationTarget = false;
    @Override
    public void updateChange() {
        this.hasDestinationTarget = this.getAgentState().seesDestination(this.getAgentState().getCarry().get().getColor());

    }

    @Override
    public boolean isSatisfied() {
        // always true
        return !this.hasDestinationTarget;
    }
}
