/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.input;

/**
 *
 * @author matt
 */
public class ButtonInputEvent extends InputEvent{
	public static final boolean BUTTON_PRESSED = true;
	public static final boolean BUTTON_RELEASED = false;
	
	protected final char button;
	protected final boolean newState;

	public ButtonInputEvent(char button, boolean newState, int modifiers) {
		super(modifiers);
		this.button = button;
		this.newState = newState;
	}
	
	public int getButton() {
		return button;
	}

	public boolean getNewState() {
		return newState;
	}

	
	
}
