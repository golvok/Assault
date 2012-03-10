/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.terrain;

import assault.game.display.GameArea;
import assault.game.gameObjects.TerrainObject;
import assault.game.loading.resourceHolders.ResourceException;
import assault.game.loading.resourceHolders.TerrainResource;
import assault.game.util.*;
import com.sun.org.apache.xerces.internal.impl.dv.xs.YearDV;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matt
 */
public class NormalTerrainGenerator extends TerrainGenerator {

	private final TerrainResource terrainSrc;

	public NormalTerrainGenerator(TerrainResource src) {
		this.terrainSrc = src;
	}

	@Override
	public boolean generateInto(GameArea g, int ga_x, int ga_y, int ga_width, int ga_height, int seed) {
		GridManager gm = g.getGM();
		int xInGrid = gm.convCoordToGrid(ga_x);
		int yInGrid = gm.convCoordToGrid(ga_y);
		int widthInGrid = gm.convDimToGrid(ga_width, ga_y);
		int heightInGrid = gm.convDimToGrid(ga_height, ga_x);

		TerrainGridCell<GridObject> tgc;

		if (!(gm instanceof TerrainGridManager)) {
			return false;
		}

		for (int x = xInGrid; x < widthInGrid; x++) {
			for (int y = yInGrid; y < heightInGrid; y++) {
				tgc = (TerrainGridCell<GridObject>) gm.getGridCellAtGrid(x, y);
				tgc.setTerrainSquare(new TerrainSquare(g, x, y, terrainSrc.getGroundCovers()[0]));
				if (x % 10 == 0 && y % 10 == 0){
					try {
						TerrainObject to = new TerrainObject(g, gm.convGridToPixel(x), gm.convGridToPixel(y), terrainSrc.getTerrainObjects()[0], null);
						g.add(to);
					} catch (ResourceException ex) {
						ex.printStackTrace(System.out);
						//fail silently
					}
				}
			}
		}
		return true;
	}
}
