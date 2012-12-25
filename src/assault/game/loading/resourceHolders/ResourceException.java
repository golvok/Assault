/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import java.io.File;
import java.io.IOException;
import org.jdom2.JDOMException;

/**
 *
 * @author matt
 */
public class ResourceException extends Exception {

	private String reasonString;
	public ResourceException(String location, Exception reason) {
		this(location, reason == null ? null : reason.toString());
	}
	
	public ResourceException(String location, JDOMException jdome, File baseFile) {
		this(location, "error while parsing "+baseFile+" Error:"+jdome);
	}
	
	public ResourceException(String location, IOException ioe, File baseFile) {
		this(location, "I/O error while parsing "+baseFile+" Error:"+ioe);
	}
	public ResourceException(String location, String reason){
		if (reason != null && location != null){
			reasonString = location +": "+ reason;
		}else if (location != null){
			reasonString = location;
		}else if (reason != null){
			reasonString = reason;
		}else{
			reasonString = "";
		}
	}
	public ResourceException(String reason){
		reasonString = reason;
	}
	public String getReason(){
		return reasonString;
	}

	@Override
	public String toString() {
		return reasonString;
	}
	
	
}
