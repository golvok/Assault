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
 * @author matt
 */
public class AStarPathFinder extends RawPathFinder {

	private boolean started;
	private final Object startedLock = new Object();
	
    public AStarPathFinder(GridManager gm) {
        super(gm);
    }

    @Override
    public synchronized RawPathObject findPath(PathFindingGridObject pfgo, int destX, int destY) {
		
		synchronized (startedLock) {
			if (started || isCanceled()) {
				System.out.println("the pathfinder has allready been started, or was canceled before it started");
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
		
        System.out.println("Starting A* pathfinder....\nFrom : " + sx + "," + sy + "\nTo   : " + destX + "," + destY);
		
        pfgo.setExamined(new boolean[gManager.getGridWidth()][gManager.getGridHeight()]);
        pfgo.setOnOpenSet(new boolean[gManager.getGridWidth()][gManager.getGridHeight()]);
        pfgo.setOnPath(new boolean[gManager.getGridWidth()][gManager.getGridHeight()]);
        pfgo.setClosedSet(new boolean[gManager.getGridWidth()][gManager.getGridHeight()]);
		
        //Some definitions:
        //h - the heuristic estimate of the distance from this point to the end
        //g - the length of the path from the start to the point
        //f = h + g
		
        double h = calcH(destX, sx, destY, sy);

        AStarGridPoint end = new AStarGridPoint(destX, destY);
        AStarGridPoint start = new AStarGridPoint(sx, sy, 0, h);//startx,starty, g(0 at this point), h
		
        AStarGridPoint[][] cameFrom = new AStarGridPoint[gManager.getGridWidth()][gManager.getGridHeight()];     //where GP(x,y) came from
        AStarGridPoint[] neighs = new AStarGridPoint[8];//neighbours (allways 8or less)
		
        PriorityQueue<AStarGridPoint> openSet = new PriorityQueue<AStarGridPoint>(20, new Comparator<AStarGridPoint>() //nodes to be checked, ordered with lowest f value on top
        {

            @Override
            public int compare(AStarGridPoint g1, AStarGridPoint g2) {
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

        AStarGridPoint[] openSetArr = openSet.toArray(new AStarGridPoint[1]);
		
        //now for the algorithim...
		
        //System.out.println("h = "+h);
        openSet.add(start);
        AStarGridPoint lowF;
        //used for the creation of the neighbours
        double neighNewG;
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
            /*synchronized (this){
            	try {
            		wait(25);
            	} catch (InterruptedException ex) {
            	}
            }*/
            pfgo.getExamined()[lowF.getX()][lowF.getY()] = true;
            if (lowF.equals(end)) {//is this point the end? (.equals() has been overridden)
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
                        neighs[i] = new AStarGridPoint(nx, ny);
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
                    AStarGridPoint neigh = neighs[i];
                    if (neigh != null && !pfgo.getClosedSet()[neigh.getX()][neigh.getY()]) {
                        neighNewG = gBetweenNeighs(lowF, neigh) + lowF.getG();
                        //System.out.println("new g = "+neighNewG + ", old g = " +lowF.g);
                        if (!openSet.contains(neigh)) {
                            newGIsBetter = true;
                        } else if (neighNewG < neigh.getG() || neigh.getG() == 0) {
                            newGIsBetter = true;
                            openSet.remove(neigh);//to cause the priority queue to reorder it
                        } else {
                            newGIsBetter = false;
                        }
                        if (newGIsBetter) {
                            cameFrom[neigh.getX()][neigh.getY()] = lowF;
                            neigh.setG(neighNewG);
                            //neigh.setH(calcH(neigh, end));
                            neigh.setH(calcHWithVectorCross(start, end, neigh));
                            //System.out.println("new h = "+neigh.h);
                            neigh.setF(neigh.getG() + neigh.getH());
                            //System.out.println("new f = "+neigh.getF());
                            //in the case that the g is changing(therefore the f is changing)
                            //to cause the priorityqueue to reorder it has allready been
                            //removed (see the else if statement above)
                            openSet.add(neigh);
                            pfgo.getOnOpenSet()[neigh.getX()][neigh.getY()] = true;
                        }
                    }
                }
            }
        }
        System.out.println("couldn't find path. canceled = "+isCanceled());
        return null;
    }

    private double gBetweenNeighs(AStarGridPoint start, AStarGridPoint neigh) {
        if (start.getX() == neigh.getX() || start.getY() == neigh.getY()) {
            return 1;
        } else {
            return 1.4142136;//...623730951...;//approx sqrt(2) + 0.0000001
        }
    }

    private double calcH(AStarGridPoint n1, AStarGridPoint n2) {
        return calcH(n1.getX(), n1.getY(), n2.getX(), n2.getY());
    }

    private double calcH(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));//euclidian distance
        //return (Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2))) * 1.001;//euclidian distance*1.001, this causes the h to be at a slightly smaller scale than g (and worth slightly more)
        //return Math.abs(x2-x1) + Math.abs(y2-y1);//manhattan distance
    }

    /**
     * also uses the vector cross product.
     * causes very straight paths that however
     * <i>always</i> follow the straight line to the goal
     * @param start
     * @param end
     * @param current
     * @return 
     */
    private double calcHWithVectorCross(AStarGridPoint start, AStarGridPoint end, AStarGridPoint current) {
        return calcH(end, current) + (Math.abs((current.getX() - end.getX()) * (start.getX() - end.getX()) - (current.getY() - end.getY()) * (start.getY() - end.getY())) * 0.01);
    }
}
