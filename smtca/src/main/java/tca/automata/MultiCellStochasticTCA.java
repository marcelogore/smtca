// -------------------------------------------
// Filename      : MultiCellStochasticTCA.java
// Author        : Sven Maerivoet
// Last modified : 17/08/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// -------------------------------------------

package tca.automata;

import tca.base.*;

public class MultiCellStochasticTCA extends MultiCellTrafficCellularAutomaton
{
	// the probability of a vehicle slowing down
	public double fSlowdownProbability;

	public MultiCellStochasticTCA(MultiCellState initialState, int maxSpeed, double cellLength, double slowdownProbability)
	{
		super(initialState,maxSpeed,cellLength);
		fSlowdownProbability = slowdownProbability;
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, MultiCellState nextState)
	{
		nextState.fCells[cellNr] = null;

		// rule 1: determine braking and acceleration
		if (sourceCell.fSpeed > sourceCell.fSpaceGap) {
			sourceCell.fSpeed = sourceCell.fSpaceGap;
		}
		else if ((sourceCell.fSpeed < sourceCell.fSpaceGap) && (sourceCell.fSpeed < getMaxSpeed(cellNr))) {
			++sourceCell.fSpeed;
		}

		// rule 2: randomize
		if ((sourceCell.fSpeed > 0) && (Math.random() < fSlowdownProbability)) {
			// note that we use random() < probability to capture the zero probability
			--sourceCell.fSpeed;
		}

		// rule 3: advance vehicle
		int targetCellNr = fState.successor(cellNr,sourceCell.fSpeed);
		Cell targetCell = new Cell();
		sourceCell.copyTo(targetCell);
		nextState.fCells[targetCellNr] = targetCell;
	}
}
