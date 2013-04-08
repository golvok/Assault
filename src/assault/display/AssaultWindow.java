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
import assault.game.gameObjects.TerrainObject;
import assault.game.gameObjects.Unit;
import assault.game.loading.ResourcePreloader;
import assault.game.loading.resourceHolders.ResourceException;
import assault.game.util.terrain.NormalTerrainGenerator;
import assault.input.InputManager;
import assault.util.OSUtil;
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
public class AssaultWindow extends Container<Bounded> {

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
		
        System.out.println("lwjgl version -> "+OSUtil.lwjglVersion);
        OSUtil.setLibrariesPath();
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
			//Display.sync(60);
		}
		System.out.println("game stopping");
		dispose();
	}

    @Override
	public void updateSelf(int delta) {
		Display.setTitle("AssaultWindow @ " + ((int) (lastFps * 100d)) / 100d + " fps");
		updateFps(delta);
        super.updateSelf(delta);
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
    private static final int NUM_DELTAS = 100;
    private int[] deltas = new int[NUM_DELTAS];
    private int currentDelta = 0;

	private void updateFps(int delta) {
        deltas[currentDelta] = delta;
        ++currentDelta;
        if (currentDelta >= NUM_DELTAS){
            currentDelta = 0;
        }
        int deltaTotal = 0;
        for (int i = 0; i < NUM_DELTAS; i++) {
            deltaTotal += deltas[i];
        }
		if (deltaTotal != 0) {
			lastFps = 1000d / ((double) deltaTotal/NUM_DELTAS);
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
			/*AUnit en2 = new AUnit(gameArea, 115, 300, rp.getModUnitByName("default", "SimpleUnit2"), p1);
			gameArea.add(en2);

			AUnit e1 = new AUnit(gameArea, 115, 400, rp.getModUnitByName("default", "SimpleUnit2"), p2);
			gameArea.add(e1);

			AUnit e3 = new AUnit(gameArea, 615, 50, rp.getModUnitByName("default", "SimpleUnit"), p2);
			gameArea.add(e3);

			AUnit u1 = new AUnit(gameArea, 615, 100, rp.getModUnitByName("default", "SimpleUnit"), p1);
			gameArea.add(u1);
			AUnit u2 = new AUnit(gameArea, 715, 100, rp.getModUnitByName("default", "SimpleUnit2"), p1);
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
				gameArea.add(new AUnit(gameArea, (i * 100) + 50, 250, su1, p1));
			}

			UnitResourceHolder su2 = rp.getModUnitByName("default", "SimpleUnit2");
			for (int i = 0; i <= 6; i++) {
				gameArea.add(new AUnit(gameArea, (i * 100) + 50, 450, su2, p2));
			}*/

            Unit b2 = new Unit(gameArea, 515, 200, rp.getModUnitByName("default", "SimpleBuilding"), p2);
			gameArea.add(b2);

			Unit b1 = new Unit(gameArea, 515, 100, rp.getModUnitByName("default", "SimpleBuilding"), p1);
			gameArea.add(b1);

            gameArea.add(new TerrainObject(gameArea, 150, 50, rp.getModTerrainObjectByName("default", "LongTObject"), null));
            gameArea.add(new TerrainObject(gameArea, 250, 50, rp.getModTerrainObjectByName("default", "TallTObject"), null));
            gameArea.add(new TerrainObject(gameArea, 250, 150, rp.getModTerrainObjectByName("default", "TallTObject"), null));
            gameArea.add(new TerrainObject(gameArea, 250, 250, rp.getModTerrainObjectByName("default", "TallTObject"), null));
            gameArea.add(new TerrainObject(gameArea, 250, 350, rp.getModTerrainObjectByName("default", "TallTObject"), null));
            gameArea.add(new TerrainObject(gameArea, 350, 350, rp.getModTerrainObjectByName("default", "LargeTObject"), null));

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
