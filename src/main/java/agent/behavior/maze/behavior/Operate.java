package agent.behavior.maze.behavior;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 18:38
 * @description：Agent picks up, drops or do anything except moving
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
import environment.Perception;
import environment.world.packet.PacketRep;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 18:38
 * @description：Agent picks up, drops or do anything except moving
 * @modified By：
 * @version: $
 */

public class Operate extends Behavior{
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
        Utils.updateTime(agentState);
        agentState.updateMapMemory();

        if(agentState.getPerception().getAllCells().stream().anyMatch(c ->
                !(c.getX() == agentState.getX() && c.getY() == agentState.getY()) && c.containsAgent())){
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

        agentCommunication.clearMessages();
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {


        Perception perception = agentState.getPerception();
        Cor goalCor = Utils.getCoordinateFromGoal(agentState);
        String target = Utils.getTargetFromGoal(agentState);
        Color color = Utils.getTargetColorFromGoal(agentState);
        CellPerception goalCell = perception.getCellPerceptionOnAbsPos(goalCor.getX(), goalCor.getY());
        if(target.equals("packet")){
            if(goalCell.getRepOfType(PacketRep.class) != null && color.equals(goalCell.getRepOfType(PacketRep.class).getColor())){
                agentAction.pickPacket(goalCor.getX(), goalCor.getY());
            }
            else{
                agentAction.skip();
            }
        }
        else if(target.startsWith("destination")){
            agentAction.putPacket(goalCor.getX(), goalCor.getY());
        }
        else agentAction.skip();
    }
}
