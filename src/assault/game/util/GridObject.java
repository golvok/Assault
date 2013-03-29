package assault.game.util;

import assault.game.display.GameArea;
import assault.util.Disposable;
import java.awt.Shape;

/**
 *
 * @author matt
 */
public interface GridObject extends Disposable{
	public double getX();
	public double getY();
	public double getWidth();
	public double getHeight();
	public GameArea getGA();

    public Shape getBounds();
}
