package statistics;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.beans.EventSetDescriptor;
import java.io.*;


import simulation.Packet;
import simulation.Priority;
import simulation.SendConnection;
import simulation.Simulation;
import simulation.Simulator;
import simulation.Time;


/**
 * This class collects the event of the simulation, saves them into file. It implements the Observer interface.
 * It provides several different statistics measures, such as average queuing time, etc.
 * 
 * @author Bernardo Paulino, Marten Sigwart
 *
 */
public class Statistics implements Observer {
	
	public static final double DEFAULT_DELAY = 200;//Microseconds
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Fields for file I/O
	private String outputFile;
	
	private PrintWriter 	pWriter;
	private FileInputStream	fStream;
	private BufferedReader 	bReader;
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%		
// Different statistics fields
	
	private EnumMap<Priority, EventList> eventLists;
	
	private int 	totalEnEvents;
	private int		totalDeEvents;
	private double 	averageQueueTime;		//in nanoseconds
	private double	delay;
	private double 	percTotalDelayed;
	private int		totalDelayed;
	

	
	
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
			this.eventLists.put(p, new EventList(p));
		}//for
		this.totalEnEvents			= 0;
		this.totalDeEvents			= 0;
		this.delay					= DEFAULT_DELAY;
		this.percTotalDelayed		= 0.0;
		this.totalDelayed			= 0;
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
	
		



