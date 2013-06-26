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

	public TerrainGenerator() {
	}

	public boolean generateInto(GameArea g, int seed) {
		return generateInto(g, 0, 0, g.getWidth(), g.getHeight(), seed);
	}

	public abstract boolean generateInto(GameArea g, float ga_x, float ga_y, float ga_width, float ga_height, int seed);
}
