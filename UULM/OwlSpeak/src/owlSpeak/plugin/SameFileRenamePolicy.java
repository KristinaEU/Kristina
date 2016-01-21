

package owlSpeak.plugin;

import java.io.File;

import com.oreilly.servlet.multipart.FileRenamePolicy;


public class SameFileRenamePolicy implements FileRenamePolicy {
	  
	public SameFileRenamePolicy() {
		super();
	}
	
	public File rename (File f) { 
		String newName = "file.wav";
		f = new File(f.getParent(), newName);		
	  return f;
	  }
	}


