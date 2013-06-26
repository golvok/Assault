/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding.moving;

import assault.util.Point;
import static assault.util.Point.*;

/**
 *
 * @author matt
 */
public class Mover {

    private Relocatable target;
    private AbstractPathObject path;

    public Mover(Relocatable r) {
		path = new AbstractPathObject();
        setTarget(r);
    }

    /**
     *
     * @param delta delta time in ms
     */
    public void advanceTarget(int delta) {
    	//TODO make moving thread safe
    	//the checks arent enough, one time path.peek was null after it was checked
        if (path != null && path.peek() != null){
			//the speed is given per unit second
            float deltaDistance = delta*target.getMovementSpeed()/1000f;
            while (deltaDistance > 0 && path.peek()!= null){
                float distanceToNextPoint = distancef(target.getLocation(),path.peek());
                if (deltaDistance - distanceToNextPoint >= 0){
                    if (target.setLocation_safe(path.peek())){
                    	path.pop();
                    }else{
                    	break;
                    }
                    deltaDistance -= distanceToNextPoint;
                }else{//can't quite make it (less than 0), linear interpoltaion
					//offset the current location by the unit vector in the correct direction, multipleid by the wanted magnitude
                    target.setLocation_safe(add(target.getLocation(), multiply(unit(delta(target.getLocation(), path.peek())), deltaDistance)));
                    deltaDistance = 0;//to avoid floating point weirdness
                    break;
                }
            }
        }
    }

    public void moveBy(float x, float y) {
        moveTo(target.getX() + x, target.getY() + y);
    }

    public void moveTo(Point p) {
		target.getPathingManger().findPath(target, (int)p.getX(), (int)p.getY(),path,true,true);
    }

    public void moveTo(float x, float y) {
        moveTo(new Point(x, y));
    }

    private void setTarget(Relocatable target) {
        this.target = target;
    }
}
