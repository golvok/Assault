/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.gameObjects;

import assault.game.Player;
import assault.game.display.CommandButton;
import assault.game.display.GameArea;
import assault.game.loading.resourceHolders.ObjectResourceHolder;
import assault.game.loading.resourceHolders.ResourceException;
import assault.game.util.pathfinding.PathFindingGridObject;
import assault.game.util.pathfinding.moving.Relocatable;
import assault.game.util.pathfinding.moving.Mover;
import java.awt.Point;

/**
 *
 * @author matt
 */
public class Controllable extends Selectable implements PathFindingGridObject, Relocatable {

    private Group aGroup = null;
    boolean[][] closed;
    private CommandButton[] cmdBtnSet = new CommandButton[0];
    //for pathfinding
    boolean[][] examined;
    private int nextMoveIndex = 0;
    /**
     * this array will replaced dynamically, therefore it should never be
     * referenced directly, and always copied (through copyNextMoves())when
     * used. The byte nextMovesHasChanged should be checked for a different
     * value (or 0) than it was when the array was first copied to see if it has
     * been updated. this is done in copyNextMoves()
     */
    private Point[] nextMoves = new Point[0];
    private Point[] nextMovesCopy = null;
    /**
     * 0 when no moves, and increments (until max value has been reached, then
     * back to one) each time the nextMoves array has changed (when this
     * happens, get a new copy via copyNextMoves())
     */
    private byte nextMovesHasChanged = 0;
    /**
     * the lock associated with nextMoves
     */
    private final Object nmLock = new Object();
    private byte oldNMHC = 0;
    boolean[][] onPath;
    boolean[][] open;
    private final Mover mover;

    public Controllable(GameArea g, int x, int y, ObjectResourceHolder src, int health, Player owner) throws ResourceException {
        super(g, x, y, src, health, owner);
        mover = new Mover(this);
    }

    @Override
    public void updateSelf(int delta) {
        getMover().advanceTarget(delta);
        super.updateSelf(delta);
    }

    @Override
    public synchronized void dispose() {
        super.dispose();
    }

    public void changeGroup(Group ag) {
        registerGroup(ag);
        ag.addControllable(this);
    }

    /**
     * Copies and returns the nextMoves Point array for use with moving. The
     * oldNMHC parameter is compared to nextMovesHasChanged, if it differs this
     * returns the new Point[]. If it is the same, this returns the
     * currentPoints parameter. If nextMovesHasChanged == 0, then this returns
     * null. <p> Note that the nextMoves array should only be used directly in a
     * synchronized context on the lock NM_LOCK.
     *
     * @param oldNMHC the old
     * @param currentPoints the current Point[] that
     * @return a copy of nextMoves if olNMC is different, or null if
     * nextMovesHasChanged == 0
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

    public Mover getMover() {
        return mover;
    }

    @Override
    public boolean[][] getClosedSet() {
        return closed;
    }

    public CommandButton[] getCmdBtnSet() {
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
     * add this to
     * <code>ag</code>, while overwriting and removing it from the previous
     * <code>AGroup</code> (if there was one)
     *
     * @param ag
     * <code>AGroup</code> to be added to
     */
    public void registerGroup(Group ag) {
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

    public final void setCmdBtnSet(CommandButton[] acbs) {
        if (acbs != null) {
            cmdBtnSet = acbs;
        } else {
            cmdBtnSet = new CommandButton[0];
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
