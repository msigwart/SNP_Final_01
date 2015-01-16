package lecuyer;

public class RandomIntervalGenerator {
	
	
	private RngStream rngStream;
	private int minInterval;
	private int maxInterval;
	
	public RandomIntervalGenerator(int minInterval, int maxInterval) {
		this.rngStream = new RngStream();
		this.minInterval = minInterval;
		this.maxInterval = maxInterval;
	}//Constructor
	
	/**
	 * Generates a new random Interval in microseconds
	 * @return the interval as long
	 */
	public int generateNewRandomInterval() {
		int interval = 0;
		
		interval = rngStream.randInt(minInterval, maxInterval);
		
		return interval;
	}//generateNewRandomInterval
	
	
}//Ran
