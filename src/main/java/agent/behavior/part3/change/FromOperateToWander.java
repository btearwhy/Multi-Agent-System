package agent.behavior.part3.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:46
 * @description：An agent successfully deliver a goal and finds there a packet to fetch
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part3.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 02:46
 * @description：An agent successfully deliver a goal and finds there a packet to fetch
 * @modified By：
 * @version: $
 */

public class FromOperateToWander extends BehaviorChange {
    private boolean hasGoal = false;
    private boolean hasPacket = false;

    @Override
    public void updateChange(){
        hasPacket = getAgentState().hasCarry();
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

        if(goal != null) hasGoal = true;
        else hasGoal = false;
    }


    @Override
    public boolean isSatisfied(){
        return !hasGoal && !hasPacket;
    }
}
