/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assault.game.gameObjects;

import assault.game.display.GameArea;
import assault.game.loading.resourceHolders.ResourceException;
import assault.game.loading.resourceHolders.ResourceResourceHolder;

/**
 *
 * @author matt
 */
public class AResource extends Selectable{

	public AResource(GameArea g,int x,int y, ResourceResourceHolder src) throws ResourceException {
		super(g, x, y, src, 1, null);
	}
}
