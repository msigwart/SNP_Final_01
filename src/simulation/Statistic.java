package simulation;
import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Statistic {
	
	private ArrayList<Event> events = new ArrayList<Event>();
	
	public void createEvent(int eventType, Packet packet){
		writeEventIntoFile(new Event(eventType, packet));
	}
	
	public void writeEventIntoFile(Event event){
		try {
            //Whatever the file path is.
			System.out.println("trying to write in the file statsTest.txt");
            File statText = new File("statsTest.txt");
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);    
            Writer w = new BufferedWriter(osw);
            w.write(event.toString());
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file statsTest.txt");
        }
	}
}
