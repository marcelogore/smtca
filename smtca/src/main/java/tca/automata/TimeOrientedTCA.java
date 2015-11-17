// ------------------------------------
// Filename      : TimeOrientedTCA.java
// Author        : Sven Maerivoet
// Last modified : 11/05/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------------

package tca.automata;

import tca.base.*;

public class TimeOrientedTCA extends TrafficCellularAutomaton
{
	// the TOCA's parameters
	public double fAverageTimeHeadway;
	public double fAccelerationProbability;
	public double fDecelerationProbability;

	public TimeOrientedTCA(State initialState, int maxSpeed, double cellLength, int nrOfLoopDetectors, int detectorRange, int loopDetectorMeasurementInterval, int multipleOfSampleTimeToSimulate, boolean performGlobalMeasurements, double averageTimeHeadway, double accelerationProbability, double decelerationProbability)
	{
		super(initialState,maxSpeed,cellLength,nrOfLoopDetectors,detectorRange,loopDetectorMeasurementInterval,multipleOfSampleTimeToSimulate,performGlobalMeasurements);
		fAverageTimeHeadway = averageTimeHeadway;
		fAccelerationProbability = accelerationProbability;
		fDecelerationProbability = decelerationProbability;
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, State nextState)
	{
		nextState.fCells[cellNr] = null;

		// rule 1: determine acceleration
		if ((sourceCell.fSpeed < getMaxSpeed(cellNr)) &&
			(sourceCell.fSpaceGap > (sourceCell.fSpeed * fAverageTimeHeadway)) &&
			(Math.random() < fAccelerationProbability)) {
			// note that we use random() < probability to capture the zero probability
			++sourceCell.fSpeed;
		}

		// rule 2: adjust speed in order to avoid collisions
		if (sourceCell.fSpeed > sourceCell.fSpaceGap) {
			sourceCell.fSpeed = sourceCell.fSpaceGap;
		}

		// rule 3: randomize
		if ((sourceCell.fSpeed > 0) &&
			(sourceCell.fSpaceGap < (sourceCell.fSpeed * fAverageTimeHeadway)) &&
			(Math.random() < fDecelerationProbability)) {
			// note that we use random() < probability to capture the zero probability
			--sourceCell.fSpeed;
		}

		// rule 4: clip to max speed
		if (sourceCell.fSpeed > getMaxSpeed(cellNr)) {
			sourceCell.fSpeed = getMaxSpeed(cellNr);
		}

		// try to stop at traffic light if necessary
		if ((cellNr == 0) && (fTrafficLightIsRed)) {
			sourceCell.fSpeed = 0;
		}

		// rule 5: advance vehicle
		int targetCellNr = fState.successor(cellNr,sourceCell.fSpeed);
		Cell targetCell = new Cell();
		sourceCell.copyTo(targetCell);
		nextState.fCells[targetCellNr] = targetCell;
	}
}
