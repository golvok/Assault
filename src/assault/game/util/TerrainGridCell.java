/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util;

import assault.game.util.terrain.TerrainSquare;

/**
 *
 * @author matt
 */
public class TerrainGridCell<E extends GridObject> extends GridCell<E> {
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
