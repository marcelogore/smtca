// ------------------------------------------------------
// Filename      : MultiCellTrafficCellularAutomaton.java
// Author        : Sven Maerivoet
// Last modified : 09/09/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------------------------------

package tca.base;

public class MultiCellTrafficCellularAutomaton
{
	// the automaton's general properties
	public MultiCellState fInitialState;
	public MultiCellState fState;
	public int            fMaxSpeed;
	public double         fCellLength;
	public int            fInitialMaxSpeed;

	public MultiCellTrafficCellularAutomaton(MultiCellState initialState, int maxSpeed, double cellLength)
	{
		fInitialState = initialState;
		setState(fInitialState);
		fMaxSpeed = maxSpeed;
		fInitialMaxSpeed = fMaxSpeed;
		fCellLength = cellLength;
		reset();
	}

	public void reset()
	{
		setState(fInitialState);
	}

	public void advanceOneStep()
	{
		fState.calcSpaceGaps();
		MultiCellState nextState = new MultiCellState(fState);

		applyRules(nextState);

		++nextState.fTime;

		fState = nextState;
	}

	public void setState(MultiCellState state)
	{
		fState = new MultiCellState(state);
	}

	protected void applyRules(MultiCellState nextState)
	{
		nextState.clearLattice();

		// process all cells
		for (int cellNr = 0; cellNr < fState.fCells.length; ++cellNr) {

			Cell sourceCell = fState.fCells[cellNr];

			if (sourceCell != null) {

				applyRulesToCell(sourceCell,cellNr,nextState);
			}
		}
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, MultiCellState nextState)
	{
	}

	public void setGlobalMaxSpeed(int maxSpeed)
	{
		fState.setGlobalMaxSpeed(maxSpeed);
		fMaxSpeed = maxSpeed;
	}

	public int getMaxSpeed(int cellNr)
	{
		Cell cell = fState.fCells[cellNr];

		if (cell != null) {
			return cell.fMaxSpeed;
		}
		else {
			return 0;
		}
	}
}
