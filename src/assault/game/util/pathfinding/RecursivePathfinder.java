package assault.game.util.pathfinding;

import assault.game.util.ObjectManager;
import assault.game.util.pathfinding.moving.AbstractPathObject;
import assault.game.util.pathfinding.moving.Relocatable;
import assault.util.Ptr;
public class RecursivePathfinder extends AbstractPathFinder {

	public RecursivePathfinder(ObjectManager gm) {
		super(gm);
	}

	@Override
	public RawPathObject findPathToNextPoint(Relocatable target, Ptr<Boolean> canceled) {
		if(target.noClip()){
//			return ;
		}
		if(!AbstractPathObject.canMakeItToNextPointInStraightLine(target.getPath(), target, oManager)){
//			return ;
		}
		throw new UnsupportedOperationException();
	}

}
