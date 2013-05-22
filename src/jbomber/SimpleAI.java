package jbomber;

import java.util.ArrayList;
import java.util.HashSet;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;

public class SimpleAI extends GenericAI {    
    private Map map;
    
    private int x, y;
    private Player player;
    
    private AStarPathFinder finder;
    
    private ArrayList<Player> players;
    
    @Override
    public void updateAI(Player player, Main main) {
        this.x = player.getX();
        this.y = player.getY();        
        this.players = new ArrayList<Player>();
        this.player = player;
                
        players.add(main.blackBomber);
        players.add(main.blueBomber);
        players.add(main.redBomber);
        players.add(main.whiteBomber);
                        
        player.setClock(player.getClock()+1);
        if (map == null) {
            this.map = main.theMap;
        }
        
        if (player.getClock() > 15 && player.getAlive()) {            
            if (!map.isPositionSafeAlternate(player.getX(), player.getY())) {
                // unsafe, must move away
                Path safe = findClosestSafeSpot();
                
                if (safe == null) { return; } // I have accepted my fate.
                takeStep(safe.getStep(1), main);
            }
            else {
                // safe, time to think
                Path op = super.findClosestOponent();
                Path po = findClosestPowerUp();

                Step st = chooseNextStep(op, po);
                if (st == null) {return;} // TODO: remove
                if ( map.isPositionSafeAlternate(st.getX(), st.getY()) ) {
                    takeStep(st, main);
                }
            }
        }        
    }
    
    /**
     * Given the paths to opponent and power-up determines the next step.
     * @param op
     * @param po
     * @return 
     */
    private Step chooseNextStep(Path op, Path po) {
        if (po == null && op == null) {
            return null;
        }
        if (po == null && op!= null) {
            return op.getStep(1);
        }
        if (op == null && po!= null) {
            return po.getStep(1);
        }
        if (op.getLength() < po.getLength() + 3) {
            // go for oponent
            return op.getStep(1);
        } else {
            // go for powerup
            return po.getStep(1);
        }
    }
    
    /**
     * Given a step it decides upon the actions required to move in that
     * direction.
     * @param s 
     */
    private void takeStep(Step s, Main m) {
        if (map.hasObstacle(s.getX(), s.getY())) {
            // place bomb, move to safety
            player.placeBomb(map);
        }
        else { // move regularly
            player.move(s.getX() - this.player.getX(), s.getY() - this.player.getY(), m);
        }
    }

    
    /**
     * finds a spot that is not targeted by a bomb.
     * @return 
     */
    private Path findClosestSafeSpot() {        
        finder = new AStarPathFinder(map, 500, false);
        Path path = null;
        
        int distance = 1;
        
        while ( (path = findSafeSpot(distance, x, y)) == null) {
            distance++;
            if (distance > 9) { return null; } // accept your faith.
        }
        return path;
    }
    
    

}