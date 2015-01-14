package statistics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.nio.file.Files;

import simulation.Packet;

public class Statistics {
	
	private ArrayList<Event> events = new ArrayList<Event>();
	private String outputFile;
	
	/**
	 * Creates a new Statistics instance for tracing simulation events
	 * @param outputFile path to output file (simulation trace)
	 */
	public Statistics(String outputFile) {
		this.outputFile = outputFile;
	}//Constructor
	
	
	/**
	 * Triggers an event of the simulation
	 * @param eventType the type of the event that occurred
	 * @param packet the packet the event occurred with
	 */
	public void triggerEvent(int eventType, Packet packet){
		writeEventIntoFile(new Event(eventType, packet));
	}//createEvent
	
	
	public void writeEventIntoFile(Event event){
		try {
            //Whatever the file path is.
			System.out.println("trying to write in the file statsTest.txt");
            File statText = new File(outputFile);
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);    
            Writer w = new BufferedWriter(osw);
            w.append(event.toString());
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file statsTest.txt");
        }//catch
	}//writeEventIntoFile
	
	
	public void readEventsFromFile(){
		File file = new File(outputFile);
		FileInputStream fis = null;
		BufferedReader reader = null;
		
		try {
			fis = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fis));
			
			System.out.println("Total file size to read (in bytes) : "
					+ fis.available());
			
			String line = reader.readLine();
			
			while(line != null){
				Event e = this.createEventFromString(line);
				
				if (e == null) {
					System.out.printf("Statistics: Could not read event from line --> \"%s\"", line);
				}//if
				else{
					System.out.printf("Reading event....\n");
					System.out.println(e.toString());
				}//else
				
				line = reader.readLine();
			}//while
			
		} catch (FileNotFoundException ex) {
            ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}//try
		}//finally
	}//readEventsFromFile
	
	public Event createEventFromString(String strEvent){
		Event event = null;
		Packet packet = null;
		int eventType;
		boolean packetPriority;
		
		if(strEvent.substring(0, strEvent.indexOf('(')).equalsIgnoreCase("DQUEUE")){
			eventType = Event.EVENT_TYPE_DQUEUE;
		}//if
		else{
			eventType = Event.EVENT_TYPE_ENQUEUE;
		}//else
		
		boolean found = Arrays.asList(strEvent.split(" ")).contains("true"); 
		
		if(found){
			packetPriority = true;
		}
		else{
			packetPriority = false;
		}//if
		
		
		String []newDigits = (strEvent.replaceAll("[^0-9. ]", "")).split(" ",-1); //removes non numeric chars and slipts the String 
		ArrayList <String> parts = new ArrayList<String>();
		
		for(int i = 0; i < newDigits.length; i++){
			if(!newDigits[i].isEmpty()) // if the string is not empty adds a new digit to the array of parts
				parts.add(newDigits[i]);
		}//for

		return new Event( eventType, Long.parseLong(parts.get(0)), new Packet( Integer.parseInt(parts.get(1)), packetPriority ) );
		
	}//addEventFromString
	
	
	
}//Statistics
