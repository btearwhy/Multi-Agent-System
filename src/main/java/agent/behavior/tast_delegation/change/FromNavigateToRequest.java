package agent.behavior.tast_delegation.change;/**
 * @author ：mmzs
 * @date ：Created in 2022/5/13 23:30
 * @description：
 * @modified By：
 * @version: $
 */

import agent.behavior.BehaviorChange;

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
