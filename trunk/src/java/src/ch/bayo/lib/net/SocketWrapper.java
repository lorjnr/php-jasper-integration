package ch.bayo.lib.net;

import java.net.*;
import java.io.*;

public class SocketWrapper {
	
	private Socket sock;
	
	private InputStream in;
	private OutputStream out;
		
	public SocketWrapper(Socket sock) {
		this.sock = sock;
		
		try {
			in = this.sock.getInputStream();
			out = this.sock.getOutputStream();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void setSoTimeout(int timeout) {
		try {
			sock.setSoTimeout(timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public InputStream getInputStream() {
		return in;
	}
	
	public OutputStream getOutputStream() {
		return out;
	}

}
