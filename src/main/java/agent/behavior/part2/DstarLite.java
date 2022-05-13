package agent.behavior.part2;/**
 * @author ：mmzs
 * @date ：Created in 2022/5/3 12:25
 * @description：
 * @modified By：
 * @version: $
 */

import util.Pair;

import java.util.*;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/5/3 12:25
 * @description：
 * @modified By：
 * @version: $
 */

public class DstarLite{
    PriorityQueueU<VertexWithPriority> priorityQueue;
    Cor last = null;
    Cor start = null;
    Cor goal = null;
    int km;
    Map<Cor, Obstacle> obstacles;
    Map<Cor, Integer> rhsMap;
    Map<Cor, Integer> gMap;
    int width = BORDER;
    int height = BORDER;

    static final int MAX_LOOP = Integer.MAX_VALUE;
    static final int BORDER = 30;
    public DstarLite(){
        priorityQueue = new PriorityQueueU<>();
        obstacles = new HashMap<>();
        rhsMap = new HashMap<>();
        gMap = new HashMap<>();
    }

    public List<Cor> getTrajectory(Cor start){
        List<Cor> trajectory = new ArrayList<>();
        Cor r = start;
        while(!this.goal.equals(r) && r.getX() != -1 && r.getY() != -1){
            r = getSmallestGCor(r);
            trajectory.add(r);
        }
        if(r.getX() == -1 && r.getY() == -1){
            return new ArrayList<>();
        }
        return trajectory;
    }

    public boolean trajContainsObtacle(Cor start){
//        int rhs = getRhs(start);
//        return rhs > Obstacle.AGENT.getCost() && rhs != Integer.MAX_VALUE;
        int g = getSmallestG(start);
        return g >= Obstacle.AGENT.getCost() && g != Integer.MAX_VALUE;
    }

    public boolean trajContainsAgent(Cor start){
        int g = getSmallestG(start);
        return g < Obstacle.PACKET.getCost() && g >= Obstacle.AGENT.getCost();
    }

    public boolean trajContainsPacket(Cor start){
        int g = getSmallestG(start);
        return g >= Obstacle.PACKET.getCost() && g != Integer.MAX_VALUE;
    }

    public Cor getFirstObstacleCor(Cor start){
        List<Cor> traj = getTrajectory(start);
        if(traj != null){
            for (Cor cor:traj){
                if(obstacles.getOrDefault(cor, Obstacle.NULL) != Obstacle.NULL){
                    return cor;
                }
            }
        }
        return null;
    }
    public int getSmallestG(Cor start){
        int g = Integer.MAX_VALUE;
        List<Cor> neighbors = getValidNeighbors(start);
        for (Cor neighbor:neighbors){
            if(getG(neighbor) < g){
                g = getG(neighbor);
            }
        }
        return g;
    }
    public Cor getSmallestGCor(Cor start){
        int g = Integer.MAX_VALUE;
        Cor cor = new Cor(-1, -1);
        List<Cor> neighbors = getValidNeighbors(start);
        for (Cor neighbor:neighbors){
            if(getG(neighbor) < g){
                g = getG(neighbor);
                cor = neighbor;
            }
        }
        return cor;
    }

    public void startOver(Cor start, Cor goal){
        initialize(start, goal);
        computeShortestPath();
    }
    public Cor getNextMove(Cor start, Cor goal){
        if(goal.getX() >= width || goal.getY() >= height){
            return new Cor(-1, -1);
        }
        if(!goal.equals(this.goal)){
            startOver(start, goal);
        }

        Cor cor = getSmallestGCor(start);
        if(cor.getX() != -1 && cor.getY() != -1){
            this.start = cor;
        }
        return cor;
    }

    boolean checkObstacleChanges(Map<Cor, Obstacle> obstacles){
        for (Map.Entry<Cor, Obstacle> o:obstacles.entrySet()){
            if(this.obstacles.getOrDefault(o.getKey(), Obstacle.NULL) != o.getValue())
                return true;
        }
        return false;
    }


    public void clearGoal(){
        goal = null;
    }

