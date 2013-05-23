package jbomber;

import java.util.ArrayList;
import java.util.HashSet;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;

public class MapClearAI extends GenericAI {
    private AStarPathFinder finder; 

    public MapClearAI()
    {
        super();
        finder = new AStarPathFinder(map, 500, false);
    }

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
                // safe, time to think
                // Get the path to the closest obstacle to destroy
                Path obstacle = findClosestObstacle();
                // Get the first step to that path
                Step st = obstacle.getStep(1);
                // There should be a path 
                if (st == null) {
                    System.out.println("NO STEP FOUND!?");
                    return;
                }
                if ( map.isPositionSafe(st.getX(), st.getY()) ) {
                    takeStep(st, main);
                }
                else 
                    System.out.println("unsafe path, not taking step");
            }
        }        
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
            cells = expandNeighbors(cells);
            distance--;
        }
        for (Cell c : cells) {
            if (map.hasObstacle(c.x, c.y)) {
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

