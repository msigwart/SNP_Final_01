package statistics;

import simulation.Packet;

public class Event {
	
	// Global event types
	public static final int EVENT_TYPE_ENQUEUE = 0;
	public static final int EVENT_TYPE_DQUEUE = 1;
	
	
	// Private members
	private final int eventType;
	private final Packet packet;
	private final long creationTime;	//in nanoseconds
	
	
	Event(int eventType, Packet packet){
		this.eventType= eventType;
		this.packet = packet;
		this.creationTime = System.nanoTime();
	}//Constructor
	
	
	/* Getters and Setters */
	public Packet getPacket(){
		return packet;
	}//getPacket
	
	public long getCreationTime(){
		return creationTime;
	}//getCreationTime
	
	public int getEventType(){
		return eventType;
	}//getEventType
	
	
	public String toString(){
		if(eventType == EVENT_TYPE_DQUEUE)
			return "DQUEUE: " + "Packet " + packet.getId();
		else{
			return "ENQUEUE: "  + "Packet " + packet.getId();
		}//if
		
	}//toString
	
}//Event
