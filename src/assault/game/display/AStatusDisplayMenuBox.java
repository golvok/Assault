/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.display;

import assault.game.gameObjects.AObject;
import org.lwjgl.util.Color;

/**
 *
 * @author Matt
 */
public class AStatusDisplayMenuBox extends AStatusDisplayBox {

	protected final static int BOX_HEIGHT = 20;
	protected final static int BOX_WIDTH = 80;
	private int index = 0;

	public AStatusDisplayMenuBox(AObject ao, int index) {
		super(ao, BOX_WIDTH, BOX_HEIGHT);
		setIndex(index);
	}

	/**
	 * sets the index to index and fixes the position of
	 * this box via fixBounds().
	 * @param index
	 * @see #fixBounds()
	 */
	public void setIndex(int index) {
		this.index = index;
		correctPosition();
	}

	public int getIndex() {
		return index;
	}
	

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void correctPosition() {
		setLocation(0, index * BOX_HEIGHT);
	}

	@Override
	public void drawSelf() {
		super.drawSelf();
		if (getObject() != null) {
			setColour(Color.BLACK);
			//drawString(object.getHealth() + "/" + object.getMaxHealth(), 0, boxHeight);
			//if (object != null && object.getMiniIcon() != null && object.getMiniIcon().getImage() != null){
            
			//Image image = object.getSrc().getMiniIcon();
			//drawImage(image, getWidth() - image.getWidth(null), getHeight() - image.getHeight(null), null);
            
			//}
            //g.setColor(object.getOwner().getColour());
            //g.drawRect(5, 5, 10, 10);
		}
		//System.out.println("ASDMB_PAINT");
	}
}
