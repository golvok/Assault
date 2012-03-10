/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.display;

import assault.input.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author matt
 */
public class InputRegistarContainer extends AContainer implements InputRegistar, MouseListener, KeyboardListener {

	private ArrayList<APaintable> mouseListeners = new ArrayList<APaintable>();
	private ArrayList<APaintable> keyListeners = new ArrayList<APaintable>();

	public InputRegistarContainer(int x, int y, int width, int height, ArrayList<APaintable> startchildren) {
		super(x, y, width, height, startchildren);
	}

	public InputRegistarContainer(int x, int y, int width, int height, int startSize) {
		super(x, y, width, height, startSize);
	}

	@Override
	public void addMouseEventReciever(APaintable ml) {
		if (ml instanceof MouseListener){
			mouseListeners.add(ml);
		}else{
			throw new InvalidParameterException(ml+"is not a Mouselistener");
		}
	}

	@Override
	public void removeMouseEventReciever(APaintable ml) {
		mouseListeners.remove(ml);
	}

	@Override
	public void addKeyboardEventReciever(APaintable kl){
		if (kl instanceof KeyboardListener){
			keyListeners.add(kl);
		}else{
			throw new InvalidParameterException(kl+"is not a Keyboardlistener");
		}
	}

	@Override
	public void removeKeyboardEventReciever(APaintable kl) {
		keyListeners.remove(kl);
	}

	@Override
	protected void removeChild(APaintable ap) {
		super.removeChild(ap);
		removeMouseEventReciever(ap);
		removeKeyboardEventReciever(ap);
	}

	@Override
	public void accept(MouseEvent me) {
		for (Iterator<APaintable> it = mouseListeners.iterator(); it.hasNext();) {
			APaintable ap = it.next();
			if (ap != null && me.intersects(ap.getBounds())){
				InputEventUtil.passAndTranslateMouseEventTo(ap, me);
			}
		}
	}

	@Override
	public void accept(KeyboardEvent ke) {
		for (Iterator<APaintable> it = keyListeners.iterator(); it.hasNext();) {
			APaintable ap = it.next();
			if (ap != null){
				InputEventUtil.passKeyboardEventTo((KeyboardListener)ap, ke);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent me) {
		accept(me);
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		accept(me);
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		accept(me);
	}

	@Override
	public void mouseExited(MouseEvent me) {
		accept(me);
	}

	@Override
	public void keyPressed(KeyboardEvent ke) {
		accept(ke);
	}
	
}
