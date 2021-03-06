/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.gameObjects;

import assault.game.Player;
import assault.game.display.GameArea;
import assault.game.loading.resourceHolders.ResourceException;
import assault.game.loading.resourceHolders.WeaponResourceHolder;

/**
 *
 * @author matt
 */
public class Weapon extends Controllable{

	int bulletSize = 0;
	boolean isProjectileWeapon = false;
//	private int rotateX;
//	private int rotateY;

	public Weapon(GameArea g,WeaponResourceHolder src, Player ownerPlayer) throws ResourceException {
		this(g,0, 0, src, ownerPlayer);
		setVisible(true);
	}

	public Weapon(GameArea g, float x, float y, WeaponResourceHolder src, Player ownerPlayer) throws ResourceException {
		super(g, x, y, src,1, ownerPlayer);
//		System.out.println("creating weapon: " + this);
		doNotPaintCross();
		doNotShowStatus();
		isProjectileWeapon = src.isProjectile();
		bulletSize = src.getBulletSize();
//		rotateX = src.getRotatePointX();
//		rotateY = src.getRotatePointY();
		setVisible(true);
	}

	@Override
	public synchronized void dispose() {
		super.dispose();
	}

	/*
	 * return true if this shot damage()'d target, except
	 * in the case where this is a projectile weapon. In
	 * that case, it allways returns false.
	 */
	public boolean shoot(AObject target) {
		if (target != null) {
			if (target.isAlive()) {
				System.out.println("Shooting: " + target);
				if (isProjectileWeapon) {
					Projectile proj = new Projectile(getGA(),getX(), getY(), target.getX(), target.getY(), bulletSize, null, null, getOwner());
					getGA().add(proj);
					proj.shoot();
					return false;
				} else {
					if (target.damage(bulletSize)) {
						new BulletHitObject(target);
						return true;
					}
				}
			} else {
				System.out.println("the target was allready dead");
			}
		} else {
			System.out.println("target was null. Canceled?");
		}
		return false;
	}

	@Override
	public  void drawSelf() {
		super.drawSelf();
		drawBoundingBox(this);
	}

    @Override
    public float getMovementSpeed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


	private class BulletHitObject {

//		private Timer repaintTimer;
//		private boolean isDone = false;
//		private int[] bullethitLocsX = new int[7];
//		private int[] bullethitLocsY = new int[7];

		public BulletHitObject(AObject target) {
			/*super();
			setOpaque(false);
			setSize(target.getSize());
			target.add(this);
			repaint();
			repaintTimer = new Timer();
			repaintTimer.scheduleAtFixedRate(new TimerTask() {

				int i = 0;

				@Override
				public void run() {
					if (i < 5) {
						i++;
						changeHitBulletLocs();
						repaint();
					} else {
						repaintTimer.cancel();
						isDone = true;
						repaint();
						getParent().repaint();
						removeFromParent();
					}
				}
			}, 0, 500);
			//System.out.println("BHO_INIT");*/
		}

		/*;
		private void removeFromParent() {
			getParent().remove(this);
		}

		private void changeHitBulletLocs() {
			for (int i = 0; i < 7; i++) {
				bullethitLocsX[i] = (int) Math.round(Math.random() * getWidth());
				bullethitLocsY[i] = (int) Math.round(Math.random() * getHeight());
			}

		}

		@Override
		protected void drawSelf(GL11 g) {
			super.paintComponent(g);
			if (!isDone) {
				g.setColor(Color.BLACK);
				for (int i = 0; i < 7; i++) {
					g.drawLine(bullethitLocsX[i], bullethitLocsY[i], bullethitLocsX[i], bullethitLocsY[i]);
				}
			}
			//System.out.println("BHO_PAINT");
		}*/
	}
}