package agent.behavior.basic.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:46
 * @description：An agent successfully deliver a goal and finds there a packet to fetch
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.basic.Utils;
import environment.Coordinate;
import environment.Perception;

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
        String goal = Utils.searchGoal(getAgentState());
        if(goal != null) hasGoal = true;
    }


    @Override
    public boolean isSatisfied(){
        if(!hasGoal && !hasPacket){
            Coordinate cor = Utils.getCoordinateFromGoal(getAgentState().getMemoryFragment("goal"));
            Utils.updatePreviousDistanceFragment(getAgentState(), "0");
        }
        return !hasGoal && !hasPacket;
    }
}
