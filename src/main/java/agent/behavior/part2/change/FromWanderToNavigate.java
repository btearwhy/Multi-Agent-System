package agent.behavior.part2.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:41
 * @description：An agent finds a goal far away and navigates towards it
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part1b.Utils;
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

    @Override
    public void updateChange(){
        JsonObject goal = Utils.searchGoal(this.getAgentState());
        if(goal != null){
            Utils.setGoal(getAgentState(), goal);
            this.hasGoal = true;
            this.packetOrGenerator = Utils.getTargetFromGoal(getAgentState()).equals("packet") || Utils.getTargetFromGoal(getAgentState()).equals("generator");
            this.inReach = Utils.isInReach(this.getAgentState(), Utils.getCoordinateFromGoal(getAgentState()));
        }
        else hasGoal = false;
    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal && packetOrGenerator && !inReach){
            Coordinate cor = Utils.getCoordinateFromGoal(getAgentState());
            Utils.updatePreviousDistance(getAgentState(), String.valueOf(Perception.manhattanDistance(getAgentState().getX(), getAgentState().getY(), cor.getX(), cor.getY())));
            return true;
        }
        return false;
    }
}