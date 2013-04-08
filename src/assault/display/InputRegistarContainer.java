package assault.display;

import java.util.ArrayList;
import java.util.Iterator;

import assault.input.InputEventUtil;
import assault.input.InputRegistar;
import assault.input.KeyboardEvent;
import assault.input.KeyboardListener;
import assault.input.MouseEvent;
import assault.input.MouseListener;

/**
 * 
 * @author matt
 */
public class InputRegistarContainer extends Container<Bounded_Impl> implements InputRegistar, MouseListener, KeyboardListener {

	private ArrayList<MouseListener> mouseListeners = new ArrayList<>();
	private ArrayList<KeyboardListener> keyListeners = new ArrayList<>();

	public InputRegistarContainer(double x, double y, double width, double height, ArrayList<Bounded_Impl> startchildren) {
		super(x, y, width, height, startchildren);
	}

	public InputRegistarContainer(double x, double y, double width, double height, int startSize) {
		super(x, y, width, height, startSize);
	}

	@Override
	public void addMouseEventReciever(MouseListener ml) {
		mouseListeners.add(ml);
	}

	@Override
	public void removeMouseEventReciever(MouseListener ml) {
		mouseListeners.remove(ml);
	}

	@Override
	public void addKeyboardEventReciever(KeyboardListener kl) {
		keyListeners.add(kl);
	}

	@Override
	public void removeKeyboardEventReciever(KeyboardListener kl) {
		keyListeners.remove(kl);
	}

	@Override
	protected void removeChild(Bounded b) {
		super.removeChild(b);
		if (b instanceof MouseListener){
			removeMouseEventReciever((MouseListener)b);
		}
		if (b instanceof KeyboardListener){
			removeKeyboardEventReciever((KeyboardListener)b);
		}
	}

	@Override
	public void accept(MouseEvent me) {
		for (Iterator<MouseListener> it = mouseListeners.iterator(); it.hasNext();) {
			MouseListener ml = it.next();
			if (ml instanceof Bounded_Impl){
				if (me.intersects(((Bounded_Impl)ml).getBounds())){
					InputEventUtil.passAndTranslateMouseEventTo((Bounded_Impl)ml, me);
				}
			}else{
				InputEventUtil.passMouseEventTo(ml, me);
			}
		}
	}

	@Override
	public void accept(KeyboardEvent ke) {
		for (Iterator<KeyboardListener> it = keyListeners.iterator(); it.hasNext();) {
			KeyboardListener kl = it.next();
			if (kl != null) {
				InputEventUtil.passKeyboardEventTo((KeyboardListener) kl, ke);
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
