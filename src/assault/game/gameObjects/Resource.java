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
public class Resource extends Selectable {

    public Resource(GameArea g, double x, double y, ResourceResourceHolder src) throws ResourceException {
        super(g, x, y, src, 1, null);
    }
}
