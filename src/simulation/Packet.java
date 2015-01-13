package simulation;

public class Packet {
	
	public static int id = 0;
	
	private final int packetId;
	
	Packet() {
		this.packetId = id++;
	}//Constructor
	
}//Packet
