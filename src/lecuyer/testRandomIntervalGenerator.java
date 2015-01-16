package lecuyer;

public class testRandomIntervalGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RandomIntervalGenerator rig = new RandomIntervalGenerator(100, 1000);
		System.out.printf("Created new RandomIntervalGenerator... \n");
		
		for (int i=0; i<100; i++) {
			System.out.printf("Generated new Interval: %d\n", rig.generateNewRandomInterval());
		}//for
		
	}//main

}//testRandomIntervalGenerator
