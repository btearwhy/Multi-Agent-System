package agent.behavior.tast_delegation.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:43
 * @description：An agent finds a goal
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.tast_delegation.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
        JsonObject goal = new JsonObject();

        if (getAgentState().getMemoryFragmentKeys().contains("be_requested")){
            JsonArray be_requested = new Gson().fromJson(getAgentState().getMemoryFragment("be_requested"),
                    JsonArray.class);

            // add into goal
            goal.addProperty("target", "packet");
            goal.addProperty("color", getAgentState().getColor().get().getRGB());
            goal.add("coordinate", be_requested.get(0).getAsJsonObject());
            be_requested.remove(0);

            // no more be_requested goals, remove memory
            if (be_requested.size() == 0) getAgentState().removeMemoryFragment("be_requested");
        }
        else{
            goal = Utils.searchGoal(this.getAgentState());
        }

        if(goal != null){
            Utils.setGoal(getAgentState(), goal);
            this.hasGoal = true;
            this.packetOrGenerator = Utils.getTargetFromGoal(getAgentState()).startsWith("packet") || Utils.getTargetFromGoal(getAgentState()).startsWith("generator");
            this.inReach = Utils.isInReach(this.getAgentState(), Utils.getCoordinateFromGoal(getAgentState()));
        }
        else hasGoal = false;
    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal && packetOrGenerator && inReach){
            return true;
        }
        return false;
    }
}
