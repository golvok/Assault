/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.display;

import java.awt.image.BufferedImage;

/**
 *
 * @author matt
 */
public class MenuButton extends Button {

	public MenuButton(String name, int x, int y, int width, int height, char shortCut, BufferedImage icon, Runnable action) {
		super(name, x, y, width, height, shortCut, icon, action);
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
}
