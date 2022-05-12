package agent.behavior.part3.change;
import agent.behavior.BehaviorChange;

public class FromWaitToCharging extends BehaviorChange {
    private int charging_bound;

    private int gradient_value;

    public FromWaitToCharging(int charging_bound) {
        this.charging_bound = charging_bound;
    }

    @Override
    public void updateChange(){
        var perception = getAgentState().getPerception();
        gradient_value = perception.getCellPerceptionOnRelPos(0,0).getGradientRepresentation().get().getValue();
    }


    @Override
    public boolean isSatisfied(){
        if (gradient_value <= charging_bound) return true;

        return false;
    }

}
