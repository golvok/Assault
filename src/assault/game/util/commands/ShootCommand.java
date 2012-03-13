/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.commands;

import assault.game.loading.ResourcePreloader;
import assault.game.gameObjects.AObject;
import assault.game.gameObjects.Unit;
import assault.game.gameObjects.Weapon;
import assault.game.util.GridObject;
import java.awt.Point;

/**
 *
 * @author 088241930
 */
public class ShootCommand extends Command implements MouseCommand, TargetCommand {

	Weapon weapon;
	int mointPoint = -1;

	private ShootCommand(String name, ResourcePreloader rp, char hotKey) {
		super(name, hotKey, rp.getCmdIcon(ResourcePreloader.MOVE_CMD_IMAGE_INDEX));
	}

	public ShootCommand(String name, ResourcePreloader rp, char hotKey, Weapon aw) {
		this(name, rp, hotKey);
		weapon = aw;
	}

	public ShootCommand(String name, ResourcePreloader rp, char hotKey, int mountPoint) {
		this(name, rp, hotKey);
		this.mointPoint = mountPoint;
	}

	@Override
	public void executeOn(AObject[] shooters, int x, int y) {
		GridObject target = shooters[0].getGA().getGM().getGoAtPixel(x, y);
		if (target instanceof AObject) {
			executeOn(shooters, (AObject) target);
		}
	}

	@Override
	public void executeOn(AObject[] shooters, AObject target) {
		if (target != null) {
			for (int i = 0; i < shooters.length; i++) {
				if (shooters[i] instanceof Unit) {
					if (weapon != null) {
						((Unit) shooters[i]).shootBulletAt(target, weapon);
					} else if (mointPoint >= 0) {
						((Unit) shooters[i]).shootBulletAt(target, mointPoint);
					}
				}
			}
		}
	}

	@Override
	public void executeOn(AObject[] aos, Point p) {
		if (p != null) {
			executeOn(aos, p.x, p.y);
		}
	}

	public Object getWeapon() {
		return weapon;
	}

	@Override
	public boolean equals(Object o) {
		if (super.equals(o)) {
			return true;
		}
		if (o instanceof ShootCommand) {
			ShootCommand sc = (ShootCommand) o;
			if (sc.getWeapon() == null) {
				if (getWeapon() == null) {
				}
			}
		}
		return false;
	}
}
