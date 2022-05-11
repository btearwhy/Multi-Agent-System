package environment.world.wall;

import java.io.Serializable;

/**
 * A class for representations of walls.
 */
public class SolidWallRep extends WallRep implements Serializable {

    /**
     * Initializes a new ObscureWallRep instance
     *
     * @param x  X-coordinate of the Wall this representation represents
     * @param y  Y-coordinate of the Wall this representation represents
     */
    protected SolidWallRep(int x, int y) {
        super(x, y);
    }

    public char getTypeChar() {
        return ('W');
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    @Override
    public boolean isSeeThrough() {
        return false;
    }
}
