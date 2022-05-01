package agent.behavior.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.Coordinate;

public class DStarLiteTest extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentState.getDStarLite().updateStart(agentState.getX(), agentState.getY());
        agentState.getDStarLite().updateGoal(25, 25);
        HashMap<Coordinate, Boolean> observed_map = new HashMap<>();

        agentState.getDStarLite().run(observed_map);
        Coordinate next = agentState.getDStarLite().getNextMove(agentState.getX(), agentState.getY());

        agentAction.step(next.getX(), next.getY());

        // No viable moves, skip turn
        agentAction.skip();
    }
}
