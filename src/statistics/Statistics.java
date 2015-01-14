package statistics;
import java.util.ArrayList;
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
            w.write(event.toString());
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
				System.out.println(line);
				Event e = this.createEventFromString(line);
				if (e == null) {
					System.out.printf("Statistics: Could not read event from line --> \"%s\"", line);
				}//if
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
		//strEvent.split(" ");
		Event event = null;
		
		String []newDigits = (strEvent.replaceAll("[^0-9. ]", "")).split(" ",-1); //removes non numeric chars and slipts the String 
		ArrayList <String> parts = new ArrayList<String>();
		
		for(int i = 0; i < newDigits.length; i++){
			if(!newDigits[i].isEmpty()) // if the string is not empty adds a new digit to the array of parts
				parts.add(newDigits[i]);
		}//for
		
		for(int i = 0; i < parts.size(); i++){
			System.out.println(parts.get(i));
		}//for
		
		return event;
		
	}//addEventFromString
	
}//Statistics
