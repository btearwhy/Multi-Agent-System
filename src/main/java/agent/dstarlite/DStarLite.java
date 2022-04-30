package agent.dstarlite;

import environment.Coordinate;
import util.Pair;

import java.util.HashMap;
import java.util.PriorityQueue;

public class DStarLite implements java.io.Serializable{
    private PriorityQueueU queueU;
    private double k_m;
    private HashMap<Coordinate, Double> rhs;
    private HashMap<Coordinate, Double> g;
    private Coordinate s_start;
    private Coordinate s_last;
    private Coordinate s_goal;

    public DStarLite() {
        queueU = new PriorityQueueU();
        k_m = 0;
        rhs = new HashMap<Coordinate, Double>();
        g = new HashMap<Coordinate, Double>();
    }

    /**
     * Calculate k1 and k2 for priority queue ordering
     * @param s the vertex needs to be calculated
     * @return a key value pair k1 and k2
     */
    private PriorityKey calculateKey(Coordinate s) {
        double k1 = Math.min(getG(s), getRHS(s)) + heuristic(s_start, s) + k_m;
        double k2 = Math.min(getG(s), getRHS(s));
        return new PriorityKey(k1, k2);
    }

    private int compareKey(Pair<Double, Double> k1, Pair<Double, Double> k2) {

    }

    private double getRHS(Coordinate s) {
        if (!rhs.containsKey(s)) {
            rhs.put(s, Double.POSITIVE_INFINITY);
        }
        return rhs.get(s);
    }

    private double getG(Coordinate s) {
        if (!g.containsKey(s)) {
            g.put(s, Double.POSITIVE_INFINITY);
        }
        return g.get(s);
    }

    private double heuristic(Coordinate s1, Coordinate s2) {
        // TODO
        return 0.;
    }

    public void init(int start_x, int start_y, int goal_x, int goal_y) {
        queueU.clear();
        k_m = 0;
        s_start = new Coordinate(start_x, start_y);
        s_goal = new Coordinate(goal_x, goal_y);
        rhs.put(s_goal, 0.);
        queueU.insert(s_goal, calculateKey(s_goal));
    }

    public void computeShortestPath() {
        while (!queueU.isEmpty() &&
                (queueU.topKey().compareTo(calculateKey(s_start)) < 0 || getRHS(s_start) > getG(s_start))) {
            Coordinate u = queueU.top(); // {11"} u=U.Top()
            PriorityKey k_old = queueU.topKey();// {12"} k_old = U.TopKey()
            VertexWithPriority s_new = calculateKey(s_old); // {13"}
            if (s_old.compareTo(s_new) < 0) { // {14"}
                // {15"} U.Update()
                while (queueU.contains(s_old)) {
                    queueU.remove(s_old);
                }
                queueU.add(s_new);
            }
            else if (getG(s_old) > getRHS(s_old)) { // {16"}
                g.put(s_old, )
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        DStarLite dStarLite = new DStarLite();
        dStarLite.init(0,0,1,1);
        dStarLite.computeShortestPath();
    }

}
