/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.gameObjects;

import java.awt.Image;

import org.lwjgl.util.Color;
import org.newdawn.slick.geom.Polygon;

import assault.display.Bounded_Impl;
import assault.game.display.GameArea;
import assault.util.Point;

/**
 *part of the terrain that can be interacted with (destroyed, created, selected ect.)
 * @author matt
 */
public final class EnvironmentObject extends AObject{

    public EnvironmentObject(GameArea g, int x, int y, Point[] shape, Image miniIcon) {
        super(g, x, y, miniIcon, null, null);
        Polygon newBounds = Bounded_Impl.PolygonFromPoints(shape);
        newBounds.setLocation(x, y);
        this.bounds = newBounds;
    }

    @Override
    public void drawSelf() {
        super.drawSelf();
        setColour(Color.GREY);
        drawPolygon(Bounded_Impl.getAsPoints(getBounds()),true);
        //drawString("E", getWidth() / 2, getHeight() / 2);
        //System.out.println("EO_PAINT");
    }
}
