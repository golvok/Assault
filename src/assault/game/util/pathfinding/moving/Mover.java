/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding.moving;

import java.awt.Point;

/**
 *
 * @author Faith
 */
public class Mover {

    Relocatable target;

    public Mover(Relocatable r) {
        setTarget(r);
    }

    public void advanceTarget(int delta) {
    }

    public void moveBy(int x, int y) {
        moveTo(target.getX() + x, target.getY() + y);
    }

    public void moveTo(Point p) {
    }

    public void moveTo(int x, int y) {
        moveTo(new Point(x, y));
    }

    private void setTarget(Relocatable target) {
        this.target = target;
    }
}
