package simulation;

import statistics.Statistics;

public class Simulator {
	public static final int PACKET_SIZE_BYTES = 1526; //bytes
	public static final int PACKET_SIZE_BITS = PACKET_SIZE_BYTES*8;
	
	//Default values for the simulation
	public static final int DEFAULT_CLIENT_SEND_INTERVAL 		= 100; //microseconds
	public static final int DEFAULT_CLIENT_SEND_MIN_INTERVAL	= 10;
	public static final int DEFAULT_CLIENT_SEND_MAX_INTERVAL	= 1000;
	public static final int DEFAULT_CLIENT_NUM_OF_PACKETS 		= 10000;
	public static final int DEFAULT_NUM_OF_CLIENTS				= 4;
	public static final int DEFAULT_NUM_OF_PRIORITY_CLIENTS		= 1;
	
	//Server globals
	public static final int DEFAULT_SERVER_RUNTIME 				= 10; //seconds
	public static final int DEFAULT_SERVER_SEND_SPEED			= 1000; //Mbs
	public static final int DEFAULT_SERVER_QUEUE_SIZE 			= 1000000;
	
	private final int clientSendInterval;
	private final int clientSendMinInterval;
	private final int clientSendMaxInterval;
	private final int clientNumPackets;
	private final int numClients;
	private final int numPriorityClients;
	private final int serverRuntime; //seconds
	private final int serverSendSpeed; //Mbs
	private final long micSecondsPerPacket;	//ca. 122 µs/Packet
	private final int serverQueueSize;
	
	private final Simulation simulation = null;
	
	Simulator(int numClients, int numProrityClients){
		this(DEFAULT_CLIENT_SEND_INTERVAL, DEFAULT_CLIENT_SEND_MIN_INTERVAL, DEFAULT_CLIENT_SEND_MAX_INTERVAL,
				DEFAULT_CLIENT_NUM_OF_PACKETS, numClients, numProrityClients, DEFAULT_SERVER_RUNTIME, DEFAULT_SERVER_SEND_SPEED, 
				DEFAULT_SERVER_QUEUE_SIZE);
	}//Constructor
	
	Simulator(int clientSendInterval, int clientSendMinInterval, int clientSendMaxInterval, int clientNumPackets, int numClients, int numPriorityClients,
			  int serverRuntime, int serverSendSpeed, int serverQueueSize){
		this.clientSendInterval = clientSendInterval;
		this.clientSendMinInterval = clientSendMinInterval;
		this.clientSendMaxInterval = clientSendMaxInterval;
		this.clientNumPackets = clientNumPackets;
		this.numClients = numClients;
		this.numPriorityClients = numPriorityClients;
		this.serverRuntime = serverRuntime;
		this.serverSendSpeed = serverSendSpeed;
		this.micSecondsPerPacket = PACKET_SIZE_BITS/serverSendSpeed;
		this.serverQueueSize = serverQueueSize;
	}//Constructor
	
	public void runSimulation(){
		
		System.out.printf("Hello Simulator\n");
		System.out.printf( "The time is: %s\n", Time.getTimeStampString());

		// Create Statistics object
		Statistics stats = new Statistics("output/ouput.txt");
		
		// Create SendConnection
		SendConnection sc = new SendConnection(serverRuntime, serverSendSpeed, serverQueueSize, stats);
		sc.start();
		
		// Creation of clients
		int clientId = 0;		
		Client cl[] = new Client[numClients];
		
		for (int i=0; i<cl.length; i++) {
			Priority p = Priority.PACKET_PRIORITY_LOW;
			if (i<numPriorityClients) {
				p = Priority.PACKET_PRIORITY_HIGH;
			}//if
			cl[i] = new Client(clientId++, clientNumPackets, clientSendInterval, p);
			cl[i].connectToSender(sc);					//Connect client to send connection
		}//for
		
		// Start of clients
		for (int i=0; i<cl.length; i++) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cl[i].start();
		}//for
		
	}//runSimulation
}
