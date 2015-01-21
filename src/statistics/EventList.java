package statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simulation.Priority;
import simulation.Time;


public class EventList {
	
	private Priority						priority;
	private Map<Integer, ArrayList<Event>> 	events;
	private ArrayList<Long>					queueTimes;
	private long 			 				avrgQueueTime;			// average queuing time in nanoseconds
	private int 			 				enqueueEventCount;
	private int 			 				dequeueEventCount;
	private int 			 				countDelayed;
	private double 			 				percentDelayed;
	private boolean							hasAverage;
	
	



	public EventList(Priority p) {
		this.events 			= new HashMap<Integer, ArrayList<Event>>();
		this.queueTimes			= new ArrayList<Long>();
		this.avrgQueueTime 		= 0L;
		this.enqueueEventCount 	= 0;
		this.dequeueEventCount 	= 0;
		this.countDelayed		= 0;
		this.percentDelayed		= 0.0;
		this.hasAverage			= false;
		this.priority			= p;
	}//Constructor


//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%		
// Getters for stats
	public Map<Integer, ArrayList<Event>> getEvents() {
		return events;
	}//getEvents
	
	
	public ArrayList<Long> getQueueTimes() {
		return queueTimes;
	}//getQueueTimes
	
	
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
	
	public void set(Event event) {
		this.events.get(event.getPacket().getId()).set(event.getEventType(), event);
	}//set
	
	/*public void addQueueTime(Long queueTime) {
		this.queueTimes.add(queueTime);
	}//addQueueTime
	*/
	
	public List<Event> remove(int key) {
		return this.events.remove(key);
	}//remove
	
	public List<Event> get(int key) {
		return this.events.get(key);
	}//get
	
	public int size() {
		return this.events.size();
	}//size
	
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
	
	
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%		
// Update EventList statistics
	
	public void updateEventListStats(Event event) {
		updateEventCounter(event);
		
	}//updateEventListStats
	
	
	public void addQueueTime(int packetId) {
		long queueTime;
		Event de, ee;
		ee = retrieveEvent(packetId, Event.EVENT_TYPE_ENQUEUE);
		if (ee == null) {
			System.out.printf("ERROR: %s Packet %d has no ENQUEUE event\n", priority, packetId);
			return;
		} else {
			de = retrieveEvent(packetId, Event.EVENT_TYPE_DEQUEUE);
			if (de == null) {
				return;
				//System.out.printf("WARNING: %s Packet %d has not been sent yet\n", priority, packetId);
			} else {
				queueTime = de.getCreationTime() - ee.getCreationTime();
				this.queueTimes.add(queueTime);
				updateAvrgQueueTime(queueTime);
				//System.out.printf("Packet %d: QueueTime = %d\n", packetId, queueTime);
			}//if
		}//if

	}//addQueueTime
	
	
	/**
	 * Updates the event counters
	 * @param event
	 */
	private void updateEventCounter(Event event) {
		switch (event.getEventType()) {
			
			case Event.EVENT_TYPE_ENQUEUE:
				incEnqueueEventCount();
				break;
			
			case Event.EVENT_TYPE_DEQUEUE:
				incDequeueEventCount();
				break;
		}//switch
	}//updateEventCounter
	
	
	/**
	 * Calculates and returns the queue time of a packet. 
	 * @param event the event to get the queueTime from, should be event DEUQUE
	 * @return returns the queue time of the packet from the event<br>
	 * returns -1 if the corresponding EN/DEQUEUE event could not be found
	 */
	private long getQueueTime(Event event, int eventType) {
		Event correspondingEvent = null;
		long queueTime;
		
		for (int i=0; i<Event.NUMBER_OF_EVENTS; i++) {
			if (i==eventType) {
				correspondingEvent = retrieveCorrespondingEvent( event.getPacket().getId(), i );
			
				if (correspondingEvent != null) {
					queueTime = event.getCreationTime() - correspondingEvent.getCreationTime();
					return queueTime;
				}//if
			}//if
		}//for
		System.out.printf("WARNING: Could not get queueTime of Packet %d\n", event.getPacket().getId());
		return -1;
	}//getQueueTime
	
	
	/**
	 * Updates the averageQueueTime
	 * @param event the event that causes the update
	 */
	private void updateAvrgQueueTime(long queueTime) {
		long newAvrgTime;
		//double newPercDelayed;
		
		if (!isAverageSet()) {
			setAvrgQueueTime(queueTime);		// First average calculation --> don't divide by 2!!!
			System.out.printf("FIRST  new average: %d\n", queueTime);
			setHasAverage(true);
		} else {
			newAvrgTime = ((getAvrgQueueTime() + queueTime)/2);
			setAvrgQueueTime(newAvrgTime);
			//System.out.printf("%s new average: %d\n", event.getPacket().getPriority(), newAvrgTime);
		}//if
					
//					if ((queueTime/Time.NANOSEC_PER_MICROSEC) > Statistics.DEFAULT_DELAY) {	//update delayed count
//						incCountDelayed();
//						newPercDelayed = (double)getCountDelayed()/getDequeueEventCount();
//						setPercentDelayed(newPercDelayed);
//						//percAllDelayed = (double)(countPrioDelayed+countNonPrioDelayed)/(deEventsPrio+deEventsNonPrio);	//TODO update!!!
//					}//if

		
	}//updateAvrgQueueTimes
	
	public void calculateDelayedCount(double delay) {
		//this.countDelayed 	= 0;
		//this.percentDelayed = 0.0;
		double newPercDelayed = 0.0;
		for (long qTime: queueTimes) {
			if ((qTime/Time.NANOSEC_PER_MICROSEC) > delay) {	//update delayed count
				incCountDelayed();
			}//if
		}//for
		newPercDelayed = (double)countDelayed/dequeueEventCount;
		setPercentDelayed(newPercDelayed);
	}//calculateDelayedCount
	
	
	
}//EventList
