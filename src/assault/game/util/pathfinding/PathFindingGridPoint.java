
package assault.game.util.pathfinding;

/**
 *
 * @author Matthew Walker
 */
public class PathFindingGridPoint {

	private final int x;
	private final int y;
	private float f;

	public PathFindingGridPoint(int x, int y, float f) {
		this(x, y);
		setF(f);
	}

	public PathFindingGridPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PathFindingGridPoint) {
			return equals((PathFindingGridPoint) o);
		} else {
			return false;
		}
	}

	public boolean equals(PathFindingGridPoint pt) {
		if (pt == null) {
			return false;
		} else {
			return pt.x == x && pt.y == y;
		}
	}

	public final float getF() {
		return f;
	}

	public final void setF(float f) {
		this.f = f;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}
}
