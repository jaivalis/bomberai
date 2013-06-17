package jbomber;

import java.util.HashSet;

public class Cell {
    public int x,y;

    Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public HashSet<Cell> getNeighbors(Map m) {
        HashSet<Cell> ret = new HashSet<Cell>();
        if (this.x + 1 < m.WIDTH) {
            if (m.board[x+1][y] != m.BLOCKED && m.board[x+1][y] != m.OBSTACLE) {
                ret.add(new Cell(x+1, y));
            }
        }
        if (this.x - 1 > 0) {
            if (m.board[x-1][y] != m.BLOCKED && m.board[x-1][y] != m.OBSTACLE) {
                ret.add(new Cell(x-1, y));
            }
        }        
        if (this.y + 1 < m.HEIGHT) {
            if (m.board[x][y+1] != m.BLOCKED && m.board[x][y+1] != m.OBSTACLE) {
                ret.add(new Cell(x, y+1));
            }
        }
        if (this.y - 1 > 0) {
            if (m.board[x][y-1] != m.BLOCKED && m.board[x][y-1] != m.OBSTACLE) {
                ret.add(new Cell(x, y-1));
            }
        }        
        return ret;
    }

    public HashSet<Cell> getNeighborsIncludingObstacle(Map m) {
        HashSet<Cell> ret = new HashSet<Cell>();
        if (this.x + 1 < m.WIDTH) {
            if (m.board[x+1][y] != m.BLOCKED) {
                ret.add(new Cell(x+1, y));
            }
        }
        if (this.x - 1 > 0) {
            if (m.board[x-1][y] != m.BLOCKED) {
                ret.add(new Cell(x-1, y));
            }
        }        
        if (this.y + 1 < m.HEIGHT) {
            if (m.board[x][y+1] != m.BLOCKED) {
                ret.add(new Cell(x, y+1));
            }
        }
        if (this.y - 1 > 0) {
            if (m.board[x][y-1] != m.BLOCKED) {
                ret.add(new Cell(x, y-1));
            }
        }        
        return ret;
    }
    
    /**
     * Used by the Set (to avoid duplicates in neighbors).
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Cell))
            return false;
        Cell other = (Cell) obj;
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Used by the Set (to avoid duplicates in neighbors).
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.x;
        hash = 53 * hash + this.y;
        return hash;
    }
}
