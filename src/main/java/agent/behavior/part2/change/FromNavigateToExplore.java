package agent.behavior.part2.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:45
 * @description：An agent successfully deliver a packet but cannot find a goal at that moment
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part2.Utils;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 02:45
 * @description：An agent successfully deliver a packet but cannot find a goal at that moment
 * @modified By：
 * @version: $
 */

public class FromNavigateToExplore extends BehaviorChange {
    private boolean hasGoal = false;
    private boolean force = false;
    @Override
    public void updateChange(){

        if(getAgentState().hasCarry() && !Utils.hasGoal(getAgentState())){
            hasGoal = true;
            Utils.setGoal(getAgentState(), Utils.searchNearestDestination(getAgentState(), getAgentState().getCarry().get().getColor()));
        }
        hasGoal = Utils.hasGoal(getAgentState());
    }


    @Override
    public boolean isSatisfied(){
       return !hasGoal;
    }
}
