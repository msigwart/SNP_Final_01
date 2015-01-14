package simulation;

import java.text.SimpleDateFormat;
import java.util.*;

public class Time {
	
	//second conversion
	public static final long MILLISEC_PER_SEC 		= 1000;
	public static final long MICROSEC_PER_SEC 		= MILLISEC_PER_SEC*1000;
	public static final long NANOSEC_PER_SEC		= MICROSEC_PER_SEC*1000;
	
	//millisecond conversion
	public static final long MICROSEC_PER_MILLISEC	= 1000;
	public static final long NANOSEC_PER_MILLISEC	= MICROSEC_PER_MILLISEC*1000;
	
	//microsecond conversion
	public static final long NANOSEC_PER_MICROSEC	= 1000;
	
	
	
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
