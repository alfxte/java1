package ns.blank;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

class UnitStorage {
	/*package*/ static HashSet<String> sAllUnit;
	public static void prepare(String unitStorageDir) {
		File dir = new File(Utils.getFullPath(unitStorageDir));			
		sAllUnit = new HashSet<>(Arrays.asList(dir.list()));
	}
}
