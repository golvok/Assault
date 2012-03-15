/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding.moving;

import assault.game.util.pathfinding.PathFindingGridObject;
import assault.game.util.pathfinding.RawPathFinder;
import assault.util.Point;

/**
 *
 * @author matt
 */
public interface Relocatable extends PathFindingGridObject{

    boolean[][] getClosedSet();

    boolean[][] getExamined();

    boolean[][] getOnOpenSet();

    boolean[][] getOnPath();

    double getMovementSpeed();

    void setLocation(Point p);

    double getX();

    void setX(double x);

    double getY();

    void setY(double y);

    Point getLocation();

    void setClosedSet(boolean[][] closed);

    void setExamined(boolean[][] exa);

    void setOnOpenSet(boolean[][] open);

    void setOnPath(boolean[][] onPath);

	public RawPathFinder getPathFinder();
}
