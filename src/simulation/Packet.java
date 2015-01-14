package simulation;

public class Packet {
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Global variables
	/**
	 * Static id counter
	 */
	public static volatile int id = 0;
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Private members
	/**
	 * Packet ID
	 */
	private final int 		packetId;
	
	/**
	 * Packet priority
	 */
	private final boolean 	priority;
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Constructors
	/**
	 * Creates a packet.
	 * @param priority priority of packet, true if priority packet
	 */
	Packet(boolean priority) {
		this.packetId = id++;
		this.priority = priority;
	}//Constructor
	
	/**
	 * Creates a default non-priority packet
	 */
	Packet() {
		this(false);
	}//Constructor
	

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Getter and Setter
	public int 		getId()			{ return packetId; }
	public boolean 	getPriority() 	{ return priority; }
	
}//Packet
