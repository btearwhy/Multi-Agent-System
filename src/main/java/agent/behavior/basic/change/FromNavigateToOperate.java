package agent.behavior.basic.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:51
 * @description：Arrive
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.basic.Utils;
import environment.Coordinate;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 18:51
 * @description：Arrive
 * @modified By：
 * @version: $
 */

public class FromNavigateToOperate extends BehaviorChange {
    private boolean hasGoal = false;
    private boolean inReach = false;

    @Override
    public void updateChange(){
        hasGoal = Utils.hasGoal(getAgentState());
        Coordinate goal = Utils.getCoordinateFromGoal(getAgentState().getMemoryFragment("goal"));
        inReach = Utils.isInReach(getAgentState(), goal);
    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal && inReach){
            Utils.updatePreviousDistanceFragment(getAgentState(), "0");
            return true;
        }
        return false;
    }

}
