package assault.display;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import assault.util.Point;

public interface Bounded {

	public abstract double getWidth();

	public abstract double getHeight();

	public abstract Point getLocation();

	public abstract Point getLocation(Point rv);

	public abstract Shape getBounds();

	public abstract Point getSize();

	public abstract Point getSize(Point rv);

	public abstract double getX();

	public abstract void setX(double x);

	public abstract double getY();

	public abstract void setY(double y);

	public abstract void setSize(Point d);

	public abstract void setSize(double width, double height);

	public abstract void setBounds(Rectangle2D r);

	public abstract void setBounds(double x, double y, double width, double height);

	public abstract void setLocation(Point p);

	public abstract void setLocation(double x, double y);
	
	public abstract boolean noClip();

}