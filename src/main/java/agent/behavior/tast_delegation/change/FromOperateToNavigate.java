package agent.behavior.tast_delegation.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:56
 * @description：
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.tast_delegation.Utils;
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

    @Override
    public void updateChange(){
        JsonObject goal = new JsonObject();
        if(Utils.hasGoal(getAgentState())) hasGoal = true;
        else{
            if(getAgentState().hasCarry() && !Utils.requestedQueueEmpty(getAgentState())){
                //Find empty place as goal
                goal = Utils.getSafeDropPlaceAsGoal(getAgentState());
            }
            else if(getAgentState().hasCarry() && Utils.requestedQueueEmpty(getAgentState())){
                goal = Utils.searchNearestDestination(getAgentState(), getAgentState().getCarry().get().getColor());
            }
            else if (!getAgentState().hasCarry() && !Utils.requestedQueueEmpty(getAgentState())){
                goal = Utils.topRequestedQueue(getAgentState());
            }
            else{
                goal = Utils.searchGoal(this.getAgentState());
            }

            if(goal != null){
                hasGoal = true;
                Utils.setGoal(getAgentState(), goal);
            }
            else hasGoal = false;
        }

    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal && !Utils.requestedQueueEmpty(getAgentState())
                && Utils.topRequestedQueue(getAgentState()).equals(Utils.getGoalJsonObj(getAgentState())))
            Utils.popRequestedQueue(getAgentState());
        if(hasGoal && !Utils.isInReach(getAgentState(), Utils.getCoordinateFromGoal(getAgentState()))){
            getAgentState().getMapMemory().getDstarLite().startOver(new Coordinate(getAgentState().getX(), getAgentState().getY()), Utils.getCoordinateFromGoal(getAgentState()));
            return true;
        }
        return false;
    }
}
