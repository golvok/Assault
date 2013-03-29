package assault.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import assault.display.Bounded;


public class QuadTreeNode<T extends Bounded>{
	
	public final static int nMAX_UNTIL_SPLIT = 5;
	public final static int nSUB_TREES = 4; //please don't change this.
	
	private int divX;
	private int divY;
	
	private boolean isLeaf = true;
	private List<QuadTreeNode<T>> branches = null;
	
	private List<T> objects = Collections.synchronizedList(new ArrayList<T>(nMAX_UNTIL_SPLIT));
	
	
	public QuadTreeNode(){
		this(null);
	}
	
	public QuadTreeNode(List<T> initial) {
		if (initial != null){
			for (T obj : initial){
				add(obj);
			}
		}
	}

	public boolean add(T obj_add){
		if (isLeaf && objects != null){
			if(objects != null && objects.size() >= nMAX_UNTIL_SPLIT){
				//create new branches
				branches = Collections.synchronizedList(new ArrayList<QuadTreeNode<T>>(nSUB_TREES));
				for(int i = 0;i < nSUB_TREES;++i){
					branches.add(new QuadTreeNode<T>());
				}
				//add the rest of the objects
				for (T obj : objects) {
					branches.get(branchIndex((int)obj.getX(), (int)obj.getY(), divX, divY)).add(obj);
				}
				isLeaf = false;
				objects = null;
				//add the new object
				return add(obj_add);
			}else{
				objects.add(obj_add);
				return true;
			}
		}else if (branches != null){
			branches.get(branchIndex((int)obj_add.getX(), (int)obj_add.getY(), divX, divY)).add(obj_add);
			return true;
		}else{
			System.out.println("QTN: Something's wrong... I'm not a leaf or objects is null, and branches is null. " + obj_add + "wasn't added.");
			return false;
		}
	}
	
	public boolean remove(T obj){
		if(obj == null){
			return false;
		}else if (!isLeaf && branches != null){
			return branches.get(branchIndex(obj, divX, divY)).remove(obj);
		}else if (objects != null){
			return objects.remove(obj);
		}else{
			System.out.println("QTN: objects was null and I'm not a leaf. " + obj + " wasn't removed.");
			return false;
		}
	}
	
	public QuadTreeNode<T> getContainer(T obj){
		if (!isLeaf){
			return branches.get(branchIndex(obj, divX, divY)).getContainer(obj);
		}else{
			return this;
		}
	}
	
	public T get(int x, int y){
		if (isLeaf){
			for (T obj : objects) {
				if (obj.getBounds().contains(x,y)){
					return obj;
				}
			}
			return null;
		} else {//not a leaf
			return branches.get(branchIndex(x, y, divX, divY)).get(x, y);
		}
	}
	
	public static final int branchIndex(Bounded obj, int divX, int divY){
		return branchIndex((int)obj.getX(), (int) obj.getY(), divX, divY);
	}
	
	public static final int branchIndex(int x, int y, int divX, int divY){
		return (x >= divX ? 0 : 2) + (y >= divY ? 1 : 0);
	}
}
