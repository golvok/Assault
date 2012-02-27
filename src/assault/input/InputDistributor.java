/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.input;

/**
 *
 * @author matt
 */
public interface InputDistributor {

	public abstract void accept(MouseEvent me);

	public abstract void accept(KeyboardEvent ke);
}
