/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assault.game.loading.resourceHolders;

import assault.game.loading.ResourcePreloader;
import java.io.File;

/**
 *
 * @author matt
 */
public abstract class ResourceHolder {

    public static <O extends ResourceHolder> O findResourceByName(String name, O[] list) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null && list[i].getName() != null && list[i].getName().equals(name)) {
                return list[i];
            }
        }
        return null;
    }

	private ResourcePreloader rp;
	private File baseFile;
	private String name;
	private boolean isvalid = false;


	
	public ResourceHolder(ResourcePreloader rp, String filePath) throws ResourceException {
		this(rp, new File("lib/ASSAULT_DATA", filePath));
		rp.printDebugLine("  " + "relative location = " + filePath);
	}
	
	public ResourceHolder(ResourcePreloader rp, String filePath, String name) throws ResourceException {
		this(rp, new File("lib/ASSAULT_DATA", filePath),name);
		rp.printDebugLine("  " + "relative location = " + filePath);
	}

	public ResourceHolder(ResourcePreloader rp, File filepath) throws ResourceException {
		this(rp, filepath, "");
		setName(createResourceName(filepath));
	}
	
	public ResourceHolder(ResourcePreloader rp, File filePath,String name) throws ResourceException {
		if (!checkAccess(filePath)) {
			throw new ResourceException("could not access location for resource (or it's null) @ " + filePath.getAbsolutePath());
		}
		/*if (location == null){
		throw new ResourceException("location was null");
		}else if (location.exists()){
		throw new ResourceException(location+" doesn't exist");
		}else if (location.canRead()){
		throw new ResourceException("do not have permission to read "+location);
		}*/
		if (rp == null) {
			//assumes that location is not null because it has allready been checked
			throw new ResourceException("ResourcePreloader was null when creating ResourceHolder for\n\t|->" + filePath);
		}
		this.rp = rp;
		this.name = name;
		rp.printDebugLine("           " + "location = " + filePath.getPath());
		this.baseFile = filePath;
	}
	private boolean loaded = false;//there are TO be used for load()
	private boolean loading = false;

	public abstract void load() throws ResourceException;
	// 
	// 
	private boolean loadedReferencial = false;//there are TO be used for loadReferencial()
	private boolean loadingReferencial = false;

	public abstract void loadReferencial() throws ResourceException;

	public static boolean checkAccess(File f) {
		return f != null
				&& f.exists()
				&& f.canRead();
	}
	
	/**
	 * returns the file name without the part after the last '.'
	 * and the dot itself. If the file name has no '.' in it,
	 * this returns the full file name, unaltered.
	 * 
	 * @param f
	 * @return 
	 */
	public static String getFileNameWithoutExtension(File f){
		int lastDotIndex = f.getName().lastIndexOf('.');
		if (lastDotIndex == -1){
			return f.getName();
		}else{
			return f.getName().substring(0,lastDotIndex);
		}
	}

	public abstract String createResourceName(File resourceFile);
	
	public static void loadResouces(ResourceHolder[] rs, ResourcePreloader rp) {
		for (int i = 0; i < rs.length; i++) {
			if (rs[i] != null) {
				try {
					rs[i].load();
				} catch (ResourceException ex) {
					rp.addError("Did not load " + rs[i].getName() + ": \n\t" + ex);
					rs[i].setValid(false);//it's false by deafult, buttttt...
					rs[i] = null;
				}
			}
		}
		for (int i = 0; i < rs.length; i++) {
			if (rs[i] != null) {
				try {
					rs[i].loadReferencial();
					rs[i].setValid(true);
				} catch (ResourceException ex) {
					rp.addError("Did not load " + rs[i].getName() + ": \n\t" + ex);
					rs[i].setValid(false);//it's false by deafult, buttttt...
					rs[i] = null;
				}
			}
		}
	}
	
	
	/**
	 * @return the ResourcePreloader
	 */
	public ResourcePreloader getRp() {
		return rp;
	}

	/**
	 * @return the baseFile
	 */
	public File getBaseFile() {
		return baseFile;
	}
	/**
	 * @return the folder that the baseFile is in
	 */
	public File getBaseFolder() {
		return baseFile.getParentFile();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public abstract String getQualifiedName();
	/**
	 * @return if (valid)
	 */
	public boolean isValid() {
		return isvalid;
	}

	/**
	 * @return if loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * @return if loading
	 */
	public boolean isLoading() {
		return loading;
	}

	/**
	 * @return if loadedReferencial
	 */
	public boolean isLoadedReferencial() {
		return loadedReferencial;
	}

	/**
	 * @return if loadingReferencial
	 */
	public boolean isLoadingReferencial() {
		return loadingReferencial;
	}

	/**
	 * @param set isvalid
	 */
	protected void setValid(boolean isvalid) {
		this.isvalid = isvalid;
	}

	/**
	 * @param set loaded
	 */
	protected void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	/**
	 * @param set loading
	 */
	protected void setLoading(boolean loading) {
		this.loading = loading;
	}

	/**
	 * @param set loadedReferencial
	 */
	protected void setLoadedReferencial(boolean loadedReferencial) {
		this.loadedReferencial = loadedReferencial;
	}

	/**
	 * @param set loadingReferencial
	 */
	protected void setLoadingReferencial(boolean loadingReferencial) {
		this.loadingReferencial = loadingReferencial;
	}

	/**
	 * @param name the name to set
	 */
	protected void setName(String name) {
		this.name = name;
	}

}
