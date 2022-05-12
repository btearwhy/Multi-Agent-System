package agent.behavior.part3.dstarlite;/**
 * @author ：mmzs
 * @date ：Created in 2022/5/3 22:15
 * @description：
 * @modified By：
 * @version: $
 */

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/5/3 22:15
 * @description：
 * @modified By：
 * @version: $
 */

import util.Pair;

public class PriorityKey implements Comparable<PriorityKey>, java.io.Serializable{
    private Pair<Double, Double> keys;

    private final double EPS = 0.00001;

    public PriorityKey(double k1, double k2) {
        keys = new Pair<>(k1, k2);
    }

    public PriorityKey(PriorityKey k) {
        keys = new Pair<>(k.getFirst(), k.getSecond());
    }

    @Override
    public int compareTo(PriorityKey o) {
        // keys are compared according to lexicographic ordering
        double k1 = keys.first;
        double k2 = keys.second;
        double k1o = o.keys.first;
        double k2o = o.keys.second;
        if (k1-EPS > k1o) return 1;
        else if (k1 < k1o-EPS) return -1;
        else if (k2 > k2o) return 1;
        else if (k2 < k2o) return -1;
        else return 0;
    }

    public double getFirst() {
        return keys.first;
    }

    public double getSecond() {
        return keys.second;
    }
}
