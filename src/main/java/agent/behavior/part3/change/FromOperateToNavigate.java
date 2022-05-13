package agent.behavior.part3.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:56
 * @description：
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part3.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import environment.Coordinate;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 18:56
 * @description：
 * @modified By：
 * @version: $
 */

public class FromOperateToNavigate extends BehaviorChange {
    private boolean hasGoal = false;

    @Override
    public void updateChange(){
        JsonObject goal = null;
        if(getAgentState().hasCarry()){
            goal = Utils.searchNearestDestination(getAgentState(), getAgentState().getCarry().get().getColor());
        }
        else{
            if (getAgentState().getMemoryFragmentKeys().contains("be_requested")){
                JsonArray be_requested = new Gson().fromJson(getAgentState().getMemoryFragment("be_requested"),
                        JsonArray.class);
                if (be_requested.size() > 0){
                    // add into memory goal
                    goal.addProperty("target", "packet");
                    goal.addProperty("color", getAgentState().getColor().get().getRGB());
                    goal.add("coordinate", be_requested.get(0).getAsJsonObject());
                    be_requested.remove(0);
                }
            }
            else{
                goal = Utils.searchGoal(this.getAgentState());
            }
        }

        if(goal != null){
            hasGoal = true;
            Utils.setGoal(getAgentState(), goal);
        }
        else hasGoal = false;
    }


    @Override
    public boolean isSatisfied(){
        if(hasGoal){
            Coordinate goal = Utils.getCoordinateFromGoal(getAgentState());
            getAgentState().getMapMemory().getDstarLite().startOver(new Coordinate(getAgentState().getX(), getAgentState().getY()), goal);
        }
        return hasGoal;
    }
}
