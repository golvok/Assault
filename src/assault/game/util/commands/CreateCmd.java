/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assault.game.util.commands;

import assault.game.display.GameArea;
import assault.game.gameObjects.Unit;
import assault.game.loading.resourceHolders.UnitResourceHolder;
import javax.swing.ImageIcon;

/**
 *
 * @author matt
 */
public class CreateCmd extends Command{
	private UnitResourceHolder objectSource;

	public CreateCmd(String name, char hotkey, UnitResourceHolder objectSource){
		super(name, hotkey, objectSource.getMiniIcon());
		this.objectSource = objectSource;
	}
	public void execute(Unit builder, GameArea ga){
		if (builder != null){
			ga.add(objectSource.createObject(ga,builder.getCreatePointAbs(), builder.getOwner()));
		}
	}

	public UnitResourceHolder getObjectSource() {
		return objectSource;
	}

}
