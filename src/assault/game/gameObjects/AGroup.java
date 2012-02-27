/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.gameObjects;

import assault.game.Player;
import assault.game.display.CommandDispatchMenu;
import assault.game.display.GameArea;
import assault.input.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.lwjgl.util.Color;

/**
 *
 * @author matt
 */
public class AGroup extends AControllable {

	private List<AControllable> controllables = Collections.synchronizedList(new ArrayList<AControllable>(5));
	private boolean drawBox = false;

	public AGroup(GameArea g, AControllable[] startControllables, Player owner) {
		super(g, owner);
		addControllables(startControllables);
		doNotShowStatus();
		doNotPaintCross();
		setCmdBtnSet(CommandDispatchMenu.getUniqueCmdBtnSet(controllables));
		resizeToControllables();
	}

	@Override
	public synchronized void dispose() {
		super.dispose();
	}
	
	public void addControllable(AControllable newControllable) {
		controllables.add(newControllable);
		newControllable.registerGroup(this);
		resizeToControllables();
		setCmdBtnSet(CommandDispatchMenu.getUniqueCmdBtnSet(controllables));
	}

	public void addControllables(AControllable[] newControllables) {
		for (int i = 0; i < newControllables.length; i++) {
			if (newControllables[i] != null) {
				controllables.add(newControllables[i]);
				newControllables[i].registerGroup(this);
			}
		}
		resizeToControllables();
		setCmdBtnSet(CommandDispatchMenu.getUniqueCmdBtnSet(controllables));
	}

	public void removeControllable(AControllable au) {
		controllables.remove(au);
		resizeToControllables();
	}

	public void removeControllables(AControllable[] aus) {
		for (int i = 0; i < aus.length; i++) {
			removeControllable(aus[i]);
		}
	}

	/*public void shootBulletAt(AObject target, AWeapon aw) {
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			AControllable AControllable = it.next();
			if (AControllable != null) {
				AControllable.shootBulletAt(target, aw);
			}
		}
	}*/

	/**
	 * using <code>setBounds()</code> adjust the X ,Y, width and Height so that
	 * this fits over the <code>AControllables</code> in <code>controllables</code>.
	 */
	public void resizeToControllables() {
		int GAP_SIZE = 10;
		int newWidth = 0;//<---ends up being min size
		int newHeight = 0;//<--|
		int newX = Integer.MAX_VALUE;
		int newY = Integer.MAX_VALUE;
		int uDim = 0;
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				// x
				uDim = controllable.getX();
				if (newX > uDim) {
					newX = uDim;
				}
				// y
				uDim = controllable.getY();
				if (newY > uDim) {
					newY = uDim;
				}
			}
		}
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				// width
				uDim = controllable.getX() + controllable.getWidth() - newX;
				if (newWidth < uDim) {
					newWidth = uDim;
				}
				// height
				uDim = controllable.getY() + controllable.getHeight() - newY;
				if (newHeight < uDim) {
					newHeight = uDim;
				}
			}
		}
		//System.out.println("setting bounds of AGroup to: \nX = "+(newX-GAP_SIZE/2)+" Y = "+(newY-GAP_SIZE/2)+" width = "+(newWidth+GAP_SIZE)+" height = "+(newHeight+GAP_SIZE));
		setBounds(newX - GAP_SIZE / 2, newY - GAP_SIZE / 2, newWidth + GAP_SIZE, newHeight + GAP_SIZE);
	}

	@Override
	public void drawSelf() {
		super.drawSelf();
		if (drawBox) {
			setColour(Color.BLACK);
			drawRect(-1, -1, getWidth()+1, getHeight()+1);
		}
	}

	public AControllable[] getControllables() {
		return controllables.toArray(new AControllable[controllables.size()]);
	}

	public Iterator<AControllable> getControllablesIterator() {
		return controllables.iterator();
	}

	@Override
	public void moveTo(int x, int y) {
		int aggx = getX();
		int aggy = getY();
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.moveTo(x + controllable.getX() - aggx, y + controllable.getY() - aggy);
			}
		}
	}

	/**
	 * adjust x coord by <code>x</code> and y coord by <code>y</code>. (they can be negative)
	 */
	@Override
	public void moveBy(int x, int y) {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.moveBy(x, y);
			}
		}
	}

	public void doNotPaintCrossOfControllables() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.doNotPaintCross();
			}
		}
	}

	public void doPaintCrossOfControllables() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.doPaintCross();
			}
		}
	}

	/**
	 * sets the <code>showStatus</code> flag of the <code>AControllables</code> in <code>controllables</code> to false and hides the status box
	 */
	public void doNotShowStatusofControllables() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.doNotShowStatus();
				controllable.hideStatusBox();
			}
		}
	}

	/**
	 * sets the <code>showStatus</code> flag of the <code>AControllables</code> in <code>controllables</code> to true
	 */
	public void doShowStatusOfControllables() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.doShowStatus();
			}
		}
	}

	/**
	 * creates(if needed) and displays the status box of the <code>AControllables</code> in <code>controllables</code> if <code>showstatus</code> flag is set
	 * NOTE: this method is not to be confused with <code>doShowStatus()</code> which just sets the <code>showStatus</code> flag to true
	 */
	@Override
	public void showStatusBox() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.showStatusBox();
			}
		}
	}

	/**
	 * hide the status box if this is not selected
	 * NOTE: this method is not to be confused with <code>doNotShowStatus()</code> which just sets the <code>showStatus</code> flag to false
	 */
	@Override
	public void hideStatusBox() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.hideStatusBox();
			}
		}
	}

	/**
	 * correct the position of the Status display box of the <code>AControllables</code> in <code>controllables</code>
	 */
	public void correctStatDispBoxPosOfControllables() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.correctStatDispBoxPos();
			}
		}
	}

	public boolean isOneOrMoreControllablesSelected() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null && controllable.isSelected()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void select() {
		deselectControllables();
		selectControllablesPseudo();
		super.select();
		//System.out.println("AG_SELECTED");
	}

	@Override
	public void deselect() {
		deselectControllablesPseudo();
		super.deselect();
	}

	@Override
	public void deselectPseudo() {
		deselectControllablesPseudo();
		super.deselectPseudo();
	}

	public void selectControllables() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.select();
			}
		}
	}

	public void selectControllablesPseudo() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.pseudoSelect();
			}
		}
	}

	public void deselectControllables() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.deselect();
			}
		}
	}

	public void deselectControllablesPseudo() {
		AControllable controllable;
		for (Iterator<AControllable> it = controllables.iterator(); it.hasNext();) {
			controllable = it.next();
			if (controllable != null) {
				controllable.deselectPseudo();
			}
		}
	}

	//====================Mouse Listeners========================
	@Override
	public void mousePressed(MouseEvent e) {
		if ((e.getButton() == 2 || e.getButton() == 3) && isOneOrMoreControllablesSelected()) {
			getGA().notifyOfMousePress(this, e);
		} else {
			super.mousePressed(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//System.out.println("OVER");
		super.mouseEntered(e);
		drawBox = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//System.out.println("OFF");
		super.mouseExited(e);
		drawBox = false;
	}
}
