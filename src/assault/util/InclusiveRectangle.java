package assault.util;

import org.newdawn.slick.geom.Rectangle;

public class InclusiveRectangle extends Rectangle {

	private static final long serialVersionUID = -4119416009174869838L;

	public InclusiveRectangle(float x, float y, float width, float height) {
		super(x, y, width, height);
	}
	
	@Override
	public boolean contains(float xp, float yp) {
		if (xp < getX()) {
			return false;
		}
		if (yp < getY()) {
			return false;
		}
		if (xp > maxX) {
			return false;
		}
		if (yp > maxY) {
			return false;
		}
		
		return true;
	}

}
