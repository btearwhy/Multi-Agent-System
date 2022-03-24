
package agent.behavior.part1A;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

public class Part1A extends Behavior {

	@Override
	public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
		// No communication
	}
    //cd D:\Guangxuan\上课\Multi-Agent Sys\project\PacketWorld-master
	//mvn compile exec:java


	@Override
	public void act(AgentState agentState, AgentAction agentAction) {

        
		// pick & put packet
		//
		Perception perception = agentState.getPerception();
        int perception_width = perception.getWidth();
        int perception_height  = perception.getHeight();
        int perception_x = perception.getOffsetX();
        int perception_y = perception.getOffsetY();
        int agent_x = agentState.getX();
        int agent_y = agentState.getY();        
        
        int shortest_distance = Integer.MAX_VALUE;
        if (agentState.hasCarry()) {
        	//agent has a packet
        	Color color = agentState.getCarry().get().getColor();
        	if (agentState.seesDestination(color)) {
        		// put packet
        		CellPerception  nearest_destination = null;
                for (int i = perception_x; i < perception_x + perception_width; i++) {
                    for (int j = perception_y; j < perception_y + perception_height ; j++) {
                    	CellPerception cell = perception.getCellPerceptionOnAbsPos(i, j);
                        if (cell != null&& cell.containsDestination(color)) {
                        	int distance = Perception.distance(agent_x,agent_y,i,j);
                            if (distance < shortest_distance) {
                                shortest_distance = distance;
                                nearest_destination =cell;
                                
                            }
                        }
                    }
                }
                if (shortest_distance == 1) {
                    // Destination is neighbor
                    agentAction.putPacket(nearest_destination.getX(), nearest_destination.getY());
                    return;
                } else {
                	Coordinate move = new Coordinate(
                            (int) Math.signum(nearest_destination.getX() - agentState.getX()),
                            (int) Math.signum(nearest_destination.getY() - agentState.getY()));
                    // take the move
                    if (perception.getCellPerceptionOnRelPos(move.getX(), move.getY()) != null
                            && perception.getCellPerceptionOnRelPos(move.getX(), move.getY()).isWalkable()) {
                        agentAction.step(agentState.getX() + move.getX(), agentState.getY() + move.getY());
                        return;
                    }
                }
        	}
        }else {
        	//agent does not have a packet
        	if (agentState.seesPacket()){
        		//pick packet
        		 CellPerception nearest_packet  = null;
                 for (int i = perception_x; i < perception_x + perception_width; i++) {
                     for (int j = perception_y; j < perception_y + perception_height ; j++) {
                    	 CellPerception cell = perception.getCellPerceptionOnAbsPos(i, j);
                         if (cell != null&& cell.containsPacket()) {
                             int distance = Perception.distance(agent_x,agent_y,i,j);
                             if (distance < shortest_distance) {
                                 shortest_distance = distance;
                                 nearest_packet  = cell;
                             }
                         }
                     }
                 }
                 if (shortest_distance == 1) {
                     // packet is neighbor
                     agentAction.pickPacket(nearest_packet .getX(), nearest_packet .getY());
                     return;
                 } else {
                	 Coordinate move = new Coordinate(
                             (int) Math.signum(nearest_packet.getX() - agent_x),
                             (int) Math.signum(nearest_packet .getY() - agent_y));
                     // take the move
                     if (perception.getCellPerceptionOnRelPos(move.getX(), move.getY()).isWalkable()) {
                         agentAction.step(agentState.getX() + move.getX(), agentState.getY() + move.getY());
                         return;
                     }
                 }
        	}
           
        } 

        // wandering 
        //
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

        // No viable moves, skip turn
        agentAction.skip();
	}
}
