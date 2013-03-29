package assault.display;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import assault.util.Point;

public class Bounded {

	protected double x;
	protected double y;
	protected double width;
	protected double height;

	public Bounded(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

	public double getWidth() {
	    return width;
	}

	public double getHeight() {
	    return height;
	}

	public Point getLocation() {
	    return new Point(x, y);
	}

	public Point getLocation(Point rv) {
	    if (rv == null) {
	        return new Point(x, y);
	    } else {
	        rv.setLocation(x, y);
	        return rv;
	    }
	}

	public Shape getBounds() {
	    return new Rectangle2D.Double(x, y, width, height);
	}

	public Point getSize() {
	    return new Point(width, height);
	}

	public Point getSize(Point rv) {
	    if (rv == null) {
	        return new Point(width, height);
	    } else {
	        rv.setLocation(width, height);
	        return rv;
	    }
	}

	public double getX() {
	    return x;
	}

	public void setX(double x) {
	    this.x = x;
	}

	public double getY() {
	    return y;
	}

	public void setY(double y) {
	    this.y = y;
	}

	public void setSize(Point d) {
	    setSize(d.getX(), d.getY());
	}

	public void setSize(double width, double height) {
	    this.width = width;
	    this.height = height;
	}

	public void setBounds(Rectangle2D r) {
	    setBounds(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public void setBounds(double x, double y, double width, double height) {
	    this.x = x;
	    this.y = y;
	    this.width = width;
	    this.height = height;
	}

	public void setLocation(Point p) {
	    setLocation(p.getX(),p.getY());
	}

	public void setLocation(double x, double y) {
	    this.x = x;
	    this.y = y;
	}

}