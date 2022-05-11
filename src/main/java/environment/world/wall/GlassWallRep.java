package environment.world.wall;

import java.io.Serializable;

public class GlassWallRep extends WallRep implements Serializable {

    protected GlassWallRep(int x, int y) {
        super(x, y);
    }

    @Override
    public char getTypeChar() {
        return 'G';
    }

    @Override
    public boolean isWalkable() {
        return false;
    }
    
    @Override
    public boolean isSeeThrough() {
        return true;
    }
}
