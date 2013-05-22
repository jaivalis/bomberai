package jbomber;

import java.util.ArrayList;
import java.util.HashSet;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;

public class SimpleAI extends PlayerAI {    
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
        
        if (player.getClock() > 15 && player.getAlive()) {
            
            if (!map.isPositionSafeAlternate(player.getX(), player.getY())) {
                // unsafe, must move away
                System.out.println("unsafe!!");
                Path safe = findClosestSafeSpot();
                
                if (safe == null) { return; } // I have accepted my fate.
                takeStep(safe.getStep(1), main);
            }
//            else {                
//                System.out.println("safe!!");
//                // safe, time to think
//                Path op = findClosestOponent();
//                Path po = findClosestPowerUp();
//
//                Step st = chooseNextStep(op, po);
//                if (st == null) {return;} // TODO: remove
//                if ( map.isPositionSafeAlternate(st.getX(), st.getY()) ) {
//                    takeStep(st, main);
//                }
//            }
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
    private void takeStep(Step s, Main m) {
        if (map.hasObstacle(s.getX(), s.getY())) {
            // place bomb, move to safety
            player.placeBomb(map);
        }
        else { // move regularly
            player.move(s.getX() - this.player.getX(), s.getY() - this.player.getY(), m);
//            this.player.setX(s.getX() - this.player.getX());
//            this.player.setY(s.getY() - this.player.getY());
            //player.move(s);
            //player.shift(m);
        }
    }
    
    private Path findClosestOponent() {
        finder = new AStarPathFinder(map, 500, false);
        Path path = null;
        
        for (Player p : players) {
            if (!p.getAlive()) { continue; }
            if (p.getColor().toString().equals(this.color)) { continue; }
            path = finder.findPath(new EnemyMover(this.color), this.x, this.y, p.getX(), p.getY());
            
//            System.out.println("Distance to opponent: " + path.getLength());            
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
//                    System.out.println("Distance to powerup: " + path.getLength());
                }
            }
        }
        return path;
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
        System.out.println("path length == " + path.getLength());
        return path;
    }
    
        
    /**
     * Returns a path of length 'l' to a safe spot given the coordinates
     * @param l
     * @param p
     * @param p
     * @return 
     */
    public Path findSafeSpot(int l, int x, int y) {
        finder = new AStarPathFinder(map, 500, false);
        Path path;
        int co = l;
        HashSet<Cell> hs = new HashSet<Cell>();
        hs.add(new Cell(x,y));
        while (co != 0) {
            //expand al l times
            hs = expandNeighbors(hs);
            co--;
        }
        System.out.println("hs size " + hs.size());
        for (Cell c : hs) {
            if (map.isPositionSafeAlternate(c.x, c.y)) {
                path = finder.findPath(new EnemyMover(this.color), this.x, this.y, c.x, c.y);
//                if (path.getLength() == l) { // TODO: is this required?
                    return path;
//                }
            }
        }
        return null;
    }
    
    private HashSet<Cell> expandNeighbors(HashSet<Cell> s) {
        HashSet<Cell> tmp = new HashSet<Cell>();
        for (Cell cell : s) {
            tmp.add(cell);
            tmp.addAll(cell.getNeighbors(map));
        }
        s.addAll(tmp);
        return s;
    }
}
