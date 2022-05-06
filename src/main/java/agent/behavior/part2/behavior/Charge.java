package agent.behavior.part2.behavior;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.part2.Utils;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.Perception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Charge extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        var perception = agentState.getPerception();

        var neighbours = perception.getNeighbours();

        var last_perception = agentState.getPerceptionLastCell();
        // put down the packet
        if (agentState.hasCarry()){
            agentAction.putPacket(last_perception.getX(), last_perception.getY());
            return;
        }

        // find the target Energy station
        int energy_distance = Integer.MAX_VALUE;
        CellPerception target = null;
        for (CellPerception cell : perception.getAllCells()){
            if (cell != null && cell.containsEnergyStation()){
                if (Perception.distance(perception.getCellPerceptionOnRelPos(0,0), cell) < energy_distance){
                    energy_distance = Perception.distance(perception.getCellPerceptionOnRelPos(0,0),cell);
                    target = cell;
                }
            }
        }

        // whether to wait
        if(target != null){
            for (int i = 1; i < 4; i++){
                CellPerception walk_place = perception.getCellPerceptionOnAbsPos(target.getX(),target.getY()-i);
                if (walk_place != null && walk_place.containsAgent() &&
                        walk_place.getAgentRepresentation().get().getName() != agentState.getName()){

                    agentAction.skip();
                    return;


                }
            }
        }

        // is at the charging position
        if (perception.getCellPerceptionOnRelPos(0, 0).containsGradient()
                && perception.getCellPerceptionOnRelPos(0, 0).getGradientRepresentation().get().getValue() == 0) {
            agentAction.skip();
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
        Utils.updateAgentNum(agentState);
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
