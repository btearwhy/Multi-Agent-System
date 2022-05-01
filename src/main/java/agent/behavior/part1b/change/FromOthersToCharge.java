package agent.behavior.part1b.change;

import agent.behavior.BehaviorChange;
import environment.EnergyValues;
import environment.world.agent.Agent;

public class FromOthersToCharge extends BehaviorChange{
	
	private int agentNum;
	private int batteryCapacity;
	private int unitConsumption;
	private int stepConsumption;
	private int chargingEfficiency;
	private boolean hasPacket;
	private int batteryState;
	private int field;
	private int fixEnergy;
	private int navigateEnergy;
	private Agent agent ;
	
	private int threshold;

	
	@Override
	public void updateChange() {
		
		this.agentNum = agent.getEnvironment().getNbAgents();
		this.batteryCapacity = EnergyValues.BATTERY_MAX;
		this.unitConsumption =  EnergyValues.BATTERY_DECAY_SKIP;
		this.stepConsumption = EnergyValues.BATTERY_DECAY_STEP;
		this.chargingEfficiency = 100;//????
		this.hasPacket = this.getAgentState().hasCarry();
		this.batteryState = this.getAgentState().getBatteryState();
		this.field = this.getAgentState().getPerception().getCellPerceptionOnRelPos(0,0).getGradientRepresentation().get().getValue();
		
		this.fixEnergy = (int)Math.ceil(this.batteryCapacity*((this.unitConsumption/this.chargingEfficiency+1)^(this.agentNum-1)-1)/
				(this.unitConsumption/this.chargingEfficiency+1)^(this.agentNum-1));
		this.navigateEnergy = this.unitConsumption*(-("false".indexOf("" + this.hasPacket)))+this.stepConsumption*this.field;
		this.threshold = this.fixEnergy + this.navigateEnergy;
	}

	@Override
	public boolean isSatisfied() {
		// TODO Auto-generated method stub
		return this.batteryState>this.threshold;
	}

	
}
