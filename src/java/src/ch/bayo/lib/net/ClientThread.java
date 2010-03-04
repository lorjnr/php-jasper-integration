package ch.bayo.lib.net;

class ClientThread extends Thread {
	
	private ClientObj obj;
	
	public ClientThread(ClientObj obj)
	{
		super();
		
		this.obj = obj;
	}
	
	public void run()
	{
		
		obj.getClientCtx().doThings();
		obj.clientDone();
		
	}

}
