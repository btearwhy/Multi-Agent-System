package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;

public class ConditionTest extends BehaviorChange {
    private boolean hasPacket = false;

    @Override
    public void updateChange() {

        this.hasPacket = this.getAgentState().hasCarry();
    }

    @Override
    public boolean isSatisfied() {
        // Decide when the Behaviour change is triggered
        // if the agent has a packet, it will change to BehaviourTwo
        return this.hasPacket;
    }

}
