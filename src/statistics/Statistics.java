package statistics;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.*;

import simulation.Packet;
import simulation.Priority;
import simulation.SendConnection;
import simulation.Time;


/**
 * This class collects the event of the simulation, saves them into file. It implements the Observer interface.
 * It provides several different statistics measures, such as average queuing time, etc.
 * 
 * @author Bernardo Paulino
 *
 */
public class Statistics implements Observer {
	
	public static final int DEFAULT_DELAY = 200;//Microseconds
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Fields for file I/O
	private String outputFile;
	
	private PrintWriter 	pWriter;
	private FileInputStream	fStream;
	private BufferedReader 	bReader;
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%		
// Different statistics fields
	
	//private ArrayList<Event> eventsNonPrio 	= new ArrayList<Event>();
	private CopyOnWriteArrayList<Event> eventsNonPrio 	= new CopyOnWriteArrayList<Event>();	// TODO--> better concurrent collection (ArrayList not concurrent)
	private long avrgQueueTimeNonPrio;			// average queuing time for non priority packets in nanoseconds
	private int enEventsNonPrio;
	private int deEventsNonPrio;
	private int countNonPrioDelayed;
	private double percNonPrioDelayed;
	
	//private ArrayList<Event> eventsPrio		= new ArrayList<Event>();
	private CopyOnWriteArrayList<Event> eventsPrio		= new CopyOnWriteArrayList<Event>();
	private long avrgQueueTimePrio;				// average queuing time for priority packets in nanoseconds
	private int enEventsPrio;
	private int deEventsPrio;
	private int countPrioDelayed;
	private double percPrioDelayed;
	
	private double percAllDelayed;
	

	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Constructors
	/**
	 * Creates a new Statistics instance for tracing simulation events
	 * @param outputFile path to output file (simulation trace)
	 */
	public Statistics(String outputFile) {
		this.outputFile = outputFile;
		
		// Init non priority fields
		this.avrgQueueTimeNonPrio 	= 0L;
		this.enEventsNonPrio 		= 0;
		this.deEventsNonPrio 		= 0;
		this.countNonPrioDelayed 	= 0;
		this.percNonPrioDelayed		= 0.0;
		
		// Init priority fields
		this.avrgQueueTimePrio 		= 0L;
		this.enEventsPrio 			= 0;
		this.deEventsPrio 			= 0;
		this.countPrioDelayed 		= 0;
		this.percPrioDelayed		= 0.0;
		
		this.percAllDelayed			= 0.0;
		
		initializeStatistics();
	}//Constructor
	
	
	
	/**
	 * Should be called by Constructor.
	 * Initializes various fields of Statistics object. Creates a new file if output file does not exist yet.
	 * Creates PrintWriter to "print" events into output file.
	 * Creates BufferedReader to read events from output file.
	 */
	private void initializeStatistics() {
		try {
			//String newFileName = outputFile + "_" + numFiles + ".txt";
			File statText = new File(this.outputFile);
			
			// Create File
	        if (!statText.exists()) {
	        	if (statText.createNewFile()) {
	        		System.out.printf("Statistics: Created new output file %s\n", outputFile);
	        	} else {
	        		System.out.printf("Statistics: Failed to create output file: %s\n", outputFile);
	        	}//if
	        }//if
	        
	        // Create Print Writer & Buffered Reader
			this.pWriter = new PrintWriter( new FileOutputStream(this.outputFile) );
			this.fStream = new FileInputStream(this.outputFile);
			this.bReader = new BufferedReader( new InputStreamReader(this.fStream) );
		} catch (IOException e) {
			e.printStackTrace();
			System.out.printf("Statistics: Could not create PrintWriter\n");
		}//catch
		
	}//initializeStatistics
	



//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Event Trigger
	
	/**
	 * Triggers an event of the simulation
	 * @param eventType the type of the event that occurred
	 * @param packet the packet the event occurred with
	 */
	public void triggerEvent(int eventType, Packet packet){
		Event event = new Event(eventType, packet);
		//write to file
		writeEventIntoFile(event);
		//do some statistics work
		updateStatistics(event);
	}//createEvent
	
