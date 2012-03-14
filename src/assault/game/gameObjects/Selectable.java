/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.gameObjects;

import assault.game.Player;
import assault.game.display.GameArea;
import assault.game.loading.resourceHolders.ObjectResourceHolder;
import assault.game.loading.resourceHolders.ResourceException;
import assault.input.MouseEvent;
import assault.input.MouseListener;

/**
 *
 * @author matt
 */
public class Selectable extends AObject implements MouseListener {

	private boolean selected = false;

	public Selectable(GameArea g, double x, double y, ObjectResourceHolder src, int health, Player owner) throws ResourceException {
		super(g, x, y, src, owner);
	}

	@Override
	public synchronized void dispose() {
		super.dispose();
	}
	
	/**
	 * selects fully/normally
	 * <p>
	 * function body:
	 * <p>
	 *	select(false);
	 *  @see AObject#deselect(boolean)
	 */
	public void select() {
		select(false);
	}

	/**
	 * pseudo-selects
	 * <p>
	 * function body:
	 * <p>
	 *	deselect(true);
	 *  @see AObject#select(boolean)
	 */
	public void pseudoSelect() {
		select(true);
	}

	/**
	 * If not already selected, selects this <code>AObject</code>.
	 * If false is passed it selects it fully. But if true is
	 * passed this <code>AObject</code> only <i>thinks</i> it
	 * is selected (it is pseudo-selected). The AMP will not
	 * have it in its selection and its numSelected variable
	 * will not be affected. Note that this won't receive commands
	 * from the AMP.
	 * <p>
	 * selects by:<br>
	 * - selected = true;<br>
	 * - showing the status box
	 * @param pseudo weather or not to pseudo-select
	 */
	public void select(boolean pseudo) {
		//System.out.println(this+": selected = "+selected+" @select(boolean)");
		boolean success = true; //starting it true will cause this to do something still if pseudo == true.
		//starting it true will cause this to do something still if pseudo == true.
		if (!selected) {
			if (!pseudo) {
				success = getGA().addToSelection(this);
				//System.out.println("AO_SELECT");
			} //else{
			//System.out.println("AO_PSELECT");
			//}
			if (success) {
				selected = true;
				showStatusBox();
				//System.out.println("AO_SELECTED");
			}
		}
	}

	/**
	 * it is not advised to call this from the AMP because strange things may happen (this calls deselectAll()).
	 */
	public void selectOnlyThis() {
		getGA().deselectAll();
		select();
	}

	/**
	 * deselects fully/normally
	 * function body:
	 * <p>
	 *	deselect(false);
	 *  @see AObject#deselect(boolean)
	 */
	public void deselect() {
		deselect(false);
	}

	/**
	 * pseudo-deselects
	 * function body:
	 * <p>
	 *	deselect(true);
	 *  @see AObject#deselect(boolean)
	 */
	public void deselectPseudo() {
		deselect(true);
	}

	/**
	 * If not already deselected, deselects this <code>AObject</code>. If false is passed it deselects it fully.
	 * but if true is passed this <code>AObject</code> only <i>thinks</i> it
	 * is deselected (it is pseudo-deselected). The AMP still has it in its selection and its numSelected
	 * variable is not affected. Note that this still could receive commands from the AMP.
	 * <p>
	 * deselects by:<br>
	 * - selected = false;<br>
	 * - hiding the status box
	 * @param pseudo weather or not to pseudo-deselect
	 */
	public void deselect(boolean pseudo) {
		if (selected) {
			if (!pseudo) {
				getGA().removeFromSelection(this);
				//System.out.println(getClass()+" AO_DESELECT");
				//System.out.println(getClass()+" AO_DESELECT");
				//System.out.println(getClass()+" AO_DESELECT");
				//System.out.println(getClass()+" AO_DESELECT");
				//System.out.println(getClass()+" AO_DESELECT");
			} //else{
			//System.out.println(getClass()+" AO_PDESELECT");
			//}
			selected = false;
			hideStatusBox();
		}
	}

	public boolean isSelected() {
		return selected;
	}

	@Override
	public void hideStatusBox() {
		//System.out.println("selected = "+selected+" @hideStatusBox()");
		//the !isSelected is necessary-what if the mouse exits while this is selected?
		if (!selected) {
			super.hideStatusBox();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		/*if (e.getClickCount() == 2) {
		System.out.println(this);
		} else {*/
		if (e.getButton() == MouseEvent.BUTTON_LEFT) {
			if (e.isLShiftDown()) {
				select();
				//System.out.println(this+" was Shift-Selected\n");
			} else if (e.isLControlDown()) {
				deselect();
				//System.out.println(this+" was Ctrl-Deselected\n");
			} else {
				selectOnlyThis();
				//System.out.println(this+" was Only-Selected\n");
			}
		} else if (!isSelected() && ((e.getButton() == MouseEvent.BUTTON_RIGHT) || (e.getButton() == MouseEvent.BUTTON_MIDDLE))) {
			System.out.println(this + " received a targetive mousepress while not selected (button " + e.getButton() + "). Notifying AMP");
			getGA().notifyOfTargetiveMousePress(this);
		} else {
			getGA().notifyOfMousePress(this, e);
		}
		//}
		//System.out.println("AO_PRESSED");
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//System.out.println("AO_RELEASED");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		showStatusBox();
		//System.out.println("AO_ENTERED");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		hideStatusBox();
		//System.out.println("AO_EXITED");
	}
}
