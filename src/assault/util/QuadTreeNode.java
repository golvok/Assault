package assault.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import assault.display.Bounded;
import assault.display.Container;


public class QuadTreeNode<T extends Bounded> extends Container<T>{
	//TODO this whole thing needs to be made to work with rectangles...
	
	
	public final static int nMAX_UNTIL_SPLIT = 10;
	public final static int nSUB_TREES = 4; //please don't change this.
	
	private int divX;
	private int divY;
	
	private boolean isLeaf = true;
	private List<QuadTreeNode<T>> branches = null;
	
	private List<T> objects = Collections.synchronizedList(new ArrayList<T>(nMAX_UNTIL_SPLIT));
	
	
	public QuadTreeNode(int x, int y, int width, int height){
		this(x,y,width,height,null);
	}
	
	public QuadTreeNode(int x, int y, int width, int height, ArrayList<T> initial) {
		super(x, y, width, height, null);
		init(x, y, width, height, initial);
	}
	
	private final void init(int x, int y, int w, int h, ArrayList<T> initial) {
		divX = x + w/2;
		divY = y + h/2;
		if (initial != null){
			for (T obj : initial){
				add(obj);
			}
		}
	}
	
	public boolean add(T obj_add){
//		dp("adding " + obj_add);
		if (isLeaf && objects != null){
			if(objects != null && objects.size() >= nMAX_UNTIL_SPLIT){
				//create new branches
//				dp("creating new level");
				branches = new ArrayList<QuadTreeNode<T>>(nSUB_TREES);
				
				//these HAVE to be correct with branchindex() !
				branches.add(0,new QuadTreeNode<T>(   divX ,  divY , (int)x + (int)getWidth() - divX , (int)y + (int)getHeight() - divY));
				branches.add(1,new QuadTreeNode<T>( (int)x ,  divY ,   divX - (int)x                 , (int)y + (int)getHeight() - divY));
				branches.add(2,new QuadTreeNode<T>(   divX ,(int)y , (int)x + (int)getWidth() - divX ,   divY - (int)y                 ));
				branches.add(3,new QuadTreeNode<T>( (int)x ,(int)y ,   divX - (int)x                 ,   divY - (int)y                 ));
				
				//add the rest of the objects
//				dp("adding parent's objects");
//				++indentDepth;
				for (T obj : objects) {
					branches.get(branchIndex((int)obj.getX(), (int)obj.getY(), divX, divY)).add(obj);
				}
//				--indentDepth;
//				dp("done adding parent's objects");
				isLeaf = false;
				objects = null;
				//add the new object
				return add(obj_add);
			}else{
//				dp("adding normally");
				if (!obj_add.noClip()){//if it clips
//					dp("checking bounds");
					for (T obj : objects) {//check intersection
						if (obj_add.getBounds().intersects(obj.getX(),obj.getY(),obj.getWidth(),obj.getHeight())){
//							dp("can't be there");
							return false;
						}
					}
//					dp("all good");
				}
				objects.add(obj_add);
				return true;
			}
		}else if (branches != null){
//			dp("I'm not a leaf... going deeper");
//			indentDepth++;
			branches.get(branchIndex((int)obj_add.getX(), (int)obj_add.getY(), divX, divY)).add(obj_add);
//			indentDepth--;
			return true;
		}else{
			System.out.println("QTN: Something's wrong... I'm not a leaf or objects is null, and branches is null. " + obj_add + "wasn't added.");
			return false;
		}
	}
	
//	private static int indentDepth = 0;
	
//	private void dp(String string) {
//		System.out.println("QOM: " + repeatchar(indentDepth, '\t') + string);
//	}
	
//	private String repeatchar(int n, char c){
//		String out = "";
//		for (int i = 0; i < n; i++) {
//			out += c;
//		}
//		return out;
//	}

	
	public class CanBeAtVals {
		public boolean canItBeThere = false;
		public QuadTreeNode<T> deepestLeaf = null;
	}
	
	public CanBeAtVals canBeAt(int x, int y, T t){
		CanBeAtVals ret = new CanBeAtVals();
		ret.deepestLeaf = getLeaf(x,y);
		ret.canItBeThere = ret.deepestLeaf.getAt(x,y).size() != 0;
		return ret;
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
	
	public QuadTreeNode<T> getLeaf(int x, int y){
		if (isLeaf){
			return this;
		}else{
			return getBranch(this, x, y).getLeaf(x, y);
		}
	}
	
	public List<T> getAt(int x, int y){
		if (isLeaf){
			ArrayList<T> ret = new ArrayList<>();
			for (T t : objects) {
				if (t.getBounds().contains(x,y)){
					ret.add(t);				
				}
			}
			return ret;
		}else{
			return getBranch(this, x, y).getAt(x, y);
		}
	}
	
	@Override
	public void drawChildren() {
		if (branches != null){
			for(QuadTreeNode<T> branch : branches){
				branch.draw();
			}
		}
	}
	
	@Override
	public void drawSelf() {
		super.drawSelf();
		drawBoundingBox(this);
	}
	
	public QuadTreeNode<T> getContainer(T obj){
		if (isLeaf){
			return this;
		}else{
			return branches.get(branchIndex(obj, divX, divY)).getContainer(obj);
		}
	}

	List<QuadTreeNode<T>> getBranches(){
		return branches;
	}
	
	public int getDivX() {
		return divX;
	}

	public int getDivY() {
		return divY;
	}

	public static final <Q extends Bounded> QuadTreeNode<Q> getBranch(QuadTreeNode<Q> parent, int x, int y){
		return parent.getBranches().get(branchIndex(x, y, parent.getDivX(), parent.getDivY()));
	}
	
	public static final int branchIndex(Bounded obj, int divX, int divY){
		return branchIndex((int)obj.getX(), (int) obj.getY(), divX, divY);
	}
	
	/**
	Return the index of the branch which the point will fall in.
	defines:   
	<code>       y
	       ------^------
	       |     |     |
		   |  1  |  0  |
 	  divY ------------> x
 	       |  3  |  2  |
 	       |     |     |
 	       ------|------
 	           divX </code>
	**/
	public static final int branchIndex(int x, int y, int divX, int divY){
		//the creation of the sub nodes should match this always.
		return (x >= divX ? 0 : 1) + (y >= divY ? 0 : 2);
	}

	public void removeAll() {
		init((int)getX(),(int)getY(),(int)getWidth(),(int)getHeight(),null);
	}
}
