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
		path = new AbstractPathObject();
        setTarget(r);
    }

    /**
     *
     * @param delta delta time in ms
     */
    public void advanceTarget(int delta) {
        if (path != null && path.peek() != null){
			//the speed is given per unit second
            double deltaDistance = delta*target.getMovementSpeed()/1000d;
			System.out.println(deltaDistance);
            while (deltaDistance > 0){
                double distanceToNextPoint = distance(target.getLocation(),path.peek());
                if (deltaDistance - distanceToNextPoint >= 0){
                    target.setLocation(path.pop());//remove and set
                    deltaDistance -= distanceToNextPoint;
					System.out.println("next");
                }else{//less than 0, linear interpoltaion
					//offset the current location by the unit vector in the correct direction, multipleid by the wanted magnitude
                    target.setLocation(add(target.getLocation(), multiply(unit(delta(target.getLocation(), path.peek())), deltaDistance)));
                    deltaDistance = 0;
					System.out.println("partial");
                    break;
                }
            }
        }
    }

    public void moveBy(double x, double y) {
        moveTo(target.getX() + x, target.getY() + y);
    }

    public void moveTo(Point p) {
		path.clear();
		path.addPoints(target.getPathFinder().findPath(target, (int)p.getX(), (int)p.getY()));
    }

    public void moveTo(double x, double y) {
        moveTo(new Point(x, y));
    }

    private void setTarget(Relocatable target) {
        this.target = target;
    }
}
