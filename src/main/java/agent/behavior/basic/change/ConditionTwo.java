package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;

// change from DoNothing to Wander
public class ConditionTwo extends BehaviorChange {
    private boolean hasPacket = false;
    private boolean hasPacketTarget = false;
    private boolean hasDestinationTarget = false;

    @Override
    public void updateChange() {

        this.hasPacket = this.getAgentState().hasCarry();
        this.hasPacketTarget = this.getAgentState().seesPacket();
        this.hasDestinationTarget = this.getAgentState().seesDestination();
    }

    @Override
    public boolean isSatisfied() {
        // Decide when the Behaviour change is triggered
        // if the agent has a packet, it will change to BehaviourTwo
        if ((this.hasPacket && !this.hasDestinationTarget) || (!this.hasPacket && !this.hasPacketTarget)) {
            return true;
        }
        return false;
    }
}
