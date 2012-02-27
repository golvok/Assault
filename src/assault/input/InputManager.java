/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 *
 * @author matt
 */
public class InputManager {
	
	private InputDistributor distributor = null;

	public InputManager() {
		this(null);
	}
	
	public InputManager(InputDistributor dist) {
		distributor = dist;
	}
	
	public boolean init(){
		return true;
	}
	
	public void processInput(){
		if (getDistributor() != null){
			processKeyboard();
			processMouse();
		}
	}

	private void processKeyboard() {
		KeyboardEvent ke;
		while (Keyboard.next()) {
			if (!Keyboard.isRepeatEvent()){
				try {
					ke = new KeyboardEvent(Keyboard.getEventCharacter(), Keyboard.getEventKeyState(), getKeyboardModifiers());
					//System.out.println(ke);
					distributor.accept(ke);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void processMouse() {
		MouseEvent me;
		while (Mouse.next()) {
			if (Mouse.getEventButton() > -1) {
				try {
					me = new MouseEvent(Mouse.getEventX(), Mouse.getEventY(), Mouse.getEventButton(), Mouse.getEventButtonState(), getKeyboardModifiers());
					//System.out.println(me);
					distributor.accept(me);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static int getKeyboardModifiers() {
		return (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? InputEvent.LSHIFT_DOWN_MASK : 0)
				| (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ? InputEvent.LCTRL_DOWN_MASK : 0)
				| (Keyboard.isKeyDown(Keyboard.KEY_LMENU) ? InputEvent.LALT_DOWN_MASK : 0);
	}

	public InputDistributor getDistributor() {
		return distributor;
	}

	public void distributeTo(InputDistributor distributor) {
		this.distributor = distributor;
	}
	
}
