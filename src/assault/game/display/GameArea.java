/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.display;

import assault.display.Paintable;
import assault.display.AssaultWindow;
import assault.display.InputRegistarContainer;
import assault.game.gameObjects.Group;
import assault.game.gameObjects.AObject;
import assault.game.gameObjects.Unit;
import assault.game.gameObjects.Selectable;
import assault.game.util.GridManager;
import assault.game.util.GridObject;
import assault.game.util.TerrainGridManager;
import assault.game.util.commands.*;
import assault.game.util.pathfinding.AStarPathFinder;
import assault.game.util.pathfinding.RawPathFinder;
import assault.game.util.pathfinding.PathFindingGridObject;
import assault.game.util.terrain.TerrainGenerator;
import assault.input.InputEventUtil;
import assault.input.KeyboardEvent;
import assault.input.MouseEvent;
import assault.util.ThreadBlocker;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.lwjgl.util.Color;

/**
 *
 * @author matt
 */
public class GameArea extends InputRegistarContainer {

	public final static Integer GROUP_DEPTH = new Integer(1);
	public final static Integer NORMAL_DEPTH = new Integer(2);
	public final static Integer BULLET_DEPTH = new Integer(3);
	public final static Integer STATUS_BOX_DEPTH = new Integer(4);
	private final List<Unit> units = Collections.synchronizedList(new ArrayList<Unit>(32));
	private AObject[] selected = new AObject[1];
	private int numSelected = 0;
	private MoveCmd gaMover;
	private TerrainGridManager gManager;
	//private Thread movementThread;
	private ThreadBlocker<Point> mouseThreadBlocker = new ThreadBlocker<Point>();
	private ThreadBlocker<AObject> nextAOTargetedThreadBlocker = new ThreadBlocker<AObject>();
	private RawPathFinder pF;
	private final AssaultWindow aWindow;
	private final TerrainGenerator terrainGenerator;

	public GameArea(AssaultWindow aw, TerrainGenerator tg) {
		super(5, 5, 790, 590, 32);//Horray! Magic numbers!
		aWindow = aw;
		terrainGenerator = tg;
		System.out.println("Creating movement thread");
		/*movementThread = new Thread(new Runnable() {

			private Unit[] unitArray = new Unit[0];

			@Override
			public void run() {
				while (!stopMT) {
					//System.out.println("MT_TICK");
					synchronized (units) {
						//System.out.println("MT_GOT_LOCK");
						unitArray = units.toArray(unitArray);
					}
					//System.out.println("MT_RELINQUISH_LOCK");
					for (int i = 0; i < unitArray.length; i++) {
						if (unitArray[i] != null) {
							unitArray[i].getMover().advanceTarget(?);
						}
					}
					synchronized (Thread.currentThread()) {
						try {
							//System.out.println("MT_WAITING");
							Thread.currentThread().wait(100);
						} catch (InterruptedException ex) {
						}
					}
				}
				//movementThread = null;
			}
		});*/

		System.out.println("Crteating gridManager");
		gManager = new TerrainGridManager(getWidth(), getHeight(), 10, terrainGenerator, this);
		System.out.println("creating pathfinder");
		//pF = new DijkstraPathFinder(getGM());
		pF = new AStarPathFinder(getGM());
		//generate terrain
		System.out.println("generating terrain");
		terrainGenerator.generateInto(this, new Random().nextInt());
	}

	@Override
	public synchronized void dispose() {
		if (!isDisposed()) {
			stopMT = true;
			gManager.dispose();
		}
		super.dispose();
	}


	/*
	 * public void add(AStatusDisplayBox asdb) { super.add(asdb,
	 * STATUS_BOX_DEPTH);
	}
	 */
	/**
	 * add(ap,true)
	 *
	 * @param ap
	 * @return
	 * @see add(APaintable ap, boolean addToGM)
	 */
	public boolean add(Paintable ap) {
		return add(ap, true);
	}
	private volatile boolean stopMT = false;

