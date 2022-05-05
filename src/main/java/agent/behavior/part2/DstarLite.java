package agent.behavior.part2;/**
 * @author ：mmzs
 * @date ：Created in 2022/5/3 12:25
 * @description：
 * @modified By：
 * @version: $
 */

import environment.Coordinate;
import util.Pair;

import java.util.*;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/5/3 12:25
 * @description：
 * @modified By：
 * @version: $
 */

public class DstarLite {
    PriorityQueueU<VertexWithPriority> priorityQueue;
    Coordinate last = null;
    Coordinate start = null;
    Coordinate goal = null;
    int km;
    Map<Coordinate, Boolean> obstacles;
    Map<Coordinate, Integer> rhsMap;
    Map<Coordinate, Integer> gMap;
    int width = Integer.MAX_VALUE;
    int height = Integer.MAX_VALUE;



    public DstarLite(){
        priorityQueue = new PriorityQueueU<>();
        obstacles = new HashMap<>();
        rhsMap = new HashMap<>();
        gMap = new HashMap<>();
    }

    public void startOver(Coordinate start, Coordinate goal){
        initialize(start, goal);
        computeShortestPath();
    }
    public Coordinate getNextMove(Coordinate start, Coordinate goal){
        if(!goal.equals(this.goal)){
            startOver(start, goal);
        }

        int g = Integer.MAX_VALUE;
        Coordinate cor = new Coordinate(-1, -1);

        List<Coordinate> neighbors = getNeighbors(start);
        for (Coordinate neighbor:neighbors){
            if(getG(neighbor) < g){
                g = getG(neighbor);
                cor = neighbor;
                this.start = neighbor;
            }
        }
        return cor;
    }

    boolean checkObstacleChanges(Map<Coordinate, Boolean> obstacles){
        for (Map.Entry<Coordinate, Boolean> o:obstacles.entrySet()){
            if(this.obstacles.getOrDefault(o.getKey(), false) != o.getValue())
                return true;
        }
        return false;
    }

    boolean checkBorderChanges(int width, int height){
        return checkWidthChange(width) && checkHeightChange(height);
    }

    boolean checkWidthChange(int width){
        if(this.width != width){
            Coordinate c = Collections.max(rhsMap.keySet(), new Comparator<Coordinate>() {
                @Override
                public int compare(Coordinate o1, Coordinate o2) {
                    return o1.getY() - o2.getY();
                }
            });
            for (int h = 0; h <= c.getY(); h++){
                if(getRhs(new Coordinate(width, h)) != Integer.MAX_VALUE){
                    return true;
                }
            }
        }
        return false;

    }

    boolean checkHeightChange(int height){
        if(this.height != height){
            Coordinate c = Collections.max(rhsMap.keySet(), new Comparator<Coordinate>() {
                @Override
                public int compare(Coordinate o1, Coordinate o2) {
                    return o1.getX() - o2.getX();
                }
            });
            for (int w = 0; w <= c.getX(); w++){
                if(getRhs(new Coordinate(w, height)) != Integer.MAX_VALUE){
                    return true;
                }
            }
        }
        return false;
    }

    void updateWidthVertex(){
        Coordinate c = Collections.max(rhsMap.keySet(), new Comparator<Coordinate>() {
            @Override
            public int compare(Coordinate o1, Coordinate o2) {
                return o1.getY() - o2.getY();
            }
        });
        for (int h = 0; h <= c.getY(); h++){
            Coordinate cor = new Coordinate(width - 1, h);
            Coordinate h1 = new Coordinate(width, h - 1);
            Coordinate h2 = new Coordinate(width, h);
            Coordinate h3 = new Coordinate(width, h + 1);
            int rhs = getRhs(cor);
            if(rhs != Integer.MAX_VALUE && (rhs == 1 + getG(h1) || rhs == 1 + getG(h2)|| rhs == 1 + getG(h3))){
                rhsMap.put(cor, getSmallestRhs(cor));
                updateVertex(cor);
            }
        }
    }

