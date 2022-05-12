package agent.behavior.part3.change;

import agent.behavior.BehaviorChange;
import environment.Coordinate;

/**
 * @author ：mmzs
 * @date ：Created in 2022/5/5 21:27
 * @description：
 * @modified By：
 * @version: $
 */



public class FromNavigateToAvoid extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        String cor = getAgentState().getMemoryFragment("avoid");
        if (cor != null){
            Coordinate goal = new Coordinate(Integer.parseInt(cor.split(",")[0]), Integer.parseInt(cor.split(",")[1]));
            getAgentState().getMapMemory().getDstarLite().startOver(new Coordinate(getAgentState().getX(), getAgentState().getY()), goal);
            return true;
        }
        else return false;
    }
}
