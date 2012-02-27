/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assault.game.util.commands;

import assault.game.gameObjects.AObject;
import java.awt.Point;

/**
 *
 * @author matt
 */
public interface MouseCommand {
	public void executeOn(AObject[] aos,int x,int y);
	public void executeOn(AObject[] aos,Point p);
}
