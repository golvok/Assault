/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.commands;

import assault.game.gameObjects.AObject;
import assault.game.gameObjects.Controllable;
import assault.game.loading.ResourcePreloader;
import assault.game.util.pathfinding.moving.Mover;
import assault.util.Point;

/**
 *
 * @author matt
 */
public class MoveCmd extends Command implements MouseCommand {

    public MoveCmd(ResourcePreloader rp) {
        super("Move", 'M', rp.getCmdIcon(ResourcePreloader.MOVE_CMD_IMAGE_INDEX));
    }

    @Override
    public void executeOn(AObject[] aos, int x, int y) {
        for (int i = 0; i < aos.length; i++) {
            if (aos[i] != null && aos[i] instanceof Controllable) {
                //TODO add logic so that the keep their formation when more than one AU is used
                Mover.moveTo(((Controllable) aos[i]),x, y);
            }
        }
    }

	@Override
	public void executeOn(AObject[] aos, Point p) {
		executeOn(aos, (int)p.getX(),(int)p.getY());
	}
	
}