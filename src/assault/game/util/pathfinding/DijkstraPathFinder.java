/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.util.pathfinding;

import assault.game.util.GridManager;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author 088241930
 */
public class DijkstraPathFinder extends RawPathFinder {

	private Object startedLock = new Object();
	private boolean started = false;

	public DijkstraPathFinder(GridManager gm) {
		super(gm);
	}

	@Override
	public RawPathObject findPath(PathFindingGridObject pfgo, int destX, int destY) {
		synchronized (startedLock) {
			if (started || isCanceled()) {
				return null;
			}
			started = true;
		}

		int sx = gManager.convCoordToGrid(pfgo.getX());//start X
		int sy = gManager.convCoordToGrid(pfgo.getY());//start Y
		destX = gManager.convCoordToGrid(destX);
		destY = gManager.convCoordToGrid(destY);

		if (!gManager.canBeAtGrid(destX, destY, pfgo)) {
			System.out.println("Cannot be at path's end so there is no path.");
			return null;
		}

		pfgo.setExamined(new boolean[gManager.getGridWidth()][gManager.getGridHeight()]);
		pfgo.setOnOpenSet(new boolean[gManager.getGridWidth()][gManager.getGridHeight()]);
		pfgo.setOnPath(new boolean[gManager.getGridWidth()][gManager.getGridHeight()]);
		pfgo.setClosedSet(new boolean[gManager.getGridWidth()][gManager.getGridHeight()]);

		System.out.println("Starting Dijkstra pathfinder....\nFrom : " + sx + "," + sy + "\nTo   : " + destX + "," + destY);

		PathFindingGridPoint[][] cameFrom = new PathFindingGridPoint[gManager.getGridWidth()][gManager.getGridHeight()];     //where GP(x,y) came from
		PathFindingGridPoint[] neighs = new PathFindingGridPoint[8];//neighbours (allways 8or less)
		PriorityQueue<PathFindingGridPoint> openSet = new PriorityQueue<PathFindingGridPoint>(20, new Comparator<PathFindingGridPoint>() //nodes to be checked, ordered with lowest f value on top
		{

			@Override
			public int compare(PathFindingGridPoint g1, PathFindingGridPoint g2) {
				if (g1.getF() < g2.getF()) {
					return -1;
				}
				if (g1.getF() > g2.getF()) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		//now for the algorithim...
		PathFindingGridPoint[] openSetArr = openSet.toArray(new PathFindingGridPoint[1]);
		PathFindingGridPoint end = new PathFindingGridPoint(destX, destY);
		PathFindingGridPoint start = new PathFindingGridPoint(sx, sy, 0);//startx,starty, g(0 at this point), h
		//System.out.println("h = "+h);
		openSet.add(start);
		PathFindingGridPoint lowF;
		//used for the creation of the neighbours
		double neighNewF;
		boolean newGIsBetter;
		int nxOff;//offset
		int nyOff;
		int nx;//new
		int ny;
		//System.out.print("searching...");
		while (!openSet.isEmpty() && !isCanceled()) {
			System.out.print(".");
			lowF = openSet.poll();//get the GP w/ the smallest f (retreive and remove)
			//System.out.println("exaimining : " + lowF.getX() + "," + lowF.getY() + " f:" + lowF.getF());
			//synchronized (this){
			//	try {
			//		wait(100);
			//	} catch (InterruptedException ex) {
			//	}
			//}
			pfgo.getExamined()[lowF.getX()][lowF.getY()] = true;
			if (lowF.getX() == destX && lowF.getY() == destY) {//is this point the end?
				System.out.println("Found a path!");
				return new RawPathObject(start, end, cameFrom, gManager, pfgo.getOnPath());
			} else {
				//.poll removes it from the openset
				pfgo.getClosedSet()[lowF.getX()][lowF.getY()] = true;//add it to the closed set
				for (int i = 0; i < 8; i++) {//change to 2 and 6 when using manhattan distance (also change calcH())
					// 0 2 1
					// 3   4
					// 6 5 7
					if (i % 3 == 0) {//0,3,6
						nxOff = -1;
					} else if (i % 3 == 1) {//1,4,7
						nxOff = +1;
					} else {//2,5
						nxOff = 0;
					}
					if (i < 3) {//0,1,2
						nyOff = -1;
					} else if (i > 4) {//5,6,7
						nyOff = +1;
					} else {//3,4
						nyOff = 0;
					}
					nx = lowF.getX() + nxOff;
					ny = lowF.getY() + nyOff;
					//check if it is within the grid
					if (nx >= 0 && nx < gManager.getGridWidth() && ny >= 0 && ny < gManager.getGridHeight() && gManager.canBeAtGrid(nx, ny, pfgo)) {
						neighs[i] = new PathFindingGridPoint(nx, ny);
						//check if this point allready has an ASGP
						if (openSet.contains(neighs[i])) {//.equals has been overridden.
							openSetArr = openSet.toArray(openSetArr);
							for (int j = 0; j < openSetArr.length; j++) {
								if (neighs[i].equals(openSetArr[j])) {//.equals has been overridden.
									neighs[i] = openSetArr[j];//.equals has been overridden.
									//System.out.println("an ASGP has allready been made for this");
								}
							}
						}
					} else {
						neighs[i] = null;
					}
				}
				for (int i = 0; i < neighs.length; i++) {
					PathFindingGridPoint neigh = neighs[i];
					if (neigh != null && !pfgo.getClosedSet()[neigh.getX()][neigh.getY()]) {
						neighNewF = fBetweenNeighs(lowF, neigh) + lowF.getF();
						//System.out.println("new g = "+neighNewG + ", old g = " +lowF.g);
						if (!openSet.contains(neigh)) {
							newGIsBetter = true;
						} else if (neighNewF < neigh.getF() || neigh.getF() == 0) {
							newGIsBetter = true;
							openSet.remove(neigh);//to cause the priority queue to reorder it
						} else {
							newGIsBetter = false;
						}
						if (newGIsBetter) {
							cameFrom[neigh.getX()][neigh.getY()] = lowF;
							neigh.setF(neighNewF);
							//System.out.println("new f = "+neigh.getF());
							//in the case that the f is changing
							//to cause the priorityqueue to reorder it has allready been
							//removed (see the else if statement above)
							openSet.add(neigh);
							pfgo.getOnOpenSet()[neigh.getX()][neigh.getY()] = true;
						}
					}
				}
			}
		}
		System.out.println("couldn't find path. canceled = " + isCanceled());
		return null;
	}

	private double fBetweenNeighs(PathFindingGridPoint srt, PathFindingGridPoint neigh) {
		if (srt.getX() == neigh.getX() || srt.getY() == neigh.getY()) {
			return 1;
		} else {
			return 1.4142135623730952;//approx sqrt(2) + 0.0000000000000001
		}
	}
}
