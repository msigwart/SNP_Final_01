package simulation;

import java.util.ArrayList;
import java.util.Scanner;

import statistics.Statistics;

public class SimulationEnvironment {
	
	public static void main(String[] args) {
		int runs;
		ArrayList <Simulator> simulators = new ArrayList<Simulator>();
		ArrayList <Statistics> stats 	= new ArrayList<Statistics>();
		Scanner sc = new Scanner(System.in);
		
		System.out.print("How many simulations do you want to do?:");
		
		while(true){
			while(!(sc.hasNextInt())){
				System.out.print("Write a number:");
				sc.next();
			}//while
			
			runs = sc.nextInt();
			if(runs > 0){
				break;
			}//if
			else{
				System.out.print("The number of simulations has to be greater than 0:");
			}
		}//while
		
		for(int i = 0; i < runs; i++){
			simulators.add(new Simulator("output/output_" + i + ".txt"));
			setSimulatorOptions(simulators.get(i), i+1);
		}//for
		
		for(int i = 0; i < runs;){
			if(simulators.get(i).isRunning() == false){//If the simulator is not running
				if(simulators.get(i).getStarted() == false){//If the simulator didn't start
					simulators.get(i).runSimulation(); //runs the simulation
				}
				else{
					stats.add(simulators.get(i).getStatistics()); //Saves the statistics of the last simulator
					i++; //will pass to the next simulator
				}//if
			}//if
		}//for
		
		for(int i = 0; i < runs; i++){
			System.out.println("\nCollecting statistics for simulation " + (i+1) + ":");
			stats.get(i).collectStatistics();
			System.out.println("\n################ Simulation " + (i+1) + " ##########################");
			System.out.println(simulators.get(i).printSimulationData());
			stats.get(i).printStatistics();
		}//for
		
	}//main

	private static void setSimulatorOptions(Simulator simulator, int simulatorIndex)
	{
		Scanner sc = new Scanner(System.in);

		while(true){
			System.out.println("--SIMULATOR " + simulatorIndex + " OPTIONS--");
			System.out.println("[1] - Client options");
			System.out.println("[2] - Server options");
			System.out.println("[3] - Default options");
			System.out.print("Select option:");
			int op = sc.nextInt();
			switch(op){
				case 1: // Client options
					while(op != 0)
					{
						System.out.println("[1] - Send interval\n[2] - Send min interval\n[3] - Send max interval");
						System.out.println("[4] - No. of packets\n[5] - No. of clients\n[6] - No. priority clients\n[0] - Return");

						op = sc.nextInt();

						int v;

						switch(op){
							case 1:
								System.out.println("Put a value for send interval (microseconds): ");
								v = sc.nextInt();
								if(v>0)
									simulator.setClientSendInterval(v);
								break;
							case 2:
								System.out.println("Put a value for send min interval: ");
								v = sc.nextInt();
								if(v>0)
									simulator.setClientSendMinInterval(v);
								break;
							case 3:
								System.out.println("Put a value for send max interval: ");
								v = sc.nextInt();
								if(v>0)
									simulator.setClientSendMaxInterval(v);
								break;
							case 4:
								System.out.println("Put a value for number of packets: ");
								v = sc.nextInt();
								if(v>0)
									simulator.setClientNumPackets(v);
								break;
							case 5:
								System.out.println("Put a value for number of clients: ");
								v = sc.nextInt();
								if(v>0)
									simulator.setNumClients(v);
								break;
							case 6:
								System.out.println("Put a value for non priority clients: ");
								v = sc.nextInt();
								if(v>0)
									simulator.setNumPriorityClients(v);
								break;
							default:
								break;
						}
					}
					break;
				// EOF client options
				case 2: // server options
					while(op != 0) {
						System.out.println("[1] - Server runtime\n[2] - Send speed\n[3] - Server queue size");
						System.out.println("[4] - ms per packet\n[0] - Return");

						op = sc.nextInt();

						int v;

						switch(op) {
							case 1:
								System.out.println("Put a value for server runtime (seconds): ");
								v = sc.nextInt();
								if (v > 0)
									simulator.setServerRuntime(v);
								break;
							case 2:
								System.out.println("Put a value for send speed: ");
								v = sc.nextInt();
								if (v > 0)
									simulator.setServerSendSpeed(v);
								break;
							case 3:
								System.out.println("Put a value for server queue size: ");
								v = sc.nextInt();
								if (v > 0)
									simulator.setServerQueueSize(v);
								break;
							case 4:
								System.out.println("Put a value for µs/Packet: ");
								long vv = sc.nextLong();
								if (vv > 0)
									simulator.setMicSecondsPerPacket(vv);
								break;
							default:
								break;
						}
					}
					break;
				// EOF server options
				case 3: // Default options
					return;
				default: // Default..
					return;
			}
		}//while
	}
	
}//SimulationEvironment
