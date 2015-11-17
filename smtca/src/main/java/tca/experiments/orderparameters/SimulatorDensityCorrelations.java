// -------------------------------------------------
// Filename      : SimulatorDensityCorrelations.java
// Author        : Sven Maerivoet
// Last modified : 30/05/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// -------------------------------------------------

// tca.experiments.orderparameters.SimulatorDensityCorrelations

package tca.experiments.orderparameters;

import smtools.miscellaneous.*;
import tca.automata.*;
import tca.base.*;

public class SimulatorDensityCorrelations
{
	private int                      fNrOfCells;
	private TrafficCellularAutomaton fTCA;
	private int                      fVehiclesCounted;
	private int                      fMaxSpeed;

	public SimulatorDensityCorrelations()
	{
		fNrOfCells                      = 300;
		int nrOfDensitySteps            = fNrOfCells / 30;
		int transientPeriod             = 1000;
		int simulationPeriod            = 100000;
		int correlationRange            = 20;
		int nrOfSpaceCorrelations       = (2 * correlationRange) + 1;
		int timeCorrelationLength       = 1;
		fMaxSpeed                       = 5;
		double slowdownProbability      = 0.01;
		double slowToStartProbability   = 0.5;
		boolean distributeHomogeneously = true;

		String filename =
//			"u:\\" +
			"density-correlations-" +
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
			"-scl" + String.valueOf(nrOfSpaceCorrelations) +
			"-tcl" + String.valueOf(timeCorrelationLength) +
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

		double[] densities = new double[nrOfDensitySteps];
		double[][] densityCorrelations = new double[nrOfDensitySteps][nrOfSpaceCorrelations];

		Chrono chrono = new Chrono();

		for (int densityStep = (nrOfDensitySteps - 1); densityStep > 0; --densityStep) {

			double globalDensity = ((double) densityStep / (double) nrOfDensitySteps);
/*
			double startDensity = 0.05;
			double endDensity = 0.20;
			double densityFraction = ((double) densityStep / (double) nrOfDensitySteps);

			double globalDensity = startDensity + ((endDensity - startDensity) * densityFraction);
*/

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

			State[] stateHistory = null;

			if (timeCorrelationLength > 0) {

				stateHistory = new State[timeCorrelationLength];

				// initialize state history
				for (int timeStep = 0; timeStep < timeCorrelationLength; ++timeStep) {
					// advance cellular automaton one step
					fTCA.advanceOneStep();

					stateHistory[timeStep] = new State(fTCA.fState);
					fTCA.fState.copyTo(stateHistory[timeStep]);
				}
			}

			int currentHistoricState = 0;

			for (int timeStep = 0; timeStep < simulationPeriod; ++timeStep) {
				// advance cellular automaton one step
				fTCA.advanceOneStep();

				// calculate density correlations
				for (int cellNr = 0; cellNr < fTCA.fState.fCells.length; ++cellNr) {

					for (int spaceCorrelation = -correlationRange; spaceCorrelation <= correlationRange; ++spaceCorrelation) {

						Cell cell = fTCA.fState.fCells[cellNr];
						Cell correlatedCell = null;

						int successorCellNr = cellNr;

						if (spaceCorrelation < 0) {
							successorCellNr = fTCA.fState.predecessor(cellNr,-spaceCorrelation);
						}
						else if (spaceCorrelation > 0) {
							successorCellNr = fTCA.fState.successor(cellNr,spaceCorrelation);
						}

						if (timeCorrelationLength > 0) {
							// find correlation in state history
							correlatedCell = stateHistory[currentHistoricState].fCells[successorCellNr];
						}
						else {
							// find correlation in current state
							correlatedCell = fTCA.fState.fCells[successorCellNr];
						}

						if ((cell != null) && (correlatedCell != null)) {
							densityCorrelations[densityStep][spaceCorrelation + correlationRange] += 1.0;
						}
					}
				}

				if (timeCorrelationLength > 0) {

					// update historic state with current information
					fTCA.fState.copyTo(stateHistory[currentHistoricState]);

					// cycle to next historic state
					currentHistoricState = (currentHistoricState + 1) % timeCorrelationLength;
				}
			}

			densities[densityStep] = globalDensity;

			for (int spaceCorrelation = -correlationRange; spaceCorrelation <= correlationRange; ++spaceCorrelation) {
				densityCorrelations[densityStep][spaceCorrelation + correlationRange] /= (double) (fNrOfCells * simulationPeriod);
				densityCorrelations[densityStep][spaceCorrelation + correlationRange] -= (globalDensity * globalDensity);
			}

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
				detectorFile.writeDouble(densities[densityStep]);
 				detectorFile.writeString("\t");

				for (int spaceCorrelation = -correlationRange; spaceCorrelation <= correlationRange; ++spaceCorrelation) {
					detectorFile.writeDouble(densityCorrelations[densityStep][spaceCorrelation + correlationRange]);
	 				detectorFile.writeString("\t");
				}

				detectorFile.writeLn();
			}
		}
		catch (Exception exc) {
		}
	}

	public static void main(String[] args) 
	{
		SimulatorDensityCorrelations simulator = new SimulatorDensityCorrelations();
	}
}
