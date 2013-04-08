/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util;

import java.util.ArrayList;

import assault.display.Bounded;

/**
 *
 * @author matt
 */
public class GridCell<E extends Bounded> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2032438631101001461L;

	public GridCell(int initSize) {
		super(initSize);
	}
	
}
