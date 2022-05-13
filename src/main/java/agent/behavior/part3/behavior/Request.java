package agent.behavior.part3.behavior;

import java.util.Random;

import com.google.gson.JsonObject;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.part3.Utils;
import environment.world.agent.AgentRep;

public class Request extends Behavior{

	AgentRep requestTargetAgent = null;
	public static boolean removeSignal = false;
	
	@Override
	public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
		requestTargetAgent = Utils.requestAgentTarget(agentState);
		JsonObject request = Utils.getTargetRequest(agentState,requestTargetAgent.getColor());
		
		String message = request.get("coordinate").getAsString();
		if (requestTargetAgent != null) {
			agentCommunication.sendMessage(requestTargetAgent, message);
			removeSignal = true;
		}
		
		//remover request
		agentState.removeMemoryFragment("request");
	}

	@Override
	public void act(AgentState agentState, AgentAction agentAction) {
        agentState.updateMapMemory();

        Utils.addRequestMemory(agentState);

        int dir;
        if(agentState.getPerceptionLastCell() == null){
            Random ra = new Random();
            dir = ra.nextInt(8);
            dir = Utils.getClockwiseDirectionIfBlocked(agentState, dir);
        }
        else{
            int preDir = Utils.getPreviousDir(agentState);
            Random rb = new Random();
            int ran = rb.nextInt(100);
            int t = 7;
            if(ran < t) dir = (preDir + 3) % 8;
            else if(ran < 2 * t) dir = (preDir + 5) % 8;
            else if(ran < 4 * t) dir = (preDir + 1) % 8;
            else if(ran < 6 * t) dir = (preDir + 2) % 8;
            else if(ran < 8 * t) dir = (preDir + 6) % 8;
            else if(ran < 10 * t) dir = (preDir + 7) % 8;
            else dir = preDir;
            dir = Utils.getClockwiseDirectionIfBlocked(agentState, dir);
        }

        
        agentAction.step(agentState.getX() + Utils.moves.get(dir).getX(), agentState.getY() + Utils.moves.get(dir).getY());
	}

}
