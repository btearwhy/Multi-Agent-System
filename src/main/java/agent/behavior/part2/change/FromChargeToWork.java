package agent.behavior.part2.change;

import agent.behavior.BehaviorChange;
import environment.EnergyValues;

public class FromChargeToWork extends BehaviorChange{

	private int batteryState;
	private int batteryStopCapacity;
	
	@Override
	public void updateChange() {
		// TODO Auto-generated method stub
		this.batteryState = this.getAgentState().getBatteryState();
		this.batteryStopCapacity = EnergyValues.BATTERY_SAFE_MAX;
	}

	@Override
	public boolean isSatisfied() {
		// TODO Auto-generated method stub
		return this.batteryState>this.batteryStopCapacity;
		
	}

}
