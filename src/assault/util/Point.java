/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.util;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 *
 * @author Matt
 */
public class Point {
	public float x;
	public float y;
	
    public Point() {
        this(0, 0);
    }
    public Point(Point p) {
        this(p.getX(), p.getY());
    }
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
		return x;
	}
    
    public float getY() {
		return y;
	}
    
    public static float deltaX(Point p1,Point p2){
        return p2.getX()-p1.getX();
    }
    public static float deltaY(Point p1,Point p2){
        return p2.getY()-p1.getY();
    }
    public static Point delta(Point p1,Point p2){
        return new Point(deltaX(p1, p2), deltaY(p1, p2));
    }
    public static Point multiply(Point p, float constant){
        return new Point(p.getX()*constant, p.getY()*constant);
    }
	public static Point divide(Point p, float constant){
        return new Point(p.getX()/constant, p.getY()/constant);
    }
	public static Point add(Point p1,Point p2){
        return new Point(p1.getX()+p2.getX(), p1.getY()+p2.getY());
    }
    public static double distance(Point p1,Point p2){
        return sqrt(pow(deltaX(p1, p2),2) + pow(deltaY(p1, p2),2));
    }
    public static float distancef(Point p1, Point p2){
    	return (float)distance(p1,p2);
    }
	public static double magnitude(Point p){
		return sqrt(pow(p.getX(), 2)+pow(p.getY(), 2));
	}
	public static float magnitudef(Point p){
		return (float)magnitude(p);
	}
	public static Point unit(Point p){
		return divide(p, (float)magnitude(p));
	}
	public static float dotProduct(Point p1, Point p2){
		return p1.x*p2.x+p1.y*p2.y;
	}
	public static Point project(Point source,Point wall){
		return multiply(unit(wall), dotProduct(wall, source));
	}
	
	public static Point farthestPoint(Point p, Point[] tests){
		int farthestIndex = 0;
		float farthestDistance = distancef(p, tests[0]);
		for(int i = 1;i < tests.length; ++i){
			float distance = distancef(p, tests[i]);
			if(farthestDistance < distance){
				farthestIndex = i;
				farthestDistance = distance;
			}
		}
		return tests[farthestIndex];
	}

	public static final int POSITIVE_DOT_PRODUCT = 0;
	public static final int NEGATIVE_DOT_PRODUCT = 1;
	
	public static Point[] farthestFromLineWithSides(Point p, Point q, Point[] tests){
		Point line = delta(p,q);
		Point[] farthests = new Point[2];
		float[] farthestDistance = new float[2];
		for(int i = 1;i < tests.length; ++i){
			Point relativeToP = delta(p,tests[i]);
			float distance = magnitudef(perpindictularDeltaVectorToLine(line,relativeToP));
			int dotProductSign = dotProduct(line, relativeToP) < 0 ? NEGATIVE_DOT_PRODUCT : POSITIVE_DOT_PRODUCT;
			if(farthestDistance[dotProductSign] < distance){
				farthests[dotProductSign] = tests[i];
				farthestDistance[dotProductSign] = distance;
			}
		}
		return farthests;
	}
	
	public static float distanceToLine(Point onLine_1, Point onLine_2, Point p) {
		return magnitudef(perpindictularDeltaVectorToLine(onLine_1, onLine_2, p));
	}
	
	public static Point perpindictularDeltaVectorToLine(Point onLine_1, Point onLine_2, Point p){
		return perpindictularDeltaVectorToLine(delta(onLine_1, onLine_2),delta(p,onLine_1));
	}
	
	public static Point perpindictularDeltaVectorToLine(Point direction, Point p){
		return delta(p,project(p,direction));
	}
	
	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "(" + getX() + ',' + getY() + ')';
	}
}
