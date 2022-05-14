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
        Collection<Mail> mails = agentCommunication.getMessages();
        for (Mail m : mails){
            JsonObject packetInfo = new Gson().fromJson(m.getMessage(), JsonObject.class);
            Utils.pushRequestedQueue(agentState, packetInfo);
        }

        // process all messages, clear
        agentCommunication.clearMessages();

    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentState.updateMapMemory();

        //Utils.addRequestMemory(agentState);

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

    }
}
