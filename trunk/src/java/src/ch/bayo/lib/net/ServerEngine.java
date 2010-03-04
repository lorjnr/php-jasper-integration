package ch.bayo.lib.net;

import java.net.*;
import java.io.*;
import java.util.*;

import ch.bayo.lib.exception.*;

public class ServerEngine {

	private SocketServer socketSrv;
	
	private ServerSocket srvSocket;	
	private AcceptThread accpThread;
	
	private Vector<ClientObj> clients;

	private int port;
	private boolean running;
	private int maxClients;

	public ServerEngine(SocketServer socketSrv, int port, int maxClients)
	{
		this.socketSrv = socketSrv;
		
		this.port = port;
		this.maxClients = maxClients;

		this.running = false;
	}

	public void start()  throws InvalidStateException, IOException
	{
		if (this.running) {
			throw new InvalidStateException();
		}
		
		clients = new Vector<ClientObj>();
		
		srvSocket = new ServerSocket(port);
		accpThread = new AcceptThread(this);
		accpThread.start();
		
	}
	
	public void stop()
	{
		accpThread.wantTerminate();
		try {
			srvSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			accpThread.join();
		} catch(InterruptedException e) {
			e.printStackTrace(System.out);
		}
		
		synchronized (clients) {
			
			for (int i=0; i<clients.size(); i++) {
				clients.elementAt(i).wantTerminate();
			}

			for (int i=0; i<clients.size(); i++) {
				clients.elementAt(i).waitFor();
			}
			
			clients = null;
		}
		
		synchronized (this) {
			this.notify();
		}
		
	}
	
	public void newSocket(Socket s)
	{
		synchronized (clients) {
			if ( clients != null ) {

				if (clients.size() >= maxClients) {
					try {
						s.close();
						return;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				clients.add(new ClientObj(this, s));
				
			}
		}
	}
	
	public void doneSocket(ClientObj obj)
	{
		synchronized (clients) {
			if ( clients != null ) {
				
				clients.remove(obj);
				
			}
		}
	}
	
	public ServerSocket getServerSocket()
	{
		return this.srvSocket;
	}
	
	public SocketServer getSocketServer()
	{
		return this.socketSrv;
	}

	public boolean running()
	{
		return this.running;
	}

}
