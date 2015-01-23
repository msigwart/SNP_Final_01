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
	public static final int CLIENT_SEND_INTERVAL 		= 100; //microseconds
	public static final int CLIENT_SEND_MIN_INTERVAL	= 10;
	public static final int CLIENT_SEND_MAX_INTERVAL	= 2500;
	public static final int CLIENT_NUM_OF_PACKETS 		= 100000;
	public static final int NUM_OF_CLIENTS				= 10;
	public static final int NUM_OF_PRIORITY_CLIENTS		= 3;
	
	//Server globals
	public static final int SERVER_RUNTIME 				= 3*60; //seconds
	public static final int SERVER_SEND_SPEED			= 1000; //Mbs
	//public static final long SERVER_SEND_SPEED_MICRO	= SERVER_SEND_SPEED*1000000;
	public static final double MICSECONDS_PER_PACKET	= (double)PACKET_SIZE_BITS/SERVER_SEND_SPEED;		//ca. 122 µs/Packet
	public static final int SERVER_QUEUE_SIZE 			= 1000000;

	

	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.printf("Hello Simulator\n");
		System.out.printf("The time is: %s\n", Time.getTimeStampString());
		System.out.printf("Server Send Speed: %d Mbs\n", SERVER_SEND_SPEED);
		System.out.printf("µs/Packet: %7.2f µs\n", MICSECONDS_PER_PACKET);


		// Create Statistics object
		Statistics stats = new Statistics("output/output.txt");
		
		// Create SendConnection
		SendConnection sc = new SendConnection(SERVER_RUNTIME, SERVER_SEND_SPEED, SERVER_QUEUE_SIZE, stats);
		sc.start();
		
		// Creation of clients
		int clientId = 0;		
		Client cl[] = new Client[NUM_OF_CLIENTS];
		
		for (int i=0; i<cl.length; i++) {
			Priority p = Priority.PACKET_PRIORITY_LOW;
			if (i<NUM_OF_PRIORITY_CLIENTS) {
				p = Priority.PACKET_PRIORITY_HIGH;
			}//if
			cl[i] = new Client(clientId++, CLIENT_NUM_OF_PACKETS, CLIENT_SEND_INTERVAL, p);
			cl[i].connectToSender(sc);					//Connect client to send connection
		}//for
		
		// Start of clients
		for (int i=0; i<cl.length; i++) {
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			cl[i].start();
		}//for
		
		
		
		
	}//main


}//Simulation
