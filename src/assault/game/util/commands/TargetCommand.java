/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assault.game.util.commands;

import assault.game.gameObjects.AObject;

/**
 *
 * @author matt
 */
public interface TargetCommand {
	public void executeOn(AObject[] aos,AObject target);
}
