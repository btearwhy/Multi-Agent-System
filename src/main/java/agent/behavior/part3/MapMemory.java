package agent.behavior.part3;/**
 * @author ：mmzs
 * @date ：Created in 2022/4/30 14:29
 * @description：
 * @modified By：
 * @version: $
 */

import environment.CellPerception;
import environment.Coordinate;

import java.util.*;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/4/30 14:29
 * @description：
 * @modified By：
 * @version: $
 */

public class MapMemory {
    int width = 30;
    int height = 30;

    Map<Coordinate, CellMemory> map;
    DstarLite dstarLite;

    public DstarLite getDstarLite() {
        return dstarLite;
    }

    public void setDstarLite(DstarLite dstarLite) {
        this.dstarLite = dstarLite;
    }

    public MapMemory(){
        map = new HashMap<>();
        dstarLite = new DstarLite();
    }

    public List<Coordinate> getTrajectory(Coordinate start){
        return dstarLite.getTrajectory(start);
    }

    public boolean trajContainsAgent(Coordinate start){
        return dstarLite.trajContainsAgent(start);
    }

    public boolean trajContainsPacket(Coordinate start){
       return dstarLite.trajContainsPacket(start);
    }

    public boolean trajContainsObtacle(Coordinate start){
        return dstarLite.trajContainsObtacle(start);
    }
    public List<CellMemory> getAllCellsMemory(){
        return new ArrayList<>(map.values());
    }

    public void updateBorder(List<CellPerception> neighbors, Coordinate cur){
        int ymax = Collections.max(neighbors, new Comparator<CellPerception>(){
            @Override
            public int compare(CellPerception c1, CellPerception c2){
                return c1.getY() - c2.getY();
            }
        }).getY();
        int xmax = Collections.max(neighbors, new Comparator<CellPerception>(){
            @Override
            public int compare(CellPerception c1, CellPerception c2){
                return c1.getX() - c2.getX();
            }
        }).getX();

        if(xmax == cur.getX()) this.width = xmax + 1;
        if(ymax == cur.getY()) this.height = ymax + 1;
    }

    public void updateMapMemory(List<CellPerception> cellPerceptions){
        updateCells(cellPerceptions);
        updateDstar(extractObstacles(cellPerceptions));
    }

    public void updateDstar(Map<Coordinate, Obstacle> obstacles){
        dstarLite.recalculate(obstacles, this.width, this.height);
    }

    public void updateCells(List<CellPerception> cellPerceptions){
        for (CellPerception cellPerception:cellPerceptions){
            Coordinate cor = new Coordinate(cellPerception.getX(), cellPerception.getY());
            map.put(cor, new CellMemory(cellPerception));
        }
    }
    public Map<Coordinate, Obstacle> extractObstacles(List<CellPerception> cellPerceptions){

        Map<Coordinate, Obstacle> obstacles = new HashMap<>();

        for (CellPerception cellPerception:cellPerceptions){
            Coordinate cor = new Coordinate(cellPerception.getX(), cellPerception.getY());
            obstacles.put(cor, getObstacleFromCell(cellPerception));
        }


        return obstacles;
    }
    public Obstacle getObstacleFromCell(CellPerception cellPerception){
        if(!cellPerception.isWalkable()){
            if (cellPerception.containsAgent())
                return Obstacle.AGENT;
            else if(cellPerception.containsPacket() && !cellPerception.containsGenerator())
                return Obstacle.PACKET;
            else
                return Obstacle.FIXED;
        }
        else{
            return Obstacle.NULL;
        }
    }

    public void clearGoal(){
        dstarLite.clearGoal();
    }

    public Map<Coordinate, CellMemory> getMap() {
        return map;
    }

    public Coordinate getNextMove(Coordinate start, Coordinate goal){
        return dstarLite.getNextMove(start, goal);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


}
