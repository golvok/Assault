
package assault.game.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import assault.display.Bounded;
import assault.game.display.GameArea;
import assault.game.util.terrain.TerrainGenerator;
import assault.util.Point;

/**
 *
 * @author matt
 */
public class GridManager implements ObjectManager {

	private final int gridSize;
	private final int width;
	private final int height;
	private final ArrayList<ArrayList<GridCell<Bounded>>> grid;
	private final List<Bounded> gObjects = Collections.synchronizedList(new ArrayList<Bounded>(64/*random*/));
	private final Object editlock = new Object();
	protected TerrainGenerator terrainGenerator;
	private final GameArea gameArea;

	public GridManager(int pixelWidth, int pixelHeight, int gridSize, GameArea ga, TerrainGenerator tg) {
		gameArea = ga;
		terrainGenerator = tg;
		this.gridSize = gridSize;
		this.width = convDimToGrid(pixelWidth, 0);
		this.height = convDimToGrid(pixelHeight, 0);
		grid = new ArrayList<ArrayList<GridCell<Bounded>>>();
		initGrid();
	}
	
	/**
	 * Inits the grid to the correct size and fills it with grid cells
	 * and generates the terrain
	 */
	private void initGrid(){
		grid.ensureCapacity(width);
		for (int i = 0; i < width; i++) {
			try {
				grid.add(i, new ArrayList<GridCell<Bounded>>(height));
				for (int j = 0; j < height; j++) {
					try {
						grid.get(i).add(j, generateGridCell(gameArea, i, j));
					} catch (IndexOutOfBoundsException e) {
					}
				}
			} catch (IndexOutOfBoundsException e) {
			}
		}
	}

	public GridCell<Bounded> generateGridCell(GameArea g, int x, int y) {
		return new GridCell<Bounded>(4);
	}
	
	/**
	 * add an GridObject to this GridManager
	 * @param go
	 * @return true if successful, false if not
	 */
	@Override
	public boolean add(Bounded go) {
		synchronized (editlock) {
			int x = convCoordToGrid(go.getX());
			int y = convCoordToGrid(go.getY());
			int widthInGrid = convDimToGrid(go.getWidth(), go.getX());
			int heightInGrid = convDimToGrid(go.getHeight(), go.getY());

			//check if where it wants to go is clear
			if (!canBeAtPixel(go.getX(), go.getY(), go)) {
				return false;
			}
			//add it to the list
			gObjects.add(go);
			//put it in the new location
			for (int k = 0; k < widthInGrid; k++) {
				for (int j = 0; j < heightInGrid; j++) {
					addToCell(x + k, y + j, go);
				}
			}
		}
		return true;
	}

	@Override
	public void remove(Bounded go) {
		if (go != null && gObjects.contains(go)) {
			synchronized (editlock) {
				int x = convCoordToGrid(go.getX());
				int y = convCoordToGrid(go.getY());
				int widthInGrid = convDimToGrid(go.getWidth(), go.getX());
				int heightInGrid = convDimToGrid(go.getHeight(), go.getY());
				//remove it from the old location
				for (int i = 0; i < widthInGrid; i++) {
					for (int j = 0; j < heightInGrid; j++) {
						try {
                            removeFromCell(x + i, y + j, go);
						} catch (IndexOutOfBoundsException e) {
						}
					}
				}
				gObjects.remove(go);
			}
			//System.out.println("GM_REMOVE " + go);
		}
	}

	@Override
	public void removeAll() {
		for (int i = 0; i < gObjects.size(); i++) {
			remove(gObjects.get(i));
		}
		gObjects.clear();
	}

	/**
	 * moves the object in the GridManager
	 * should be called before when x and y are actually changed
	 * and used as a decision weather to do so or not.
	 * Also do not change the size between this and the movement
	 * @param ao
	 * @param newX
	 * @param newY
	 * @return (movementWasSuccessful)
	 */
	@Override
	public boolean notifyOfImminentMovement(Bounded ao, Point oldPt, Point newPt) {
		return notifyOfImminentMovement(ao, oldPt.getX(), oldPt.getY(), newPt.getX(), newPt.getY());
	}

