package agent.behavior.maze.behavior;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:15
 * @description：An agent has a goal and move towards the goal until the goal is in reach
 * @modified By：
 * @version: $
 */

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.maze.*;
import environment.*;
import environment.world.agent.AgentRep;
import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 18:15
 * @description：An agent has a goal and move towards the goal until the goal is in reach
 * @modified By：
 * @version: $
 */

public class Navigate extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        Utils.updateTime(agentState);
        agentState.updateMapMemory();

        //handle message
        for (Mail mail:agentCommunication.getMessages()){
            if(mail.message().startsWith("exchange")){
                String sender = mail.getFrom();
                if(agentState.getMemoryFragment("exchange") != null
                        && agentState.getMemoryFragment("exchangeRequest") != null
                        && agentState.getMemoryFragment("exchangeRequest").equals(sender)){

                    String[] m = mail.getMessage().split("/");
                    String name = m[2];
                    Cor goal = new Cor(Integer.parseInt(m[3].split(",")[0]), Integer.parseInt(m[3].split(",")[1]));
                    String[] my =  agentState.getMemoryFragment("exchange").split("/");
                    String requiredSender = my[0];
                    if(sender.equals(requiredSender) && name.equals(agentState.getName())) {
                        if(m[1].equals("explore")){
                            agentState.addMemoryFragment("explore", goal.toString());
                            agentState.removeMemoryFragment("goal");
                        }
                        else{
                            agentState.addMemoryFragment("goal", m[1]);
                        }
                        agentState.addMemoryFragment("steal", "none");
                        if(m[1].contains("destination")){
                            agentState.addMemoryFragment("steal", sender);
                        }
                        agentState.removeMemoryFragment("exchange");
                    }
                }
            }
            else{
                try {
                    String info = mail.getMessage();
                    ByteArrayInputStream bais = new ByteArrayInputStream(info.getBytes(StandardCharsets.ISO_8859_1));
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    MapMemory mm = (MapMemory) ois.readObject();
                    agentState.getMapMemory().updateMapMemory(mm);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        agentState.removeMemoryFragment("exchangeRequest");


        //sending message
        //sending exchange request
        if(agentState.getPerception().getAllCells().stream().anyMatch(c ->
                !(c.getX() == agentState.getX() && c.getY() == agentState.getY()) && c.containsAgent())){


            if(agentState.getMemoryFragment("exchange") != null){
                String name = agentState.getMemoryFragment("exchange").split("/")[0];
                for (CellPerception c:agentState.getPerception().getAllCells()){
                    if(c.containsAgent() && c.getAgentRepresentation().get().getName().equals(name)){
                        String message = "exchange/" + agentState.getMemoryFragment("goal") +"/" + agentState.getMemoryFragment("exchange");
                        agentCommunication.sendMessage(c.getAgentRepresentation().get(), message);
                        agentState.addMemoryFragment("exchangeRequest", name);
                        break;
                    }
                }
            }



            //sending map info
            String mapMessage = "";
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                MapMemory mapMemory = agentState.getMapMemory();
                oos.writeObject(mapMemory);
                oos.writeObject(null);
                oos.flush();
                mapMessage = baos.toString(StandardCharsets.ISO_8859_1);
                oos.close();
                baos.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            for (CellPerception c: agentState.getAllCellsMemory()){
                if(c.containsAgent() && !(c.getX() == agentState.getX() && c.getY() == agentState.getY())){
                    agentCommunication.sendMessage(c.getAgentRepresentation().get(), mapMessage);
                }
            }
        }

        agentState.removeMemoryFragment("exchange");



        agentCommunication.clearMessages();

    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        agentState.updateMapMemory();

        AgentRep a= agentState.getPerception().getCellPerceptionOnRelPos(0, 0).getAgentRepresentation().get();
        Cor cur = new Cor(agentState.getX(), agentState.getY());
        if(agentState.getMemoryFragment("steal") != null){
            if(!agentState.getMemoryFragment("steal").equals("none")){
                String stealee = agentState.getMemoryFragment("steal");
                CellPerception k = null;
                for(CellPerception c:agentState.getPerception().getNeighbours()){
                    if(c != null && c.getAgentRepresentation().isPresent() && c.getAgentRepresentation().get().getName().equals(stealee)){
                        k = c;
                        break;
                    }
                }
                if(k != null  && !agentState.hasCarry() && k.getAgentRepresentation().get().carriesPacket()) {
                    agentAction.stealPacket(k.getX(), k.getY());
                }
                else agentAction.skip();
            }
            else{
                agentAction.skip();
            }
            agentState.removeMemoryFragment("steal");
            return;
        }

        Cor goal = Utils.getCoordinateFromGoal(agentState);

        for (CellPerception c:agentState.getPerception().getAllCells()){
            if(agentState.hasCarry()){
                DestinationRep d = c.getRepOfType(DestinationRep.class);
                if(d != null && d.getColor().equals(Utils.getTargetColorFromGoal(agentState))
                        && Perception.distance(d.getX(), d.getY(), cur.getX(), cur.getY())
                            < Perception.distance(goal.getX(), goal.getY(), cur.getX(), cur.getY())){
                    goal = new Cor(d.getX(), d.getY());
                    Utils.updateGoalCor(agentState, goal);
                    break;
                }
            }
            else{
                PacketRep p = c.getRepOfType(PacketRep.class);
                if(p != null && p.getColor().equals(Utils.getTargetColorFromGoal(agentState))
                        && Perception.distance(p.getX(), p.getY(), cur.getX(), cur.getY())
                            < Perception.distance(goal.getX(), goal.getY(), cur.getX(), cur.getY())){
                    goal = new Cor(p.getX(), p.getY());
                    Utils.updateGoalCor(agentState, goal);
                    break;
                }
            }
        }


        Cor next = agentState.getMapMemory().getNextMove(cur, goal);
        if(next.equals(new Cor(-1, -1))) {
            if(Utils.trapped(agentState)){
                agentState.getMapMemory().getDstarLite().startOver(cur, goal);
                Cor n = agentState.getMapMemory().getNextMove(cur, goal);
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
            if(agentState.getMapMemory().trajContainsObtacle(cur)){
                List<Cor> traj = agentState.getMapMemory().getTrajectory(cur);
                Representation obstacle = null;
                for (Cor cor:traj){

                    CellPerception c = agentState.getPerception().getCellPerceptionOnAbsPos(cor.getX(), cor.getY());
                    if(c != null){
                        if(!c.isWalkable()){
                            obstacle = c.getReps().get(0);
                            break;
                        }
                    }
                    else break;
                }
                if(obstacle == null){
                    agentAction.step(next.getX(), next.getY());
                }
                else{
                    if(obstacle instanceof AgentRep){
                       if(agentState.getPerception().getCellPerceptionOnAbsPos(next.getX(), next.getY()).isWalkable()){
                            agentAction.step(next.getX(), next.getY());
                        }
                        else {
                            agentAction.skip();
                            agentState.addMemoryFragment("exchange", ((AgentRep)obstacle).getName() + "/" + goal);
                        }

                    }
                    else{
                        agentAction.skip();
                    }
                }
            }
            else{
                agentState.removeMemoryFragment("exchange");
                agentAction.step(next.getX(), next.getY());

            }
        }
    }
}
