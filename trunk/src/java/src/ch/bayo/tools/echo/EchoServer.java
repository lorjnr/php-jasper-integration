package ch.bayo.tools.echo;

import ch.bayo.lib.net.*;
import java.io.*;
import java.net.*;

class EchoEngine extends SocketServer {
	
	public ClientContext createContext() {
		
		return new EchoContext();
		
	}
	
}

class EchoContext extends ClientContext {
	
	public void doThings() {
		
		getSocket().setSoTimeout(1000);
		
		while ( ! getWantTerminate() ) {
			
			int len = -1;
			
			byte[] b = new byte[100];
			try {
				len = getSocket().getInputStream().read(b);
			} catch (SocketTimeoutException e) {
				len = 0;
			} catch (SocketException e) {
				len = -1;
			} catch (IOException e) {
				len = -1;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (len == -1) {
				break;
			}
			
			if (len > 0) {
				try {
					getSocket().getOutputStream().write(b, 0, len);
				} catch (Exception e) {
					//do not handle
				}
			}			
			
		}
		
	}
	
}



public class EchoServer {

	public static void main(String[] args)
	{
		
		EchoEngine engine = new EchoEngine();
		
		try {
			engine.start(1234, 50);
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		System.out.println("Server running!");
		
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		
		String line = "";
		while ( ! line.equals("quit") ) {
			System.out.print("command: ");
			try {
				line = in.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			engine.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
