package simulation;

import java.util.Observable;
import java.util.Observer;

import statistics.Statistics;

public class Simulator implements Observer{
	
	//Packet default globals
	public static final int PACKET_SIZE_BYTES = 1526; //bytes
	public static final int PACKET_SIZE_BITS = PACKET_SIZE_BYTES*8;
	
	//Client default globals
	private static int DEFAULT_CLIENT_SEND_INTERVAL 	= 100; //microseconds
	private static int DEFAULT_CLIENT_SEND_MIN_INTERVAL	= 10;
	private static int DEFAULT_CLIENT_SEND_MAX_INTERVAL	= 1000;
	private static int DEFAULT_CLIENT_NUM_OF_PACKETS 	= 10000;
	private static int DEFAULT_NUM_OF_CLIENTS			= 4;
	private static int DEFAULT_NUM_OF_PRIORITY_CLIENTS	= 1;
	
	//Server default globals
	private static int DEFAULT_SERVER_RUNTIME 			= 10; //seconds
	private static int DEFAULT_SERVER_SEND_SPEED		= 1000; //Mbs
	private static int DEFAULT_SERVER_QUEUE_SIZE 		= 1000000;
	
	//Private Members
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
	
	//private final Simulation simulation = null;
	private final Statistics stats;
	private String file;
	private boolean running;
	private boolean started;
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
	// Constructors	
	
	Simulator(String file){
		this(DEFAULT_CLIENT_SEND_INTERVAL, DEFAULT_CLIENT_SEND_MIN_INTERVAL, DEFAULT_CLIENT_SEND_MAX_INTERVAL,
				DEFAULT_CLIENT_NUM_OF_PACKETS, DEFAULT_NUM_OF_CLIENTS, DEFAULT_NUM_OF_PRIORITY_CLIENTS, DEFAULT_SERVER_RUNTIME, DEFAULT_SERVER_SEND_SPEED, 
				DEFAULT_SERVER_QUEUE_SIZE, file);
	}//Constructor
	
	Simulator(int numClients, int numProrityClients, String file){
		this(DEFAULT_CLIENT_SEND_INTERVAL, DEFAULT_CLIENT_SEND_MIN_INTERVAL, DEFAULT_CLIENT_SEND_MAX_INTERVAL,
				DEFAULT_CLIENT_NUM_OF_PACKETS, numClients, numProrityClients, DEFAULT_SERVER_RUNTIME, DEFAULT_SERVER_SEND_SPEED, 
				DEFAULT_SERVER_QUEUE_SIZE, file);
	}//Constructor
	
	Simulator(int numClients, int numProrityClients, int clientNumPackets, String file){
		this(DEFAULT_CLIENT_SEND_INTERVAL, DEFAULT_CLIENT_SEND_MIN_INTERVAL, DEFAULT_CLIENT_SEND_MAX_INTERVAL,
				clientNumPackets, numClients, numProrityClients, DEFAULT_SERVER_RUNTIME, DEFAULT_SERVER_SEND_SPEED, 
				DEFAULT_SERVER_QUEUE_SIZE, file);
	}//Constructor
	
	Simulator(int clientSendMinInterval, int clientSendMaxInterval, int serverRuntime, int serverSendSpeed, String file){
		this(DEFAULT_CLIENT_SEND_INTERVAL, clientSendMinInterval, clientSendMaxInterval,
				DEFAULT_CLIENT_NUM_OF_PACKETS, DEFAULT_NUM_OF_CLIENTS, DEFAULT_NUM_OF_PRIORITY_CLIENTS, serverRuntime, serverSendSpeed, 
				DEFAULT_SERVER_QUEUE_SIZE, file);
	}//Constructor
	
	Simulator(int clientSendMinInterval, int clientSendMaxInterval, int clientNumPackets, int serverRuntime, int serverSendSpeed, String file){
		this(DEFAULT_CLIENT_SEND_INTERVAL, clientSendMinInterval, clientSendMaxInterval,
				clientNumPackets, DEFAULT_NUM_OF_CLIENTS, DEFAULT_NUM_OF_PRIORITY_CLIENTS, serverRuntime, serverSendSpeed, 
				DEFAULT_SERVER_QUEUE_SIZE, file);
	}//Constructor
	
	Simulator(int clientSendMinInterval, int clientSendMaxInterval, int clientNumPackets, int serverRuntime, int serverSendSpeed, int serverQueueSize, String file){
		this(DEFAULT_CLIENT_SEND_INTERVAL, clientSendMinInterval, clientSendMaxInterval,
				clientNumPackets, DEFAULT_NUM_OF_CLIENTS, DEFAULT_NUM_OF_PRIORITY_CLIENTS, serverRuntime, serverSendSpeed, 
				serverQueueSize, file);
	}//Constructor
	
	Simulator(int clientSendInterval, int clientSendMinInterval, int clientSendMaxInterval, int clientNumPackets, int numClients, int numPriorityClients,
			  int serverRuntime, int serverSendSpeed, int serverQueueSize, String file){
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
		this.file = file;
		this.stats = new Statistics(file);
		this.running = false;
		this.started = false;
	}//Constructor
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
	// Methods
	
	public void runSimulation(){
		
		System.out.printf("Hello Simulator\n");
		System.out.printf( "The time is: %s\n", Time.getTimeStampString());

		// Create SendConnection
		SendConnection sc = new SendConnection(serverRuntime, serverSendSpeed, serverQueueSize, stats);
		sc.addObserver(this);
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
		
		running = true;
		started = true;
		
	}//runSimulation
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
	// Setters
	
	public static void setDefaultClientSendMinInterval(int defaultClientMinInterval){
		DEFAULT_CLIENT_SEND_MIN_INTERVAL = defaultClientMinInterval;
	}//setDefaultClientSendMinInterval
	
	public static void setDefaultClientSendMaxInterval(int defaultClientMaxInterval){
		DEFAULT_CLIENT_SEND_MAX_INTERVAL = defaultClientMaxInterval;
	}//setDefaultClientSendMaxInterval
	
	public static void setDefaultClientNumPackets(int defaultClientNumPackets){
		DEFAULT_CLIENT_NUM_OF_PACKETS = defaultClientNumPackets;
	}//setDefaultClientNumPackets
	
	public static void setDefaultNumClients(int defaultNumClient){
		DEFAULT_NUM_OF_CLIENTS = defaultNumClient;
	}//setDefaultNumClients
	
	public static void setDefaultNumPriorityClients(int defaultNumPriorityClients){
		DEFAULT_NUM_OF_PRIORITY_CLIENTS = defaultNumPriorityClients;
	}//setDefaultNumPriorityClients
	
	public static void setDefaultServerRuntime(int defaultServerRuntime){
		DEFAULT_SERVER_RUNTIME = defaultServerRuntime;
	}//setDefaultServerRuntime
	
	public static void setDefaultServerSendSpeed(int defaultServerSendSpeed){
		DEFAULT_SERVER_SEND_SPEED = defaultServerSendSpeed;
	}//setDefaultServerSendSpeed
	
	public static void setDefaultServerQueueSize(int defaultServerQueueSize){
		DEFAULT_SERVER_QUEUE_SIZE = defaultServerQueueSize;
	}//setDefaultServerQueueSize

	
	@Override
	public void update(Observable arg0, Object arg1) {
		switch ((int)arg1) {
			case SendConnection.SERVER_EVENT_TERMINATED:
				stats.printStatistics();
				running = false;
				break;
			default:
				break;
		}//switch	
	}
	
	
	public boolean isRunning(){
		return running;
	}
	
	public boolean getStarted(){
		return started;
	}
}
