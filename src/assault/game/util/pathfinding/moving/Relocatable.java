/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding.moving;

import assault.game.util.pathfinding.PathFindingBounded;
import assault.game.util.pathfinding.PathingManager;
import assault.util.Point;

/**
 *
 * @author matt
 */
public interface Relocatable extends PathFindingBounded{

    boolean[][] getClosedSet();

    boolean[][] getExamined();

    boolean[][] getOnOpenSet();

    boolean[][] getOnPath();

    double getMovementSpeed();

    void setLocation(Point p);

    Point getLocation();

    void setClosedSet(boolean[][] closed);

    void setExamined(boolean[][] exa);

    void setOnOpenSet(boolean[][] open);

    void setOnPath(boolean[][] onPath);

	public PathingManager getPathingManger();

	/**
	 * set the location in a safe way, ie. if there is some sort
	 * of object managing entity, then check with it to see if 
	 * the new location is valid.
	 * @param add
	 * @return true if the new location is safe, and the set was successful. False otherwise.
	 */
	boolean setLocation_safe(Point add);
}
