package agent.behavior.energy.behavior;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.energy.Utils;
import environment.CellPerception;
import environment.Mail;
import environment.Perception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Wait extends Behavior {
    private int jam_avoid_number;

    private boolean wait_flag = true;

    public Wait(int jam_avoid_number_number) {
        this.jam_avoid_number = jam_avoid_number_number;
    }

    public int getJamNumber(){
        return this.jam_avoid_number;
    }

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        agentState.updateMapMemory();

        agentCommunication.broadcastMessage(String.valueOf(agentState.getBatteryState()));
        Collection<Mail> mails = agentCommunication.getMessages();

        // update electricity information in memory
        Utils.memoryElectricity(agentState, mails);


        agentCommunication.clearMessages();
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        Utils.updateAgentNum(agentState);
        //System.out.println("name: " + agentState.getName() + " energy: " + agentState.getBatteryState());

        var perception = agentState.getPerception();

        var neighbours = perception.getNeighbours();


        // find the target Energy station
        int energy_distance = Integer.MAX_VALUE;
        CellPerception target = null;
        for (CellPerception cell : perception.getAllCells()) {
            if (cell != null && cell.containsEnergyStation()) {
                if (Perception.distance(perception.getCellPerceptionOnRelPos(0, 0), cell) < energy_distance) {
                    energy_distance = Perception.distance(perception.getCellPerceptionOnRelPos(0, 0), cell);
                    target = cell;
                }
            }
        }

        // Wait : another agent is charging
        if (target != null) {
            for (int i = 1; i <= jam_avoid_number; i++) {
                CellPerception walk_place = perception.getCellPerceptionOnAbsPos(target.getX(), target.getY() - i);
                if (walk_place != null && walk_place.containsAgent() &&
                        walk_place.getAgentRepresentation().get().getName() != agentState.getName()) {

                    agentAction.skip();
                    return;


                }
            }
        }

        // Wait : according to priority
        int gradient_value = perception.getCellPerceptionOnRelPos(0, 0).getGradientRepresentation().get().getValue();
        if (agentState.getMemoryFragmentKeys().contains("electricity")) {
            String min_agent = Utils.getLowestEnergy(agentState);

            if (min_agent != agentState.getName()) {
                agentState.removeMemoryFragment("electricity");
                agentAction.skip();
                return;
            }
        }

        // Walk to the station
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

        if (min_neighbours.size() == 1){
            target_cell = min_neighbours.get(0);
        }
        else {
            List<Integer> nb_neighbors = new ArrayList<Integer>();
            for (CellPerception cell : min_neighbours) {
                nb_neighbors.add(Utils.count_walkable_neighbors(perception, cell));
            }
            int initial_max_index = nb_neighbors.indexOf(Collections.max(nb_neighbors));
            target_cell = min_neighbours.get(initial_max_index);

            // to avoid revisit previous cell

            if (agentState.getPerceptionLastCell() != null){


                if (target_cell.getX() == agentState.getPerceptionLastCell().getX()
                        && target_cell.getY() == agentState.getPerceptionLastCell().getY()){
                    min_neighbours.remove(initial_max_index);
                    nb_neighbors.remove(initial_max_index);

                    int second_max_index = nb_neighbors.indexOf(Collections.max(nb_neighbors));
                    target_cell = min_neighbours.get(second_max_index);
                }
            }

        }

        /*Coordinate cur = new Coordinate(agentState.getX(), agentState.getY());
        Coordinate goal = new Coordinate(target.getX(), target.getY()-jam_avoid_number);
        Coordinate target_cell = agentState.getMapMemory().getNextMove(cur, goal);
        System.out.println(target_cell);
        if(target_cell.equals(new Coordinate(-1, -1))) {
            if(Utils.trapped(agentState)){
                agentState.getMapMemory().getDstarLite().startOver(cur, goal);
                Coordinate n = agentState.getMapMemory().getNextMove(cur, goal);
                CellPerception m = agentState.getPerception().getCellPerceptionOnAbsPos(n.getX(), n.getY());
                if(m != null && m.isWalkable())
                    agentAction.step(n.getX(), n.getY());
                else agentAction.skip();
            }
            else{
                agentAction.skip();
            }
        }
        else{

            CellPerception m = agentState.getPerception().getCellPerceptionOnAbsPos(target_cell.getX(), target_cell.getY());
            if(m != null && m.isWalkable()){
                System.out.println("Agent: " + agentState.getName() + " steps from (" + agentState.getX() + ","
                        + agentState.getY() + ") to (" + target_cell.getX() + "," + target_cell.getY() + ")");
                agentAction.step(target_cell.getX(), target_cell.getY());}
            else agentAction.skip();
        }*/

        agentAction.step(target_cell.getX(),target_cell.getY());
        return;

    }

}
