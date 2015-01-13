package simulation;


public class Simulation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.printf("Hello Simulator 123\n");

		long t1, t2;
		t1 = Time.getTimeStamp();
		System.out.printf( "the time is: %s\n", Time.getTimeStampString());
		t2 = Time.getTimeStamp();
		System.out.printf( "MilliSeconds# %d\n", t2-t1 );
		
		SendConnection sc = new SendConnection(20, 100);
		sc.start();
		
		int clientId = 0;
		Client cl = new Client(clientId++, 10, 1000);
		cl.connectToSender(sc);
		cl.start();
		
		/*
		Client cl[] = new Client[10];
		
		// Creation of clients
		for (int i=0; i<cl.length; i++) {
			cl[i] = new Client(clientId++, 10);
			cl[i].connectToSender(sc);					//Connect client to send connection
		}//for
		
		// Start of clients
		for (int i=0; i<cl.length; i++) {
			cl[i].start();
		}//for
		*/
		
	}//main

}//Simulation
