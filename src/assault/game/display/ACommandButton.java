/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.display;

import assault.game.util.commands.ACommand;

/**
 *
 * @author matt
 */
public class ACommandButton extends CDMButton {

	private ACommand cmd;
	private ACommandButton btnRef = this;

	public ACommandButton(ACommand acmd) {
		super(acmd.getName(), acmd.getShortCut(), acmd.getIcon());
		cmd = acmd;
		setEnabled(acmd.isValidCommand());
		setAction(new Runnable() {
					private CommandDispatchMenu acdm;

					@Override
					public void run() {
						//TODO is there a better way to do commands from buttons?
						if (cmd.isValidCommand()) {
							try {
								btnRef.setEnabled(false);
								acdm = getAW().getACDM();
								acdm.disableAllExceptEsc();
								getAW().getGA().executeCommandOnSelectionBySelection(cmd);
								//System.out.println("Next mousePress was acknowledged");
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							acdm.enableAll();
						}
					}
				});
	}

	@Override
	public void setEnabled(boolean bln) {
		if (cmd.isValidCommand() || !bln) {
			super.setEnabled(bln);
		}
	}

	public ACommand getCmd() {
		return cmd;
	}

}
