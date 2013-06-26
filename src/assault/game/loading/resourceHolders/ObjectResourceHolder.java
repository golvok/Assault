/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jdom2.DataConversionException;
import org.jdom2.input.SAXBuilder;
import org.lwjgl.util.ReadableColor;

import assault.game.Player;
import assault.game.display.GameArea;
import assault.game.gameObjects.AObject;
import assault.game.loading.ResourcePreloader;
import assault.game.loading.Texture;
import assault.game.loading.TextureMaker;
import assault.util.Point;

/**
 * override parseXmlValues() & parseReferentialXmlValues NOT load() & loadReferencial() (not that you can) and call super method in first line
 * @author matt
 */
public class ObjectResourceHolder extends XmlResource {

	public static final String TYPE_NAME = "Object";
	protected ModResourceHolder mod;
	private float width;
	private float height;
	private int maxHealth;
	private BufferedImage miniIcon;
	private BufferedImage naturalImage;
	private File baseImageFile;

	public ObjectResourceHolder(ResourcePreloader rp, SAXBuilder sb, ModResourceHolder mod, File xmlFile) throws BadlyFormattedXMLException, ResourceException {
		super(rp, sb, xmlFile);
		this.mod = mod;
	}

	/**
	 * parses the Root element and baseFile for AObject values
	 * @throws BadlyFormattedXMLException
	 * @throws ResourceException 
	 */
	@Override
	protected void parseXmlValues() throws BadlyFormattedXMLException, ResourceException {
		try {
			getRp().setStatusString("\nloading Object values in " + getBaseFile());

			//simple values
			width = getRootE().getChild("dims").getAttribute("width").getFloatValue();
			height = getRootE().getChild("dims").getAttribute("height").getFloatValue();
			maxHealth = getRootE().getChild("health").getAttribute("max").getIntValue();

			//name
			String foundName = getRootE().getChildText("name");
			if (!(foundName).equals(getFileNameWithoutExtension(getBaseFile()))) {
				throw new BadlyFormattedXMLException(getQualifiedName(), "the name found in the file (" + foundName + ") differs from file name(" + getBaseFile().getName() + ").");
			}
			//setName(foundName); this is redundant

			//miniIcon
			File miniIconFile = new File(getBaseFile().getParent(), getRootE().getChild("miniIcon").getAttributeValue("src"));
			getRp().setStatusString("\tmini icon path = " + miniIconFile);
			if (checkAccess(miniIconFile)) {
				miniIcon = ImageIO.read(miniIconFile);
				getRp().setStatusString("\tO.K. good image");
			} else {
				getRp().addError("there is no image (or it's inaccessable) at " + miniIconFile);
				miniIcon = null;
			}

			//natural image
			baseImageFile = new File(getBaseFile().getParent(), getRootE().getChild("img").getChild("base").getAttributeValue("src"));
			getRp().setStatusString("\tnatural image path = " + baseImageFile);
			if (checkAccess(baseImageFile)) {
				naturalImage = ImageIO.read(baseImageFile);
				getRp().setStatusString("\tO.K. good image");
			} else {
				getRp().addError("there is no image (or it's inaccessable) at " + baseImageFile);
				naturalImage = null;
			}

			getRp().setStatusString("loaded object values in " + getBaseFile());

		} catch (DataConversionException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName(), ex, getBaseFile());
		} catch (NullPointerException ex) {
			throw new BadlyFormattedXMLException(getQualifiedName(), ex, getBaseFile(), getRootE());
//		} 
//		catch (JDOMException ex) {
//			throw new ResourceException(getQualifiedName(), ex, getBaseFile());
		} catch (IOException ex) {
			throw new ResourceException(getQualifiedName(),
					"I/O error during parsing. (src: " + getBaseFile() + "):\n" + ex.toString());
		}
	}

	@Override
	public synchronized void parseReferencialXmlValues() throws ResourceException {
	}

	public AObject createObject(GameArea g, float x, float y, Player owner) throws ResourceException {
		return new AObject(g, x, y, this, owner);
	}

	public AObject createObject(GameArea g, Point p, Player owner) throws ResourceException {
		return createObject(g, p.getX(), p.getY(), owner);
	}

	@Override
	public String getQualifiedName() {
		String out = "";
		if (mod == null || mod.getName() == null) {
			out += "???.";
		} else {
			out += mod.getName() + ".";
		}
		if (getName() == null) {
			if (getBaseFile() != null) {
				out += getBaseFile().toString();
			} else {
				out += "???";
			}
		} else {
			out += getName();
		}
		return out;
	}

	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public BufferedImage getMiniIcon() {
		return miniIcon;
	}

	public BufferedImage getBaseImage(Player player) {
		return filterImageForPlayerColour(naturalImage, player);
	}

	public Texture getBaseImageTexture(Player player) {
		return TextureMaker.getTexture(getBaseImage(player));
	}
	private static Map<BufferedImage, Map<Player, BufferedImage>> allreadyFiltered = Collections.synchronizedMap(new HashMap<BufferedImage, Map<Player, BufferedImage>>(40));

	/**
	 * replaces the transparent black pixels in <code>img</code> with
	 * <code>player.getColour()</code>. filtered images
	 * are stored in a <code>Map</code> for reuse if the same image and
	 * player are requested again.
	 * 
	 * @param img image to filter
	 * @param player who's colour or pre-filtered images to use
	 * @return the filtered image
	 */
	private static BufferedImage filterImageForPlayerColour(BufferedImage img, final Player player) {

		//check if and retieve allready filtered images
		Map<Player, BufferedImage> ApToBMap = allreadyFiltered.get(img);
		if (ApToBMap != null) {
			BufferedImage biInMap = ApToBMap.get(player);
			if (biInMap != null) {
				//rp.printDebugLine("allready filtered");
				return biInMap;
			}
		}
		//rp.printDebugLine("needs filtering");

		//the colour to replace
		final Color replC = new Color(0x00, 0x00, 0xfe, 0xff);//almost blue

		//replace the pixels
		BufferedImage out = new LookupOp(new LookupTable(0, 4) {

			@Override
			public int[] lookupPixel(int[] src, int[] dest) {
				if (src.length == 4) {
					if (src[0] == replC.getRed() && src[1] == replC.getGreen() && src[2] == replC.getBlue() && src[3] == replC.getAlpha()) {
						if (dest == null) {
							dest = new int[4];
						}
						ReadableColor c = player.getColour();
						dest[0] = c.getRed();
						dest[1] = c.getGreen();
						dest[2] = c.getBlue();
						dest[3] = c.getAlpha();
						//rp.printDebugLine("changed a Pixel");
						return dest;
					}// else {
					//return src;
					//}
				}// else {
				//rp.printDebugLine("wrong length l = " + src.length);
				return src;
				//}
			}
		}, null).filter(img, null);

		//add the filtered image to the map (uses the same <APlayer, BufferedImage> Map retrieved before)
		if (ApToBMap == null) {
			//the case where the <APlayer, BufferedImage> map needs to be added
			ApToBMap = Collections.synchronizedMap(new HashMap<Player, BufferedImage>(8));
			allreadyFiltered.put(img, ApToBMap);
		}
		ApToBMap.put(player, out);

		return out;
	}

	public String getTypeName() {
		return TYPE_NAME;
	}

	@Override
	public String createResourceName(File resourceFile) {
		return getFileNameWithoutExtension(resourceFile);
	}
}
