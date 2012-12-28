/**
 * 
 */
package assault.util;

/**
 * @author matt
 *
 */
public class Ptr<T> {

	private T var;
	
	public Ptr(T value) {
		setVal(value);
	}

	public void setVal(T value) {
		var = value;
	}
	
	public T getVal(){
		return var;
	}

}
