/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.display;

import assault.game.display.TopGameContainer;
import assault.game.loading.TextureMaker;
import assault.input.KeyboardEvent;
import assault.input.KeyboardListener;
import assault.input.MouseEvent;
import assault.input.MouseListener;
import java.awt.image.BufferedImage;
import org.lwjgl.util.Color;

/**
 *
 * @author matt
 */
public abstract class Button extends Paintable implements MouseListener, KeyboardListener {

    private String name;
    private char hotkey;
    private BufferedImage icon;
    private Runnable action;
    private boolean enabled = true;

    public Button(String name, int x, int y, int width, int height, char shortCut, BufferedImage icon, Runnable action) {
        super(x, y, width, height);
        this.name = name;
        setAction(action);
        setIcon(icon);
        setHotkey(shortCut);
    }

    @Override
    public void drawSelf() {
        drawRect(0, 0, getWidth(), getHeight());
        drawTexture(0, 0, getWidth(), getHeight(), TextureMaker.getTexture(getIcon()));
        if (!isEnabled()) {
            setColour(new Color(153, 153, 153, 153));//a nice grey
            fillRect(0, 0, getWidth(), getHeight());
        }
        //drawString(acmd.getName(), 0, 0);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        press();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        release();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyPressed(KeyboardEvent ke) {
        if (ke.getButton() == getHotkey()) {
            press();
            release();
        }
    }

    public AssaultWindow getAW() {
        try {
            return TopGameContainer.getTGC(this).getAW();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public Runnable getAction() {
        return action;
    }

    public BufferedImage getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public char getHotkey() {
        return hotkey;
    }

    protected void setHotkey(char c) {
        hotkey = c;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void press() {
    }

    public void release() {
        if (isEnabled()) {
            runAction();
        }
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void setEnabled(boolean bln) {
        enabled = bln;
    }

    protected void runAction() {
        new Thread(getAction()).start();
    }

    protected void setIcon(BufferedImage i) {
        icon = i;
    }

    @Override
    public String toString() {
        return super.toString() + " hotkey = " + getHotkey() + " enabled = " + isEnabled();
    }
}
