package assault.game.display;

import assault.display.AContainer;
import assault.display.APaintable;
import assault.display.AssaultWindow;
import assault.input.*;

public class TopGameContainer extends AContainer implements InputDistributor {

	private AssaultWindow window;
	private CommandDispatchMenu cdm;
	private GameArea gameArea;
	private StatusDisplayMenu sDisplayMenu;

	public TopGameContainer(int x, int y, int w, int h, AssaultWindow window) {
		super(x, y, w, h, 4);
		this.window = window;
	}

	public AssaultWindow getAW() {
		return window;
	}

	public void addChild(CommandDispatchMenu ncdm) {
		super.addChild(ncdm);
		cdm = ncdm;
	}

	public void addChild(GameArea ga) {
		super.addChild(ga);
		gameArea = ga;
	}

	public void addChild(StatusDisplayMenu sdm) {
		super.addChild(sdm);
		sDisplayMenu = sdm;
	}

	public static TopGameContainer getTGC(APaintable ap) {
		AContainer ac = null;
		if (ap != null) {
			for (ac = ap.getParent(); !(ac instanceof TopGameContainer) && ac != null; ac = ac.getParent()) {
			}
		}
		return (TopGameContainer) ac;
	}

	@Override
	public void accept(MouseEvent me) {

		MouseListener ml;
		if (me.intersects(cdm.getBounds())) {
			ml = cdm;
		} else if (me.intersects(sDisplayMenu.getBounds())) {
			ml = sDisplayMenu;
		} else {
			ml = gameArea;
		}
		InputEventUtil.passMouseEventTo(ml, me);
	}

	@Override
	public void accept(KeyboardEvent ke) {
		if (ke.isLControlDown() && ke.getButton() == 'q'){
			getAW().enterMainMenu();
		}
	}
}
