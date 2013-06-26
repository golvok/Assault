/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.display;

import assault.display.Button;
import java.awt.image.BufferedImage;

/**
 *
 * @author matt
 */
public class CDMButton extends Button {
	
	public static final int BUTTON_WIDTH = 30;
	public static final int BUTTON_HEIGHT = 30;
	
	private int index = -1;

	public CDMButton(String name, char shortCut, BufferedImage icon) {
		super(name, 0, 0, BUTTON_WIDTH, BUTTON_HEIGHT, shortCut, icon, null);
	}

	/**
	 * sets the index, the bounds of
	 * this as if it were in the ACDM
	 * 
	 * @param index
	 */
	void setIndex(int index, int numColumns) {
		if (this.index != index) {
			this.index = index;
			setX((index % numColumns) * CDMButton.BUTTON_WIDTH);
			setY((index / numColumns) * CDMButton.BUTTON_HEIGHT);
		}
	}

	public int getIndex() {
		return index;
	}

	@Override
	public void dispose() {
	}
}
