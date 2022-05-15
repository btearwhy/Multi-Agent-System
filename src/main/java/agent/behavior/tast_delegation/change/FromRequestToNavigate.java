package agent.behavior.tast_delegation.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/5/14 00:10
 * @description：
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.tast_delegation.Utils;
import environment.Coordinate;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/5/14 00:10
 * @description：
 * @modified By：
 * @version: $
 */

public class FromRequestToNavigate extends BehaviorChange {

    @Override
    public void updateChange(){

    }


    @Override
    public boolean isSatisfied(){
        if(getAgentState().getMemoryFragment("request") == null){
            if(!Utils.requestedQueueEmpty(getAgentState())){
                if(getAgentState().hasCarry()){
                    Utils.setGoal(getAgentState(), Utils.getSafeDropPlaceAsGoal(getAgentState()));
                }
                else{
                    Utils.setGoal(getAgentState(), Utils.popRequestedQueue(getAgentState()));
                }
            }
            getAgentState().getMapMemory().getDstarLite().startOver(new Coordinate(getAgentState().getX(), getAgentState().getY()), Utils.getCoordinateFromGoal(getAgentState()));
            return true;
        }
        return false;
    }
}