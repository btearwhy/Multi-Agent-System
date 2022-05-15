package agent;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import agent.behavior.Behavior;
import agent.behavior.BehaviorState;
import agent.behavior.memory.CellMemory;
import agent.behavior.memory.MapMemory;
import environment.CellPerception;
import environment.Perception;
import environment.world.packet.Packet;



public interface AgentState {

    /**
     * Get the perception of this agent.
     */
    Perception getPerception();


    /**
     * Returns a CellPerception of the previous area this agent stood on.
     */
    CellPerception getPerceptionLastCell();

    /**
     * Check to see if an agent can see a destination with the specified color.
     *
     * @return {@code true} if this agent sees such a destination, {@code false} otherwise.
     */
    boolean seesDestination(Color color);

    
    /**
     * Check to see if an agent can see any destination.
     *
     * @return {@code true} if this agent sees such a destination, {@code false} otherwise.
     */
    boolean seesDestination();


    /**
     * Check to see if this agent can see a packet with the specified color.
     *
     * @return {@code true} if this agent can see such a packet, {@code false} otherwise.
     */
    boolean seesPacket(Color color);


    /**
     * Check to see if this agent can see any packet.
     *
     * @return {@code true} if this agent can see such a packet, {@code false} otherwise.
     */
    boolean seesPacket();



    
    /**
     * Returns the optional packet this agent is carrying.
     * @return An optional of the packet the agent carries, or an empty optional otherwise.
     */
    Optional<Packet> getCarry();
    

    /**
     * Check if the agent is carrying something.
     * @return {@code true} if the agent carries a packet, {@code false} otherwise.
     */
    boolean hasCarry();


    /**
     * Get the X coordinate of this agent.
     */
    int getX();

    /**
     * Get the Y coordinate of this agent.
     */
    int getY();


    /**
     * Get the name of this agent.
     */
    String getName();


    /**
     * Get the optional color of this agent itself.
     */
    Optional<Color> getColor();


    /**
     * Get the battery state of the agent.
     * @return  The battery state of the agent (from {@link environment.EnergyValues#BATTERY_MIN} to {@link environment.EnergyValues#BATTERY_MAX}).
     */
    int getBatteryState();


    /**
     * Get the current Behavior.
     */
    Behavior getCurrentBehavior();
    

    



    /**
     * Adds a memory.txt fragment to this agent (if its memory.txt is not full).
     *
     * @param key     The key associated with the memory.txt fragment
     * @param data    The memory.txt fragment itself
     */
    void addMemoryFragment(String key, String data);

    /**
     * Removes a memory.txt fragment with given key from this agent's memory.txt.
     * @param key  The key of the memory.txt fragment to remove.
     */
    void removeMemoryFragment(String key);

    /**
     * Get a memory.txt fragment with given key from this agent's memory.txt.
     * @param key  The key of the memory.txt fragment to retrieve.
     */
    String getMemoryFragment(String key);

    /**
     * Get all the keys of stored memory.txt fragments in this agent's memory.txt.
     */
    Set<String> getMemoryFragmentKeys();

    /**
     * Get the current number of memory.txt fragments in memory.txt of this agent.
     */
    int getNbMemoryFragments();

    /**
     * Get the maximum number of memory.txt fragments for this agent.
     */
    int getMaxNbMemoryFragments();


    /**
     * Set the behavior state of this agent. This method should, generally speaking, not be used by developers.
     * @param state The behavior state to switch to.
     */
    void setCurrentBehaviorState(BehaviorState state);

    void updateMapMemory();

    void clearGoal();

    List<CellMemory> getAllCellsMemory();

    MapMemory getMapMemory();

}
