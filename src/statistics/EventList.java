package statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simulation.Time;


public class EventList {
	
	private Map<Integer, ArrayList<Event>> 	events;
	private long 			 			avrgQueueTime;			// average queuing time in nanoseconds
	private int 			 			enqueueEventCount;
	private int 			 			dequeueEventCount;
	private int 			 			countDelayed;
	private double 			 			percentDelayed;
	private boolean						hasAverage;
	
	



	public EventList() {
		this.events 			= new HashMap<Integer, ArrayList<Event>>();
		this.avrgQueueTime 		= 0L;
		this.enqueueEventCount 	= 0;
		this.dequeueEventCount 	= 0;
		this.countDelayed		= 0;
		this.percentDelayed		= 0.0;
		this.hasAverage			= false;
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
	
	
	
	public boolean isAverageSet() {
		return hasAverage;
	}//isAverageSet


	public void setHasAverage(boolean hasAverage) {
		this.hasAverage = hasAverage;
	}//setHasAverage
	
	
	
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
	
	
	public Event retrieveCorrespondingEvent(int packetId, int eventType) {
		switch (eventType) {
			case Event.EVENT_TYPE_ENQUEUE:
				return retrieveEvent(packetId, Event.EVENT_TYPE_DEQUEUE);
			case Event.EVENT_TYPE_DEQUEUE:
				return retrieveEvent(packetId, Event.EVENT_TYPE_ENQUEUE);
			default:
				return null;
		}//switch
	}//retrieveCorrespondingEvent
	
	
	
	/**
	 * Updates the averageQueueTime
	 * @param event the event that causes the update
	 */
	public void updateAvrgQueueTime(Event event) {
		Event correspondingEvent = null;					// The corresponding enqueue Event
		long queueTime;
		long newAvrgTime;
		double newPercDelayed;
		
		//EventList el = eventLists.get(event.getPacket().getPriority());
		//System.out.printf("%s old average: %d\n", event.getPacket().getPriority(), el.getAvrgQueueTime());
		for (int i=0; i<Event.NUMBER_OF_EVENTS; i++) {
			if (i==event.getEventType()) {
				correspondingEvent = retrieveCorrespondingEvent( event.getPacket().getId(), i );
				
				if (correspondingEvent != null) {
					queueTime = event.getCreationTime() - correspondingEvent.getCreationTime();
					//System.out.printf("Packet %d -- %s new queueTime: %d\n", event.getPacket().getId(), event.getPacket().getPriority(), queueTime);
					if (!isAverageSet()) {
						setAvrgQueueTime(queueTime);		// First average calculation --> don't divide by 2!!!
						System.out.printf("FIRST %s new average: %d\n", event.getPacket().getPriority(), queueTime);
						setHasAverage(true);
					} else {
						newAvrgTime = ((getAvrgQueueTime() + queueTime)/2);
						setAvrgQueueTime(newAvrgTime);
						//System.out.printf("%s new average: %d\n", event.getPacket().getPriority(), newAvrgTime);
					}//if
					
					if ((queueTime/Time.NANOSEC_PER_MICROSEC) > Statistics.DEFAULT_DELAY) {	//update delayed count
						incCountDelayed();
						newPercDelayed = (double)getCountDelayed()/getDequeueEventCount();
						setPercentDelayed(newPercDelayed);
						//percAllDelayed = (double)(countPrioDelayed+countNonPrioDelayed)/(deEventsPrio+deEventsNonPrio);	//TODO update!!!
					}//if
				}//if
			}//if
		}//for
		
		
	}//updateAvrgQueueTimes
	
	
	
}//EventList
