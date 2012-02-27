/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.terrain;

import assault.game.display.GameArea;

/**
 *
 * @author matt
 */
public abstract class TerrainGenerator {

	public TerrainGenerator(int seed) {
	}
	
	public abstract TerrainSquare getCordinate(GameArea g, int x, int y);
}
