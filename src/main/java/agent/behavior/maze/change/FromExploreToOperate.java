package agent.behavior.maze.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:43
 * @description：An agent finds a goal
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.maze.Utils;
import com.google.gson.JsonObject;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 02:43
 * @description：An agent finds a goal
 * @modified By：
 * @version: $
 */

public class FromExploreToOperate extends BehaviorChange {
    private boolean hasGoal = false;
    private boolean packetOrGenerator = false;
    private boolean inReach = false;

    @Override
    public void updateChange(){
        if(Utils.hasGoal(getAgentState())){
            this.inReach = Utils.isInReach(this.getAgentState(), Utils.getCoordinateFromGoal(getAgentState()));
            hasGoal = true;
        }
        else{
            JsonObject goal = Utils.searchGoal(this.getAgentState());
            if(goal != null){
                Utils.setGoal(getAgentState(), goal);
                this.hasGoal = true;
                this.inReach = Utils.isInReach(this.getAgentState(), Utils.getCoordinateFromGoal(getAgentState()));
            }
            else hasGoal = false;
        }
    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal && inReach){
            return true;
        }
        return false;
    }
}
