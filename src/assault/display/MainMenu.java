/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.display;

import assault.game.loading.ResourcePreloader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author matt
 */
public class MainMenu extends Menu {
	
	public MainMenu(final AssaultWindow aw) {
		super(aw, 
			new ArrayList<Button>(
				Arrays.asList(
					new MenuButton("Start", 50, 100, 80, 30, 's', ResourcePreloader.loadImage("ASSAULT_DATA/menu/main/start.png"), 
						new Runnable() {
							@Override
							public void run() {
								aw.startGame();
							}
						}
					),
					new MenuButton("Quit" , 50,  50, 80, 30, 'q', ResourcePreloader.loadImage("ASSAULT_DATA/menu/main/quit.png") , 
						new Runnable() {
							@Override
							public void run() {
								aw.quitGame();
							}
						}
					)		
				)
			)
		);
	}

	@Override
	public void drawSelf() {
		super.drawSelf();
	}
	
}
