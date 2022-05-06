package agent.behavior.part2.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/5/5 23:55
 * @description：
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part2.Utils;
import environment.Coordinate;
import org.checkerframework.checker.units.qual.C;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/5/5 23:55
 * @description：
 * @modified By：
 * @version: $
 */

public class FromAvoidToNavigate extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        Coordinate cur = new Coordinate(getAgentState().getX(), getAgentState().getY());
        if(Utils.hasGoal(getAgentState())){
            Coordinate goal = Utils.getCoordinateFromGoal(getAgentState());
            getAgentState().getMapMemory().getDstarLite().startOver(cur, goal);
            if(getAgentState().getMapMemory().getDstarLite().getSmallestG(cur) < 1000){
                getAgentState().removeMemoryFragment("avoid");
                return true;
            }
        }
        return false;
    }
}