    void updateHeightVertex(){
        Coordinate c = Collections.max(rhsMap.keySet(), new Comparator<Coordinate>() {
            @Override
            public int compare(Coordinate o1, Coordinate o2) {
                return o1.getX() - o2.getX();
            }
        });
        for (int w = 0; w <= c.getX(); w++){
            Coordinate cor = new Coordinate(w, height - 1);
            Coordinate w1 = new Coordinate(w - 1, height);
            Coordinate w2 = new Coordinate(w, height);
            Coordinate w3 = new Coordinate(w + 1, height);
            int rhs = getRhs(cor);
            if(rhs != Integer.MAX_VALUE && (rhs == 1 + getG(w1) || rhs == 1 + getG(w2)|| rhs == 1 + getG(w3))){
                rhsMap.put(cor, getSmallestRhs(cor));
                updateVertex(cor);
            }
        }
    }


    public void clearGoal(){
        goal = null;
    }

    public void recalculate(Map<Coordinate, Boolean> obstacles, int width, int height){
        if(goal == null){
            for (Map.Entry<Coordinate, Boolean> entry:obstacles.entrySet()){
                if(entry.getValue() && !this.obstacles.getOrDefault(entry.getKey(), false)){
                    this.obstacles.put(entry.getKey(), true);
                }
                if(!entry.getValue() && this.obstacles.containsKey(entry.getKey())){
                    this.obstacles.remove(entry.getKey());
                }
            }
            this.width = width;
            this.height = height;
        }
        else{
            if(checkWidthChange(width) || checkHeightChange(height) || checkObstacleChanges(obstacles)){
                km += heuristic(last, start);
                last = start;
                if(checkWidthChange(width)){
                    this.width = width;
                    updateWidthVertex();
                }
                if(checkHeightChange(height)){
                    this.height = height;
                    updateHeightVertex();
                }
                for (Map.Entry<Coordinate, Boolean> o:obstacles.entrySet()){
                    //障碍消失
                    if(!o.getValue() && this.obstacles.getOrDefault(o.getKey(), false)){
                        rhsMap.put(o.getKey(), getSmallestRhs(o.getKey()));
                        this.obstacles.remove(o.getKey());
                        updateVertex(o.getKey());

                    }
                    else if(o.getValue() && !this.obstacles.getOrDefault(o.getKey(), false)){

                        priorityQueue.remove(o.getKey());
                        for (Coordinate u:getNeighbors(o.getKey())){
                            if(add(1, getG(o.getKey())) == getRhs(u)){
                                this.obstacles.put(o.getKey(), true);
                                if(!u.equals(goal)) {
                                    rhsMap.put(u, getSmallestRhs(u));
                                }
                                updateVertex(u);
                            }
                        }

                        this.obstacles.put(o.getKey(), true);
                    }
                }
                computeShortestPath();
            }
        }

    }


    public List<Coordinate> getNeighbors(Coordinate c){
        List<Coordinate> neighbors = new ArrayList<>();
        for (Coordinate dir:Utils.moves){
            Coordinate des = c.add(dir);
            if(validCell(des))
                neighbors.add(des);
        }
        return neighbors;
    }

    public boolean validCell(Coordinate u){
        return inside(u) && !obstacles.getOrDefault(u, false);
    }

    public boolean inside(Coordinate u){
        return u.getX() < width && u.getX() >= 0 && u.getY() < height && u.getY() >= 0;
    }


    public List<Coordinate> getNeighborsAndSelf(Coordinate c){
        List<Coordinate> neighbors = getNeighbors(c);
        if(validCell(c)) neighbors.add(c);
        return neighbors;
    }

    int add(int a, int b){
        if(a == Integer.MAX_VALUE || b==Integer.MAX_VALUE) return Integer.MAX_VALUE;
        else return a+b;
    }

