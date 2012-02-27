/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading;

/**
 *
 * @author matt
 */
public class Texture {
    private final int texID;
    private final int height;
    private final int width;
    private final float top;
    private final float left;

    public Texture(int texID, int w, int h, int imgHeight, int imgWidth) {
        this.texID = texID;
        height = h;
        width = w;
        top = (float)imgHeight/(float)h;
        left = (float)imgWidth/(float)w;
    }

    public int getHeight() {
        return height;
    }

    public int getTexID() {
        return texID;
    }

    public int getWidth() {
        return width;
    }
    
    public float getTop(){
        return top;
    }

    public float getLeft() {
        return left;
    }
    
}
