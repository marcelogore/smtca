// --------------------------------------------
// Filename      : HelbingSchreckenbergTCA.java
// Author        : Sven Maerivoet
// Last modified : 24/05/2005
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// --------------------------------------------

package tca.automata;

import tca.base.*;

public class HelbingSchreckenbergTCA extends MultiCellTrafficCellularAutomaton
{
	// the HS-TCA's parameters
	public double fSlowdownProbability;
	public double fLambda;

	// the optimal velocity matrix
	int[] fOVMatrix =
		{0,0,   // 0,1
		 1,1,   // 2,3
		 2,2,   // 4,5
		 3,     // 6
		 4,     // 7
		 5,     // 8
		 6,     // 9
		 7,     // 10
		 8,     // 11
		 9,     // 12
		 10,    // 13
		 11,11, // 14,15
		 12,12,12, // 16,17,18
		 13,13,13,13,13, // 19,20,21,22,23
		 14,14,14,14,14,14,14,14,14,14,14,14,14, // 24,25,26,27,28,29,30,31,32,33,34,35,36
		 15     // >= 37		 
		};

	public HelbingSchreckenbergTCA(MultiCellState initialState, int maxSpeed, double cellLength, double slowdownProbability, double lambda)
	{
		super(initialState,maxSpeed,cellLength);
		fSlowdownProbability = slowdownProbability;
		fLambda = lambda;
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, MultiCellState nextState)
	{
		// rule 1: define optimal velocity
		int optimalVelocity = fOVMatrix[37];
		if ((sourceCell.fSpaceGap >= 0) && (sourceCell.fSpaceGap < 37)) {
			optimalVelocity = fOVMatrix[sourceCell.fSpaceGap];
		}

		// rule 2: determine speed
		double speedOffset = fLambda * (optimalVelocity - sourceCell.fSpeed);
		sourceCell.fSpeed += (int) Math.floor(speedOffset);

		if (sourceCell.fSpeed < 0) {
			sourceCell.fSpeed = 0;
		}

		// rule 3: randomize
		if ((sourceCell.fSpeed > 0) && (Math.random() < fSlowdownProbability)) {
			// note that we use random() < probability to capture the zero probability
			--sourceCell.fSpeed;
		}
		
		// rule 4: advance vehicle
		int targetCellNr = fState.successor(cellNr,sourceCell.fSpeed);
		Cell targetCell = new Cell();
		sourceCell.copyTo(targetCell);
		nextState.fCells[targetCellNr] = targetCell;
	}
}
