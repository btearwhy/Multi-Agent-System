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
import environment.Representation;
import environment.world.destination.Destination;
import environment.world.destination.DestinationRep;
import environment.world.packet.Packet;
import environment.world.packet.PacketRep;
import environment.world.wall.WallRep;
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
    Map<Coordinate, CellMemory> map;


    PriorityQueue<CellMemory> priorityQueue = new PriorityQueue<>();
    CellMemory sLast;
    CellMemory sStart;
    CellMemory sGoal;
    int km;

    public MapMemory(){
        map = new HashMap<>();
    }

    public List<CellMemory> getAllCellsMemory(){
        return new ArrayList<>(map.values());
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
        List<Boolean> walkable = new ArrayList<>();
        for(CellPerception c:cellPerceptions){
            Coordinate cor = new Coordinate(c.getX(), c.getY());
            if(!map.containsKey(cor)) map.put(cor, new CellMemory(cor.getX(), cor.getY()));
             walkable.add(map.get(cor).isWalkable());
        }
        updateMapMemory(cellPerceptions);
        for(int i = 0; i < cellPerceptions.size(); i++){
            CellPerception c = cellPerceptions.get(i);
            Coordinate cor = new Coordinate(c.getX(), c.getY());
            if(walkable.get(i) && !c.isWalkable() && !c.containsAgent()){
                List<CellMemory> neighbors = getNeighbors(map.get(cor));
                for (CellMemory n:neighbors){
                    if(n.getRhs() == 1 + map.get(cor).getG()){
                        map.get(cor).setRhs(CellMemory.MAXVALUE);
                        map.get(cor).setG(CellMemory.MAXVALUE);
                        if(!n.equals(sGoal)){
                            n.setRhs(getSmallestRhs(n));
                        }
                    }
                }
            }
            else if(!walkable.get(i) && (c.isWalkable() || c.containsAgent())){
                List<CellMemory> neighbors = getNeighborsAndSelf(map.get(cor));
                for (CellMemory n:neighbors){
                    if(!n.equals(sGoal)){
                        n.setRhs(getSmallestRhs(n));
                    }
                }
            }
        }
    }

    public boolean computeShortestPath(){
        sLast = sStart;
        initialize();
        compute();
//        int i = 0;
//        List<CellMemory> cells = getAllCellsMemory();
//        Collections.sort(cells, new Comparator<CellMemory>() {
//            @Override
//            public int compare(CellMemory o1, CellMemory o2) {
//                int o = o1.getY() - o2.getY();
//                if(o != 0) return o;
//                return o1.getX() - o2.getX();
//            }
//        });
//        for (CellMemory c:cells){
//            System.out.print((c.getRhs() == CellMemory.MAXVALUE ? -1 : c.getRhs()) + "|");
//            System.out.print((c.getG() == CellMemory.MAXVALUE ? -1 : c.getG()) + "\t");
//            if(i++ % 12 == 11) {
//                System.out.print('\n');
//            }
//        }

        if(sStart.getG() == CellMemory.MAXVALUE) return false;
        return true;
    }


    public int getDirection(Coordinate cur, Coordinate goal){
        CellMemory cell = getSmallestGCell(cur);
        if(sGoal != null && !goal.equals(sGoal.getCoordinate()) || cell == null){
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
        //System.out.println("sStart " + sStart.getX() + " |" + sStart.getY());
        //System.out.println("sGoal " + sGoal.getX() + " |" + sGoal.getY());
        while(!priorityQueue.isEmpty() && priorityQueue.peek().getKey().compareTo(calculateKey(sStart)) < 0 || sStart.getRhs() > sStart.getG()){
            CellMemory u = priorityQueue.peek();
            System.out.println("检查" + u.getX()  + " | " + u.getY());
            System.out.println("之前start:" + sStart.getX() + "|" + sStart.getY() + "\t:" + sStart.getRhs() + "｜" + sStart.getG());
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
            for (CellMemory cs : priorityQueue){
                System.out.print(cs.getCoordinate().toString() + cs.getKey());
                System.out.print("\t");
            }

        }
        if(!priorityQueue.isEmpty())
            sStart.setG(sStart.getRhs());
    }

    List<CellMemory> getNeighbors(CellPerception cellMemory){
        List<CellMemory> res = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            Coordinate des = new Coordinate(cellMemory.getX() + Utils.moves.get(i).getX(), cellMemory.getY() + Utils.moves.get(i).getY());
            if(!map.containsKey(des)){
                res.add(new CellMemory(des.getX(), des.getY()));
            }
            else if(!map.get(des).isBorder() && map.get(des).isWalkable()){
                res.add(map.get(des));
            }
        }
        return res;
    }

    List<CellMemory> getNeighborsAndSelf(CellPerception cellMemory){
        List<CellMemory> res = getNeighbors(cellMemory);
        Coordinate cor = new Coordinate(cellMemory.getX(), cellMemory.getY());
        if(!map.containsKey(cor)) map.put(cor, new CellMemory(cellMemory));
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
        boolean exception1 = c1.equals(sGoal) || c1.equals(sStart);
        boolean exception2 = c2.equals(sGoal) || c2.equals(sStart);
        if(exception1 || exception2 || c1.isWalkable() && c2.isWalkable()) return 1;
        else return CellMemory.MAXVALUE;
    }

    public Map<Coordinate, CellMemory> getMap() {
        return map;
    }

}
