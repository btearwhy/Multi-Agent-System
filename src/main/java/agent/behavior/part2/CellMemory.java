package agent.behavior.part2;/**
 * @author ：mmzs
 * @date ：Created in 2022/4/30 16:06
 * @description：
 * @modified By：
 * @version: $
 */

import environment.CellPerception;
import environment.Coordinate;
import environment.Representation;
import environment.world.agent.AgentRep;
import util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/4/30 16:06
 * @description：
 * @modified By：
 * @version: $
 */

public class CellMemory extends CellPerception{




    public CellMemory(CellPerception cellPerception){
        super(cellPerception.getX(), cellPerception.getY());
        List<Representation> reps = cellPerception.getReps();
        //reps.removeIf(e->e instanceof AgentRep);
        for (Representation rep:reps){
            this.addRep(rep);
        }

    }

    public Coordinate getCoordinate(){
        return new Coordinate(this.getX(), this.getY());
    }

    public CellMemory(int x, int y){
        super(x, y);
    }


    @Override
    public boolean equals(Object c){
        return ((CellMemory)c).getX() == this.getX() && ((CellMemory)c).getY() == this.getY();
    }

}

