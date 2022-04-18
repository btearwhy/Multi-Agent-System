package agent.behavior.charge;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Charge extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        var perception = agentState.getPerception();

        if (perception.getCellPerceptionOnRelPos(0, 0).getGradientRepresentation().isPresent()
                && perception.getCellPerceptionOnRelPos(0, 0).getGradientRepresentation().get().getValue() == 0) {
            agentAction.skip();
            return;
        }

        var neighbours = perception.getNeighbours();
        int min_grad = Integer.MAX_VALUE;
        Coordinate move = null;

        for (var n:neighbours) {
            if (n != null && n.isWalkable()) {
                if (n.getGradientRepresentation().isPresent() &&
                        n.getGradientRepresentation().get().getValue() < min_grad) {
                    min_grad = n.getGradientRepresentation().get().getValue();
                    move = new Coordinate(n.getX(), n.getY());
                }
            }
        }

        // take the move with lowest value
        if (move != null) {
            agentAction.step(move.getX(), move.getY());
            return;
        }

        // take a random move
        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        // Shuffle moves randomly
        Collections.shuffle(moves);

        // Check for viable moves
        for (var m : moves) {
            int x = m.getX();
            int y = m.getY();

            // If the area is null, it is outside the bounds of the environment
            //  (when the agent is at any edge for example some moves are not possible)
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                agentAction.step(agentState.getX() + x, agentState.getY() + y);
                return;
            }
        }
    }
}
