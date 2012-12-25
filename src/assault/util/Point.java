/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.util;

import java.awt.geom.Point2D.Double;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 *
 * @author Matt
 */
public class Point extends Double {
	private static final long serialVersionUID = 1L;

    public Point() {
        this(0, 0);
    }
    public Point(Point p) {
        this(p.getX(), p.getY());
    }
    public Point(double x, double y) {
        super(x, y);
    }

    public static double deltaX(Point p1,Point p2){
        return p2.getX()-p1.getX();
    }
    public static double deltaY(Point p1,Point p2){
        return p2.getY()-p1.getY();
    }
    public static Point delta(Point p1,Point p2){
        return new Point(deltaX(p1, p2), deltaY(p1, p2));
    }
    public static Point multiply(Point p, double constant){
        return new Point(p.getX()*constant, p.getY()*constant);
    }
	public static Point divide(Point p, double constant){
        return new Point(p.getX()/constant, p.getY()/constant);
    }
	public static Point add(Point p1,Point p2){
        return new Point(p1.getX()+p2.getX(), p1.getY()+p2.getY());
    }
    public static double distance(Point p1,Point p2){
        return sqrt(pow(deltaX(p1, p2),2) + pow(deltaY(p1, p2),2));
    }
	public static double magnitude(Point p){
		return sqrt(pow(p.getX(), 2)+pow(p.getY(), 2));
	}
	public static Point unit(Point p){
		return divide(p, magnitude(p));
	}
}
