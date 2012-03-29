/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding.moving;

import assault.game.util.pathfinding.RawPathObject;
import java.util.ArrayDeque;
import java.util.Deque;
import assault.util.Point;

/**
 * really just a wrapper around a deque of points with some useful utility
 * methods, but conceals some that aren't needed. Supposed to be a more abstract
 * representation of a path, more like a list of waypoints than a path. However,
 * it is expected to be used as a place for those "intermediate" points as well.
 * (that's why it is a deque)
 *
 * @author matt
 */
public class AbstractPathObject {

	private Deque<Point> points;

	public AbstractPathObject(RawPathObject rpo) {
		if (rpo != null) {
			points = new ArrayDeque<Point>(rpo.points.length);
			addPoints(rpo);
		} else {
			points = new ArrayDeque<Point>();
		}
	}

	public AbstractPathObject() {
		this(null);
	}

	public final Point peek() {
		return points.peek();
	}

	public final Point pop() {
		return points.pop();
	}

	public final void addPoint(Point p) {
		points.push(p);
	}

	public final void addPoints(Deque<Point> newPoints) {
		for (Point p : newPoints) {
			addPoint(p);
		}
	}

	public final void addPoints(RawPathObject rpo) {
		if (rpo != null) {
			for (int i = rpo.points.length - 1; i >= 0; --i) {
				Point p = rpo.points[i];
				addPoint(p);
			}
		}
	}

	public final void clear() {
		points.clear();
	}
}
