package agent.dstarlite;

import java.util.Objects;
import java.util.PriorityQueue;

public class Vertex implements Comparable<Vertex>{
    public int x;
    public int y;
    public double k1;
    public double k2;

    private final double EPS = 0.00001;

    public Vertex(int x, int y, double k1, double k2) {
        this.x = x;
        this.y = y;
        this.k1 = k1;
        this.k2 = k2;
    }

    @Override
    public int compareTo(Vertex o) {
        // keys are compared according to lexicographic ordering
        if (k1-EPS > o.k1) return 1;
        else if (k1 < o.k1-EPS) return -1;
        else if (k2 > o.k2) return 1;
        else if (k2 < o.k2) return -1;
        else return 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof Vertex casted))
            return false;

        if (casted.x != x)
            return false;

        return casted.y == y;
    }

    @Override
    public String toString() {
        return String.format("vertex at (%d,%d) with k1=%.1f and k2=%.1f", x, y, k1, k2);
    }

    public static void main(String[] args) {
        Vertex v1 = new Vertex(0,0,15.,20.);
        Vertex v2 = new Vertex(0,1,11., 21.);
        PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>();
        queue.clear();
        queue.add(v1);
        queue.add(v2);
        System.out.println(queue.peek());

    }
}
