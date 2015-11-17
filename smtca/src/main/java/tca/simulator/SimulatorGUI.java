// ---------------------------------
// Filename      : SimulatorGUI.java
// Author        : Sven Maerivoet
// Last modified : 14/09/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ---------------------------------

// tca.simulator.SimulatorGUI

package tca.simulator;

import tca.base.*;

public class SimulatorGUI
{
	public SimulatorGUI(boolean demoMode)
	{
		// general setup
		int nrOfCells                      = 300;
		int nrOfVehicles                   = nrOfCells / 6;
		int maxSpeed                       = 5;
		double cellLength                  = 7.5;
		int nrOfLoopDetectors              = 14;
		int detectorRange                  = (nrOfCells / nrOfLoopDetectors);
		int nrOfDensitySteps               = nrOfCells / 2;
		int multipleOfSampleTimeToSimulate = 10;
		int transientPeriod                = 1000;

		if (demoMode) {

			nrOfVehicles                   = 50;
			nrOfDensitySteps               = nrOfCells / 10;
			multipleOfSampleTimeToSimulate = 2;
			transientPeriod                = 100;
		}

		// custom switches
		GUI.kSaveTCALatticeToDisk                      = false;
		GUI.kHighlightStoppedVehicles                  = false;
		TrafficCellularAutomaton.kLoopDetectorsEnabled = true;

		// create graphical user-interface
		GUI gui =
			new GUI(
				nrOfCells,
				nrOfVehicles,
				maxSpeed,
				cellLength,
				nrOfLoopDetectors,
				detectorRange,
				nrOfDensitySteps,
				multipleOfSampleTimeToSimulate,
				transientPeriod);

		// create the simulator's thread
		Thread thread = new Thread(gui);
		thread.start();
	}

	public static void main(String[] args) 
	{
		boolean demoMode = false;

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("-DEMO")) {
				demoMode = true;
			}
		}

		SimulatorGUI simulator = new SimulatorGUI(demoMode);
	}
}
