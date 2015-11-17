// ------------------------------------------------------------
// Filename      : SimulatorNearestNeighbourOrderParameter.java
// Author        : Sven Maerivoet
// Last modified : 30/05/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------------------------------------

// tca.experiments.orderparameters.SimulatorNearestNeighbourOrderParameter

package tca.experiments.orderparameters;

import smtools.miscellaneous.*;
import tca.automata.*;
import tca.base.*;

public class SimulatorNearestNeighbourOrderParameter
{
	private int                      fNrOfCells;
	private TrafficCellularAutomaton fTCA;
	private int                      fVehiclesCounted;
	private int                      fMaxSpeed;

	public SimulatorNearestNeighbourOrderParameter()
	{
		fNrOfCells                      = 300;
		int nrOfDensitySteps            = fNrOfCells / 2;
		int transientPeriod             = 1000;
		int simulationPeriod            = 100000;
		fMaxSpeed                       = 5;
		double slowdownProbability      = 0.01;
		double slowToStartProbability   = 0.5;
		double averageTimeGap           = 1.12;
		double accelerationProbability  = 0.9;
		double decelerationProbability  = 0.9;
		boolean distributeHomogeneously = true;

		String filename =
//			"u:\\" +
			"nn-order-parameter-" +
//			"homogeneous-" +
//			"compactjam-" +
			"ca184" +
//			"stca" +
//			"stcacc" +
//			"vdrtca" +
//			"vdrcctca" +
//			"fi" +
//			"originaltakayasu2" +
//			"takayasu2" +
//			"toca" +
//			"tasep" +
			"-c" + String.valueOf(fNrOfCells) +
			"-s" + String.valueOf(nrOfDensitySteps) +
			"-tp" + String.valueOf(transientPeriod) +
			"-sp" + String.valueOf(simulationPeriod) +
			"-vmax" + String.valueOf(fMaxSpeed) +
//			"-sp" + String.valueOf(slowdownProbability) +
//			"-s2sp" + String.valueOf(slowToStartProbability) +
//			"-atg" + String.valueOf(averageTimeGap) +
//			"-ap" + String.valueOf(accelerationProbability) +
//			"-dp" + String.valueOf(decelerationProbability) +
			".data";

		double cellLength = 7.5;

		TextFileWriter detectorFile = null;
		try {
			detectorFile = new TextFileWriter(filename);
		}
		catch (Exception exc) {
		}

		double[][] orderParameters = new double[nrOfDensitySteps][2];

		Chrono chrono = new Chrono();

		for (int densityStep = (nrOfDensitySteps - 1); densityStep > 0; --densityStep) {

			double globalDensity = ((double) densityStep / (double) nrOfDensitySteps);
			int nrOfVehicles = (int) Math.round(globalDensity * fNrOfCells);

	    // create the TCA's initial state
			State initialState = new State(fNrOfCells);
			initialState.distributeVehicles(nrOfVehicles,fMaxSpeed,distributeHomogeneously,!distributeHomogeneously);

			fTCA =
				new CA184(initialState,fMaxSpeed,cellLength,14,fNrOfCells / 14,60,1,false);
//				new StochasticTCA(initialState,fMaxSpeed,cellLength,14,fNrOfCells / 14,60,1,false,slowdownProbability);
//				new StochasticCCTCA(initialState,fMaxSpeed,cellLength,14,fNrOfCells / 14,60,1,false,slowdownProbability);
//				new VDRTCA(initialState,fMaxSpeed,cellLength,14,fNrOfCells / 14,60,1,false,slowdownProbability,slowToStartProbability);
//				new VDRCCTCA(initialState,fMaxSpeed,cellLength,14,fNrOfCells / 14,60,1,false,slowdownProbability,slowToStartProbability);
//				new FukuiIshibashiTCA(initialState,fMaxSpeed,cellLength,14,fNrOfCells / 14,60,1,false,slowdownProbability);
//				new OriginalTakayasuTCA(initialState,fMaxSpeed,cellLength,14,fNrOfCells / 14,60,1,false);
//				new TakayasuTCA(initialState,fMaxSpeed,cellLength,14,fNrOfCells / 14,60,1,false,slowdownProbability,slowToStartProbability);
//				new TimeOrientedTCA(initialState,fMaxSpeed,cellLength,14,fNrOfCells / 14,60,1,false,averageTimeGap,accelerationProbability,decelerationProbability);
//				new TASEP(initialState,fMaxSpeed,cellLength,14,fNrOfCells / 14,60,1,false);

			TrafficCellularAutomaton.kLoopDetectorsEnabled = false;

			// allow transient period
			for (int timeStep = 0; timeStep < transientPeriod; ++timeStep) {
				// advance cellular automaton one step
				fTCA.advanceOneStep();
			}
			fTCA.fState.calcSpaceGaps();

			int orderParameter = 0;

			for (int timeStep = 0; timeStep < simulationPeriod; ++timeStep) {
				// advance cellular automaton one step
				fTCA.advanceOneStep();

				// calculate order parameter
				for (int cellNr = 0; cellNr < fTCA.fState.fCells.length; ++cellNr) {

					Cell cell = fTCA.fState.fCells[cellNr];
					Cell nextCell = fTCA.fState.fCells[fTCA.fState.successor(cellNr,1)];

					if ((cell != null) && (nextCell != null)) {
						++orderParameter;
					}
				}
			}

			orderParameters[densityStep][0] = globalDensity;
			orderParameters[densityStep][1] = (double) orderParameter / (double) (fNrOfCells * simulationPeriod);

			double percentCompleted = 100.0 - (((double) densityStep / (double) (nrOfDensitySteps - 2)) * 100.0);

			if (percentCompleted >= 1.0) {

				// compute the time needed for the total computation
				double totalTimeNeeded = ((double) chrono.getElapsedTimeInMilliseconds() / percentCompleted) * 100.0;

				// compute the time left for completion
				int timeLeft = (int) Math.round(totalTimeNeeded - (percentCompleted * (totalTimeNeeded / 100.0)));
				String timeSpentString = "";
				String timeLeftString = "";
				try {
					timeSpentString = TimeFormatter.getTruncatedTimeString(new Time(chrono.getElapsedTimeInMilliseconds()));
					timeLeftString = TimeFormatter.getTruncatedTimeString(new Time(timeLeft));
				}
				catch (Exception exc) {
				}

				System.out.println(timeSpentString + " / " + timeLeftString + " [" + String.valueOf((int) Math.floor(percentCompleted)) + "%]");
			}
		}

		try {
			for (int densityStep = 0; densityStep < nrOfDensitySteps; ++densityStep) {
				detectorFile.writeDouble(orderParameters[densityStep][0]);
				detectorFile.writeString("\t");
				detectorFile.writeDouble(orderParameters[densityStep][1]);
				detectorFile.writeLn();
			}
		}
		catch (Exception exc) {
		}
	}

	public static void main(String[] args) 
	{
		SimulatorNearestNeighbourOrderParameter simulator = new SimulatorNearestNeighbourOrderParameter();
	}
}
