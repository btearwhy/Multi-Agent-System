package agent.behavior.part1b;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;

import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import javax.swing.*;
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

        var neighbours = perception.getNeighbours();

        // put down the packet
        if (agentState.hasCarry()){
            var last_perception = agentState.getPerceptionLastCell();
            agentAction.putPacket(last_perception.getX(), last_perception.getY());
            return;
        }

        // energy station is occupied, wait for charging
        for (CellPerception cell : perception.getAllCells()){
            if (cell != null && cell.containsEnergyStation()){
                CellPerception charge_place = perception.getCellPerceptionOnAbsPos(cell.getX(),cell.getY()-1);
                if (charge_place != null && charge_place.containsAgent()){
                    agentAction.skip();
                    return;
                }
            }
        }

        // is at the charging position
        if (perception.getCellPerceptionOnRelPos(0, 0).containsGradient()
                && perception.getCellPerceptionOnRelPos(0, 0).getGradientRepresentation().get().getValue() == 0) {
            agentAction.skip();
            System.out.println("Charging");
            return;
        }

        CellPerception current_cell = perception.getCellPerceptionOnRelPos(0,0);

        int min_grad = Integer.MAX_VALUE;
        // find the min gradient value
        for (var n : neighbours) {
            if (n != null && n.isWalkable()) {
                if ( n.containsGradient() &&
                        n.getGradientRepresentation().get().getValue() < min_grad){
                    min_grad = n.getGradientRepresentation().get().getValue();
                }
            }

            // charging station is occupied
            //if (n != null && n.containsAgent() && n.containsGradient()
                    //&& n.getGradientRepresentation().get().getValue() <
                    //current_cell.getGradientRepresentation().get().getValue()){
                //agentAction.skip();
                //System.out.println("Wait for charging");
            //}
        }

        // extract all neighbors' coordinates with min gradient value
        List<CellPerception> min_neighbours = new ArrayList<CellPerception>();
        for (var n : neighbours) {
            if (n != null && n.isWalkable()){
                if (n.containsGradient() &&
                        n.getGradientRepresentation().get().getValue() == min_grad){
                    min_neighbours.add(n);

                }
            }
        }

        // find the step with the max number of neighbors
        CellPerception target_cell = null;
        if (min_neighbours.size() == 0){
            agentAction.skip();
            System.out.println("No way");
            return;
        }
        else{
            int max_num_neighbors = 0;
            for (CellPerception cell : min_neighbours){
                int current_num_neighbors = count_walkable_neighbors(perception, cell);
                if (current_num_neighbors >= max_num_neighbors){
                    max_num_neighbors = current_num_neighbors;
                    target_cell = cell;
                }
            }
        }

        agentAction.step(target_cell.getX(),target_cell.getY());
        System.out.println("To station");
        return;
    }

    public int count_walkable_neighbors(Perception perception, CellPerception cell){
        int x = cell.getX();
        int y = cell.getY();

        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));


        int count = 0;
        for (Coordinate m : moves){
            CellPerception neighbor_cell = perception.getCellPerceptionOnAbsPos(x+m.getX(),y+m.getY());
            if ( neighbor_cell != null && neighbor_cell.isWalkable()){
                count ++;
            }
        }

        return count;

    }
}
