/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assault.game.util.commands;

/**
 *
 * @author matt
 */
public abstract class ComplexCommand extends Command{
	Command[] cmds;
	
	public ComplexCommand(String name,char shortCut,Command cmd) {
		super(name, shortCut, cmd.getIcon());
		this.cmds = new Command[1];
		cmds[0] = cmd;
	}
	public ComplexCommand(String name,Command cmd) {
		super(name, cmd.getIcon());
		this.cmds = new Command[1];
		cmds[0] = cmd;
	}
	public ComplexCommand(String name,char shortCut,Command[] cmds) {
		super(name, shortCut, cmds[0].getIcon());
		this.cmds = cmds;
	}
	public ComplexCommand(String name,Command[] cmds) {
		super(name,cmds[0].getIcon());
		this.cmds = cmds;
	}
}
