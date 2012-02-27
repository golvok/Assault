/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import assault.game.gameObjects.TerrainObject;
import assault.game.loading.ResourcePreloader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author matt
 */
public class ModResourceHolder extends XmlResource {

	public static final String UNIT_FFOLDER_NAME = "Unit";
	public static final String WEAPON_FOLDER_NAME = "Weapon";
	public static final String TERRAIN_OBJECT_FOLDER_NAME = "TerrainObject";
	public static final String INFO_FILE_NAME = "modinfo.xml";
	public static final String INFO_FILE_NAME_TAG_NAME = "name";
	public static final String INFO_FILE_HAS_UNITS_TAG_NAME = "hasUnits";
	public static final String INFO_FILE_HAS_WEAPONS_TAG_NAME = "hasWeapons";
	public static final String INFO_FILE_HAS_TERRAIN_OBJECTS_TAG_NAME = "hasTerrainObjects";
	public static final String INFO_FILE_HAS_TERRAINS_TAG_NAME = "hasTerrains";
	private Element infoFileRoot;
	private boolean hasUnits = false;
	private boolean hasWeapons = false;
	private boolean hasTerrainObjects = false;
	private boolean hasTerrains = false;
	private UnitResourceHolder[] units = {};
	private WeaponResourceHolder[] weapons = {};
	private TerrainObjectResourceHolder[] terrainObjects = {};
	private TerrainResource[] terrains = {};

	/**
	 *
	 * @param name name of the folder in src/mods/ to look in
	 */
	public ModResourceHolder(String name, ResourcePreloader rp, SAXBuilder sb) throws ResourceException {
		super(rp, sb, new File("lib/ASSAULT_DATA/mods/" + name + "/" + INFO_FILE_NAME), name);
		rp.setStatusString("created mod named " + name);
	}

	@Override
	public synchronized void parseXmlValues() throws ResourceException {
		if (!isLoaded()) {
			//parse the infofile
			try {
				getRp().printDebugLine("mod path = " + getBaseFile().getAbsolutePath());
				String infoPath = getBaseFile().getAbsolutePath();
				getRp().printDebugLine("mod infoFile path = " + infoPath);
				infoFileRoot = getBuilder().build(new File(infoPath)).getRootElement();
				if (infoFileRoot.getChild(ModResourceHolder.INFO_FILE_NAME_TAG_NAME) != null) {
					String name2 = infoFileRoot.getChild(ModResourceHolder.INFO_FILE_NAME_TAG_NAME).getText();
					getRp().printDebugLine("mod name (in info file) = " + name2);
				} else {
					getRp().setStatusString("name for mod at " + getName() + " wasn't in info file -- continuing with name of folder");
				}
				if (infoFileRoot.getChild(ModResourceHolder.INFO_FILE_HAS_UNITS_TAG_NAME) != null) {
					hasUnits = true;
				}
				if (infoFileRoot.getChild(ModResourceHolder.INFO_FILE_HAS_WEAPONS_TAG_NAME) != null) {
					hasWeapons = true;
				}
				if (infoFileRoot.getChild(ModResourceHolder.INFO_FILE_HAS_TERRAIN_OBJECTS_TAG_NAME) != null) {
					hasTerrainObjects = true;
				}
				if (infoFileRoot.getChild(ModResourceHolder.INFO_FILE_HAS_TERRAINS_TAG_NAME) != null) {
					hasTerrains = true;
				}
			} catch (NullPointerException ex) {
				throw new BadlyFormattedXMLException(getName(), ex, getBaseFile(), infoFileRoot);
			} catch (JDOMException ex) {
				throw new ResourceException(getName() + "'s mod info file", ex);
			} catch (IOException ex) {
				throw new ResourceException(getName() + "'s mod info file", ex);
			}
			//create this mod's contents
			createResourceHolders();
			setLoaded(true);
		}
	}

	@Override
	public void parseReferencialXmlValues() throws ResourceException {
		//units
		loadResouces(units,getRp());
		//weapons
		loadResouces(weapons,getRp());
		//terrrainObjects
		loadResouces(terrainObjects,getRp());
		//terrains
		loadResouces(terrains,getRp());
	}


	
	private void createResourceHolders() {
		if (hasUnits) {
			units = (UnitResourceHolder[]) createObjectResourceHolder(UNIT_FFOLDER_NAME);
		}
		if (hasWeapons) {
			weapons = (WeaponResourceHolder[]) createObjectResourceHolder(WEAPON_FOLDER_NAME);
		}
		if (hasTerrainObjects) {
			terrainObjects = (TerrainObjectResourceHolder[]) createObjectResourceHolder(TERRAIN_OBJECT_FOLDER_NAME);
		}
		if (hasTerrains){
			terrains = createTerrains();
		}
	}

