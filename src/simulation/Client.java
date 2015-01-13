package simulation;

public class Client extends Thread {

	private volatile boolean running = true;
	private volatile SendConnection sendConnection = null;
	private final int numOfPackets;
	
	Client(int numPackets) {
		super();
		this.numOfPackets = numPackets;
	}//Constructor
	
	
	public void connectToSender(SendConnection sender) {
		this.sendConnection = sender;
	}//connectToSender
	
	
	
	public void run() {
		if (Thread.currentThread() != this) throw new IllegalStateException();
		while (running) {
			System.out.println("Client started...");
			
			if (sendConnection != null) {				//wait for connection to sender
				System.out.print("Client: Connected to sendConnection");
				for (int i=0; i<numOfPackets; i++) {	//send number of Packets
					
				}//for
			}//if
			
		}//while
	}//run
	
}//Client
