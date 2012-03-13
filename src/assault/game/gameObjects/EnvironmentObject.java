/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.gameObjects;

import assault.game.display.GameArea;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import org.lwjgl.util.Color;

/**
 *part of the terrain that can be interacted with (destroyed, created, selected ect.)
 * @author matt
 */
public final class EnvironmentObject extends AObject{

    private Polygon shape = null;
	private boolean selected = false;
	private Image miniIcon = null;

    public EnvironmentObject(GameArea g, int x, int y, Point[] shape, Image miniIcon) {
        super(g, x, y, miniIcon, null, null);
        setLocation(x, y);
        int[] xPoints = new int[shape.length];
        int[] yPoints = new int[shape.length];
        for (int i = 0; i < shape.length; i++) {
            xPoints[i] = shape[i].x;
            yPoints[i] = shape[i].y;
        }
        this.shape = new Polygon(xPoints, yPoints, xPoints.length);

        Rectangle bounds = this.shape.getBounds();
        setSize(bounds.width + 1, bounds.height + 1);
    }

    @Override
    public void drawSelf() {
        super.drawSelf();
        setColour(Color.GREY);
        drawPolygon(shape);
        //drawString("E", getWidth() / 2, getHeight() / 2);
        //System.out.println("EO_PAINT");
    }
}
