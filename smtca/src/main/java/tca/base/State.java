// ------------------------------
// Filename      : State.java
// Author        : Sven Maerivoet
// Last modified : 13/08/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------

package tca.base;

import java.util.*;

public class State
{
	// the state's datastructures
	public Cell[] fCells;
	public int    fTime;
	public int    fNrOfVehicles;

	public State(int nrOfCells)
	{
		fCells = new Cell[nrOfCells];
		fTime = 0;
		fNrOfVehicles = 0;
	}

	public State(State state)
	{
		this(state.fCells.length);
		state.copyTo(this);
	}

	public void clear()
	{
		int nrOfCells = fCells.length;
		fCells = new Cell[nrOfCells];

		fTime = 0;
		fNrOfVehicles = 0;
	}

	public String getTextRepresentation()
	{
		String textRepresentation = "t" + String.valueOf(fTime) + "\t";

		for (int i = 0; i < fCells.length; ++i) {
			Cell cell = fCells[i];
			if (cell != null) {
				textRepresentation += String.valueOf(cell.fSpeed);
			}
			else {
				textRepresentation += ".";
			}
		}

		return textRepresentation;
	}

	public void distributeVehicles(int nrOfVehicles, int maxSpeed, boolean distributeHomogeneously, boolean startWithCompactJam)
	{
		// clear lattice
		for (int cellNr = 0; cellNr < fCells.length; ++cellNr) {
			fCells[cellNr] = null;
		}

		// create a set containing unique vehicle IDs
		Vector vehicleIDs = new Vector(nrOfVehicles);
		for (int vehicleID = 0; vehicleID < nrOfVehicles; ++vehicleID) {
			vehicleIDs.add(new Integer(vehicleID));
		}

		if (startWithCompactJam) {

			// create a large compact megajam of standing cars
			for (int vehicleNr = 0; vehicleNr < nrOfVehicles; ++vehicleNr) {

				Cell cell = new Cell();
				cell.fMaxSpeed = maxSpeed;
				cell.fSpeed = 0;
				cell.fVehicleID = vehicleNr;
				fCells[vehicleNr] = cell;

				int randomVehicleID = (int) Math.floor((Math.random() * vehicleIDs.size()));
				cell.fVehicleID = ((Integer) vehicleIDs.get(randomVehicleID)).intValue();
				vehicleIDs.remove(randomVehicleID);
			}
		}
		else if (distributeHomogeneously) {

			// how many vehicles can be distributed homogeneously ?
			int nrOfHomogeneouslyPlacedVehicles = (int) Math.floor((double) fCells.length / (double) (1 + maxSpeed));

			if (nrOfVehicles < nrOfHomogeneouslyPlacedVehicles) {
				nrOfHomogeneouslyPlacedVehicles = nrOfVehicles;
			}

			// homogeneously distribute some vehicles over the lattice
			int vehiclePosition = 0;
			for (int vehicleNr = 0; vehicleNr < nrOfHomogeneouslyPlacedVehicles; ++vehicleNr) {

				Cell cell = new Cell();
				cell.fMaxSpeed = maxSpeed;
				cell.fSpeed = maxSpeed;

				int randomVehicleID = (int) Math.floor((Math.random() * vehicleIDs.size()));
				cell.fVehicleID = ((Integer) vehicleIDs.get(randomVehicleID)).intValue();
				vehicleIDs.remove(randomVehicleID);

				fCells[vehiclePosition] = cell;

				vehiclePosition += (1 + maxSpeed);
			}

			// distribute excess of vehicles uniformly among the free positions
			int vehiclesLeftToDistribute = nrOfVehicles - nrOfHomogeneouslyPlacedVehicles;

			if (vehiclesLeftToDistribute > 0) {

				// fill cells
				for (int i = 0; i < vehiclesLeftToDistribute; ++i) {

					Cell cell = new Cell();
					cell.fMaxSpeed = maxSpeed;
					cell.fSpeed = maxSpeed;

					int randomVehicleID = (int) Math.floor((Math.random() * vehicleIDs.size()));
					cell.fVehicleID = ((Integer) vehicleIDs.get(randomVehicleID)).intValue();
					vehicleIDs.remove(randomVehicleID);

					// find a suitable free position
					boolean positionOk = false;
					while (!positionOk) {				
						vehiclePosition = (int) (Math.random() * fCells.length);

						if (fCells[vehiclePosition] == null) {
							positionOk = true;
						}
					}
				
					fCells[vehiclePosition] = cell;
				}
			}
		}
		else {
			// random distribution

			// generate map of positions
			int[] positions = new int[nrOfVehicles];

			for (int vehicleNr = 0; vehicleNr < nrOfVehicles; ++vehicleNr) {

				// find a suitable position for the vehicle
				int position = 0;

				boolean positionOk = false;
				while (!positionOk) {
					position = (int) (Math.random() * fCells.length);

					boolean positionEncountered = false;
					for (int i = 0; i < vehicleNr; ++i) {
						if (positions[i] == position) {
							positionEncountered = true;
						}
					}

					positionOk = (positionEncountered == false);
				}

				positions[vehicleNr] = position;
			}

			// fill cells
			for (int i = 0; i < nrOfVehicles; ++i) {
				Cell cell = new Cell();
				cell.fMaxSpeed = maxSpeed;
				cell.fSpeed = maxSpeed;
				cell.fVehicleID = i;
				fCells[positions[i]] = cell;
			}
		}

		fNrOfVehicles = nrOfVehicles;
	}

