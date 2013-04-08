/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.input;

/**
 *
 * @author matt
 */
public interface InputRegistar extends InputDistributor{
	
	public void    addMouseEventReciever(MouseListener il);
	public void removeMouseEventReciever(MouseListener ml);
	
	public void    addKeyboardEventReciever(KeyboardListener il);
	public void removeKeyboardEventReciever(KeyboardListener kl);
}
