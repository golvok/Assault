/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.display;

import assault.game.Player;
import assault.game.display.CommandDispatchMenu;
import assault.game.display.GameArea;
import assault.game.display.StatusDisplayMenu;
import assault.game.display.TopGameContainer;
import assault.game.gameObjects.AGroup;
import assault.game.gameObjects.AUnit;
import assault.game.loading.ResourcePreloader;
import assault.game.loading.resourceHolders.ResourceException;
import assault.game.loading.resourceHolders.UnitResourceHolder;
import assault.game.util.terrain.NormalTerrainGenerator;
import assault.input.InputManager;
import assault.util.ThreadBlocker;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Color;

/**
 * The main window
 * @author matt
 */
public class AssaultWindow extends AContainer {

	public enum GameState {
		NOT_RUNNING,IN_MAIN_MENU,IN_GAME;
	}
	private GameState gameState;
	
	//
	private long lastframe = 0;
	private double lastFps = 0;
	private InputManager inputManager = null;
	//for main menu
	private Menu mainMenu = null;
	//for game
	private GameArea gameArea = null;
	private ResourcePreloader rp = null;
	private CommandDispatchMenu commandDispatchMenu = null;
	private StatusDisplayMenu statusDisplayMenu = null;
	private TopGameContainer tgContainer = null;
	//for game initialization scheme
	private ThreadBlocker<Boolean> initializationBlocker = new ThreadBlocker<Boolean>();

	public AssaultWindow() {
		super(0, 0, 800, 600, 2);
	}
	
	public static void main(String[] argv) {
		
		System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir")+"/lib/lwjgl-2.8.3/native/macosx");
		
		Thread.currentThread().setName("Assault Main Thread");
		
		AssaultWindow newThis = new AssaultWindow();
		newThis.start();
	}

	private void init() {
		System.out.println("init game window");
		initGL();
		getDelta();//to initialize this method
		inputManager = new InputManager(null);
	}

	private void initGL() {
		System.out.println("init OpenGL");
		//glShadeModel(GL_SMOOTH);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, getWidth(), 0, getHeight(), 10, -10);
		glMatrixMode(GL_MODELVIEW);
	}

	public void start() {
		System.out.println("opening window");
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		System.out.println("starting initialization");
		init();

		enterMainMenu();
		System.out.println("init done. Entering loop");

		boolean triedInitializing = false;

		while (!gameStateIs(GameState.NOT_RUNNING)) {

			if (Display.isCloseRequested()) {
				setGameState(GameState.NOT_RUNNING);
			}

			if (gameStateIs(GameState.IN_MAIN_MENU)) {
				//when the game is wanted to be initialized
				if (initializationBlocker.isAWaitingThread() == true) {
					if (!triedInitializing) {//and not allready tried
						initializationBlocker.notifyWaitingThreadWith(initGame());
						triedInitializing = true;
					}else{
						initializationBlocker.notifyWaitingThreadWith(false);
					}
				} else {
					triedInitializing = false;
				}
			}

			inputManager.processInput();
			update(getDelta());
			renderGraphics();
			/*synchronized (this) {
			try {
			wait(50);
			} catch (InterruptedException ex) {
			
			}
			}*/
			Display.update();
			//System.out.println(lastFps);
			Display.sync(60);
		}
		System.out.println("game stopping");
		dispose();
	}

	private void update(int delta) {
		Display.setTitle("AssaultWindow @ " + ((int) (lastFps * 100d)) / 100d + " fps");
		updateFps(delta);
	}

	private void renderGraphics() {
		glClearColor(0.95f, 0.95f, 1f, 1f);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		draw();
	}

	@Override
	public void drawSelf() {
		super.drawSelf();
	}

