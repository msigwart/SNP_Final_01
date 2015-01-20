package statistics;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.*;


import simulation.Packet;
import simulation.Priority;
import simulation.SendConnection;
import simulation.Simulation;
import simulation.Time;


/**
 * This class collects the event of the simulation, saves them into file. It implements the Observer interface.
 * It provides several different statistics measures, such as average queuing time, etc.
 * 
 * @author Bernardo Paulino, Marten Sigwart
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
	
	private EnumMap<Priority, EventList> eventLists;
	
	private double percAllDelayed;
	private double averageQueueTime;		//in microseconds
	

	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Constructors
	/**
	 * Creates a new Statistics instance for tracing simulation events
	 * @param outputFile path to output file (simulation trace)
	 */
	public Statistics(String outputFile) {
		this.outputFile = outputFile;
		this.eventLists = new EnumMap<>(Priority.class);
		for (Priority p: Priority.values()) {
			this.eventLists.put(p, new EventList());
		}//for
		this.percAllDelayed			= 0.0;
		this.averageQueueTime		= 0.0;
		
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
	public void triggerEvent(Event event){
		//Event event = new Event(eventType, time, packet);
		//write to file
		writeEventIntoFile(event);
		//do some statistics work
		//updateStatistics(event);			//DON'T!!! Too expensive, takes too long
	}//createEvent
	
	/**
	 * Writes the specified event into the output file and 
	 * adds the event to corresponding event list for immediate statistics processing
	 * @param event the event to be processed
	 */
	private synchronized void writeEventIntoFile(Event event){
		//System.out.printf("Statistics: Writing event into file...");
		pWriter.printf("%s\n", event.toString());
		
	}//writeEventIntoFile
	
	
	/**
	 * Updates some statistics. Is called when a new event is written to file
	 * Can only update statistics when the events are also added to the corresponding event lists.
	 * @param event the newly arrived event
	 */
	private void updateStatistics(Event event) {
		//do some statistics work
		
		updateEventCounter(event);
		if (event.getEventType() == Event.EVENT_TYPE_DEQUEUE) {
			updateEventListStats(event);
		}//if
		
	}//updateStatistics
	
	
	
	private void updateEventCounter(Event event) {
		switch (event.getEventType()) {
			
			case Event.EVENT_TYPE_ENQUEUE:
				eventLists.get(event.getPacket().getPriority()).incEnqueueEventCount();
				break;
			
			case Event.EVENT_TYPE_DEQUEUE:
				eventLists.get(event.getPacket().getPriority()).incDequeueEventCount();
				break;
		}//switch
	}//updateEventCounter
	
	
	/*
	 * 
	 */
	public void updateEventListStats(Event event) {

		EventList el = eventLists.get(event.getPacket().getPriority());
		el.updateAvrgQueueTime(event);
		
		
	}//updateAvrgQueueTimes
	

	
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Different update statistics methods as well as helper methods



//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Read methods to read events from an existing output file
	
	/**
	 * Reads all events from the output file and saves them into the corresponding event lists
	 */
	public void readEventsFromFile(){

		try {
			int lines = 0;
			int progressStep;
			int nextProgress;
			int progressCounter;
			
			//Count lines
			while (bReader.readLine() != null) {
				lines++;
			}//while
			bReader.close();
			fStream.close();
			
			fStream = new FileInputStream(this.outputFile);
			bReader = new BufferedReader(new InputStreamReader(fStream));
			//System.out.printf("Total file size to read (in bytes) : %d\n", fStream.available());
			System.out.printf("Total lines to read : %d\n", lines);
			
			//Calculate progress display
			progressStep = lines/100;
			nextProgress = progressStep;
			progressCounter = 0;
			
			// Read events
			lines = 0;
			String line = bReader.readLine();
			System.out.printf("Reading tracefile...\n");
			while(line != null){
				Event e = this.createEventFromString(line);
				if (e == null) {
					System.out.printf("Statistics: Could not read event from line --> \"%s\"", line);
				}//if
				else {
					try {
						if ( eventLists.get(e.getPacket().getPriority()).get(e.getPacket().getId()) == null) {
							ArrayList<Event> newEventList = new ArrayList<Event>();
							for (int i=0; i<Event.NUMBER_OF_EVENTS; i++) {
								newEventList.add(null);
							}//for
							newEventList.add(e.getEventType(), e);
							eventLists.get(e.getPacket().getPriority()).put(e.getPacket().getId(), newEventList);
						} else {
							eventLists.get( e.getPacket().getPriority() ).get( e.getPacket().getId() ).add( e.getEventType(), e );
						}//if
						updateStatistics(e);

					} catch (NullPointerException ne) {
						System.out.printf("Caught expception\n");
						continue;
					}//catch
				}//else
				if (lines++ >= nextProgress) {
					System.out.printf("%d Percent...\n", progressCounter++);
					nextProgress += progressStep;
				}//if
				line = bReader.readLine();
			}//while
			
		} catch (IOException ex) {
            ex.printStackTrace();
		} finally {
			try {
				bReader.close();
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
		
		if (arg instanceof Event) {
			updateStatistics((Event)arg);
		} else {
			switch ((int)arg) {
				case SendConnection.SERVER_EVENT_TERMINATED:
					pWriter.close();
					collectStatistics();
					printStatistics();
					break;
				default:
					break;
			}//switch
		}//if
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
		//avrgQueueTimePrio = getAverageQueueTime(Priority.PACKET_PRIORITY_HIGH);
		//avrgQueueTimeNonPrio = getAverageQueueTime(Priority.PACKET_PRIORITY_LOW);
	}//collectingStatistics


	/**
	 * Return the average time spent by packet in the queue
	 * @return returns the average queue time in nanoseconds
	 */
	private long getAverageQueueTime(Priority p) {
		long sumQueueTime = 0L;
		int eventCount = 0;
		ArrayList<Event> events;
		
		for (int i=0; i<Packet.id; i++) {
			events = eventLists.get(p).getEvents().get(i);
			sumQueueTime += events.get(Event.EVENT_TYPE_DEQUEUE).getCreationTime() - events.get(Event.EVENT_TYPE_ENQUEUE).getCreationTime();
			eventCount++;
		}//for
		
		if (eventCount == 0) {
			return 0;
		} else return sumQueueTime/eventCount;
		
	}//getAverageQueueTime
	
	
	/**
	 * Counts the number of events with a certain priority
	 * @param priority the priority of the events to count
	 * @return returns the number of events
	 */
	private int getEventCount(Priority p, int eventType) {
		int count = 0;
		for (int i=0; i<Packet.id; i++) {
			if (eventLists.get(p).get(i).get(eventType) != null) {
				count++;
			}//if
		}//for
		
		return count;

	}//getEventCount
	
	public void countAllEvents() {
		
		for (Priority p: Priority.values()) {
			eventLists.get(p).setEnqueueEventCount(getEventCount(p, Event.EVENT_TYPE_ENQUEUE));
			eventLists.get(p).setDequeueEventCount(getEventCount(p, Event.EVENT_TYPE_DEQUEUE));
		}//for

	}//countAllEvents
	
	
	
	
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Various print methods
	/**
	 * Prints all events contained in output file.
	 */
	public void printEvents() {
		for (Priority p: Priority.values()) {
			System.out.printf("\n%s:\n", p.toString());
			for (int i=0; i<Packet.id; i++) {
				for (Event e: eventLists.get(p).get(i)) {
					System.out.printf("%s\n", e.toString());
				}//for
			}//for
		}//for
	}//printEvents
	
	/**
	 * Prints all statistics about simulation run in a nice way
	 */
	public void printStatistics() {
		
		System.out.printf("\n############### Statistics ##########################################\n");
		System.out.printf("SendTime Server: %d µs/Packet --- %d ns/Packet\n", Simulation.MICSECONDS_PER_PACKET, Simulation.MICSECONDS_PER_PACKET*Time.NANOSEC_PER_MICROSEC);
		printEventStatistics();
		printQueueStatistics();
		printPacketStatistics();
		
	}//printStatistics
	
	public void printEventStatistics() {
		printStatTitle("Event Counts:");
		System.out.printf("\t\t\t ENQUEUE:\t DEQUEUE:\t|    TOTAL:\n" +
				"--------------------------------------------------------+-----------\n");
		for (Priority p: Priority.values()) {
			
			int enEvents = eventLists.get(p).getEnqueueEventCount();
			int deEvents = eventLists.get(p).getDequeueEventCount();
			System.out.printf("%s:\t%9d\t%9d\t| %9d\n", p.toString(), enEvents, deEvents, enEvents+deEvents);
			
		}//for
		//System.out.printf("========================================================+===========\n" +
		//				  "Total Events \t\t%9d\t%9d\t| %9d\n\n", enEventsPrio+enEventsNonPrio, deEventsPrio+deEventsNonPrio, eventsNonPrio.size()+eventsPrio.size());
		
	}//printEventStatistics
	
	public void printQueueStatistics() {
		printStatTitle("Queue Time:");
		for (Priority p: Priority.values()) {
			System.out.printf("%s:\t%7.2f µs\n", p, (double)eventLists.get(p).getAvrgQueueTime()/Time.NANOSEC_PER_MICROSEC);
		}//for
		//System.out.printf("Total:\t\t\t%9d µs\n\n", ( (avrgQueueTimeNonPrio+avrgQueueTimePrio)/2) / Time.NANOSEC_PER_MICROSEC );	//TODO only if both != 0
		// If 'µ' is not displayed correctly, go to Eclipse > Preferences > General > Workspace > Text File Encoding	
	}//printQueueStatistics
	
	
	public void printPacketStatistics() {
		printStatTitle("Packet Count:");
		System.out.printf("Packets with delay of %d microseconds:\n", DEFAULT_DELAY);
		
		System.out.printf("\n\t\t\t Delayed:\t    Total:\t| Percentage:\n" +
				"--------------------------------------------------------+-----------\n");
		for (Priority p: Priority.values()) {

			int countDelayed 	= eventLists.get(p).getCountDelayed();
			int deEvents		= eventLists.get(p).getDequeueEventCount();
			double percDelayed 	= eventLists.get(p).getPercentDelayed();
			System.out.printf("%s:\t%9d\t%9d\t| %9f\n", p, countDelayed, deEvents, percDelayed*100);

		}//for
		/*System.out.printf("========================================================+===========\n" +		//TODO
						  "Total Packets \t\t%9d\t%9d\t| %9f\n", countPrioDelayed+countNonPrioDelayed, 
						  										deEventsPrio+deEventsNonPrio, 
						  										percAllDelayed*100);
						  							*/
	}//printPacketStatistics
	
	
	private void printStatTitle(String statTitle) {
		//System.out.printf("\n=== %-12s ====================================================\n\n", statTitle);
		System.out.printf("\n>>>>>>>>>> %-12s <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n\n", statTitle);
	}//printStatTitle


	
}//Statistics
