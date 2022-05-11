package agent.behavior.part2.behavior;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 02:39
 * @description：When an agent doesn't have a goal, it just wanders to search a possible goal
 * @modified By：
 * @version: $
 */

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 02:39
 * @description：When an agent doesn't have a goal, it just wanders to search a possible goal
 * @modified By：
 * @version: $
 */

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.behavior.part2.CellMemory;
import agent.behavior.part2.Cor;
import agent.behavior.part2.MapMemory;
import agent.behavior.part2.Utils;
import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.Representation;
import environment.world.agent.AgentRep;
import environment.world.destination.DestinationRep;
import environment.world.packet.Packet;
import environment.world.packet.PacketRep;
import util.Pair;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Wander extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        Utils.updateTime(agentState);
        agentState.updateMapMemory();

        if(agentState.getPerception().getAllCells().stream().anyMatch(c ->
                !(c.getX() == agentState.getX() && c.getY() == agentState.getY()) && c.containsAgent())){
            //sending message
            //sending exchange request
            if(agentState.getMemoryFragment("exchange") != null){
                String name = agentState.getMemoryFragment("exchange").split("/")[0];
                for (CellPerception c:agentState.getPerception().getAllCells()){
                    if(c.containsAgent() && c.getAgentRepresentation().get().getName().equals(name)){
                        agentCommunication.sendMessage(c.getAgentRepresentation().get(),
                                "exchange/wander/" + agentState.getMemoryFragment("exchange"));
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

            //mapMessage = "info/" + mapMessage;
            for (CellPerception c: agentState.getAllCellsMemory()){
                if(c.containsAgent() && !(c.getX() == agentState.getX() && c.getY() == agentState.getY())){
                    agentCommunication.sendMessage(c.getAgentRepresentation().get(), mapMessage);
                }
            }
        }

        //handle message
        for (Mail mail:agentCommunication.getMessages()){
            //mail.getMessage().startsWith("info")
            if(mail.message().startsWith("exchange")){
                if(agentState.getMemoryFragment("exchange") != null){
                    String sender = mail.getFrom();
                    String[] m = mail.getMessage().split("/");
                    String name = m[2];
                    Cor goal = new Cor(Integer.parseInt(m[3].split(",")[0]), Integer.parseInt(m[3].split(",")[1]));
                    String[] my =  agentState.getMemoryFragment("exchange").split("/");
                    String requiredSender = my[0];
                    if(sender.equals(requiredSender) && name.equals(agentState.getName())) {
                        if(m[1].equals("wander")){
                            agentState.addMemoryFragment("wander", goal.toString());
                        }

                        else{
                            //System.out.println(agentState.getName() +" 交换前goal" + agentState.getMemoryFragment("goal"));
                            //System.out.println(agentState.getName() + "交换后goal" + m[1]);
                            agentState.addMemoryFragment("goal", m[1]);
                            agentState.removeMemoryFragment("wander");
                        }
                        agentState.addMemoryFragment("steal", "none");
                        if(m[1].contains("destination")){
                            agentState.addMemoryFragment("steal", agentState.getName());
                        }
                    }
                    agentState.removeMemoryFragment("exchange");
                }

            }
            else{
                try {
                    String info = mail.getMessage();
                    //String info = mail.getMessage().split("/")[1];
                    ByteArrayInputStream bais = new ByteArrayInputStream(info.getBytes(StandardCharsets.ISO_8859_1));
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    MapMemory mm = (MapMemory) ois.readObject();
                    agentState.getMapMemory().updateMapMemory(mm);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }



        agentCommunication.clearMessages();
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        //System.out.println(agentState.getName() + "|wander|" + agentState.getMemoryFragment("wander"));


        Cor cur = new Cor(agentState.getX(), agentState.getY());


        if(agentState.getMemoryFragment("steal") != null){
            if(agentState.getMemoryFragment("steal").equals(agentState.getName())){
                CellMemory cellMemory = agentState.getMapMemory().getFirstObstacle(cur);
                Cor obCor = cellMemory.getCoordinate();
                Representation obstacle = cellMemory.getReps().get(0);
                if(Utils.isInReach(agentState, obCor)
                        && !agentState.hasCarry()
                        && ((AgentRep)obstacle).carriesPacket()){
                    agentAction.stealPacket(obCor.getX(), obCor.getY());
                }
                else {
                    agentAction.skip();
                }
            }
            else{
                agentAction.skip();
            }
            agentState.removeMemoryFragment("steal");
            return;
        }

        Cor next = null;
        MapMemory mm = agentState.getMapMemory();
        String wanderGoal = agentState.getMemoryFragment("wander");
        Cor goal = null;
        if(wanderGoal!= null){
            goal = new Cor(Integer.parseInt(wanderGoal.split(",")[0]), Integer.parseInt(wanderGoal.split(",")[1]));
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
                agentState.addMemoryFragment("wander", goal.toString());
                next = mm.getNextMove(cur, goal);
            }
        }
        if(next.getX() == -1 && next.getY() == -1){
            agentAction.skip();
        }
        else{
            if(agentState.getMapMemory().trajContainsObtacle(cur)) {
                CellMemory cellMemory = agentState.getMapMemory().getFirstObstacle(cur);

                Representation obstacle = cellMemory.getReps().get(0);
                if(obstacle instanceof AgentRep){
                    //System.out.println(agentState.getName() + "发现" + ((AgentRep) obstacle).getName() +"挡路");
                    if(agentState.getPerception().getCellPerceptionOnAbsPos(next.getX(), next.getY()).isWalkable()){
                        agentAction.step(next.getX(), next.getY());
                    }
                    else{
                        agentAction.skip();
                        agentState.addMemoryFragment("exchange", ((AgentRep)obstacle).getName() + "/" + goal);
                    }
                }
                else if(obstacle instanceof PacketRep){
                    if(((PacketRep) obstacle).getColor().equals(agentState.getColor())){
                        agentState.addMemoryFragment("clear", obstacle.getX() +","+obstacle.getY());
                    }
                    else{
                        agentState.addMemoryFragment("help", String.valueOf(((PacketRep) obstacle).getColor().getRGB()));
                    }
                    agentAction.skip();
                }
                else{
                    //不存在的情况
                    agentAction.skip();
                }
            }
            else{
                agentState.removeMemoryFragment("exchange");
                agentAction.step(next.getX(), next.getY());
            }
        }
        //agentState.updateMapMemory();
    }
}
