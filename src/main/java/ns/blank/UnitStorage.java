package ns.blank;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

class UnitStorage {
	
	/*package*/ static HashSet<String> sMonophoneUnit;
	/*package*/ static HashSet<String> sDiphoneUnit;	
	
	public static void prepare(String basedir) {
		File monophoneDir = new File(Utils.getFullPath(basedir + "/monophone/"));
		File diphoneDir = new File(Utils.getFullPath(basedir + "/diphone/"));
		
		sMonophoneUnit = new HashSet<>(Arrays.asList(monophoneDir.list()));
		sDiphoneUnit = new HashSet<>(Arrays.asList(diphoneDir.list()));
	}
}
