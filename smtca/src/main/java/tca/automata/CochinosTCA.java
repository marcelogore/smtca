// --------------------------------
// Filename      : CochinosTCA.java
// Author        : Sven Maerivoet
// Last modified : 22/10/2003
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// --------------------------------

package tca.automata;

import tca.base.*;

public class CochinosTCA extends TrafficCellularAutomaton
{
	public CochinosTCA(State initialState, int maxSpeed, double cellLength, int nrOfLoopDetectors, int detectorRange, int loopDetectorMeasurementInterval, int multipleOfSampleTimeToSimulate, boolean performGlobalMeasurements)
	{
		super(initialState,maxSpeed,cellLength,nrOfLoopDetectors,detectorRange,loopDetectorMeasurementInterval,multipleOfSampleTimeToSimulate,performGlobalMeasurements);
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, State nextState)
	{
		nextState.fCells[cellNr] = null;

		int newSpeed = sourceCell.fSpeed;

		// rule 1: acceleration of free vehicles
		if ((sourceCell.fSpeed < getMaxSpeed(cellNr)) && (sourceCell.fSpaceGap > (sourceCell.fSpeed + 1))) {
			newSpeed = sourceCell.fSpeed + 1;
		}

		// rule 2: slowing down due to other vehicles
		if (sourceCell.fSpeed > (sourceCell.fSpaceGap - 1)) {
			sourceCell.fSpeed = sourceCell.fSpaceGap;
			newSpeed = sourceCell.fSpeed;
		}

		// rule 3: clip to max speed
		if (sourceCell.fSpeed > getMaxSpeed(cellNr)) {
			sourceCell.fSpeed = getMaxSpeed(cellNr);
			newSpeed = sourceCell.fSpeed;
		}

		// try to stop at traffic light if necessary
		if ((cellNr == 0) && (fTrafficLightIsRed)) {
			sourceCell.fSpeed = 0;
		}

		// rule 4: advance vehicle
		int targetCellNr = fState.successor(cellNr,sourceCell.fSpeed);
		Cell targetCell = new Cell();
		sourceCell.copyTo(targetCell);
		targetCell.fSpeed = newSpeed;
		nextState.fCells[targetCellNr] = targetCell;
	}
}
