/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import assault.game.loading.Texture;
import assault.game.loading.TextureMaker;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jdom.DataConversionException;
import org.jdom.Element;

/**
 *
 * @author matt
 */
public class GroundCover {

	BufferedImage image;
	Texture texture;
	private int rangeMin;
	private int rangeMax;

	public GroundCover(TerrainResource td, Element baseElement) throws ResourceException, BadlyFormattedXMLException, IOException, DataConversionException {
		//texture
		File baseImageFile = new File(td.getBaseFile().getParentFile(), baseElement.getAttributeValue("src"));
		td.getRp().setStatusString("\tgroundcover image path = " + baseImageFile);
		if (ResourceHolder.checkAccess(baseImageFile)) {
			image = ImageIO.read(baseImageFile);
			td.getRp().setStatusString("\tO.K. good image");
			texture = TextureMaker.getTexture(image);
		} else {
			td.getRp().addError("there is no image (or it's inaccessable) at " + baseImageFile);
			image = null;
			texture = new Texture(0, 1, 1, 0, 0);//a blank texture
		}
		//range
		rangeMin = baseElement.getAttribute("rangeMin").getIntValue();
		rangeMax = baseElement.getAttribute("rangeMax").getIntValue();
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getRangeMax() {
		return rangeMax;
	}

	public int getRangeMin() {
		return rangeMin;
	}

	public Texture getTexture() {
		return texture;
	}
	
}
