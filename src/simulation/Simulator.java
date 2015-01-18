package simulation;

import statistics.Statistics;

public class Simulator {
	public static final int PACKET_SIZE_BYTES = 1526; //bytes
	public static final int PACKET_SIZE_BITS = PACKET_SIZE_BYTES*8;
	
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
		this(100, 10, 1000, 10000, numClients, numProrityClients, 10, 1000, 1000000);
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
