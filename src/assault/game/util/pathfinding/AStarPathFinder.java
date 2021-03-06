
package assault.game.util.pathfinding;

import java.util.Comparator;
import java.util.PriorityQueue;

import assault.game.util.GridManager;
import assault.game.util.pathfinding.moving.Relocatable;
import assault.util.Point;
import assault.util.Ptr;

/**
 *
 * @author matt
 */
public class AStarPathFinder extends RawPathFinder {

//	private boolean started;
//	private final Object startedLock = new Object();
	
    public AStarPathFinder(GridManager gm) {
        super(gm);
    }

    @Override
    public RawPathObject findPathToNextPoint(Relocatable target, Ptr<Boolean> canceled) {
    	if(canceled.getVal()){
    		return null;
    	}
//		synchronized (startedLock) {
//			if (started || isCanceled()) {
//				System.out.println("the pathfinder has allready been started, or was canceled before it started");
//				return;
//			}
//			started = true;
//		}
		
        int sx = getOMasGM().convCoordToGrid(target.getX());//start X
        int sy = getOMasGM().convCoordToGrid(target.getY());//start Y
        int destX;
        int destY;
        {
	        Point destPoint = target.getPath().peek();
	        destX = getOMasGM().convCoordToGrid(destPoint.x);
	        destY = getOMasGM().convCoordToGrid(destPoint.y);
        }
        if (!getOMasGM().canBeAtGrid(destX, destY, target)) {
            System.out.println("Cannot be at path's end so there is no path.");
            return null;
        }
		
        System.out.println("Starting A* pathfinder....\nFrom : " + sx + "," + sy + "\nTo   : " + destX + "," + destY);
		
        target.setExamined(new boolean[getOMasGM().getGridWidth()][getOMasGM().getGridHeight()]);
        target.setOnOpenSet(new boolean[getOMasGM().getGridWidth()][getOMasGM().getGridHeight()]);
        target.setOnPath(new boolean[getOMasGM().getGridWidth()][getOMasGM().getGridHeight()]);
        target.setClosedSet(new boolean[getOMasGM().getGridWidth()][getOMasGM().getGridHeight()]);
		
        //Some definitions:
        //h - the heuristic estimate of the distance from this point to the end
        //g - the length of the path from the start to the point
        //f = h + g
		
        float h = (float)calcH(destX, sx, destY, sy);

        AStarGridPoint end = new AStarGridPoint(destX, destY);
        AStarGridPoint start = new AStarGridPoint(sx, sy, 0, h);//startx,starty, g(0 at this point), h
		
        AStarGridPoint[][] cameFrom = new AStarGridPoint[getOMasGM().getGridWidth()][getOMasGM().getGridHeight()];     //where GP(x,y) came from
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
        float neighNewG;
        boolean newGIsBetter;
        int nxOff;//offset
        int nyOff;
        int nx;//new
        int ny;
        //System.out.print("searching...");
        while (!openSet.isEmpty() && !canceled.getVal()) {
            System.out.print(".");
            lowF = openSet.poll();//get the GP w/ the smallest f (retreive and remove)
            //System.out.println("exaimining : " + lowF.getX() + "," + lowF.getY() + " f:" + lowF.getF());
            /*synchronized (this){
            	try {
            		wait(25);
            	} catch (InterruptedException ex) {
            	}
            }*/
            target.getExamined()[lowF.getX()][lowF.getY()] = true;
            if (lowF.equals(end)) {//is this point the end? (.equals() has been overridden)
            	RawPathObject rpo = new RawPathObject(start, end, cameFrom, getOMasGM(), target.getOnPath());
                System.out.println("Found a path!");
                return rpo;
            } else {
                //.poll removes it from the openset
                target.getClosedSet()[lowF.getX()][lowF.getY()] = true;//add it to the closed set
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
                    if (nx >= 0 && nx < getOMasGM().getGridWidth() && ny >= 0 && ny < getOMasGM().getGridHeight() && getOMasGM().canBeAtGrid(nx, ny, target)) {
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
                    if (neigh != null && !target.getClosedSet()[neigh.getX()][neigh.getY()]) {
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
                            neigh.setH((float)calcHWithVectorCross(start, end, neigh));
                            //System.out.println("new h = "+neigh.h);
                            neigh.setF(neigh.getG() + neigh.getH());
                            //System.out.println("new f = "+neigh.getF());
                            //in the case that the g is changing(therefore the f is changing)
                            //to cause the priorityqueue to reorder it has allready been
                            //removed (see the else if statement above)
                            openSet.add(neigh);
                            target.getOnOpenSet()[neigh.getX()][neigh.getY()] = true;
                        }
                    }
                }
            }
        }
        System.out.println("couldn't find path. canceled = " + canceled.getVal());
        return null;
    }

    private GridManager getOMasGM() {
		return (GridManager)oManager;
	}

	private float gBetweenNeighs(AStarGridPoint start, AStarGridPoint neigh) {
        if (start.getX() == neigh.getX() || start.getY() == neigh.getY()) {
            return 1;
        } else {
        	return (float)1.4142135623730952;//approx sqrt(2) + 0.0000000000000001
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
