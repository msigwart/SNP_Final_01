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
		
		SendConnection sc = new SendConnection();
		sc.start();
		
	}//main

}//Simulation
