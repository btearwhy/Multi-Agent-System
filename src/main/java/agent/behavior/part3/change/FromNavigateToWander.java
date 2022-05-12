package agent.behavior.part3.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:45
 * @description：An agent successfully deliver a packet but cannot find a goal at that moment
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part3.Utils;

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
            getAgentState().clearGoal();
            return true;
        }
        return false;
    }
}
