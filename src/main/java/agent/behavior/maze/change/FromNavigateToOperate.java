package agent.behavior.maze.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:51
 * @description：Arrive
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.maze.Cor;
import agent.behavior.maze.Utils;

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
        Cor goal;
        hasGoal = Utils.hasGoal(getAgentState());
        if(hasGoal){
            goal = Utils.getCoordinateFromGoal(getAgentState());
            inReach = Utils.isInReach(getAgentState(), goal);
        }

    }


    @Override
    public boolean isSatisfied(){
        return hasGoal && inReach;
    }

}
