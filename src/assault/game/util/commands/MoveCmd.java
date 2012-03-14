/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.commands;

import assault.game.gameObjects.AObject;
import assault.game.gameObjects.Unit;
import assault.game.loading.ResourcePreloader;
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
            if (aos[i] != null && aos[i] instanceof Unit) {
                //TODO add logic so that the keep their formation when more than one AU is used
                ((Unit) aos[i]).getMover().moveTo(x, y);
            }
        }
    }

	@Override
	public void executeOn(AObject[] aos, Point p) {
		executeOn(aos, (int)p.getX(),(int)p.getY());
	}
	
}