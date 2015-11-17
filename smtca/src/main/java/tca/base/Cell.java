// ------------------------------
// Filename      : Cell.java
// Author        : Sven Maerivoet
// Last modified : 12/08/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------

package tca.base;

public class Cell
{
	// cell-specific data
	public int fMaxSpeed;
	public int fSpeed;
	public int fSpaceGap;
	public int fVehicleID;
	public int fVehicleLength;
	public int fBrakeLight;

	public Cell()
	{
		clear();
	}

	public void clear()
	{
		fMaxSpeed = 0;
		fSpeed = 0;
		fSpaceGap = 0;
		fVehicleID = 0;
		fVehicleLength = 1;
		fBrakeLight = 0;
	}

	public void copyTo(Cell cell)
	{
		cell.fMaxSpeed = fMaxSpeed;
		cell.fSpeed = fSpeed;
		cell.fSpaceGap = fSpaceGap;
		cell.fVehicleID = fVehicleID;
		cell.fVehicleLength = fVehicleLength;
		cell.fBrakeLight = fBrakeLight;
	}
}
