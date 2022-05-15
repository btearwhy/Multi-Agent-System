package agent.behavior.memory.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:56
 * @description：
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.memory.Utils;
import com.google.gson.JsonObject;
import environment.Coordinate;
import jdk.jshell.execution.Util;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 18:56
 * @description：
 * @modified By：
 * @version: $
 */

public class FromOperateToNavigate extends BehaviorChange {
    private boolean hasGoal = false;

    @Override
    public void updateChange(){
        JsonObject goal = new JsonObject();
        if(getAgentState().hasCarry() ){
            //Find empty place as goal
            goal = Utils.searchNearestDestination(getAgentState(), getAgentState().getCarry().get().getColor());
        }
        else{
            goal = Utils.searchGoal(this.getAgentState());
        }


        if(goal != null){
            Utils.setGoal(getAgentState(), goal);
            hasGoal = true;
        }
        else hasGoal = false;

    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal && !Utils.isInReach(getAgentState(), Utils.getCoordinateFromGoal(getAgentState()))){
            getAgentState().getMapMemory().getDstarLite().startOver(new Coordinate(getAgentState().getX(), getAgentState().getY()), Utils.getCoordinateFromGoal(getAgentState()));
            return true;
        }
        return false;
    }
}
