// ----------------------------------------
// Filename      : KernerKlenovWolfTCA.java
// Author        : Sven Maerivoet
// Last modified : 26/05/2005
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
 // ----------------------------------------

package tca.automata;

import tca.base.*;

public class KernerKlenovWolfTCA extends MultiCellTrafficCellularAutomaton
{
	// the KKW-TCA's parameters
	public int    fA;
	public int    fB;
	public int    fL;
	public int    fD0;
	public double fK;
	public double fPa1;
	public double fPa2;
	public double fVp;
	public double fSlowdownProbability;
	public double fSlowToStartProbability;

	public KernerKlenovWolfTCA(MultiCellState initialState, int maxSpeed, double cellLength, int a, int b, int l, int d0, double k, double pa1, double pa2, double vp, double slowdownProbability, double slowToStartProbability)
	{
		super(initialState,maxSpeed,cellLength);
		fA = a;
		fB = b;
		fL = l;
		fD0 = d0;
		fK = k;
		fPa1 = pa1;
		fPa2 = pa2;
		fVp = vp;
		fSlowdownProbability = slowdownProbability;
		fSlowToStartProbability = slowToStartProbability;
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, MultiCellState nextState)
	{
		// find this vehicle's successor in order to retrieve its speed
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

		// rule 1: determine desired speed
		double D = fD0 + (fK * sourceCell.fSpeed);

		int desiredSpeed = sourceCell.fSpeed + fA;

		if (sourceCell.fSpaceGap <= (D - fL)) {

			int deltaT = -fB;
			if (sourceCell.fSpeed == successorCell.fSpeed) {
				deltaT = 0;
			}
			else if (sourceCell.fSpeed < successorCell.fSpeed) {
				deltaT = fA;
			}

			desiredSpeed = sourceCell.fSpeed + deltaT;
		}

		// rule 2: determine deterministic speed (v = min{vMax,safeSpeed,desiredSpeed})
		int newSpeed = sourceCell.fMaxSpeed;
		int safeSpeed = sourceCell.fSpaceGap;

		if (newSpeed > safeSpeed) {
			newSpeed = safeSpeed;
		}

		if (newSpeed > desiredSpeed) {
			newSpeed = desiredSpeed;
		}

		// v = max{0,min{}})
		if (sourceCell.fSpeed < 0) {
			sourceCell.fSpeed = 0;
		}

		// rule 3: apply stochasticity to the speed update
		double pb = fSlowToStartProbability;
		if (sourceCell.fSpeed > 0) {
			pb = fSlowdownProbability;
		}

		double pa = fPa1;
		if (sourceCell.fSpeed >= fVp) {
			pa = fPa2;
		}

		int eta = 0;
		double r = Math.random();
		if (r < pb) {
			eta = -fB;
		}
		else if ((r >= pb) && (r < (pb + pa))) {
			eta = fA;
		}

		// v = min{v' + (a * eta),v' + a,vSafe,vMax}
		sourceCell.fSpeed = newSpeed + eta;

		if (sourceCell.fSpeed > (newSpeed + fA)) {
			sourceCell.fSpeed = newSpeed + fA;
		}

		if (sourceCell.fSpeed > safeSpeed) {
			sourceCell.fSpeed = safeSpeed;
		}

		if (sourceCell.fSpeed > sourceCell.fMaxSpeed) {
			sourceCell.fSpeed = sourceCell.fMaxSpeed;
		}

		// v = max{0,min{...}})
		if (sourceCell.fSpeed < 0) {
			sourceCell.fSpeed = 0;
		}

		// rule 4: advance vehicle
		int targetCellNr = fState.successor(cellNr,sourceCell.fSpeed);
		Cell targetCell = new Cell();
		sourceCell.copyTo(targetCell);
		nextState.fCells[targetCellNr] = targetCell;
	}
}
