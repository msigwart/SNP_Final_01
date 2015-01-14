package simulation;

import java.util.Observable;
import java.util.Observer;

/**
 * Client class to send packets to a send connection.
 * Extends Thread and implements Observer (Observer Pattern)
 * @author Marten Sigwart
 *
 */
public class Client extends Thread implements Observer {

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
		sendConnection.addObserver(this);
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
				
				while (packetCounter<numOfPackets && running == true) {	//send number of Packets
					newTime = System.nanoTime();	//get currentTime
					
					if ( (newTime - currentTime) >= (interval*Time.NANOSEC_PER_MICROSEC) ) {	//TODO --> Check for long repetition	
						
						/* Create new packet */
						Packet packet = new Packet();
						//System.out.printf("Client %d: Created Packet: ID %d at time\n", this.clientId, packet.getId());
						if ( !sendConnection.enqueuePacket(packet) ){
							System.out.printf("-------> Client %d: Lost packet %d\n", this.clientId, packet.getId());
						} else {
							System.out.printf("Client %d: Sent packet %d\n", this.clientId, packet.getId());
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
