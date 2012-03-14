/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading;

import assault.display.Paintable;
import assault.game.loading.resourceHolders.*;
import assault.util.Disposable;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author matt
 */
public class ResourcePreloader extends Paintable implements Disposable{

	private BufferedImage[] commandImages = new BufferedImage[6];
	//if any more of these are added make sure there is room in the array for them
	public static final int MOVE_CMD_IMAGE_INDEX = 0;
	public static final int CREATE_CMD_IMAGE_INDEX = 1;
	private String statusString = "";
	private ArrayList<Exception> errors = new ArrayList<Exception>(10);
	private ModResourceHolder[] mods;
	private String[] modNames;
	private SAXBuilder builder;
	private boolean started = false;

	public ResourcePreloader(String[] mods, double w, double h) {
		super(0, 0, w, h);
		modNames = mods;
	}

	/**
	 * starts the preloader <b>in this thread</b>; blocks until completed
	 */
	public void start() {
		if (!started) {
			started = true;
			try {
				setStatusString("initializing preLoader");
				mods = new ModResourceHolder[modNames.length];
				builder = new SAXBuilder();

				setStatusString("loading command icons...");
				commandImages[MOVE_CMD_IMAGE_INDEX] = ImageIO.read(getClass().getResourceAsStream("/assault/game/util/commands/imagesUsedForIcons/" + "Move" + "Cmd_Icon.png"));

				commandImages[CREATE_CMD_IMAGE_INDEX] = ImageIO.read(getClass().getResourceAsStream("/assault/game/util/commands/imagesUsedForIcons/" + "Create" + "Cmd_Icon.png"));

			} catch (IOException ex) {
				addError("trouble accessing cmdIcon images");
			}

			setStatusString("creating mods...");
			for (int i = 0; i < mods.length; i++) {
				try {
					this.mods[i] = new ModResourceHolder(modNames[i], this, builder);
				} catch (ResourceException ex) {
					addError("did not create " + modNames[i] + " mod!\n\tMessage : " + ex.getReason());
				}
			}

			setStatusString("loading mods...");
			for (int i = 0; i < mods.length; i++) {
				if (mods[i] != null) {
					try {
						mods[i].load();
					} catch (ResourceException ex) {
						addError("did not load " + modNames[i] + " mod!\n\tMessage : " + ex.getReason());
					}
				}
			}
			for (int i = 0; i < mods.length; i++) {
				if (mods[i] != null) {
					try {
						mods[i].loadReferencial();
					} catch (ResourceException ex) {
						addError("did not load " + modNames[i] + " mod!\n\tMessage : " + ex.getReason());
					}
				}
			}
			setStatusString("done loading!");
			//setStatusString("");

			/*if (getParent() != null) {
			getParent().remove(this);
			}*/
		}
	}

	public void setStatusString(String status) {
		if (status != null) {
			statusString = status;
			System.out.println(status);
		}
		//repaint(30, 0, 20, getWidth());
	}

	public SAXBuilder getBuilder() {
		return builder;
	}

	public ModResourceHolder getModByName(String name) throws ResourceException {
		for (int i = 0; i < mods.length; i++) {
			if (mods[i] != null && mods[i].getName().equals(name)) {
				return mods[i];
			}
		}
		throw new ResourceException("getModByName: the " + name + " mod doesn't exist");
	}

	public UnitResourceHolder getModUnitByName(String modName, String unitName) throws ResourceException {
		UnitResourceHolder unit = getModByName(modName).getUnitByName(unitName);
		if (unit != null) {
			return unit;
		} else {
			throw new ResourceException("getModUnitByName: " + modName + "." + unitName + " doesn't exist");
		}
	}

	public WeaponResourceHolder getModWeaponByName(String modName, String weaponName) throws ResourceException {
		WeaponResourceHolder weapon = getModByName(modName).getWeaponByName(weaponName);
		if (weapon != null) {
			return weapon;
		} else {
			throw new ResourceException("getModWeaponByName: " + modName + "." + weaponName + " doesn't exist");
		}
	}

    public TerrainObjectResourceHolder getModTerrainObjectByName(String modName, String terrainOName) throws ResourceException {
		TerrainObjectResourceHolder terrain = getModByName(modName).getTerrainObjectByName(terrainOName);
		if (terrain != null) {
			return terrain;
		} else {
			throw new ResourceException("getModTerrainObjectByName: " + modName + "." + terrainOName + " doesn't exist");
		}
    }

	public TerrainResource getModTerrainByName(String modName, String terrainName) throws ResourceException {
		TerrainResource terrain = getModByName(modName).getTerrainByName(terrainName);
		if (terrain != null) {
			return terrain;
		} else {
			throw new ResourceException("getModTerrainByName: " + modName + "." + terrainName + " doesn't exist");
		}
	}

	public BufferedImage getCmdIcon(int index) {
		if (index >= 0 && index < commandImages.length) {
			return commandImages[index];
		} else {
			return null;
		}
	}

	@Override
	public void drawSelf() {
		//drawString(statusString, 0, 50);
		//System.out.println("RP_PAINT");
	}

	public synchronized void addError(String message) {
		addError(new ResourceException(message));
	}

	public void addError(ResourceException e) {
		System.out.println("  !!!!  " + e.getReason());
		errors.add(e);
	}

	public void printDebugLine() {
		printDebugLine("");
	}

	public void printDebugLine(String s) {
		System.out.println(s);
	}
	public static BufferedImage loadImage(String path){
		try {
			return ImageIO.read(new File(path));
		} catch (IOException ex) {
			System.out.println("problem loading image at "+path);
		}
		return null;
	}

	@Override
	public void dispose() {
		super.dispose();
	}
    
}
