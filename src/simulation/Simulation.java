package simulation;

import statistics.Statistics;



public class Simulation {
	
	
	//Simulation globals
	public static final int SIMULATION_RUNS = 5;
	public static final int SIMULATION_TIME_UNIT = 1; //microsecond
	
	//Packet globals
	public static final int PACKET_SIZE_BYTES 			= 1526; //bytes
	public static final int PACKET_SIZE_BITS 			= PACKET_SIZE_BYTES*8;
	
	//Client globals
	public static final int CLIENT_SEND_INTERVAL 		= 10; //microseconds
	public static final int CLIENT_NUM_OF_PACKETS 		= 100000;
	public static final int NUM_OF_CLIENTS				= 4;
	
	//Server globals
	public static final int SERVER_RUNTIME 				= 5; //seconds
	public static final int SERVER_SEND_SPEED			= 100; //Mbs
	//public static final long SERVER_SEND_SPEED_MICRO	= SERVER_SEND_SPEED*1000000;
	public static final long MICSECONDS_PER_PACKET		= PACKET_SIZE_BITS/SERVER_SEND_SPEED;
	public static final int SERVER_QUEUE_SIZE 			= 300000;

	

	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.printf("Hello Simulator\n");
		System.out.printf( "The time is: %s\n", Time.getTimeStampString());

		// Create Statistics object
		Statistics stats = new Statistics("output/statTest");
		
		// Create SendConnection
		SendConnection sc = new SendConnection(SERVER_RUNTIME, SERVER_SEND_SPEED, SERVER_QUEUE_SIZE, stats);
		sc.start();
		
		// Creation of clients
		int clientId = 0;		
		Client cl[] = new Client[NUM_OF_CLIENTS];
		
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
