package agent.behavior.myfirstagent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.Coordinate;
import org.checkerframework.checker.units.qual.C;

public class MyFirstAgent extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // get perception
        var perception = agentState.getPerception();
        int width = perception.getWidth();
        int height = perception.getHeight();
        int offsetX = perception.getOffsetX();
        int offsetY = perception.getOffsetY();

        // find the closest packet using manhattan distance and move towards it
        if (!agentState.hasCarry() && agentState.seesPacket()) {
            int man_dist = Integer.MAX_VALUE;
            Coordinate closestPacket = null;
            Coordinate move = new Coordinate(0,0);
            for (int i = offsetX; i < offsetX + width; i++) {
                for (int j = offsetY; j < offsetY + height; j++) {
                    if (perception.getCellPerceptionOnAbsPos(i, j) != null
                            && perception.getCellPerceptionOnAbsPos(i, j).containsPacket()) {
                        int d = Math.abs(i - perception.getSelfX()) + Math.abs(j - perception.getSelfY());
                        if (d < man_dist) {
                            man_dist = d;
                            closestPacket = new Coordinate(i, j);
                            move = new Coordinate(
                                    (int) Math.signum(i - perception.getSelfX()),
                                    (int) Math.signum(j - perception.getSelfY()));
                        }
                    }
                }
            }
            if (closestPacket != null
                    && Math.abs(closestPacket.getX() - agentState.getX()) <= 1
                    && Math.abs(closestPacket.getY() - agentState.getY()) <= 1) {
                // packet is neighbor
                agentAction.pickPacket(closestPacket.getX(), closestPacket.getY());
                return;
            } else {
                // take the move
                if (perception.getCellPerceptionOnRelPos(move.getX(), move.getY()) != null
                        && perception.getCellPerceptionOnRelPos(move.getX(), move.getY()).isWalkable()) {
                    agentAction.step(agentState.getX() + move.getX(), agentState.getY() + move.getY());
                    return;
                }
            }
        } else if (agentState.hasCarry() && agentState.seesDestination()) {
            int man_dist = Integer.MAX_VALUE;
            Coordinate closestDestination = null;
            Coordinate move = new Coordinate(0,0);
            for (int i = offsetX; i < offsetX + width; i++) {
                for (int j = offsetY; j < offsetY + height; j++) {
                    if (perception.getCellPerceptionOnAbsPos(i, j) != null
                            && perception.getCellPerceptionOnAbsPos(i, j).containsAnyDestination()) {
                        int d = Math.abs(i - perception.getSelfX()) + Math.abs(j - perception.getSelfY());
                        if (d < man_dist) {
                            man_dist = d;
                            closestDestination = new Coordinate(i, j);
                            move = new Coordinate(
                                    (int) Math.signum(i - perception.getSelfX()),
                                    (int) Math.signum(j - perception.getSelfY()));
                        }
                    }
                }
            }
            if (closestDestination != null
                    && Math.abs(closestDestination.getX() - agentState.getX()) <= 1
                    && Math.abs(closestDestination.getY() - agentState.getY()) <= 1){
                // Destination is neighbor
                agentAction.putPacket(closestDestination.getX(), closestDestination.getY());
                return;
            } else {
                // take the move
                if (perception.getCellPerceptionOnRelPos(move.getX(), move.getY()) != null
                        && perception.getCellPerceptionOnRelPos(move.getX(), move.getY()).isWalkable()) {
                    agentAction.step(agentState.getX() + move.getX(), agentState.getY() + move.getY());
                    return;
                }
            }
        } else {
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
            for (var move : moves) {
                int x = move.getX();
                int y = move.getY();

                // If the area is null, it is outside the bounds of the environment
                //  (when the agent is at any edge for example some moves are not possible)
                if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                    agentAction.step(agentState.getX() + x, agentState.getY() + y);
                    return;
                }
            }

        }


        //agentAction.step(agentState.getX() + 1, agentState.getY() + 1);
        // No viable moves, skip turn
        agentAction.skip();
    }
}
