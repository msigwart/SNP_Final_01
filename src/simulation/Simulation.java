package simulation;



public class Simulation {
	
	
	//Client globals
	public static final int CLIENT_SEND_INTERVAL 	= 1000; //milliseconds
	public static final int CLIENT_NUM_OF_PACKETS 	= 10;
	
	//Server globals
	public static final int SERVER_RUNTIME 			= 20; //seconds
	public static final int SERVER_SEND_SPEED		= 100; //Mbs
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.printf("Hello Simulator 123\n");

		long t1, t2;
		t1 = Time.getTimeStamp();
		System.out.printf( "the time is: %s\n", Time.getTimeStampString());
		t2 = Time.getTimeStamp();
		System.out.printf( "MilliSeconds# %d\n", t2-t1 );
		
		SendConnection sc = new SendConnection(SERVER_RUNTIME, SERVER_SEND_SPEED);
		sc.start();
		
		int clientId = 0;
		
		Client cl = new Client(clientId++, CLIENT_NUM_OF_PACKETS, CLIENT_SEND_INTERVAL);
		cl.connectToSender(sc);
		cl.start();
		
		/*
		Client cl[] = new Client[10];
		
		// Creation of clients
		for (int i=0; i<cl.length; i++) {
			cl[i] = new Client(clientId++, 10, 1000);
			cl[i].connectToSender(sc);					//Connect client to send connection
		}//for
		
		// Start of clients
		for (int i=0; i<cl.length; i++) {
			cl[i].start();
		}//for
		*/
		
	}//main

}//Simulation
