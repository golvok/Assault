/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assault.game.util.commands;

/**
 *
 * @author matt
 */
public abstract class AComplexCommand extends ACommand{
	ACommand[] cmds;
	
	public AComplexCommand(String name,char shortCut,ACommand cmd) {
		super(name, shortCut, cmd.getIcon());
		this.cmds = new ACommand[1];
		cmds[0] = cmd;
	}
	public AComplexCommand(String name,ACommand cmd) {
		super(name, cmd.getIcon());
		this.cmds = new ACommand[1];
		cmds[0] = cmd;
	}
	public AComplexCommand(String name,char shortCut,ACommand[] cmds) {
		super(name, shortCut, cmds[0].getIcon());
		this.cmds = cmds;
	}
	public AComplexCommand(String name,ACommand[] cmds) {
		super(name,cmds[0].getIcon());
		this.cmds = cmds;
	}
}
