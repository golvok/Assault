/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding.moving;

import java.awt.Point;

/**
 *
 * @author Faith
 */
public interface Relocatable {

    boolean[][] getClosedSet();

    boolean[][] getExamined();

    boolean[][] getOnOpenSet();

    boolean[][] getOnPath();

    void setLocation(Point p);

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    Point getLocation();

    void setClosedSet(boolean[][] closed);

    void setExamined(boolean[][] exa);

    void setOnOpenSet(boolean[][] open);

    void setOnPath(boolean[][] onPath);
}
