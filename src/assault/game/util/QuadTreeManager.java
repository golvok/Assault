package assault.game.util;

import java.util.List;

import assault.display.Bounded;
import assault.game.display.GameArea;
import assault.game.util.terrain.TerrainGenerator;
import assault.util.Point;
import assault.util.QuadTreeNode;


public class QuadTreeManager extends QuadTreeNode<Bounded> implements ObjectManager {

	
	public QuadTreeManager(GameArea ga, TerrainGenerator tg) {
		super((int)ga.getX(), (int)ga.getY(), (int)ga.getWidth(), (int)ga.getHeight(),null);
	}

	@Override
	public boolean notifyOfImminentMovement(Bounded willMove, Point newPt) {
		// TODO optimise
		remove(willMove);
		return add(willMove);
	}

	@Override
	public boolean notifyOfImminentMovement(Bounded willMove, double newX, double newY) {
		return notifyOfImminentMovement(willMove, new Point(newX,newY));
	}

	@Override
	public void dispose() {
		//TODO implement this properly
		super.dispose();
	}

	@Override
	public boolean isPixelEmpty(int x, int y) throws IndexOutOfBoundsException {
		return getAt(x, y).size() > 0;
	}

	@Override
	public List<Bounded> getBoundedsAt(int x, int y) throws IndexOutOfBoundsException {
		return getAt(x,y);
	}

	@Override
	public boolean canBeAtPixel(double x, double y, Bounded b) {
		return canBeAt_bool((int)x, (int)y, b);
	}

	@Override
	public int getPixelHeight() {
		return (int)getHeight();
	}

	@Override
	public int getPixelWidth() {
		return (int)getWidth();
	}

}
