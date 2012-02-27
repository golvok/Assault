/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.input;

import assault.display.APaintable;

/**
 *
 * @author matt
 */
public interface InputRegistar extends InputDistributor{
	
	public void    addMouseEventReciever(APaintable il);
	public void removeMouseEventReciever(APaintable ml);
	
	public void    addKeyboardEventReciever(APaintable il);
	public void removeKeyboardEventReciever(APaintable kl);
}
