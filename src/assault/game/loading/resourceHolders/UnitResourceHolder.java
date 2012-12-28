/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import assault.game.Player;
import assault.game.display.CommandButton;
import assault.game.display.GameArea;
import assault.game.gameObjects.Unit;
import assault.game.loading.ResourcePreloader;
import assault.game.util.commands.Command;
import assault.game.util.commands.ShootCommand;
import assault.util.Point;
import java.io.File;
import java.util.List;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author matt
 */
public class UnitResourceHolder extends ControllableResourceHolder {

	public static final String UNIT_TYPE_NAME = "Unit";
	private Point CreatePoint;
	private Point[] mountPts = {};
	private String[] speedNames = {};
	private double[] speeds = {};
	private WeaponResourceHolder[] weapons = {};

	/**
	 *
	 * @param mod it's parent mod
	 * @param xml it's name
	 * @param rp it's parent resourcePreloader
	 * @param builder a SAXbuilder to use
	 */
	public UnitResourceHolder(ResourcePreloader rp, SAXBuilder builder, ModResourceHolder mod, File xmlFile) throws ResourceException {
		super(rp, builder, mod, xmlFile);
		rp.setStatusString("created unit at " + getBaseFile());
	}

	@Override
	protected void parseXmlValues() throws BadlyFormattedXMLException, ResourceException {
		super.parseXmlValues();
		getRp().setStatusString("loading the unit values in " + getBaseFile());
		try {
			//create Point
			Element cpElement = getRootE().getChild("createPoint");
			CreatePoint = new Point(cpElement.getAttribute("x").getIntValue(), cpElement.getAttribute("y").getIntValue());

			//mount points
			Element[] mntPtsElements = {};
			mntPtsElements = (Element[]) getRootE().getChild("mountPoints").getChildren("p").toArray(mntPtsElements);
			int higestMPINdex = 0;
			int mpIndex;
			for (int i = 0; i < mntPtsElements.length; i++) {
				mpIndex = mntPtsElements[i].getAttribute("i").getIntValue();
				if (mpIndex > higestMPINdex) {
					higestMPINdex = mpIndex;
				} else if (mpIndex < 0) {
					mntPtsElements[i] = null;
					getRp().addError("The mountpoint with a negative index in " + getQualifiedName() + " was ignored");
				}
			}
			mountPts = new Point[higestMPINdex + 1];
			for (int i = 0; i < mntPtsElements.length; i++) {
				//no need for bounds checking, allready done
				mountPts[mntPtsElements[i].getAttribute("i").getIntValue()] = new Point(mntPtsElements[i].getAttribute("x").getIntValue(), mntPtsElements[i].getAttribute("y").getIntValue());
			}

			//speeds
			Element speedsElement = getRootE().getChild("speeds");
			if (getRootE().getChild("noSpeeds") == null || speedsElement != null) {
				List<Element> speedElements = speedsElement.getChildren();
				int numSpeeds = speedElements.size();
				speedNames = new String[numSpeeds];
				speeds = new double[numSpeeds];
				for (int i = 0; i < numSpeeds; i++) {
					Element speed = speedElements.get(i);
					speedNames[i] = speed.getName();
					speeds[i] = speed.getAttribute("val").getDoubleValue();
				}
			}

			//weapons
			if (getRootE().getChild("noWeapons") == null) {
				Element[] weaponElements = {};
				weaponElements = (Element[]) getRootE().getChild("weapons").getChildren("weapon").toArray(weaponElements);
				weapons = new WeaponResourceHolder[mountPts.length];
				String weaponName;
				String modName;
				int mountPointIndex;
				for (int i = 0; i < weaponElements.length; i++) {
					mountPointIndex = weaponElements[i].getAttribute("mountpoint").getIntValue();
					weaponName = weaponElements[i].getAttributeValue("name");
					modName = weaponElements[i].getAttributeValue("mod");
					if (mountPointIndex >= 0 && mountPointIndex < mountPts.length) {
						try {
							weapons[mountPointIndex] = getRp().getModWeaponByName(modName, weaponName);
						} catch (ResourceException e) {
							getRp().addError(getQualifiedName() + " tried to add a " + modName + "." + weaponName + " weapon, which doesn't exist Message:\n\t" + e);
						}
					} else {
						getRp().addError(getQualifiedName() + " tried to add a " + modName + "." + weaponName + " weapon to the invalid (OOBounds or null) mountpoint index, " + mountPointIndex);
					}
				}
			}
		} catch (DataConversionException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName(), ex, getBaseFile());
		} catch (NullPointerException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName(), ex, getBaseFile(), getRootE());
		}
		getRp().setStatusString("loaded unit values in " + getBaseFile());
	}

	@Override
	protected CommandButton parseXmlButton(Element xmlBtn) throws BadlyFormattedXMLException {
		BadlyFormattedXMLException superException = null;
		try {
			CommandButton superB = super.parseXmlButton(xmlBtn);
			if (superB != null) {
				return superB;
			}
		} catch (BadlyFormattedXMLException e) {
			superException = e;
			//continue normally, try to parse it with this method
		}
		String cmdType;
		Element cmdE;
		Command cmd = null;
		//TODO add functionality for complex commands
		try {
			cmdE = xmlBtn.getChild("action").getChild("command");
			cmdType = cmdE.getAttributeValue("type");
			if (cmdType.equals("Shoot")) {
				char hotkey = cmdE.getChildText("hotKey").charAt(0);
				int mountPoint = cmdE.getChild("mountPoint").getAttribute("val").getIntValue();
				if (mountPoint < 0) {
					throw new BadlyFormattedXMLException("the specified mountpoint for a button in " + getQualifiedName() + "(" + mountPoint + ") is negative");
				}
				cmd = new ShootCommand(cmdE.getChildText("name"), getRp(), hotkey, mountPoint);
			} else {
				if (superException != null && cmdType.equals("Create")) {
					throw superException;
				} else {
					throw new BadlyFormattedXMLException("unrecognised command : " + cmdType);
				}
			}
		} catch (DataConversionException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName() + ", parseXmlButton()", "could not convert from a string properly. Check button xml.");
		} catch (NullPointerException ex) {
			if (xmlBtn == null) {
				throw new BadlyFormattedXMLException(getQualifiedName(), "a xmlButton supplied was null");
			} else if (xmlBtn.getChild("action") == null) {
				throw new BadlyFormattedXMLException(getQualifiedName(), "an \"action\" element in a supplied xmlButton was null");
			} else if (xmlBtn.getChild("action").getChild("command") == null) {
				throw new BadlyFormattedXMLException(getQualifiedName(), "a \"command\" element in a supplied xmlButton was null");
			} else if (xmlBtn.getChild("action").getChild("command").getChildText("name") == null) {
				throw new BadlyFormattedXMLException(getQualifiedName(), "a \"name\" element in a supplied xmlButton was null");
			} else {
				throw new BadlyFormattedXMLException(getQualifiedName(), "a xmlButton supplied was improperly formatted Message : " + ex.getStackTrace()[0]);
			}
		}
		if (cmd != null) {
			return new CommandButton(cmd);
		} else {
			return null;
		}
	}

	@Override
	public Unit createObject(GameArea g, double x, double y, Player owner) {
		try {
			return new Unit(g, x, y, this, owner);
		} catch (ResourceException ex) {
			getRp().addError(ex);
			return null;
		}
	}

	@Override
	public Unit createObject(GameArea g, Point p, Player owner) {
		return createObject(g, p.getX(), p.getY(), owner);
	}

	/**
	 * remember this point is a <i>relative<i/> point
	 * @return 
	 */
	public Point getCreatePoint() {
		return new Point(CreatePoint);
	}

	public Point[] getMountPoints() {
		Point[] temp = new Point[mountPts.length];
		for (int i = 0; i < temp.length; i++) {
			if (mountPts[i] != null) {
				temp[i] = new Point(mountPts[i]);
			} else {
				temp[i] = null;
			}
		}
		return temp;
	}

	/**
	 * -1 if speed doesn't exist
	 * @param type
	 * @return 
	 */
	public double getSpeed(String type) {
		for (int i = 0; i < speedNames.length; i++) {
			if (speedNames[i].equals(type)) {
				return speeds[i];
			}
		}
		return -1;
	}

	public String[] getSpeedNames() {
		String[] temp = new String[speedNames.length];
		System.arraycopy(speedNames, 0, temp, 0, speedNames.length);
		return temp;
	}

	public double[] getSpeeds() {
		double[] temp = new double[speeds.length];
		System.arraycopy(speeds, 0, temp, 0, speeds.length);
		return temp;
	}

	public WeaponResourceHolder[] getWeapons() {
		WeaponResourceHolder[] temp = new WeaponResourceHolder[weapons.length];
		System.arraycopy(weapons, 0, temp, 0, weapons.length);
		return temp;
	}
}