	/**
	 * 
	 * @return the difference in milliseconds since the last time this method was called
	 */
	private int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastframe);
		lastframe = time;
		return delta;
	}

	/**
	 * milliseconds
	 * @return 
	 */
	private long getTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	private void updateFps(int delta) {
		if (delta != 0) {
			lastFps = 1000d / ((double) delta);
		} else {
			lastFps = -1;
		}
	}

	void quitGame() {
		setGameState(GameState.NOT_RUNNING);
	}

	public void enterMainMenu() {
		System.out.println("entering main menu");
		if (tgContainer != null){
			tgContainer.dispose();
		}
		mainMenu = new MainMenu(this);
		inputManager.distributeTo(mainMenu);
		addChild(mainMenu);
		setGameState(GameState.IN_MAIN_MENU);
	}

	public void startGame() {
		System.out.println("entering game");
		if (mainMenu != null){
			mainMenu.dispose();
		}
		if (initializationBlocker.WaitUntilValue()) {//waits until the main loop tries to initialize the game
			System.out.println("starting game");
			removeChild(mainMenu);
			inputManager.distributeTo(tgContainer);
			addChild(tgContainer);
			setGameState(GameState.IN_GAME);
		}
	}

	private boolean initGame() {
		System.out.println("initializing game");
		tgContainer = new TopGameContainer(0, 0, getWidth(), getHeight(), this);

		String[] mods = {
			"default",};


		final Player p1 = new Player(Color.RED);
		final Player p2 = new Player(Color.ORANGE);

		rp = new ResourcePreloader(mods, tgContainer.getWidth(), tgContainer.getHeight());
		tgContainer.addChild(rp);

		rp.start();//blocks until done

		try {
			gameArea = new GameArea(this, new NormalTerrainGenerator(rp.getModTerrainByName("default", "defaultTerrain")));
		} catch (ResourceException ex) {
			rp.addError(ex);
			rp.printDebugLine("GameArea failed to load");
			tgContainer.removeAllChildren();
			tgContainer = null;
			return false;
		}

		commandDispatchMenu = new CommandDispatchMenu(0, 0, 3);
		statusDisplayMenu = new StatusDisplayMenu(100, 100);

		tgContainer.addChild(gameArea);
		tgContainer.addChild(commandDispatchMenu);
		tgContainer.addChild(statusDisplayMenu);

		try {
			//<editor-fold defaultstate="collapsed" desc="unit creation">
			AUnit en2 = new AUnit(gameArea, 105, 300, rp.getModUnitByName("default", "SimpleUnit2"), p1);
			gameArea.add(en2);

			AUnit b1 = new AUnit(gameArea, 505, 100, rp.getModUnitByName("default", "SimpleBuilding"), p1);
			gameArea.add(b1);

			AUnit e1 = new AUnit(gameArea, 105, 400, rp.getModUnitByName("default", "SimpleUnit2"), p2);
			gameArea.add(e1);

			AUnit e2 = new AUnit(gameArea, 505, 200, rp.getModUnitByName("default", "SimpleBuilding"), p2);
			gameArea.add(e2);

			AUnit e3 = new AUnit(gameArea, 605, 50, rp.getModUnitByName("default", "SimpleUnit"), p2);
			gameArea.add(e3);

			AUnit u1 = new AUnit(gameArea, 605, 100, rp.getModUnitByName("default", "SimpleUnit"), p1);
			gameArea.add(u1);
			AUnit u2 = new AUnit(gameArea, 705, 100, rp.getModUnitByName("default", "SimpleUnit2"), p1);
			gameArea.add(u2);
			AUnit u3 = new AUnit(gameArea, 705, 180, rp.getModUnitByName("default", "SimpleUnit"), p1);
			gameArea.add(u3);
			AUnit[] gu1 = {u1, u2, u3};
			AGroup g1 = new AGroup(gameArea, gu1, p2);
			gameArea.add(g1);

			AUnit w1 = new AUnit(gameArea, 655, 50, rp.getModUnitByName("default", "Worker"), p2);
			gameArea.add(w1);

			UnitResourceHolder su1 = rp.getModUnitByName("default", "SimpleUnit");
			for (int i = 0; i <= 6; i++) {
				gameArea.add(new AUnit(gameArea, (i * 58) + 100, 200, su1, p1));
			}

			UnitResourceHolder su2 = rp.getModUnitByName("default", "SimpleUnit2");
			for (int i = 0; i <= 6; i++) {
				gameArea.add(new AUnit(gameArea, (i * 65) + 100, 500, su2, p2));
			}
			//</editor-fold>
		} catch (ResourceException ex) {
			rp.addError(ex);
		}
		synchronized (this) {
			try {
				wait(1000);
			} catch (InterruptedException ex) {
			}
			tgContainer.removeChild(rp);
		}
		System.out.println("game initialization successful");
		return true;
	}

	public StatusDisplayMenu getASDM() {
		return statusDisplayMenu;
	}

	public CommandDispatchMenu getACDM() {
		return commandDispatchMenu;
	}

	public GameArea getGA() {
		return gameArea;
	}

	public ResourcePreloader getRP() {
		return rp;
	}

	@Override
	public void dispose() {
		System.out.println("game disposing");
		setGameState(GameState.NOT_RUNNING);
		super.dispose();
		System.out.println("closing window");
		Display.destroy();
	}

	/**
	 * @return the gameState
	 */
	public GameState getGameState() {
		return gameState;
	}

	public boolean gameStateIs(GameState testState) {
		return testState == getGameState();
	}

	/**
	 * @param gameState the gameState to set
	 */
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
}
