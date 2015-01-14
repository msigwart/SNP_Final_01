package simulation;

public class Event {
	
	public static final int EVENT_TYPE_ENQUEUE = 0;
	public static final int EVENT_TYPE_DQUEUE = 1;
	
	private Packet packet;
	
	private long creationTime;
	
	Event(int typeEvent, Packet packet){
		this.packet = packet;
		this.creationTime = System.nanoTime();
	}
	
	public Packet getPacket(){
		return this.packet;
	}
	
	public long getCreationTime(){
		return creationTime;
	}
	
}
