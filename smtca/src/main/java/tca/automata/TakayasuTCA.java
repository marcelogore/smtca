// --------------------------------
// Filename      : TakayasuTCA.java
// Author        : Sven Maerivoet
// Last modified : 06/05/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// --------------------------------

package tca.automata;

import tca.base.*;

public class TakayasuTCA extends StochasticTCA
{
	// the probability of a vehicle slowing down with small gaps
	public double fSlowdownProbabilityWithSmallGaps;

	public TakayasuTCA(State initialState, int maxSpeed, double cellLength, int nrOfLoopDetectors, int detectorRange, int loopDetectorMeasurementInterval, int multipleOfSampleTimeToSimulate, boolean performGlobalMeasurements, double slowdownProbability, double slowdownProbabilityWithSmallGaps)
	{
		super(initialState,maxSpeed,cellLength,nrOfLoopDetectors,detectorRange,loopDetectorMeasurementInterval,multipleOfSampleTimeToSimulate,performGlobalMeasurements,slowdownProbability);
		fSlowdownProbabilityWithSmallGaps = slowdownProbabilityWithSmallGaps;
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, State nextState)
	{
		nextState.fCells[cellNr] = null;

		// Takayasu and Takayasu's modified ruleset

		// rule 1: determine acceleration and braking
		if ((sourceCell.fSpeed == 0) && (sourceCell.fSpaceGap == 1)) {
			if (Math.random() < (1.0 - fSlowdownProbabilityWithSmallGaps)) {
				// note that we use random() < probability to capture the zero probability
				sourceCell.fSpeed = 1;
			}
		}
		else {
			++sourceCell.fSpeed;

			if (sourceCell.fSpeed > sourceCell.fSpaceGap) {
				sourceCell.fSpeed = sourceCell.fSpaceGap;
			}

			if (sourceCell.fSpeed > getMaxSpeed(cellNr)) {
				sourceCell.fSpeed = getMaxSpeed(cellNr);
			}
		}

		// rule 2: introduce stochastic noise
		if (sourceCell.fSpeed > 0) {
			if (Math.random() < fSlowdownProbability) {
				// note that we use random() < probability to capture the zero probability
				--sourceCell.fSpeed;
			}
		}

		// try to stop at traffic light if necessary
		if ((cellNr == 0) && (fTrafficLightIsRed)) {
			sourceCell.fSpeed = 0;
		}

		// rule 4: advance vehicle
		int targetCellNr = fState.successor(cellNr,sourceCell.fSpeed);
		Cell targetCell = new Cell();
		sourceCell.copyTo(targetCell);
		nextState.fCells[targetCellNr] = targetCell;
	}
}
