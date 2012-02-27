
package assault.game.util.pathfinding;

import assault.game.util.GridObject;

/**
 *
 * @author 088241930
 */
public abstract interface PathFindingGridObject extends GridObject{
    
    public boolean[][] getExamined();
    public boolean[][] getOnOpenSet();
    public boolean[][] getOnPath();
    public boolean[][] getClosedSet();
    
    public void setExamined(boolean[][] exa);
    public void setOnOpenSet(boolean[][] open);
    public void setOnPath(boolean[][] onPath);
    public void setClosedSet(boolean[][] closed);
}
