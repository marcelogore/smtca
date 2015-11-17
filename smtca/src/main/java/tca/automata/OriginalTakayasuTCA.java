// ----------------------------------------
// Filename      : OriginalTakayasuTCA.java
// Author        : Sven Maerivoet
// Last modified : 07/05/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ----------------------------------------

package tca.automata;

import tca.base.*;

public class OriginalTakayasuTCA extends TrafficCellularAutomaton
{
	public OriginalTakayasuTCA(State initialState, int maxSpeed, double cellLength, int nrOfLoopDetectors, int detectorRange, int loopDetectorMeasurementInterval, int multipleOfSampleTimeToSimulate, boolean performGlobalMeasurements)
	{
		super(initialState,maxSpeed,cellLength,nrOfLoopDetectors,detectorRange,loopDetectorMeasurementInterval,multipleOfSampleTimeToSimulate,performGlobalMeasurements);
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, State nextState)
	{
		nextState.fCells[cellNr] = null;

		// Takayasu and Takayasu's original ruleset

		// rule 1: determine braking
		if (sourceCell.fSpeed > sourceCell.fSpaceGap) {
			sourceCell.fSpeed = sourceCell.fSpaceGap;
		}

		// rule 2: determine acceleration
		if ((sourceCell.fSpeed == 0) && (sourceCell.fSpaceGap >= 2)) {
			sourceCell.fSpeed = 1;
		}

		// try to stop at traffic light if necessary
		if ((cellNr == 0) && (fTrafficLightIsRed)) {
			sourceCell.fSpeed = 0;
		}

		// rule 3: advance vehicle
		int targetCellNr = fState.successor(cellNr,sourceCell.fSpeed);
		Cell targetCell = new Cell();
		sourceCell.copyTo(targetCell);
		nextState.fCells[targetCellNr] = targetCell;
	}
}
