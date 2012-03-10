/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.display;

import assault.game.loading.Texture;
import assault.util.Disposable;
import java.awt.*;
import java.awt.geom.PathIterator;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.ReadableColor;

/**
 *
 * @author matt
 */
public abstract class APaintable implements Disposable {

	private int x;
	private int y;
	private int width;
	private int height;
	private boolean visible;
	private AContainer parent;

	public APaintable(int x, int y, int width, int height) {
		setVisible(true);
		setLocation(x, y);
		setSize(width, height);
	}

	public final void draw() {
		glPushMatrix();
		glTranslatef(x, y, 0);
		if (isVisible()) {
			drawSelf();
		}
		glPopMatrix();
	}

	public abstract void drawSelf();

	public int getWidth() {
		return width;
	}

	public int getHeight() {
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
		return new Rectangle(x, y, width, height);
	}

	public Dimension getSize() {
		return new Dimension(width, height);
	}

	public Dimension getSize(Dimension rv) {
		if (rv == null) {
			return new Dimension(width, height);
		} else {
			rv.setSize(width, height);
			return rv;
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setSize(Dimension d) {
		setSize(d.width, d.height);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setBounds(Rectangle r) {
		setBounds(r.x, r.y, r.width, r.height);
	}

	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setLocation(Point p) {
		setLocation(p.x, p.y);
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setVisible(boolean b) {
		visible = b;
	}

	public boolean isVisible() {
		return visible;
	}

	public static void drawRect(int x, int y, int w, int h) {
		//System.out.println(w+","+h);
		glBegin(GL_LINE_STRIP);
		glVertex2i(x, y);
		glVertex2i(x, y + h);
		glVertex2i(x + w, y + h);
		glVertex2i(x + w, y);
		glVertex2f(x, y);
		glEnd();
		//drawLine(x, y, x+w, y+h);
	}

	/**
	 * the APaintable is <i>contained</i> in the box this function draws
	 * @param ap
	 */
	public static void drawConatiningBox(APaintable ap) {
		drawRect(0, 0, ap.getWidth() + 1, ap.getWidth() + 1);
	}

	public static void fillRect(int x, int y, int w, int h) {
		//System.out.println("fillrect");
		glBegin(GL_QUADS);
		glVertex2i(x, y);
		glVertex2i(x, y + h);
		glVertex2i(x + w, y + h);
		glVertex2i(x + w, y);
		glEnd();
	}

	public static void drawLine(int x1, int y1, int x2, int y2) {
		glBegin(GL_LINES);
		glVertex2i(x1, y1);
		glVertex2i(x2, y2);
		glEnd();
	}

	public static void drawPolygon(Polygon pol) {
		glBegin(GL_LINES);
		float[] points = new float[6];
		PathIterator pi = pol.getPathIterator(null);
		loop:
		{
			while (true) {
				switch (pi.currentSegment(points)) {
					case PathIterator.SEG_MOVETO:
					case PathIterator.SEG_LINETO:
						glVertex2f(points[0], points[1]);
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

	public static void drawTexture(int x, int y, int w, int h, int TexID) {
		glBindTexture(GL_TEXTURE_2D, TexID);
		setColour(ReadableColor.WHITE);
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0, 0);
			glVertex2i(x, y);
			glTexCoord2f(0, 1);
			glVertex2i(x, y + h);
			glTexCoord2f(1, 1);
			glVertex2i(x + w, y + h);
			glTexCoord2f(1, 0);
			glVertex2i(x + w, y);
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
	public static void drawTexture(int x, int y, int w, int h, Texture tex) {
		glBindTexture(GL_TEXTURE_2D, tex.getTexID());
		setColour(ReadableColor.WHITE);
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0, tex.getTop());
			glVertex2i(x, y);
			glTexCoord2f(0, 0);
			glVertex2i(x, y + h + 1);
			glTexCoord2f(tex.getLeft(), 0);
			glVertex2i(x + w + 1, y + h + 1);
			glTexCoord2f(tex.getLeft(), tex.getTop());
			glVertex2i(x + w + 1, y);
		}
		glEnd();
	}

	public AContainer getParent() {
		return parent;
	}

	public void setParent(AContainer parent) {
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
	 * @return the disposed
	 */
	@Override
	final public boolean isDisposed() {
		return disposed;
	}
}
