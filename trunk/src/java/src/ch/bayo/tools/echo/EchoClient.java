package ch.bayo.tools.echo;

import java.net.*;
import java.io.*;

public class EchoClient {

	public static void main(String[] args) {
		
		try {
			Socket sock = new Socket(InetAddress.getLocalHost(), 1234);
			
			sock.setSoTimeout(300);
			
			InputStream in = sock.getInputStream();
			OutputStream out = sock.getOutputStream();
			
			OutputThread ot = new OutputThread(in);
			ot.start();
			
			// ********* BlaBlaBla
			
			doBlaBlaBla(out);
			
			// ********* BlaBlaBla
			
			ot.wantTerminate();
			ot.join();
			
			in.close();
			out.close();
			sock.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void doBlaBlaBla(OutputStream out) {
		
		String s;		
		try {

			s = "Dominic";
			out.write(s.getBytes());
			
			s = "Lalala";
			out.write(s.getBytes());

			s = "tatata";
			out.write(s.getBytes());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}

class OutputThread extends Thread {

	private InputStream in;
	private boolean terminate;
	
	public OutputThread(InputStream in) {
		super();
	
		this.in = in;
		this.terminate = false;
	}
	
	
	public void run() {
		
		int len = -1;
		byte[] b = new byte[100];
		
		while ( ! this.terminate ) {

			try {
				len = in.read(b);
			} catch (Exception e) {
				len = 0;
			}
			if (len == -1) {
				break;
			}
			if (len > 0) {
				System.out.write(b, 0, len);
			}			
			
		}
		
	}
	
	public void wantTerminate() {
		this.terminate = true;
	}
	
}