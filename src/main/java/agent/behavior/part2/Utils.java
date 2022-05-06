package agent.behavior.part2;/**
 * @author ：mmzs
 * @date ：Created in 2022/3/19 03:00
 * @description：Some utils methods
 * @modified By：
 * @version: $
 */

import agent.AgentAction;
import agent.AgentState;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import environment.ActiveItemID;
import environment.CellPerception;
import environment.Coordinate;
import environment.EnergyValues;
import environment.Perception;
import environment.Representation;
import environment.world.agent.AgentRep;
import environment.world.destination.DestinationRep;
import environment.world.generator.PacketGeneratorRep;
import environment.world.packet.PacketRep;
import util.Pair;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author     ：mmzs
 * @date       ：Created in 2022/3/19 03:00
 * @description：Some utils methods
 * @modified By：
 * @version: $
 */

public class Utils {
    public static final List<Coordinate> moves = new ArrayList<>(List.of(
            new Coordinate(0, 1), new Coordinate(1, 1),
            new Coordinate(1, 0), new Coordinate(1, -1),
            new Coordinate(0, -1), new Coordinate(-1, -1),
            new Coordinate(-1, 0), new Coordinate(-1, 1)
    ));



    public static int getDir(int x1, int y1, int x2, int y2){
        int dir = 0;
        if(x2 == x1 && y2 > y1) dir = 0;
        else if(x2 > x1 && y2 > y1) dir = 1;
        else if(x2 > x1 && y2 == y1) dir = 2;
        else if(x2 > x1 && y2 < y1) dir = 3;
        else if(x2 == x1 && y2 < y1) dir = 4;
        else if(x2 < x1 && y2 < y1) dir = 5;
        else if(x2 < x1 && y2 == y1) dir = 6;
        else if(x2 < x1 && y2 > y1) dir = 7;
        else dir = 8;
        return dir;
    }

    public static int getClockwiseDirectionIfBlocked(AgentState agentState, int dir){
        int i = 0;
        int x = agentState.getX();
        int y = agentState.getY();
        while(agentState.getPerception().getCellPerceptionOnAbsPos(x + moves.get((dir + i) % 8).getX(), y + moves.get((dir + i) % 8).getY()) == null ||
                !agentState.getPerception().getCellPerceptionOnAbsPos(x + moves.get((dir + i) % 8).getX(), y + moves.get((dir + i) % 8).getY()).isWalkable()){
            i++;
        }
        return (dir + i) % 8;
    }



    public static int getPreviousDir(AgentState agentState){
        CellPerception pre = agentState.getPerceptionLastCell();
        return getDir(pre.getX(), pre.getY(), agentState.getX(), agentState.getY());
    }



    public static String getTargetFromGoal(AgentState agentState){
        JsonObject goal = new Gson().fromJson(agentState.getMemoryFragment("goal"), JsonObject.class);
        return goal.get("target").getAsString();
    }

    public static Color getTargetColorFromGoal(AgentState agentState){
        JsonObject goal = new Gson().fromJson(agentState.getMemoryFragment("goal"), JsonObject.class);
        return new Color(Integer.parseInt(goal.get("color").getAsString()));
    }

    public static Coordinate getCoordinateFromGoal(AgentState agentState){
        JsonObject goalObject = new Gson().fromJson(agentState.getMemoryFragment("goal"), JsonObject.class);
        JsonObject corObject = goalObject.getAsJsonObject("coordinate");
        Coordinate cor = new Coordinate(Integer.valueOf(corObject.get("x").getAsString()), Integer.valueOf(corObject.get("y").getAsString()));
        return cor;
    }



    public static void setGoal(AgentState agentState, JsonObject goalObject){
        agentState.removeMemoryFragment("goal");
        agentState.addMemoryFragment("goal", goalObject.toString());
    }

    public static boolean hasGoal(AgentState agentState){
        return agentState.getMemoryFragment("goal") != null;
    }


