/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.display;

import assault.display.Paintable;
import assault.game.gameObjects.AObject;
import org.lwjgl.util.ReadableColor;

/**
 *
 * @author matt
 */
public class StatusDisplayBox extends Paintable {

	private AObject object;
	public final static int BOX_WIDTH = 50;
	public final static int BOX_HEIGHT = 10;

	public StatusDisplayBox(AObject object) {
		this(object, BOX_WIDTH, BOX_HEIGHT);
	}

	public StatusDisplayBox(AObject object, int w, int h) {
		super(0, object.getHeight(), w, h);
		this.object = object;
	}

	@Override
	public void dispose() {
		if (!isDisposed()) {
			object = null;
		}
		super.dispose();
	}

	public void correctPosition() {
		setLocation(0, object.getHeight());
	}

	public AObject getObject() {
		return object;
	}

	@Override
	public void drawSelf() {
		if (object != null) {
			setColour(ReadableColor.BLACK);
			drawRect(1, 1, getWidth(), (int) Math.floor((double) getHeight() / 3d));
			setColour(ReadableColor.RED);
			fillRect(1, 1, (int) (Math.floor((double) getWidth() * ((double) object.getHealth() / (double) object.getMaxHealth()) - 1d)), (int) (Math.floor((double) getHeight() / 3d - 1d)));
		}
		//System.out.println("ASDB_PAINT");
	}
}
