/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import assault.game.Player;
import assault.game.display.GameArea;
import assault.game.gameObjects.TerrainObject;
import assault.game.loading.ResourcePreloader;
import assault.util.Point;
import java.io.File;
import org.jdom.DataConversionException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author matt
 */
public class TerrainObjectResourceHolder extends SelectableResourceHolder {

	String layer = "";
	boolean impassible = false;

	public TerrainObjectResourceHolder(ResourcePreloader rp, SAXBuilder sb, ModResourceHolder mod, File xmlFile) throws BadlyFormattedXMLException, ResourceException {
		super(rp, sb, mod, xmlFile);
	}

	@Override
	protected void parseXmlValues() throws BadlyFormattedXMLException, ResourceException {
		super.parseXmlValues();
		
		try {
			layer = getRootE().getChild("layer").getAttributeValue("val");
			impassible = getRootE().getChild("impassible").getAttribute("val").getBooleanValue();
		} catch (DataConversionException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName(),
					"bad data in " + getBaseFile() + " (something cant be converted from a string properly) Message: " + ex.getStackTrace()[0]);
		}
	}

	@Override
	public TerrainObject createObject(GameArea g, double x, double y, Player owner) throws ResourceException {
		return new TerrainObject(g, x, y, this, owner);
	}

	@Override
	public TerrainObject createObject(GameArea g, Point p, Player owner) throws ResourceException {
		return createObject(g, p.getX(), p.getY(), owner);
	}

	public boolean isImpassible() {
		return impassible;
	}

	public String getLayer() {
		return layer;
	}
	
}
