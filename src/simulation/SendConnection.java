package simulation;

import java.util.Observable;

public class SendConnection extends Observable implements Runnable {
	
	//Globals
	public static final int DEFAULT_QUEUE_SIZE = 10000;
	//Server events
	public static final int SERVER_EVENT_TERMINATED = 1;
	
	
	//Thread variables
	private final Thread thread = new Thread(this);
	private volatile boolean running = true;
	
	
	/**
	 * Priority buffer for received packets from priority clients
	 */
	private Packet[] queuePrio;
	volatile int inIndexPrio = 0;
	private volatile int outIndexPrio = 0;
	
	/**
	 * Buffer for received packets from non-priority clients
	 */
	private Packet[] queueNonPrio;
	volatile int inIndexNonPrio = 0;
	private volatile int outIndexNonPrio = 0;
	
	
	/**
	 * Connection speed in Mbs
	 */
	private final int connectionSpeed; 		//in Mbs

	
	//Time variables
	private final long runTime;				//run time in seconds of SendConnection
	private long startTime;
	private long currentTime;
	
	
	
	
	/* Constructors */	
	SendConnection() {
		this.runTime = 10;
		this.connectionSpeed = 10;
		this.queueNonPrio = new Packet[DEFAULT_QUEUE_SIZE];
	}//Constructor
	
	
	
	/**
	 * Creates a new SendConnection
	 * @param runTime connection run time in sec after number of seconds it will terminate
	 * @param speed connection speed in Mbs
	 */
	SendConnection(int runTime, int speed, int queueSize) {
		this.runTime = (long)runTime*Time.NANOSEC_PER_SEC;
		this.connectionSpeed = speed;
		this.queueNonPrio = new Packet[queueSize];
	}//Constructor
	
	
	/**
	 * Delegate for thread start
	 */
	public void start() {
		this.thread.start();
	}//start
	
	// Method to terminate thread
	public void terminate() { running = false; }
	
	
	// Run method of thread
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
				dequeuePacket();
				currentTime = newTime;
			}//if
			
			// It's time to terminate SendConnection
			if (currentTime - startTime >= (runTime)) {
				running = false;
				tellObservers(SERVER_EVENT_TERMINATED);
			}//if
		}//while
		
		System.out.println("SendConnection has terminated...");
	}//run
	
	
	
	/**
	 * This method enqueues a packet into the packet queue
	 * @param packet the packet to be enqueued
	 * @return true if a packet is successfully received
	 * 		   false if a packet could not be enqueued --> Queue full?
	 */
	public synchronized boolean enqueuePacket(Packet packet) {
		if (queueNonPrio[inIndexNonPrio] == null) {
			queueNonPrio[inIndexNonPrio] = packet;
			inIndexNonPrio = (inIndexNonPrio+1)%queueNonPrio.length;
			System.out.printf("SendConnection: received packet %d\n", packet.getId());
			return true;
		} else {
			return false;
		}//if
	}//enqueuePacket
	
	
	
	/**
	 * This method dequeues the packet first in line in the packet queue
	 * @return true if a packet is successfully dequeued
	 * 		   false if no packet is in line
	 */
	private synchronized boolean dequeuePacket() {
		if (queueNonPrio[outIndexNonPrio] == null) {
			return false;
		} else {
			System.out.printf("SendConnection: Sending packet %d\n", queueNonPrio[outIndexNonPrio].getId());
			queueNonPrio[outIndexNonPrio] = null;
			outIndexNonPrio = (outIndexNonPrio+1)%queueNonPrio.length;
			return true;
		}//if
	}//dequeuePacket
	
	
	//Tells connected clients about state changes --> OBSERVER PATTERN
	public void tellObservers(int event) {
		setChanged();
		notifyObservers(event);
	}//tellObservers
	
	
}//SendConnection
