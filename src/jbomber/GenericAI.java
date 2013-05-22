package jbomber;

import java.util.ArrayList;
import java.util.HashSet;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;


public abstract class GenericAI {
    private Map map;
    
    private int x, y;
    private Player player;
    
    private AStarPathFinder finder;
    
    private ArrayList<Player> players;
    
    public abstract void updateAI(Player player, Main main);
    
    
    protected Path findClosestOponent() {
        finder = new AStarPathFinder(map, 500, false);
        Path path = null;
        
        for (Player p : players) {
            if (!p.getAlive()) { continue; }
            if (p.getColor().toString().equals(this.player.getColor().toString())) { continue; }
            path = finder.findPath(new EnemyMover(this.player.getColor().toString()), this.x, this.y, p.getX(), p.getY());           
        }
        return path;
    }
    
    protected Path findClosestPowerUp() {
        finder = new AStarPathFinder(map, 500, false);
        Path path = null;
        
        for (int i = 0; i < map.getWidthInTiles(); i++) {
            for (int j = 0; j < map.getHeightInTiles(); j++) {
                if (map.board[i][j] == 5 || map.board[i][j] == 6) {
                    path = finder.findPath(new EnemyMover(this.player.getColor().toString()), this.x, this.y, i, j);
                }
            }
        }
        return path;
    }
    
        /**
     * Returns a path of length 'l' to a safe spot given the coordinates
     * @param l
     * @param p
     * @param p
     * @return 
     */
    protected Path findSafeSpot(int l, int x, int y) {
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
//        System.out.println("hs size " + hs.size() + " hs.size() == l ? " + (hs.size() == l));
        for (Cell c : hs) {
//            System.out.println("c.x, x.y = [" + c.x + ", " + c.y + "]");
            if (map.isPositionSafeAlternate(c.x, c.y)) {
                path = finder.findPath(new EnemyMover(this.player.getColor().toString()), this.x, this.y, c.x, c.y);
//                if (path.getLength() == l) { // TODO: is this required?
                    return path;
//                }
            }
        }
//        System.out.println("returning null for l = " + l);
        return null;
    }
    
    protected HashSet<Cell> expandNeighbors(HashSet<Cell> s) {
        HashSet<Cell> tmp = new HashSet<Cell>();
        for (Cell cell : s) {
            tmp.add(cell);
            tmp.addAll(cell.getNeighbors(map));
        }
        s.addAll(tmp);
        return s;
    }
}
