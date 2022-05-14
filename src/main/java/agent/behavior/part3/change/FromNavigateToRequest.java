package agent.behavior.part3.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/5/13 23:30
 * @description：
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;
import agent.behavior.part3.Utils;
import com.google.gson.JsonObject;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/5/13 23:30
 * @description：
 * @modified By：
 * @version: $
 */

public class FromNavigateToRequest extends BehaviorChange {
    @Override
    public void updateChange() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isSatisfied() {
        return getAgentState().getMemoryFragment("request") != null;
    }
}
