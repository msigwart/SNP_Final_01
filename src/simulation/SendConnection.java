package simulation;

public class SendConnection extends Thread {
	
	public static final int SERVER_QUEUE_SIZE = 10000;
	
	private volatile boolean running = true;
	private final long runTime;				//run time in seconds of SendConnection
	private final int connectionSpeed; 		//in Mbs
	
	/**
	 * Buffer for received packets
	 */
	private Packet[] pQueue = new Packet[SERVER_QUEUE_SIZE];
	private volatile int enqueueIndex = 0;
	private volatile int dequeueIndex = 0;
	
	
	private long startTime;
	private long currentTime;
	
	/* Constructors */	
	SendConnection() {
		super();
		this.runTime = 10;
		this.connectionSpeed = 10;
	}//Constructor
	
	/**
	 * Creates a new SendConnection
	 * @param runTime connection run time in sec after number of seconds it will terminate
	 * @param speed connection speed in Mbs
	 */
	SendConnection(int runTime, int speed) {
		super();
		this.runTime = (long)runTime*Time.NANOSEC_PER_SEC;
		this.connectionSpeed = speed;
	}//Constructor
	
	
	
	// Method to terminate thread
	public void terminate() { running = false; }
	
	// Run method of thread
	public void run() {
		if (Thread.currentThread() != this) throw new IllegalStateException();
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
		if (pQueue[enqueueIndex] == null) {
			pQueue[enqueueIndex] = packet;
			enqueueIndex = (enqueueIndex+1)%pQueue.length;
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
		if (pQueue[dequeueIndex] == null) {
			return false;
		} else {
			System.out.printf("SendConnection: Sending packet %d\n", pQueue[dequeueIndex].getId());
			pQueue[dequeueIndex] = null;
			dequeueIndex = (dequeueIndex+1)%pQueue.length;
			return true;
		}//if
	}//dequeuePacket
	
	
}//SendConnection
