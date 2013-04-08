package assault.game.util;

import assault.display.Bounded;
import assault.game.util.terrain.TerrainSquare;

/**
 *
 * @author matt
 */
public class TerrainGridCell<E extends Bounded> extends GridCell<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TerrainSquare terrainSquare;

	public TerrainGridCell(int initSize) {
		this(initSize,null);
	}
	
	public TerrainGridCell(int initSize, TerrainSquare ts) {
		super(initSize);
		terrainSquare = ts;
	}

	public TerrainSquare getTerrainSquare() {
		return terrainSquare;
	}
	
	public void setTerrainSquare(TerrainSquare ts){
		terrainSquare = ts;
	}

}
