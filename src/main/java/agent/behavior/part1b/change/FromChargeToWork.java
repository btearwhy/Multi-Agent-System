package agent.behavior.part1b.change;

import agent.behavior.BehaviorChange;
import environment.EnergyValues;

public class FromChargeToWork extends BehaviorChange{

	private int batteryState;
	private int batteryCapacity;
	
	@Override
	public void updateChange() {
		// TODO Auto-generated method stub
		this.batteryState = this.getAgentState().getBatteryState();
		this.batteryCapacity = EnergyValues.BATTERY_MAX;
	}

	@Override
	public boolean isSatisfied() {
		// TODO Auto-generated method stub
		return this.batteryState==this.batteryCapacity;
	}

}
