package jbomber;

import static jbomber.Main.mt;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

public class Map implements TileBasedMap {
    
    /** The values indicating cell contents */
    public final int CLEAR = 0;
    public final int BLOCKED = 1;
    public final int OBSTACLE = 2;

    /** The width in grid cells of our map */
    public final int WIDTH = 19;
    public final int HEIGHT = 15;

    public static final int TILE_SIZE = 32;

    /** The actual data for our map */
    public int[][] board    = new int[WIDTH][HEIGHT];
    public int[][] players  = new int[WIDTH][HEIGHT];
    public Bomb[][] bombs   = new Bomb[WIDTH][HEIGHT];
    public Fire[][] fire    = new Fire[WIDTH][HEIGHT];
    
    public int jitterX;
    public int jitterY;
    private SpriteSheet tileset;

    private TiledMap map;
    public Map(Main main) {
        board   = new int[19][15];
        players = new int[19][15];
        bombs   = new Bomb[19][15];
        fire    = new Fire[19][15];
        
        this.jitterX = main.jitterX;
        this.jitterY = main.jitterY;
        this.tileset = main.tileset;
        
        for (int x = 0; x < 19; x++) {
            for (int y = 0; y < 15; y++) {
                board[x][y] = 1;
            }
        }
        for (int x = 1; x < 18; x++) {
            for (int y = 1; y < 14; y++) {
                if (x % 2 == 0 && y % 2 == 0) {
                    board[x][y] = BLOCKED;
                }
                else {
                    board[x][y] = CLEAR;
                }
            }
        }
        for (int x = 0; x < 19; x++) {
            for (int y = 0; y < 15; y++) {
                if (board[x][y] != 1 && mt.nextInt(5) > 1) {
//                    board[x][y] = OBSTACLE;
                }
            }
        }
        board[1][1]     = CLEAR;
        board[1][2]     = CLEAR;
        board[2][1]     = CLEAR;
        board[16][1]    = CLEAR;
        board[17][1]    = CLEAR;
        board[17][2]    = CLEAR;
        board[16][13]   = CLEAR;
        board[17][13]   = CLEAR;
        board[17][12]   = CLEAR;
        board[1][13]    = CLEAR;
        board[1][12]    = CLEAR;
        board[2][13]    = CLEAR;
    }

    @Override
    public int getWidthInTiles() { return WIDTH; }

    @Override
    public int getHeightInTiles() { return HEIGHT; }

    @Override
    public void pathFinderVisited(int x, int y) {
        
        
    }

    /**
     * Check if a particular location on the map is blocked. Note
     * that the x and y parameters are floating point numbers meaning
     * that we can be checking partially across a grid cell. Blocking objects
     * include BLOCKs and bombs.
     * 
     * @param x The x position to check for blocking
     * @param y The y position to check for blocking
     * @return True if the location is blocked
     */
    @Override
    public boolean blocked(PathFindingContext pfc, int x, int y) {
        return board[(int) x][(int) y] == BLOCKED || 
                bombs[(int) x][(int) y] != null;
    }

    /**
     * Get the cost of moving through the given tile
     * @param pfc
     * @param x
     * @param y
     * @return 
     */
    @Override
    public float getCost(PathFindingContext pfc, int x, int y) {        
        if (board[(int) x][(int) y] == OBSTACLE) { return 3; }
        else { return 1; }
    }

    
    
