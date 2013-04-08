/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import java.io.File;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

/**
 *
 * @author matt
 */
public class BadlyFormattedXMLException extends ResourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2293004544894038123L;
	
	public BadlyFormattedXMLException(String location, DataConversionException dce, File baseFile){
		super(location, "bad data in " + baseFile + " (something cant be converted from a string properly) Line: " + dce.getStackTrace()[0]);
	}
	public BadlyFormattedXMLException(String location, NullPointerException npe, File baseFile, Element rootElement){
		super(location, ((rootElement == null) ? ("Root element in " + baseFile + " was null") : (baseFile + " is improperly formated (something's missing or named wrong)")) + "\n\tLine: " + npe.getStackTrace()[0]);
	}
	public BadlyFormattedXMLException(String location, String reason){
		super(location, reason);
	}
	public BadlyFormattedXMLException(String reason){
		super(reason);
	}
	
}
