/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.display;

import assault.display.Button;
import assault.display.Menu;
import assault.game.gameObjects.AControllable;
import assault.game.gameObjects.AObject;
import assault.game.util.commands.ACommand;
import assault.game.util.commands.CreateCmd;
import assault.game.util.commands.ShootCommand;
import java.awt.Dimension;
import java.awt.Polygon;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.lwjgl.util.ReadableColor;

/**
 *
 * @author matt
 */
public class CommandDispatchMenu extends Menu {

	private List<ACommandButton> aCButtons = Collections.synchronizedList(new ArrayList<ACommandButton>(10));
	private int numColumns;
	private int numRows;

	public CommandDispatchMenu(int x, int y, int columns) {
		super(x, y, columns * CDMButton.BUTTON_WIDTH, 0, new ArrayList<Button>());
		numRows = 0;
		numColumns = columns;
	}

	public void setCmdBtns(AObject ao) {
		if (ao instanceof AControllable) {
			setCmdBtns(((AControllable) ao).getCmdBtnSet());
		}
	}

	/**
	 * finds the unique commands in
	 * <code>aos</code> and sets those to be displayed unless there is only one
	 * <code>AObject</code>, in which case it just sets the command buttons to
	 * <code>aos[0]</code>'s command button set.
	 *
	 * @param aos
	 */
	public void setCmdBtns(AObject[] aos) {
		if (aos.length == 1) {
			setCmdBtns(aos[0]);
		} else {
			setCmdBtns(getUniqueCmdBtnSet(aos));
		}
	}

	private void setCmdBtns(ACommandButton[] acbs) {
		removeAllCmdBtns();
		ACommandButton acb;
		for (int i = 0; i < acbs.length; i++) {
			if (acbs[i] != null) {
				acb = acbs[i];
				acb.setIndex(i, numColumns);
				addButton(acb);
			}
		}
		refreshSizeAndNumRows();
		//System.out.println("set command buttons");
	}

	public static ACommandButton[] constructBtnHierarchy(AObject[] aos) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public static ACommandButton[] getUniqueCmdBtnSet(List<AControllable> units) {
		return getUniqueCmdBtnSet(units.toArray(new AControllable[units.size()]));
	}

	public static ACommandButton[] getUniqueCmdBtnSet(AObject[] aos) {
		//TODO make this more effecient/go with a different model
		//(sub menus for each type maybe?)
		ACommandButton[] uniqueSet;
		uniqueSet = new ACommandButton[6];
		ACommandButton acb;
		if (aos != null) {
			int firstAUindex = -1;//must be initialized to a value BELOW 0
			boolean onlyOneAU = true;
			//find AUs in aos, remembering the first one and breaking if a second is found
			for (int m = 0; m < aos.length; m++) {
				if (aos[m] instanceof AControllable) {
					if (firstAUindex >= 0) {
						//found a second
						onlyOneAU = false;
						break;
					}
					//should only get executed once (on the 1st AU) because then fistAUindex >= 0 right?
					firstAUindex = m;
				}
			}
			onlyOneAU = onlyOneAU && firstAUindex >= 0;//if there are none then onlyoneAU will be true and firstAUindex will still be BELOW 0
			if (onlyOneAU) {
				//if there's only one...
				return ((AControllable) aos[firstAUindex]).getCmdBtnSet();
			} else if (firstAUindex < 0) {
				//there's none?
				return new ACommandButton[0];
			}
			ACommandButton[] aoSet;
			//iterate over aos
			for (int i = firstAUindex; i < aos.length; i++) {
				if (aos[i] instanceof AControllable) {
					aoSet = ((AControllable) aos[i]).getCmdBtnSet();
					//iterate over cmdButton set
					for (int j = 0; j < aoSet.length; j++) {
						acb = aoSet[j];
						if (acb != null) {
							boolean isUnique = true;
							ACommand cmdOfBtnToAdd = acb.getCmd();
							//if the underlying command is null, add it anyways (also is a null check)
							if (cmdOfBtnToAdd != null) {
								Class<? extends ACommand> typeToAdd = cmdOfBtnToAdd.getClass();
								//iterate over the existing unique set
								for (int k = 0; k < uniqueSet.length; k++) {
									if (uniqueSet[k] != null) {
										ACommand cmdOfUniqueBtn = uniqueSet[k].getCmd();
										if (cmdOfUniqueBtn != null) {
											//if it's a createCmd, check the underlying ObjectResourceHolder for uniqueness
											if (cmdOfUniqueBtn instanceof CreateCmd && typeToAdd.equals(CreateCmd.class)) {
												if (((CreateCmd) cmdOfBtnToAdd).getObjectSource().equals(((CreateCmd) cmdOfUniqueBtn).getObjectSource())) {
													isUnique = false;
													break;
												}
												//if it's a shootCmd, check the underlying Weapon for uniqueness
											} else if (cmdOfUniqueBtn instanceof ShootCommand && typeToAdd.equals(ShootCommand.class)) {
												if (cmdOfBtnToAdd.equals(cmdOfUniqueBtn)) {//.equals is overridden
													isUnique = false;
													break;
												}
												//if not check the class. (this is for moveCmd etc.)
											} else if (cmdOfBtnToAdd.getClass().equals(cmdOfUniqueBtn.getClass())) {
												isUnique = false;
												break;
											}
										}
									}
								}
							}
							if (isUnique) {
								boolean foundEmptySlot = false;
								//find an empty slot
								for (int l = 0; l < uniqueSet.length; l++) {
									if (uniqueSet[l] == null) {
										uniqueSet[l] = acb;
										foundEmptySlot = true;
										break;
									}
								}
								if (!foundEmptySlot) {
									//if none make some
									ACommandButton[] temp = new ACommandButton[uniqueSet.length + 3];
									System.arraycopy(uniqueSet, 0, temp, 0, uniqueSet.length);
									temp[uniqueSet.length] = acb;
									uniqueSet = temp;
								}
							}
						}
					}
				}
			}
		}
		return uniqueSet;
	}

