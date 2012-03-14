/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding.moving;

import static assault.util.Point.*;
import assault.util.Point;

/**
 *
 * @author matt
 */
public class Mover {

    Relocatable target;
    AbstractPathObject path;

    public Mover(Relocatable r) {
        setTarget(r);
    }

    /**
     *
     * @param delta delta time in ms
     */
    public void advanceTarget(int delta) {
        if (path != null){
            double deltaDistance = delta*target.getMovementSpeed()/1000d;
            while (deltaDistance > 0){
                double distanceToNextPoint = distance(target.getLocation(),path.peek());
                if (deltaDistance - distanceToNextPoint >= 0){
                    target.setLocation(path.pop());//remove and set
                    deltaDistance -= distanceToNextPoint;
                }else{//less than 0, linear interpoltaion
                    double slope = deltaY(target.getLocation(), path.peek())/deltaX(target.getLocation(), path.peek());
                    
                    deltaDistance = 0;
                    break;
                }
            }
        }
    }

    public void moveBy(double x, double y) {
        moveTo(target.getX() + x, target.getY() + y);
    }

    public void moveTo(Point p) {
    }

    public void moveTo(double x, double y) {
        moveTo(new Point(x, y));
    }

    private void setTarget(Relocatable target) {
        this.target = target;
    }
}
