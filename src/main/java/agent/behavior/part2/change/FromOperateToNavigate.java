package agent.behavior.part2.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:56
 * @description：
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part2.Cor;
import agent.behavior.part2.Utils;
import com.google.gson.JsonObject;
import environment.Coordinate;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 18:56
 * @description：
 * @modified By：
 * @version: $
 */

public class FromOperateToNavigate extends BehaviorChange {
    private boolean hasGoal = false;
    private boolean inReach = false;
    @Override
    public void updateChange(){
        JsonObject goal;
        if(getAgentState().hasCarry()){
            goal = Utils.searchNearestDestination(getAgentState(), getAgentState().getCarry().get().getColor());
        }
        else{
            goal = Utils.searchGoal(getAgentState());
        }
        if(goal != null){
            hasGoal = true;
            Utils.setGoal(getAgentState(), goal);
            inReach = Utils.isInReach(getAgentState(), Utils.getCoordinateFromGoal(getAgentState()));
        }
        else hasGoal = false;
    }


    @Override
    public boolean isSatisfied(){
        return hasGoal && !inReach;
    }
}
