package simulation;

public class Client extends Thread {

	private volatile boolean running = true;
	private volatile SendConnection sendConnection = null;
	
	private final int clientId;			//Client ID
	private final int numOfPackets;		//The number of packets sent by client
	private final int interval;			//Interval at which packets are sent in microseconds
	
	
	private long startTime;
	private long currentTime;
	
	/**
	 * Creates a new client
	 * @param id the client ID
	 * @param numPackets the number of packets the client will send
	 * @param interval the interval in milliseconds at which rate packets are sent
	 */
	Client(int id, int numPackets, int interval) {
		super();
		this.clientId = id;
		this.numOfPackets = numPackets;
		this.interval = interval;
	}//Constructor
	
	
	// Getter for client ID
	public int getClientId() { return this.clientId; }
	
	
	public void connectToSender(SendConnection sender) {
		this.sendConnection = sender;
	}//connectToSender
	
	
	
	public void run() {
		if (Thread.currentThread() != this) throw new IllegalStateException();
		System.out.printf("Client %d started...\n", this.clientId);

		while (running) {
			
			if (sendConnection != null) {				//wait for connection to sender
				startTime = System.nanoTime();
				currentTime = startTime;
				System.out.printf("Client %d: Connected to sendConnection\n", this.clientId);
				
				int packetCounter = 0;
				long newTime;
				while (packetCounter<numOfPackets) {	//send number of Packets
					newTime = System.nanoTime();	//get currentTime
					if (newTime - currentTime >= interval*Time.NANOSEC_PER_MICROSEC) {	//TODO --> Check for long repetition
						
						Packet packet = new Packet();
						System.out.printf("Client %d: Created Packet: ID %d\n", this.clientId, packet.getId());
						if ( !sendConnection.enqueuePacket(packet) ){
							System.out.printf("-------> Client %d: Lost packet %d\n", this.clientId, packet.getId());
						} else {
							//System.out.printf("Client %d: Sent packet %d\n", this.clientId, packet.getId());
						}//if
						packetCounter += 1;
						currentTime = newTime;
					}//if
				}//while
				running = false;
			}//if
			
		}//while
		System.out.printf("Client %d terminated.\n", this.clientId);
	}//run
	
}//Client
