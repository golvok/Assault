package assault.util;

import java.io.File;
import java.net.URI;

public class DataFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataFile(String pathname) {
		super(modifyPath(null,pathname).getPath());
	}

	public DataFile(URI uri) {
		super(modifyPath(null,new File(uri).getPath()).getPath());
	}

	public DataFile(String parent, String child) {
		super(modifyPath(new File(parent),child).getPath());
	}

	public DataFile(File parent, String child) {
		super(modifyPath(parent,child).getPath());
	}
	
	private static File modifyPath(File parent, String child){
		File f;
		if(parent != null){
			f = new File(parent,child);
		}else{
			f = new File(child);
		}
		if (f.getPath().startsWith("ASSAULT_DATA")){
			return new File(OSUtil.LIB_FOLDER_NAME,f.getPath());
		}else{
			return f;
		}
	}

}
