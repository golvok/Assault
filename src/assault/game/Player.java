/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assault.game;

import org.lwjgl.util.ReadableColor;

/**
 *
 * @author matt
 */
public class Player {
	private ReadableColor colour;
	public Player(ReadableColor colour) {
		super();
		this.colour = colour;
	}
	public ReadableColor getColour() {
		return colour;
	}
}
