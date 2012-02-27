/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util;

import java.util.ArrayList;

/**
 *
 * @author matt
 */
public class GridCell<E extends GridObject> extends ArrayList<E> {

	public GridCell(int initSize) {
		super(initSize);
	}
	
}