    public Map<Pair<Cor, Cor>, Integer> getChangedEdgeOldCost(Map<Cor, Obstacle> obstacles){
        Map<Pair<Cor, Cor>, Integer> res = new HashMap<>();
        for(Map.Entry<Cor, Obstacle> entry: obstacles.entrySet()){
            if(this.obstacles.getOrDefault(entry.getKey(), Obstacle.NULL) != entry.getValue()){
                for (Cor n:getNeighbors(entry.getKey())){
                    res.put(new Pair<>(entry.getKey(), n), cost(entry.getKey(), n));
                    res.put(new Pair<>(n, entry.getKey()), cost(n, entry.getKey()));
                }
            }
        }
        return res;
    }

    public List<Cor> getNeighbors(Cor c){
        List<Cor> neighbors = new ArrayList<>();
        for (Cor dir:Utils.moves){
            Cor des = c.add(dir);
            neighbors.add(des);
        }
        return neighbors;
    }

    public void updateObstacles(Map<Cor, Obstacle> obstacles){
        for (Map.Entry<Cor, Obstacle> entry:obstacles.entrySet()){
            if(entry.getValue() != Obstacle.NULL){
                this.obstacles.put(entry.getKey(), entry.getValue());
            }
            else{
                this.obstacles.remove(entry.getKey());
            }
        }
    }

    public void recalculate(Map<Cor, Obstacle> obstacles, int width, int height){
        if(goal == null){
            updateObstacles(obstacles);
            this.width = width;
            this.height = height;
        }
        else{
            if(this.width != width || this.height != height || checkObstacleChanges(obstacles)){
                km += heuristic(last, start);
                last = start;
                if(this.width != width){
                    int ymax = Collections.max(rhsMap.keySet(), new Comparator<Cor>() {
                        @Override
                        public int compare(Cor o1, Cor o2) {
                            return o1.getY() - o2.getY();
                        }
                    }).getY();
                    for (int i = 0; i <= ymax + 1; i++){
                        obstacles.put(new Cor(width, i), Obstacle.FIXED);
                    }
                }

                if(this.height != height){
                    int xmax = Collections.max(rhsMap.keySet(), new Comparator<Cor>() {
                        @Override
                        public int compare(Cor o1, Cor o2) {
                            return o1.getX() - o2.getX();
                        }
                    }).getX();
                    for (int i = 0; i <= xmax + 1; i++){
                        obstacles.put(new Cor(i, height), Obstacle.FIXED);
                    }
                }
                Map<Pair<Cor, Cor>, Integer> oldEdges = getChangedEdgeOldCost(obstacles);
                updateObstacles(obstacles);
                this.width = width;
                this.height = height;

                for (Map.Entry<Pair<Cor, Cor>, Integer> entry:oldEdges.entrySet()){
                    Cor u = entry.getKey().first;
                    Cor v = entry.getKey().second;
                    if(entry.getValue() > cost(u, v)){
                        if(!u.equals(goal)) rhsMap.put(u, Math.min(getRhs(u), add(cost(u, v), getG(v))));
                    }
                    else if(getRhs(u) == add(entry.getValue(), getG(v))){
                        if(!u.equals(goal)) rhsMap.put(u, getSmallestRhs(u));
                    }
                    updateVertex(u);
                }
                computeShortestPath();
            }
        }

    }


    public List<Cor> getValidNeighbors(Cor c){
        List<Cor> neighbors = new ArrayList<>();
        for (Cor dir:Utils.moves){
            Cor des = c.add(dir);
            if(validCell(des) || des.equals(this.goal))
                neighbors.add(des);
        }
        return neighbors;
    }

    public boolean validCell(Cor u){
        return inside(u) && obstacles.getOrDefault(u, Obstacle.NULL) != Obstacle.FIXED;
    }

    public boolean inside(Cor u){
        return u.getX() < width && u.getX() >= 0 && u.getY() < height && u.getY() >= 0;
    }


    public List<Cor> getValidNeighborsAndSelf(Cor c){
        List<Cor> neighbors = getValidNeighbors(c);
        if(validCell(c)) neighbors.add(c);
        return neighbors;
    }

    int add(int a, int b){
        if(a == Integer.MAX_VALUE || b==Integer.MAX_VALUE) return Integer.MAX_VALUE;
        else return a+b;
    }

