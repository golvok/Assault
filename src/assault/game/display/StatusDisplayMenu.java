/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.display;

import assault.display.InputRegistarContainer;
import assault.game.gameObjects.Controllable;
import assault.game.gameObjects.Group;
import assault.game.gameObjects.AObject;
import java.util.*;

/**
 *
 * @author Matt
 */
public class StatusDisplayMenu extends InputRegistarContainer {

	private List<AObject> aObjects = Collections.synchronizedList(new ArrayList<AObject>(10));
	private List<StatusDisplayMenuBox> boxes = Collections.synchronizedList(new ArrayList<StatusDisplayMenuBox>(10));
	private Map<AObject, StatusDisplayMenuBox> ao2Box = new HashMap<AObject, StatusDisplayMenuBox>(10);
	private Map<Integer, StatusDisplayMenuBox> index2Box = new HashMap<Integer, StatusDisplayMenuBox>(10);

	public StatusDisplayMenu(int x, int y) {
		super(x, y, 100, 100, 16);
		//System.out.println("asdm constructor");
	}

	/**
	 *
	 * @param ao
	 * @param index
	 * @return if (successful)
	 */
	public boolean setBoxAt(AObject ao, int index) {
		boolean justMoving = false;
		if (aObjects.contains(ao)) {
			if (ao2Box.get(ao).getIndex() != index) {
				removeBox(ao);
				justMoving = true;
			} else {
				//the case where the box for ao is at index so nothing needs to be done
				return true;
			}
		}
		if (index <= aObjects.size()) {//to prevent blanks
			//remove the box at index
			StatusDisplayMenuBox asdmbToRemove = index2Box.get(index);
			if (asdmbToRemove != null) {
				if (!justMoving) {
					removeChild(asdmbToRemove);
					aObjects.remove(asdmbToRemove.getObject());
					boxes.remove(asdmbToRemove);
					ao2Box.remove(asdmbToRemove.getObject());
					index2Box.remove(asdmbToRemove.getIndex());
				}
			}
			//there now is an empty space,
			//put a new one where it was or move the old one
			StatusDisplayMenuBox asdmb;
			if (!justMoving) {
				aObjects.add(ao);
				asdmb = new StatusDisplayMenuBox(ao, index);
				boxes.add(asdmb);
				ao2Box.put(ao, asdmb);
				index2Box.put(asdmb.getIndex(), asdmb);
				addChild(asdmb);
			} else {
				asdmb = asdmbToRemove;
				asdmb.setIndex(index);//causes it to move itself
				index2Box.put(index, asdmb);//update this map
			}
			return true;
		}
		return false;
	}

	public void setDisplayedBox(AObject ao) {
		removeAllBoxes();
		addBox(ao);
	}

	public void setDisplayedBoxes(AObject[] aos) {
		removeAllBoxes();
		addBoxes(aos);
	}

	public void addBox(AObject ao) {
		if (ao instanceof Group) {
			addBoxes(((Group) ao).getControllables());
		} else {
			aObjects.add(ao);
			StatusDisplayMenuBox asdmb = new StatusDisplayMenuBox(ao, boxes.size());
			boxes.add(asdmb);
			ao2Box.put(ao, asdmb);
			index2Box.put(asdmb.getIndex(), asdmb);
			addChild(asdmb);
		}
	}

	public void addBoxes(AObject[] aos) {
		for (int j = 0; j < aos.length; j++) {
			addBox(aos[j]);
		}
	}

	public void removeBox(AObject ao) {
		if (ao instanceof Group) {
			Controllable[] aus = ((Group) ao).getControllables();
			for (int i = 0; i < aus.length; i++) {
				removeBox(aus[i]);
			}
		} else {
			StatusDisplayMenuBox asdmb = ao2Box.remove(ao);//also gets
			if (asdmb != null) {
				//decrease the indicies of all boxes after it
				//and figure out where to repaint
				float minY = Float.POSITIVE_INFINITY;
				float maxY = 0;
				float maxX = 0;
				StatusDisplayMenuBox box;
				for (int i = 0; i < aObjects.size(); i++) {
					box = index2Box.get(i);
					if (box != null && box.getIndex() > asdmb.getIndex()) {
						box.setIndex(box.getIndex() - 1);
						if (box.getY() < minY) {
							minY = box.getY();
						}
						if (box.getY() > maxY) {
							maxY = box.getY();
						}
						if (box.getX() > maxX) {
							maxX = box.getX();
						}
					}
				}
			}
			removeChild(asdmb);
		}
	}

	protected void removeAllBoxes() {
		aObjects.clear();
		boxes.clear();
		ao2Box.clear();
		index2Box.clear();
		removeAllChildren();
	}

	@Override
	public void drawSelf() {
		super.drawSelf();
		//System.out.println("ASDM has "+boxes.size()+" boxes");
		drawRect(0, 0, getWidth(), getHeight());
		//System.out.println("ASDM_PAINT");
	}
}
