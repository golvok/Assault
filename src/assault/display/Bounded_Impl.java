 package assault.display;

import java.lang.reflect.InvocationTargetException;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import assault.util.InclusiveRectangle;
import assault.util.Point;

public class Bounded_Impl implements Bounded{
	
	protected Shape bounds;
	protected boolean noClip = false;

	public Bounded_Impl(Bounded src) {
		this(src.getBounds(),src.noClip());
    }
	
	public Bounded_Impl(Shape bounds, boolean noClip){
		this(bounds);
		this.noClip = noClip;
	}
	
	public Bounded_Impl(Shape bounds){
		Shape newBounds;
		if (bounds instanceof Cloneable){
			try {
				this.bounds = (Shape)bounds.getClass().getMethod("clone").invoke(bounds);
				return;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException 
					| NoSuchMethodException	| SecurityException e) {
				//do nothing, and try the attempts below.
			}
		}
		if(bounds instanceof Rectangle){
			newBounds = new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()); 
		} else if (bounds instanceof Circle){
			newBounds = new Circle(bounds.getCenterX(), bounds.getCenterY(), ((Circle)bounds).radius, bounds.getPointCount());
		} else if (bounds instanceof Ellipse){
			newBounds = new Ellipse(bounds.getCenterX(), bounds.getCenterY(), ((Ellipse)bounds).getRadius1(),((Ellipse)bounds).getRadius2(),bounds.getPointCount());
		} else if (bounds.getPointCount() < 3) {
			throw new IllegalArgumentException("Not enough points (" + bounds.getPointCount() + ") to be a shape with area");
		} else {
			newBounds = new Polygon(bounds.getPoints());
		}		
		this.bounds = newBounds;
	}
	
	public Bounded_Impl(float x, float y, float width, float height, boolean noClip){
		this(x,y,width,height);
		this.noClip = noClip;
	}
	
	public Bounded_Impl(float x, float y, float width, float height){
		this.bounds = new InclusiveRectangle(x, y, width, height);
	}

	@Override
	public float getWidth() {
	    return bounds.getWidth();
	}

	@Override
	public float getHeight() {
	    return bounds.getHeight();
	}

	@Override
	public Point getLocation() {
	    return new Point(getX(), getY());
	}
	
	public Point getCenter(){
		return new Point(bounds.getCenterX(), bounds.getCenterY());
	}
	
	@Override
	public float getX() {
	    return bounds.getX();
	}

	@Override
	public void setX(float x) {
	    bounds.setX(x);
	}

	@Override
	public float getY() {
	    return bounds.getY();
	}

	@Override
	public void setY(float y) {
	    bounds.setY(y);
	}

	@Override
	public void setLocation(Point p) {
	    setLocation(p.getX(),p.getY());
	}

	@Override
	public void setLocation(float x, float y) {
	    bounds.setLocation(x, y);
	}
	
	@Override
	public boolean noClip() {
		return noClip;
	}

	@Override
	public boolean clipsWith(Bounded test) {
		if(test.noClip()){
			return false;
		}else{
			return clipsWith(test.getBounds());
		}
	}
	
	@Override
	public boolean clipsWith(Shape test) {
		if(noClip()){
			return false;
		}else{
			return getBounds().intersects(test);
		}
	}
	
	@Override
	public String toString() {
		String dims = "x = " + getX() + ", y = " + getY() + ", w = " + getWidth() + ", h = " + getHeight() + " #" + hashCode();
		if (getClass() == Bounded_Impl.class){
			return getClass().getSimpleName() + ", " + dims;
		}
		return dims;
	}

	@Override
	public Shape getBounds() {
		return bounds;
	}
	
	public static Point[] getAsPoints(Shape shape){
		float[] points = shape.getPoints();
		Point[] ret = new Point[points.length/2];
		for(int i = 0;i < ret.length;i++){
			ret[i] = new Point(points[i*2], points[i*2 + 1]);
		}
		return ret;
	}	
	
	public static Polygon PolygonFromPoints(Point[] points){
		float[] pointsForPolygon = new float[points.length*2];
		for(int i = 0;i<points.length;++i){
			pointsForPolygon[i*2] = points[i].x;
			pointsForPolygon[i*2 + 1] = points[i].y;
		}
		return new Polygon(pointsForPolygon);
	}
}