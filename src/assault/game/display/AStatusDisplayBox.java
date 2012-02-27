/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.display;

import assault.game.gameObjects.AObject;
import assault.display.APaintable;
import org.lwjgl.util.ReadableColor;

/**
 *
 * @author matt
 */
public class AStatusDisplayBox extends APaintable {

	private AObject object;
	public final static int BOX_WIDTH = 50;
	public final static int BOX_HEIGHT = 10;

	public AStatusDisplayBox(AObject object) {
		super(0, 0, BOX_WIDTH, BOX_HEIGHT);
		this.object = object;
	}

	public AStatusDisplayBox(AObject ao, int w, int h) {
		super(0, 0, w, h);
		this.object = ao;
		correctPosition();
	}

	@Override
	public void dispose() {
		if (!isDisposed()) {
			object = null;
		}
		super.dispose();
	}

	public void correctPosition() {
		setLocation(object.getX(), object.getY() + object.getHeight());
	}

	public AObject getObject() {
		return object;
	}

	@Override
	public void drawSelf() {
		if (object != null) {
			setColour(ReadableColor.BLACK);
			drawRect(0, 0, getWidth(), (int) Math.floor((double) getHeight() / 3d));
			setColour(ReadableColor.RED);
			fillRect(1, 1, (int) (Math.floor((double) getWidth() * ((double) object.getHealth() / (double) object.getMaxHealth()) - 1d)), (int) (Math.floor((double) getHeight() / 3d - 1d)));
		}
		//System.out.println("ASDB_PAINT");
	}

	private GameArea getGA() {
		return object.getGA();
	}
}
