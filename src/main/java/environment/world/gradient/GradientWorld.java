package environment.world.gradient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.eventbus.EventBus;

import environment.Coordinate;
import environment.World;
import environment.world.destination.Destination;
import environment.world.energystation.EnergyStation;
import environment.world.generator.PacketGenerator;
import environment.world.wall.Wall;

public class GradientWorld extends World<Gradient> {

    /**
     * Initialize the GradientWorld
     */
    public GradientWorld(EventBus eventBus) {
        super(eventBus);
    }


    /**
     * Place a collection of gradients inside the Gradient World.
     *
     * @param gradients The collection of gradients.
     */
    @Override
    public void placeItems(Collection<Gradient> gradients) {
        gradients.forEach(this::placeItem);
    }

    /**
     * Place a single gradient in the Gradient World.
     *
     * @param item The gradient.
     */
    @Override
    public void placeItem(Gradient item) {
        putItem(item);
    }

    public void addGradientsWithStartLocation(int x, int y)
    {

        List<Gradient> gradients = new ArrayList<>();

        // add charging place
        int centerX = x;
        int centerY = y-1;
        this.placeItem(new Gradient(centerX,centerY,0));

        List<Coordinate> no_sign_place = new ArrayList<>();
        List<EnergyStation> energy_station_list = getEnvironment().getEnergyStationWorld().getItemsFlat();
        for (EnergyStation e : energy_station_list){
            no_sign_place.add(new Coordinate(e.getX(),e.getY()));
        }
        List<Wall> wall_list = getEnvironment().getWallWorld().getItemsFlat();
        for (Wall w : wall_list){
            no_sign_place.add(new Coordinate(w.getX(),w.getY()));
        }
        List<Destination> destinations_list = getEnvironment().getDestinationWorld().getItemsFlat();
        for (Destination d : destinations_list){
            no_sign_place.add(new Coordinate(d.getX(),d.getY()));
        }
        List<PacketGenerator> generators_list = getEnvironment().getPacketGeneratorWorld().getItemsFlat();
        for (PacketGenerator g : generators_list){
            no_sign_place.add(new Coordinate(g.getX(),g.getY()));
        }

        List<Gradient> layer = new ArrayList<>();
        layer.add(new Gradient(centerX,centerY,0));
        int gradient_value = 1;

        while (!layer.isEmpty()){


            List<Gradient> next_layer = new ArrayList<>();


            for (Gradient g : layer){

                for (int i = -1; i <= 1; i++){
                    for (int j = -1; j <= 1; j++){
                        int g_x = g.getX() + i;
                        int g_y = g.getY() + j;

                        if (inBounds(g_x,g_y) && !no_sign_place.contains(new Coordinate(g_x,g_y))){
                            boolean flag = !no_sign_place.contains(new Coordinate(g_x,g_y));
                            if ( getItem(g_x,g_y) == null || gradient_value < getItem(g_x,g_y).getValue()){
                                Gradient add_target = new Gradient(g_x,g_y,gradient_value);
                                if (!next_layer.contains(add_target)){
                                    next_layer.add(add_target);
                                }
                            }
                        }

                    }
                }

            }

            //remove last layer element
            layer.clear();
            layer.addAll(next_layer);

            this.placeItems(layer);
            gradient_value++;
        }

        if (getItem(x,y) != null){
            free(x,y);
        }

    }
}
