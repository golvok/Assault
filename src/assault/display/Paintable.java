package assault.display;

import assault.game.loading.Texture;
import assault.util.Disposable;
import assault.util.Updateable;
import java.awt.Polygon;
import java.awt.geom.PathIterator;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.ReadableColor;

/**
 *
 * @author matt
 */
public abstract class Paintable extends Bounded_Impl implements Disposable, Updateable {

    private boolean visible;
    private Container<? extends Bounded> parent;

    public Paintable(double x, double y, double width, double height) {
    	super(x, y, width, height);
        this.visible = true;
    }

    public final void draw() {
        if (isVisible()) {
            glPushMatrix();
            glTranslated(x, y, 0);
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

    public static void drawRect(double x, double y, double w, double h) {
        //System.out.println(w+","+h);
        glBegin(GL_LINE_STRIP);
        glVertex2d(x, y);
        glVertex2d(x, y + h);
        glVertex2d(x + w, y + h);
        glVertex2d(x + w, y);
        glVertex2d(x, y);
        glEnd();
        //drawLine(x, y, x+w, y+h);
    }

    /**
     * the APaintable is <i>contained</i> in the box this function draws
     *
     * @param ap
     */
    public static void drawBoundingBox(Paintable ap) {
        drawRect(0, 0, ap.getWidth(), ap.getHeight());
//        System.out.println("bounding box of: " + ap);
    }

    public static void fillRect(double x, double y, double w, double h) {
        //System.out.println("fillrect");
        glBegin(GL_QUADS);
        glVertex2d(x, y);
        glVertex2d(x, y + h);
        glVertex2d(x + w, y + h);
        glVertex2d(x + w, y);
        glEnd();
    }

    public static void drawLine(double x1, double y1, double x2, double y2) {
        glBegin(GL_LINES);
        glVertex2d(x1, y1);
        glVertex2d(x2, y2);
        glEnd();
    }

    public static void drawPolygon(Polygon pol) {
        glBegin(GL_LINES);
        double[] points = new double[6];
        PathIterator pi = pol.getPathIterator(null);
        loop:
        {
            while (true) {
                switch (pi.currentSegment(points)) {
                    case PathIterator.SEG_MOVETO:
                    case PathIterator.SEG_LINETO:
                        glVertex2d(points[0], points[1]);
                        pi.next();
                        break;
                    case PathIterator.SEG_CLOSE:
                        break loop;
                }
            }
        }
        glEnd();
    }

    public static void setColour(ReadableColor c) {
        glColor4ub(c.getRedByte(), c.getGreenByte(), c.getBlueByte(), c.getAlphaByte());
    }

    public static void drawTexture(double x, double y, double w, double h, int TexID) {
        glBindTexture(GL_TEXTURE_2D, TexID);
        setColour(ReadableColor.WHITE);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 1);
            glVertex2d(x, y);
            glTexCoord2f(0, 0);
            glVertex2d(x, y + h);
            glTexCoord2f(1, 0);
            glVertex2d(x + w, y + h);
            glTexCoord2f(1, 1);
            glVertex2d(x + w, y);
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
    public static void drawTexture(double x, double y, double w, double h, Texture tex) {
        glBindTexture(GL_TEXTURE_2D, tex.getTexID());
        setColour(ReadableColor.WHITE);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, tex.getTop());
            glVertex2d(x, y);
            glTexCoord2f(0, 0);
            glVertex2d(x, y + h);
            glTexCoord2f(tex.getLeft(), 0);
            glVertex2d(x + w, y + h);
            glTexCoord2f(tex.getLeft(), tex.getTop());
            glVertex2d(x + w, y);
        }
        glEnd();
    }
}
