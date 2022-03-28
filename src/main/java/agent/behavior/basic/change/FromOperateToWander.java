package agent.behavior.basic.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:46
 * @description：An agent successfully deliver a goal and finds there a packet to fetch
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.basic.Utils;
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
    private boolean hasPacket = false;

    @Override
    public void updateChange(){
        hasPacket = getAgentState().hasCarry();
        JsonObject goal = Utils.searchGoal(getAgentState());
        if(goal != null) hasGoal = true;
        else hasGoal = false;
    }


    @Override
    public boolean isSatisfied(){
        if(!hasGoal && !hasPacket){
            Utils.updatePreviousDistance(getAgentState(), "0");
        }
        return !hasGoal && !hasPacket;
    }
}
