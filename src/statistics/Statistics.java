package statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.nio.file.Files;

import simulation.Packet;
import simulation.Priority;
import simulation.SendConnection;


/**
 * This class collects the event of the simulation, saves them into file. It implements the Observer interface.
 * It provides several different statistics measures, such as average queuing time, etc.
 * 
 * @author Bernardo Paulino
 *
 */
public class Statistics implements Observer {
	
	private ArrayList<Event> events = new ArrayList<Event>();
	private String outputFile;
	
	private PrintWriter 	pWriter;
	private FileInputStream	fStream;
	private BufferedReader 	bReader;
	
	private int numFiles;
	
	/**
	 * Creates a new Statistics instance for tracing simulation events
	 * @param outputFile path to output file (simulation trace)
	 */
	public Statistics(String outputFile) {
		this.outputFile = outputFile;
		//this.numFiles = this.readNumOfFiles();
		initializeStatistics();
	}//Constructor
	
	
	/**
	 * Triggers an event of the simulation
	 * @param eventType the type of the event that occurred
	 * @param packet the packet the event occurred with
	 */
	public void triggerEvent(int eventType, Packet packet){
		writeEventIntoFile(new Event(eventType, packet));
	}//createEvent
	
	
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
	
	
	private synchronized void writeEventIntoFile(Event event){
		System.out.printf("Statistics: Writing event into file...");
		pWriter.printf("%s\n", event.toString());
	}//writeEventIntoFile
	
	
	
	public void readEventsFromFile(){
		/*String newFileName = outputFile + "_" + numFiles + ".txt";
		File file = new File(newFileName);
		FileInputStream fis = null;
		BufferedReader reader = null;
		*/
		try {
			System.out.printf("Total file size to read (in bytes) : %d\n", fStream.available());
			
			String line = bReader.readLine();
			
			while(line != null){
				Event e = this.createEventFromString(line);
				
				if (e == null) {
					System.out.printf("Statistics: Could not read event from line --> \"%s\"", line);
				}//if
				else{
					//System.out.printf("Reading event....\n");
					System.out.println(e.toString());
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
	
	
	
	public Event createEventFromString(String strEvent){
		Event event = null;
		Packet packet = null;
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
		
		boolean found = Arrays.asList(strEvent.split(" ")).contains("true"); 		//TODO: Change to enum Priority
		
		if(found){
			packetPriority = Priority.PACKET_PRIORITY_HIGH;
		}
		else{
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
	
	/*private int readNumOfFiles(){
		return new File("output/").listFiles().length;
	}*/
	
	@Override
	public void update(Observable o, Object arg) {		//OBSERVER PATTERN
		
		switch ((int)arg) {
			case SendConnection.SERVER_EVENT_TERMINATED:
				pWriter.close();
				System.out.printf("######################## Statistics ######################### \n");
				collectStatistics();
				break;
			default:
				break;
		}//switch
	}//update
	
	
	private void collectStatistics() {
		System.out.printf("Collecting statistics...\n");
		readEventsFromFile();
	}//collectingStatistics





	
}//Statistics