	/**
	 * add an APaintable to this GA. if (
	 * <code>addToGM</code>) AObjects, AGroups and AProjectiles are correctly
	 * added to the GridManager. returns false and does nothing if the GM
	 * refuses to add it &&
	 * <code>addToGM</code>.
	 *
	 * @param ap
	 * @param addToGM add to the gridmanager if if is an AObject but not an
	 * AGroup
	 * @return true if successful, false if not
	 */
	public boolean add(Paintable ap, boolean addToGM) {
		/*if (!movementThread.isAlive()) {
			System.out.println("starting movement thread");
			movementThread.start();
		}*/

		if (ap != null) {
			if (addToGM && ap instanceof AObject && !(ap instanceof Group)) {
				if (!gManager.add((AObject) ap)) {
					System.out.println("unable to add " + ap + " to GA");
					return false;
				}
			}
			/*
			 * if (ap instanceof AProjectile) { addProjectile((AProjectile) ap);
			 * } else if (ap instanceof AGroup) {//this extends AUnit, therefore
			 * addGroup((AGroup) ap); } else
			 */
			if (ap instanceof Unit) {//_this_ must be below AGroup
				units.add((Unit) ap);
			}/*
			 * else if (ap != null) { super.add(ap, NORMAL_DEPTH);
			}
			 */
			addChild(ap);
			return true;
		} else {
			return false;
		}
	}

	/*
	 * private void addProjectile(AProjectile bu) { if (bu != null) {
	 * this.add(bu, BULLET_DEPTH); }
	}
	 */

	/*
	 * private void addGroup(AGroup ag) { if (ag != null) { this.add(ag,
	 * GROUP_DEPTH); }
	}
	 */
	public void add(Paintable[] aps) {
		add(aps, true);
	}

	public void add(Paintable[] aps, boolean addToGM) {
		for (int i = 0; i < aps.length; i++) {
			this.add(aps[i], addToGM);
		}
	}

	public void remove(Paintable ap) {
		removeChild(ap);
	}

	public void remove(AObject ao) {
		if (ao != null && !(ao instanceof Group)) {
			if (ao instanceof Selectable) {
				((Selectable) ao).deselect();
			}
			getGM().remove(ao);
			if (ao instanceof Unit) {
				units.remove((Unit) ao);
			}
			System.out.println("GA: removing " + ao);
			removeChild(ao);
		}
		//System.out.println("GA_REMOVE");
	}

	public void remove(AObject[] aos) {
		for (int i = 0; i < aos.length; i++) {
			remove(aos[i]);
		}
	}

	public void addToSelection(AObject[] aos) {
		for (int i = 0; i < aos.length; i++) {
			addToSelection(aos[i]);
		}
	}

	/**
	 *
	 * @param ao
	 * @return true if it was added successfully false if not.
	 */
	public boolean addToSelection(AObject ao) {
		for (int i = 0; i < selected.length; i++) {
			if (ao == selected[i]) {
				return false;
			}
		}
		int i = 0;
		if (numSelected + 1 > selected.length) {
			AObject[] temp = new AObject[selected.length + 5];
			System.arraycopy(selected, 0, temp, 0, selected.length);
			selected = temp;
			i = numSelected;//starts the loop below on a for-sure 1st null;
			//System.out.print("extended selection array first then ");
		}
		for (; i < selected.length; i++) {
			if (selected[i] == null) {
				selected[i] = ao;
				break;
			}
		}
		//System.out.println("searched for null in selection array");
		numSelected++;
		//System.out.println("numSelected = "+numSelected+" @GA.addToSelection(AObject)");
		getAW().getASDM().addBox(ao);
		getAW().getACDM().setCmdBtns(selected);
		return true;
	}

	public void removeFromSelection(AObject ao) {
		boolean removedSomething = false;
		for (int i = 0; i < selected.length; i++) {
			if (ao.equals(selected[i])) {
				cancelAllUIWaitingThreads();
				selected[i] = null;
				removedSomething = true;
				break;
			}
		}
		if (!removedSomething) {
			System.out.println(ao.getClass() + " wasn't in the selection when GA.removeFromSelection(AObject) was attempted, did nothing");
		} else {
			numSelected--;
			getAW().getACDM().setCmdBtns(getSelection());
			getAW().getASDM().removeBox(ao);
		}
		//System.out.println("numSelected = "+numSelected+" @GA.removeFromSelection(AObject)");
	}

