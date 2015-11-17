// ------------------------------
// Filename      : CA184.java
// Author        : Sven Maerivoet
// Last modified : 12/12/2002
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------

package tca.automata;

import tca.base.*;

public class CA184 extends StochasticTCA
{
	public CA184(State initialState, int maxSpeed, double cellLength, int nrOfLoopDetectors, int detectorRange, int loopDetectorMeasurementInterval, int multipleOfSampleTimeToSimulate, boolean performGlobalMeasurements)
	{
		super(initialState,maxSpeed,cellLength,nrOfLoopDetectors,detectorRange,loopDetectorMeasurementInterval,multipleOfSampleTimeToSimulate,performGlobalMeasurements,0.0);
	}
}
