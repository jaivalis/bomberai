package jbomber;

import static jbomber.Main.mt;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

public class Map implements TileBasedMap {
    
    /** The values indicating cell contents */
    private static final int CLEAR = 0;
    private static final int BLOCKED = 1;
    private static final int OBSTACLE = 2;

    /** The width in grid cells of our map */
    private static final int WIDTH = 19;
    private static final int HEIGHT = 15;

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
                    board[x][y] = OBSTACLE;
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
     * that we can be checking partially across a grid cell.
     * 
     * @param x The x position to check for blocking
     * @param y The y position to check for blocking
     * @return True if the location is blocked
     */
    @Override
    public boolean blocked(PathFindingContext pfc, int x, int y) {
        return board[(int) x][(int) y] == BLOCKED;// || board[(int) x][(int) y] == OBSTACLE;
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
        for (int x = 0; x < 19; x++) {
            for (int y = 0; y < 15; y++) {
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

    boolean isPositionSafe(Player player) {
        return true;
    }
    
    
    
}
