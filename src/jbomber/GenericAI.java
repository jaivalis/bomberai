package jbomber;

import java.util.ArrayList;
import java.util.HashSet;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;

public abstract class GenericAI {
    protected Map map;
    protected Player player;
    protected AStarPathFinder finder;    
    protected ArrayList<Player> players;
    protected Main main;
   
    public GenericAI(Main main, Player thisPlayer)
    {
        this.main = main;
        this.map = main.theMap;
        this.finder = new AStarPathFinder(this.map, 500, false);
        this.players = new ArrayList<Player>();
        this.player = thisPlayer;
        players.add(main.blackBomber);
        players.add(main.blueBomber);
        players.add(main.redBomber);
        players.add(main.whiteBomber);
        System.out.println("init done");
    }
    
    /**
     * Abstract update method: checks if the player can call an update, and then
     * calls the updateAI that should be implemented by subclasses 
     */
    public void abstractUpdateAI()
    {
        player.setClock(player.getClock()+1);
        
        if (player.getClock() > 15 && player.getAlive()) {            
            updateAI();
        }        
    }

    protected Path findClosestOponent() {
        finder = new AStarPathFinder(map, 500, false);
        Path path = null;
        
        for (Player p : players) {
            if (!p.getAlive()) { continue; }
            if (p.getColor().toString().equals(this.player.getColor().toString())) { continue; }
            path = finder.findPath(new EnemyMover(this.player.getColor().toString()), 
                    this.player.getX(), this.player.getY(), p.getX(), p.getY());           
        }
        return path;
    }
    
    protected Path findClosestPowerUp() {
        finder = new AStarPathFinder(map, 500, false);
        Path path = null;
        
        for (int i = 0; i < map.getWidthInTiles(); i++) {
            for (int j = 0; j < map.getHeightInTiles(); j++) {
                if (map.board[i][j] == 5 || map.board[i][j] == 6) {
                    path = finder.findPath(new EnemyMover(this.player.getColor().toString()),
                            this.player.getX(), this.player.getY(), i, j);
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
        for (Cell c : hs) {
            if (map.isPositionSafe(c.x, c.y)) {
                path = finder.findPath(new EnemyMover(this.player.getColor().toString()),
                        this.player.getX(), this.player.getY(), c.x, c.y);
                return path;
            }
        }
        return null;
    }
    
    /**
     * Given a step it decides upon the actions required to move in that
     * direction.
     * @param s 
     */
    protected void takeStep(Path.Step s, Main m) {
        if (map.hasObstacle(s.getX(), s.getY())) {
            // place bomb, move to safety
            player.placeBomb(map);
        }
        else { // move regularly
            if (!m.theMap.hasFire(s.getX(), s.getY())) {
                player.move(s.getX() - this.player.getX(), s.getY() - this.player.getY(), m);
            }
        }
    }
    
    protected boolean isReachable(Path p) {
        if (p == null) { return false; }
        for (int i = 0; i < p.getLength(); i++) {
            if (map.board[p.getX(i)][p.getY(i)] == map.OBSTACLE) {
                return false;
            }
        }
        return true;        
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

    protected HashSet<Cell> expandNeighborsIncludingObstacle(HashSet<Cell> s) {
        HashSet<Cell> tmp = new HashSet<Cell>();
        for (Cell cell : s) {
            tmp.add(cell);
            tmp.addAll(cell.getNeighborsIncludingObstacle(map));
        }
        s.addAll(tmp);
        return s;
    }

    public abstract void updateAI();
}
