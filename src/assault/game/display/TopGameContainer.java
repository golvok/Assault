package assault.game.display;

import assault.display.Container;
import assault.display.Paintable;
import assault.display.AssaultWindow;
import assault.input.*;

public class TopGameContainer extends Container implements InputDistributor {

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

	public static TopGameContainer getTGC(Paintable ap) {
		Container ac = null;
		if (ap != null) {
			for (ac = ap.getParent(); !(ac instanceof TopGameContainer) && ac != null; ac = ac.getParent()) {
			}
		}
		return (TopGameContainer) ac;
	}

	@Override
	public void accept(MouseEvent me) {

		Paintable ap;
		if (me.intersects(cdm.getBounds())) {
			ap = cdm;
		} else if (me.intersects(sDisplayMenu.getBounds())) {
			ap = sDisplayMenu;
		} else {
			ap = gameArea;
		}
		InputEventUtil.passAndTranslateMouseEventTo(ap, me);
	}

	@Override
	public void accept(KeyboardEvent ke) {
		if (ke.isLAltDown() && ke.getButton() == 'q'){
			getAW().enterMainMenu();
		}
		cdm.accept(ke);
	}
}
