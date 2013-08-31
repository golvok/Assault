package assault.game.util.pathfinding;

import assault.game.util.ObjectManager;
import assault.game.util.pathfinding.moving.Relocatable;
import assault.util.Ptr;

/**
 *
 * @author Matthew Walker
 */
public abstract class RawPathFinder {

    protected final ObjectManager oManager;
      
    public RawPathFinder(ObjectManager om) {
        oManager = om;
    }
    
    public abstract RawPathObject findPathToNextPoint(Relocatable target, Ptr<Boolean> canceled);
	
}
