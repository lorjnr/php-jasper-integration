package ch.bayo.lib.net;

import ch.bayo.lib.exception.*;
import java.io.*;

public abstract class SocketServer {

	private ServerEngine engine;
		
	public SocketServer()
	{
		this.engine = null;
	}
	
	public synchronized void start(int port, int maxClients) throws InvalidStateException, IOException
	{
		if (engine != null) {
			throw new InvalidStateException();
		}
		this.engine = new ServerEngine(this, port, maxClients);		
		this.engine.start();
	}
	
	public synchronized void stop() throws InvalidStateException
	{
		if (engine == null) {
			throw new InvalidStateException();
		}
		engine.stop();
		engine = null;
	}
	
	public abstract ClientContext createContext();
		
}
