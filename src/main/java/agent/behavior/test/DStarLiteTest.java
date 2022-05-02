package agent.behavior.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.dstarlite.DStarLite;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import util.Pair;

public class DStarLiteTest extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        Perception perception = agentState.getPerception();
        agentState.getDStarLite().updateStart(agentState.getX(), agentState.getY());
        agentState.getDStarLite().updateGoal(0, 0);

        agentState.getDStarLite().run(DStarLite.getObservedMap(agentState));
        Coordinate next = agentState.getDStarLite().getNextMove(agentState.getX(), agentState.getY());

        if (perception.getCellPerceptionOnAbsPos(next.getX(), next.getY()) != null &&
                perception.getCellPerceptionOnAbsPos(next.getX(), next.getY()).isWalkable() &&
                (next.getX() >= 0 && next.getY() >= 0)) {
            agentAction.step(next.getX(), next.getY());
            return;
        }

        // No viable moves, skip turn
        agentAction.skip();
    }
}
