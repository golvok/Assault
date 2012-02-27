/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assault.game.util;

import assault.game.display.GameArea;
import assault.util.Disposable;
import java.awt.Shape;

/**
 *
 * @author matt
 */
public interface GridObject extends Disposable{
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	public GameArea getGA();

    public Shape getBounds();
}
