package environment.world.gradient;

import environment.Item;
import gui.video.Drawer;

import java.util.Collection;

public class Gradient extends Item<GradientRep> {

    private final int value;
    

    /**
     * Initializes a new Gradient
     *
     * @param x X-coordinate of the Item
     * @param y Y-coordinate of the Item
     * @param value The value of the gradient
     */
    public Gradient(int x, int y, int value) {
        super(x, y);
        this.value = value;
    }


    /**
     * Get the gradient's value.
     */
    public int getValue() {
        return value;
    }

    /**
     * Get the representation of this Gradient.
     */
    @Override
    public GradientRep getRepresentation() {
        return new GradientRep(this.getX(), this.getY(), this.getValue());
    }

    @Override
    public void draw(Drawer drawer) {
        drawer.drawGradient(this);
    }

    public boolean isEqual (Gradient g){
        if (this.getX() == g.getX() && this.getY() == g.getY() && this.value == g.getValue()){
            return true;
        }
        return  false;
    }

    public static boolean containGradient(Collection<Gradient> gradients, Gradient target){
        for (Gradient g : gradients){
            if (g.isEqual(target)){
                return true;
            }
        }
        return false;
    }
}
