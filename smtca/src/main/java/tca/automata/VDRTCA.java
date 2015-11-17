// ------------------------------
// Filename      : VDRTCA.java
// Author        : Sven Maerivoet
// Last modified : 22/09/2002
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------

package tca.automata;

import tca.base.*;

public class VDRTCA extends StochasticTCA
{
	// the probability of a vehicle slowing down when standing still
	public double fSlowdownProbabilityWhenStandingStill;

	public VDRTCA(State initialState, int maxSpeed, double cellLength, int nrOfLoopDetectors, int detectorRange, int loopDetectorMeasurementInterval, int multipleOfSampleTimeToSimulate, boolean performGlobalMeasurements, double slowdownProbability, double slowdownProbabilityWhenStandingStill)
	{
		super(initialState,maxSpeed,cellLength,nrOfLoopDetectors,detectorRange,loopDetectorMeasurementInterval,multipleOfSampleTimeToSimulate,performGlobalMeasurements,slowdownProbability);
		fSlowdownProbabilityWhenStandingStill = slowdownProbabilityWhenStandingStill;
	}

	protected void applyRulesToCell(Cell sourceCell, int cellNr, State nextState)
	{
		double oldSlowdownProbability = fSlowdownProbability;

		// rule 0: determine velocity-dependent slowdown probability
		if (sourceCell.fSpeed == 0) {
			fSlowdownProbability = fSlowdownProbabilityWhenStandingStill;
		}

		// apply standard STCA-rules
		super.applyRulesToCell(sourceCell,cellNr,nextState);

		fSlowdownProbability = oldSlowdownProbability;
	}
}
