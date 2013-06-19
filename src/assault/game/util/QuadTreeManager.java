package assault.game.util;

import java.util.List;

import assault.display.Bounded;
import assault.display.Bounded_Impl;
import assault.display.Container;
import assault.game.display.GameArea;
import assault.game.util.terrain.TerrainGenerator;
import assault.util.Point;
import assault.util.QuadTreeNode;


public class QuadTreeManager extends Container<Bounded> implements ObjectManager {

	private QuadTreeNode<Bounded> rootNode;
	
	public QuadTreeManager(GameArea ga, TerrainGenerator tg) {
		super((int)ga.getX(), (int)ga.getY(), (int)ga.getWidth(), (int)ga.getHeight(),null);
		rootNode = new QuadTreeNode<>(0, 0, getPixelWidth(), getPixelHeight());
		super.addChild(rootNode);
	}

	@Override
	public boolean notifyOfImminentMovement(final Bounded willMove, Point newPt) {
		// TODO make this do this properly
		Bounded newRegion = new Bounded_Impl(newPt.x, newPt.y, willMove.getWidth(), willMove.getHeight()){
			@Override
			public String toString() {
				return super.toString() + " (" + willMove + ')';
			}
		};
		List<Bounded> allthatClipWithNewRegion = rootNode.getAllClipping(newRegion); 
		if(allthatClipWithNewRegion.size() > 1 || !allthatClipWithNewRegion.get(0).equals(willMove)){
			return false;
		}
		
		remove(willMove);
		return rootNode.addAs(newRegion,willMove).isSuccess();
	}

	@Override
	public boolean notifyOfImminentMovement(Bounded willMove, double newX, double newY) {
		return notifyOfImminentMovement(willMove, new Point(newX,newY));
	}

	@Override
	public void drawSelf() {
		super.drawSelf();
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public boolean isPixelEmpty(int x, int y) throws IndexOutOfBoundsException {
		return rootNode.getAt(x, y).size() > 0;
	}

	@Override
	public List<Bounded> getBoundedsAt(int x, int y) throws IndexOutOfBoundsException {
		return rootNode.getAt(x,y);
	}

	@Override
	public boolean canBeAtPixel(double x, double y, Bounded b) {
		return rootNode.canBeAt((int)x, (int)y, b);
	}

	@Override
	public int getPixelHeight() {
		return (int)getHeight();
	}

	@Override
	public int getPixelWidth() {
		return (int)getWidth();
	}

	@Override
	public boolean add(Bounded b) {
		return rootNode.add(b).isSuccess();
	}

	@Override
	public boolean remove(Bounded b) {
		return rootNode.remove(b);
	}

	@Override
	public void removeAll() {
		rootNode.removeAll();
	}

}
