package jbomber;

import java.util.ArrayList;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;

public class SimpleAI extends PlayerAI {
//    private static int[][] board;
//    private static int[][] players;
//    private static Bomb[][] bombs;
//    private static Fire[][] fire;
    
    private Map map;
    
    private int x, y;
    private String color;
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
        
        this.color = player.getColor().toString();
        
        
        player.setClock(player.getClock()+1);
        if (map == null) {
            this.map = main.theMap;
        }
        
        System.out.println();
        if (player.getClock() > 15 && player.getAlive()) {
            
            if (!map.isPositionSafe(player)) {
                // unsafe, must move away
                
            }
            else {
                // safe, time to think
                Path op = findClosestOponent();
                Path po = findClosestPowerUp();

                Step st = chooseNextStep(op, po);
                if (st == null) {return;}
                takeStep(st);
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
        if (po == null && op == null) { // TODO: remove
            return null;
        }
        if (po == null || op.getLength() < po.getLength() + 3) {
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
    private void takeStep(Step s) {
        if (map.hasObstacle(s.getX(), s.getY())) {
            // place bomb, move to safety
            player.placeBomb(map);
        }
        else { // move regularly
            player.move(s);
        }
    }
    
    private Path findClosestOponent() {
        finder = new AStarPathFinder(map, 500, false);
        Path path = null;
        
        for (Player p : players) {
            if (!p.getAlive()) { continue; }
            if (p.getColor().toString().equals(this.color)) { continue; }
            path = finder.findPath(new EnemyMover(this.color), this.x, this.y, p.getX(), p.getY());
            
            System.out.println("Distance to opponent: " + path.getLength());
            
        }
        
        return path;
    }
    
    private Path findClosestPowerUp() {        
        finder = new AStarPathFinder(map, 500, false);
        Path path = null;
        
        for (int i = 0; i < map.getWidthInTiles(); i++) {
            for (int j = 0; j < map.getHeightInTiles(); j++) {
                if (map.board[i][j] == 5 || map.board[i][j] == 6) {
                    path = finder.findPath(new EnemyMover(this.color), this.x, this.y, i, j);
                    System.out.println("Distance to powerup: " + path.getLength());
                }
            }
        }
        return path;
    }
}
