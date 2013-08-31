/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.gameObjects;

import java.awt.Image;

import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

import assault.display.AssaultWindow;
import assault.display.Container;
import assault.display.Paintable;
import assault.game.Player;
import assault.game.display.GameArea;
import assault.game.display.StatusDisplayBox;
import assault.game.loading.resourceHolders.ObjectResourceHolder;
import assault.game.loading.resourceHolders.ResourceException;

/**
 *
 * @author matt
 */
public class AObject extends Container<Paintable> {

	public final static int CROSS_SIZE = 4;
	private Player owner;
	private int maxHealth = 1;
	private int health = 1;
	private boolean invincible = false;
	private boolean paintCross = true;
	private boolean showStatus = true;
	private StatusDisplayBox statDispBox = null;
	private ObjectResourceHolder src = null;
//	private final Image naturalImage;
	private boolean alive = true;
	private GameArea ga;
	private AssaultWindow aw;
//	private Image miniIcon = null;

	public AObject(GameArea g, float x, float y, ObjectResourceHolder src, int health, Player owner) throws ResourceException {
		this(g, x, y, src.getWidth(), src.getHeight(), health, src.getMaxHealth(), src.getMiniIcon(), src.getBaseImage(owner), owner);
		if (!src.isValid()) {
			throw new ResourceException("supplied resource holder for new object (" + src.getQualifiedName() + ") is invalid!");
		}
		this.src = src;
	}

	public AObject(GameArea g, float x, float y, ObjectResourceHolder src, Player owner) throws ResourceException {
		this(g, x, y, src, src.getMaxHealth(), owner);
	}

	public AObject(GameArea g, float x, float y, float width, float height, int maxHealth, Image miniIcon, Image naturalImage, Player owner) {
		this(g, x, y, width, height, maxHealth, maxHealth, miniIcon, naturalImage, owner);
	}

	public AObject(GameArea g, float x, float y, float width, float height, int health, int maxHealth, Image miniIcon, Image naturalImage, Player owner) {
		this(g, x, y, width, height, miniIcon, naturalImage, owner);
		invincible = false;
		setMaxHealth(maxHealth);
		setHealth(health);
	}

	public AObject(GameArea g, float x, float y, Image miniIcon, Image naturalImage, Player owner) {
		this(g, x, y, 0, 0, miniIcon, naturalImage, owner);
	}

	public AObject(GameArea g, float x, float y, float width, float height, Image miniIcon, Image naturalImage, Player owner) {
		super(x, y, width, height, 2);
		setOwner(owner);
		ga = g;
//		this.miniIcon = miniIcon;
//		this.naturalImage = naturalImage;
		invincible = true;//may be set again by other constructors
		statDispBox = new StatusDisplayBox(this);
        hideStatusBox();
        addChild(statDispBox);
		//System.out.println("AO_INIT");
	}

	//=======================END=CONSTRUCTORS========================
	@Override
	public synchronized void dispose() {
		if (!isDisposed()) {
			statDispBox.dispose();
			getGA().remove(this);
			//System.out.println("AO_DISPOSE");
		}
		super.dispose();
	}

	@Override
	public void drawSelf() {
		super.drawSelf();
		//g2.rotate(Math.PI/2, getWidth()/2, getHeight()/2);
		if (src != null) {
			drawTexture(0, 0, getWidth(), getHeight(), src.getBaseImageTexture(owner));
		}
		setColour(getDrawColour());
		if (paintCross) {
			drawLine((getWidth() - AObject.CROSS_SIZE) / 2, getHeight() / 2, (getWidth() + AObject.CROSS_SIZE) / 2, (getHeight()) / 2);
			drawLine(getWidth() / 2, (getHeight() - AObject.CROSS_SIZE) / 2, getWidth() / 2, (getHeight() + AObject.CROSS_SIZE) / 2);
		}
		//System.out.println("AO_PAINT");
	}

	private void setOwner(Player owner) {
		if (owner != null) {
			this.owner = owner;
		} else {
			System.out.println("owner specified was null. creating new owner (colour=black)");
			this.owner = new Player(Color.BLACK);
		}
	}

	/**
	 * 
	 * @param newHealth
	 * @return true if dead
	 */
	private boolean setHealth(int newHealth) {
		if (newHealth > 0) {
			health = newHealth;
			return false;
		} else {
			health = 0;
			return true;
		}
	}

	private void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	/**
	 * 
	 * @param amount
	 * @return true if the damage was successful
	 */
	protected boolean damage(int amount) {
		if (isAlive() && !invincible) {
			if (setHealth(getHealth() - amount)) {
				kill();
			}
			return true;
		}
		return false;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	protected synchronized boolean kill() {
		if (alive) {
			alive = false;
			hideStatusBox();
			dispose();
			return true;
		}
		return false;
	}

	public Player getOwner() {
		return owner;
	}

	/**
	 */
	public GameArea getGA() {
		return ga;
	}

	/**
	 */
	public AssaultWindow getAW() {
		return aw;
	}

	public ReadableColor getDrawColour() {
		return getOwner().getColour();
	}

	public void doNotPaintCross() {
		paintCross = false;
	}

	public void doPaintCross() {
		paintCross = true;
	}

	public ObjectResourceHolder getSrc() {
		return src;
	}

	public boolean isAlive() {
		return alive;
	}

	@Override
	public String toString() {
		return (getSrc() != null ? getSrc().getQualifiedName() : getClass().getSimpleName()) + ", " + super.toString();
	}

	//====================Mouse Listeners========================

	/**
	 * sets the <code>showStatus</code> flag to false and hides the status box
	 */
	public void doNotShowStatus() {
		showStatus = false;
		hideStatusBox();
	}

	/**
	 * sets the <code>showStatus</code> flag to true
	 */
	public void doShowStatus() {
		showStatus = true;
	}

	/**
	 * hide the status box if <code>selected == false</code>
	 * NOTE: this method is not to be confused with <code>doNotShowStatus()</code>
	 * which just sets the <code>showStatus</code> flag to false
	 */
	public void hideStatusBox() {
		if (statDispBox != null) {
			statDispBox.setVisible(false);
		}
	}

	/**
	 * creates(if needed) and displays the status box if <code>showstatus</code> flag is set
	 * NOTE: this method is not to be confused with <code>doShowStatus()</code> which just sets the <code>showStatus</code> flag to true
	 */
	public void showStatusBox() {
		if (showStatus && statDispBox != null) {
			statDispBox.setVisible(true);
		}
		//System.out.println("AO_SHOW_STATUS"+showStatus+this);
	}
}