	/**
	 * deselectPseudo()s everything and does the same thing as {@link #removeFromSelection(AObject)}
	 * except more efficiently.
	 */
	public void deselectAll() {
		cancelAllUIWaitingThreads();
		for (int i = 0; i < selected.length; i++) {
			if (selected[i] != null && selected[i] instanceof Selectable) {
				((Selectable) selected[i]).deselectPseudo();//a full deselect would call removeFromSelection(AObject) which would be less efficient
				//System.out.println(selected[i].getClass() +" was deselectPseudo()ed @GA.deselectAll()");
				selected[i] = null;
				numSelected--;
			}
		}
		getAW().getASDM().removeAllBoxes();
		getAW().getACDM().removeAllCmdBtns();
		if (numSelected != 0) {
			System.err.println("numSelected = " + numSelected + " at end of @GA.deselectAll() --- IT WAS NON ZERO!!!\nset to 0.");
			numSelected = 0;
		}
		//System.out.println("numSelected = "+numSelected+" @GA.deselectAll()");
	}

	/**
	 * checks if numSelected is > 0 true if it is, false otherwise
	 *
	 * @return if this
	 * <code>GameArea</code> has a selection, return true. false otherwise.
	 */
	public boolean hasSelection() {
		return numSelected > 0;
	}

	/**
	 * Starts at this instance's parent, checks if it is an instance of the
	 * AGameViewer class, if it is, returns it, if it's not checks the parent's
	 * parent and so on. If any parent is null returns null
	 *
	 * @return First instance of AGameViewer that is found when going up through
	 * this instance's lineage. If any parent is null returns null
	 */
	public AssaultWindow getAW() {
		return aWindow;
	}

	public AObject[] getSelection() {
		AObject[] temp = new AObject[selected.length];
		System.arraycopy(selected, 0, temp, 0, selected.length);
		return temp;
	}

	private Unit[] getAUnitSelection() {
		Unit[] aus = new Unit[getSelection().length];
		int j = 0;
		for (int i = 0; i < selected.length; i++) {
			if (selected[i] instanceof Unit) {
				aus[j] = (Unit) selected[i];
				j++;
			}
		}
		return aus;
	}

	/**
	 * Makes it so that ao is the only thing selected. Really only should be
	 * used from within the GA (keep it private please) seeing as it does not
	 * call select() on ao.
	 *
	 * @param ao
	 */
	private void setSelection(AObject ao) {
		deselectAll();
		selected = new AObject[3];
		selected[0] = ao;
		numSelected++;
		//boxes and cmds are removed in deselectAll()
		getAW().getASDM().addBox(ao);
		getAW().getACDM().setCmdBtns(ao);
	}

	public final GridManager getGM() {
		return gManager;
	}

	public RawPathFinder getPF() {
		return pF;
	}

	@Override
	public void drawSelf() {
		super.drawSelf();
		//System.out.println("GA_PAINT");
		if (numSelected == 1 && selected[0] instanceof PathFindingGridObject && getPF() != null) {
			PathFindingGridObject pfgo = (PathFindingGridObject) selected[0];
			if (pfgo.getExamined() != null && pfgo.getOnOpenSet() != null && pfgo.getOnPath() != null) {
				boolean[][] exa = pfgo.getExamined();
				boolean[][] open = pfgo.getOnOpenSet();
				boolean[][] path = pfgo.getOnPath();
				boolean[][] closed = pfgo.getClosedSet();
				for (int i = 0; i < exa.length; i++) {
					for (int j = 0; j < exa[i].length; j++) {
						if (path[i][j]) {
							setColour(Color.CYAN);
							fillRect(getGM().convGridToPixel(i) + 1, getGM().convGridToPixel(j) + 1, getGM().getGridSize() - 1, getGM().getGridSize() - 1);
						} else if (closed[i][j]) {
							setColour(Color.RED);
							fillRect(getGM().convGridToPixel(i) + 1, getGM().convGridToPixel(j) + 1, getGM().getGridSize() - 1, getGM().getGridSize() - 1);
						} else if (open[i][j]) {
							setColour(Color.BLUE);
							fillRect(getGM().convGridToPixel(i) + 1, getGM().convGridToPixel(j) + 1, getGM().getGridSize() - 1, getGM().getGridSize() - 1);
						} else if (exa[i][j]) {
							setColour(Color.BLACK);
							fillRect(getGM().convGridToPixel(i) + 1, getGM().convGridToPixel(j) + 1, getGM().getGridSize() - 1, getGM().getGridSize() - 1);
						}
					}
				}
			}
		}
	}

	public void executeCommandOnSelectionBySelection(Command cmd) {
		executeCommand(cmd, getSelection(), getAUnitSelection());
	}

