package ch.bayo.lib.net;

public abstract class ClientContext {
	
	private ClientObj obj;
	private SocketWrapper sock;
	
	private boolean wantTerminate;
	
	public ClientContext()
	{
		this.obj = null;
		this.sock = null;
		this.wantTerminate = false;
	}
		
	public abstract void doThings();
	
	protected SocketWrapper getSocket() {
		return this.sock;
	}
	
	protected boolean getWantTerminate() {
		return this.wantTerminate;
	}
		
	void setClientObj(ClientObj obj) {
		this.obj = obj;
		this.sock = this.obj.getSocket();
	}
	
	void wantTerminate() {
		this.wantTerminate = true;
	}

}
