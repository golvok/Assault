/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import assault.game.display.CommandButton;
import assault.game.loading.ResourcePreloader;
import assault.game.util.commands.Command;
import assault.game.util.commands.CreateCmd;
import assault.game.util.commands.MoveCmd;
import java.io.File;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author matt
 */
public class ControllableResourceHolder extends SelectableResourceHolder {

	private CommandButton[] cmdBtns;

	public ControllableResourceHolder(ResourcePreloader rp, SAXBuilder sb, ModResourceHolder mod, File xmlFile) throws BadlyFormattedXMLException, ResourceException {
		super(rp, sb, mod, xmlFile);
	}

	@Override
	public synchronized void parseReferencialXmlValues() throws ResourceException {
		parseXmlButtons();
	}

	public CommandButton[] getCmdBtns() {
		CommandButton[] temp = new CommandButton[cmdBtns.length];
		System.arraycopy(cmdBtns, 0, temp, 0, cmdBtns.length);
		return temp;
	}
	@SuppressWarnings("unused")
	protected CommandButton parseXmlButton(Element xmlBtn) throws BadlyFormattedXMLException {
		String cmdType;
		Element cmdE;
		Command cmd = null;
		UnitResourceHolder unit = null;
		//TODO add functionality for complex commands
		try {
			cmdE = xmlBtn.getChild("action").getChild("command");
			cmdType = cmdE.getAttributeValue("type");
			if (cmdType.equals("Move")) {
				cmd = new MoveCmd(getRp());
			} else if (cmdType.equals("Create")) {
				Element unitTypeE = cmdE.getChild("unitType");
				char hotkey = cmdE.getChildText("hotKey").charAt(0);
				String modName = unitTypeE.getAttributeValue("mod");
				String unitName = unitTypeE.getAttributeValue("name");
				try {
					unit = getRp().getModUnitByName(modName, unitName);
				} catch (ResourceException ex) {
					//getModUnit allways throws an exception on failure
					throw new BadlyFormattedXMLException(getQualifiedName(), "a button element references something wrong. Message:\n" + ex.getReason() + " Can't create Create Button");
				}
				cmd = new CreateCmd(cmdE.getChildText("name"), hotkey, unit);
			} else {
				throw new BadlyFormattedXMLException("unrecognised command : " + cmdType);
			}
		} catch (NullPointerException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName(), "a button element is improperly formated (something's missing or named wrong)" + (unit != null ? " or there is something wrong (missing?) in " + unit.getQualifiedName() : "") + " Message: " + ex.getStackTrace()[0]);
		}
		
		if (cmd != null) {
			return new CommandButton(cmd);
		} else {
			return null;//apparetly this is dead code. not sure how. added suppression
		}
	}

	private void parseXmlButtons() throws BadlyFormattedXMLException {
		try {
			Element[] xmlBtns = {};
			xmlBtns = (Element[]) getRootE().getChild("buttons").getChildren("button").toArray(xmlBtns);
			cmdBtns = new CommandButton[xmlBtns.length];
			for (int i = 0; i < xmlBtns.length; i++) {
				try {
					cmdBtns[i] = parseXmlButton(xmlBtns[i]);
				} catch (BadlyFormattedXMLException e) {
					getRp().addError(e);
				}
			}
		} catch (NullPointerException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName(), ((getRootE() == null) ? ("Root element in " + getBaseFile() + " was null") : (getBaseFile() + " is improperly formated (something's missing or named wrong)")) + "\n\tMessage: " + ex.getStackTrace()[0]);
		}
	}
}
