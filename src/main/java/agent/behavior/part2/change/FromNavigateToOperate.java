package agent.behavior.part2.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:51
 * @description：Arrive
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part1b.Utils;
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
        Coordinate goal = Utils.getCoordinateFromGoal(getAgentState());
        inReach = Utils.isInReach(getAgentState(), goal);
    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal && inReach){
            Utils.updatePreviousDistance(getAgentState(), "0");
            return true;
        }
        return false;
    }

}