    public boolean hasObstacle(int x, int y) {
        return board[x][y] == OBSTACLE;
    }
    
    
    /************************    Drawing Functions     ************************/
    /**
     * Render the map to the graphics context provided. The rendering
     * is just simple fill rectangles
     * 
     * @param g The graphics context on which to draw the map
     */
    public void draw(Main main) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tileset.getSprite(5, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                switch(board[x][y]) {
                    case 1: {
                        tileset.getSprite(4, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                        break;
                    } case 2: {
                        tileset.getSprite(3, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                        break;
                    } case 3: {
                        if (bombs[x][y].getTimeLeft() > 80) {
                            main.bombImage.getSprite(0, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                        } if (bombs[x][y].getTimeLeft() <= 80 && bombs[x][y].getTimeLeft() > 50) {
                            main.bombImage.getSprite(1, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                        } if (bombs[x][y].getTimeLeft() <= 50 && bombs[x][y].getTimeLeft() > 20) {
                            main.bombImage.getSprite(2, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                        } if (bombs[x][y].getTimeLeft() <= 20) {
                            main.bombImage.getSprite(3, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                        } break;
                    }
                    //4 is player
                    case 5: {
                        if (fire[x][y] == null) {
                            tileset.getSprite(2, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                        } break;
                    } case 6: {
                        if (fire[x][y] == null) {
                            tileset.getSprite(15, 0).draw(x * 32 + jitterX, y * 32 + jitterY);
                        } break;
                    }
                }
            }
        }
    }

    /**
     * Returns if the players position is in bomb range.
     * @param player
     * @return 
     */
//    public boolean isPositionSafe(int xp, int yp) {
//        Bomb b;
//        int pow;
//        for (int x = 0; x < WIDTH; x++) {
//            for (int y = 0; y < HEIGHT; y++) {
//                b = bombs[x][y];
//                if (b != null) {
//                    pow = b.getSize();
//                    
//                    if (x == xp && y == yp) {
//                        return false;
//                    }
//                    if (x == xp) {
//                        while (pow > -1) {
//                            if (y + pow == yp) { return false; }
//                            if (y - pow == yp) { return false; }                            
//                            pow--;
//                        }
//                    }
//                    if (y == yp) {
//                        while (pow > -1) {
//                            if (x + pow == xp) { return false; }
//                            if (x - pow == xp) { return false; }
//                            pow--;
//                        }
//                    }
//                }
//            }
//        }        
//        return true;
//    }
    
    public boolean isPositionSafeAlternate(int x, int y) {   
        int offset = 0;        
        boolean upFlag, downFlag, leftFlag, rightFlag;
        upFlag = downFlag = leftFlag = rightFlag = true;
        if (bombs[x][y]!= null || hasFire(x,y)) return false;
        while (upFlag || downFlag || leftFlag || rightFlag) {
            if (upFlag) {
                if (x+offset == WIDTH) { upFlag = false; break; }
                if (this.board[x+offset][y] == OBSTACLE) { upFlag = false; }
                else {
                    if (this.bombs[x+offset][y] != null) {
//                        System.out.println("~>" + this.bombs[x+offset][y].getSize());
                        if (this.bombs[x+offset][y].getSize() >= offset) { return false; }
                    }
                }
            } 
            if (downFlag) {
                if (x-offset == -1) { downFlag = false; break; }
                if (this.board[x-offset][y] == OBSTACLE) { downFlag = false; }
                else {
                    if (this.bombs[x-offset][y] != null) {
//                        System.out.println("~~>" + this.bombs[x-offset][y].getSize());
                        if (this.bombs[x-offset][y].getSize() >= offset) { return false; }
                    }
                }
            }
            if (leftFlag) {
                if (y+offset == HEIGHT) { leftFlag = false; break; }
                if (this.board[x][y+offset] == OBSTACLE) { leftFlag = false; }
                else {
                    if (this.bombs[x][y+offset] != null) {
//                        System.out.println("~~~>" + this.bombs[x][y+offset].getSize());
                        if (this.bombs[x][y+offset].getSize() >= offset) { return false; }
                    }
                }
            }
            if (rightFlag) {                
                if (y-offset == -1) { rightFlag = false; break; }
                if (this.board[x][y-offset] == OBSTACLE) { rightFlag = false; }
                else {
                    if (this.bombs[x][y-offset] != null) {
//                        System.out.println("~~~~>" + this.bombs[x][y-offset].getSize());
                        if (this.bombs[x][y-offset].getSize() >= offset) { return false; }
                    }
                }
            }
            offset++;
        }
        return true;
    }

    private boolean hasFire(int x, int y) {
//        System.out.println("? " + fire[x][y]!=null);
        return fire[x][y]!=null;
    }
    
}
