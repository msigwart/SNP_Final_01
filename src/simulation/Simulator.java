package simulation;

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
	}
	
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
		//this.simulation = new Simulation();
	}//Constructor
	
}
