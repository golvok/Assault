/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.input;

import assault.display.Bounded;
import assault.display.Paintable;

/**
 *
 * @author matt
 */
public interface InputRegistar extends InputDistributor{
	
	public void    addMouseEventReciever(Paintable il);
	public void removeMouseEventReciever(Bounded ml);
	
	public void    addKeyboardEventReciever(Paintable il);
	public void removeKeyboardEventReciever(Bounded kl);
}
