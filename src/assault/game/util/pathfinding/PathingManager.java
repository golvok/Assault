
package assault.game.util.pathfinding;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import assault.game.util.GridManager;
import assault.game.util.pathfinding.moving.AbstractPathObject;
import assault.util.Ptr;

/**
 *
 * @author matt
 */
public class PathingManager{
	
	private final static int N_THREADS = 4;//TODO make this configurable
	
	private ExecutorService threadPool;
	private Map<PathFindingGridObject, PathFindingRunnable> pfgo2Task;
	private RawPathFinder rpf;
	
	public PathingManager(GridManager gm) {
		threadPool = java.util.concurrent.Executors.newFixedThreadPool(N_THREADS);
		pfgo2Task = Collections.synchronizedMap(new HashMap<PathFindingGridObject, PathFindingRunnable>(N_THREADS));
		rpf = new AStarPathFinder(gm);
	}

	public void findPath(final PathFindingGridObject pfgo, final int destX, final int destY, final AbstractPathObject apoToaddTo, final boolean clearApo, final boolean clearImmediately) {
		PathFindingRunnable pfr = new PathFindingRunnable(rpf, pfgo, destX, destY, apoToaddTo, clearApo, clearImmediately);
		pfgo2Task.put(pfgo, pfr);
		threadPool.execute(pfr);
	}
	public int cancel(PathFindingGridObject pfgo){
		PathFindingRunnable pfr = pfgo2Task.remove(pfgo);
		if(pfr == null){
			return 1;
		}else{
			pfr.cancel();
			return 0;
		}
	}
}

class PathFindingRunnable implements Runnable{
	private PathFindingGridObject pfgo;
	private int destX;
	private int destY;
	private AbstractPathObject apoToaddTo;
	private boolean clearApo;
	private boolean clearImmediately;
	private RawPathFinder rpf;
	private Ptr<Boolean> canceled = new Ptr<>(false);
	
	public PathFindingRunnable(RawPathFinder rpf, PathFindingGridObject pfgo, int destX, int destY, AbstractPathObject apoToaddTo, boolean clearApo, boolean clearImmediately) {
		this.rpf = rpf;
		this.pfgo = pfgo;
		this.destX = destX;
		this.destY = destY;
		this.apoToaddTo = apoToaddTo;
		this.clearApo = clearApo;
		this.clearImmediately = clearImmediately;
	}
	@Override
	public void run() {
		if (!canceled.getVal()){
			rpf.findPath(pfgo, destX, destY, apoToaddTo, clearApo, clearImmediately,canceled);
			return;
		}
		System.out.println("a PathFindingRunnable was canceled before it started. probably bad.");
	}
	
	public void cancel(){
		canceled.setVal(true);
	}
	
	
	
}