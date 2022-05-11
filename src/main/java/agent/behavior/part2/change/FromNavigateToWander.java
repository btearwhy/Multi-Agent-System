package agent.behavior.part2.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:45
 * @description：An agent successfully deliver a packet but cannot find a goal at that moment
 * @modified By：
 * @version: $
 */

import agent.AgentState;
import agent.behavior.BehaviorChange;
import agent.behavior.part2.Utils;
import agent.behavior.part2.behavior.Navigate;
import environment.CellPerception;
import environment.Coordinate;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 02:45
 * @description：An agent successfully deliver a packet but cannot find a goal at that moment
 * @modified By：
 * @version: $
 */

public class FromNavigateToWander extends BehaviorChange {
    private boolean hasGoal = false;
    private boolean force = false;
    @Override
    public void updateChange(){
//        if(getAgentState().getMemoryFragment("switch") != null){
//            getAgentState().removeMemoryFragment("switch");
//            force = true;
//        }

        hasGoal = Utils.hasGoal(getAgentState());
    }


    @Override
    public boolean isSatisfied(){
       return !hasGoal;
    }
}