	/**
	 * moves the object in the GridManager
	 * should be called before when x and y are actually changed
	 * and used as a decision weather to do so or not.
	 * Also do not change the size between this and the movement
	 * @param go
	 * @param newX
	 * @param newY
	 * @return (movementWasSuccessful)
	 */
	@Override
	public boolean notifyOfImminentMovement(Bounded go, double oldX, double oldY, double newX, double newY) {
		synchronized (editlock) {
			//System.out.println("oldX = "+oldX);
			//System.out.println("oldY = "+oldY);
			//System.out.println("newX = "+newX);
			//System.out.println("newY = "+newY);
			int widthInGridOld = convDimToGrid(go.getWidth(), oldX);
			int heightInGridOld = convDimToGrid(go.getHeight(), oldY);
			int widthInGridNew = convDimToGrid(go.getWidth(), newX);
			int heightInGridNew = convDimToGrid(go.getHeight(), newY);

			//check if where it wants to go is clear
			if (!canBeAtPixel(newX, newY, go)) {
				System.out.println("GM: where it wants to go is not open @move");
				return false;
			}
			//System.out.println("GM: where it wants to go is clear @move");

			int oldXg = convCoordToGrid(oldX);
			int oldYg = convCoordToGrid(oldY);
			int newXg = convCoordToGrid(newX);
			int newYg = convCoordToGrid(newY);

			try {
				//check if it is indeed where it says it is
				for (int i = 0; i < widthInGridOld; i++) {
					for (int j = 0; j < heightInGridOld; j++) {
						if (!getGridCellAtGrid(oldXg + i, oldYg + j).contains(go)) {
							System.out.println("GM: NOT where it says it is @move");
							//System.out.println("a "+getGridCellAtGrid(oldX + i, oldY + j)+" is there!");
							//System.out.println("a "+go+" should be there!");
							return false;
						}
					}
				}
			} catch (IndexOutOfBoundsException e) {
				//only checks have been done, therefore there is nothing to clean up
				return false;
			}
			//System.out.println("GM: where it says it is @move");

			//remove it from the old location
			for (int i = 0; i < widthInGridOld; i++) {
				for (int j = 0; j < heightInGridOld; j++) {
					removeFromCell(oldXg + i, oldYg + j, go);
				}
			}
			//put it in the new location
			for (int i = 0; i < widthInGridNew; i++) {
				for (int j = 0; j < heightInGridNew; j++) {
					addToCell(newXg + i, newYg + j, go);
				}
			}
			return true;
		}
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param ao
	 * @throws IndexOutOfBoundsException if it's well.. out of bounds...
	 */
	private void addToCell(int x, int y, Bounded ao) throws IndexOutOfBoundsException {
		synchronized (editlock) {
			try {
				grid.get(x).get(y).add(ao);
				//System.out.println("set (" + x + "," + y + ") to " + ao);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("cell (" + x + "," + y + ") is out of bounds");
				throw e;
			}
		}
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param ao
	 * @throws IndexOutOfBoundsException if it's well.. out of bounds...
	 */
	private void removeFromCell(int x, int y, Bounded ao) throws IndexOutOfBoundsException {
		synchronized (editlock) {
			try {
				grid.get(x).get(y).remove(ao);
				//System.out.println("set (" + x + "," + y + ") to " + ao);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("cell (" + x + "," + y + ") is out of bounds");
				throw e;
			}
		}
	}

	private boolean disposed = false;
	
	@Override
	public synchronized void dispose() {
		if (!disposed){
			removeAll();
			disposed = true;
		}
	}

	@Override
	final public boolean isDisposed() {
		return disposed;
	}
	
	/**
	 *
	 * @param x
	 * @param y
	 * @param ao
	 * @throws IndexOutOfBoundsException if it's well.. out of bounds...
	 */
	@SuppressWarnings("unused")
	private void setCell(int x, int y, GridCell<Bounded> gos) throws IndexOutOfBoundsException {
		synchronized (editlock) {
			try {
				grid.get(x).set(y, gos);
				//System.out.println("set (" + x + "," + y + ") to " + ao);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("cell (" + x + "," + y + ") is out of bounds");
				throw e;
			}
		}
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @return
	 * @throws IndexOutOfBoundsException if it's well.. out of bounds...
	 */
	public boolean isCellEmpty(int x, int y) throws IndexOutOfBoundsException {
		return getGridCellAtGrid(x, y).isEmpty();
	}

	/**
	 * gets the list of GO's that could be at pixel (x,y) then
	 * iterates over them, returning the false if at least one's
	 * .bounds().contains(x,y) == true
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @throws IndexOutOfBoundsException if it's well.. out of bounds...
	 */
	@Override
	public boolean isPixelEmpty(int x, int y) throws IndexOutOfBoundsException {
		synchronized (editlock) {
			List<Bounded> cell = getGridCellAtGrid(convCoordToGrid(x), convCoordToGrid(y));
			for (Bounded go : cell) {
				if (go.getBounds().contains(x, y)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * gets the list of GO's that could be at pixel (x,y) then
	 * iterates over them, returning the first one that .bounds().contains(x,y)
	 * null if no match found
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @throws IndexOutOfBoundsException if it's well.. out of bounds...
	 */
	@Override
	public Bounded getGoAtPixel(int x, int y) throws IndexOutOfBoundsException {
		synchronized (editlock) {
			try{
				List<Bounded> cell = getGridCellAtGrid(convCoordToGrid(x), convCoordToGrid(y));

				for (Bounded go : cell) {
					if (go.getBounds().contains(x, y)) {
						return go;
					}
				}
			} catch (IndexOutOfBoundsException ex){
				return null;
			}
			return null;
		}
	}

	/**
	 * return the list of GO's that are in grid cell (x,y)
	 * may be empty
	 * 
	 * @param x
	 * @param y
	 * @throws IndexOutOfBoundsException if it's well.. out of bounds...
	 */
	public GridCell<Bounded> getGridCellAtGrid(int x, int y) throws IndexOutOfBoundsException {
		synchronized (editlock) {
			try {
				//System.out.println("cell ("+x+","+y+") is "+grid[x][y]);
				return grid.get(x).get(y);
			} catch (IndexOutOfBoundsException e) {
				System.out.println("cell (" + x + "," + y + ") is out of bounds");
				throw e;
			}
		}
	}

	/**
	 * using grid co-ords, tests if if <code>go</code> can exist at (<code>GridX, GridY</code>)
	 * This method assumes that <code>go</code> is checking if it needs
	 * to be perfectly aligned with the grid at it's origin. Usually canBeAtPixel is more useful.
	 * @param GridX in the Grid
	 * @param GridY in the Grid
	 * @param go
	 * @return 
	 */
	public boolean canBeAtGrid(int GridX, int GridY, Bounded go) {
		return canBeAtPixel(convGridToPixel(GridX), convGridToPixel(GridY), go);
	}

	/**
	 * using AMP co-ords, tests if <code>go</code> can exist at <code>(x, y)</code>
	 * if go is null, returns false
	 * @param x in the GA
	 * @param y in the GA
	 * @param go
	 * @return 
	 */
	@Override
	public boolean canBeAtPixel(double x, double y, Bounded go) {
		if (go == null) {
			return false;
		}
		synchronized (editlock) {
			final int w = convDimToGrid(go.getWidth(), x);
			final int h = convDimToGrid(go.getHeight(), y);
			final int GridX = convCoordToGrid(x);
			final int GridY = convCoordToGrid(y);
			GridCell<Bounded> cell;
			try {
				for (int i = GridX; i < GridX + w; i++) {
					for (int j = GridY; j < GridY + h; j++) {
						cell = getGridCellAtGrid(i, j);
						if (!isCellEmpty(i, j)) {
							for (Bounded goInCell : cell) {
								//System.out.println((goInCell != go) + ", " + (goInCell.getBounds().intersects((Rectangle) go.getBounds())) + " "+ go.hashCode() +" " + x + ", " + y);
								if (goInCell != null && goInCell != go && goInCell.getBounds().intersects(x, y, go.getWidth(), go.getHeight())) {
									return false;
								}
							}
						}
					}
				}
			} catch (IndexOutOfBoundsException aiobe) {
				System.out.println("out of bounds @canBeAtPixel");
				return false;

			}
			return true;
		}

	}

	/**
	 * should be used to convert an x or y to the grid.
	 * <p>
	 * conversion process: if num % gridsize == 0 it divides by gridSize.
	 * if not, it ceils that same equation.
	 * @param num
	 * @return the grid normalized index equivalent of num
	 */
	public final int convCoordToGrid(double num) {
		if (num % gridSize == 0) {
			return (int)(num / gridSize);
		} else {
			return /*int i = */ (int) Math.floor((num) / (gridSize));
			//System.out.println("converted " + num + "(coord) to " + i);
			//return i;
		}
	}

	/**
	 * should be used to convert a width or height to the grid.
	 * the <code>AMPcoord</code> parameter should be the corresponding coordinate
	 * singlet (either x or y for width and height respectively)
	 * associated with that dimension. this allows the method to
	 * account for if <code>go</code> occupies more cells than it is long.
	 * for example if the <code>AMPdim</code> was 25 long but started at 7
	 * it used to convert to 3 (if gridSize was 10) but it
	 * would actually occupy 4:<br>
	 * <code>0--10--20--30-40<br>
	 *       |  .|...|...|.  |</code>
	 * <code>if (AMPcoord + dim) % gridsize == 0</code> then it just ceils <code>AMPdim</code>/gridsize .
	 * this is when the go fits perfectly in an even number of gridspaces and
	 * the special accommodations for when it doesn't are not necessary and generate
	 * incorrect numbers if used
	 * @param AMPdim the dimension
	 * @param AMPcoord the corresponding coordinate singlet (w --> x and h --> y)
	 * @return the converted dimension
	 */
	public final int convDimToGrid(double AMPdim, double AMPcoord) {
		if (AMPcoord >= 0) {
			if (AMPcoord % gridSize == 0 || (AMPcoord + AMPdim) % gridSize == 0) {
				return (int) Math.ceil(((double) AMPdim) / ((double) gridSize));
			} else {
				return (int) Math.ceil(((double) ((AMPcoord % gridSize) + AMPdim)) / (double) (gridSize));
			}
		} else {
			return 0;
		}
	}

	/**
	 * should be used to convert an grid x or y to an actual pixel x or y.
	 * <p>
	 * conversion process: multiplies by gridSize.
	 * @param num
	 * @return num*gridSize
	 */
	public final int convGridToPixel(int num) {
		return num * gridSize;
	}

	/**
	 * creates a rectangle that is the space in the grid that go occupies
	 * @param go
	 * @return
	 */
	public Rectangle getGridbox(Bounded go) {
		return new Rectangle(convCoordToGrid(go.getX()), convCoordToGrid(go.getY()), convDimToGrid(go.getWidth(), go.getX()), convDimToGrid(go.getHeight(), go.getY()));
	}

	public int getGridSize() {
		return gridSize;
	}

	public int getGridHeight() {
		return height;
	}

	public int getGridWidth() {
		return width;
	}

	@Override
	public int getPixelHeight() {
		return convGridToPixel(height);
	}

	@Override
	public int getPixelWidth() {
		return convGridToPixel(width);
	}
}
