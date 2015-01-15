package statistics;

import simulation.Client;
import simulation.Packet;

public class Event {
	
	// Global event types
	public static final int EVENT_TYPE_ENQUEUE = 0;
	public static final int EVENT_TYPE_DQUEUE = 1;
	
	
	// Private members
	private final int eventType;
	private final Packet packet;
	private final long creationTime;	//in nanoseconds
	
	
	/**
	 * Constructor for event. Creates an event and captures 
	 * the time of event occurrence.
	 * @param eventType the type of the event
	 * @param packet the packet the event occurred with
	 */
	Event(int eventType, Packet packet){
		this.eventType = eventType;
		this.packet = packet;
		this.creationTime = System.nanoTime();
	}//Constructor
	
	Event(int eventType, long creationTime, Packet packet){
		this.eventType = eventType;
		this.creationTime = creationTime;
		this.packet = packet;
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
	
	
	@Override
	public String toString(){
		if(eventType == EVENT_TYPE_DQUEUE)
			return "DQUEUE  at (" + creationTime + "): " + "Packet " + packet.getId() + " - Priority " + packet.getPriority();
		else{
			return "ENQUEUE at (" + creationTime + "): " + "Packet " + packet.getId() + " - Priority " + packet.getPriority();
		}//if	
	}//toString
	
	
}//Event
