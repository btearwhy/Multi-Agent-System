package agent.behavior.part1b.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:45
 * @description：An agent successfully deliver a packet but cannot find a goal at that moment
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part1b.Utils;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 02:45
 * @description：An agent successfully deliver a packet but cannot find a goal at that moment
 * @modified By：
 * @version: $
 */

public class FromNavigateToWander extends BehaviorChange {
    private boolean hasGoal = false;

    @Override
    public void updateChange(){
        hasGoal = Utils.hasGoal(getAgentState());
    }


    @Override
    public boolean isSatisfied(){
        if(!hasGoal){
            Utils.updatePreviousDistance(getAgentState(), "0");
            return true;
        }
        return false;
    }
}
