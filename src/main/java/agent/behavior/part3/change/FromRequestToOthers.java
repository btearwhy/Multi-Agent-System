package agent.behavior.part3.change;

import agent.behavior.BehaviorChange;
import agent.behavior.part3.behavior.Request;

public class FromRequestToOthers extends BehaviorChange {

	@Override
	public void updateChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSatisfied() {
		return Request.removeSignal;
	}

}
