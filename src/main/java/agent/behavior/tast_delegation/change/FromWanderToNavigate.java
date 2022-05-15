package agent.behavior.tast_delegation.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:41
 * @description：An agent finds a goal far away and navigates towards it
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.tast_delegation.Utils;
import com.google.gson.JsonObject;
import environment.Coordinate;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 18:41
 * @description：An agent finds a goal far away and navigates towards it
 * @modified By：
 * @version: $
 */

public class FromWanderToNavigate extends BehaviorChange {
    private boolean hasGoal = false;
    private boolean packetOrGenerator = false;
    private boolean inReach = false;

    @Override
    public void updateChange(){
        JsonObject goal = new JsonObject();

        if (!Utils.requestedQueueEmpty(getAgentState())){
            goal = Utils.popRequestedQueue(getAgentState());
        }
        else{
            goal = Utils.searchGoal(this.getAgentState());
        }

        if(goal != null){
            Utils.setGoal(getAgentState(), goal);
            this.hasGoal = true;
            this.packetOrGenerator = Utils.getTargetFromGoal(getAgentState()).equals("packet") || Utils.getTargetFromGoal(getAgentState()).equals("generator");
            this.inReach = Utils.isInReach(this.getAgentState(), Utils.getCoordinateFromGoal(getAgentState()));
        }
        else{
            this.hasGoal = false;
        }
    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal && packetOrGenerator && !inReach){
            Coordinate goal = Utils.getCoordinateFromGoal(getAgentState());
            getAgentState().getMapMemory().getDstarLite().startOver(new Coordinate(getAgentState().getX(), getAgentState().getY()), goal);

            return true;
        }
        return false;
    }
}