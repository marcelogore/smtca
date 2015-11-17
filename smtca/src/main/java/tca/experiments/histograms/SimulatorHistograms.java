// ----------------------------------------
// Filename      : SimulatorHistograms.java
// Author        : Sven Maerivoet
// Last modified : 25/08/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ----------------------------------------

// tca.experiments.histograms.SimulatorHistograms

package tca.experiments.histograms;

import smtools.exceptions.*;
import smtools.miscellaneous.*;
import tca.automata.*;
import tca.base.*;

public class SimulatorHistograms
{
	private int                      fNrOfCells;
	private TrafficCellularAutomaton fTCA;
	private int                      fVehiclesCounted;
	private int                      fMaxSpeed;

	public SimulatorHistograms()
	{
		fNrOfCells                      = 300;
		int nrOfDensitySteps            = 150;
		int transientPeriod             = 500;
		int simulationPeriod            = 50000;

		double timeStepDuration         = 1.0;
		fMaxSpeed                       = 5;
		double slowdownProbability      = 0.01;
		double slowToStartProbability   = 0.5;
		double averageTimeGap           = 1.12;
		double accelerationProbability  = 0.9;
		double decelerationProbability  = 0.9;
		boolean distributeHomogeneously = true;

		Chrono chrono = new Chrono();

//		for (int densityStep = nrOfDensitySteps; densityStep >= 1; --densityStep) {
		for (int densityStep = 1; densityStep <= nrOfDensitySteps; ++densityStep) {

			double globalDensity = ((double) densityStep / (double) (nrOfDensitySteps + 1));

			String filenamePrefix =
				"e:\\" +
				"TCA-HISTOGRAMS\\";

			String filenamePostfix =
//				"homogeneous-" +
//				"compactjam-" +
				"ca184" +
//				"stca" +
//				"stcacc" +
//				"vdrtca" +
//				"vdrcctca" +
//				"fi" +
//				"originaltakayasu2" +
//				"takayasu2" +
//				"toca" +
//				"tasep" +
//				"emmerichrank" +
				"-c" + String.valueOf(fNrOfCells) +
				"-s" + String.valueOf(nrOfDensitySteps) +
				"-d" + String.valueOf(StringTools.alignRight(String.valueOf(densityStep),3,'0')) +
				"-tp" + String.valueOf(transientPeriod) +
				"-sp" + String.valueOf(simulationPeriod) +
				"-vmax" + String.valueOf(fMaxSpeed) +
//				"-sp" + String.valueOf(slowdownProbability) +
//				"-s2sp" + String.valueOf(slowToStartProbability) +
//				"-atg" + String.valueOf(averageTimeGap) +
//				"-ap" + String.valueOf(accelerationProbability) +
//				"-dp" + String.valueOf(decelerationProbability) +
				".data";

			String filename1 =
				filenamePrefix +
				"histogram-speed-" +
				filenamePostfix;

			String filename2 =
				filenamePrefix +
				"histogram-spacegap-" +
				filenamePostfix;

			String filename3 =
				filenamePrefix +
				"histogram-timegap-" +
				filenamePostfix;

			double cellLength = 7.5;

			TextFileWriter dataFile1 = null;
			TextFileWriter dataFile2 = null;
			TextFileWriter dataFile3 = null;
			try {
				dataFile1 = new TextFileWriter(filename1);
				dataFile2 = new TextFileWriter(filename2);
				dataFile3 = new TextFileWriter(filename3);
			}
			catch (Exception exc) {
			}

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
//				new EmmerichRankTCA(initialState,fMaxSpeed,cellLength,14,fNrOfCells / 14,60,1,false,slowdownProbability);

			TrafficCellularAutomaton.kLoopDetectorsEnabled = false;

			// allow transient period
			for (int timeStep = 0; timeStep < transientPeriod; ++timeStep) {
				// advance cellular automaton one step
				fTCA.advanceOneStep();
			}
			fTCA.fState.calcSpaceGaps();

			for (int timeStep = 0; timeStep < simulationPeriod; ++timeStep) {
				// advance cellular automaton one step
				fTCA.advanceOneStep();

				// record all speeds, space and time gaps
				for (int cellNr = 0; cellNr < fNrOfCells; ++cellNr) {
					Cell cell = fTCA.fState.fCells[cellNr];

					if (cell != null) {

						try {

							// ==============
							// MEASURE SPEEDS
							// ==============
							dataFile1.writeInt(cell.fSpeed);
							dataFile1.writeLn();

							// ==================
							// MEASURE SPACE GAPS
							// ==================
							dataFile2.writeInt(cell.fSpaceGap);
							dataFile2.writeLn();

							// =================
							// MEASURE TIME GAPS
							// =================
							if (cell.fSpeed != 0) {
								double timeGap = (double) cell.fSpaceGap / ((double) cell.fSpeed / timeStepDuration);
								dataFile3.writeDouble(timeGap);
								dataFile3.writeLn();
							}
							else {
								dataFile3.writeString("Inf");
								dataFile3.writeLn();
							}
						}
						catch (FileWriteException exc) {
						}
					}
				}
			}

			dataFile1.finalize();
			dataFile2.finalize();
			dataFile3.finalize();

//			double percentCompleted = 100.0 - (((double) densityStep / (double) (nrOfDensitySteps + 1.0)) * 100.0);
			double percentCompleted = ((double) densityStep / (double) (nrOfDensitySteps + 1.0)) * 100.0;

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

				System.out.println(
					timeSpentString + " / " +
					timeLeftString + " [" + String.valueOf((int) Math.floor(percentCompleted)) + "%] " +
					"densityStep = " + densityStep);
			}
		} // densityStep
	}

	public static void main(String[] args) 
	{
		SimulatorHistograms simulator = new SimulatorHistograms();
	}
}
