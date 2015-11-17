// ----------------------------------
// Filename      : BrakeLightTCA.java
// Author        : Sven Maerivoet
// Last modified : 09/09/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ----------------------------------

package tca.automata;

import tca.base.*;

public class BrakeLightTCA extends MultiCellTrafficCellularAutomaton
{
	// the BL-TCA's parameters
	public double fPb;
	public double fP0;
	public double fPd;
	public int    fh;
	public int    fSecurityGap;

	public BrakeLightTCA(MultiCellState initialState, int maxSpeed, double cellLength, double Pb, double P0, double Pd, int fh_, int securityGap)
	{
		super(initialState,maxSpeed,cellLength);
		fPb = Pb;
		fP0 = P0;
		fPd = Pd;
		fh = fh_;
		fSecurityGap = securityGap;
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, MultiCellState nextState)
	{
		double th = Double.MAX_VALUE;
		if (sourceCell.fSpeed != 0) {
			th = (double) sourceCell.fSpaceGap / (double) sourceCell.fSpeed;
		}

		double ts = sourceCell.fSpeed;
		if (ts > fh) {
			ts = fh;
		}

		// find this vehicle's successor in order to retrieve its brake light status 
		Cell successorCell = sourceCell;
		if (fState.fNrOfVehicles > 1) {

			int cellOffset = 1;
			boolean successorFound = false;
			while (!successorFound) {

				successorCell = fState.fCells[fState.successor(cellNr,cellOffset)];
				if (successorCell != null) {
					successorFound = true;
				}
				else {
					++cellOffset;
				}
			}
		}

		// rule 0: determine randomisation
		double slowdownProbability = fPd;
		boolean brakingFlag = false;
		if ((successorCell.fBrakeLight == 1) && (th < ts)) {
			slowdownProbability = fPb;
			brakingFlag = true;
		}
		else if (sourceCell.fSpeed == 0) {
			slowdownProbability = fP0;
		}

		int newSpeed = sourceCell.fSpeed;
		int newBrakeLight = 0;

		// rule 1: acceleration
		if (((successorCell.fBrakeLight == 0) && (sourceCell.fBrakeLight == 0)) || (th >= ts)) {

			++newSpeed;

			if (newSpeed > getMaxSpeed(cellNr)) {
				newSpeed = getMaxSpeed(cellNr);
			}
		}

		// rule 2: braking
		int leadersAnticipatedSpeed = successorCell.fSpeed;
		if (successorCell.fSpaceGap < leadersAnticipatedSpeed) {
			leadersAnticipatedSpeed = successorCell.fSpaceGap;
		}
		int spaceGapOffset = leadersAnticipatedSpeed - fSecurityGap;
		if (spaceGapOffset < 0) {
			spaceGapOffset = 0;
		}
		int effectiveSpaceGap = sourceCell.fSpaceGap + spaceGapOffset;

		if (effectiveSpaceGap < newSpeed) {
			newSpeed = effectiveSpaceGap;
		}

		if (newSpeed < sourceCell.fSpeed) {
			newBrakeLight = 1;
		}

		// rule 3: randomisation braking
		if (Math.random() < slowdownProbability) {
			// note that we use random() < probability to capture the zero probability

			int oldSpeed = newSpeed;
			--newSpeed;
			if (newSpeed < 0) {
				newSpeed = 0;
			}

			if (brakingFlag && (newSpeed == (oldSpeed - 1))) {
				newBrakeLight = 1;
			}
		}

		sourceCell.fSpeed = newSpeed;
		sourceCell.fBrakeLight = newBrakeLight;

		// rule 4: advance vehicle
		int targetCellNr = fState.successor(cellNr,sourceCell.fSpeed);

		Cell targetCell = new Cell();
		sourceCell.copyTo(targetCell);
		nextState.fCells[targetCellNr] = targetCell;
	}
}
