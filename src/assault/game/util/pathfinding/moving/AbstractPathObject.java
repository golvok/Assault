/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding.moving;

import static assault.display.Bounded_Impl.PolygonFromPoints;
import static assault.display.Bounded_Impl.getAsPoints;
import static assault.util.Point.farthestFromLineWithSides;
import static assault.util.Point.farthestPoint;

import java.util.ArrayDeque;
import java.util.Deque;

import org.newdawn.slick.geom.Shape;

import assault.display.Bounded;
import assault.display.Bounded_Impl;
import assault.game.util.ObjectManager;
import assault.game.util.pathfinding.RawPathObject;
import assault.util.Point;

/**
 * really just a wrapper around a deque of points with some useful utility
 * methods, but conceals some that aren't needed. Supposed to be a more abstract
 * representation of a path, more like a list of waypoints than a path. However,
 * it is expected to be used as a place for those "intermediate" points as well.
 * (that's why it is a deque, you can put more points in front of the current points
 * _and_ behind them)
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

	public static boolean canMakeItToNextPointInStraightLine(AbstractPathObject pathObject, Bounded test, ObjectManager om){
		return canMakeItToPointInStraightLine(pathObject.peek(), test, om);
	}
	
	public static boolean canMakeItToPointInStraightLine(Point p, Bounded test, ObjectManager om){
		if(test.noClip()){
			return true;
		}else{		
			return ObjectManager.Impl.ListOnlyContains(om.getClippingWith(futureRegionToPoint(p, test, om)),test);
		}
	}
	
	public static Shape futureRegionToPoint(Point nextPoint, Bounded test, ObjectManager om) {
		//TODO optimise. maybe add some sort of cacheing mechanism, an extension to bounded maybe
		Bounded nextRegion = new Bounded_Impl(test);
		nextRegion.setLocation(nextPoint);
		Point currentCenter = test.getCenter();
		Point nextCenter = nextRegion.getCenter();
		Point[] nextPoints = getAsPoints(nextRegion.getBounds());
		Point[] currentPoints = getAsPoints(test.getBounds());
		
		Point farthestInNext = farthestPoint(currentCenter,nextPoints);
		Point farthestInCurrent = farthestPoint(nextCenter,currentPoints);
		Point[] extremitiesOfNext = farthestFromLineWithSides(currentCenter, nextCenter, nextPoints);
		Point[] extremitiesOfCurrent = farthestFromLineWithSides(currentCenter, nextCenter, currentPoints);
		
		int nPoints = 6;
		boolean addFarthestInCurrent = true;
		if(farthestInCurrent.equals(extremitiesOfCurrent[0]) || farthestInCurrent.equals(extremitiesOfCurrent[1])){
			nPoints--;
			addFarthestInCurrent = false;
		}
		boolean addFarthestInNext = true;
		if(farthestInNext.equals(extremitiesOfNext[0]) || farthestInNext.equals(extremitiesOfNext[1])){
			nPoints--;
			addFarthestInNext = false;
		}
		
		Point[] futureRegion = new Point[nPoints];
		
		int nPointsAdded = 0;

		futureRegion[nPointsAdded] = extremitiesOfCurrent[0];
		futureRegion[nPointsAdded+1] = extremitiesOfNext[0];
		nPointsAdded += 2;
		
		if (addFarthestInNext){
			futureRegion[nPointsAdded] = farthestInNext;
			nPointsAdded++;
		}
		
		futureRegion[nPointsAdded] = extremitiesOfNext[1];
		futureRegion[nPointsAdded+1] = extremitiesOfCurrent[1];
		nPointsAdded += 2;

		if (addFarthestInCurrent){
			futureRegion[nPointsAdded] = farthestInCurrent;
//			nPointsAdded++;
		}
		
		return PolygonFromPoints(futureRegion);
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
