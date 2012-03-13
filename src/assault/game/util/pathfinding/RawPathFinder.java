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
public abstract class RawPathFinder {

    protected final GridManager gManager;
	private boolean canceled = false;
      
    public RawPathFinder(GridManager gm) {
        gManager = gm;
    }
    public abstract RawPathObject findPath(PathFindingGridObject pfgo, int destX, int destY);
	
	public void cancel(){
		canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}
	
}
