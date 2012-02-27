/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.gameObjects;

import assault.game.Player;
import assault.game.display.GameArea;
import assault.game.loading.resourceHolders.ResourceException;
import assault.game.loading.resourceHolders.TerrainObjectResourceHolder;

/**
 *
 * @author matt
 */
public class TerrainObject extends Selectable {

	public TerrainObject(GameArea g, int x, int y, TerrainObjectResourceHolder src, int health, Player owner) throws ResourceException {
		super(g, x, y, src, health, owner);
	}

	public TerrainObject(GameArea g, int x, int y, TerrainObjectResourceHolder src, Player owner) throws ResourceException {
		this(g, x, y, src, src.getMaxHealth(), owner);
	}

	@Override
	public TerrainObjectResourceHolder getSrc() {
		return (TerrainObjectResourceHolder) super.getSrc();
	}
}
