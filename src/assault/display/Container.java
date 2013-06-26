package assault.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author matt
 */
public class Container<B extends Bounded> extends Paintable{

	private final List<B> children;

	public Container(float x, float y, float width, float height, ArrayList<B> startchildren) {
		this(x, y, width, height, startchildren != null ? startchildren.size() : 0);
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
	public Container(float x, float y, float width, float height, int startSize) {
		super(x, y, width, height);
		children = Collections.synchronizedList(new ArrayList<B>(startSize));
	}

	@Override
	public void drawSelf() {
		drawChildren();
	}

	public void drawChildren() {
		synchronized (children) {
			for (B child : children) {
				if (child instanceof Paintable) {
					((Paintable)child).adjustMatrixAndDrawSelf();
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
			for (B child : children) {
				if (child instanceof Paintable) {
					((Paintable)child).update(delta);
				}
			}
		}
    }

	public void addChildren(List<? extends B> aps) {
		if (aps != null) {
			for (B ab : aps) {
				addChild(ab);
			}
		}
	}

	public void addChild(B ap) {
		//System.out.println("adding "+ap+" to "+this);
		if (ap instanceof Paintable) {
			((Paintable)ap).setParent(this);
			children.add(ap);
		}
	}

	protected void removeChild(Bounded ap) {
		children.remove(ap);
	}

	protected void removeAllChildren() {
		children.clear();
	}

	@Override
	public synchronized void dispose() {
		if (!isDisposed()) {
			ArrayList<B> temp = new ArrayList<B>(children);
			for (B ap : temp) {
				if (ap instanceof Paintable) {
					((Paintable)ap).dispose();
				}
				//System.out.println("dispose " + ap);
			}
			removeAllChildren();
		}
		super.dispose();
	}
	
	public List<B> getChildren(){
		return Collections.unmodifiableList(children);
	}
}
