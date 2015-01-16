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
		this.connectionSpeed = speed;
		//this.queueNonPrio = new Packet[queueSize];
		this.queueNonPriority 	= new ConcurrentLinkedQueue<Packet>();
		this.queuePriority		= new ConcurrentLinkedQueue<Packet>();
		this.stats = stats;
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
		System.out.println("SendConnection started...");
		
		long newTime;
		while (running) {
			//System.out.println("SendConnection: Doing some work.");
			newTime = System.nanoTime();
			
			// It's time to send a packet
			if ( (newTime - currentTime) >= (Simulation.MICSECONDS_PER_PACKET*Time.NANOSEC_PER_MICROSEC) ) {
				//dequeuePacket();
				Packet packet = null;
				if ( !queuePriority.isEmpty() ) {
					packet = dequeuePacket(true);
					currentTime = newTime;
				} else if ( !queueNonPriority.isEmpty() ) {
					packet = dequeuePacket(false);
					currentTime = newTime;
				}//if
			}//if
			
			// It's time to terminate SendConnection
			if (currentTime - startTime >= (runTime)) {
				running = false;
				tellObservers(SERVER_EVENT_TERMINATED);
			}//if
		}//while
		
		System.out.println("SendConnection has terminated...");
		//stats.readEventsFromFile();			//TODO--> shouldn't be called in SendConnection, belongs to simulation environment
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
	public boolean enqueuePacket(Packet packet, boolean priority) {
		System.out.printf("SendConnection: received packet %d\n", packet.getId());
		if (priority = true) {
			return queuePriority.add(packet);
		} else {
			return queueNonPriority.add(packet);
		}//else
	}//enqueuePacket
	
	
	
	/**
	 * This method dequeues the packet first in line in the packet queue
	 * @param priority the priority of the packet --> determines from which queue
	 * @return true if a packet is successfully dequeued
	 * 		   false if no packet is in line
	 */
	private Packet dequeuePacket(boolean priority) {
		try {
			Packet packet = null;
			if (priority == true) {
				packet = queuePriority.remove();
			} else {
				packet = queueNonPriority.remove();
			}//if
			System.out.printf("SendConnection: Sending packet %d\n", packet.getId());
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
