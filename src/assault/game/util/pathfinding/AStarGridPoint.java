
package assault.game.util.pathfinding;

/**
 *
 * @author matt
 */
public class AStarGridPoint extends PathFindingGridPoint {

    private float h;
    private float g;

    public AStarGridPoint(int x, int y, float g, float h) {
        super(x, y, g + h);
        setH(h);
        setG(g);
    }

    public AStarGridPoint(int x, int y) {
        super(x, y);
    }

    public final float getH() {
        return h;
    }

    public final void setH(float h) {
        this.h = h;
    }

    public final float getG() {
        return g;
    }

    public final void setG(float g) {
        this.g = g;
    }

}
