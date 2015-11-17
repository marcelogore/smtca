// --------------------------------------
// Filename      : FukuiIshibashiTCA.java
// Author        : Sven Maerivoet
// Last modified : 22/10/2003
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// --------------------------------------

package tca.automata;

import tca.base.*;

public class FukuiIshibashiTCA extends StochasticTCA
{
	public FukuiIshibashiTCA(State initialState, int maxSpeed, double cellLength, int nrOfLoopDetectors, int detectorRange, int loopDetectorMeasurementInterval, int multipleOfSampleTimeToSimulate, boolean performGlobalMeasurements, double slowdownProbability)
	{
		super(initialState,maxSpeed,cellLength,nrOfLoopDetectors,detectorRange,loopDetectorMeasurementInterval,multipleOfSampleTimeToSimulate,performGlobalMeasurements,slowdownProbability);
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, State nextState)
	{
		nextState.fCells[cellNr] = null;

		int maxSpeed = getMaxSpeed(cellNr);

		// rule 1: determine instantaneous acceleration and braking
		if (maxSpeed < sourceCell.fSpaceGap) {
			sourceCell.fSpeed = maxSpeed;
		}
		else {
			sourceCell.fSpeed = sourceCell.fSpaceGap;
		}

		// rule 2: randomize
		if ((sourceCell.fSpeed == maxSpeed) && (Math.random() < fSlowdownProbability)) {
			// note that we use random() < probability to capture the zero probability
			--sourceCell.fSpeed;
			if (sourceCell.fSpeed < 0) {
				sourceCell.fSpeed = 0;
			}			
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
