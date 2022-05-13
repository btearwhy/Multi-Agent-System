package agent.behavior.part2.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:41
 * @description：An agent finds a goal far away and navigates towards it
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part2.Cor;
import agent.behavior.part2.Utils;
import com.google.gson.JsonObject;
import environment.Coordinate;
import environment.Perception;

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

    private boolean force = false;

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
//        if(getAgentState().getMemoryFragment("switch") != null){
//            getAgentState().removeMemoryFragment("switch");
//            force = true;
//            return;
//        }

    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal && !inReach){
            Cor goal = Utils.getCoordinateFromGoal(getAgentState());
            getAgentState().getMapMemory().getDstarLite().startOver(new Cor(getAgentState().getX(), getAgentState().getY()), goal);
            return true;
        }
        return false;
    }
}