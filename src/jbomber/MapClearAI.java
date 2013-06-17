package jbomber;

import java.util.ArrayList;
import java.util.HashSet;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;

public class MapClearAI extends GenericAI {

    public MapClearAI (Main main, Player player)
    {
        super(main, player);
    }

    @Override
    public void updateAI() {       
        Path path;
        // It is of utmost importance to get a powerup
        if (map.isPositionSafe(player.getX(), player.getY())) 
        {
            path = findClosestPowerUp();
            if(path == null 
                || !isReachable(path) 
                || !map.isPositionSafe(path.getStep(1).getX(),
                    path.getStep(1).getY()))
            {
                // If we have bombs, and no powerups are near, blow up some
                // obstacles!
                //if(player.getBombAmt() > 0)
                //{
                    // Get the path
                path = findClosestObstacle();
                //}
                // If we cannot blow up stuff, and we can not get powerups, we
                // should just get to safety.
                if(path == null 
                    || !map.isPositionSafe(path.getStep(1).getX(),
                    path.getStep(1).getY()))
                {      
                    // just wait!
                    //path = findClosestSafeSpot();
                    System.out.println("Doing nothing");
                    return;
                }
                else
                    System.out.println("going for bombing");
            }
            else
                System.out.println("going for powerup");
        }
        else
        {
            // Just try to get to safety!
            path = findClosestSafeSpot();
            System.out.println("going for safety");
        }


        if (path == null)
        {
            System.out.println("No path found");
            return;
        }
        // Get first step of chosen path
        Step st = path.getStep(1);
        // There should be a path 
        if (st == null) {
            System.out.println("No path to anywhere found");
            return;
        }
        takeStep(st, main);
    }
    
    private Path findClosestObstacle()
    {
        Path path; 
        int distance = 1;
        while ( (path = findObstacle(distance, this.player.getX(), this.player.getY())) == null) {
            distance++;
            if (distance > 9) { return null; } // accept your faith.
        }
        return path;

    }

    private Path findObstacle(int distance, int x, int y)
    {
        HashSet<Cell> cells = new HashSet<Cell>();
        cells.add(new Cell(x,y));
        while (distance != 0) {
            //expand `distance` times
            cells = expandNeighborsIncludingObstacle(cells);
            distance--;
        }
        for (Cell c : cells) {
            if (map.hasObstacle(c.x, c.y)) {
                //System.out.printf("found obstacle at %d, %d\n", c.x, c.y);
                Path path = finder.findPath(new EnemyMover(this.player.getColor().toString()),
                    this.player.getX(), this.player.getY(), c.x, c.y);
                return path;
            }
        }
        return null;
    }

    /**
     * finds a spot that is not targeted by a bomb.
     * @return 
     */
    private Path findClosestSafeSpot() {        
        Path path; 
        int distance = 1;
        while ( (path = findSafeSpot(distance, this.player.getX(), this.player.getY())) == null) {
            distance++;
            if (distance > 9) { return null; } // accept your faith.
        }
        return path;
    }
    
    

}

