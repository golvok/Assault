package assault.game.util;

import assault.display.Bounded;
import assault.util.Point;

public class DualObjectManager implements ObjectManager {
	
	private ObjectManager sub;
	private ObjectManager dom;

	public DualObjectManager(ObjectManager dom, ObjectManager sub) {
		this.dom = dom;
		this.sub = sub;
	}

	@Override
	public boolean add(Bounded b) {
		return compare(dom.add(b) ,sub.add(b) ,"add");
	}

	@Override
	public boolean remove(Bounded b) {
		return compare(dom.remove(b) ,sub.remove(b) ,"remove");
	}

	@Override
	public void removeAll() {
		sub.removeAll();
		dom.removeAll();
	}

	@Override
	public boolean notifyOfImminentMovement(Bounded willMove, Point newPt) {
		return compare(dom.notifyOfImminentMovement(willMove, newPt),sub.notifyOfImminentMovement(willMove, newPt),"notifyOfImminentMovement(pt)");
	}

	@Override
	public boolean notifyOfImminentMovement(Bounded willMove, double newX, double newY) {
		return compare(dom.notifyOfImminentMovement(willMove, newX, newY) ,sub.notifyOfImminentMovement(willMove, newX, newY) ,"notifyOfImminentMovement(xy)");
	}

	@Override
	public void dispose() {
		dom.dispose();
		sub.dispose();
	}

	@Override
	public boolean isDisposed() {
		return compare(dom.isDisposed() ,sub.isDisposed() ,"isDisposed");
	}

	@Override
	public boolean isPixelEmpty(int x, int y) throws IndexOutOfBoundsException {
		return compare(dom.isPixelEmpty(x, y) ,sub.isPixelEmpty(x, y) ,"isPixelEmpty");
	}

	@Override
	public Bounded getBoundedAtPixel(int x, int y) throws IndexOutOfBoundsException {
		return compare(dom.getBoundedAtPixel(x, y) ,sub.getBoundedAtPixel(x, y) ,"getBoundedAtPixel");
	}

	@Override
	public boolean canBeAtPixel(double x, double y, Bounded b) {
		return compare(dom.canBeAtPixel(x, y, b) ,sub.canBeAtPixel(x, y, b) ,"canBeAtPixel");
	}

	@Override
	public int getPixelHeight() {
		return compare(dom.getPixelHeight() ,sub.getPixelHeight() ,"getPixelHeight");
	}

	@Override
	public int getPixelWidth() {
		return compare(dom.getPixelWidth() ,sub.getPixelWidth() ,"getPixelWidth");
	}

	private <T> T compare(T doms, T subs,String thing){
		if (doms != subs){
			System.out.println("The OMs disagree on "+thing+": sub ("+sub.getClass().getCanonicalName()+") gave "+subs+" and dom gave "+doms);
		}
		return doms;
	}
	
}
