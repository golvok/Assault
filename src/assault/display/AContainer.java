/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author matt
 */
public abstract class AContainer extends APaintable {

	private final List<APaintable> children;

	public AContainer(int x, int y, int width, int height, ArrayList<APaintable> startchildren) {
		this(x, y, width, height, startchildren.size());
		addChildren(startchildren);
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param startSize the initial size passed to the children
	 * <code>List</code>
	 */
	public AContainer(int x, int y, int width, int height, int startSize) {
		super(x, y, width, height);
		children = Collections.synchronizedList(new ArrayList<APaintable>(startSize));
	}

	@Override
	public void drawSelf() {
		drawChildren();
	}

	public void drawChildren() {
		synchronized (children) {
			for (APaintable ap : children) {
				if (ap != null) {
					ap.draw();
				}
			}
		}

	}

	public void addChildren(ArrayList<? extends APaintable> aps) {
		if (aps != null) {
			for (APaintable aPaintable : aps) {
				addChild((APaintable) aPaintable);
			}
		}
	}

	public void addChild(APaintable ap) {
		//System.out.println("adding "+ap+" to "+this);
		if (ap != null) {
			ap.setParent(this);
			children.add(ap);
		}
	}

	protected void removeChild(APaintable ap) {
		children.remove(ap);
	}

	protected void removeAllChildren() {
		children.clear();
	}

	@Override
	public synchronized void dispose() {
		if (!isDisposed()) {
			ArrayList<APaintable> temp = new ArrayList<APaintable>(children);
			for (APaintable ap : temp) {
				if (ap != null) {
					ap.dispose();
				}
				//System.out.println("dispose " + ap);
			}
			removeAllChildren();
		}
		super.dispose();
	}
}
