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


import environment.Coordinate;

import java.util.PriorityQueue;

public class PriorityQueueU {
    private PriorityQueue<VertexWithPriority> priorityQueueU;

    public PriorityQueueU() {
        priorityQueueU = new PriorityQueue<VertexWithPriority>();
    }

    public boolean isEmpty() {
        return priorityQueueU.isEmpty();
    }

    public boolean contains(Coordinate coordinate) {
        VertexWithPriority v = new VertexWithPriority(coordinate);
        return priorityQueueU.contains(v);
    }

    public void clear() {
        priorityQueueU.clear();
    }

    public Coordinate top() {
        if (!priorityQueueU.isEmpty()) {
            VertexWithPriority v = priorityQueueU.peek();
            return new Coordinate(v.coordinate.getX(), v.coordinate.getY());
        }
        else return null;
    }

    public PriorityKey topKey() {
        if (!priorityQueueU.isEmpty()) {
            VertexWithPriority v = priorityQueueU.peek();
            return new PriorityKey(v.key.getFirst(), v.key.getSecond());
        }
        else return new PriorityKey(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public Coordinate pop() {
        if (!priorityQueueU.isEmpty()) {
            VertexWithPriority v = priorityQueueU.poll();
            return new Coordinate(v.coordinate.getX(), v.coordinate.getY());
        }
        else return null;
    }

    public void insert(Coordinate coordinate, PriorityKey keys) {
        priorityQueueU.add(new VertexWithPriority(coordinate, keys));
    }

    public void update(Coordinate coordinate, PriorityKey keys) {
        VertexWithPriority v = new VertexWithPriority(coordinate, keys);
        while (priorityQueueU.contains(v)) {
            priorityQueueU.remove(v);
        }
        priorityQueueU.add(v);
    }

    public void remove(Coordinate coordinate) {
        VertexWithPriority v = new VertexWithPriority(coordinate);
        while (priorityQueueU.contains(v)) {
            priorityQueueU.remove(v);
        }
    }

}
