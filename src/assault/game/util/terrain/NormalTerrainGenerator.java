/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.terrain;

import assault.game.display.GameArea;
import assault.game.loading.resourceHolders.TerrainResource;

/**
 *
 * @author matt
 */
public class NormalTerrainGenerator extends TerrainGenerator {

	private final TerrainResource terrainSrc;

	public NormalTerrainGenerator(TerrainResource src) {
		this(src, 0);
	}
	public NormalTerrainGenerator(TerrainResource src, int seed){
		super(seed);
		this.terrainSrc = src;
	}

	@Override
	public TerrainSquare getCordinate(GameArea g,int x, int y) {
		return new TerrainSquare(g, x, y, ((x % 10 == 0 && y % 10 == 0) ? (terrainSrc.getTerrainObjects()[0]) : (null)), terrainSrc.getGroundCovers()[0]);
	}
}