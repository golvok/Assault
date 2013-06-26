package assault.display;

import org.newdawn.slick.geom.Shape;

import assault.util.Point;

public interface Bounded {

	public float getWidth();

	public float getHeight();

	public Point getLocation();
	
	public Point getCenter();

	public float getX();

	public void setX(float x);

	public float getY();

	public void setY(float y);

	public void setLocation(Point p);

	public void setLocation(float x, float y);
	
	public boolean noClip();
	
	public Shape getBounds();
	
	public boolean clipsWith(Bounded test);

	public boolean clipsWith(Shape test);

}