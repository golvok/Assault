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
public abstract class Container extends Paintable{

	private final List<Paintable> children;

	public Container(double x, double y, double width, double height, ArrayList<Paintable> startchildren) {
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
	public Container(double x, double y, double width, double height, int startSize) {
		super(x, y, width, height);
		children = Collections.synchronizedList(new ArrayList<Paintable>(startSize));
	}

	@Override
	public void drawSelf() {
		drawChildren();
	}

	public void drawChildren() {
		synchronized (children) {
			for (Paintable ap : children) {
				if (ap != null) {
					ap.draw();
				}
			}
		}

	}

    @Override
    public void updateSelf(int delta) {
        updateChildren(delta);
    }

    public void updateChildren(int delta) {
        synchronized (children) {
			for (Paintable ap : children) {
				if (ap != null) {
					ap.update(delta);
				}
			}
		}
    }

	public void addChildren(ArrayList<? extends Paintable> aps) {
		if (aps != null) {
			for (Paintable aPaintable : aps) {
				addChild((Paintable) aPaintable);
			}
		}
	}

	public void addChild(Paintable ap) {
		//System.out.println("adding "+ap+" to "+this);
		if (ap != null) {
			ap.setParent(this);
			children.add(ap);
		}
	}

	protected void removeChild(Paintable ap) {
		children.remove(ap);
	}

	protected void removeAllChildren() {
		children.clear();
	}

	@Override
	public synchronized void dispose() {
		if (!isDisposed()) {
			ArrayList<Paintable> temp = new ArrayList<Paintable>(children);
			for (Paintable ap : temp) {
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
