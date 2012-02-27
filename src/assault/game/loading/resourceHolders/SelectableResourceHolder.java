/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import assault.game.loading.ResourcePreloader;
import java.io.File;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author matt
 */
public class SelectableResourceHolder extends ObjectResourceHolder{
	public SelectableResourceHolder(ResourcePreloader rp, SAXBuilder sb, ModResourceHolder mod, File xmlFile) throws BadlyFormattedXMLException, ResourceException {
        super(rp, sb, mod, xmlFile);
    }
}
