
package assault.game.util.pathfinding;

import assault.game.util.GridManager;
import assault.game.util.pathfinding.moving.AbstractPathObject;
import assault.util.Ptr;

/**
 *
 * @author 088241930
 */
public abstract class RawPathFinder {

    protected final GridManager gManager;
      
    public RawPathFinder(GridManager gm) {
        gManager = gm;
    }
    public abstract RawPathObject findPath(PathFindingBounded pfgo, int destX, int destY, AbstractPathObject apoToaddTo,boolean clearApo, boolean clearImmediately, Ptr<Boolean> canceled);
	
}
