/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.terrain;

import assault.display.APaintable;
import assault.game.display.GameArea;
import assault.game.gameObjects.TerrainObject;
import assault.game.loading.resourceHolders.GroundCover;
import assault.game.loading.resourceHolders.ResourceException;
import assault.game.loading.resourceHolders.TerrainObjectResourceHolder;

/**
 *
 * @author matt
 */
public class TerrainSquare extends APaintable {

	private TerrainObject[] terrainObjects;
	private GroundCover groundCover;

	public TerrainSquare(GameArea g, int x, int y, TerrainObjectResourceHolder to, GroundCover gc) {
		//TODO change these hardcoded values (all the 10's) after terrain generation is rewriten
		super(x*10, y*10, 10, 10);
		groundCover = gc;
		if (to != null) {
			terrainObjects = new TerrainObject[1];
			try {
				terrainObjects[0] = to.createObject(g, x * 10, y * 10, null);
			} catch (ResourceException ex) {
			}
		} else {
			terrainObjects = new TerrainObject[0];
		}
	}

	public GroundCover getGroundCover() {
		return groundCover;
	}

	public TerrainObject getTerrainObject(int i) {
		return terrainObjects[i];
	}

	public TerrainObject[] getTerrainObjects() {
		return terrainObjects;
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
