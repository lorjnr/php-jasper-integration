package ch.bayo.lib.net;

import java.net.*;

class ClientObj {
	
	private ServerEngine engine;
	
	private Socket sock;
	private ClientThread thread;
	
	private ClientContext clientCtx;
	private SocketWrapper sockWrapper;
	
	public ClientObj(ServerEngine engine, Socket sock)
	{
		this.engine = engine;
		this.sock = sock;
		this.sockWrapper = new SocketWrapper(this.sock);

		this.clientCtx = this.engine.getSocketServer().createContext();
		this.clientCtx.setClientObj(this);

		this.thread = new ClientThread(this);
		this.thread.start();
	}
	
	public void clientDone()
	{
		try {
			sock.getInputStream().close();
			sock.getOutputStream().close();
		} catch (Exception e) {
			//ignore
		}
		try {
			sock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		engine.doneSocket(this);
	}
	
	public ClientContext getClientCtx()
	{
		return this.clientCtx;
	}
	
	public SocketWrapper getSocket()
	{
		return this.sockWrapper;
	}
	
	public void wantTerminate()
	{
		clientCtx.wantTerminate();
	}
	
	public void waitFor()
	{
		try {
			this.thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
