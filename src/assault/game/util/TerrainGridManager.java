
package assault.game.util;

import assault.game.display.GameArea;
import assault.game.util.terrain.TerrainGenerator;

/**
 *
 * @author matt
 */
public class TerrainGridManager extends GridManager{
	
	public TerrainGridManager(int pixelWidth, int pixelHeight, int gridSize, TerrainGenerator tg,GameArea ga) {
		super(pixelWidth, pixelHeight, gridSize,ga,tg);
		//terrainGenerator = tg;
	}

	@Override
	public GridCell<GridObject> generateGridCell(GameArea g, int x, int y) {
		return new TerrainGridCell<GridObject>(4);
	}

	@Override
	public TerrainGridCell<GridObject> getGridCellAtGrid(int x, int y) throws IndexOutOfBoundsException {
		return (TerrainGridCell<GridObject>)super.getGridCellAtGrid(x, y);
	}
}
