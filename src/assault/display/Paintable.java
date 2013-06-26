package assault.display;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glColor4ub;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.util.ReadableColor;
import org.newdawn.slick.geom.Shape;

import assault.game.loading.Texture;
import assault.util.Disposable;
import assault.util.Point;
import assault.util.Updateable;

/**
 *
 * @author matt
 */
public abstract class Paintable extends Bounded_Impl implements Disposable, Updateable {

    private boolean visible;
    private Container<? extends Bounded> parent;

    public Paintable(float x, float y, float width, float height) {
    	super(x, y, width, height);
        this.visible = true;
    }

    public final void adjustMatrixAndDrawSelf() {
        if (isVisible()) {
            glPushMatrix();
            glTranslated(getX(), getY(), 0);
            drawSelf();
            glPopMatrix();
        }
    }

    public abstract void drawSelf();

    @Override
    public final void update(int delta) {
        updateSelf(delta);
    }

    public void updateSelf(int delta) {
    }

    public void setVisible(boolean b) {
        visible = b;
    }

    public boolean isVisible() {
        return visible;
    }
    
    public Container<? extends Bounded> getParent() {
        return parent;
    }

    public void setParent(Container<? extends Bounded> parent) {
        if (getParent() != null) {
            getParent().removeChild(this);
        }
        this.parent = parent;
    }
    private boolean disposed = false;

    @Override
    public synchronized void dispose() {
        if (!isDisposed()) {
            if (getParent() != null) {
                getParent().removeChild(this);
            }
            disposed = true;
            //System.out.println("AP_DISPOSE : "+this);
        }
    }

    /**
     * @return disposed
     */
    @Override
    final public boolean isDisposed() {
        return disposed;
    }
    
    
    //----------------Static methods for OpenGL drawing.-----------------

    public static void drawRect(float x, float y, float w, float h) {
        //System.out.println(w+","+h);
        glBegin(GL_LINE_STRIP);
        glVertex2f(x, y);
        glVertex2f(x , y + h);
        glVertex2f(x + w, y + h);
        glVertex2f(x + w, y);
        glVertex2f(x, y);
        glEnd();
        //drawLine(x, y, x+w, y+h);
    }

    /**
     * draws the inclusive edges of the Paintable
     *
     * @param ap
     */
    public static void drawBoundingBox(Paintable ap) {
        drawRect(1, 0, ap.getWidth() - 1, ap.getHeight() - 1);
//		System.out.println("bounding box of: " + ap);
    }

    public static void fillRect(float x, float y, float w, float h) {
        //System.out.println("fillrect");
        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x, y + h);
        glVertex2f(x + w, y + h);
        glVertex2f(x + w, y);
        glEnd();
    }

    public static void drawLine(float x1, float y1, float x2, float y2) {
        glBegin(GL_LINES);
        glVertex2f(x1, y1);
        glVertex2f(x2, y2);
        glEnd();
    }

    public static void drawPolygon(Point[] points, boolean close) {
        glBegin(GL_LINES);
        for(Point p : points){
        	glVertex2f(p.getX(), p.getY());
        }
        if(close){
        	glVertex2f(points[0].getX(), points[0].getY());
        }
        glEnd();
    }
    
    public static void drawPolygon(Shape shape) {
    	glBegin(GL_LINES);
    	if(shape.getPointCount() == 0){
    		return;
    	}
		for(int i = 0;i<shape.getPoints().length;){
			glVertex2f(shape.getPoints()[i],shape.getPoints()[++i]);
			++i;
		}
		if(shape.closed()){
			glVertex2f(shape.getPoints()[0],shape.getPoints()[1]);
		}
		glEnd();
	}

    public static void setColour(ReadableColor c) {
        glColor4ub(c.getRedByte(), c.getGreenByte(), c.getBlueByte(), c.getAlphaByte());
    }

    public static void drawTexture(float x, float y, float w, float h, int TexID) {
        glBindTexture(GL_TEXTURE_2D, TexID);
        setColour(ReadableColor.WHITE);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 1);
            glVertex2f(x, y);
            glTexCoord2f(0, 0);
            glVertex2f(x, y + h);
            glTexCoord2f(1, 0);
            glVertex2f(x + w, y + h);
            glTexCoord2f(1, 1);
            glVertex2f(x + w, y);
        }
        glEnd();
    }

    /**
     * draws at (x,y) with dimensions (w,h), filling this rectangle with the
     * <i>whole image area</i> of the texture not the whole texture. uses
     * Texture.getTop() and getLeft()
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @param tex
     */
    public static void drawTexture(float x, float y, float w, float h, Texture tex) {
        glBindTexture(GL_TEXTURE_2D, tex.getTexID());
        setColour(ReadableColor.WHITE);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, tex.getTop());
            glVertex2f(x, y);
            glTexCoord2f(0, 0);
            glVertex2f(x, y + h);
            glTexCoord2f(tex.getLeft(), 0);
            glVertex2f(x + w, y + h);
            glTexCoord2f(tex.getLeft(), tex.getTop());
            glVertex2f(x + w, y);
        }
        glEnd();
    }
}