	/**
	 * executes the
	 * <code>ACommand</code> on the specified target(s), getting mouse presses
	 * and targets required by the various
	 * <code>ACommand</code> interfaces. NOTE: It will not return until the
	 * command can be fully executed. Ie. until the next mouse press is detected
	 * the the command is executed.
	 *
	 * @param cmd
	 * @param targets
	 */
	public void executeCommand(Command cmd, AObject[] targets, Unit[] executors) {
		if (cmd instanceof CreateCmd) {
			for (int i = 0; i < executors.length; i++) {
				Unit builder = executors[i];
				//System.out.println("executing CreateCmd");
				((CreateCmd) cmd).execute(builder, this);
			}
		} else if (cmd instanceof TargetCommand) {
			AObject target = getNextAObjectTargeted();
			if (target != null) {
				((TargetCommand) cmd).executeOn(targets, target);
			}
		} else if (cmd instanceof MouseCommand) {
			Point mouse = getnextMousePress();
			if (mouse != null) {
				((MouseCommand) cmd).executeOn(targets, mouse);
			}
		} else if (cmd instanceof NormalCommand) {
			((NormalCommand) cmd).execute();
		} else {
			System.out.println("Command didn't implement an interface or that interface isn't implemented in the GA or was null");
		}
	}

	/**
	 * causes the thread to wait until a mouse press has occurred and returns
	 * the location as a point or null if it is canceled.
	 *
	 * @return the location of the next mouse press.
	 */
	synchronized Point getnextMousePress() {
		return mouseThreadBlocker.WaitUntilValue();
	}

	synchronized AObject getNextAObjectTargeted() {
		return nextAOTargetedThreadBlocker.WaitUntilValue();
	}

	public void notifyOfMousePress(int x, int y, int button, int mask) {
		if (mouseThreadBlocker.isAWaitingThread() && button == MouseEvent.BUTTON_RIGHT) {
			mouseThreadBlocker.notifyWaitingThreadWith(new Point(x, y));
		} else if ((mask & MouseEvent.LSHIFT_DOWN_MASK) == 0 && button == MouseEvent.BUTTON_LEFT) {
			deselectAll();
		} else if (button == MouseEvent.BUTTON_RIGHT) {
			if (gaMover == null) {
				gaMover = new MoveCmd(getAW().getRP());
			}
			gaMover.executeOn(selected, x, y);
		}
	}

	public void notifyOfMousePress(AObject objectClicked, MouseEvent e) {
		notifyOfMousePress(e.getX(), e.getY(), e.getButton(), e.getModifiers());
	}

	public void notifyOfTargetiveMousePress(AObject ao) {
		nextAOTargetedThreadBlocker.notifyWaitingThreadWith(ao);
		mouseThreadBlocker.notifyWaitingThreadWith(new Point(ao.getX() + ao.getWidth() / 2, ao.getY() + ao.getHealth() / 2));
	}

	private void cancelAllUIWaitingThreads() {
		nextAOTargetedThreadBlocker.cancelWaitingThread();
		mouseThreadBlocker.cancelWaitingThread();
	}

	public AObject getAoAt(int x, int y) {
		if (getGM() != null) {
			try {
				GridObject go = getGM().getGoAtPixel(x, y);
				if (go instanceof AObject && go.getBounds().contains(x, y)) {
					return ((AObject) go);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}
		}
		return null;
	}

	//====================Mouse Listeners========================

	@Override
	public void accept(MouseEvent me) {
		//System.out.println("GM_ACCEPT "+me);
		AObject ao = getAoAt(me.getX(), me.getY());
		if (ao != null && ao instanceof Selectable) {
			InputEventUtil.passAndTranslateMouseEventTo(ao,me);
		} else if (me.getNewState() == MouseEvent.BUTTON_PRESSED) {
			notifyOfMousePress(me.getX(), me.getY(), me.getButton(), me.getModifiers());
		}
	}
	
	private Selectable lastAsEntered;

	public void mouseMoved(MouseEvent e) {
		AObject ao = getAoAt(e.getX(), e.getY());
		if (ao != null && ao instanceof Selectable) {
			if (ao != lastAsEntered) {
				if (lastAsEntered != null) {
					lastAsEntered.mouseExited(e);
				}
				lastAsEntered = (Selectable) ao;
				((Selectable) ao).mouseEntered(e);
			}
		} else if (lastAsEntered != null) {
			lastAsEntered.mouseExited(e);
			lastAsEntered = null;
		}
	}

	@Override
	public void accept(KeyboardEvent ke) {
		//System.out.println("GA_ACCEPT "+ke);
		super.accept(ke);
	}
}