    void computeShortestPath(){
        int i = 0;
        while(++i < MAX_LOOP && !priorityQueue.isEmpty() && (priorityQueue.peek().getPriorityKey().compareTo(calculateKey(start)) < 0  || getG(start) < getRhs(start))){
//            System.out.print(i);
            Cor u = priorityQueue.peek().getCoordinate();
            PriorityKey kOld = priorityQueue.peek().getPriorityKey();
            PriorityKey kNew = calculateKey(u);
            if(kOld.compareTo(kNew) < 0){
                priorityQueue.remove(u);
                priorityQueue.add(new VertexWithPriority(u, kNew));
            }
            else if(getG(u) > getRhs(u)){
                gMap.put(u, getRhs(u));
                priorityQueue.remove(u);
                for (Cor s: getValidNeighbors(u)){
                    if(!s.equals(goal)) rhsMap.put(s, Math.min(getRhs(s), add(cost(s, u) , getG(u))));
                    updateVertex(s);
                }
            }
            else{
                int gOld = getG(u);
                gMap.remove(u);
                for (Cor s: getValidNeighborsAndSelf(u)){
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
        //System.out.println();
    }

    public int getSmallestRhs(Cor u){
        int rhs = Integer.MAX_VALUE;
        for (Cor neighbor: getValidNeighbors(u)){
            if(add(cost(u, neighbor), getG(neighbor))< rhs){
                rhs = add(cost(u, neighbor), getG(neighbor));
            }
        }
        return rhs;
    }

    int cost(Cor c, Cor u){
//        if(u.equals(this.goal)){
//            return 1;
//        }
//        else if(validCell(u)){
//            return obstacles.getOrDefault(u, Obstacle.NULL).getCost();
//        }
//        else return Integer.MAX_VALUE;

        int costC = obstacles.getOrDefault(c, Obstacle.NULL).getCost();
        int costU = obstacles.getOrDefault(u, Obstacle.NULL).getCost();
        if(c.equals(this.start)){
            return add(1,  costU);
        }
        if(u.equals(this.goal)){
            return add(1 , costC);
        }
        if(validCell(c) && validCell(u)){
            return add(costC ,costU);
        }
        else return Integer.MAX_VALUE;
    }

    void initialize(Cor start, Cor goal){
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

    public int getG(Cor u){
        return gMap.getOrDefault(u, Integer.MAX_VALUE);
    }

    public int getRhs(Cor u){
        return rhsMap.getOrDefault(u, Integer.MAX_VALUE);
    }

    int heuristic(Cor u, Cor v){
        return 2 * Math.max(Math.abs(u.getX() - v.getX()), Math.abs(u.getY() - v.getY()));
    }


    PriorityKey calculateKey(Cor u){
        int m = Math.min(getG(u), getRhs(u));
        return new PriorityKey(add(add(m , heuristic(this.start, u)), km), m);
    }


    void updateVertex(Cor u){
        if(getRhs(u) != getG(u) && priorityQueue.contains(u)){
            priorityQueue.remove(u);
            priorityQueue.add(new VertexWithPriority(u, calculateKey(u)));
        }
        else if(getRhs(u) != getG(u) && !priorityQueue.contains(u)) {
            priorityQueue.add(new VertexWithPriority(u, calculateKey(u)));
        }
        else if(getRhs(u) == getG(u) && priorityQueue.contains(u)) priorityQueue.remove(u);
    }

    public Map<Cor, Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(Map<Cor, Obstacle> obstacles) {
        this.obstacles = obstacles;
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
    private Cor coordinate;
    private PriorityKey key;

    public VertexWithPriority(Cor coordinate){
        this.coordinate = coordinate;
    }

    public VertexWithPriority(Cor coordinate, PriorityKey priorityKey){
        this.coordinate = coordinate;
        this.key = priorityKey;
    }

    public VertexWithPriority(Cor coordinate, int key1, int key2){
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

    public Cor getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Cor coordinate) {
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
        return o instanceof Cor && super.remove(new VertexWithPriority((Cor)o));
    }
}

enum Obstacle{
    FIXED{
        public int getCost(){
            return Integer.MAX_VALUE;
        }
    },
    AGENT{
        public int getCost(){
            return 100;
        }
    },
    PACKET{
        public int getCost(){
            return 10000;
        }
    },
    NULL{
        public int getCost(){
            return 1;
        }
    };

    public abstract int getCost();

}