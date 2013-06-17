package jbomber;

import java.util.ArrayList;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;

public class SimpleAI extends GenericAI {
    
    public SimpleAI (Main main, Player player)
    {
        super(main, player);
    }

    @Override
    public void updateAI() {       
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
                attackEnemy(op);
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
    
    /**
     * Given the paths to opponent and power-up determines the next step.
     * @param op Path to the opponent.
     * @param po Path to the power-up.
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

    /**
     * Sees if the targeted opponent is within hitting range and attacks if so.
     * @param op Path to the target.
     */
    private void attackEnemy(Path toOp) {
        int opX = toOp.getStep(toOp.getLength() - 1).getX();
        int opY = toOp.getStep(toOp.getLength() - 1).getY();
        if (this.player.getX() == opX || this.player.getY() == opY) {
            // in the same row or column
            if (toOp.getLength() <= this.player.getFirePower() + 1) {
                // and is reachable
                this.player.placeBomb(map);
//                System.out.println("Player #"+player.getPID()+" will attack block ["+opX+", "+opY+"]");
            }
        }
    }
}
