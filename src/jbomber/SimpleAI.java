package jbomber;

import java.util.ArrayList;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;

public class SimpleAI extends GenericAI {
    
    @Override
    public void updateAI(Player player, Main main) {       
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
            if (!map.isPositionSafe(player.getX(), player.getY())) {
                // unsafe, must move away
                Path safe = findClosestSafeSpot();
                
                if (safe == null) { return; } // I have accepted my fate.
                takeStep(safe.getStep(1), main);
            }
            else {
                Path op = super.findClosestOponent();
                Path po = findClosestPowerUp();

                if (isEnemyReachable(op)) {
                    // attack enemy
                } else {                    
                    // safe, time to think
                    // explore, look for powerups
                }
                
                Step st = chooseNextStep(op, po);
                if (st == null) {return;} // TODO: remove
                if ( map.isPositionSafe(st.getX(), st.getY()) ) {
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
        if (po == null && op == null)   { return null; }
        if (po == null && op!= null)    { return op.getStep(1); }
        if (op == null && po!= null)    { return po.getStep(1); }
        if (op.getLength() < po.getLength() + 3) {
            // go for oponent
            return op.getStep(1);
        } else {
            // go for powerup
            return po.getStep(1);
        }
    }
    
    /**
     * Finds a spot that is not targeted by a bomb.
     * @return 
     */
    private Path findClosestSafeSpot() {        
        finder = new AStarPathFinder(map, 500, false);
        Path path = null;
        
        int distance = 1;
        
        while ( (path = findSafeSpot(distance, this.player.getX(), this.player.getY())) == null) {
            distance++;
            if (distance > 9) { return null; } // accept your faith.
        }
        return path;
    }
}