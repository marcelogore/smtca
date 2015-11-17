// ------------------------------------
// Filename      : StochasticCCTCA.java
// Author        : Sven Maerivoet
// Last modified : 30/05/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------------

package tca.automata;

import tca.base.*;

public class StochasticCCTCA extends StochasticTCA
{
	public StochasticCCTCA(State initialState, int maxSpeed, double cellLength, int nrOfLoopDetectors, int detectorRange, int loopDetectorMeasurementInterval, int multipleOfSampleTimeToSimulate, boolean performGlobalMeasurements, double slowdownProbability)
	{
		super(initialState,maxSpeed,cellLength,nrOfLoopDetectors,detectorRange,loopDetectorMeasurementInterval,multipleOfSampleTimeToSimulate,performGlobalMeasurements,slowdownProbability);
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, State nextState)
	{
		nextState.fCells[cellNr] = null;

		// determine if vehicle is free-flowing
		if ((sourceCell.fSpaceGap >= getMaxSpeed(cellNr)) && (sourceCell.fSpeed == getMaxSpeed(cellNr))) {
	
			// try to stop at traffic light if necessary
			if ((cellNr == 0) && (fTrafficLightIsRed)) {
				sourceCell.fSpeed = 0;
			}

			// advance vehicle (using current speed, i.e. cruise control)
			int targetCellNr = fState.successor(cellNr,sourceCell.fSpeed);
			Cell targetCell = new Cell();
			sourceCell.copyTo(targetCell);
			nextState.fCells[targetCellNr] = targetCell;
		}
		else {
			// apply standard STCA-rules
			super.applyRulesToCell(sourceCell,cellNr,nextState);
		}
	}
}
