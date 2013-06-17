package assault.util;

import static assault.util.IndentingDebugPrinter.dp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import assault.display.Bounded;
import assault.display.Container;
import assault.util.IndentingDebugPrinter.IndentLevel;
import assault.util.IndentingDebugPrinter.LineTag;
import assault.util.IndentingDebugPrinter.PrefixTag;


public class QuadTreeNode<T extends Bounded> extends Container<QuadTreeNode<T>>{
	//TODO this whole thing needs to be made to work with rectangles...
	
	private static final PrefixTag CLASS_TAG = new PrefixTag("QTN");
	private static final IndentLevel ADD_METHOD_INDENT_LEVEL = new IndentLevel();
	private static final LineTag ADD_TAG = new LineTag("add",CLASS_TAG,ADD_METHOD_INDENT_LEVEL);
	private static final LineTag NUM_OBJ_TAG = new LineTag("num_obj",CLASS_TAG,ADD_METHOD_INDENT_LEVEL);
	
	static {
		ADD_TAG.enabled = false;
	}
	
	public final static int nMAX_UNTIL_SPLIT = 1;
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
		System.out.println("created new quadtreenode: " + this);
	}
	
	public boolean add(T obj_add){
		dp("adding " + obj_add, ADD_TAG);
		if (isLeaf && objects != null){
			if(objects != null && objects.size() >= nMAX_UNTIL_SPLIT){
				//create new branches
				dp("creating new level, with " + objects.size() + " + 1 objects", ADD_TAG,NUM_OBJ_TAG);
				if (NUM_OBJ_TAG.enabled){
					StringBuilder builder = new StringBuilder();
					builder.append("current objects:\n");
					for(Bounded b : objects){
						builder.append(b);
						builder.append('\n');
					}
					builder.append("and, adding:\n");
					builder.append(obj_add);
					builder.append('\n');
					dp(builder.toString(),ADD_TAG,NUM_OBJ_TAG);
				}
				branches = new ArrayList<QuadTreeNode<T>>(nSUB_TREES);
				
				//these HAVE to be correct with branchindex() !
				branches.add(0,new QuadTreeNode<T>(   divX ,  divY , (int)x + (int)getWidth() - divX , (int)y + (int)getHeight() - divY));
				branches.add(1,new QuadTreeNode<T>( (int)x ,  divY ,   divX - (int)x                 , (int)y + (int)getHeight() - divY));
				branches.add(2,new QuadTreeNode<T>(   divX ,(int)y , (int)x + (int)getWidth() - divX ,   divY - (int)y                 ));
				branches.add(3,new QuadTreeNode<T>( (int)x ,(int)y ,   divX - (int)x                 ,   divY - (int)y                 ));
				
				super.addChildren(branches);
				
				//add the rest of the objects
				dp("adding parent's objects", ADD_TAG);
				ADD_METHOD_INDENT_LEVEL.increment();
				for (T obj : objects) {
					branches.get(branchIndex((int)obj.getX(), (int)obj.getY(), divX, divY)).add(obj);
				}
				ADD_METHOD_INDENT_LEVEL.decrement();
				dp("done adding parent's objects", ADD_TAG);
				isLeaf = false;
				objects = null;
//				add the new object
				return add(obj_add);
			}else{
				dp("adding normally", ADD_TAG);
				if (!obj_add.noClip()){//if it clips
					dp("checking bounds", ADD_TAG);
					for (T obj : objects) {//check intersection
						if (obj_add.getBounds().intersects(obj.getX(),obj.getY(),obj.getWidth(),obj.getHeight())){
							dp("can't be there", ADD_TAG);
							return false;
						}
					}
					dp("all good", ADD_TAG);
				}
				objects.add(obj_add);
				return true;
			}
		}else if (branches != null){
			dp("I'm not a leaf... going deeper", ADD_TAG);
			ADD_METHOD_INDENT_LEVEL.increment();
			branches.get(branchIndex((int)obj_add.getX(), (int)obj_add.getY(), divX, divY)).add(obj_add);
			ADD_METHOD_INDENT_LEVEL.decrement();
			return true;
		}else{
			System.out.println("QTN: Something's wrong... I'm not a leaf or objects is null, and branches is null. " + obj_add + "wasn't added.");
			return false;
		}
	}
	
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
	public String toString() {
		return this.getClass().getSimpleName() + " x=" + getX() + " y=" + getY() + " w=" + getWidth() + " h=" +getHeight();
	}
	
	@Override
	public void drawSelf() {
		super.drawSelf();
		drawRect(getX(),getY(),getWidth(),getHeight());
	}

	@Override
	public void drawChildren() {
		for (QuadTreeNode<T> child : getChildren()) {
			child.drawSelf();
		}
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
	<pre>Return the index of the branch which the point will fall in.
	defines:   
	             y
	       ------^------
	       |     |     |
	       |  1  |  0  |
 	  divY ------------> x
 	       |  3  |  2  |
 	       |     |     |
 	       ------|------
 	           divX </pre>
	**/
	public static final int branchIndex(int x, int y, int divX, int divY){
		//the creation of the sub nodes should match this always.
		return (x >= divX ? 0 : 1) + (y >= divY ? 0 : 2);
	}

	public void removeAll() {
		init((int)getX(),(int)getY(),(int)getWidth(),(int)getHeight(),null);
	}
}
