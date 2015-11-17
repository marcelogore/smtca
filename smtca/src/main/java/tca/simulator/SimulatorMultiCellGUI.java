// ------------------------------------------
// Filename      : SimulatorMultiCellGUI.java
// Author        : Sven Maerivoet
// Last modified : 14/09/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------------------

// tca.simulator.SimulatorMultiCellGUI

package tca.simulator;

public class SimulatorMultiCellGUI
{
	public SimulatorMultiCellGUI()
	{
		// general setup
		int baseNrOfCells  = 300;
		int nrOfCells    = baseNrOfCells * 2; // HS-TCA
//		int nrOfCells    = baseNrOfCells * 5; // BL-TCA
//		int nrOfCells    = baseNrOfCells * 15; // KKW-TCA
		int nrOfVehicles = nrOfCells / 6;

		// custom switches
		MultiCellGUI.kTXDiagramTimeHorizon     = 580;
		MultiCellGUI.kHighlightStoppedVehicles = false;

		// create graphical user-interface
		MultiCellGUI gui =
			new MultiCellGUI(
				nrOfCells,
				nrOfVehicles);

		// create the simulator's thread
		Thread thread = new Thread(gui);
		thread.start();
	}

	public static void main(String[] args) 
	{
		SimulatorMultiCellGUI simulator = new SimulatorMultiCellGUI();
	}
}
