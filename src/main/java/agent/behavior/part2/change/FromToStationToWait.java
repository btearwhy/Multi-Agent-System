package agent.behavior.part2.change;
import agent.behavior.BehaviorChange;
import agent.behavior.part2.Utils;
import environment.Coordinate;

public class FromToStationToWait extends BehaviorChange {
    private int wait_lower_bound;
    private int wait_upper_bound;

    private int gradient_value;

    public FromToStationToWait(int wait_lower_bound, int wait_upper_bound) {
        this.wait_lower_bound = wait_lower_bound;
        this.wait_upper_bound = wait_upper_bound;
    }

    @Override
    public void updateChange(){
        var perception = getAgentState().getPerception();
        gradient_value = perception.getCellPerceptionOnRelPos(0,0).getGradientRepresentation().get().getValue();
    }


    @Override
    public boolean isSatisfied(){
        if (gradient_value >= wait_lower_bound && gradient_value <= wait_upper_bound) return true;

        return false;
    }

}
