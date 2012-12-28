/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import assault.game.gameObjects.TerrainObject;
import assault.game.loading.ResourcePreloader;
import java.io.File;
import java.io.IOException;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author matt
 */
public class TerrainResource extends XmlResource {

	private int range;
	private GroundCover[] groundCovers = {};
	private TerrainObject[] terrainObjects = {};
	private final ModResourceHolder mod;

	public TerrainResource(ResourcePreloader rp, SAXBuilder sb, ModResourceHolder mod, File location) throws ResourceException {
		super(rp, sb, location);
		this.mod = mod;
	}

	@Override
	protected void parseXmlValues() throws BadlyFormattedXMLException, ResourceException {
		try {
			getRp().setStatusString("\nloading Terrain values in " + getBaseFile());
			//name
			String foundName = getRootE().getChildText("name");
			if (!(foundName).equals(getBaseFile().getParentFile().getName())) {
				throw new BadlyFormattedXMLException("the name found in the file (" + foundName + ") differs from folder name(" + getBaseFile().getParentFile().getName() + ").");
			}
			getRp().printDebugLine("name = " + getName());
			//no point in setting it to the same thing

			//range
			range = getRootE().getChild("range").getAttribute("val").getIntValue();

			//groundcovers (but not objects)
			Element[] groundCoverEs = {};
			groundCoverEs = (Element[]) getRootE().getChild("groundCovers").getChild("covers").getChildren("type").toArray(groundCoverEs);
			groundCovers = new GroundCover[groundCoverEs.length];
			for (int i = 0; i < groundCoverEs.length; i++) {
				groundCovers[i] = new GroundCover(this, groundCoverEs[i]);
			}

		} catch (DataConversionException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName(), ex, getBaseFile());
		} catch (NullPointerException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName(), ex, getBaseFile(), getRootE());
		} catch (IOException ioe) {
			throw new ResourceException(getName(), ioe, getBaseFile());
		}
		getRp().setStatusString("loaded Terrain values in " + getBaseFile());
	}

	@Override
	protected void parseReferencialXmlValues() throws BadlyFormattedXMLException, ResourceException {
		
	}

	/**
	 * @return range
	 */
	public int getRange() {
		return range;
	}

	/**
	 * @return groundCovers
	 */
	public GroundCover[] getGroundCovers() {
		return groundCovers;
	}

	/**
	 * @return terrainObjects
	 */
	public TerrainObjectResourceHolder[] getTerrainObjects() {
		return mod.getTerrainObjects();
	}

	private void setRange(int range) {
		this.range = range;
	}

	private void setGroundCovers(GroundCover[] groundCovers) {
		this.groundCovers = groundCovers;
	}

	private void setTerrainObjects(TerrainObject[] terrainObjects) {
		this.terrainObjects = terrainObjects;
	}

	@Override
	public String getQualifiedName() {
		String out = "";
		if (mod == null || mod.getName() == null) {
			out += "???.";
		} else {
			out += mod.getName() + ".";
		}
		if (getName() == null) {
			if (getBaseFile() != null) {
				out += getBaseFile().toString();
			} else {
				out += "???";
			}
		} else {
			out += getName();
		}
		return out;
	}

	@Override
	public String createResourceName(File f) {
		return f.getParentFile().getName();
	}
}
