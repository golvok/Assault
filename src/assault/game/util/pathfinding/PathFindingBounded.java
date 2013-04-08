
package assault.game.util.pathfinding;

import assault.display.Bounded;

/**
 *
 * @author 088241930
 */
public abstract interface PathFindingBounded extends Bounded{
    
    public boolean[][] getExamined();
    public boolean[][] getOnOpenSet();
    public boolean[][] getOnPath();
    public boolean[][] getClosedSet();
    
    public void setExamined(boolean[][] exa);
    public void setOnOpenSet(boolean[][] open);
    public void setOnPath(boolean[][] onPath);
    public void setClosedSet(boolean[][] closed);
}
