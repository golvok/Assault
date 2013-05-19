package assault.game.util;

import java.util.List;

import assault.display.Bounded;
import assault.util.Disposable;
import assault.util.Point;

public interface ObjectManager extends Disposable {

	/**
	 * add a Bounded to this ObjectManager
	 * 
	 * @param b
	 * @return true if successful, false if not
	 */
	public abstract boolean add(Bounded b);

	public abstract boolean remove(Bounded b);

	public abstract void removeAll();

	/**
	 * moves the object in the ObjectManager. should be called before when x and y
	 * are actually changed and used as a decision weather to do so or not. Also
	 * do not change the size between this and the movement
	 * 
	 * @param willMove
	 * @param newX
	 * @param newY
	 * @return (movementWasSuccessful)
	 */
	public abstract boolean notifyOfImminentMovement(Bounded willMove, Point newPt);

	/**
	 * moves the object in the ObjectManager. should be called before when x and y
	 * are actually changed and used as a decision weather to do so or not. Also
	 * do not change the size between this and the movement
	 * 
	 * @param willMove
	 * @param newX
	 * @param newY
	 * @return (movementWasSuccessful)
	 */
	public abstract boolean notifyOfImminentMovement(Bounded willMove,
			double newX, double newY);

	public abstract void dispose();

	public abstract boolean isDisposed();

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @throws IndexOutOfBoundsException if it's well.. out of bounds...
	 */
	public abstract boolean isPixelEmpty(int x, int y)
			throws IndexOutOfBoundsException;

	/**
	 * 
	 * get the objects at the given point. They should be in order of z-depth
	 * ie the one that is "on top" should be the first element in the list.
	 * 
	 * @param x
	 * @param y
	 * @return the objects at the given point. 
	 * @throws IndexOutOfBoundsException if it's well.. out of bounds...
	 */
	public abstract List<Bounded> getBoundedAtPixel(int x, int y) throws IndexOutOfBoundsException;

	/**
	 * using AMP co-ords, tests if <code>b</code> can exist at
	 * <code>(x, y)</code> if b is null, returns false
	 * 
	 * @param x
	 *            in the GA
	 * @param y
	 *            in the GA
	 * @param b
	 * @return
	 */
	public abstract boolean canBeAtPixel(double x, double y, Bounded b);

	public abstract int getPixelHeight();

	public abstract int getPixelWidth();

}