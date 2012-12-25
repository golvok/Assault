/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.util;

/**
 *
 * @author Matt
 */
public class OSUtil {

    private static final String os = System.getProperty("os.name").toLowerCase();
    private static final boolean isUnix = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0;
    private static final boolean isWindows = os.indexOf("win") >= 0;
    private static final boolean isMac = os.indexOf("mac") >= 0;
    private static final boolean isSolaris = os.indexOf("sunos") >= 0;
    public static final String lwjglVersion = "2.8.5";
    public static final String LIB_FOLDER_NAME = "Assault_lib";
    
    public static boolean isMac() {
        return isMac;
    }

    public static boolean isUnix() {
        return isUnix;
    }

    public static boolean isWindows() {
        return isWindows;
    }

    public static boolean isSolaris() {
        return isSolaris;
    }

    public static String getOs() {
        return os;
    }

    public static void setLibrariesPath() {
        if (isMac()){
            System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir")+"/" + LIB_FOLDER_NAME + "/lwjgl-"+lwjglVersion+"/native/macosx");
        }else if (isUnix()){
            System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir")+"/" + LIB_FOLDER_NAME + "/lwjgl-"+lwjglVersion+"/native/linux");
        }else if (isWindows()){
            System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir")+"\\" + LIB_FOLDER_NAME + "\\lwjgl-"+lwjglVersion+"\\native\\windows");
        }else if (isSolaris()){
            System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir")+"/" + LIB_FOLDER_NAME + "/lwjgl-"+lwjglVersion+"/native/solaris");
        }else{
            //try linux?
            System.out.println("unrecognised OS. Trying Linux lwjgl libraries");
            System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir")+"/" + LIB_FOLDER_NAME + "/lwjgl-"+lwjglVersion+"/native/linux");
        }

    }

}