    void computeShortestPath(){
        while(!priorityQueue.isEmpty() && (priorityQueue.peek().getPriorityKey().compareTo(calculateKey(start)) < 0  || getG(start) < getRhs(start))){

            Coordinate u = priorityQueue.peek().getCoordinate();
            PriorityKey kOld = priorityQueue.peek().getPriorityKey();
            PriorityKey kNew = calculateKey(u);
            if(kOld.compareTo(kNew) < 0){
                priorityQueue.remove(u);
                priorityQueue.add(new VertexWithPriority(u, kNew));
            }
            else if(getG(u) > getRhs(u)){
                gMap.put(u, getRhs(u));
                priorityQueue.remove(u);
                for (Coordinate s:getNeighbors(u)){
                    if(!s.equals(goal)) rhsMap.put(s, Math.min(getRhs(s), add(cost(s, u) , getG(u))));
                    updateVertex(s);
                }
            }
            else{
                int gOld = getG(u);
                gMap.remove(u);
                for (Coordinate s:getNeighborsAndSelf(u)){
                    if(add(cost(s, u) , gOld) == getRhs(s)){
                        if(!s.equals(goal)){
                            int minRhs = getSmallestRhs(s);
                            if(minRhs == Integer.MAX_VALUE){
                                rhsMap.remove(s);
                            }
                            else{
                                rhsMap.put(s, minRhs);
                            }
                        }
                    }
                    updateVertex(s);
                }
            }
        }

    }

    public int getSmallestRhs(Coordinate u){
        int rhs = Integer.MAX_VALUE;
        for (Coordinate neighbor:getNeighbors(u)){
            if(add(cost(u, neighbor), getG(neighbor))< rhs){
                rhs = add(cost(u, neighbor), getG(neighbor));
            }
        }
        return rhs;
    }

    int cost(Coordinate c, Coordinate u){
        return 1;
//        if((validCell(c) || c.equals(goal)) && (validCell(u) || u.equals(goal))) return 1;
//        else return Integer.MAX_VALUE;
    }

    void initialize(Coordinate start, Coordinate goal){
        this.start = start;
        this.last = this.start;
        this.goal = goal;
        priorityQueue.clear();
        km = 0;
        gMap.clear();
        rhsMap.clear();
        rhsMap.put(this.goal, 0);
        priorityQueue.add(new VertexWithPriority(this.goal, new PriorityKey(heuristic(this.start, this.goal), 0)));
    }

    public int getG(Coordinate u){
        return gMap.getOrDefault(u, Integer.MAX_VALUE);
    }

    public int getRhs(Coordinate u){
        return rhsMap.getOrDefault(u, Integer.MAX_VALUE);
    }

    int heuristic(Coordinate u, Coordinate v){
        return Math.max(Math.abs(u.getX() - v.getX()), Math.abs(u.getY() - v.getY()));
    }


    PriorityKey calculateKey(Coordinate u){
        int m = Math.min(getG(u), getRhs(u));
        return new PriorityKey(add(add(m , heuristic(this.start, u)), km), m);
    }

    //结构可改
    void updateVertex(Coordinate u){
        if(getRhs(u) != getG(u) && priorityQueue.contains(u)){
            priorityQueue.remove(u);
            priorityQueue.add(new VertexWithPriority(u, calculateKey(u)));
        }
        else if(getRhs(u) != getG(u) && !priorityQueue.contains(u)) {
            priorityQueue.add(new VertexWithPriority(u, calculateKey(u)));
        }
        else if(getRhs(u) == getG(u) && priorityQueue.contains(u)) priorityQueue.remove(u);
    }


}


class PriorityKey implements Comparable<PriorityKey>{
    public Pair<Integer, Integer> key;


    public PriorityKey(int key1, int key2){
        this.key = new Pair<>(key1, key2);
    }

    public Pair<Integer, Integer> getKey() {
        return key;
    }

