// ------------------------------------
// Filename      : EmmerichRankTCA.java
// Author        : Sven Maerivoet
// Last modified : 12/08/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------------

package tca.automata;

import tca.base.*;

public class EmmerichRankTCA extends StochasticTCA
{
	// the slowdown matrix
	int[][] fSlowdownMatrix;

	public EmmerichRankTCA(State initialState, int maxSpeed, double cellLength, int nrOfLoopDetectors, int detectorRange, int loopDetectorMeasurementInterval, int multipleOfSampleTimeToSimulate, boolean performGlobalMeasurements, double slowdownProbability)
	{
		super(initialState,maxSpeed,cellLength,nrOfLoopDetectors,detectorRange,loopDetectorMeasurementInterval,multipleOfSampleTimeToSimulate,performGlobalMeasurements,slowdownProbability);

		// setup the slowdown matrix
		int[] fSlowdownMatrixRow0 = {0,0,0,0,0,0};
		int[] fSlowdownMatrixRow1 = {0,1,1,1,1,1};
		int[] fSlowdownMatrixRow2 = {0,1,2,2,2,2};
		int[] fSlowdownMatrixRow3 = {0,1,2,3,3,3};
		int[] fSlowdownMatrixRow4 = {0,1,2,3,4,4};
		int[] fSlowdownMatrixRow5 = {0,1,2,3,4,4};
		int[] fSlowdownMatrixRow6 = {0,1,2,3,4,4};
		int[] fSlowdownMatrixRow7 = {0,1,2,3,4,4};
		int[] fSlowdownMatrixRow8 = {0,1,2,3,4,4};
		int[] fSlowdownMatrixRow9 = {0,1,2,3,4,4};
		int[] fSlowdownMatrixRow10 = {0,1,2,3,4,5};
		fSlowdownMatrix = new int[11][6];
		fSlowdownMatrix[0] = fSlowdownMatrixRow0;
		fSlowdownMatrix[1] = fSlowdownMatrixRow1;
		fSlowdownMatrix[2] = fSlowdownMatrixRow2;
		fSlowdownMatrix[3] = fSlowdownMatrixRow3;
		fSlowdownMatrix[4] = fSlowdownMatrixRow4;
		fSlowdownMatrix[5] = fSlowdownMatrixRow5;
		fSlowdownMatrix[6] = fSlowdownMatrixRow6;
		fSlowdownMatrix[7] = fSlowdownMatrixRow7;
		fSlowdownMatrix[8] = fSlowdownMatrixRow8;
		fSlowdownMatrix[9] = fSlowdownMatrixRow9;
		fSlowdownMatrix[10] = fSlowdownMatrixRow10;
	}

	protected void applyRules(State nextState)
	{
		if (fState.fNrOfVehicles == 0) {
			return;
		}

		fState.calcSpaceGaps();
		fState.copyTo(nextState);

		// find the first vehicle in the lattice
		int firstCellNr = 0;
		boolean found = false;
		while ((!found) && (firstCellNr < fState.fCells.length)) {

			Cell sourceCell = fState.fCells[firstCellNr];
			if (sourceCell != null) {
				found = true;
			}
			else {
				++firstCellNr;
			}
		}

		// find the first vehicle with the largest space gap
		int largestSpaceGapCellNr = firstCellNr;
		for (int cellNr = 0; cellNr < fState.fCells.length; ++cellNr) {

			Cell cell = fState.fCells[cellNr];
			if (cell != null) {

				if (cell.fSpaceGap > fState.fCells[largestSpaceGapCellNr].fSpaceGap) {
					largestSpaceGapCellNr = cellNr;
				}
			}
		}

		// update the vehicle's position
		// processing vehicles in the upstream direction
		int cellNr = largestSpaceGapCellNr;
		int successorSpeed = 0;
		for (int cellsProcessed = 0; cellsProcessed < fState.fCells.length; ++cellsProcessed) {

			Cell sourceCell = fState.fCells[cellNr];
			if (sourceCell != null) {

				// adapt the vehicle's space gap due to the possible movement of its leader
				sourceCell.fSpaceGap += successorSpeed;

				applyRulesToCell(sourceCell,cellNr,nextState);
				successorSpeed = sourceCell.fSpeed;
			}

			--cellNr;
			if (cellNr < 0) {
				cellNr = (fState.fCells.length - 1);
			}
		}

		fState = new State(nextState);
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, State nextState)
	{
		nextState.fCells[cellNr] = null;

		// rule 1: determine acceleration
		if ((sourceCell.fSpeed < getMaxSpeed(cellNr)) && (sourceCell.fSpeed < sourceCell.fSpaceGap)) {
			++sourceCell.fSpeed;
		}

		// rule 2: slowing down due to other vehicles
		if (sourceCell.fSpaceGap <= 10) {
			sourceCell.fSpeed = fSlowdownMatrix[sourceCell.fSpaceGap][sourceCell.fSpeed];
		}

		// rule 3: randomisation
		if ((sourceCell.fSpeed > 0) && (Math.random() < fSlowdownProbability)) {
			// note that we use random() < probability to capture the zero probability
			--sourceCell.fSpeed;
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
