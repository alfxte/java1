package ns.blank;

import java.nio.file.Paths;

class Utils {
	public static String getFullPath(String relativePath) {
		return Paths.get(relativePath).toAbsolutePath().normalize().toString();			
	}
}
