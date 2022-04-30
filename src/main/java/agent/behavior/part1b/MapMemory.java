package agent.behavior.part1b;/**
 * @author ：mmzs
 * @date ：Created in 2022/4/30 14:29
 * @description：
 * @modified By：
 * @version: $
 */

import environment.CellPerception;
import environment.Coordinate;

import java.util.PriorityQueue;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/4/30 14:29
 * @description：
 * @modified By：
 * @version: $
 */

public class MapMemory {
    int row;
    int col;
    CellPerception[][] cellPerceptions;
    int[][] rhs;
    int[][] g;
    PriorityQueue<CellPerception> priorityQueue;
    CellPerception sLast;

    void computeShortestPath(Coordinate cur, Coordinate goal){

    }

    void compute(){

    }

    void initialize(){

    }

    void updateVertex(CellPerception cellPerception){

    }

    void calculateKey(CellPerception cellPerception){

    }

}
