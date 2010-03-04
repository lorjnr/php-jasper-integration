package ch.bayo.jasper.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;


class ShutdownHook extends Thread {
	
	private ServerEngine engine;
	
	ShutdownHook(ServerEngine engine) {
		this.engine = engine;
	}
	
	public void run() {
		System.out.println("Shutdown Server!");		
		try {
			engine.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Down!");
	}
	
}

public class Main {

	public static void main(String[] args)
	{
		
		int port = 0;
		int maxClients = 0;
		try {
			port = Integer.valueOf(args[0]);
		} catch (Exception e) {
		}
		if (port == 0) {
			System.out.println("No port specified!");
			System.exit(1);
		}
		try {
			maxClients = Integer.valueOf(args[1]);
		} catch (Exception e) {
			maxClients = 0;
		}
		if (maxClients == 0) {
			maxClients = 5;
		}

		ServerEngine engine = new ServerEngine();
		
		try {
			engine.start(port, maxClients);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		ShutdownHook sh = new ShutdownHook(engine);
		Runtime.getRuntime().addShutdownHook(sh);

		System.out.println("Server running!");
		
		boolean detached = false;
		if (args.length > 1) {
			if (args[1].compareTo("detached") == 0) {
				detached = true;
			}
		}
		
		if (detached) {

			System.out.println("Use CTRL-C to shutdown!");
			
			synchronized (engine) {
				try {
					engine.wait();
				} catch (InterruptedException e) {
				}
			}
		} else {
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
		}
		
		System.exit(0);
		
	}
	
}
