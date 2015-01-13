package simulation;

import java.text.SimpleDateFormat;
import java.util.*;

public class Time {

	public static String getTimeStampString() {
		return new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss.SSS",
				new Locale("de","DE")
				).format( new Date().getTime() );
	}//getTimeStampString
	
	public static long getTimeStamp() {
		return new Date().getTime();
	}//getTimeStamp
	
	
}//Time