	/**
	 * Writes the specified event into the output file and 
	 * adds the event to corresponding event list for immediate statistics processing
	 * @param event the event to be processed
	 */
	private synchronized void writeEventIntoFile(Event event){
		//System.out.printf("Statistics: Writing event into file...");
		pWriter.printf("%s\n", event.toString());//TODO--> add to event ArrayLists for updateStatistics during simulation
		
		switch (event.getPacket().getPriority()) {
			case PACKET_PRIORITY_HIGH:
				eventsPrio.add(event);
				break;
			case PACKET_PRIORITY_LOW:
				eventsNonPrio.add(event);
				break;
			default:
				break;
			
		}//switch
		
	}//writeEventIntoFile
	
	
	/**
	 * Updates some statistics. Is called when a new event is written to file
	 * Can only update statistics when the events are also added to the corresponding event lists.
	 * @param event the newly arrived event
	 */
	private void updateStatistics(Event event) {
		//do some statistics work
		switch (event.getEventType()) {
		
			case Event.EVENT_TYPE_ENQUEUE:
				switch (event.getPacket().getPriority()) {
					case PACKET_PRIORITY_HIGH:	enEventsPrio++;		break;		//update counter
					case PACKET_PRIORITY_LOW:  	enEventsNonPrio++;	break;		//update counter
					default: break;
				}//switch
				break;
				
			case Event.EVENT_TYPE_DEQUEUE:
				Event enEvent;					// The corresponding enqueue Event
				long queueTime;
				switch (event.getPacket().getPriority()) {
				
					case PACKET_PRIORITY_HIGH:
						deEventsPrio++;		//update counter
						
						//update average queue time
						enEvent = findEvent(Priority.PACKET_PRIORITY_HIGH, event.getPacket().getId(), Event.EVENT_TYPE_ENQUEUE);
						if (enEvent != null) {
							queueTime = event.getCreationTime() - enEvent.getCreationTime();
							avrgQueueTimePrio = (avrgQueueTimePrio + queueTime)/2;
							if ((queueTime/Time.NANOSEC_PER_MICROSEC) > DEFAULT_DELAY) {	//update delayed count
								countPrioDelayed++;
								percPrioDelayed = (double)countPrioDelayed/deEventsPrio;
								percAllDelayed = (double)(countPrioDelayed+countNonPrioDelayed)/(deEventsPrio+deEventsNonPrio);
							}//if
						} else {
							System.out.printf("ERROR ----------------> Couldn't find corresponding enqueue Packet %d\n", event.getPacket().getId());
						}//if
						break;
						
					case PACKET_PRIORITY_LOW:
						deEventsNonPrio++;	//update counter
						
						//update average queue time
						enEvent = findEvent(Priority.PACKET_PRIORITY_LOW, event.getPacket().getId(), Event.EVENT_TYPE_ENQUEUE);
						if (enEvent != null) {
							queueTime = event.getCreationTime() - enEvent.getCreationTime();
							avrgQueueTimeNonPrio = (avrgQueueTimeNonPrio + queueTime)/2;
							if ((queueTime/Time.NANOSEC_PER_MICROSEC) > DEFAULT_DELAY) {	//update delayed count
								countNonPrioDelayed++;
								percNonPrioDelayed = (double)countNonPrioDelayed/deEventsNonPrio;
								percAllDelayed = (double)(countPrioDelayed+countNonPrioDelayed)/(deEventsPrio+deEventsNonPrio);

							}//if
						} else {
							System.out.printf("ERROR ----------------> Couldn't find corresponding enqueue Packet %d\n", event.getPacket().getId());
						}//if
						break;
					default: break;
				}//switch
				
				break;
				
			case Event.EVENT_TYPE_UNKNOWN:
				break;
			default:
				break;
		}//switch
	}//updateStatistics
	

	
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Different update statistics methods as well as helper methods
	
	/**
	 * Helper method which will return an event of specified priority, packet id, and event type
	 * @param priority the priority of the event
	 * @param id the packet id included in event
	 * @param eventType the type of event
	 * @return returns the desired event when it was found in event queues<br>
	 * 		   returns null if it's not found
	 */
	private Event findEvent(Priority priority, int id, int eventType) {
		switch (priority) {
			case PACKET_PRIORITY_HIGH:
				for (Event e: eventsPrio) {
					if (e.getEventType() == eventType && e.getPacket().getId() == id) {
						return e;
					}//if
				}//for
				return null;
			case PACKET_PRIORITY_LOW:
				for (Event e: eventsNonPrio) {
					if (e.getEventType() == eventType && e.getPacket().getId() == id) {
						return e;
					}//if
				}//for
				return null;
			default:
				return null;
		}//switch
	}//findEvent



//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Read methods to read events from an existing output file
	
