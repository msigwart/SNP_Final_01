package statistics;

public class DelayStat {

	private final int delay;	//in microseconds
	private int countDelayed;
	private double percentDelayed;
	
	
	public DelayStat(int delay) {
		this.delay 			= delay;
		this.countDelayed 	= 0;
		this.percentDelayed = 0.0;
		
	}//Constructor


	public int getDelay() {
		return delay;
	}//getDelay

	public int getCountDelayed() {
		return countDelayed;
	}//getTotalDelayed

	public double getPercentDelayed() {
		return percentDelayed;
	}//getPercDelayed

	
	public void setPercentDelayed(double percDelayed) {
		this.percentDelayed = percDelayed;
	}//setPercDelayed

	public void setDelayCount(int countDelayed) {
		this.countDelayed = countDelayed;
	}//addDelayCount

	public void addDelayCount(int countDelayed) {
		this.countDelayed += countDelayed;
	}//addDelayCount
	
	
	
	
}//DelayStat