    public static JsonObject searchGoal(AgentState agentState){
        return searchGoalInMemory(agentState);

    }

    public static JsonObject searchNearestDestination(AgentState agentState, Color color){
        JsonObject goal = null;
        List<CellMemory> destination = new ArrayList<>();
        List<CellMemory> cells = agentState.getAllCellsMemory();
        for (CellMemory c:cells){
            DestinationRep d = c.getRepOfType(DestinationRep.class);
            if(d != null && d.getColor().equals(color)){
                destination.add(c);
            }
        }
        if(destination.isEmpty()) return null;
        CellMemory m = Collections.min(destination, new Comparator<CellMemory>() {
            @Override
            public int compare(CellMemory o1, CellMemory o2) {
                return Perception.distance(agentState.getX(), agentState.getY(), o1.getX(), o1.getY())
                        - Perception.distance(agentState.getX(), agentState.getY(), o2.getX(), o2.getY());
            }
        });
        goal = new JsonObject();
        goal.addProperty("target", "destination");
        goal.addProperty("color", color.getRGB());
        JsonObject corObject = new JsonObject();
        corObject.addProperty("x", String.valueOf(m.getX()));
        corObject.addProperty("y", String.valueOf(m.getY()));
        goal.add("coordinate", corObject);
        return goal;
    }

    @Nullable
    public static JsonObject searchGoalInMemory(AgentState agentState){
        JsonObject goal = null;
        List<CellMemory> cells = agentState.getAllCellsMemory();
        Map<Color, Boolean> destination = new HashMap<>();
        List<CellMemory> packet = new ArrayList<>();
        for (CellMemory cell:cells){
            PacketRep pr = cell.getRepOfType(PacketRep.class);
            DestinationRep dr = cell.getRepOfType(DestinationRep.class);
            if(pr != null){
                packet.add(cell);
            }
            else if(dr != null){
                destination.put(dr.getColor(), true);
            }
        }
        Collections.sort(packet, new Comparator<CellMemory>() {
            @Override
            public int compare(CellMemory o1, CellMemory o2) {
                return Perception.distance(agentState.getX(), agentState.getY(), o1.getX(), o1.getY())
                        - Perception.distance(agentState.getX(), agentState.getY(), o2.getX(), o2.getY());
            }
        });


        if(agentState.getColor().isEmpty()){
            for(CellMemory c:packet){
                Color color = c.getRepOfType(PacketRep.class).getColor();
                if(destination.containsKey(color)){
                    JsonObject corObject = new JsonObject();
                    corObject.addProperty("x", String.valueOf(c.getX()));
                    corObject.addProperty("y", String.valueOf(c.getY()));
                    goal = new JsonObject();
                    goal.addProperty("target", "packet");
                    goal.addProperty("color", color.getRGB());
                    goal.add("coordinate", corObject);
                    break;
                }
            }
        }
        else{
            Color agentColor = agentState.getColor().get();
            for(CellMemory c:packet){
                Color color = c.getRepOfType(PacketRep.class).getColor();
                if(agentColor.equals(color) && destination.containsKey(color)){
                    JsonObject corObject = new JsonObject();
                    corObject.addProperty("x", String.valueOf(c.getX()));
                    corObject.addProperty("y", String.valueOf(c.getY()));
                    goal = new JsonObject();
                    goal.addProperty("target", "packet");
                    goal.addProperty("color", color.getRGB());
                    goal.add("coordinate", corObject);
                    break;
                }
            }
        }
        return goal;
    }


    public static boolean isInReach(AgentState agentState, Coordinate coordinate){
        return agentState.getPerception().getCellPerceptionOnAbsPos(coordinate.getX(), coordinate.getY()) != null &&
                Perception.distance(agentState.getX(), agentState.getY(), coordinate.getX(), coordinate.getY()) < 2;
    }

