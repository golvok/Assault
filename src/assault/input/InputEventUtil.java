/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.input;

import assault.display.APaintable;
import java.security.InvalidParameterException;

/**
 *
 * @author matt
 */
public class InputEventUtil {

	public static void passMouseEventTo(MouseListener ml, MouseEvent me) {
		if (me.getNewState() == MouseEvent.BUTTON_PRESSED) {
			ml.mousePressed(me);
		} else if (me.getNewState() == MouseEvent.BUTTON_RELEASED) {
			ml.mouseReleased(me);
		}
	}

	public static void passMouseEventTo(APaintable ap, MouseEvent me) {
		if (ap instanceof MouseListener) {
			if (me.getNewState() == MouseEvent.BUTTON_PRESSED) {
				((MouseListener) ap).mousePressed (me.translate(ap));
			} else if (me.getNewState() == MouseEvent.BUTTON_RELEASED) {
				((MouseListener) ap).mouseReleased(me.translate(ap));
			}
		}else{
			throw new InvalidParameterException(ap+" is not a Mouselistener");
		}
	}

	public static void passKeyboardEventTo(KeyboardListener kl, KeyboardEvent ke) {
		if (ke.getNewState() == MouseEvent.BUTTON_PRESSED) {
			kl.keyPressed(ke);
		} /*
		 * else if (ke.getNewState() == MouseEvent.BUTTON_RELEASED){
		 * kl.keyReleased(ke); }
		 */ /*
		 * else if (ke.getNewState() == MouseEvent.BUTTON_RELEASED){
		 * kl.keyReleased(ke); }
		 */
	}
}
