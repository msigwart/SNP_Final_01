package simulation;

import java.util.ArrayList;
import java.util.Scanner;

import statistics.Statistics;

public class SimulationEnvironment {
	
	public static void main(String[] args) {
		int runs;
		ArrayList<Simulator> simulators = new ArrayList<Simulator>();
		ArrayList<Statistics> stats 	= new ArrayList<Statistics>();
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
			System.out.println("\nPrinting statistics for simulation " + (i+1) + ":");
			stats.get(i).printStatistics();
		}//for
	}

}
