package agent.behavior.part3.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:46
 * @description：An agent successfully deliver a goal and finds there a packet to fetch
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part3.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
        if(getAgentState().hasCarry() && !Utils.requestedQueueEmpty(getAgentState())){
            //Find empty place as goal
            goal = Utils.getSafeDropPlaceAsGoal(getAgentState());
        }
        else if(getAgentState().hasCarry() && Utils.requestedQueueEmpty(getAgentState())){
            goal = Utils.searchNearestDestination(getAgentState(), getAgentState().getCarry().get().getColor());
        }
        else if (!getAgentState().hasCarry() && !Utils.requestedQueueEmpty(getAgentState())){
            goal = Utils.topRequestedQueue(getAgentState());
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
            getAgentState().clearGoal();
            return true;
        }
        return false;
    }
}
