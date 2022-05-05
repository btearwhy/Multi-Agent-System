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
import agent.behavior.part2.CellMemory;
import agent.behavior.part2.DstarLite;
import agent.behavior.part2.MapMemory;
import agent.behavior.part2.Utils;
import environment.*;
import environment.world.agent.AgentRep;
import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;
import environment.world.wall.SolidWallRep;
import environment.world.wall.WallRep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        // No communication

        agentState.updateMapMemory();

    }
    public static void maptest(AgentState agentState){
        System.out.println(agentState.getName());
        Map<Coordinate, CellMemory> map = agentState.getMapMemory().getMap();
        DstarLite d = agentState.getMapMemory().getDstarLite();
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                String s = "";
                if(agentState.getX() == j && agentState.getY() == i){
                    s = "$";
                }
                Coordinate corr = new Coordinate(j, i);
                if(map.containsKey(corr)){
                    List<Representation> r = map.get(corr).getReps();
                    if(r.size() == 0)
                        s = "无" + s;
                    else if(r.get(0) instanceof PacketRep)
                        s = "包" + s;
                    else if(r.get(0) instanceof DestinationRep)
                        s = "目" + s;
                    else if(r.get(0) instanceof WallRep)
                        s = "墙" + s;
                    else if(r.get(0) instanceof AgentRep)
                        s = "人" + s;
                    else s = r.toString();
                    s = (d.getRhs(corr) == Integer.MAX_VALUE ? "X" : d.getRhs(corr)) + "|"
                            + (d.getG(corr) == Integer.MAX_VALUE ? "X" : d.getG(corr)) + "|"
                            + s;
                    System.out.print(String.format("%-10s", s));
                }
                else{
                    s = (d.getRhs(corr) == Integer.MAX_VALUE ? "X" : d.getRhs(corr)) + "|"
                            + (d.getG(corr) == Integer.MAX_VALUE ? "X" : d.getG(corr)) + "|"
                            + "未" +s;
                    System.out.print(String.format("%-10s", s));
                }
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {


        Coordinate cur = new Coordinate(agentState.getX(), agentState.getY());
        Coordinate goal = Utils.getCoordinateFromGoal(agentState);
        Coordinate next = agentState.getMapMemory().getNextMove(cur, goal);
        //Coordinate next = agentState.getDstarLite().getNextMove(cur, goal);

        if(next.equals(new Coordinate(-1, -1))) {
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
            System.out.println("之前");
            maptest(agentState);
            CellPerception m = agentState.getPerception().getCellPerceptionOnAbsPos(next.getX(), next.getY());
            if(m != null && m.isWalkable())
                agentAction.step(next.getX(), next.getY());
            else agentAction.skip();
            System.out.println("之后");
            maptest(agentState);
            System.out.println();
        }
    }

    private void collideHandle(AgentState agentState, AgentAction agentAction, int intendedDir){
        int x = agentState.getX();
        int y = agentState.getY();
        if(agentState.getPerception().getCellPerceptionOnAbsPos(x + Utils.moves.get(intendedDir).getX(), y + Utils.moves.get(intendedDir).getY()) != null &&
                agentState.getPerception().getCellPerceptionOnAbsPos(x + Utils.moves.get(intendedDir).getX(), y + Utils.moves.get(intendedDir).getY()).containsAgent()) {
            Random ra = new Random();
            if (ra.nextInt(2) < 1) {
                agentAction.skip();
                return;
            }
        }
        intendedDir = Utils.getClockwiseDirectionIfBlocked(agentState, intendedDir);
        agentAction.step(x + Utils.moves.get(intendedDir).getX(), y + Utils.moves.get(intendedDir).getY());
    }
}
