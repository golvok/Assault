package assault.game.gameObjects;

import assault.game.Player;
import assault.game.display.CommandButton;
import assault.game.display.GameArea;
import assault.game.loading.resourceHolders.ControllableResourceHolder;
import assault.game.loading.resourceHolders.ResourceException;
import assault.game.util.pathfinding.PathingManager;
import assault.game.util.pathfinding.moving.AbstractPathObject;
import assault.game.util.pathfinding.moving.Mover;
import assault.game.util.pathfinding.moving.Relocatable;
import assault.util.Point;

/**
 *
 * @author matt
 */
public abstract class Controllable extends Selectable implements Relocatable {

    private Group aGroup = null;
    private CommandButton[] cmdBtnSet = new CommandButton[0];
    private AbstractPathObject path = new AbstractPathObject();
    
    //for pathfinding visualization
    private boolean[][] closed;
    private boolean[][] examined;
    private boolean[][] onPath;
    private boolean[][] open;

    public Controllable(GameArea g, float x, float y, ControllableResourceHolder src, int health, Player owner) throws ResourceException {
        super(g, x, y, src, health, owner);
        noClip = src.noClip();
		setCmdBtnSet(src.getCmdBtns());
    }

    @Override
    public void updateSelf(int delta) {
    	Mover.advance(this, delta);
        super.updateSelf(delta);
    }

	@Override
	public void setLocation(Point p) {
		System.out.println(this + " Moving to: " + p);
		super.setLocation(p);
	}
	
	@Override
	public boolean setLocation_safe(Point p) {
		if (getGA().getOM().notifyOfImminentMovement(this, p)) {
			setLocation(p);
			return true;
		} else {
			return false;
		}
	}

    @Override
    public synchronized void dispose() {
        super.dispose();
    }

    public void changeGroup(Group ag) {
        registerGroup(ag);
        ag.addControllable(this);
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

    @Override
    public boolean canMakeItToPointInStraightLine(Point test) {
    	return AbstractPathObject.canMakeItToPointInStraightLine(test, this, getGA().getOM());
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

    @SuppressWarnings("unused")
	private void resizeAGroup() {
        if (aGroup != null) {
            aGroup.resizeToControllables();
        }
    }

    @Override
    public void setClosedSet(boolean[][] closed) {
        this.closed = closed;
    }

	/**
	 * doesn't copy a new array
	 * @param acbs 
	 */
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

	@Override
	public PathingManager getPathingManger() {
		return getGA().getPM();
	}
	
	@Override
	public AbstractPathObject getPath() {
		return path;
	}

}
