package statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventList {
	
	private Map<Integer, ArrayList<Event>> 	events;
	private long 			 			avrgQueueTime;			// average queuing time in nanoseconds
	private int 			 			enqueueEventCount;
	private int 			 			dequeueEventCount;
	private int 			 			countDelayed;
	private double 			 			percentDelayed;
	
	
	public EventList() {
		this.events 			= new HashMap<Integer, ArrayList<Event>>();
		this.avrgQueueTime 		= 0L;
		this.enqueueEventCount 	= 0;
		this.dequeueEventCount 	= 0;
		this.countDelayed		= 0;
		this.percentDelayed		= 0.0;
	}//Constructor


//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%		
// Getters for stats
	public Map<Integer, ArrayList<Event>> getEvents() {
		return events;
	}//getEvents
	
	public long getAvrgQueueTime() {
		return avrgQueueTime;
	}//getAvrgQueueTime

	public int getEventCount(int eventType) {
		if (eventType == Event.EVENT_TYPE_ENQUEUE) {
			return getEnqueueEventCount();
		} else if (eventType == Event.EVENT_TYPE_DEQUEUE) {
			return getDequeueEventCount();
		} else {
			System.out.printf("ERROR -----> UNKNOWN EVENT %d\n", eventType);
			return -1;
		}//if
	}//getEventCount
	
	public int getEnqueueEventCount() {
		return enqueueEventCount;
	}//getEnqueueEventCount

	public int getDequeueEventCount() {
		return dequeueEventCount;
	}//getDequeueEventCount
	

	public int getCountDelayed() {
		return countDelayed;
	}//getCountDelayed

	public double getPercentDelayed() {
		return percentDelayed;
	}//getPercentDelayed
	
	
	
	public void setAvrgQueueTime(long avrgQueueTime) {
		this.avrgQueueTime = avrgQueueTime;
	}//setAvrgQueueTime

	public void setEnqueueEventCount(int count) {
		this.enqueueEventCount = count;
	}//setEnqueueEventCount

	public void setDequeueEventCount(int count) {
		this.dequeueEventCount = count;
	}//incDequeueEventCount
	
	public void incEnqueueEventCount() {
		this.enqueueEventCount++;
	}//setEnqueueEventCount

	public void incDequeueEventCount() {
		this.dequeueEventCount++;
	}//incDequeueEventCount

	public void incCountDelayed() {
		this.countDelayed++;
	}//incCountDelayed

	public void setPercentDelayed(double percentDelayed) {
		this.percentDelayed = percentDelayed;
	}//setPercentDelayed
	

	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%		
// Wrapper methods for HashMap and List

	public void add(Event event) {
		this.events.get(event.getPacket().getId()).add(event.getEventType(), event);
	}//add
	
	
	public List<Event> remove(int key) {
		return this.events.remove(key);
	}//remove
	
	public List<Event> get(int key) {
		return this.events.get(key);
	}//get
	
	public void clear() {
		this.events.clear();
	}//clear
	
	public void put(Integer key, ArrayList<Event> list) {
		this.events.put(key, list);
	}//events
	
	
	/**
	 * Helper method which will return an event of specified priority, packet id, and event type
	 * @param packetId the packet id included in event
	 * @param eventType the type of event
	 * @return returns the desired event when it was found in event queues<br>
	 * 		   returns null if it's not found
	 */
	public Event retrieveEvent(int packetId, int eventType) {
		ArrayList<Event> pEvents = this.events.get(packetId);
		Event ev = null;
		try {
			ev = pEvents.get(eventType);
		} catch (NullPointerException ne) {
			return null;
		}//catch
		return ev;//pEvents.get(eventType);
	}//retrieveEvent
	
	
	
}//EventList
