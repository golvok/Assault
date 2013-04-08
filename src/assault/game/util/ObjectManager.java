package assault.game.util;

import assault.display.Bounded;
import assault.util.Disposable;
import assault.util.Point;

public interface ObjectManager extends Disposable {

	/**
	 * add an ObjectObject to this ObjectManager
	 * 
	 * @param go
	 * @return true if successful, false if not
	 */
	public abstract boolean add(Bounded go);

	public abstract void remove(Bounded go);

	public abstract void removeAll();

	/**
	 * moves the object in the ObjectManager should be called before when x and y
	 * are actually changed and used as a decision weather to do so or not. Also
	 * do not change the size between this and the movement
	 * 
	 * @param ao
	 * @param newX
	 * @param newY
	 * @return (movementWasSuccessful)
	 */
	public abstract boolean notifyOfImminentMovement(Bounded ao,
			Point oldPt, Point newPt);

	/**
	 * moves the object in the ObjectManager should be called before when x and y
	 * are actually changed and used as a decision weather to do so or not. Also
	 * do not change the size between this and the movement
	 * 
	 * @param go
	 * @param newX
	 * @param newY
	 * @return (movementWasSuccessful)
	 */
	public abstract boolean notifyOfImminentMovement(Bounded go,
			double oldX, double oldY, double newX, double newY);

	public abstract void dispose();

	public abstract boolean isDisposed();

	/**
	 * gets the list of GO's that could be at pixel (x,y) then iterates over
	 * them, returning the false if at least one's .bounds().contains(x,y) ==
	 * true
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @throws IndexOutOfBoundsException
	 *             if it's well.. out of bounds...
	 */
	public abstract boolean isPixelEmpty(int x, int y)
			throws IndexOutOfBoundsException;

	/**
	 * gets the list of GO's that could be at pixel (x,y) then iterates over
	 * them, returning the first one that .bounds().contains(x,y) null if no
	 * match found
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @throws IndexOutOfBoundsException
	 *             if it's well.. out of bounds...
	 */
	public abstract Bounded getGoAtPixel(int x, int y)
			throws IndexOutOfBoundsException;

	/**
	 * using AMP co-ords, tests if <code>go</code> can exist at
	 * <code>(x, y)</code> if go is null, returns false
	 * 
	 * @param x
	 *            in the GA
	 * @param y
	 *            in the GA
	 * @param go
	 * @return
	 */
	public abstract boolean canBeAtPixel(double x, double y, Bounded go);

	public abstract int getPixelHeight();

	public abstract int getPixelWidth();

}