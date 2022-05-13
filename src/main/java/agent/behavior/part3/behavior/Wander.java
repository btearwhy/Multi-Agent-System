package agent.behavior.part3.behavior;/**
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
import agent.behavior.part3.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import environment.Mail;
import environment.CellPerception;
import environment.Coordinate;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Wander extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // receive request message and add into memory
        if (agentCommunication.getNbMessages() > 0){
            Collection<Mail> mails = agentCommunication.getMessages();
            for (Mail m : mails){
                JsonObject packet_request = new Gson().fromJson(m.getMessage(), JsonObject.class);

                // doesn't have memory fragment "be_requested", create
                if (agentState.getMemoryFragment("be_requested") == null){
                    JsonArray be_requested = new JsonArray();



                    be_requested.add(packet_request);
                    agentState.addMemoryFragment("be_requested",be_requested.toString());
                }

                // "be_requested" exists in memory
                JsonArray be_requested = new Gson().fromJson(agentState.getMemoryFragment("be_requested"),
                        JsonArray.class);
                // if agent already remember the packet don't put it into memory; if not add into memory
                if (!Utils.jsonarray_contain(be_requested, packet_request)){
                    be_requested.add(packet_request);
                }
            }

            // process all messages, clear
            agentCommunication.clearMessages();

        }

    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentState.updateMapMemory();

        Utils.addRequestMemory(agentState);

        int dir;
        if(agentState.getPerceptionLastCell() == null){
            Random ra = new Random();
            dir = ra.nextInt(8);
            dir = Utils.getClockwiseDirectionIfBlocked(agentState, dir);
        }
        else{
            int preDir = Utils.getPreviousDir(agentState);
            Random rb = new Random();
            int ran = rb.nextInt(100);
            int t = 7;
            if(ran < t) dir = (preDir + 3) % 8;
            else if(ran < 2 * t) dir = (preDir + 5) % 8;
            else if(ran < 4 * t) dir = (preDir + 1) % 8;
            else if(ran < 6 * t) dir = (preDir + 2) % 8;
            else if(ran < 8 * t) dir = (preDir + 6) % 8;
            else if(ran < 10 * t) dir = (preDir + 7) % 8;
            else dir = preDir;
            dir = Utils.getClockwiseDirectionIfBlocked(agentState, dir);
        }

        agentAction.step(agentState.getX() + Utils.moves.get(dir).getX(), agentState.getY() + Utils.moves.get(dir).getY());

        Utils.updateAgentNum(agentState);

//        int j = 0;
//        for (CellPerception c:agentState.getPerception().getAllCells()){
//            if(c.getNbReps() == 0){
//                System.out.print('#');
//            }
//            if(c.getRepOfType(PacketRep.class) != null){
//                System.out.print('1');
//            }
//            if(c.getRepOfType(DestinationRep.class) != null){
//                System.out.print('2');
//            }
//            if(j++ % 12 == 11) {
//                System.out.print('\n');
//            }
//        }
//        System.out.println("/////////////");
//        int i = 0;
//        List<CellMemory> cells = agentState.getAllCellsMemory();
//        Collections.sort(cells, new Comparator<CellMemory>() {
//            @Override
//            public int compare(CellMemory o1, CellMemory o2) {
//                int o = o1.getY() - o2.getY();
//                if(o != 0) return o;
//                return o1.getX() - o2.getX();
//            }
//        });
//        for (CellMemory c:cells){
//            if(c.getNbReps() == 0){
//                System.out.print('#');
//            }
//            if(c.getRepOfType(PacketRep.class) != null){
//                System.out.print('1');
//            }
//            if(c.getRepOfType(DestinationRep.class) != null){
//                System.out.print('2');
//            }
//            if(i++ % 12 == 11) {
//                System.out.print('\n');
//            }
//        }
    }
}
