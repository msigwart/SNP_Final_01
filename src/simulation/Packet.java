package simulation;

public class Packet {
	
	public static final int PACKET_SIZE = 1526; //bytes
	
	/**
	 * Static id counter
	 */
	public static int id = 0;
	
	/**
	 * Packet ID
	 */
	private final int packetId;
	

	/**
	 * Creates a packet
	 */
	Packet() {
		this.packetId = id++;
	}//Constructor
	
	//getter
	public int getId() { return packetId; }
	
}//Packet
