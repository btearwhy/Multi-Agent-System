package agent.dstarlite;

import environment.Coordinate;
import util.Pair;

import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;

public class VertexWithPriority implements Comparable<VertexWithPriority>, java.io.Serializable{
    public Coordinate coordinate;
    public PriorityKey key;


    public VertexWithPriority(int x, int y, double k1, double k2) {
        coordinate = new Coordinate(x, y);
        key = new PriorityKey(k1, k2);
    }

    public VertexWithPriority(int x, int y) {
        coordinate = new Coordinate(x, y);
        key = new PriorityKey(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public VertexWithPriority(Coordinate c) {
        coordinate = new Coordinate(c.getX(), c.getY());
        key = new PriorityKey(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public VertexWithPriority(Coordinate c, PriorityKey k) {
        coordinate = new Coordinate(c.getX(), c.getY());
        key = new PriorityKey(k.getFirst(), k.getSecond());
    }

    public VertexWithPriority(VertexWithPriority v) {
        coordinate = new Coordinate(v.coordinate.getX(), v.coordinate.getY());
        key = new PriorityKey(v.key.getFirst(), v.key.getSecond());
    }

    @Override
    public int compareTo(VertexWithPriority o) {
        return key.compareTo(o.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate.getX(), coordinate.getY());
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof VertexWithPriority casted))
            return false;

        if (casted.coordinate.getX() != coordinate.getX())
            return false;

        return casted.coordinate.getY() == coordinate.getY();
    }

    @Override
    public String toString() {
        return String.format("vertex at (%d,%d) with k1=%.1f and k2=%.1f",
                coordinate.getX(), coordinate.getY(), key.getFirst(), key.getSecond());
    }

    public static void main(String[] args) {
        VertexWithPriority v1 = new VertexWithPriority(0,0,15.,20.);
        VertexWithPriority v2 = new VertexWithPriority(0,0,11., 21.);
        VertexWithPriority v3 = new VertexWithPriority(0,0,11., 21.);
        PriorityQueue<VertexWithPriority> queue = new PriorityQueue<VertexWithPriority>();
        queue.clear();
        queue.add(v1);
        queue.add(v2);
        System.out.println();

        HashMap<VertexWithPriority, Double> map = new HashMap<>();
        map.put(v1, 0.);
        map.put(v2, 1.);
        System.out.println();

    }
}
