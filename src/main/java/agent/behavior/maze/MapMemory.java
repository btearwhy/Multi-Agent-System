package agent.behavior.maze;/**
 * @author ：mmzs
 * @date ：Created in 2022/4/30 14:29
 * @description：
 * @modified By：
 * @version: $
 */

import environment.CellPerception;


import java.io.Serializable;
import java.util.*;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/4/30 14:29
 * @description：
 * @modified By：
 * @version: $
 */

public class MapMemory implements Serializable {
    int width = BORDER;
    int height = BORDER;

    Map<Cor, CellMemory> map;
    transient DstarLite dstarLite;
    static final int BORDER = 30;
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

    public List<Cor> getTrajectory(Cor start){
        return dstarLite.getTrajectory(start);
    }

    public boolean trajContainsAgent(Cor start){
        return dstarLite.trajContainsAgent(start);
    }

    public boolean trajContainsPacket(Cor start){
       return dstarLite.trajContainsPacket(start);
    }

    public boolean trajContainsObtacle(Cor start){
        return dstarLite.trajContainsObtacle(start);
    }

    public boolean discovered(Cor cor){
        return map.containsKey(cor);
    }

    public boolean allDiscovered(){
        return width != BORDER && height != BORDER && map.size() == width * height;
    }

    public boolean borderDiscovered(){
        return width != BORDER && height != BORDER;
    }

    public Cor getRandomUndiscoveredCor(){
        Cor res = null;
        Random ra = new Random();
        if(allDiscovered()){
            res = new Cor(ra.nextInt(width), ra.nextInt(height));
        }
        else if(borderDiscovered()){
            List<Cor> cors = new ArrayList<>();
            for (int i = 0; i < width; i++){
                for (int j = 0; j < height; j++){
                    Cor c = new Cor(i, j);
                    if(!map.containsKey(c)){
                        cors.add(c);
                    }
                }
            }
            Random r = new Random();
            res = cors.get(r.nextInt(cors.size()));
        }
        else{
            int x, y;
            int x_far = Collections.max(map.keySet(), new Comparator<Cor>(){
                    @Override
                    public int compare(Cor o1, Cor o2) {
                        return o1.getX() - o2.getX();
                    }
                }).getX();
                int y_far = Collections.max(map.keySet(), new Comparator<Cor>(){
                    @Override
                    public int compare(Cor o1, Cor o2) {
                        return o1.getY() - o2.getY();
                    }
                }).getY();
            do{
                x = ra.nextInt(Math.min(x_far * 2, width));
                y = ra.nextInt(Math.min(y_far * 2, height));
            }while(discovered(new Cor(x, y)));
            res = new Cor(x, y);
        }
        return res;
    }
    public CellMemory getFirstObstacle(Cor start){
        return map.get(dstarLite.getFirstObstacleCor(start));
    }

    public Cor getFirstObstacleCor(Cor start){
        return dstarLite.getFirstObstacleCor(start);
    }

    public List<CellMemory> getAllCellsMemory(){
        return new ArrayList<>(map.values());
    }

    public void updateBorder(List<CellPerception> neighbors, Cor cur){
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

    public void updateMapMemory(MapMemory mm){
        List<CellMemory> cells = new ArrayList<>(mm.getMap().values());
        cells.removeIf(c -> c.getAgentRepresentation().isPresent());
        Map<Cor, Obstacle> obstacles = new HashMap<>();
        if(mm.getWidth() < this.width){
            setWidth(mm.getWidth());
        }
        if(mm.getHeight() < this.height){
            setHeight(mm.getHeight());
        }
        for (CellMemory cell:cells){
            if(!map.containsKey(cell.getCoordinate())){
                map.put(cell.getCoordinate(), new CellMemory(cell, cell.getTime()));
                obstacles.put(cell.getCoordinate(), getObstacleFromCell(cell));
            }
            else if(map.get(cell.getCoordinate()).getTime() < cell.getTime()){
                map.put(cell.getCoordinate(), new CellMemory(cell, cell.getTime()));
                obstacles.put(cell.getCoordinate(), getObstacleFromCell(cell));
            }
        }
        updateDstar(obstacles);

    }
    public void updateMapMemory(List<CellPerception> cellPerceptions, int t){
        updateCells(cellPerceptions, t);
        updateDstar(extractObstacles(cellPerceptions));
    }

    public void updateDstar(Map<Cor, Obstacle> obstacles){
        dstarLite.recalculate(obstacles, this.width, this.height);
    }

    public void updateCells(List<CellPerception> cellPerceptions, int t){
        for (CellPerception cellPerception:cellPerceptions){
            Cor cor = new Cor(cellPerception.getX(), cellPerception.getY());
            map.put(cor, new CellMemory(cellPerception, t));
        }
    }
    public Map<Cor, Obstacle> extractObstacles(List<CellPerception> cellPerceptions){
        Map<Cor, Obstacle> obstacles = new HashMap<>();
        for (CellPerception cellPerception:cellPerceptions){
            Cor cor = new Cor(cellPerception.getX(), cellPerception.getY());
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

    public Map<Cor, CellMemory> getMap() {
        return map;
    }

    public Cor getNextMove(Cor start, Cor goal){
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

    public void setMap(Map<Cor, CellMemory> map) {
        this.map = map;
    }

}

