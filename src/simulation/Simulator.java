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
	private int clientSendInterval;
	private int clientSendMinInterval;
	private int clientSendMaxInterval;
	private int clientNumPackets;
	private int numClients;
	private int numPriorityClients;
	private int serverRuntime; //seconds
	private int serverSendSpeed; //Mbs
	private long micSecondsPerPacket;	//ca. 122 µs/Packet
	private int serverQueueSize;
	
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
		this.setClientSendInterval(clientSendInterval);
		this.setClientSendMinInterval(clientSendMinInterval);
		this.setClientSendMaxInterval(clientSendMaxInterval);
		this.setClientNumPackets(clientNumPackets);
		this.setNumClients(numClients);
		this.setNumPriorityClients(numPriorityClients);
		this.setServerRuntime(serverRuntime);
		this.setServerSendSpeed(serverSendSpeed);
		this.setMicSecondsPerPacket(PACKET_SIZE_BITS/serverSendSpeed);
		this.setServerQueueSize(serverQueueSize);
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
				//stats.printStatistics();
				running = false;
				break;
			default:
				break;
		}//switch	
	}
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
	//Getters
	
	public boolean isRunning(){
		return this.running;
	}
	
	public boolean getStarted(){
		return this.started;
	}
	
	public int getClientSendInterval() {
		return clientSendInterval;
	}

	public int getClientSendMinInterval() {
		return clientSendMinInterval;
	}

	public int getClientSendMaxInterval() {
		return clientSendMaxInterval;
	}

	public int getClientNumPackets() {
		return clientNumPackets;
	}

	public int getNumClients() {
		return numClients;
	}

	public int getNumPriorityClients() {
		return numPriorityClients;
	}

	public int getServerRuntime() {
		return serverRuntime;
	}

	public int getServerSendSpeed() {
		return serverSendSpeed;
	}

	public long getMicSecondsPerPacket() {
		return micSecondsPerPacket;
	}

	public int getServerQueueSize() {
		return serverQueueSize;
	}
	
	public Statistics getStatistics(){
		return this.stats;
	}
	
	public String printSimulationData(){
		String str = new String();
		
		str += "\n>>>>>>>>>>>>>> Clients Information <<<<<<<<<<<<<<<<<<\n\n";
		str += "\tNumber of Clients: " 				+ getNumClients() 			 + "\n";
		str += "\tNumber of Priority Clients: " 	+ getNumPriorityClients() 	 + "\n";
		str += "\tClient Send Min Interval: " 		+ getClientSendMinInterval() + "\n";
		str += "\tClient Send Max Interval: " 		+ getClientSendMaxInterval() + "\n";
		str += "\tNumber of packets per client: " 	+ getClientNumPackets() 	 + "\n";
		str += "\tNumber of packets per client: " 	+ getClientNumPackets() 	 + "\n";
		
		str += "\n>>>>>>>>>>>>>> Server Information <<<<<<<<<<<<<<<<<<<\n\n";
		str += "\tServer Runtime: " 				+ getServerRuntime() 		 + " sec\n";
		str += "\tServer Send Speed: " 				+ getServerSendSpeed() 		 + " Mb/s\n";
		str += "\tServer Queue Size: " 				+ getServerQueueSize() 		 + "\n";
		
		return str;
	}

	public void setClientSendInterval(int clientSendInterval) {
		this.clientSendInterval = clientSendInterval;
	}

	public void setClientSendMinInterval(int clientSendMinInterval) {
		this.clientSendMinInterval = clientSendMinInterval;
	}

	public void setClientSendMaxInterval(int clientSendMaxInterval) {
		this.clientSendMaxInterval = clientSendMaxInterval;
	}

	public void setClientNumPackets(int clientNumPackets) {
		this.clientNumPackets = clientNumPackets;
	}

	public void setNumClients(int numClients) {
		this.numClients = numClients;
	}

	public void setNumPriorityClients(int numPriorityClients) {
		this.numPriorityClients = numPriorityClients;
	}

	public void setServerRuntime(int serverRuntime) {
		this.serverRuntime = serverRuntime;
	}

	public void setServerSendSpeed(int serverSendSpeed) {
		this.serverSendSpeed = serverSendSpeed;
	}

	public void setMicSecondsPerPacket(long micSecondsPerPacket) {
		this.micSecondsPerPacket = micSecondsPerPacket;
	}

	public void setServerQueueSize(int serverQueueSize) {
		this.serverQueueSize = serverQueueSize;
	}
}
