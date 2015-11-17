// ------------------------------
// Filename      : TASEP.java
// Author        : Sven Maerivoet
// Last modified : 12/06/2005
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------

package tca.automata;

import tca.base.*;

public class TASEP extends TrafficCellularAutomaton
{
	public TASEP(State initialState, int maxSpeed, double cellLength, int nrOfLoopDetectors, int detectorRange, int loopDetectorMeasurementInterval, int multipleOfSampleTimeToSimulate, boolean performGlobalMeasurements)
	{
		super(initialState,maxSpeed,cellLength,nrOfLoopDetectors,detectorRange,loopDetectorMeasurementInterval,multipleOfSampleTimeToSimulate,performGlobalMeasurements);
	}

	protected void applyRules(State nextState)
	{
		for (int subStep = 0; subStep < fState.fNrOfVehicles; ++subStep) {

			fState.calcSpaceGaps();
			fState.copyTo(nextState);

			// randomly select a vehicle to move
 			int cellNr = (int) Math.floor(Math.random() * fState.fCells.length);
 			while (fState.fCells[cellNr] == null) {
 	 			cellNr = (int) Math.floor(Math.random() * fState.fCells.length);
 			}

 			Cell sourceCell = fState.fCells[cellNr];
			applyRulesToCell(sourceCell,cellNr,nextState);
			
			fState = new State(nextState);
		}
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, State nextState)
	{
		nextState.fCells[cellNr] = null;

		// rule 1a: determine acceleration
		if (sourceCell.fSpeed > sourceCell.fSpaceGap) {
			sourceCell.fSpeed = sourceCell.fSpaceGap;
		}
		else if ((sourceCell.fSpeed < sourceCell.fSpaceGap) && (sourceCell.fSpeed < getMaxSpeed(cellNr))) {
				++sourceCell.fSpeed;
		}

		// rule 1b: clip to max speed
		if (sourceCell.fSpeed > getMaxSpeed(cellNr)) {
			sourceCell.fSpeed = getMaxSpeed(cellNr);
		}

		// try to stop at traffic light if necessary
		if ((cellNr == 0) && (fTrafficLightIsRed)) {
			sourceCell.fSpeed = 0;
		}

		// rule 2: advance vehicle
		int targetCellNr = fState.successor(cellNr,sourceCell.fSpeed);
		Cell targetCell = new Cell();
		sourceCell.copyTo(targetCell);
		nextState.fCells[targetCellNr] = targetCell;
	}
}
