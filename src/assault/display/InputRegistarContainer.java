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
public class InputRegistarContainer extends Container implements InputRegistar, MouseListener, KeyboardListener {

	private ArrayList<Paintable> mouseListeners = new ArrayList<Paintable>();
	private ArrayList<Paintable> keyListeners = new ArrayList<Paintable>();

	public InputRegistarContainer(double x, double y, double width, double height, ArrayList<Paintable> startchildren) {
		super(x, y, width, height, startchildren);
	}

	public InputRegistarContainer(double x, double y, double width, double height, int startSize) {
		super(x, y, width, height, startSize);
	}

	@Override
	public void addMouseEventReciever(Paintable ml) {
		if (ml instanceof MouseListener){
			mouseListeners.add(ml);
		}else{
			throw new InvalidParameterException(ml+"is not a Mouselistener");
		}
	}

	@Override
	public void removeMouseEventReciever(Bounded ml) {
		mouseListeners.remove(ml);
	}

	@Override
	public void addKeyboardEventReciever(Paintable kl){
		if (kl instanceof KeyboardListener){
			keyListeners.add(kl);
		}else{
			throw new InvalidParameterException(kl+"is not a Keyboardlistener");
		}
	}

	@Override
	public void removeKeyboardEventReciever(Bounded kl) {
		keyListeners.remove(kl);
	}

	@Override
	protected void removeChild(Paintable ap) {
		super.removeChild(ap);
		removeMouseEventReciever(ap);
		removeKeyboardEventReciever(ap);
	}

	@Override
	public void accept(MouseEvent me) {
		for (Iterator<Paintable> it = mouseListeners.iterator(); it.hasNext();) {
			Bounded ap = it.next();
			if (ap != null && me.intersects(ap.getBounds())){
				InputEventUtil.passAndTranslateMouseEventTo(ap, me);
			}
		}
	}

	@Override
	public void accept(KeyboardEvent ke) {
		for (Iterator<Paintable> it = keyListeners.iterator(); it.hasNext();) {
			Bounded ap = it.next();
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
