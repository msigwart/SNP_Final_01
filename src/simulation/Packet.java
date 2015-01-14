package simulation;

public class Packet {
	
	
	/**
	 * Static id counter
	 */
	public static volatile int id = 0;
	
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
	
	public Packet(int packetId){
		this.packetId = packetId;
	}//Constructor
	
	//getter
	public int getId() { return packetId; }
	
}//Packet
