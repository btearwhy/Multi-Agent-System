package agent.behavior.basic.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:56
 * @description：
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.basic.Utils;
import environment.Coordinate;
import environment.Perception;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 18:56
 * @description：
 * @modified By：
 * @version: $
 */

public class FromOperateToNavigate extends BehaviorChange {
    private boolean hasGoal;

    @Override
    public void updateChange(){
        String goal = null;
        if(getAgentState().hasCarry()){
            goal = Utils.searchNearestDestination(getAgentState(), getAgentState().getCarry().get().getColor());
        }
        else{
            goal = Utils.searchGoal(getAgentState());
        }
        if(goal != null){
            hasGoal = true;
            Utils.setGoal(getAgentState(), goal);
        }
    }


    @Override
    public boolean isSatisfied(){
        if (hasGoal){
            Coordinate cor = Utils.getCoordinateFromGoal(getAgentState().getMemoryFragment("goal"));
            Utils.updatePreviousDistanceFragment(getAgentState(), String.valueOf(Perception.manhattanDistance(getAgentState().getX(), getAgentState().getY(), cor.getX(), cor.getY())));
        }
        return hasGoal;
    }
}