	protected void removeAllCmdBtns() {
		ArrayList<ACommandButton> tmp = new ArrayList<ACommandButton>(aCButtons);
		for (ACommandButton acb : tmp){
			if (acb != null) {
				acb.setEnabled(true);
				removeButton(acb);
			}
		}
	}

	public void disableAllExceptEsc() {
		for (ACommandButton acb : aCButtons) {
			if (acb != null) {
				acb.setEnabled(false);
			}
		}
	}

	void enableAll() {
		for (ACommandButton acb : aCButtons) {
			if (acb != null) {
				acb.setEnabled(true);
			}
		}
	}

	public void enableAllExcept(ACommandButton exemptBtn) {
		for (ACommandButton acb : aCButtons) {
			if (acb != null && acb != exemptBtn) {
				acb.setEnabled(true);
			}
		}
	}

	public Dimension refreshSizeAndNumRows() {
		setNumRows(aCButtons.size() / numColumns);
		int width = numColumns * CDMButton.BUTTON_WIDTH;
		int height = numRows * CDMButton.BUTTON_HEIGHT;
		setSize(width, height);
		return new Dimension(width, height);
	}

	@Override
	public void drawSelf() {
		super.drawSelf();
		//System.out.println("ACDM_PAINT");
		setColour(ReadableColor.BLACK);
		drawRect(0, 0, getWidth(), getHeight());
		for (int i = 1; i < numColumns; i++) {
			drawLine(i * CDMButton.BUTTON_WIDTH, 0, i * CDMButton.BUTTON_WIDTH, this.getHeight());
		}
		for (int i = 1; i < numRows; i++) {
			drawLine(0, i * CDMButton.BUTTON_HEIGHT, this.getWidth(), i * CDMButton.BUTTON_HEIGHT);
		}
	}

	private void addButton(ACommandButton acb) {
		acb.setIndex(aCButtons.size(), numColumns);
		aCButtons.add(acb);
		super.addButton(acb);
		refreshSizeAndNumRows();
	}

	private void removeButton(ACommandButton acb) {
		aCButtons.remove(acb);
		super.removeButton(acb);
		refreshSizeAndNumRows();
	}

	@Override
	public Polygon getBounds() {
		int[] xs = new int[6];
		int[] ys = new int[6];

		if (aCButtons.size() > 0) {

			CDMButton topRight = aCButtons.get(aCButtons.size() - 1);//farthest right in top row
			CDMButton secondTopRight;//farthest right in top complete row

			if (aCButtons.size() < numColumns || aCButtons.size() % numColumns == 0) {//case of complete top row or # buttons less than # of coloumns
				secondTopRight = topRight;
			} else {
				secondTopRight = aCButtons.get(aCButtons.size() - (aCButtons.size() % numColumns) - 1);
			}

			/*
			 * ie:
			 *
			 * 1--2 | | +--3--+--4 | | | | 0--+--+--5
			 */

			//xs[0] = 0
			//ys[0] = 0

			//xs[1] = 0
			ys[1] = topRight.getY() + topRight.getHeight();

			xs[2] = topRight.getX() + topRight.getWidth();
			ys[2] = topRight.getY() + topRight.getHeight();

			xs[3] = topRight.getX() + topRight.getWidth();
			ys[3] = topRight.getY();

			xs[4] = secondTopRight.getX() + secondTopRight.getWidth();
			ys[4] = secondTopRight.getY() + secondTopRight.getHeight();

			xs[5] = secondTopRight.getX() + secondTopRight.getWidth();
			//ys[5] = 0; 
		}
		return new Polygon(xs, ys, 6);
	}

	private void setNumRows(int i) {
		numRows = i;
		setSize(getWidth(), i * CDMButton.BUTTON_HEIGHT);
	}
}
