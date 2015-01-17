package simulation;

import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

import statistics.Event;
import statistics.Statistics;

/**
 * SendConnection implements the Runnable interface and contains a Thread object as private member. 
 * It extends Observable (Observer Pattern).
 * This class represents a sending (outgoing) connection. In the simulation it represents the router.
 * Multiple clients (Class Client) can connect to the router and send packets to it.
 * It contains queues for priority as well as non priority packets.
 * The packets are "sent" on the outgoing connection on a set speed (in Mbs).
 * @author Marten Sigwart
 *
 */
public class SendConnection extends Observable implements Runnable {
	

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Global variables
	
	// Default values
	public static final int DEFAULT_QUEUE_NONPRIO_SIZE 	= 10000;
	public static final int DEFAULT_QUEUE_PRIO_SIZE 	= 10000;
	public static final int DEFAULT_RUN_TIME			= 10;//seconds
	public static final int DEFAULT_CONNECTION_SPEED	= 10;//Mbs
	
	//Server events
	public static final int SERVER_EVENT_TERMINATED 	= 1;
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Private Members
	
	//Thread variables
	private final 		Thread 		thread 	= new Thread(this);
	private volatile 	boolean 	running = true;
	
	/**
	 * Priority buffer for received packets from priority clients
	 */
	private 			ConcurrentLinkedQueue<Packet>	queuePriority;

	/**
	 * Buffer for received packets from non-priority clients
	 */
	private 			ConcurrentLinkedQueue<Packet> 	queueNonPriority;
	
	
	/**
	 * Connection speed in Mbs
	 */
	private final 		int 		connectionSpeed; 		//in Mbs
	private final 		Statistics 	stats;

	
	//Time variables
	private final long runTime;				//run time in seconds of SendConnection
	private long startTime;
	private long currentTime;
	
	private long progressTime;				//field to display progress during simulation
	

	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Constructors	
	
	/**
	 *  Creates a new Send connection.<br>
	 *  - Run time set to 10 s.<br>
	 *  - Connection speed set to 10 Mbs.<br>
	 */	
	SendConnection(Statistics stats) {
		this(DEFAULT_RUN_TIME, DEFAULT_CONNECTION_SPEED, DEFAULT_QUEUE_NONPRIO_SIZE, stats);
	}//Constructor
	
	/**
	 * Creates a new SendConnection
	 * @param runTime connection run time in sec after number of seconds it will terminate
	 * @param speed connection speed in Mbs
	 */
	SendConnection(int runTime, int speed, int queueSize, Statistics stats) {
		this.runTime = (long)runTime*Time.NANOSEC_PER_SEC;
		this.progressTime = this.runTime/100;	//Time per 1 percent progress
		this.connectionSpeed = speed;
		
		//init queues
		this.queueNonPriority 	= new ConcurrentLinkedQueue<Packet>();
		this.queuePriority		= new ConcurrentLinkedQueue<Packet>();
		
		//init stats
		this.stats = stats;
		this.addObserver(stats);
	}//Constructor

	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Methods
	
	/**
	 * Delegate for thread start
	 */
	public void start() {
		this.thread.start();
	}//start
	
	// Method to terminate thread
	public void terminate() { running = false; }
	
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Run method
	
