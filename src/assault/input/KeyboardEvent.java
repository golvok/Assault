/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.input;

/**
 *
 * @author matt
 */
public class KeyboardEvent extends ButtonInputEvent {

	public KeyboardEvent(char charcter, boolean down, int modifiers) {
		super(charcter, down, modifiers);
	}

	@Override
	public String toString() {
		return "KeyboardEvent: char '"+getButton()+ (getNewState() == BUTTON_PRESSED ? "' pressed " : "' released");
	}

	/*KeyboardEvent translate(APaintable aPaintable) {
		
	}*/
}
