// -----------------------------------
// Filename      : MultiCellState.java
// Author        : Sven Maerivoet
// Last modified : 09/09/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// -----------------------------------

package tca.base;

import java.util.*;

public class MultiCellState
{
	// the state's datastructures
	public Cell[] fCells;
	public int    fTime;
	public int    fNrOfVehicles;

	public MultiCellState(int nrOfCells)
	{
		fCells = new Cell[nrOfCells];
		fTime = 0;
		fNrOfVehicles = 0;
	}

	public MultiCellState(MultiCellState state)
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

	public void clearLattice()
	{
		for (int cellNr = 0; cellNr < fCells.length; ++cellNr) {
			fCells[cellNr] = null;
		}
	}

	public String getTextRepresentation()
	{
		char[] charTable =
			{'0','1','2','3','4','5','6','7','8','9','a',
			'b','c','d','e','f','g','h','i','j','k',
			'l','m','n','o','p','q','r','s','t'};
		String textRepresentation = "t" + String.valueOf(fTime) + "\t";

		for (int i = 0; i < fCells.length; ++i) {

			Cell cell = fCells[i];

			if (cell != null) {
				textRepresentation += charTable[cell.fSpeed];
			}
			else {
				textRepresentation += ".";
			}
 		}

		return textRepresentation;
	}

	public void distributeVehicles(int nrOfVehicles, int maxSpeed, int vehicleLength, boolean distributeHomogeneously)
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

		if (distributeHomogeneously) {

			// how many vehicles can be distributed homogeneously ?
			int nrOfHomogeneouslyPlacedVehicles = fCells.length / (vehicleLength + maxSpeed);

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

				cell.fVehicleLength = vehicleLength;

				fCells[vehiclePosition] = cell;

				vehiclePosition += (vehicleLength + maxSpeed);
			}

			// distribute excess of vehicles uniformly among the free positions
			int vehiclesLeftToDistribute = nrOfVehicles - nrOfHomogeneouslyPlacedVehicles;
			if (vehiclesLeftToDistribute < 0) {
				vehiclesLeftToDistribute = 0;
			}

			// try to distribute the rest of the vehicles
			if ((vehiclesLeftToDistribute > 0) && (maxSpeed >= vehicleLength)) {

				int nrOfVehiclesBetweenHomogeneousPlaces = maxSpeed / vehicleLength;

				// generate a map with predefined positions
				Vector vehiclePositions = new Vector(vehiclesLeftToDistribute);

				for (int freeSlots = 0; freeSlots < nrOfHomogeneouslyPlacedVehicles; ++freeSlots) {
					for (int betweenCounter = 0; betweenCounter < nrOfVehiclesBetweenHomogeneousPlaces; ++betweenCounter) {

						int latticePosition =
							(freeSlots * (vehicleLength + maxSpeed)) +
							(betweenCounter + 1) * vehicleLength;

						vehiclePositions.add(new Integer(latticePosition));
					}
				}

				if (vehiclesLeftToDistribute > vehiclePositions.size()) {
					vehiclesLeftToDistribute = vehiclePositions.size();
				}

				// fill cells
				for (int i = 0; i < vehiclesLeftToDistribute; ++i) {

					// pick a position from the map (and remove it after placing a vehicle)
					int randomVehiclePositionID = (int) Math.floor((Math.random() * vehiclePositions.size()));
					vehiclePosition = ((Integer) vehiclePositions.get(randomVehiclePositionID)).intValue();
					vehiclePositions.remove(randomVehiclePositionID);

					Cell cell = new Cell();
					cell.fMaxSpeed = maxSpeed;
					cell.fSpeed = maxSpeed;

					int randomVehicleID = (int) Math.floor((Math.random() * vehicleIDs.size()));
					cell.fVehicleID = ((Integer) vehicleIDs.get(randomVehicleID)).intValue();
					vehicleIDs.remove(randomVehicleID);

					cell.fVehicleLength = vehicleLength;

					fCells[vehiclePosition] = cell;
				}

				nrOfVehicles = nrOfHomogeneouslyPlacedVehicles + vehiclesLeftToDistribute;
			}
		}
		else {

			// create a large compact megajam of standing cars
			int vehiclePosition = 0;

			for (int vehicleNr = 0; vehicleNr < nrOfVehicles; ++vehicleNr) {

				Cell cell = new Cell();
				cell.fMaxSpeed = maxSpeed;
				cell.fSpeed = 0;

				// select and remove a random vehicle ID from the list
				int randomVehicleID = (int) Math.floor((Math.random() * vehicleIDs.size()));
				cell.fVehicleID = ((Integer) vehicleIDs.get(randomVehicleID)).intValue();
				vehicleIDs.remove(randomVehicleID);

				cell.fVehicleLength = vehicleLength;
				fCells[vehiclePosition] = cell;

				vehiclePosition += cell.fVehicleLength;
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

			// is there exactly one vehicle present ?
			if (fNrOfVehicles == 1) {
				Cell cell = fCells[firstVehiclePosition];
				cell.fSpaceGap = fCells.length - cell.fVehicleLength;
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
					fCells[lastKnownVehiclePosition].fSpaceGap =
						currentCell - lastKnownVehiclePosition - fCells[lastKnownVehiclePosition].fVehicleLength;
					lastKnownVehiclePosition = currentCell;
				}

				++currentCell;
			}

			// calc space gap for last vehicle
			fCells[lastKnownVehiclePosition].fSpaceGap =
				firstVehiclePosition + (fCells.length - lastKnownVehiclePosition) - fCells[lastKnownVehiclePosition].fVehicleLength;
		}
		catch (NullPointerException exc) {
			// just in case ...
		}
	}

	public void copyTo(MultiCellState state)
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
