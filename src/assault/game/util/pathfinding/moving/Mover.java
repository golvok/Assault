package assault.game.util.pathfinding.moving;

import assault.util.Point;
import static assault.util.Point.*;

/**
 * 
 * @author Matthew Walker
 */
public class Mover {

	private Mover() {
	}

	/**
	 * 
	 * @param delta
	 *            delta time in ms
	 */
	public static void advance(Relocatable target, int delta) {
		AbstractPathObject path = target.getPath();
		// TODO make moving thread safe
		// the checks aren't enough, one time path.peek was null after it was
		// checked
		if (path != null && path.peek() != null) {
			// the speed is given per unit second
			float deltaDistance = delta * target.getMovementSpeed() / 1000f;
			while (deltaDistance > 0 && path.peek() != null) {

				float distanceToNextPoint = distancef(target.getLocation(), path.peek());
				if (deltaDistance - distanceToNextPoint >= 0) {
					moveSafely(target, path.pop());
					deltaDistance -= distanceToNextPoint;
				} else {// can't quite make it (less than 0), linear
						// interpolation
						// offset the current location by the unit vector in the
						// correct direction, multiplied by the wanted magnitude
					Point nextLocation = add(target.getLocation(), multiply(unit(delta(target.getLocation(), path.peek())), deltaDistance));
					moveSafely(target, nextLocation);
					deltaDistance = 0;// to avoid floating point weirdness
					break;
				}
			}
		}
	}

	private static void moveSafely(Relocatable target, Point p) {
		boolean success = false;
		if (target.canMakeItToPointInStraightLine(p)) {
			success = target.setLocation_safe(p);
		}
		if (!success) {
			target.getPathingManger().pathToNextPoint(target);
		}
	}

	public static void moveBy(Relocatable target, float x, float y) {
		moveTo(target, target.getX() + x, target.getY() + y);
	}

	public static void moveTo(Relocatable target, Point p) {
		target.getPath().clear();
		target.getPath().addPoint(p);
		target.getPathingManger().pathToNextPoint(target);
	}

	public static void moveTo(Relocatable target, float x, float y) {
		moveTo(target, new Point(x, y));
	}
}
