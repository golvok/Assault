/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assault.game.gameObjects;

import assault.game.Player;
import assault.game.display.GameArea;
import assault.util.Point;
import java.awt.Image;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author matt
 */
public class Projectile extends AObject{
	private Point eP = new Point();
	private int speed = 3;
	private float dX = 0;//difference X, Y
	private float dY = 0;
	private float mX = 0;//Move X, Y
	private float mY = 0;
	private float X = 0;//where this should be if java used float percision. See Move()
	private float Y = 0;
	private boolean isShot = false;
	private Timer moverTimer = new Timer();
	public Projectile(GameArea g,float sX, float sY, float eX, float eY, int size, Image miniIcon, Image naturalImage, Player ownerPlayer){
		super(g,sX,sY,size,size,miniIcon,naturalImage,ownerPlayer);
		X = sX;
		Y = sY;
		doNotPaintCross();
		doNotShowStatus();
		eP.x = eX;
		eP.y = eY;
		dX = (float)(eX-sX);
		dY = (float)(eY-sY);
		double tan = 0;
		double angle = 0;
		if(dX == 0){
			mY = speed*Math.abs(dY)/dY;
		}else if(dY == 0){
			mX = speed*Math.abs(dX)/dX;
		}else{
			int neg = 1;
			if ((dX < 0 && dY>0)||(dX > 0 && dY < 0)){
				tan = (((float)(eX-sX)/(float)(eY-sY)));
				neg = -1;
			}else if((dX != 0 || dY != 0)){
				tan = (((float)(eY-sY)/(float)(eX-sX)));
			}
			angle = Math.atan(tan);
			mX = (float)((Math.cos(angle)*speed)*(float)neg*Math.abs(dX)/dX);
			mY = (float)((Math.sin(angle)*speed) *Math.abs(dY)/dY);
			if (dX>0||dY>0){
				float temp = mX;
				mX = mY;
				mY = temp;
			}
		}
		// <editor-fold defaultstate="collapsed" desc="useful debugging output">
		//System.out.println(sX + ", " + sY + " s_");
		//System.out.println(eX +", "+eY+" e_");
		//System.out.println(tan+" t");
		//System.out.println(angle*180/Math.PI+" a");//converted to degrees
		//System.out.println(mX+", "+mY+" m_");// </editor-fold>
		//System.out.println("AP_INIT");
	}
	/**
	 * shoots this projectile unless it has already been shot
	 * @return <code>true</code> if method executed successfully
	 *	    <p>
	 *	    <code>false</code> if projectile is already shot
	 */
	public boolean shoot(){
		if(!isShot){
			isShot = true;
			moverTimer.scheduleAtFixedRate(new TimerTask() {
//				public boolean running = true;
				@Override
				public void run() {
					move();
				}
			}, 100, 100);
			return true;
		}else{
			return false;
		}
	}
	@Override
	public void drawSelf(){
		//g = (Graphics2D)g;
		super.drawSelf();
		drawLine(getWidth()/2, getHeight()/2, (int)dX, (int)dY);
		drawLine(getWidth()/2, getHeight()/2, -(int)dX, -(int)dY);
		//System.out.println("BU_PAINT");
	}
	private void move(){
		X += mX;
		Y += mY;
		GameArea ga = getGA();
		if(X<0||X>ga.getWidth()||Y<0||Y>ga.getHeight()){
		    dispose();
		    return;
		}
		setLocation((int)Math.round(X), (int)Math.round(Y));
		//System.out.println("AP_MOVE");
	}
	/**
	 * will remove this from field and take care of any duties to remove it completely.
	 */
    @Override
    public void dispose(){
		moverTimer.cancel();
		super.dispose();
		//System.out.println("AP_DISPOSE");
    }
}