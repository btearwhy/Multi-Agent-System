package agent.behavior.maze;/**
 * @author ：mmzs
 * @date ：Created in 2022/4/30 16:06
 * @description：
 * @modified By：
 * @version: $
 */

import environment.CellPerception;
import environment.Representation;

import java.io.Serializable;
import java.util.List;


public class CellMemory extends CellPerception implements Serializable {

    int time = 0;

    public CellMemory(CellPerception cellPerception, int time){
        super(cellPerception.getX(), cellPerception.getY());
        List<Representation> reps = cellPerception.getReps();
        for (Representation rep:reps){
            this.addRep(rep);
        }
        this.time = time;
    }



    public Cor getCoordinate(){
        return new Cor(this.getX(), this.getY());
    }

    public CellMemory(int x, int y, int time){
        super(x, y);
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object c){
        return ((CellMemory)c).getX() == this.getX() && ((CellMemory)c).getY() == this.getY();
    }

}

