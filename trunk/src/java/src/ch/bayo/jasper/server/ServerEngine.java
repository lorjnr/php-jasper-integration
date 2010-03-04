package ch.bayo.jasper.server;

import ch.bayo.lib.net.ClientContext;
import ch.bayo.lib.net.SocketServer;

class ServerEngine extends SocketServer {
	
	public ClientContext createContext() {
		
		return new Client();
		
	}
	
}
