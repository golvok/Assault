/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.commands;

import assault.game.loading.ResourcePreloader;
import assault.game.gameObjects.AObject;
import assault.game.gameObjects.AUnit;
import java.awt.Point;

/**
 *
 * @author matt
 */
public class MoveCmd extends ACommand implements MouseCommand {

    public MoveCmd(ResourcePreloader rp) {
        super("Move", 'M', rp.getCmdIcon(ResourcePreloader.MOVE_CMD_IMAGE_INDEX));
    }

    @Override
    public void executeOn(AObject[] aos, int x, int y) {
        for (int i = 0; i < aos.length; i++) {
            if (aos[i] != null && aos[i] instanceof AUnit) {
                //TODO add logic so that the keep their formation when more than one AU is used
                ((AUnit) aos[i]).moveTo(x, y);
            }
        }
    }

	@Override
	public void executeOn(AObject[] aos, Point p) {
		executeOn(aos, p.x, p.y);
	}
	
}