package server;

public class Server_Configuration {
	private static int REGISTRY_PORT = 2964;
	
	private static int MTL_PORT = 1001;
	private static int LVL_PORT = 1002;
	private static int DDO_PORT = 1003;
	
	static enum courseRegistered{
		COMP6231, COMP6651
	}
	
	static enum location{
		mtl, lvl, ddo
	}
	
	static enum status{
		active, not_active
	}
}
