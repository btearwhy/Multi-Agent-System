package agent.behavior.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
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
        agentState.getDStarLite().updateGoal(20, 21);
        HashMap<Coordinate, Boolean> observed_map = new HashMap<>();
        for (CellPerception cell:perception.getAllCells()) {
            if (cell.isWalkable() ||
                    ((cell.getX() == agentState.getX()) && (cell.getY() == agentState.getY()))) {
                observed_map.put(new Coordinate(cell.getX(), cell.getY()), false); // no obstacle
            }
            else {
                observed_map.put(new Coordinate(cell.getX(), cell.getY()), true); // obstacle
            }
        }

        agentState.getDStarLite().run(observed_map);
        Coordinate next = agentState.getDStarLite().getNextMove(agentState.getX(), agentState.getY());

        if (perception.getCellPerceptionOnAbsPos(next.getX(), next.getY()) != null &&
                perception.getCellPerceptionOnAbsPos(next.getX(), next.getY()).isWalkable() &&
                (next.getX() >= 0 && next.getY() >= 0)) {
            agentAction.step(next.getX(), next.getY());
        }

        // No viable moves, skip turn
        agentAction.skip();
    }
}
