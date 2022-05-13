package agent.behavior.part3.change;

import com.google.gson.JsonObject;

import agent.behavior.BehaviorChange;
import agent.behavior.part3.Utils;

public class FromOthersToRequest extends BehaviorChange {

	@Override
	public void updateChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSatisfied() {
		JsonObject requestObj = Utils.getRequest(this.getAgentState());
		boolean hasRequest = false;
		if (requestObj!=null) {
			hasRequest = true;
		}
		return hasRequest;
	}

}
