package server;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class ServerConfig {
	private static int REGISTRY_PORT = 2964;
	
	private static int MTL_PORT = 2047;
	private static int LVL_PORT = 2048;
	private static int DDO_PORT = 2049;
	
	static Logger LOGGER = null;
	static FileHandler file = null;
	
	static enum S_courseRegistered{
		COMP6231, COMP6651
	}
	
	static enum S_location{
		mtl, lvl, ddo
	}
	
	static enum S_status{
		active, not_active
	}
	
	public static int getMTL_PORT() {
		return MTL_PORT;
	}

	public static int getLVL_PORT() {
		return LVL_PORT;
	}

	public static int getDDO_PORT() {
		return DDO_PORT;
	}

	public static int getRegistryPort() {
		return REGISTRY_PORT;
	}


	
}
