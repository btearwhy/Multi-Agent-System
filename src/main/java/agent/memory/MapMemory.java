package agent.memory;/**
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
    int width = Integer.MAX_VALUE;
    int height = Integer.MAX_VALUE;

    Map<Coordinate, CellMemory> map;

    public MapMemory(){
        map = new HashMap<>();
    }

    public List<CellMemory> getAllCellsMemory(){
        return new ArrayList<>(map.values());
    }

    public void updateBorder(List<CellPerception> cellPerceptions, Coordinate cur, int width, int height){
        CellPerception w = Collections.max(cellPerceptions, new Comparator<CellPerception>() {
            @Override
            public int compare(CellPerception o1, CellPerception o2) {
                return o1.getX() - o2.getX();
            }
        });

        CellPerception h = Collections.max(cellPerceptions, new Comparator<CellPerception>() {
            @Override
            public int compare(CellPerception o1, CellPerception o2) {
                return o1.getY() - o2.getY();
            }
        });


        List<CellPerception> widths = new ArrayList<>();
        List<CellPerception> heights = new ArrayList<>();
        if(w.getX() < cur.getX() + width / 2){
            for (CellPerception c:cellPerceptions){
                if(c.getX() == w.getX()){
                    widths.add(c);
                }
            }
            int i = 0;
            for (CellPerception cw:widths){
                if(!cw.containsWall()) i++;
            }
            if(i == widths.size()){
                setWidth(w.getX() + 1);
            }

        }

        if(h.getY() < cur.getY() + height / 2){
            for (CellPerception c:cellPerceptions){
                if(c.getY() == h.getY()){
                    heights.add(c);
                }
            }
            int i = 0;
            for (CellPerception ch:heights){
                if(!ch.containsWall()) i++;
            }
            if(i == heights.size()){
                setHeight(h.getY() + 1);
            }
        }
    }

    public void updateMapMemory(List<CellPerception> cellPerceptions, Coordinate cur, int width, int height){
        updateBorder(cellPerceptions, cur, width, height);
        Map<Coordinate, Boolean> obstacles = new HashMap<>();
        for (CellPerception cellPerception:cellPerceptions){
            Coordinate cor = new Coordinate(cellPerception.getX(), cellPerception.getY());
            map.put(cor, new CellMemory(cellPerception));

            if(!cellPerception.isWalkable() && !(cellPerception.getX() == cur.getX() && cellPerception.getY() == cur.getY())/*&& !cellPerception.containsAgent()*/){
                obstacles.put(cor, true);
            }
            else{
                obstacles.put(cor, false);
            }
        }
    }

    public Map<Coordinate, CellMemory> getMap() {
        return map;
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
