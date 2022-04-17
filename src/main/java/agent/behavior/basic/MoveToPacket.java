package agent.behavior.basic;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.ArrayList;
import java.util.List;

public class MoveToPacket extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        if (!agentState.seesPacket()) {
            agentAction.skip();
            return;
        }

        Perception agentPerception = agentState.getPerception();

        int xOffset = 1;
        int yOffset = 1;

        CellPerception packetCell = null;

        // search the nearest packet layer by layer within perception range
        while (true) {

            List<Coordinate> offsets = new ArrayList<>();
            for (int i = -xOffset; i <= xOffset; i++) {
                offsets.add( new Coordinate(i, yOffset) );
                offsets.add( new Coordinate(i, -yOffset) );
            }
            for (int j = -(yOffset-1); j <= yOffset-1; j++) {
                offsets.add( new Coordinate(xOffset, j) );
                offsets.add( new Coordinate(-xOffset, j) );
            }

            // visit all cells in current search range to check whether contain a packet
            for (var offset : offsets) {
                if (agentPerception.getCellPerceptionOnRelPos(offset.getX(),offset.getY()) != null &&
                        agentPerception.getCellPerceptionOnRelPos(offset.getX(),offset.getY()).containsPacket()) {
                    packetCell = agentPerception.getCellPerceptionOnRelPos(offset.getX(),offset.getY());
                    break;
                }
            }

            if (packetCell != null) {
                break;
            }

            // expand the search range
            xOffset = xOffset + 1;
            yOffset = yOffset + 1;
        }

        // find which direction to take
        CellPerception[] neighbors = agentPerception.getNeighboursInOrder();
        int minDistance = 10000;
        CellPerception nextStepCell = null;
        for ( var neighbor : neighbors ) {
            if (neighbor != null && neighbor.isWalkable()) {
                int distance = Perception.ManhattanDistance(neighbor , packetCell);
                if (distance <= minDistance) {
                    minDistance = distance;
                    nextStepCell = neighbor;
                }
            }

        }

        // take next step

        agentAction.step(nextStepCell.getX(), nextStepCell.getY());


    }
}