//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Read methods to read events from an existing output file
	
	/**
	 * Reads all events from the output file and saves them into the corresponding event lists
	 * @return returns the number of lines read
	 */
	public int readEventsFromFile(int numOfLines){
		int lines = 0;
		
		try {
//			int progressStep;
//			int nextProgress;
//			int progressCounter;
			
			
			//Calculate progress display
//			progressStep = numOfLines/100;
//			nextProgress = progressStep;
//			progressCounter = 0;
			
			System.out.printf("Reading tracefile...\n");
			for (int i=0; i<numOfLines; i++) {
				String line = bReader.readLine();
				if (line == null) {
					bReader.close();
					System.out.printf("END OF FILE\n");
					return lines;
				}//if
				Event e = Event.createEventFromString(line);
				if (e == null) {
					System.out.printf("Statistics: Could not read event from line --> \"%s\"", line);
				}//if
				else {
					try {
						if ( eventLists.get(e.getPacket().getPriority()).get(e.getPacket().getId()) == null) {
							ArrayList<Event> newEventList = new ArrayList<Event>();
							for (int j=0; j<Event.NUMBER_OF_EVENTS; j++) {
								newEventList.add(j, null);
							}//for
							newEventList.set(e.getEventType(), e);		//replace existing null element
							eventLists.get(e.getPacket().getPriority()).put(e.getPacket().getId(), newEventList);
							
//							if (e.getEventType() == Event.EVENT_TYPE_DEQUEUE) {
//								System.out.printf("WARNING: Writing DEQUEUE event for packet %d first\n", e.getPacket().getId());
//							}
						} else {
							eventLists.get(e.getPacket().getPriority()).set(e);		//replace existing null element
//							if (e.getEventType() == Event.EVENT_TYPE_ENQUEUE) {
//								System.out.printf("WARNING: Writing ENQUEUE event for packet %d second\n", e.getPacket().getId());
//								System.out.printf("Packet %d: %s\n", e.getPacket().getId(), 
//										eventLists.get(e.getPacket().getPriority()).get(e.getPacket().getId()) );
//							}//if
						}//if
						updateStatistics(e);

					} catch (NullPointerException ne) {
						System.out.printf("Caught Null Pointer Exception\n");
						return lines;
					}//catch
				}//else
				/*if (i >= nextProgress) {
					System.out.printf("%d Percent...\n", progressCounter++);
					nextProgress += progressStep;
				}//if*/
				lines++;
			}//for
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;	
			
	}//readEventsFromFile
	
	


	
	
	
	
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
		
		int numOfLines = readNumberOfLines();
		System.out.printf("Total lines to read : %d\n", numOfLines);

		if (numOfLines <= 100000) {
			readEventsFromFile(numOfLines);
			for (Priority p: Priority.values()) {
				System.out.printf("Queue %s size: %d\n", p, eventLists.get(p).size());
				for (Map.Entry<Integer, ArrayList<Event>> e: eventLists.get(p).getEvents().entrySet()) {
					eventLists.get(p).addQueueTime(e.getKey());
				}//for
			}//for
			updateAvrgQueueTime();
		} else {
			int eventsRead = 0;
			int readCycle = 1;

			do {
				eventsRead = readEventsFromFile(100000);

				System.out.printf("Read %d events, Part %d\n", eventsRead, readCycle++);
				
				for (Priority p: Priority.values()) {
					System.out.printf("Queue %s size: %d\n", p, eventLists.get(p).size());
					for (Map.Entry<Integer, ArrayList<Event>> e: eventLists.get(p).getEvents().entrySet()) {
						eventLists.get(p).addQueueTime(e.getKey());
					}//for
				}//for
				System.out.printf("Delete processed events.\n");
				updateAvrgQueueTime();
				calculateDelayStatistics(DEFAULT_DELAY);			//TODO Support multiple delay counts
				deleteProcessedEvents();
				
				System.out.printf("Deleted processed events.\n");
				for (Priority p: Priority.values()) {
					System.out.printf("%s events: %d\n", p, eventLists.get(p).size());
				}//for
			} while (eventsRead == 100000);
		}//if
		//printEvents();
		//avrgQueueTimePrio = getAverageQueueTime(Priority.PACKET_PRIORITY_HIGH);
		//avrgQueueTimeNonPrio = getAverageQueueTime(Priority.PACKET_PRIORITY_LOW);
	}//collectingStatistics

	
	private void deleteProcessedEvents() {
		for (Priority p: Priority.values()) {
			EventList el = eventLists.get(p);
			el.getQueueTimes().clear();
			ArrayList<Integer> packetsToRemove = new ArrayList<Integer>();

			for (Map.Entry<Integer, ArrayList<Event>> e: el.getEvents().entrySet()) {
				if (e.getValue().get(Event.EVENT_TYPE_DEQUEUE) != null) {
					packetsToRemove.add(e.getKey());
				}//if
			}//for
			
			for (int packetId: packetsToRemove) {
				el.remove(packetId);
			}//for
		}//for
	}//deleteProcessedEvents
	
	/**
	 * Reads the number of Lines in the tracefile
	 * @return
	 */
	private int readNumberOfLines() {
		//Count lines
		int lines = 0;
		try {
			while (bReader.readLine() != null) {
				lines++;
			}//while
			bReader.close();
			fStream.close();
			
			fStream = new FileInputStream(this.outputFile);
			bReader = new BufferedReader(new InputStreamReader(fStream));
			return lines;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}//catch
		//System.out.printf("Total file size to read (in bytes) : %d\n", fStream.available());
	}//readNumberOfLines
	
	/**
	 * Updates some statistics. Is called when a new event is written to file
	 * Can only update statistics when the events are also added to the corresponding event lists.
	 * @param event the newly arrived event
	 */
	private void updateStatistics(Event event) {
		//do some statistics work
		
		EventList el = eventLists.get(event.getPacket().getPriority());
		el.updateEventListStats(event);
		updateCounters();
		//updateAvrgQueueTime();

		
	}//updateStatistics
	
	/**
	 * Updates total enqueue and dequeue counters
	 */
	private void updateCounters() {
		this.totalEnEvents 	= 0;
		this.totalDeEvents 	= 0;

		for (Priority p: Priority.values()) {
			EventList el = eventLists.get(p);
			this.totalEnEvents += el.getEnqueueEventCount();
			this.totalDeEvents += el.getDequeueEventCount();
		}//for
		
	}//updateCounters
	
	
	/**
	 * Updates the total average time
	 */
	private void updateAvrgQueueTime() {
		this.averageQueueTime = 0.0;
		for (Priority p: Priority.values()) {
			EventList el = eventLists.get(p);
			if (this.averageQueueTime == 0.0) {
				this.averageQueueTime = el.getAvrgQueueTime();
			} else {
				this.averageQueueTime = (double)((this.averageQueueTime+el.getAvrgQueueTime())/2);
			}//if
		}//for
	}//updateAvrgQueueTime
	
	
	private void calculateDelayStatistics(double delay) {
		this.delay = delay;
		//this.totalDelayed	= 0;
		//this.percTotalDelayed = 0;
		
		for (Priority p: Priority.values()) {
			eventLists.get(p).calculateDelayedCount(delay);
			this.totalDelayed += eventLists.get(p).getCountDelayed();
		}//for
		this.percTotalDelayed = (double)totalDelayed/totalDeEvents;

	}//calculateDelayStatistics

	
	/**
	 * Return the average time spent by packet in the queue
	 * @return returns the average queue time in nanoseconds
	 */
	public double getAverageQueueTime(Priority p) {
		return averageQueueTime;
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
		calculateDelayStatistics(Statistics.DEFAULT_DELAY);
		printDelayStatistics();
		//printSimulationAnalysis();
		
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
		System.out.printf("========================================================+===========\n" +
						  "Total Events \t\t%9d\t%9d\t| %9d\n\n", totalEnEvents, totalDeEvents, totalEnEvents+totalDeEvents);
		
	}//printEventStatistics
	
	public void printQueueStatistics() {
		printStatTitle("Queue Time:");
		for (Priority p: Priority.values()) {
			System.out.printf("%s:\t%7.2f µs\n", p, (double)eventLists.get(p).getAvrgQueueTime()/Time.NANOSEC_PER_MICROSEC);
		}//for
		System.out.printf("Total:\t\t\t%7.2f µs\n\n", (this.averageQueueTime/Time.NANOSEC_PER_MICROSEC) );	//TODO only if both != 0
		// If 'µ' is not displayed correctly, go to Eclipse > Preferences > General > Workspace > Text File Encoding	
	}//printQueueStatistics
	
	
	public void printDelayStatistics() {
		printStatTitle("Packet Count:");
		System.out.printf("Packets with delay of %7.2f microseconds:\n", this.delay);
		
		System.out.printf("\n\t\t\t Delayed:\t    Total:\t| Percentage:\n" +
				"--------------------------------------------------------+-----------\n");
		for (Priority p: Priority.values()) {

			int countDelayed 	= eventLists.get(p).getCountDelayed();
			int deEvents		= eventLists.get(p).getDequeueEventCount();
			double percDelayed 	= eventLists.get(p).getPercentDelayed();
			System.out.printf("%s:\t%9d\t%9d\t| %9f\n", p, countDelayed, deEvents, percDelayed*100);

		}//for
		System.out.printf("========================================================+===========\n" +		//TODO
						  "Total Packets \t\t%9d\t%9d\t| %9f\n", totalDelayed, 
						  										totalDeEvents, 
						  										percTotalDelayed*100);
	}//printPacketStatistics
	
	
	public void printSimulationAnalysis() {
		printStatTitle("Simulation Analysis:");
		System.out.printf("M       = %7.2f µs\n", Simulation.MICSECONDS_PER_PACKET*1.0);
		System.out.printf("1.1 * M = %7.2f µs\n", Simulation.MICSECONDS_PER_PACKET*1.1);
		System.out.printf("1.5 * M = %7.2f µs\n", Simulation.MICSECONDS_PER_PACKET*1.5);
		System.out.printf("4.0 * M = %7.2f µs\n", Simulation.MICSECONDS_PER_PACKET*4.0);
		
		// 1.1 * M
		calculateDelayStatistics(Simulation.MICSECONDS_PER_PACKET);
		printDelayStatistics();
		
		// 1.5 * M
		calculateDelayStatistics(Simulation.MICSECONDS_PER_PACKET*1.5);
		printDelayStatistics();
		
		// 4.0 * M
		calculateDelayStatistics(Simulation.MICSECONDS_PER_PACKET*4.0);
		printDelayStatistics();
		

	}//printSimulationAnalysis
	
	
	private void printStatTitle(String statTitle) {
		//System.out.printf("\n=== %-12s ====================================================\n\n", statTitle);
		System.out.printf("\n>>>>>>>>>> %-12s <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n\n", statTitle);
	}//printStatTitle
	
	



	
}//Statistics
