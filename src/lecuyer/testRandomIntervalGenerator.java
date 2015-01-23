package lecuyer;

public class testRandomIntervalGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RandomIntervalGenerator rig1 = new RandomIntervalGenerator(10, 1000);
		RandomIntervalGenerator rig2 = new RandomIntervalGenerator(10, 1000);
		System.out.printf("Created 2 new RandomIntervalGenerator... \n");
		
		for (int i=0; i<100; i++) {
			System.out.printf("Generated new Interval: 1: %4d   2: %4d\n", rig1.generateNewRandomInterval(), rig2.generateNewRandomInterval());
		}//for
		
	}//main

}//testRandomIntervalGenerator
