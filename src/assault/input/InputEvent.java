/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.input;

/**
 *
 * @author matt
 */
public abstract class InputEvent {
    
    public static final int LSHIFT_DOWN_MASK = 1;
    public static final int LCTRL_DOWN_MASK = 2;
    public static final int LMETA_DOWN_MASK = 4;
    public static final int LALT_DOWN_MASK = 8;
    
    private int modifiers;

    public InputEvent(int modifiers) {
        this.modifiers = modifiers;
    }

    public int getModifiers() {
        return modifiers;
    }
    
    public boolean isLShiftDown() {
        return (modifiers & LSHIFT_DOWN_MASK) == LSHIFT_DOWN_MASK;
    }
    
    public boolean isLControlDown() {
        return (modifiers & LCTRL_DOWN_MASK) == LCTRL_DOWN_MASK;
    }
    
    public boolean isLAltDown() {
        return (modifiers & LALT_DOWN_MASK) == LALT_DOWN_MASK;
    }
}
