package agent.dstarlite;

import environment.Coordinate;

import java.util.*;

public class DStarLite implements java.io.Serializable{
    private PriorityQueueU queueU;
    private double k_m;
    private HashMap<Coordinate, Double> rhs;
    private HashMap<Coordinate, Double> g;
    private Coordinate s_start;
    private Coordinate s_last;
    private Coordinate s_goal;
    HashSet<Coordinate> old_obstacles;
    private HashSet<Coordinate> obstacles;
    private final Coordinate[] eight_neighbors;

    public DStarLite() {
        queueU = new PriorityQueueU();
        k_m = 0;
        rhs = new HashMap<Coordinate, Double>();
        g = new HashMap<Coordinate, Double>();
        obstacles = new HashSet<>();
        eight_neighbors = new Coordinate[]{
                new Coordinate(1,0),
                new Coordinate(1,1),
                new Coordinate(0,1),
                new Coordinate(-1,1),
                new Coordinate(-1,0),
                new Coordinate(-1,-1),
                new Coordinate(0,-1),
                new Coordinate(1,-1)};
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

    private double getRHS(Coordinate u) {
        if (!rhs.containsKey(u)) {
            rhs.put(u, Double.POSITIVE_INFINITY);
        }
        return rhs.get(u);
    }

    private double getG(Coordinate u) {
        if (!g.containsKey(u)) {
            g.put(u, Double.POSITIVE_INFINITY);
        }
        return g.get(u);
    }

    private ArrayList<Coordinate> getPred(Coordinate u) {
        ArrayList<Coordinate> pred = new ArrayList<>();
//        if (obstacles.contains(u)) {
//            return pred;
//        }
//        else {
//            for (Coordinate n:eight_neighbors) {
//                if (!obstacles.contains(u.add(n))) {
//                    pred.add(u.add(n));
//                }
//            }
//        }

        for (Coordinate n:eight_neighbors) {
            pred.add(u.add(n));
        }

        return pred;
    }

    private ArrayList<Coordinate> getSucc(Coordinate u) {
        ArrayList<Coordinate> succ = new ArrayList<>();
//        if (obstacles.contains(u)) {
//            return succ;
//        }
//        else {
//            for (Coordinate n:eight_neighbors) {
//                if (!obstacles.contains(u.add(n))) {
//                    succ.add(u.add(n));
//                }
//            }
//        }
        for (Coordinate n:eight_neighbors) {
            succ.add(u.add(n));
        }

        return succ;
    }

    /**
     * Cost of edge s1 -> s2
     * @param s1
     * @param s2
     * @return cost
     */
    private double cost(Coordinate s1, Coordinate s2) {
        int dx = Math.abs(s1.diff(s2).getX());
        int dy = Math.abs(s1.diff(s2).getY());

        assert (dx <= 1 && dy <= 1);

        if (obstacles.contains(s1) || obstacles.contains(s2)) {
            return Double.POSITIVE_INFINITY;
        }
        else if (dx == 0 && dy == 0) {
            return 0.;
        }
        else {
            return 1.;
        }
    }

    private double old_cost(Coordinate s1, Coordinate s2) {
        int dx = Math.abs(s1.diff(s2).getX());
        int dy = Math.abs(s1.diff(s2).getY());

        assert (dx <= 1 && dy <= 1);

        if (old_obstacles.contains(s1) || old_obstacles.contains(s2)) {
            return Double.POSITIVE_INFINITY;
        }
        else if (dx == 0 && dy == 0) {
            return 0.;
        }
        else {
            return 1.;
        }
    }

    private double heuristic(Coordinate s1, Coordinate s2) {
        // TODO
        Coordinate diff = s1.diff(s2);
        int h = Math.max(Math.abs(diff.getX()), Math.abs(diff.getY()));
        return (double) h;
    }

    public void init(int start_x, int start_y, int goal_x, int goal_y) {
        queueU.clear();
        k_m = 0;
        s_start = new Coordinate(start_x, start_y);
        s_last = new Coordinate(start_x, start_y);
        s_goal = new Coordinate(goal_x, goal_y);
        rhs.put(s_goal, 0.);
        queueU.insert(s_goal, calculateKey(s_goal));
        computeShortestPath();
    }

    private void updateVertex(Coordinate u) {
        boolean containU = queueU.contains(u);
        int compare = Double.compare(getG(u), getRHS(u));
        if (compare != 0 && containU) {
            queueU.update(u, calculateKey(u));
        }
        else if (compare != 0 && !containU) {
            queueU.insert(u, calculateKey(u));
        }
        else if (compare == 0 && containU) {
            queueU.remove(u);
        }
    }

    private void computeShortestPath() {
        while (!queueU.isEmpty() &&
                (queueU.topKey().compareTo(calculateKey(s_start)) < 0 || getRHS(s_start) > getG(s_start))) {
            Coordinate u = queueU.top(); // {11"} u=U.Top()
            PriorityKey k_old = queueU.topKey();// {12"} k_old = U.TopKey()
            PriorityKey k_new = calculateKey(u); // {13"}
            if (k_old.compareTo(k_new) < 0) { // {14"}
                queueU.update(u, k_new); // {15"} U.Update()
            }
            else if (getG(u) > getRHS(u)) { // {16"}
                g.put(u, getRHS(u));
                queueU.remove(u);
                for (Coordinate s:getPred(u)) {
                    if (!s.equals(s_goal)) {
                        rhs.put(s, Math.min(getRHS(s), cost(s,u) + getG(u)));
                    }
                    updateVertex(s);
                }
            }
            else {
                double g_old = getG(u);
                g.put(u, Double.POSITIVE_INFINITY);
                ArrayList<Coordinate> list = getSucc(u);
                list.add(u);
                for (Coordinate s:list) {
                    if (Double.compare(getRHS(s), (cost(s, u) + g_old)) == 0) {
                        if (!s.equals(s_goal)) {
                            // get min_{s_prime} cost(s, s_prime) + getG(s_prime)
                            double min_val = Double.POSITIVE_INFINITY;
                            for (Coordinate s_prime:getSucc(s)) {
                                double val = cost(s, s_prime) + getG(s_prime);
                                if (val < min_val) {
                                    min_val = val;
                                }
                            }
                            rhs.put(s, min_val);
                        }
                    }
                    updateVertex(s);
                }
            }
        }
        System.out.println();
    }

    /**
     * Get the absolute coordinate of the next move. {34"}
     * @param current current position
     * @return next position to move to
     */
    public Coordinate getNextMove(Coordinate current) {
        double min_val = Double.POSITIVE_INFINITY;
        Coordinate min_move = new Coordinate(0,0);

        if (current.equals(s_goal)) {
            return min_move;
        }

        if (!(getG(s_start) < Double.POSITIVE_INFINITY)) {
            System.out.println("There is no known path to goal.");
            return min_move;
        }

        // argmin
        for (Coordinate s_prime:getSucc(current)) {
            double val = cost(current, s_prime) + getG(s_prime);
            if (val < min_val) {
                min_val = val;
                min_move = s_prime;
            }
        }
        return new Coordinate(min_move.getX(), min_move.getY());
    }

    /**
     * Update s_start if the agent moves successfully to a new coordinate. {34"} {35"}
     * @param s_start the new coordinate
     */
    public void updateStart(Coordinate s_start) {
        this.s_start = s_start;
    }

    /**
     * compare the stored obstacles with the observed map
     * to see if any obstacles are added or removed. {36"}
     * @param observed_map the observed map with key-value pair (Coordinate-hasObstacle).
     * @return the obstacle is changed or not.
     */
    private boolean edgeCostChanged(HashMap<Coordinate, Boolean> observed_map) {
        boolean changed = false;
        for (Coordinate s:observed_map.keySet()) {
            if (observed_map.get(s) && !obstacles.contains(s)) {
                changed = true;
                break;
            }
            else if (!observed_map.get(s) && obstacles.contains(s)) {
                changed = true;
                break;
            }
        }

        return changed;
    }

    /**
     * {38"}-{39"}
     */
    private void updateLast() {
        k_m = k_m + heuristic(s_last, s_start);
        s_last = s_start;
    }

    private void updateEdgeHelper(Coordinate u, Coordinate v) {
        if (Double.compare(old_cost(u, v), cost(u, v)) > 0) {
            if (!u.equals(s_goal)) {
                rhs.put(u, Math.min(getRHS(u), cost(u, v) + getG(v)));
            }
        }
        else if (Double.compare(getRHS(u), old_cost(u, v) + getG(v)) == 0) {
            if (!u.equals(s_goal)) {
                // get min_{s_prime} cost(u, s_prime) + getG(s_prime)
                double min_val = Double.POSITIVE_INFINITY;
                for (Coordinate s_prime:getSucc(u)) {
                    double val = cost(u, s_prime) + getG(s_prime);
                    if (val < min_val) {
                        min_val = val;
                    }
                }
                rhs.put(u, min_val);
            }
        }
    }

    /**
     * {40"}-{17"}
     * @param observed_map the observed map with key-value pair (Coordinate-hasObstacle).
     */
    private void updateEdge(HashMap<Coordinate, Boolean> observed_map) {
        // store old_obstacles
        old_obstacles = new HashSet<>(obstacles);
        // get new obstacles
        for (Coordinate u:observed_map.keySet()) {
            if (observed_map.get(u) && !obstacles.contains(u)) {
                obstacles.add(u);
            }
            else if (!observed_map.get(u) && obstacles.contains(u)) {
                obstacles.remove(u);
            }
            else break;

            for (Coordinate n:eight_neighbors) {
                Coordinate v = u.add(n);
                updateEdgeHelper(u, v);
                updateVertex(u);
                updateEdgeHelper(v, u);
                updateVertex(v);
            }
        }
    }

    /**
     *
     * @param observed_map the observed map with key-value pair (Coordinate-hasObstacle).
     */
    public void run(HashMap<Coordinate, Boolean> observed_map) {
        if (edgeCostChanged(observed_map)) {
            updateLast();
            updateEdge(observed_map);
            computeShortestPath();
        }
    }

    public static void main(String[] args) {
        DStarLite dStarLite = new DStarLite();
        dStarLite.init(1,1,3,3);
        ArrayList<Coordinate> obstacles = new ArrayList<>();
        dStarLite.computeShortestPath();

        System.out.println(Double.compare(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

}
