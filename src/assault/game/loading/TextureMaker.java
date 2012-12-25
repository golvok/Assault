/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading;


import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A utility class to load textures for OpenGL. This source is based
 * on a textureLoader that can be found in the lwjgl wiki
 *
 * OpenGL uses a particular image format. Since the images that are
 * loaded from disk may not match this format this loader introduces
 * a intermediate image which the source image is copied into. In turn,
 * this image is used as source for the OpenGL texture.
 *
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class TextureMaker {

    private static Map<BufferedImage, Texture> allreadyConverted = Collections.synchronizedMap(new HashMap<BufferedImage, Texture>(40));
    private static Map<String, Texture> allreadyConvertedS = Collections.synchronizedMap(new HashMap<String, Texture>(40));
    /** The colour model including alpha for the GL image */
    private static ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[]{8, 8, 8, 8},
            true,
            false,
            ComponentColorModel.TRANSLUCENT,
            DataBuffer.TYPE_BYTE);
    /** The colour model for the GL image */
    private static ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[]{8, 8, 8, 0},
            false,
            false,
            ComponentColorModel.OPAQUE,
            DataBuffer.TYPE_BYTE);

    public static Texture getTexture(String path) throws IOException {
        Texture tex = allreadyConvertedS.get(path);
        if (tex == null) {
            org.newdawn.slick.opengl.Texture GLtex = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(path));
            tex = new Texture(GLtex.getTextureID(), GLtex.getTextureWidth(), GLtex.getTextureHeight(),GLtex.getImageHeight(),GLtex.getImageWidth());
            allreadyConvertedS.put(path, tex);
        }
        return tex;
    }

    public static Texture getTexture(BufferedImage bi) {
        Texture texID = allreadyConverted.get(bi);
        if (texID == null) {
			//to print the stack trace
			/*try {
				throw new NullPointerException();
			} catch (NullPointerException n){
				n.printStackTrace();
			}*/
            texID = convertToTexture(bi,
                    GL_TEXTURE_2D, // target
                    GL_RGBA, // dst pixel format
                    GL_LINEAR, // min filter (unused)
                    GL_LINEAR);
			
            allreadyConverted.put(bi, texID);
        }
        return texID;
    }

    private static Texture convertToTexture(BufferedImage bi,
            int target,
            int dstPixelFormat,
            int minFilter,
            int magFilter) {
        int srcPixelFormat;

        // create the texture ID for this texture
		int textureID = glGenTextures();
        int texWidth = get2Fold(bi.getWidth());
        int texHeight = get2Fold(bi.getHeight());

        // bind this texture
        glBindTexture(target, textureID);

        if (bi.getColorModel().hasAlpha()) {
            srcPixelFormat = GL_RGBA;
        } else {
            srcPixelFormat = GL_RGB;
        }

        // convert that image into a byte buffer of texture data
        ByteBuffer textureBuffer = convertImageData(bi);

        if (target == GL_TEXTURE_2D) {
            glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
            glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
        }

        // produce a texture from the byte buffer
        glTexImage2D(target,
                0,
                dstPixelFormat,
                texWidth,
                texHeight,
                0,
                srcPixelFormat,
                GL_UNSIGNED_BYTE,
                textureBuffer);

        return new Texture(textureID, texWidth, texHeight,bi.getHeight(), bi.getWidth());
    }

    /**
     * Get the closest greater power of 2 to the fold number
     *
     * @param fold The target number
     * @return The power of 2
     */
    private static int get2Fold(int fold) {
        int ret = 2;
        while (ret < fold) {
            ret *= 2;
        }
        return ret;
    }

    /**
     * Convert the buffered image to a texture
     *
     * @param bufferedImage The image to convert to a texture
     * @param texture The texture to store the data into
     * @return A buffer containing the data
     */
    @SuppressWarnings("rawtypes")
	private static ByteBuffer convertImageData(BufferedImage bufferedImage) {
        ByteBuffer imageBuffer;
        WritableRaster raster;
        BufferedImage texImage;

        int texWidth = 2;
        int texHeight = 2;

        // find the closest power of 2 for the width and height
        // of the produced texture
        while (texWidth < bufferedImage.getWidth()) {
            texWidth *= 2;
        }
        while (texHeight < bufferedImage.getHeight()) {
            texHeight *= 2;
        }

        // create a raster that can be used by OpenGL as a source
        // for a texture
        if (bufferedImage.getColorModel().hasAlpha()) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
            texImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable());
        } else {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
            texImage = new BufferedImage(glColorModel, raster, false, new Hashtable());
        }

        // copy the source image into the produced image
        Graphics g = texImage.getGraphics();
        g.setColor(new Color(0f, 0f, 0f, 0f));
        g.fillRect(0, 0, texWidth, texHeight);
        g.drawImage(bufferedImage, 0, 0, null);

        // build a byte buffer from the temporary image
        // that be used by OpenGL to produce a texture.
        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

        imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.flip();

        return imageBuffer;
    }
}
