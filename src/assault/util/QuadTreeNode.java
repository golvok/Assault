package assault.util;

import static assault.util.IndentingDebugPrinter.dp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.newdawn.slick.geom.Shape;

import assault.display.Bounded;
import assault.display.Container;
import assault.util.IndentingDebugPrinter.IndentLevel;
import assault.util.IndentingDebugPrinter.Line;
import assault.util.IndentingDebugPrinter.LineTag;
import assault.util.IndentingDebugPrinter.PrefixTag;


public class QuadTreeNode<T extends Bounded> extends Container<QuadTreeNode<T>>{
	
	private static final PrefixTag CLASS_TAG = new PrefixTag("QTN");
	
	private static final IndentLevel ADD_METHOD_INDENT_LEVEL = new IndentLevel();
	private static final LineTag ADD_TAG = new LineTag("add",CLASS_TAG,ADD_METHOD_INDENT_LEVEL);
	private static final LineTag NUM_OBJ_TAG = new LineTag("num_obj",CLASS_TAG,ADD_METHOD_INDENT_LEVEL);

	private static final IndentLevel GET_AT_METHOD_INDENT = new IndentLevel();
	public static final LineTag GET_AT_TAG = new LineTag("getAt", CLASS_TAG, GET_AT_METHOD_INDENT);
	
	static {
//		ADD_TAG.enabled = false;
//		NUM_OBJ_TAG.enabled = false;
//		GET_AT_TAG.enabled = false;
	}
	
	public final static int nMAX_UNTIL_SPLIT = 1;
	public final static int nSUB_TREES = 4; //please don't change this.
	
	private int divX;
	private int divY;
	
	private List<QuadTreeNode<T>> branches = null;
	
