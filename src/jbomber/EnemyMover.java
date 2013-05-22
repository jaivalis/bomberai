package jbomber;

import org.newdawn.slick.util.pathfinding.Mover;

public class EnemyMover implements Mover {
    /** The unit ID moving */
    private String id;

    /**
     * Create a new mover to be used while path finder
     * 
     * @param type The ID of the unit moving
     */
    public EnemyMover(String id) {
        this.id = id;
    }

    /**
     * Get the ID of the unit moving
     * 
     * @return The ID of the unit moving
     */
    public String getId() {
        return id;
    }
}
