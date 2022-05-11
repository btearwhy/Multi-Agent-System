package agent.behavior.part2.behavior;/**
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
import agent.behavior.part2.*;
import environment.*;
import environment.world.agent.AgentRep;
import environment.world.destination.DestinationRep;
import environment.world.packet.Packet;
import environment.world.packet.PacketRep;
import environment.world.wall.SolidWallRep;
import environment.world.wall.WallRep;

import javax.swing.*;
import java.io.*;
import java.nio.charset.Charset;
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

        if(agentState.getPerception().getAllCells().stream().anyMatch(c ->
                !(c.getX() == agentState.getX() && c.getY() == agentState.getY()) && c.containsAgent())){
            //sending message
            //sending exchange request

            if(agentState.getMemoryFragment("exchange") != null){
                String name = agentState.getMemoryFragment("exchange").split("/")[0];
                for (CellPerception c:agentState.getPerception().getAllCells()){
                    if(c.containsAgent() && c.getAgentRepresentation().get().getName().equals(name)){
                        String message = "exchange/" + agentState.getMemoryFragment("goal") +"/" + agentState.getMemoryFragment("exchange");
                        agentCommunication.sendMessage(c.getAgentRepresentation().get(), message);
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
                            agentState.removeMemoryFragment("goal");
                        }
                        else{
                            //System.out.println(agentState.getName() +" 交换前goal" + agentState.getMemoryFragment("goal"));
                            //System.out.println(agentState.getName() + "交换后goal" + m[1]);
                            agentState.addMemoryFragment("goal", m[1]);
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
        //System.out.println(agentState.getName() + "|navigate|" + agentState.getMemoryFragment("goal"));

        if(agentState.getName().equals("a")){
            System.out.println("行动前");
            System.out.println(agentState.getName() + "|navigate|" + agentState.getMemoryFragment("goal"));
            agentState.getMapMemory().show(10, 10);
            System.out.println();
        }
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


        Cor goal = Utils.getCoordinateFromGoal(agentState);
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
                CellMemory cellMemory = agentState.getMapMemory().getFirstObstacle(cur);

                Representation obstacle = cellMemory.getReps().get(0);
                if(obstacle instanceof AgentRep){
                    //System.out.println(agentState.getName() + "发现" + ((AgentRep) obstacle).getName() + "在" +obstacle.getX() + "," + obstacle.getY() +"挡路");
                    if(agentState.getPerception().getCellPerceptionOnAbsPos(next.getX(), next.getY()).isWalkable()){
                        agentAction.step(next.getX(), next.getY());
                    }
                    else{
                        agentAction.skip();
                        agentState.addMemoryFragment("exchange", ((AgentRep)obstacle).getName() + "/" + goal);
                    }
                    //两者带不同颜色包相撞，无法处理
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
//                CellPerception m = agentState.getPerception().getCellPerceptionOnAbsPos(next.getX(), next.getY());
//                if(m != null && m.isWalkable())
//                    agentAction.step(next.getX(), next.getY());
//                else agentAction.skip();
            }
        }

        if(agentState.getName().equals("a")){
            System.out.println("行动后---------------");
            System.out.println(agentState.getName() + "|navigate|" + agentState.getMemoryFragment("goal"));
            agentState.getMapMemory().show(10, 10);
            System.out.println();
        }
        //System.out.println(agentState.getName() + "|" + agentState.getMemoryFragment("exchange"));
        //Utils.updateAgentNum(agentState);
        //agentState.updateMapMemory();
    }
}
