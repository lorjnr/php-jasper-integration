package ch.bayo.lib.net;

import java.io.IOException;
import java.net.*;

class AcceptThread extends Thread {

	private ServerEngine engine;
	private ServerSocket srvSock;
	
	private boolean terminate;

	public AcceptThread(ServerEngine engine)
	{
		super();
		
		this.engine = engine;
		this.srvSock = this.engine.getServerSocket();
		
		this.terminate = false;
	}

	public void run()
	{
		while ( ! terminate)
		{
			Socket s = null;
			try {
				s = srvSock.accept();
			} catch (SocketException e) {
				//do not handle
			} catch (IOException e) {
				e.printStackTrace();
			}

			if ( terminate ) break;				

			if ( s != null ) {
				engine.newSocket(s);
			}
			
		}	
	}
	
	public void wantTerminate()
	{
		terminate = true;
	}

}