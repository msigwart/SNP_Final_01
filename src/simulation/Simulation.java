package simulation;



public class Simulation {
	
	
	//Simulation globals
	public static final int SIMULATION_RUNS = 5;
	
	//Client globals
	public static final int CLIENT_SEND_INTERVAL 	= 1000; //milliseconds
	public static final int CLIENT_NUM_OF_PACKETS 	= 10;
	public static final int NUM_OF_CLIENTS			= 10;
	
	//Server globals
	public static final int SERVER_RUNTIME 			= 20; //seconds
	public static final int SERVER_SEND_SPEED		= 100; //Mbs
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.printf("Hello Simulator\n");
		System.out.printf( "The time is: %s\n", Time.getTimeStampString());

		
		SendConnection sc = new SendConnection(SERVER_RUNTIME, SERVER_SEND_SPEED);
		sc.start();
		
		int clientId = 0;		
		Client cl[] = new Client[NUM_OF_CLIENTS];
		
		// Creation of clients
		for (int i=0; i<cl.length; i++) {
			cl[i] = new Client(clientId++, CLIENT_NUM_OF_PACKETS, CLIENT_SEND_INTERVAL);
			cl[i].connectToSender(sc);					//Connect client to send connection
		}//for
		
		// Start of clients
		for (int i=0; i<cl.length; i++) {
			cl[i].start();
		}//for
		
		
		
		
	}//main

}//Simulation
