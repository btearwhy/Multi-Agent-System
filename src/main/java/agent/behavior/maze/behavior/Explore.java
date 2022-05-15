package agent.behavior.maze.behavior;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:39
 * @description：When an agent doesn't have a goal, it just explores to search a possible goal
 * @modified By：
 * @version: $
 */


import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.maze.Cor;
import agent.behavior.maze.MapMemory;
import agent.behavior.maze.Utils;
import environment.CellPerception;
import environment.Mail;
import environment.Representation;
import environment.world.agent.AgentRep;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Explore extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

        Utils.updateTime(agentState);
        agentState.updateMapMemory();

        //handle message
        for (Mail mail:agentCommunication.getMessages()){
            //mail.getMessage().startsWith("info")
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
                        }
                        else{
                            agentState.addMemoryFragment("goal", m[1]);
                            agentState.removeMemoryFragment("explore");
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
                        String message = "exchange/" + "explore" +"/" + agentState.getMemoryFragment("exchange");
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

        Cor next = null;
        MapMemory mm = agentState.getMapMemory();
        String exploreGoal = agentState.getMemoryFragment("explore");
        Cor goal = null;
        if(exploreGoal!= null){
            goal = new Cor(Integer.parseInt(exploreGoal.split(",")[0]), Integer.parseInt(exploreGoal.split(",")[1]));
            if(!mm.discovered(goal)){
                next = agentState.getMapMemory().getNextMove(cur, goal);
            }
        }

        if(next == null || (next.getX() == -1 && next.getY() == -1)){
            goal = mm.getRandomUndiscoveredCor();
            if(goal.getX() == -1 && goal.getY() == -1){
                next = new Cor(-1, -1);
            }
            else{
                agentState.addMemoryFragment("explore", goal.toString());
                next = mm.getNextMove(cur, goal);
            }
        }
        if(next.getX() == -1 && next.getY() == -1){
            agentAction.skip();
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
