/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import java.io.File;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import assault.game.Player;
import assault.game.display.GameArea;
import assault.game.gameObjects.Weapon;
import assault.game.loading.ResourcePreloader;
import assault.util.Point;

/**
 *
 * @author 088241930
 */
public class WeaponResourceHolder extends ControllableResourceHolder {

	final static String WEAPON_TYPE_NAME = "Weapon";
	private int bulletSize;
	private boolean projectile;
	private int rotatePointX;
	private int rotatePointY;

	public WeaponResourceHolder(ResourcePreloader rp, SAXBuilder builder, ModResourceHolder mod, File xmlFile) throws BadlyFormattedXMLException, ResourceException {
		super(rp, builder, mod, xmlFile);
	}

	@Override
	public void parseXmlValues() throws BadlyFormattedXMLException, ResourceException {
		setNoClip(true);//override the default value of noclip
		super.parseXmlValues();
		
		getRp().setStatusString("loading weapon values in " + getBaseFile());
		try {
			//rotation point
			Element rpe = getRootE().getChild("rotatePoint");
			rotatePointX = rpe.getAttribute("x").getIntValue();
			rotatePointY = rpe.getAttribute("y").getIntValue();

			//projectile
			projectile = getRootE().getChild("projectile").getAttribute("val").getBooleanValue();

			//size
			bulletSize = getRootE().getChild("size").getAttribute("val").getIntValue();
		} catch (DataConversionException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName(), ex, getBaseFile());
		} catch (NullPointerException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName(), ex, getBaseFile(), getRootE());
		}
		getRp().setStatusString("loaded weapon values in " + getBaseFile());
	}

	@Override
	public Weapon createObject(GameArea g, float x, float y, Player owner) throws ResourceException {
		return new Weapon(g, x, y, this, owner);
	}

	@Override
	public Weapon createObject(GameArea g, Point p, Player owner) throws ResourceException {
		return createObject(g, p.getX(), p.getY(), owner);
	}

	public int getBulletSize() {
		return bulletSize;
	}

	public boolean isProjectile() {
		return projectile;
	}

	public int getRotatePointX() {
		return rotatePointX;
	}

	public int getRotatePointY() {
		return rotatePointY;
	}
}
