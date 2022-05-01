package agent.behavior.part2;/**
 * @author ：mmzs
 * @date ：Created in 2022/4/30 14:29
 * @description：
 * @modified By：
 * @version: $
 */

import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import org.checkerframework.checker.units.qual.C;

import java.util.*;

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
    Map<Coordinate, CellMemory> map;
    PriorityQueue<CellMemory> priorityQueue = new PriorityQueue<>();
    CellMemory sLast;
    CellMemory sStart;
    CellMemory sGoal;
    int km;

    public MapMemory(int row, int col){
        this.row = row;
        this.col = col;
        map = new HashMap<>();
    }

    public void updateMapMemory(List<CellPerception> cellPerceptions){
        for (CellPerception cellPerception:cellPerceptions){
            Coordinate cor = new Coordinate(cellPerception.getX(), cellPerception.getY());
            int g = CellMemory.MAXVALUE;
            int rhs = CellMemory.MAXVALUE;
            if(map.containsKey(cor)){
                g = map.get(cor).getG();
                rhs = map.get(cor).getRhs();
            }
            if(cellPerception instanceof CellMemory)
                map.put(cor, (CellMemory) cellPerception);
            else map.put(cor, new CellMemory(cellPerception));
            map.get(cor).setRhs(rhs);
            map.get(cor).setG(g);
        }

    }

    public void recalculate(Coordinate start, List<CellPerception> cellPerceptions){
        sStart = map.get(start);
        km += heuristic(sLast, sStart);
        sLast = sStart;
        List<CellMemory> changesToObstacles = new ArrayList<>();
        List<CellMemory> changesToFree = new ArrayList<>();
        for(CellPerception c:cellPerceptions){
            Coordinate cor = new Coordinate(c.getX(), c.getY());
            if(map.containsKey(cor) && !map.get(cor).isWalkable() && (c.isWalkable() || c.containsAgent())){
                //障碍消失
                for (CellMemory u:getNeighborsAndSelf(c)){
                    for (CellMemory v : getNeighbors(u)){

                    }
                }
            }
        }
    }

    public boolean computeShortestPath(){
        sLast = sStart;
        initialize();
        compute();
        if(sStart.getG() == CellMemory.MAXVALUE) return false;
        return true;
    }


    public int getDirection(Coordinate cur, Coordinate goal){
        CellMemory cell = getSmallestGCell(cur);
        if(cell == null){
            sStart = map.get(cur);
            sGoal = map.get(goal);
            if(!computeShortestPath()){
                return -1;
            }
            else{
                cell = getSmallestGCell(cur);
            }
        }
        return Utils.getDir(cur.getX(), cur.getY(), cell.getX(), cell.getY());
    }

    CellMemory getSmallestGCell(Coordinate cur){
        CellMemory res = null;
        for(int i = 0; i < 8; i++){
            Coordinate des = new Coordinate(cur.getX() + Utils.moves.get(i).getX(), cur.getY() + Utils.moves.get(i).getY());
            if(map.containsKey(des) && map.get(des).getG() != CellMemory.MAXVALUE){
                if(res == null || res.getG() > map.get(des).getG()){
                    res = map.get(des);
                }
            }
        }
        return res;
    }

    int getSmallestRhs(CellMemory s){
        List<CellMemory> ns = getNeighbors(s);
        int res = CellMemory.MAXVALUE;
        for (CellMemory n : ns){
            int tmp = cost(s, n) + n.getG();
            if(tmp < res){
                res = tmp;
            }
        }
        return res;
    }

    void compute(){
        while(priorityQueue.peek().getKey().compareTo(sStart.getKey()) < 0 || sStart.getRhs() > sStart.getG()){
            CellMemory u = priorityQueue.peek();
            Key kOld = u.getKey();
            Key kNew = calculateKey(u);
            if(kOld.compareTo(kNew) < 0){
                priorityQueue.remove(u);
                u.setKey(kNew);
                priorityQueue.add(u);
            }
            else if(u.getG() > u.getRhs()){
                u.setG(u.getRhs());
                priorityQueue.remove(u);
                for(CellMemory s : getNeighbors(u)){
                    if(!s.equals(sGoal)) s.setRhs(Math.min(s.getRhs(), cost(s, u) + u.getG()));
                    updateVertex(s);
                }
            }
            else{
                int gOld = u.getG();
                u.setG(CellMemory.MAXVALUE);
                for (CellMemory n:getNeighborsAndSelf(u)){
                    if(n.getRhs() == cost(n, u) + gOld && n != sGoal){
                        n.setRhs(getSmallestRhs(n));
                    }
                    updateVertex(n);
                }
            }
        }
    }

    List<CellMemory> getNeighbors(CellPerception cellMemory){
        List<CellMemory> res = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            Coordinate des = new Coordinate(cellMemory.getX() + Utils.moves.get(i).getX(), cellMemory.getY() + Utils.moves.get(i).getY());
            if(!map.containsKey(des)){
                res.add(new CellMemory(des.getX(), des.getY()));
            }
            else if(!map.get(des).isBorder()){
                res.add(map.get(des));
            }
        }
        return res;
    }

    List<CellMemory> getNeighborsAndSelf(CellPerception cellMemory){
        List<CellMemory> res = getNeighbors(cellMemory);
        Coordinate cor = new Coordinate(cellMemory.getX(), cellMemory.getY());
        res.add(map.get(cor));
        return res;
    }

    void initialize(){
        priorityQueue.clear();
        km = 0;
        for (Map.Entry<Coordinate, CellMemory> c : map.entrySet()){
            c.getValue().setG(CellMemory.MAXVALUE);
            c.getValue().setRhs(CellMemory.MAXVALUE);
        }
        sGoal.setRhs(0);
        sGoal.setKey(calculateKey(sGoal));
        priorityQueue.add(sGoal);
    }

    void updateVertex(CellMemory cellMemory){
        if(cellMemory.getG() != cellMemory.getRhs() && priorityQueue.contains(cellMemory)){
            priorityQueue.remove(cellMemory);
            cellMemory.setKey(calculateKey(cellMemory));
            priorityQueue.add(cellMemory);
        }
        else if(cellMemory.getG() != cellMemory.getRhs() && !priorityQueue.contains(cellMemory)){
            cellMemory.setKey(calculateKey(cellMemory));
            priorityQueue.add(cellMemory);
        }
        else if(cellMemory.getG() == cellMemory.getRhs() && priorityQueue.contains(cellMemory)){
            priorityQueue.remove(cellMemory);
        }
    }

    Key calculateKey(CellMemory cellMemory){
        int tmp = Math.min(cellMemory.getG(), cellMemory.getRhs());
        return new Key(tmp + heuristic(sStart ,cellMemory), tmp);
    }

    int heuristic(CellMemory c1, CellMemory c2){
        return Perception.distance(c1.getX(), c1.getY(), c2.getX(), c2.getY());
    }

    int cost(CellMemory c1, CellMemory c2){
        if(c1.isWalkable() && c2.isWalkable()) return 1;
        else return CellMemory.MAXVALUE;
    }

}
