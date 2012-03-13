/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import assault.game.loading.ResourcePreloader;
import assault.game.Player;
import assault.game.display.GameArea;
import assault.game.gameObjects.AObject;
import assault.game.gameObjects.Resource;
import java.awt.Point;
import java.io.File;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author matt
 */
public class ResourceResourceHolder extends SelectableResourceHolder {

	final static String RESOURCE_TYPE_NAME = "Resource";

	public ResourceResourceHolder(ModResourceHolder mod, File location, ResourcePreloader rp, SAXBuilder builder) throws BadlyFormattedXMLException, ResourceException {
		super(rp, builder, mod, location);
	}

	@Override
	public Resource createObject(GameArea g, int x, int y, Player owner) {
		try {
			return new Resource(g, x, y, this);
		} catch (ResourceException ex) {
			return null;
		}
	}

	@Override
	public AObject createObject(GameArea g, Point p, Player owner) {
		return createObject(g, p.x, p.y, owner);
	}
}
