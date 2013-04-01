/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.input;

import assault.display.Bounded;
import assault.display.Paintable;
import java.awt.Shape;

/**
 *
 * @author matt
 */
public class MouseEvent extends ButtonInputEvent {

	public static final int BUTTON_LEFT = 0;
	public static final int BUTTON_RIGHT = 1;
	public static final int BUTTON_MIDDLE = 2;
	
	private final int x;
	private final int y;

	public MouseEvent(int x, int y, int button, boolean newState, int modifiers) {
		super((char)button,newState,modifiers);
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	
	/**
	 * creates a new mouseEvent object relative to ap's origin.
	 * @param ap
	 * @return 
	 */
	public MouseEvent translate(Bounded ap) {
		return new MouseEvent((int)Math.round(getX() - ap.getX()), (int)Math.round(getY() - ap.getY()), getButton(), getNewState(), getModifiers());
	}

	public boolean intersects(Shape r) {
		return r.contains(x, y);
	}

	@Override
	public String toString() {
		return "MouseEvent: button "+ getButton() + (getNewState() == BUTTON_PRESSED ? " pressed " : " released") + " at ("+getX()+","+getY()+")";
	}
}