	/**
	 * Reads all events from the output file and saves them into the corresponding event lists
	 */
	public void readEventsFromFile(){

		try {
			//System.out.printf("Total file size to read (in bytes) : %d\n", fStream.available());
			String line = bReader.readLine();
			
			while(line != null){
				Event e = this.createEventFromString(line);
				
				if (e == null) {
					System.out.printf("Statistics: Could not read event from line --> \"%s\"", line);
				}//if
				else{
					switch (e.getPacket().getPriority()) {
						case PACKET_PRIORITY_HIGH:
							eventsPrio.add(e);
							break;
						case PACKET_PRIORITY_LOW:
							eventsNonPrio.add(e);
							break;
						default:
							break;
					}//switch
				}//else
				line = bReader.readLine();
			}//while
			
		} catch (IOException ex) {
            ex.printStackTrace();
		} finally {
			try {
				if (fStream != null)
					fStream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}//try
		}//finally*/
	}//readEventsFromFile
	
	
	/**TODO--> Belongs into class Event??
	 * Creates an event from a specified string line
	 * @param strEvent the string line
	 * @return the created Event object
	 */
	private Event createEventFromString(String strEvent){
		int eventType;
		Priority packetPriority;
		
		if( strEvent.toLowerCase().contains("DEQUEUE".toLowerCase()) ) {
			eventType = Event.EVENT_TYPE_DEQUEUE;
		}//if
		else if ( strEvent.toLowerCase().contains("ENQUEUE".toLowerCase()) ) {
			eventType = Event.EVENT_TYPE_ENQUEUE;
		} else {
			eventType = Event.EVENT_TYPE_UNKNOWN;
		}//if
		
		//boolean found = Arrays.asList(strEvent.split(" ")).contains("PACKET_PRIORITY_HIGH"); 		//TODO: Change to enum Priority
		
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

	
	
	
	
	@Override
	public void update(Observable o, Object arg) {		//OBSERVER PATTERN
		
		switch ((int)arg) {
			case SendConnection.SERVER_EVENT_TERMINATED:
				pWriter.close();
				System.out.printf("\n######################## Statistics #########################\n");
				//collectStatistics();
				printStatistics();
				break;
			default:
				break;
		}//switch
	}//update
	

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Various statistics methods
	
	/**
	 * Called when the Observable(SendConnection) notifies about its termination
	 * Collects various statistics from the trace (output) file of the simulation
	 */
	public void collectStatistics() {
		System.out.printf("Collecting statistics...\n");
		readEventsFromFile();
		//printEvents();
		avrgQueueTimePrio = getAverageQueueTime(Priority.PACKET_PRIORITY_HIGH);
		avrgQueueTimeNonPrio = getAverageQueueTime(Priority.PACKET_PRIORITY_LOW);
	}//collectingStatistics


	/**
	 * Return the average time spent by packet in the queue
	 * @return returns the average queue time in nanoseconds
	 */
	private long getAverageQueueTime(Priority priority) {
		long sumQueueTime = 0L;
		int eventCount = 0;
		
		switch (priority) {
		
			case PACKET_PRIORITY_HIGH:
				for (Event en: eventsPrio) {
					if (en.getEventType() == Event.EVENT_TYPE_ENQUEUE) {
						for (Event de: eventsPrio) {
							if (de.getEventType() == Event.EVENT_TYPE_DEQUEUE && de.getPacket().getId() == en.getPacket().getId()) {
								sumQueueTime += (de.getCreationTime() - en.getCreationTime());
								eventCount++;
								break;
							}//if
						}//for
					}//if
				}//for
				break;
				
			case PACKET_PRIORITY_LOW:
				for (Event en: eventsNonPrio) {
					if (en.getEventType() == Event.EVENT_TYPE_ENQUEUE) {
						for (Event de: eventsNonPrio) {
							if (de.getEventType() == Event.EVENT_TYPE_DEQUEUE && de.getPacket().getId() == en.getPacket().getId()) {
								sumQueueTime += (de.getCreationTime() - en.getCreationTime());
								eventCount++;
								break;
							}//if
						}//for
					}//if
				}//for
				break;
				
			default:
				break;
		}//switch
		
		if (eventCount == 0) {
			return 0;
		} else return sumQueueTime/eventCount;
		
	}//getAverageQueueTime
	
	
	/**
	 * Counts the number of events with a certain priority
	 * @param priority the priority of the events to count
	 * @return returns the number of events
	 */
	private int getEventCount(Priority priority, int eventType) {
		int count = 0;
		switch (priority) {
			case PACKET_PRIORITY_HIGH:
				for (Event e: eventsPrio) {
					if (e.getEventType() == eventType) {
						count++;
					}//if
				}//for
				break;
			case PACKET_PRIORITY_LOW:
				for (Event e: eventsNonPrio) {
					if (e.getEventType() == eventType) {
						count++;
					}//if
				}//for
				break;
			default:
				break;
		}//switch

		return count;
	}//getEventCount
	
	public void countAllEvents() {
		// High Priority
		enEventsPrio = getEventCount(Priority.PACKET_PRIORITY_HIGH, Event.EVENT_TYPE_ENQUEUE);
		deEventsPrio = getEventCount(Priority.PACKET_PRIORITY_HIGH, Event.EVENT_TYPE_DEQUEUE);
		
		// Low Priority
		enEventsNonPrio = getEventCount(Priority.PACKET_PRIORITY_LOW, Event.EVENT_TYPE_ENQUEUE);
		deEventsNonPrio = getEventCount(Priority.PACKET_PRIORITY_LOW, Event.EVENT_TYPE_DEQUEUE);
	}//countAllEvents
	
	
	
	
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Various print methods
	/**
	 * Prints all events contained in output file.
	 */
	public void printEvents() {
		System.out.printf("\nHIGH PRIORITY EVENTS:\n");
		for (Event e: eventsPrio) {
			System.out.printf("%s\n", e.toString());
		}//for
		System.out.printf("\nLOW PRIORITY EVENTS:\n");
		for (Event e: eventsNonPrio) {
			System.out.printf("%s\n", e.toString());
		}//for
	}//printEvents
	
	/**
	 * Prints all statistics about simulation run in a nice way
	 */
	public void printStatistics() {
		
		System.out.printf("\n=== Event Counts ================\n");
		System.out.printf("\n\t\t\t ENQUEUE:\t DEQUEUE:\t|    TOTAL:\n" +
				"--------------------------------------------------------+-----------\n");
		for (Priority p: Priority.values()) {

			switch (p) {
				case PACKET_PRIORITY_HIGH:
					System.out.printf("High Priority Events:\t%9d\t%9d\t| %9d\n", enEventsPrio, deEventsPrio, enEventsPrio+deEventsPrio);
					break;
				case PACKET_PRIORITY_LOW:
					System.out.printf("Low Priority Events:\t%9d\t%9d\t| %9d\n", enEventsNonPrio, deEventsNonPrio, enEventsNonPrio+deEventsNonPrio);
					break;
				default:
					break;
			}//switch
		}//for
		System.out.printf("========================================================+===========\n" +
						  "Total Events \t\t%9d\t%9d\t| %9d\n", enEventsPrio+enEventsNonPrio, deEventsPrio+deEventsNonPrio, eventsNonPrio.size()+eventsPrio.size());
		
		System.out.printf("\n=== Average Queue Time =============\n");
		System.out.printf("High Priority Packets:\t%9d µs\n", avrgQueueTimePrio/Time.NANOSEC_PER_MICROSEC);
		System.out.printf("Low Priority Packets:\t%9d µs\n", avrgQueueTimeNonPrio/Time.NANOSEC_PER_MICROSEC);
		System.out.printf("Total:\t\t\t%9d µs\n", ( (avrgQueueTimeNonPrio+avrgQueueTimePrio)/2) / Time.NANOSEC_PER_MICROSEC );	//TODO only if both != 0
		// If 'µ' is not displayed correctly, go to Eclipse > Preferences > General > Workspace > Text File Encoding
		
		printPacketStatistics();
		
	}//printStatistics
	
	
	
	public void printPacketStatistics() {
		System.out.printf("\n=== Packet Counts =============\n", DEFAULT_DELAY);
		System.out.printf("\nNumber of packets with a delay of more than %d\n", DEFAULT_DELAY);
		System.out.printf("High Priority Packets:\t%9d\n", countPrioDelayed);		
		System.out.printf("Low Priority Packets:\t%9d\n", countNonPrioDelayed);
		
		System.out.printf("\n\t\t\t Delayed:\t Total:\t|    Delayed percentage:\n" +
				"--------------------------------------------------------+-----------\n");
		for (Priority p: Priority.values()) {

			switch (p) {
				case PACKET_PRIORITY_HIGH:
					System.out.printf("High Priority Packets:\t%9d\t%9d\t| %9f\n", countPrioDelayed, deEventsPrio, percPrioDelayed*100);
					break;
				case PACKET_PRIORITY_LOW:
					System.out.printf("Low Priority Packets:\t%9d\t%9d\t| %9f\n", countNonPrioDelayed, deEventsNonPrio, percNonPrioDelayed*100);
					break;
				default:
					break;
			}//switch
		}//for
		System.out.printf("========================================================+===========\n" +
						  "Total Packets \t\t%9d\t%9d\t| %9f\n", countPrioDelayed+countNonPrioDelayed, 
						  										deEventsPrio+deEventsNonPrio, 
						  										percAllDelayed*100);
	}//printPacketStatistics


	
}//Statistics
