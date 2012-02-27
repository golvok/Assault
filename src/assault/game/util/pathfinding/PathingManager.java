/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding;

/**
 *
 * @author matt
 */
public class PathingManager {
	private boolean canceled = false;

	public boolean isCanceled() {
		return canceled;
	}

	public void Cancelpathing() {
		this.canceled = true;
	}
	
}
