package simulation;

public class SendConnection extends Thread {
	
	private volatile boolean running = true;
	private final int runTime;		//run time in second of SendConnection
	
	/* Constructors */	
	SendConnection() {
		super();
		this.runTime = 10;
	}//Constructor
	
	SendConnection(int runTime) {
		super();
		this.runTime = runTime;
	}//Constructor
	
	
	
	// Method to terminate thread
	public void terminate() { running = false; }
	
	// Run method of thread
	public void run() {
		if (Thread.currentThread() != this) throw new IllegalStateException();
		
		while (running) {
			System.out.println("Send Connection started...");
			System.out.println("SendConnection: Doing some work.");
			running = false;
		}//while
		System.out.println("SendConnection has terminated...");
	}//run
	
	
}//SendConnection
