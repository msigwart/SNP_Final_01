package simulation;

import java.util.Observable;
import java.util.Observer;

import lecuyer.RandomIntervalGenerator;

/**
 * Client class to send packets to a send connection.
 * Extends Thread and implements Observer (Observer Pattern)
 * @author Marten Sigwart
 *
 */
public class Client extends Thread implements Observer {

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Private Members
	private volatile boolean running = true;
	private volatile SendConnection sendConnection = null;
	
	private final int 		clientId;			//Client ID
	private final int 		numOfPackets;		//The number of packets sent by client
	private final Priority 	priority;
	
	private final RandomIntervalGenerator rig;	//random intervals for sending packets of
	
	private int		interval;			//Interval at which packets are sent in microseconds
	private long 	startTime;
	private long 	currentTime;

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Constructors
	/**
	 * Creates a new client
	 * @param id the client ID
	 * @param numPackets the number of packets the client will send
	 * @param interval the interval in milliseconds at which rate packets are sent
	 * @param priority true if Client is a priority client
	 */
	Client(int id, int numPackets, int interval, Priority priority) {
		super();
		this.clientId 		= id;
		this.numOfPackets 	= numPackets;
		this.interval 		= interval;
		this.priority 		= priority;
		this.rig			= new RandomIntervalGenerator(Simulation.CLIENT_SEND_MIN_INTERVAL, Simulation.CLIENT_SEND_MAX_INTERVAL);
	}//Constructor
	
	/**
	 * Creates a new client, priority will be set to false
	 * @param id the client ID
	 * @param numPackets the number of packets the client will send
	 * @param interval the interval in milliseconds at which rate packets are sent
	 */
	Client(int id, int numPackets, int interval) {
		this(id, numPackets, interval, Priority.PACKET_PRIORITY_LOW);
	}//Constructor
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Methods
	
	
	// Getter for client ID
	public int getClientId() { return this.clientId; }
	
	
	public void connectToSender(SendConnection sender) {
		this.sendConnection = sender;
		sendConnection.addObserver(this);
	}//connectToSender
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Run Method	
	/**
	 * Run Method for client:<br>
	 * 1. Wait for connection to SendConnection<br>
	 * 2. Send number of packets<br>
	 * 3. Delete this from list of Observers<br>
	 * 4. Terminate<br>
	 */
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
				interval = rig.generateNewRandomInterval();
				//System.out.printf("Client: new Interval: %d\n", interval);
				
				while (packetCounter<numOfPackets && running == true) {	//send number of Packets
					newTime = System.nanoTime();	//get currentTime
					
					if ( (newTime - currentTime) >= (interval*Time.NANOSEC_PER_MICROSEC) ) {	//TODO --> Check for long repetition	
						
						/* Create new packet */
						Packet packet = new Packet(this.priority);
						//System.out.printf("Client %d: Created Packet: ID %d at time\n", this.clientId, packet.getId());
						if ( !sendConnection.enqueuePacket(packet, packet.getPriority()) ){			//packet could not be put in server queue
							System.out.printf("-------> Client %d: Lost packet %d\n", this.clientId, packet.getId());
						} else {
							//System.out.printf("Client %d: Sent packet %d\n", this.clientId, packet.getId());	//packet successfully reached server queue
							interval = rig.generateNewRandomInterval();
							//System.out.printf("Client %d: new Interval: %d\n", this.clientId, interval);
						}//if
						packetCounter += 1;
						currentTime = newTime;
						
					} else if (newTime < currentTime) {
						currentTime = newTime;
						
					}//if
					
				}//while
				running = false;
			}//if
			
		}//while
		this.sendConnection.deleteObserver(this);		//delete yourself from list of observers
		this.sendConnection = null;						//delete sendConncection
		System.out.printf("Client %d: terminated.\n", this.clientId);
	}//run


	@Override		//--> OBSERVER PATTERN
	public void update(Observable o, Object arg) {
		switch ((int)arg) {
			case SendConnection.SERVER_EVENT_TERMINATED:
				System.out.printf("Client %d: Lost connection to SendConnection, terminating...\n", this.clientId);
				running = false;
				sendConnection.deleteObserver(this);
				break;
			default:
				break;
		}//switch
			
	}//update
	
}//Client
