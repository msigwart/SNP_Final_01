package simulation;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.Files;

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
	
	public void readEventsFromFile() throws IOException{
		File file = new File("statsTest.txt");
		FileInputStream fis = null;
 
		try {
			fis = new FileInputStream(file);
 
			System.out.println("Total file size to read (in bytes) : "
					+ fis.available());
 
			int content;
			while ((content = fis.read()) != -1) {
				// convert to char and display it
				System.out.print((char) content);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
