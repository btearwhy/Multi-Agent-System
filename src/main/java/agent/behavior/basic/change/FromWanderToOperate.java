package agent.behavior.basic.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:43
 * @description：An agent finds a goal
 * @modified By：
 * @version: $
 */

import agent.AgentState;
import agent.behavior.BehaviorChange;
import agent.behavior.basic.Utils;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.packet.PacketRep;

import java.awt.*;
import java.util.List;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 02:43
 * @description：An agent finds a goal
 * @modified By：
 * @version: $
 */

public class FromWanderToOperate extends BehaviorChange {
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
    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal && packetOrGenerator && inReach){
            Utils.updatePreviousDistanceFragment(getAgentState(), "");
            return true;
        }
        return false;
    }
}
