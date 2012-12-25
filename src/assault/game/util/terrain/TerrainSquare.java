/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.terrain;

import assault.display.Paintable;
import assault.game.display.GameArea;
import assault.game.loading.resourceHolders.GroundCover;

/**
 *
 * @author matt
 */
public class TerrainSquare extends Paintable {
	
	private GroundCover groundCover;

	public TerrainSquare(GameArea g, int x, int y, GroundCover gc) {
		//TODO change these hardcoded values (all the 10's) after terrain generation is rewriten
		super(x*10, y*10, 10, 10);
		groundCover = gc;
	}

	public GroundCover getGroundCover() {
		return groundCover;
	}

	@Override
	public void drawSelf() {
		drawTexture(0, 0, getWidth(), getHeight(), groundCover.getTexture());
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
}
