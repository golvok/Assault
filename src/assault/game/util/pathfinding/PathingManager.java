
package assault.game.util.pathfinding;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import assault.game.util.GridManager;
import assault.game.util.pathfinding.moving.Relocatable;
import assault.util.Ptr;

/**
 *
 * @author matt
 */
public class PathingManager{
	
	private final static int N_THREADS = 4;//TODO make this configurable
	
	private ExecutorService threadPool = java.util.concurrent.Executors.newFixedThreadPool(N_THREADS);
	private Map<PathFindingBounded, PathFindingRunnable> pfgo2Task = Collections.synchronizedMap(new HashMap<PathFindingBounded, PathFindingRunnable>(N_THREADS));
	private RawPathFinder rpf;
	
	public PathingManager(GridManager gm) {
		rpf = new AStarPathFinder(gm);
	}

	public void pathToNextPoint(Relocatable target){
		PathFindingRunnable pfr = new PathFindingRunnable(target, rpf);
		pfgo2Task.put(target, pfr);
		threadPool.execute(pfr);
	}
	
	public int cancel(PathFindingBounded pfgo){
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
	private Relocatable target;
	private RawPathFinder rpf;
	private Ptr<Boolean> canceled = new Ptr<>(false);
	
	public PathFindingRunnable(Relocatable target, RawPathFinder rpf) {
		this.rpf = rpf;
		this.target = target;
	}
	
	@Override
	public void run() {
		if (!canceled.getVal()){
			RawPathObject result = rpf.findPathToNextPoint(target, canceled);
			if(!canceled.getVal() && result != null){
				target.getPath().addPoints(result);
			}
			return;
		}
		System.out.println("a PathFindingRunnable was canceled before it started. probably bad.");
	}
	
	public void cancel(){
		canceled.setVal(true);
	}
	
}