package assault.game.util;

import assault.display.Bounded;
import assault.game.display.GameArea;
import assault.game.util.terrain.TerrainGenerator;

/**
 *
 * @author matt
 */
public class TerrainGridManager extends GridManager{
	
	public TerrainGridManager(int gridSize, GameArea ga,TerrainGenerator tg) {
		super(gridSize,ga,tg);
		//terrainGenerator = tg;
	}

	@Override
	public GridCell<Bounded> generateGridCell(GameArea g, int x, int y) {
		return new TerrainGridCell<Bounded>(4);
	}

	@Override
	public TerrainGridCell<Bounded> getGridCellAtGrid(int x, int y) throws IndexOutOfBoundsException {
		return (TerrainGridCell<Bounded>)super.getGridCellAtGrid(x, y);
	}
}
