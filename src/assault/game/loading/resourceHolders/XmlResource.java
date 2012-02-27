/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import assault.game.loading.ResourcePreloader;
import java.io.File;
import java.io.IOException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author matt
 */
public abstract class XmlResource extends ResourceHolder{

	private  SAXBuilder builder;
	private Document xml;
	private Element rootE;
	
	public XmlResource(ResourcePreloader rp, SAXBuilder sb, File filePath, String name) throws ResourceException {
		super(rp, filePath, name);
		builder = sb;
	}
	
	public XmlResource(ResourcePreloader rp, SAXBuilder sb, File filePath) throws ResourceException {
		super(rp, filePath);
		builder = sb;
	}
	
	

	@Override
	public final synchronized void load() throws ResourceException {
		try {
			if (!isLoaded()) {
			setLoading(true);
			xml = getBuilder().build(getBaseFile());
			rootE = getXml().getRootElement();
			parseXmlValues();
			setLoading(false);
			setLoaded(true);
		}
		} catch (JDOMException ex) {
			throw new ResourceException(getName(),
					"problem bulding XML. (src: " + getBaseFile() + "):\n " + ex.toString());
		} catch (IOException ex) {
			throw new ResourceException(getName(),
					"I/O error during build. (src: " + getBaseFile() + "):\n" + ex.toString());
		}
	}

	@Override
	public final synchronized void loadReferencial() throws ResourceException {
		if (!isLoadedReferencial() && isLoaded()) {
			setLoadingReferencial(true);
			parseReferencialXmlValues();
			setLoadingReferencial(false);
			setLoadedReferencial(true);
		}
	}
	
	/**
	 * parse the XML for simple values (numbers, strings, etc.)
	 * that aren't to be used to reference other resources
	 * 
	 * @throws BadlyFormattedXMLException
	 * @throws ResourceException 
	 */
	protected abstract void parseXmlValues() throws BadlyFormattedXMLException, ResourceException;
	
	/**
	 * parse the XML for referential values (that will possibly
	 * need some information from/references to other resources, 
	 * create buttons are an example)
	 * 
	 * @throws BadlyFormattedXMLException
	 * @throws ResourceException 
	 */
	protected abstract void parseReferencialXmlValues() throws BadlyFormattedXMLException, ResourceException;
	
	/**
	 * @return the builder
	 */
	public SAXBuilder getBuilder() {
		return builder;
	}

	/**
	 * @return the xml
	 */
	public Document getXml() {
		return xml;
	}

	/**
	 * @return the rootE
	 */
	public Element getRootE() {
		return rootE;
	}

	
}
