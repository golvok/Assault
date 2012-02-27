/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.gameObjects;

import assault.game.Player;
import assault.game.display.ACommandButton;
import assault.game.display.GameArea;
import assault.game.loading.resourceHolders.ObjectResourceHolder;
import assault.game.loading.resourceHolders.ResourceException;
import assault.game.util.pathfinding.AStarPathFinder;
import assault.game.util.pathfinding.PathFinder;
import assault.game.util.pathfinding.PathFindingGridObject;
import assault.game.util.pathfinding.PathObject;
import java.awt.Point;

/**
 *
 * @author matt
 */
public class AControllable extends Selectable implements PathFindingGridObject {
	private AGroup aGroup = null;
	boolean[][] closed;
	private ACommandButton[] cmdBtnSet = new ACommandButton[0];
	//for pathfinding
	boolean[][] examined;
	private int nextMoveIndex = 0;
	/**
	 * this array will replaced dynamically, therefore
	 * it should never be referenced directly, and
	 * always copied (through copyNextMoves())when used.
	 * The byte nextMovesHasChanged should be checked for
	 * a different value (or 0) than it was when the array was
	 * first copied to see if it has been updated. this is done
	 * in copyNextMoves()
	 */
	private Point[] nextMoves = new Point[0];
	private Point[] nextMovesCopy = null;
	/**
	 * 0 when no moves, and increments (until max value has been
	 * reached, then back to one) each time the nextMoves
	 * array has changed (when this happens, get a new copy via
	 * copyNextMoves())
	 */
	private byte nextMovesHasChanged = 0;
	/**
	 * the lock associated with nextMoves
	 */
	private final Object nmLock = new Object();
	private byte oldNMHC = 0;
	boolean[][] onPath;
	boolean[][] open;
	PathFinder pf;
	public AControllable(GameArea g, int x, int y, ObjectResourceHolder src, int health, Player owner) throws ResourceException {
		super(g, x, y, src, health, owner);
	}
	/*public AControllable(GameArea g, int x, int y, ObjectResourceHolder src, Player owner) throws ResourceException {
		super(g, x, y, src, owner);
	}*/
	public AControllable(GameArea g, Player owner){
		super(g, owner);
	}

	@Override
	public synchronized void dispose() {
		super.dispose();
	}

	public void advance() {
		//System.out.println("AU_ADVANCE");
		synchronized (nmLock) {
			//System.out.println("AU_ADVANCE_GOT_LOCK");
			nextMovesCopy = copyNextMoves(oldNMHC, nextMovesCopy);
			if (oldNMHC != nextMovesHasChanged) {
				//move the pointer to the beginning of the array
				nextMoveIndex = 0;
				oldNMHC = nextMovesHasChanged;
			}
		}
		//System.out.println("AU_ADVANCE_RELINQUISHED_LOCK");
		if (nextMovesCopy != null) {
			if (nextMoveIndex < nextMovesCopy.length) {
				if (getGA().getGM().notifyOfImminentMovement(this, getLocation(), nextMovesCopy[nextMoveIndex])) {
					//System.out.println("AU_ADVANCING");
					setLocation(nextMovesCopy[nextMoveIndex]);
					resizeAGroup();
					correctStatDispBoxPos();
					nextMoveIndex++;
					//System.out.println("AU_MOVED");
				}// else {
				//System.out.println("AU_NOT_MOVED");
				//TODO put some logic here to recalculate path
				//}
				//System.out.println("AU_NOT_MOVED");
				//TODO put some logic here to recalculate path
				//}
			} else {
				//when there are no moves left
				nextMoveIndex = 0;
				nextMovesCopy = null;
				//System.out.println("AU_ADVANCE_NOT_MOVED(NONE_LEFT)");
			}
		}//else{
		//the list is null(@see copyNextMoves())
		//System.out.println("AU_ADVANCE_NOT_MOVED(NULL)");
		//}
		//the list is null(@see copyNextMoves())
		//System.out.println("AU_ADVANCE_NOT_MOVED(NULL)");
		//}
	}

