/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assault.game.util.commands;

import java.awt.image.BufferedImage;

/**
 *
 * @author matt
 */
public abstract class ACommand {
	private boolean isValidCommand = true;
	private BufferedImage icon;
	private char shortCut = Character.MIN_VALUE;
	private String cmdName;
	
	public ACommand(String name,char shortCut,BufferedImage icon) {
		this(name, icon);
		this.shortCut = shortCut;
	}
	public ACommand(String name,BufferedImage icon) {
		System.out.println("Creating an ACommand");
		if (name == null) {
			isValidCommand = false;
			System.out.println("invalid (null) \"name\" parameter for an ACommand");
			throw new NullPointerException();
		}
		if (icon == null) {
			isValidCommand = false;
			System.out.println("invalid (null) \"icon\" parameter for ACommand named " + ((name != null) ? name : "null"));
			throw new NullPointerException();
		}
		cmdName = name;
		this.icon = icon;
		System.out.println("done creating ACommand named "+ name);
	}
	public BufferedImage getIcon() {
		return icon;
	}
	public String getName() {
		return cmdName;
	}

	public char getShortCut() {
		return shortCut;
	}

	public boolean isValidCommand() {
		return isValidCommand;
	}
	
}