    public void setKey(Pair<Integer, Integer> key) {
        this.key = key;
    }


    @Override
    public int compareTo(PriorityKey k){
        if(key.first == k.getKey().first) return key.second - k.getKey().second;
        else return key.first - k.getKey().first;
    }

    @Override
    public String toString(){
        return key.first + "," + key.second;
    }

}

class VertexWithPriority implements Comparable<VertexWithPriority>{
    private Coordinate coordinate;
    private PriorityKey key;

    public VertexWithPriority(Coordinate coordinate){
        this.coordinate = coordinate;
    }

    public VertexWithPriority(Coordinate coordinate, PriorityKey priorityKey){
        this.coordinate = coordinate;
        this.key = priorityKey;
    }

    public VertexWithPriority(Coordinate coordinate, int key1, int key2){
        this.coordinate = coordinate;
        this.key = new PriorityKey(key1, key2);
    }

    @Override
    public boolean equals(Object o){
        return o instanceof VertexWithPriority && coordinate.equals(((VertexWithPriority) o).getCoordinate());
    }

    @Override
    public int compareTo(VertexWithPriority p){
        return this.key.compareTo(p.getPriorityKey());
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public PriorityKey getPriorityKey() {
        return key;
    }

    public void setPriorityKey(PriorityKey priorityKey) {
        this.key = priorityKey;
    }
}

class PriorityQueueU<T> extends PriorityQueue<T>{
    @Override
    public boolean contains(Object u){
        for (Object p:this){
            if(((VertexWithPriority)p).getCoordinate().equals(u)) return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o){
        return o instanceof Coordinate && super.remove(new VertexWithPriority((Coordinate)o));
    }
}
//
//class PriorityQueueU {
//    public PriorityQueue<VertexWithPriority> priorityQueueU;
//
//    public PriorityQueueU() {
//        priorityQueueU = new PriorityQueue<VertexWithPriority>();
//    }
//
//    public boolean isEmpty() {
//        return priorityQueueU.isEmpty();
//    }
//
//    public boolean contains(Coordinate coordinate) {
//        VertexWithPriority v = new VertexWithPriority(coordinate);
//        return priorityQueueU.contains(v);
//    }
//
//    public void clear() {
//        priorityQueueU.clear();
//    }
//
//    public Coordinate top() {
//        if (!priorityQueueU.isEmpty()) {
//            VertexWithPriority v = priorityQueueU.peek();
//            return new Coordinate(v.coordinate.getX(), v.coordinate.getY());
//        }
//        else return null;
//    }
//
//    public PriorityKey topKey() {
//        if (!priorityQueueU.isEmpty()) {
//            VertexWithPriority v = priorityQueueU.peek();
//            return new PriorityKey(v.key.key.first, v.key.key.second);
//        }
//        else return new PriorityKey(Integer.MAX_VALUE, Integer.MAX_VALUE);
//    }
//
//    public Coordinate pop() {
//        if (!priorityQueueU.isEmpty()) {
//            VertexWithPriority v = priorityQueueU.poll();
//            return new Coordinate(v.coordinate.getX(), v.coordinate.getY());
//        }
//        else return null;
//    }
//
//    public void insert(Coordinate coordinate, PriorityKey keys) {
//        priorityQueueU.add(new VertexWithPriority(coordinate, keys));
//    }
//
//    public void update(Coordinate coordinate, PriorityKey keys) {
//        VertexWithPriority v = new VertexWithPriority(coordinate, keys);
//        while (priorityQueueU.contains(v)) {
//            priorityQueueU.remove(v);
//        }
//        priorityQueueU.add(v);
//    }
//
//    public void remove(Coordinate coordinate) {
//        VertexWithPriority v = new VertexWithPriority(coordinate);
//        while (priorityQueueU.contains(v)) {
//            priorityQueueU.remove(v);
//        }
//    }
//
//}