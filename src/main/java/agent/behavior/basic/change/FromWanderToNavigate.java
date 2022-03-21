package agent.behavior.basic.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:41
 * @description：An agent finds a goal far away and navigates towards it
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.basic.Utils;
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
        String goal = Utils.searchGoal(this.getAgentState());
        if(goal != null){
            Utils.setGoal(getAgentState(), goal);
            this.hasGoal = true;
            this.packetOrGenerator = Utils.getTargetFromGoal(goal).startsWith("packet") || Utils.getTargetFromGoal(goal).startsWith("generator");
            this.inReach = Utils.isInReach(this.getAgentState(), Utils.getCoordinateFromGoal(goal));
        }
        else hasGoal = false;
    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal && packetOrGenerator && !inReach){
            Coordinate cor = Utils.getCoordinateFromGoal(getAgentState().getMemoryFragment("goal"));
            Utils.updatePreviousDistanceFragment(getAgentState(), String.valueOf(Perception.manhattanDistance(getAgentState().getX(), getAgentState().getY(), cor.getX(), cor.getY())));
            return true;
        }
        return false;
    }
}
