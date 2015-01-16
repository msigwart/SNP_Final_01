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
	//private final boolean 	priority;
	private final Priority  priority;
	
	
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Constructors
	
	public Packet(int packetId, Priority priority){
		this.packetId = packetId;
		this.priority = priority;
	}//Constructor
	
	/**
	 * Creates a packet.
	 * @param priority priority of packet, true if priority packet
	 */
	Packet(Priority priority) {
		this(id++, priority);
	}//Constructor
	
	/**
	 * Creates a default non-priority packet
	 */
	Packet() {
		this(Priority.PACKET_PRIORITY_LOW);
	}//Constructor
	

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%	
// Getter and Setter
	public int 		getId()			{ return packetId; }
	public Priority	getPriority() 	{ return priority; }

	
}//Packet
