/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding;

import assault.game.util.GridManager;

/**
 *
 * @author 088241930
 */
public abstract class PathFinder {

    protected final GridManager gManager;
	private boolean canceled = false;
      
    public PathFinder(GridManager gm) {
        gManager = gm;
    }
    public abstract PathObject findPath(PathFindingGridObject pfgo, int destX, int destY);
	
	public void cancel(){
		canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}
	
}
