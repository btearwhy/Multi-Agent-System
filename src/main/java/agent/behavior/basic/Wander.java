package agent.behavior.basic;/**
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

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.packet.PacketRep;

public class Wander extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        int dir;
        if(!agentState.getMemoryFragmentKeys().contains("goal") && !agentState.getMemoryFragmentKeys().contains("previous")){
            agentState.addMemoryFragment("goal", "");
            agentState.addMemoryFragment("previous", ";");
        }
        if(agentState.getMemoryFragment("previous").equals(";")){
            Random ra = new Random();
            dir = ra.nextInt(8);
            dir = Utils.getClockwiseDirection(agentState, dir);
            Utils.updatePreviousCoordinate(agentState, (agentState.getX() - 1) + "," + agentState.getY());
            Utils.updatePreviousDistanceFragment(agentState, "1");
        }
        else{
            String previous = agentState.getMemoryFragment("previous");
            Coordinate preCor = Utils.getCoordinateFromString(previous.substring(0, previous.indexOf(";")));
            int preDir = Utils.getDir(preCor.getX(), preCor.getY(), agentState.getX(), agentState.getY());
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
            dir = Utils.getClockwiseDirection(agentState, dir);
        }
        Utils.step(agentState, agentAction, agentState.getX() + Utils.moves.get(dir).getX(), agentState.getY() + Utils.moves.get(dir).getY());
        Utils.updateMemoryFragment(agentState);
    }
}
