/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.display;

import assault.input.InputEventUtil;
import assault.input.InputRegistar;
import assault.input.KeyboardEvent;
import assault.input.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author matt
 */
public class Menu extends InputRegistarContainer implements InputRegistar{

	private ArrayList<Button> buttons = null;

	public Menu(AssaultWindow aw, ArrayList<Button> buttons) {
		this(aw.getX(), aw.getY(), aw.getWidth(), aw.getHeight(), buttons);
	}

	public Menu(double x, double y, double width, double height, ArrayList<Button> startButtons) {
		super(x, y, width, height, startButtons.size());
		buttons = new ArrayList<Button>(startButtons.size());
		for (Button button : startButtons) {
			addButton(button);
		}
	}

	public void addButton(Button b) {
		//System.out.println(b + " added");
		buttons.add(b);
		addMouseEventReciever(b);
		addChild(b);
	}

	public void removeButton(Button b) {
		buttons.remove(b);
		removeChild(b);
	}

	public void setButtons(ArrayList<Button> buttons) {
		this.buttons = new ArrayList<Button>(buttons);
	}

	public ArrayList<Button> getButtons() {
		return new ArrayList<Button>(buttons);
	}

	@Override
	public void accept(MouseEvent me) {
		//System.out.println(this + " accepted " + me);
		for (Iterator<Button> it = getButtons().iterator(); it.hasNext();) {
			Button button = it.next();
			if (button.getBounds().contains(me.getX(), me.getY())) {
				InputEventUtil.passAndTranslateMouseEventTo((Bounded)button, me);
				//System.out.println("mousematch : " + button);
			}
		}
	}

	@Override
	public void accept(KeyboardEvent ke) {
		//System.out.println(this + " accepted " + ke);
		for (Iterator<Button> it = getButtons().iterator(); it.hasNext();) {
			Button button = it.next();
			//System.out.println("possible match " + button.getHotkey());
			if (button.getHotkey() == ke.getButton()) {
				InputEventUtil.passKeyboardEventTo(button, ke);
				//System.out.println("keymatch : " + button);
			}
		}
	}
}
