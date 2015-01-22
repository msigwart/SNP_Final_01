package statistics;

import java.util.ArrayList;

import simulation.Client;
import simulation.Packet;
import simulation.Priority;

public class Event {
	
	// Global event types
	public static final int EVENT_TYPE_ENQUEUE = 0;
	public static final int EVENT_TYPE_DEQUEUE = 1;
	public static final int NUMBER_OF_EVENTS   = 2;
	public static final int EVENT_TYPE_UNKNOWN = -1;
	
	
	// Private members
	private final int eventType;
	private final Packet packet;
	private final long creationTime;	//in nanoseconds
	
	
	
	/**
	 * Creates an event from a specified string line
	 * @param strEvent the string line
	 * @return the created Event object
	 */
	public static Event createEventFromString(String strEvent){
		int eventType;
		Priority packetPriority;
		
		if( strEvent.toLowerCase().contains("DEQUEUE".toLowerCase()) ) {
			eventType = EVENT_TYPE_DEQUEUE;
		}//if
		else if ( strEvent.toLowerCase().contains("ENQUEUE".toLowerCase()) ) {
			eventType = EVENT_TYPE_ENQUEUE;
		} else {
			eventType = EVENT_TYPE_UNKNOWN;
		}//if
		
		
		if ( strEvent.toLowerCase().contains("PACKET_PRIORITY_HIGH".toLowerCase()) ) {
			packetPriority = Priority.PACKET_PRIORITY_HIGH;
		} 
		else {
			packetPriority = Priority.PACKET_PRIORITY_LOW;
		}//if
		
		
		String []newDigits = (strEvent.replaceAll("[^0-9. ]", "")).split(" ",-1); //removes non numeric chars and slipts the String 
		ArrayList <String> parts = new ArrayList<String>();
		
		for(int i = 0; i < newDigits.length; i++){
			if(!newDigits[i].isEmpty()) // if the string is not empty adds a new digit to the array of parts
				parts.add(newDigits[i]);
		}//for

		return new Event( eventType, Long.parseLong(parts.get(0)), new Packet( Integer.parseInt(parts.get(1)), packetPriority ) );
		
	}//addEventFromString
	
	
	
	
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
	
	public Event(int eventType, long creationTime, Packet packet){
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
		switch (eventType) {
			case EVENT_TYPE_DEQUEUE:
				return "DEQUEUE  at (" + creationTime + "): " + "Packet " + packet.getId() + " - Priority " + packet.getPriority();
			case EVENT_TYPE_ENQUEUE:
				return "ENQUEUE at (" + creationTime + "): " + "Packet " + packet.getId() + " - Priority " + packet.getPriority();
			default:
				return "UNKNOWN at (" + creationTime + "): " + "Packet " + packet.getId() + " - Priority " + packet.getPriority();
		}//switch

	}//toString
	
	
}//Event