	public void changeGroup(AGroup ag) {
		registerGroup(ag);
		ag.addControllable(this);
	}

	/**
	 * Copies and returns the nextMoves Point array for use with moving.
	 * The oldNMHC parameter is compared to nextMovesHasChanged, if it differs
	 * this returns the new Point[]. If it is the same, this returns the
	 * currentPoints parameter. If nextMovesHasChanged == 0, then this returns null.
	 * <p>
	 * Note that the nextMoves array should only be used directly in a
	 * synchronized context on the lock NM_LOCK.
	 *
	 * @param oldNMHC the old
	 * @param currentPoints the current Point[] that
	 * @return a copy of nextMoves if olNMC is different, or null if nextMovesHasChanged == 0
	 */
	private Point[] copyNextMoves(byte oldNMHC, Point[] currentPoints) {
		synchronized (nmLock) {
			if (nextMovesHasChanged == 0) {
				return null;
			} else {
				if (nextMovesHasChanged == oldNMHC) {
					return currentPoints;
				} else {
					Point[] pts = new Point[nextMoves.length];
					System.arraycopy(nextMoves, 0, pts, 0, nextMoves.length);
					return pts;
				}
			}
		}
	}

	@Override
	public boolean[][] getClosedSet() {
		return closed;
	}

	public ACommandButton[] getCmdBtnSet() {
		return cmdBtnSet;
	}

	@Override
	public boolean[][] getExamined() {
		return examined;
	}

	@Override
	public boolean[][] getOnOpenSet() {
		return open;
	}

	@Override
	public boolean[][] getOnPath() {
		return onPath;
	}

	/**
	 * adjust x co-ord by <code>x</code> and y co-ord by <code>y</code>. (they can be negative)
	 */
	public void moveBy(int x, int y) {
		moveTo(getX() + x, getY() + y);
	}

	public void moveTo(Point p) {
		if (p != null) {
			moveTo(p.x, p.y);
		}
	}

	/**
	 * try to get as close as possible to (x,y)
	 * @param x
	 * @param y
	 */
	public void moveTo(final int x, final int y) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (pf != null) {
					pf.cancel();
				}
				pf = new AStarPathFinder(getGA().getGM());
				PathObject po = pf.findPath(AControllable.this, x, y);
				pf = null;
				if (po != null) {
					synchronized (nmLock) {
						nextMoves = po.points;
						if (nextMovesHasChanged == Byte.MAX_VALUE) {
							nextMovesHasChanged = 1;
						} else {
							nextMovesHasChanged++;
						}
					}
				} else {
					synchronized (nmLock) {
						nextMovesHasChanged = 0; //causes copyNextMoves() to return null
						//causes copyNextMoves() to return null
						nextMoves = null;
					}
				}
			}
		}).start();
	}

	/**
	 * add this to <code>ag</code>, while overwriting and removing
	 * it from the previous <code>AGroup</code> (if there was one)
	 * @param ag <code>AGroup</code> to be added to
	 */
	public void registerGroup(AGroup ag) {
		if (aGroup != ag) {
			removeFromGroup();
			aGroup = ag;
		}
	}

	public void removeFromGroup() {
		if (aGroup != null) {
			aGroup.removeControllable(this);
			aGroup = null;
		}
	}

	private void resizeAGroup() {
		if (aGroup != null) {
			aGroup.resizeToControllables();
		}
	}

	@Override
	public void setClosedSet(boolean[][] closed) {
		this.closed = closed;
	}

	public final void setCmdBtnSet(ACommandButton[] acbs) {
		if (acbs != null) {
			cmdBtnSet = acbs;
		} else {
			cmdBtnSet = new ACommandButton[0];
		}
	}

	@Override
	public void setExamined(boolean[][] exa) {
		examined = exa;
	}

	@Override
	public void setOnOpenSet(boolean[][] open) {
		this.open = open;
	}

	@Override
	public void setOnPath(boolean[][] onPath) {
		this.onPath = onPath;
	}
}
