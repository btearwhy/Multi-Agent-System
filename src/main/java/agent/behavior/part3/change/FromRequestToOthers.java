package agent.behavior.part3.change;

import com.google.gson.JsonObject;

import agent.behavior.BehaviorChange;
import agent.behavior.part3.Utils;

public class FromRequestToOthers extends BehaviorChange {

	@Override
	public void updateChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSatisfied() {
		JsonObject requestObj = Utils.getRequest(this.getAgentState());
		boolean noRequest = false;
		if (requestObj==null) {
			noRequest = true;
		}
		return noRequest;
	}

}
