package agent.behavior.memory.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:46
 * @description：An agent successfully deliver a goal and finds there a packet to fetch
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.memory.Utils;
import com.google.gson.JsonObject;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 02:46
 * @description：An agent successfully deliver a goal and finds there a packet to fetch
 * @modified By：
 * @version: $
 */

public class FromOperateToWander extends BehaviorChange {
    private boolean hasGoal = false;


    @Override
    public void updateChange(){
        JsonObject goal = new JsonObject();
        if(getAgentState().hasCarry() ){
            //Find empty place as goal
            goal = Utils.searchNearestDestination(getAgentState(), getAgentState().getCarry().get().getColor());
        }
        else{
            goal = Utils.searchGoal(this.getAgentState());
        }


        if(goal != null){
            hasGoal = true;
        }
        else hasGoal = false;
    }


    @Override
    public boolean isSatisfied(){
        if(!hasGoal && !getAgentState().hasCarry()) {
            getAgentState().removeMemoryFragment("goal");
            return true;
        }
        return false;
    }
}
