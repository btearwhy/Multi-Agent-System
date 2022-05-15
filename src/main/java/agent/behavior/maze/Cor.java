package agent.behavior.maze;/**
 * @author ：mmzs
 * @date ：Created in 2022/5/9 17:29
 * @description：
 * @modified By：
 * @version: $
 */

import environment.Coordinate;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/5/9 17:29
 * @description：
 * @modified By：
 * @version: $
 */

public class Cor implements Serializable {
    private int x;
    private int y;

    public Cor(int x, int y){
        this.x = x;
        this.y = y;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return String.format("%d,%d", this.getX(), this.getY());
    }

    public Cor diff(Cor other) {
        return new Cor(x - other.getX(), y - other.getY());
    }

    public Cor add(Cor other) {
        return new Cor(x + other.getX(), y + other.getY());
    }

    public Cor add(Coordinate other) { return new Cor(x + other.getX(), y + other.getY()); }
    public boolean any(Predicate<Integer> pred) {
        return pred.test(x) || pred.test(y);
    }

    public boolean all(Predicate<Integer> pred) {
        return pred.test(x) && pred.test(y);
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Cor){
            return x == ((Cor) o).getX() && y == ((Cor) o).getY();
        }
        else if(o instanceof Coordinate){
            return x == ((Coordinate) o).getX() && y == ((Coordinate) o).getY();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    /**
     * Returns a new Cor containing the sign of this (-1, 0 or 1)
     */
}