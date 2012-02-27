/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding;

/**
 *
 * @author matt
 */
public class AStarGridPoint extends PathFindingGridPoint {

    private double h;
    private double g;

    public AStarGridPoint(int x, int y, double g, double h) {
        super(x, y, g + h);
        setH(h);
        setG(g);
    }

    public AStarGridPoint(int x, int y) {
        super(x, y);
    }

    public final double getH() {
        return h;
    }

    public final void setH(double h) {
        this.h = h;
    }

    public final double getG() {
        return g;
    }

    public final void setG(double g) {
        this.g = g;
    }

}