	private List<T> objects = new ArrayList<T>(nMAX_UNTIL_SPLIT);
	
	
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
		branches = null;
		objects.clear();
		if (initial != null){
			for (T obj : initial){
				add(obj);
			}
		}
		System.out.println("created new quadtreenode: " + this);
	}
	
	public enum AddRetVal{
		ALL_GOOD(true),
		WONT_FIT(false),
		CANT_BE_THERE(false),
		;
		private boolean success;
		private AddRetVal(boolean success) {
			this.success = success;
		}
		public boolean isSuccess() {
			return success;
		}
	}
	
	public AddRetVal add(T t){
		return addAs(t,t);
	}

	public AddRetVal addAs(final Bounded surrogate,T actual){
		dp(new Line() {public String l() {return
				"adding " + surrogate + "; " + QuadTreeNode.this;}}, ADD_TAG);
		if (!willFit(this,surrogate)){
			dp("wont't fit!", ADD_TAG);
			return AddRetVal.WONT_FIT;
		}
		if(branches == null){//hasn't split yet
			if(objects.size() < nMAX_UNTIL_SPLIT){
				dp("adding normally", ADD_TAG);//ie. not too many objects.
				if (!surrogate.noClip()){//if it clips
					dp("checking bounds", ADD_TAG);
					for (T obj : objects) {//check intersection
						if (surrogate.clipsWith(obj)){
							dp("can't be there", ADD_TAG);
							return AddRetVal.CANT_BE_THERE;
						}
					}
					dp("all good", ADD_TAG);
				}
				objects.add(actual);
				return AddRetVal.ALL_GOOD;
			}else{
				//create new branches
				dp(new Line() {public String l() {return
					"creating new level. Have " + objects.size() + " + 1 objects to add.";}},ADD_TAG,NUM_OBJ_TAG);
				if (NUM_OBJ_TAG.enabled){
					StringBuilder builder = new StringBuilder();
					builder.append("current objects:\n");
					for(Bounded b : objects){
						builder.append(b);
						builder.append('\n');
					}
					builder.append("and, adding:\n");
					builder.append(surrogate);
					builder.append('\n');
					dp(builder.toString(),NUM_OBJ_TAG);
				}
				branches = new ArrayList<QuadTreeNode<T>>(nSUB_TREES);
				
				//these HAVE to be correct with branchindex() !
				branches.add(0,new QuadTreeNode<T>(   divX     ,   divY     , (int)getX() + (int)getWidth() - divX , (int)getY() + (int)getHeight() - divY));
				branches.add(1,new QuadTreeNode<T>( (int)getX(),   divY     ,   divX - (int)getX()                 , (int)getY() + (int)getHeight() - divY));
				branches.add(2,new QuadTreeNode<T>(   divX     , (int)getY(), (int)getX() + (int)getWidth() - divX ,   divY - (int)getY()                 ));
				branches.add(3,new QuadTreeNode<T>( (int)getX(), (int)getY(),   divX - (int)getX()                 ,   divY - (int)getY()                 ));
				
				super.addChildren(branches);
				
				//add the rest of the objects
				dp("adding parent's objects", ADD_TAG);
				List<T> addedSuccessfully = new ArrayList<>(nMAX_UNTIL_SPLIT);
				for (T obj : objects) {
					ADD_METHOD_INDENT_LEVEL.increment();
					AddRetVal branchAddResult = branches.get(branchIndex((int)obj.getX(), (int)obj.getY(), divX, divY)).add(obj);
					ADD_METHOD_INDENT_LEVEL.decrement();
					if(branchAddResult.isSuccess() || branchAddResult == AddRetVal.CANT_BE_THERE){//will leave the ones that won't fit.
						addedSuccessfully.add(obj);
					}
				}
				objects.removeAll(addedSuccessfully);
				dp("done adding parent's objects", ADD_TAG);
			}
		}
		
		ADD_METHOD_INDENT_LEVEL.increment();
		AddRetVal branchAddResult = branches.get(branchIndex((int)surrogate.getX(), (int)surrogate.getY(), divX, divY)).addAs(surrogate,actual);
		ADD_METHOD_INDENT_LEVEL.decrement();

		if(branchAddResult == AddRetVal.WONT_FIT){
			dp("keeping reference in parent",ADD_TAG);
			objects.add(actual);
			return AddRetVal.ALL_GOOD;
		}
		return branchAddResult;
	}
	
	public boolean canBeAt(int x, int y, T t){
		for (T obj : objects){
			if(t.clipsWith(obj)){
				return false;
			}
		}
		if (branches != null){
			if(willFitInABranch(this,t)){
				return branches.get(branchIndex(this, t)).canBeAt(x, y, t);
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	public boolean remove(T obj){
		if(obj == null){
			return false;
		}else if (objects.remove(obj)){
			return true;
		}else if (branches != null){ // && willFitInABranch(this,obj) ?
			return getBranch(this,obj).remove(obj);
		}else{
			System.out.println(CLASS_TAG + ": couldn'n find " + obj + ", so it wasn't removed.");
			return false;
		}
	}
	
	public List<T> getAt(int x, int y){
		dp(new Line() {public String l() {return
				QuadTreeNode.this.toString();}},GET_AT_TAG);
		ArrayList<T> ret = new ArrayList<>();
		for (T t : objects) {
			if (t.getBounds().contains(x,y)){
				ret.add(t);				
			}
		}
		if (branches != null){
			GET_AT_METHOD_INDENT.increment();
			ret.addAll(getBranch(this, x, y).getAt(x, y));
			GET_AT_METHOD_INDENT.decrement();
		}
		return ret;
	}
	
	public List<T> getClippingWith(Shape region){
		List<T> ret = new ArrayList<>();
		if(this.clipsWith(region)){
			for(T obj : objects){
				if (obj.clipsWith(region)){
					ret.add(obj);
				}
			}
		}
		if (branches != null){
			for(QuadTreeNode<T> branch : branches){
				ret.addAll(branch.getClippingWith(region));
			}
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return String.format("%s x=%f y=%f w=%f h=%f #o=%d b=%b\n  %s",
				this.getClass().getSimpleName(),getX(),getY(),getWidth(),getHeight(),objects.size(),branches,objects.toString());
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
		if (objects.contains(obj)){
			return this;
		}else{
			return branches.get(branchIndex(obj, divX, divY)).getContainer(obj);
		}
	}

	List<QuadTreeNode<T>> getBranches(){
		return Collections.unmodifiableList(branches);
	}
	
	public int getDivX() {
		return divX;
	}

	public int getDivY() {
		return divY;
	}
	
	public static final <Q extends Bounded> boolean willFit(QuadTreeNode<Q> node, Bounded test){
		return node.getBounds().contains(test.getBounds());
	}
	
	public static final <Q extends Bounded> boolean willFitInABranch(QuadTreeNode<Q> hasBranches, Q test){
		QuadTreeNode<Q> branch =  hasBranches.branches.get(0);
		return branch.getWidth() >= test.getWidth() && branch.getHeight() >= test.getHeight();
	}

	public static final <Q extends Bounded> QuadTreeNode<Q> getBranch(QuadTreeNode<Q> parent, Q test){
		return getBranch(parent, (int)test.getX(), (int)test.getY());
	}
	
	public static final <Q extends Bounded> QuadTreeNode<Q> getBranch(QuadTreeNode<Q> parent, int x, int y){
		return parent.getBranches().get(branchIndex(x, y, parent.getDivX(), parent.getDivY()));
	}
	
	public static final int branchIndex(QuadTreeNode<? extends Bounded> parent, Bounded obj){
		return branchIndex(obj, parent.getDivX(), parent.getDivY());
	}
	
	public static final int branchIndex(Bounded obj, int divX, int divY){
		return branchIndex((int)obj.getX(), (int) obj.getY(), divX, divY);
	}
	
	/**
	*<pre>Return the index of the branch which the point will fall in.
	*defines:   
	*           y
	*           ^-----------+
	*           |     |     |
	*           |  1  |  0  |
 	*      divY-+-----+-----|
 	*           |  3  |  2  |
 	*           |     |     |
 	*           o-----|-----+-> x
 	*               divX </pre>
	**/
	public static final int branchIndex(int x, int y, int divX, int divY){
		//the creation of the sub nodes should match this always.
		return (x >= divX ? 0 : 1) + (y >= divY ? 0 : 2);
	}

	public void removeAll() {
		init((int)getX(),(int)getY(),(int)getWidth(),(int)getHeight(),null);
	}
}
