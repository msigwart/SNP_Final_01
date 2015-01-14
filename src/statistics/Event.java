package statistics;

import simulation.Packet;

public class Event {
	
	public static final int EVENT_TYPE_ENQUEUE = 0;
	public static final int EVENT_TYPE_DQUEUE = 1;
	private final int eventType;
	
	private Packet packet;
	
	private long creationTime;
	
	
	Event(int eventType, Packet packet){
		this.packet = packet;
		this.eventType= eventType;
		this.creationTime = System.nanoTime();
	}
	
	public Packet getPacket(){
		return packet;
	}
	
	public long getCreationTime(){
		return creationTime;
	}
	
	public int getEventType(){
		return eventType;
	}
	
	public String toString(){
		if(eventType == EVENT_TYPE_DQUEUE)
			return "DQUEUE: " + "Packet " + packet.getId();
		else{
			return "ENQUEUE: "  + "Packet " + packet.getId();
		}
		
	}
	
}