	/**
	 * Run method of SendConnection:<br>
	 * 1. Capture start time<br>
	 * 2. Set current time to start time<br>
	 * 3. Check if a packet can be send, if yes send packet (dequeue)<br>
	 * 4. Check if run time is reached, if yes terminate SendConnection<br>
	 */
	public void run() {
		if (thread == null || Thread.currentThread() != thread) throw new IllegalStateException();
		startTime 	= System.nanoTime();			// Get start time
		currentTime = startTime;
		int progressPercent = 0;
		long nextProgress = startTime + progressTime;
		System.out.println("SendConnection started...");
		System.out.printf("Simulation Progress:\n");

		
		long newTime;
		while (running) {
			//System.out.println("SendConnection: Doing some work.");
			newTime = System.nanoTime();
			
			// It's time to send a packet
			if ( (newTime - currentTime) >= (Simulation.MICSECONDS_PER_PACKET*Time.NANOSEC_PER_MICROSEC) ) {
				//dequeuePacket();
				Packet packet = null;
				if ( !queuePriority.isEmpty() ) {
					packet = dequeuePacket(Priority.PACKET_PRIORITY_HIGH);			//TODO: Change to enum Priority
					currentTime = newTime;
				} else if ( !queueNonPriority.isEmpty() ) {
					packet = dequeuePacket(Priority.PACKET_PRIORITY_LOW);
					currentTime = newTime;
				}
				//currentTime = newTime;
				/*packet = dequeuePacket(Priority.PACKET_PRIORITY_HIGH);		//Try to dequeue high priority
				if (packet == null) {										//If no high priority, dequeue low priority
					packet = dequeuePacket(Priority.PACKET_PRIORITY_LOW);
				}//if
				if (packet != null) {
					currentTime = newTime;
				}//if*/
			}//if
			
			//Display progress
			if (newTime >= nextProgress) {
				progressPercent++;	
				nextProgress += progressTime;			//set next progress time (old + time per 1% progress)
				//System.out.print("\r");				//Does not work as desired--> Eclipse Bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=76936
				/*for (int i=0; i<progressPercent; i++) {
					System.out.print("#");
				}//for
				for (int i=progressPercent; i<100; i++) {
					System.out.print(" ");
				}//for*/
				System.out.printf("%d Percent....\n", progressPercent);
			}//if
			
			// It's time to terminate SendConnection
			if (newTime - startTime >= (runTime)) {
				running = false;
			}//if
		}//while
		System.out.println("SendConnection: Terminated...");
		tellObservers(SERVER_EVENT_TERMINATED);
	}//run

	
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Enqueue and Dequeue methods	
	
	/**
	 * This method enqueues a packet into the packet queue
	 * @param packet the packet to be enqueued
	 * @param priority the priority of the packet --> determines into which queue
	 * @return true if a packet is successfully received
	 * 		   false if a packet could not be enqueued --> Queue full?
	 */
	public boolean enqueuePacket(Packet packet, Priority priority) {
		//System.out.printf("SendConnection: received packet %d\n", packet.getId());		//TODO: Output message in calls not in declaration
		boolean success = false;
		switch (priority) {
			case PACKET_PRIORITY_HIGH:
				stats.triggerEvent(Event.EVENT_TYPE_ENQUEUE, packet);
				success = queuePriority.add(packet);
				break;
			case PACKET_PRIORITY_LOW:
				stats.triggerEvent(Event.EVENT_TYPE_ENQUEUE, packet);
				success = queueNonPriority.add(packet);
				break;
			default:
				break;
		}//switch
		
		return success;

	}//enqueuePacket
	
	
	
	/**
	 * This method dequeues the packet first in line in the packet queue
	 * @param priority the priority of the packet --> determines from which queue
	 * @return true if a packet is successfully dequeued
	 * 		   false if no packet is in line
	 */
	private Packet dequeuePacket(Priority priority) {
		try {
			Packet packet = null;
			switch (priority) {
				case PACKET_PRIORITY_HIGH:
					packet = queuePriority.remove();
					stats.triggerEvent(Event.EVENT_TYPE_DEQUEUE, packet);
					break;
				case PACKET_PRIORITY_LOW:
					packet = queueNonPriority.remove();
					stats.triggerEvent(Event.EVENT_TYPE_DEQUEUE, packet);
					break;
				default:
					break;
			}//switch
			//System.out.printf("SendConnection: Sending packet %d\n", packet.getId()); //TODO: Output message in calls not in declaration
			return packet;
		} catch (NoSuchElementException e) {
			//System.out.printf("SendConnection: No Element in Queue...\n");
			return null;
		}//try
	}//dequeuePacket
	
	
	//Tells connected clients about state changes --> OBSERVER PATTERN
	public void tellObservers(int event) {
		setChanged();
		notifyObservers(event);
	}//tellObservers
	
	
}//SendConnection
