/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.gameObjects;

import assault.game.Player;
import assault.game.display.GameArea;
import assault.game.loading.resourceHolders.ResourceException;
import assault.game.loading.resourceHolders.UnitResourceHolder;
import assault.game.loading.resourceHolders.WeaponResourceHolder;
import java.awt.Point;

/**
 *
 * @author matt
 */
public class AUnit extends AControllable {

	private Point createPoint = new Point(0, 0);
	private Point[] mountPoints;
	private AWeapon[] weapons;

	public AUnit(GameArea g, int x, int y, UnitResourceHolder src, Player owner) throws ResourceException {
		super(g, x, y, src, 5, owner);
		setCreatePoint(src.getCreatePoint());
		setCmdBtnSet(src.getCmdBtns());
		setMountPoints(src.getMountPoints());
		WeaponResourceHolder[] weaponsSources = src.getWeapons();
		for (int i = 0; i < weaponsSources.length; i++) {
			if (getMountPoint(i) != null && weaponsSources[i] != null) {
				equipWeapon(new AWeapon(getGA(), weaponsSources[i], owner), i);
			}
		}
	}

	public AUnit(GameArea g, Player owner) throws ResourceException {
		super(g, 0, 0, null, 0, owner);
	}

	@Override
	public void addSubPartsToGA() {
		super.addSubPartsToGA();
		if (getGA() != null) {
			if (weapons != null) {
				for (int i = 0; i < weapons.length; i++) {
					getGA().add(weapons[i], false);
				}
			}
		}
	}

	@Override
	public void updateSubPartsPosition() {
		super.updateSubPartsPosition();
		if (weapons != null) {
			for (int i = 0; i < weapons.length; i++) {
				if (weapons[i] != null) {
					weapons[i].refreshPosition();
				}
			}
		}
	}

	protected final boolean equipWeapon(AWeapon aw, int mountNum) {
		//System.out.println("AUNIT_EQUIP_WEAPON");
		if (mountNum < weapons.length && mountNum >= 0 && hasWeapon(aw) < 0 && aw != null) {
			if (aw.mountOn(this, mountNum)) {
				weapons[mountNum] = aw;
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private void removeWeapon(int mountNum) {
		if (mountNum < weapons.length && mountNum >= 0) {
			weapons[mountNum].dispose();
			weapons[mountNum] = null;
		}
	}

	public final AWeapon getWeapon(int mountNum) {
		if (mountNum < weapons.length && mountNum >= 0) {
			return weapons[mountNum];
		}
		return null;
	}

	/**
	 * the mount number of that weapon or -1 if this
	 * AUint doesn't have it or aw is null. 
	 * @param aw
	 * @return 
	 */
	public final int hasWeapon(AWeapon aw) {
		if (aw != null) {
			for (int i = 0; i < weapons.length; i++) {
				if (weapons[i] == aw) {
					return i;
				}
			}
		}
		return -1;
	}

	final Point getMountPoint(int number) {
		if (number >= 0 && number < mountPoints.length && mountPoints[number] != null) {
			return new Point(mountPoints[number]);
		} else {
			return null;
		}
	}

	@Override
	public void drawSelf() {
		super.drawSelf();
		drawRect(0, 0, getWidth(), getHeight());
		//System.out.println("AU_PAINT");
	}

	public void shootBulletAt(AObject target, AWeapon aw) {
		if (hasWeapon(aw) >= 0) {
			aw.shoot(target);
		}
	}

	public void shootBulletAt(AObject target, int mountNum) {
		if (mountNum < weapons.length && mountNum >= 0 && weapons[mountNum] != null) {
			weapons[mountNum].shoot(target);
		}
	}

	private void setMountPoints(Point[] pts) {
		mountPoints = new Point[pts.length];
		for (int i = 0; i < pts.length; i++) {
			if (pts[i] == null) {
				continue;//keep it null
			} else {
				mountPoints[i] = new Point(pts[i]);
			}
		}
		if (weapons != null) {
			AWeapon[] tempW = new AWeapon[pts.length];
			System.arraycopy(weapons, 0, tempW, 0, tempW.length);
			weapons = tempW;
		} else {
			weapons = new AWeapon[pts.length];
		}
	}

	/**
	 * will remove this from parent and take care of any duties to remove it completely.
	 */
	@Override
	public synchronized void dispose() {
		if (!isDisposed()) {
			removeFromGroup();
			for (int i = 0; i < weapons.length; i++) {
				if (weapons[i] != null) {
					weapons[i].dispose();
				}
			}
			//System.out.println("AU_DISPOSE");
		}
		super.dispose();
	}

	/**
	 * get the point to create new AObjects
	 * This one IS NOT relative!
	 * @return 
	 */
	public Point getCreatePointAbs() {
		return new Point(createPoint.x + getX(), createPoint.y + getY());
	}

	/**
	 * get the point to create new AObjects
	 * This one IS relative!
	 * @return 
	 */
	public Point getCreatePointRel() {
		return new Point(createPoint.x + getX(), createPoint.y + getY());
	}

	/**
	 * set the point <i>relative</i> to the origin of this AObject,
	 * where new AObjects will be created. 
	 * This method calculates the actual create point
	 * from the relative one given.
	 * @param x a relative x
	 * @param y a relative y
	 */
	private void setCreatePoint(int x, int y) {
		createPoint.x = x;
		createPoint.y = y;
	}

	/**
	 * set the point <i>relative</i> to the origin of this AObject,
	 * where new AObjects will be created.
	 * @see setCreatePoint(int,int)
	 * @param p a point relative to this AU's origin
	 */
	private void setCreatePoint(Point p) {
		setCreatePoint(p.x, p.y);
	}

	@Override
	public UnitResourceHolder getSrc() {
		return (UnitResourceHolder) super.getSrc();
	}
}
