package agent.behavior.part2.behavior;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.part2.Utils;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Charge extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentState.updateMapMemory();
        var perception = agentState.getPerception();

        var neighbours = perception.getNeighbours();

        // put down the packet
//        if (agentState.hasCarry()){
//            var last_perception = agentState.getPerceptionLastCell();
//            agentAction.putPacket(last_perception.getX(), last_perception.getY());
//            return;
//        }

        if(agentState.hasCarry()){
            for (CellPerception c:perception.getNeighbours()){
                if(c != null && c.isWalkable()){
                    agentAction.putPacket(c.getX(), c.getY());
                    return;
                }
            }
            agentAction.skip();
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

//        CellPerception min_cell = null;
        // find the min gradient value
        for (var n : neighbours) {
            if (n != null && n.isWalkable()) {
                if ( n.containsGradient() &&
                        n.getGradientRepresentation().get().getValue() < min_grad){
                    min_grad = n.getGradientRepresentation().get().getValue();
//                    min_cell = n;
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

//        Coordinate cur  = new Coordinate(agentState.getX(), agentState.getY());
//        Coordinate goal = new Coordinate(min_cell.getX(), min_cell.getY());
//        if(min_cell == null || (min_grad == 0 && min_cell.containsAgent())) {
//            agentAction.skip();
//            System.out.println(agentState.getName() + "skip1");
//        }
//        else{
//            Coordinate next = agentState.getMapMemory().getNextMove(cur, goal);
//            if(next.getX() == -1 && next.getY() == -1) {
//                if(Utils.trapped(agentState)){
//                    agentState.getMapMemory().getDstarLite().startOver(cur, goal);
//                    Coordinate n = agentState.getMapMemory().getNextMove(cur, goal);
//                    agentAction.step(n.getX(), n.getY());
//                }
//                else{
//                    agentAction.skip();
//                }
//                System.out.println(agentState.getName() + "skip2");
//            }
//            else {
//                //System.out.println(agentState.getName() + "goto" + station);
//                agentAction.step(next.getX(), next.getY());
//            }
//        }


         //extract all neighbors' coordinates with min gradient value
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
