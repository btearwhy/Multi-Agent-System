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

public class CellMemory extends CellPerception implements Comparable<CellMemory> {
    public static final int MAXVALUE = Integer.MAX_VALUE / 2 - 1;
    private int rhs = MAXVALUE;
    private int g = MAXVALUE;


    private Key key;

    private boolean border = false;

    public CellMemory(int x, int y, int rhs, int g) {
        super(x, y);
        this.rhs = rhs;
        this.g = g;
    }

    public CellMemory(CellPerception cellPerception){
        super(cellPerception.getX(), cellPerception.getY());
        List<Representation> reps = cellPerception.getReps();
        reps.removeIf(e->e instanceof AgentRep);
        for (Representation rep:reps){
            this.addRep(rep);
        }

    }

    public Coordinate getCoordinate(){
        return new Coordinate(this.getX(), this.getY());
    }

    public CellMemory(int x, int y, boolean border) {
        super(x, y);
        this.border = border;
    }


    public void setRhs(int rhs) {
        this.rhs = rhs;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getG(){
        return g;
    }

    public int getRhs(){
        return rhs;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public boolean isBorder() {
        return border;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }


    public CellMemory(int x, int y){
        super(x, y);
    }


    @Override
    public int compareTo(CellMemory c){
        return this.getKey().compareTo(c.getKey());
    }

    @Override
    public boolean equals(Object c){
        return ((CellMemory)c).getX() == this.getX() && ((CellMemory)c).getY() == this.getY();
    }

}

class Key implements Comparable<Key>{
    private int key1;
    private int key2;

    public Key(int key1, int key2){
        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public int compareTo(Key k){
        int res = this.getKey1() - k.getKey1();
        return res == 0? this.getKey2() - k.getKey2() : res;
    }

    public int getKey1() {
        return key1;
    }

    public void setKey1(int key1) {
        this.key1 = key1;
    }

    public int getKey2() {
        return key2;
    }

    public void setKey2(int key2) {
        this.key2 = key2;
    }


    @Override
    public String toString(){
        return key1 + ";" + key2;
    }

}