	private ObjectResourceHolder[] createObjectResourceHolder(String typeName) {
		if (!(typeName.equals(UNIT_FFOLDER_NAME) || typeName.equals(WEAPON_FOLDER_NAME) || typeName.equals(TERRAIN_OBJECT_FOLDER_NAME))) {
			//only one of the typenames allowed.
			getRp().printDebugLine("unknown Type name: " + typeName);
			return null;
		}
		if (!isLoaded()) {
			ObjectResourceHolder[] arrayOut = null;

			//get all the .xml files in the directory
			getRp().setStatusString("finding " + typeName + " xml files for " + getName() + " looking at ...");
			File[] objectFiles = new File(getBaseFolder(), typeName).listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					getRp().printDebugLine("\t" + dir + "/" + name);
					return name.endsWith(".xml");
				}
			});
			if (typeName.equals(UNIT_FFOLDER_NAME)) {
				arrayOut = new UnitResourceHolder[objectFiles.length];
			} else if (typeName.equals(WEAPON_FOLDER_NAME)) {
				arrayOut = new WeaponResourceHolder[objectFiles.length];
			} else if (typeName.equals(TERRAIN_OBJECT_FOLDER_NAME)) {
				arrayOut = new TerrainObjectResourceHolder[objectFiles.length];
			}
			int j = 0;
			for (int i = 0; i < objectFiles.length; i++) {
				getRp().setStatusString("creating a " + typeName + " from xml. using ...");
				File xmlFile = objectFiles[i];
				getRp().setStatusString(xmlFile.getAbsolutePath());
				//rp.printDebugLine("checking existance of " + xmlLoc);
				//if (!xmlLoc.exists()){
				//rp.printDebugLine("                      " + xmlLoc + " exists!");
				try {
					if (typeName.equals(UNIT_FFOLDER_NAME)) {
						arrayOut[j] = new UnitResourceHolder(getRp(), getBuilder(), this, xmlFile);
					} else if (typeName.equals(WEAPON_FOLDER_NAME)) {
						arrayOut[j] = new WeaponResourceHolder(getRp(), getBuilder(), this, xmlFile);
					} else if (typeName.equals(TERRAIN_OBJECT_FOLDER_NAME)) {
						arrayOut[j] = new TerrainObjectResourceHolder(getRp(), getBuilder(), this, xmlFile);
					}
					j++;
					getRp().setStatusString("Created " + typeName);
				} catch (ResourceException e) {
					getRp().addError("Did not create " + typeName + "!");
				}
				//}else{
				//	rp.printDebugLine("too bad");
				//}
				getRp().printDebugLine();
			}
			return arrayOut;
		}
		return null;
	}
	
	private TerrainResource[] createTerrains() {
		TerrainResource[] out;
		//get all the valid terrain folders in the directory
		getRp().setStatusString("finding TerrainDescription xml files for " + getName() + " looking at ...");
		File[] terrainFolders = new File(getBaseFolder(), "Terrain").listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				getRp().printDebugLine("\t" + pathname + "/TerrainDescription.xml");
				return checkAccess(new File(pathname, "TerrainDescription.xml"));
			}
		});
		out = new TerrainResource[terrainFolders.length];
		for (int i = 0; i < terrainFolders.length; i++) {
			try {
				out[i] = new TerrainResource(getRp(), getBuilder(), this, new File(terrainFolders[i], "TerrainDescription.xml"));
			} catch (ResourceException ex) {
				getRp().addError("Did not load " + terrains[i].getName() + ": \n\t" + ex);
			}
		}
		return out;
	}

	public String getPath() {
		return getBaseFile().getAbsolutePath();
	}

	public UnitResourceHolder getUnitByName(String unitName) {
		for (int i = 0; i < units.length; i++) {
			if (units[i] != null && units[i].getName() != null && units[i].getName().equals(unitName)) {
				return units[i];
			}
		}
		return null;
	}

	public WeaponResourceHolder getWeaponByName(String weaponName) {
		for (int i = 0; i < weapons.length; i++) {
			if (weapons[i] != null && weaponName.equals(weapons[i].getName())) {
				return weapons[i];
			}
		}
		return null;
	}
	public TerrainResource getTerrainByName(String terrainName) {
		for (int i = 0; i < weapons.length; i++) {
			if (terrains[i] != null && terrainName.equals(terrains[i].getName())) {
				return terrains[i];
			}
		}
		return null;
	}		
	public boolean hasUnits() {
		return hasUnits;
	}

	public UnitResourceHolder[] getUnits() {
		return units;
	}

	@Override
	public String getQualifiedName() {
		return getName();
	}

	@Override
	public String createResourceName(File resourceFile) {
		return getFileNameWithoutExtension(resourceFile);
	}

	TerrainObjectResourceHolder[] getTerrainObjects() {
		return terrainObjects;
	}
}
