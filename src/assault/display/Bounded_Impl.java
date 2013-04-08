package assault.display;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import assault.util.Point;

public class Bounded_Impl implements Bounded{

	protected double x;
	protected double y;
	protected double width;
	protected double height;

	public Bounded_Impl(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

	@Override
	public double getWidth() {
	    return width;
	}

	@Override
	public double getHeight() {
	    return height;
	}

	@Override
	public Point getLocation() {
	    return new Point(x, y);
	}

	@Override
	public Point getLocation(Point rv) {
	    if (rv == null) {
	        return new Point(x, y);
	    } else {
	        rv.setLocation(x, y);
	        return rv;
	    }
	}

	@Override
	public Shape getBounds() {
	    return new Rectangle2D.Double(x, y, width, height);
	}

	@Override
	public Point getSize() {
	    return new Point(width, height);
	}

	@Override
	public Point getSize(Point rv) {
	    if (rv == null) {
	        return new Point(width, height);
	    } else {
	        rv.setLocation(width, height);
	        return rv;
	    }
	}

	@Override
	public double getX() {
	    return x;
	}

	@Override
	public void setX(double x) {
	    this.x = x;
	}

	@Override
	public double getY() {
	    return y;
	}

	@Override
	public void setY(double y) {
	    this.y = y;
	}

	@Override
	public void setSize(Point d) {
	    setSize(d.getX(), d.getY());
	}

	@Override
	public void setSize(double width, double height) {
	    this.width = width;
	    this.height = height;
	}

	@Override
	public void setBounds(Rectangle2D r) {
	    setBounds(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public void setBounds(double x, double y, double width, double height) {
	    this.x = x;
	    this.y = y;
	    this.width = width;
	    this.height = height;
	}

	@Override
	public void setLocation(Point p) {
	    setLocation(p.getX(),p.getY());
	}

	@Override
	public void setLocation(double x, double y) {
	    this.x = x;
	    this.y = y;
	}

}