    public static double chargeThreshold(AgentState agentState) {
		double agentNum = (double)calAgentInMemory(agentState);
		double batteryCapacity = (double)EnergyValues.BATTERY_SAFE_MAX;
		double unitConsumption =  (double)EnergyValues.BATTERY_DECAY_SKIP;
		double stepConsumption =(double) EnergyValues.BATTERY_DECAY_STEP;
		double chargingEfficiency = (double)100;
		boolean hasPacket = agentState.hasCarry();
		double field = (double)agentState.getPerception().getCellPerceptionOnRelPos(0,0).getGradientRepresentation().get().getValue();
		int H = agentState.getPerception().getHeight();
		int W = agentState.getPerception().getHeight();
		double perceptionRadius = (double)Math.max(H, W);

		double waitEnergy = Math.ceil(batteryCapacity*(Math.pow((unitConsumption/chargingEfficiency+1),(agentNum-1.0))-1.0)
				/(Math.pow((unitConsumption/chargingEfficiency+1),(agentNum-1.0))));
		double navigateEnergy = unitConsumption*(-("false".indexOf("" + hasPacket)))+stepConsumption*field;
		double agentStepEnergy = unitConsumption*agentNum*(perceptionRadius+1.0)/2.0;
		
		double threshold = navigateEnergy + agentStepEnergy + waitEnergy; 
		System.out.println(agentNum+" "+waitEnergy+" "+threshold);///
		return threshold;
    }
    
    //calculate agent number
    public static int calAgentInMemory(AgentState agentState){
    	 int agentNum = 1;
         String key = "agent";
         JsonObject agentObj = new Gson().fromJson(agentState.getMemoryFragment(key), JsonObject.class);
         if(agentObj!=null) {
        	 JsonArray agentArray = agentObj.get(key).getAsJsonArray();
             System.out.println("agentnum"+agentArray.size());
             agentNum = agentArray.size()+1;
         }
         return agentNum;
    }
    
    //update agent
    public static void updateAgentNum(AgentState agentState){
    	Perception perception = agentState.getPerception();
    	List<CellPerception> allCells = perception.getAllCells();
    	for(CellPerception cell : allCells){
    		if(cell.containsAgent()){
    			if (cell.getX()!=agentState.getX()&&cell.getY()!=agentState.getY()) {
    				//System.out.println("agent"+agentState.getX()+" "+agentState.getY()+"cell"+cell.getX()+" "+cell.getY());///
    				updateAgentNum(agentState, cell.getRepOfType(AgentRep.class));
    			}
    		}
    	}
    }
    
    public static void updateAgentNum(AgentState agentState, Representation representation) {
    	String key = "agent";
    	ActiveItemID id = ((AgentRep) representation).getId();
    	 if(agentState.getMemoryFragment(key) != null){
    		 JsonObject data = new Gson().fromJson(agentState.getMemoryFragment(key), JsonObject.class);
    		// System.out.println("id1");///
    		 JsonArray array = data.get(key).getAsJsonArray();
    		 boolean mark = false;
    		 for(int i = 0;i < array.size(); i++){
    			 int idJson = array.get(i).getAsJsonObject().get("id").getAsInt();
    			 if(idJson == id.getID()){
    				 mark = true;
    				 return;
    			 }	 
    		 }
    		 if(!mark){
    			 JsonObject newIdObj = new JsonObject();
    			 newIdObj.addProperty("id", String.valueOf(id.getID()));
    			 //System.out.println("id2");///
    			 array.add(newIdObj);
    		 }
    		 agentState.removeMemoryFragment(key);
    		 agentState.addMemoryFragment(key, data.toString());
             
         }else{ 
        	 JsonObject basicObj = new JsonObject();
             JsonObject idObject = new JsonObject();
             JsonArray idArray = new JsonArray();
             idObject.addProperty("id", String.valueOf(id.getID()));
             idArray.add(idObject);
             basicObj.add(key, idArray);
             agentState.addMemoryFragment(key, basicObj.toString());
             //System.out.println("id0");
         }
    }
}

