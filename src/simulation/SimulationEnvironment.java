package simulation;

import java.util.Scanner;

public class SimulationEnvironment {
	
	public static void main(String[] args) {
		int runs;
		Scanner sc = new Scanner(System.in);
		
		System.out.print("How many simulations do you want to do?:");
		
		while(true){
			while(!(sc.hasNextInt())){
				sc.next();
			}//while
			
			runs = sc.nextInt();
			if(runs > 0){
				System.out.print("Runs = " + runs);
				break;
			}
		}//while
		
		System.out.print("Runs = " + runs);
		
		//Simulator simulator = new Simulator(4, 1);
		//simulator.runSimulation();
		
		
	}

}