	public void setGlobalMaxSpeed(int maxSpeed)
	{
		for (int cellNr = 0; cellNr < fCells.length; ++cellNr) {
			Cell cell = fCells[cellNr];
			if (cell != null) {
				cell.fMaxSpeed = maxSpeed;
			}
		}
	}

	public void calcSpaceGaps()
	{
		try {
			// find position of first vehicle
			int firstVehiclePosition = 0;

			boolean firstVehicleFound = false;
			while ((!firstVehicleFound) && (firstVehiclePosition < fCells.length)) {

				Cell cell = fCells[firstVehiclePosition];
				if (cell != null) {
					firstVehicleFound = true;
				}
				else {
					++firstVehiclePosition;
				}
			}

			if (!firstVehicleFound) {
				// no vehicles present in cellular automaton
				return;
			}

			// is there exactly one vehicle present at the end of the cellular automaton's lattice ?
			if ((firstVehiclePosition == (fCells.length - 1)) || (fNrOfVehicles == 1)) {
				Cell cell = fCells[firstVehiclePosition];
				cell.fSpaceGap = fCells.length - 1;
				return;
			}

			// calc space gaps for all vehicles
			int lastKnownVehiclePosition = firstVehiclePosition;
			int currentCell = lastKnownVehiclePosition + 1;

			// scan all subsequent cells in the lattice
			while (currentCell < fCells.length) {

				// find next vehicle
				Cell cell = fCells[currentCell];
				if (cell != null) {

					// calc gap with previous vehicle
					fCells[lastKnownVehiclePosition].fSpaceGap = currentCell - lastKnownVehiclePosition - 1;
					lastKnownVehiclePosition = currentCell;
				}

				++currentCell;
			}

			// calc space gap for last vehicle
			fCells[lastKnownVehiclePosition].fSpaceGap = (fCells.length - lastKnownVehiclePosition - 1) + firstVehiclePosition;
		}
		catch (NullPointerException exc) {
			// just in case ...
		}
	}

	public void copyTo(State state)
	{
		for (int cellNr = 0; cellNr < fCells.length; ++cellNr) {

			Cell cell = fCells[cellNr];

			if (cell != null) {
				state.fCells[cellNr] = new Cell();
				cell.copyTo(state.fCells[cellNr]);
			}
		}

		state.fTime = fTime;
		state.fNrOfVehicles = fNrOfVehicles;
	}

	public int predecessor(int cellNr, int cellsToSkip)
	{
		int pred = cellNr - cellsToSkip;
		if (pred < 0) {
			pred += fCells.length;
		}

		return pred;
	}

	public int successor(int cellNr, int cellsToSkip)
	{
		return ((cellNr + cellsToSkip) % fCells.length);
	}
}
