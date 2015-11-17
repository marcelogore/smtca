// ----------------------------------------------------------
// Filename      : SimulatorFundamentalDiagramsMultiCell.java
// Author        : Sven Maerivoet
// Last modified : 22/06/2005
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ----------------------------------------------------------

// tca.experiments.fundamentaldiagrams.SimulatorFundamentalDiagramsMultiCell

package tca.experiments.fundamentaldiagrams;

import smtools.miscellaneous.*;
import tca.automata.*;
import tca.base.*;

public class SimulatorFundamentalDiagramsMultiCell
{
	public SimulatorFundamentalDiagramsMultiCell()
	{
		int nrOfCells        = 10000;
		int nrOfDensitySteps = 100;
		int stopDensityStep  = nrOfDensitySteps; 
		int transientPeriod  = 1000;
		int simulationPeriod = 50000;

		boolean distributeHomogeneously     = true;
		boolean saveGlobalDataIncrementally = true;
		boolean performLocalMeasurements    = false;
		boolean usePointDetector            = false;
		int measurementPeriod               = 60;
		
///*
		// =================
		// STCA (multi-cell)
		// =================
		int    vehicleLength       = 8;
		double cellLength          = 7.5 / vehicleLength;
		double timeStepDuration    = 1.0;
		int    maxSpeed            = 5 * vehicleLength;
		double slowdownProbability = 0.5;
//*/

/*
		// =========================
		// Helbing-Schreckenberg TCA
		// =========================
		double cellLength          = 2.5;
		double timeStepDuration    = 1.0;
		int    vehicleLength       = 2;
		int    maxSpeed            = 15;
		double slowdownProbability = 0.001;
		double lambda              = 1.0 / 1.3;
*/

/*
		// ===============
		// Brake Light TCA
		// ===============
		double cellLength       = 1.5;
		double timeStepDuration = 1.0;
		int    vehicleLength    = 5;
		int    maxSpeed         = 20;
		double pb               = 0.94;
		double p0               = 0.5;
		double pd               = 0.1;
		int    h                = 6;
		int    securityGap      = 7;
*/

/*
		// ======================
		// Kerner-Klenov-Wolf TCA
		// ======================
		double cellLength             = 0.5;
		double timeStepDuration       = 1.0;
		int    vehicleLength          = 15;
		int    maxSpeed               = 60;
		double slowdownProbability    = 0.04;
		double slowToStartProbability = 0.425;
		int    a                      = 1;
		int    b                      = 1;
		int    l                      = vehicleLength;
		int    D0                     = 60;
		double k                      = 2.55;
		double pa1                    = 0.2;
		double pa2                    = 0.052;
		double vp                     = 28;
*/

		int actualMeasurementPeriod      = (int) Math.round((double) measurementPeriod / timeStepDuration);
		int detectorLength               = maxSpeed;

		if (performLocalMeasurements) {
			simulationPeriod /= 10;
		}

		String filename =
//			"u:\\" +
			"fundamental-diagrams-";

		if (distributeHomogeneously) {
			filename += "homogeneous-";
		}
		else {
			filename += "compactjam-";
		}

		filename +=
			"mcstca" +
//			"hstca" +
//			"bltca" +
//			"kkwtca" +
			"-c" + String.valueOf(nrOfCells) +
			"-s" + String.valueOf(nrOfDensitySteps) +
			"-tp" + String.valueOf(transientPeriod) +
			"-sp" + String.valueOf(simulationPeriod) +
			"-vmax" + String.valueOf(maxSpeed) +
			"-cl" + String.valueOf(cellLength) +
			"-ts" + String.valueOf(timeStepDuration) +
			"-vl" + String.valueOf(vehicleLength) +

///*
			// =================
			// STCA (multi-cell)
			// =================
			"-sp" + String.valueOf(slowdownProbability) +
//*/

/*
			// =========================
			// Helbing-Schreckenberg TCA
			// =========================
			"-sp" + String.valueOf(slowdownProbability) +
			"-l" + String.valueOf(lambda) +
*/

/*
			// ===============
			// Brake Light TCA
			// ===============
			"-spb" + String.valueOf(pb) +
			"-sp0" + String.valueOf(p0) +
			"-spd" + String.valueOf(pd) +
			"-h" + String.valueOf(h) +
			"-secgap" + String.valueOf(securityGap) +
*/

/*
			// ======================
			// Kerner-Klenov-Wolf TCA
			// ======================
			"-sp" + String.valueOf(slowdownProbability) +
			"-s2sp" + String.valueOf(slowToStartProbability) +
			"-a" + String.valueOf(a) +
			"-b" + String.valueOf(b) +
			"-l" + String.valueOf(l) +
			"-d0" + String.valueOf(D0) +
			"-k" + String.valueOf(k) +
			"-pa1" + String.valueOf(pa1) +
			"-pa2" + String.valueOf(pa2) +
			"-vp" + String.valueOf(vp) +
*/

			"";

		if (performLocalMeasurements) {
			filename += ("-local-mp" + String.valueOf(measurementPeriod));

			if (usePointDetector) {
				filename += "-point-measurements";
			}
		}

		filename += ".data";

		TextFileWriter detectorFile = null;
		try {
			detectorFile = new TextFileWriter(filename);
		}
		catch (Exception exc) {
		}

		Chrono chrono = new Chrono();

		double[][] globalFundamentalDiagrams = new double[nrOfDensitySteps][3];

		System.out.println("Start...");

//		for (int densityStep = (nrOfDensitySteps - 1); densityStep > 0; --densityStep) {
		for (int densityStep = 1; densityStep <= stopDensityStep; ++densityStep) {

			double globalDensity = ((double) densityStep / (double) nrOfDensitySteps);
			int nrOfVehicles = (int) Math.round(globalDensity * ((double) nrOfCells / (double) vehicleLength));

	    // create the TCA's initial state
			MultiCellState initialState = new MultiCellState(nrOfCells);

			initialState.distributeVehicles(nrOfVehicles,maxSpeed,vehicleLength,distributeHomogeneously);

			// recalculate the global density
			nrOfVehicles = initialState.fNrOfVehicles;
			globalDensity = (double) nrOfVehicles / ((double) nrOfCells / (double) vehicleLength);

			MultiCellTrafficCellularAutomaton fTCA =
				new MultiCellStochasticTCA(initialState,maxSpeed,cellLength,slowdownProbability);
//				new HelbingSchreckenbergTCA(initialState,maxSpeed,cellLength,slowdownProbability,lambda);
//				new BrakeLightTCA(initialState,maxSpeed,cellLength,pb,p0,pd,h,securityGap);
//				new KernerKlenovWolfTCA(initialState,maxSpeed,cellLength,a,b,l,D0,k,pa1,pa2,vp,slowdownProbability,slowToStartProbability);

			// allow transient period
			for (int timeStep = 0; timeStep < transientPeriod; ++timeStep) {

				// advance cellular automaton one step
				fTCA.advanceOneStep();
			}
			fTCA.fState.calcSpaceGaps();

			double globalSpaceMeanSpeed = 0.0;
			int measurementTimeLeft = actualMeasurementPeriod - 1;
			double localDensity = 0.0;
			double localFlow = 0.0;
			double localSpaceMeanSpeed = 0.0;
			int pointDetectorNrOfVehiclesCounted = 0;

			final int kEmpty = -1;
			int[] loopDetectorStripVehicleIDsCurrentT = new int[detectorLength];
			int[] loopDetectorStripSpeedsCurrentT = new int[detectorLength];
			int[] loopDetectorStripVehicleIDsPreviousT = new int[detectorLength];
			int[] loopDetectorStripSpeedsPreviousT = new int[detectorLength];
			for (int i = 0; i < detectorLength; ++i) {
				loopDetectorStripVehicleIDsCurrentT[i] = kEmpty;
				loopDetectorStripSpeedsCurrentT[i] = kEmpty;
				loopDetectorStripVehicleIDsPreviousT[i] = kEmpty;
				loopDetectorStripSpeedsPreviousT[i] = kEmpty;
			}

			for (int timeStep = 0; timeStep < simulationPeriod; ++timeStep) {
				// advance cellular automaton one step
				fTCA.advanceOneStep();

				if (performLocalMeasurements) {

					if (usePointDetector) {

						// collect information of all vehicles located upstream of the loop detector
						// the detector is located right after the strip of cells at the beginning of the lattice
						for (int cellNr = 0; cellNr < detectorLength; ++cellNr) {
							Cell cell = fTCA.fState.fCells[cellNr];

							if (cell != null) {
								loopDetectorStripVehicleIDsCurrentT[cellNr] = cell.fVehicleID;
								loopDetectorStripSpeedsCurrentT[cellNr] = cell.fSpeed;
							}
							else {
								loopDetectorStripVehicleIDsCurrentT[cellNr] = kEmpty;
								loopDetectorStripSpeedsCurrentT[cellNr] = kEmpty;
							}
						}

						// cross-reference this information with the first vehicle downstream of the loop detector
						boolean vehicleFound = false;
						int cellNr = detectorLength;
						while ((!vehicleFound) && (cellNr < (detectorLength + maxSpeed))) {

							Cell cell = fTCA.fState.fCells[cellNr];
							if (cell != null) {

								vehicleFound = true;

								// cross-reference this first vehicle downstream of the loop detector
								// with the information from the strip at the previous time step
								boolean vehicleMatch = false;
								int i = 0;
								while ((!vehicleMatch) && (i < detectorLength)) {

									if (cell.fVehicleID == loopDetectorStripVehicleIDsPreviousT[i]) {

										vehicleMatch = true;

										// only record moving vehicles
										if (loopDetectorStripSpeedsPreviousT[i] != 0) {
											localSpaceMeanSpeed += (1.0 / (double) loopDetectorStripSpeedsPreviousT[i]);
											++pointDetectorNrOfVehiclesCounted;
										}
									}
									else {
										++i;
									}
								}
							}
							else {
								++cellNr;
							}
						}

						// update loop detector with the current information
						for (int i = 0; i < detectorLength; ++i) {
							loopDetectorStripVehicleIDsPreviousT[i] = loopDetectorStripVehicleIDsCurrentT[i];
							loopDetectorStripSpeedsPreviousT[i] = loopDetectorStripSpeedsCurrentT[i];
						}

						if (measurementTimeLeft == 0) {

							if (pointDetectorNrOfVehiclesCounted != 0) {

								localSpaceMeanSpeed /= (double) pointDetectorNrOfVehiclesCounted;
								if (localSpaceMeanSpeed != 0.0) {
									localSpaceMeanSpeed = 1.0 / localSpaceMeanSpeed;
								}
								localFlow = (double) pointDetectorNrOfVehiclesCounted / (double) actualMeasurementPeriod;

								if (localSpaceMeanSpeed != 0.0) {
									localDensity = (localFlow / localSpaceMeanSpeed) * vehicleLength;
								}
								else {
									localDensity = 0.0;
								}

								try {
									detectorFile.writeDouble(localDensity);
									detectorFile.writeString("\t");

									detectorFile.writeDouble(localFlow);
									detectorFile.writeString("\t");

									detectorFile.writeDouble(localSpaceMeanSpeed);
									detectorFile.writeLn();
								}
								catch (Exception exc) {
								}
							}

							pointDetectorNrOfVehiclesCounted = 0;
							localDensity = 0.0;
							localFlow = 0.0;
							localSpaceMeanSpeed = 0.0;
							measurementTimeLeft = actualMeasurementPeriod - 1;
						}
						else {
							--measurementTimeLeft;
						}
					} // use point detector
					else {

						// locally calculate the number of vehicles and the local space-mean speed
						// using a detector of finite length situated at cell #0
						int nrOfVehiclesCounted = 0;
						double localSpaceMeanSpeedSum = 0.0;

						// first investigate a possible vehicle at the end of the lattice
						// that extends into the detector's zone

 						for (int cellNr = (nrOfCells - vehicleLength + 1); cellNr < nrOfCells; ++cellNr) {
							Cell cell = fTCA.fState.fCells[cellNr];

							if (cell != null) {
								++nrOfVehiclesCounted;
								localSpaceMeanSpeedSum += cell.fSpeed;
							}
						}

						// now check the detector's zone itself
						for (int cellNr = 0; cellNr < detectorLength; ++cellNr) {
							Cell cell = fTCA.fState.fCells[cellNr];

							if (cell != null) {
								++nrOfVehiclesCounted;
								localSpaceMeanSpeedSum += cell.fSpeed;
							}
						}

						if (nrOfVehiclesCounted != 0) {
							localDensity += nrOfVehiclesCounted;
							localFlow += localSpaceMeanSpeedSum;
						}

						if (measurementTimeLeft == 0) {

							if (localDensity != 0.0) {

								localSpaceMeanSpeed = localFlow / localDensity;
								localDensity /= ((double) actualMeasurementPeriod * (double) (detectorLength + vehicleLength - 1));
								localDensity *= vehicleLength;
								localFlow /= ((double) actualMeasurementPeriod * (double) (detectorLength + vehicleLength - 1));
								try {
									detectorFile.writeDouble(localDensity);
									detectorFile.writeString("\t");

									detectorFile.writeDouble(localFlow);
									detectorFile.writeString("\t");

									detectorFile.writeDouble(localSpaceMeanSpeed);
									detectorFile.writeLn();
								}
								catch (Exception exc) {
								}
							}

							localDensity = 0.0;
							localFlow = 0.0;
							localSpaceMeanSpeed = 0.0;
							measurementTimeLeft = actualMeasurementPeriod - 1;
						}
						else {
							--measurementTimeLeft;
						}
					} // use a detector of finite length
				} // perform local measurements
				else {
					// global measurements
					// globalDensity was calculated above
					// calculate global space-mean speed
					for (int cellNr = 0; cellNr < fTCA.fState.fCells.length; ++cellNr) {
						Cell cell = fTCA.fState.fCells[cellNr];

						if (cell != null) {

							globalSpaceMeanSpeed += cell.fSpeed;
						}
					}
				} // perform global measurements
			}

			if (!performLocalMeasurements) {
				
				globalSpaceMeanSpeed /= (nrOfVehicles * simulationPeriod);
				double globalFlow = (globalDensity * globalSpaceMeanSpeed) / ((double) vehicleLength);

				globalFundamentalDiagrams[densityStep - 1][0] = globalDensity;
				globalFundamentalDiagrams[densityStep - 1][1] = globalFlow;
				globalFundamentalDiagrams[densityStep - 1][2] = globalSpaceMeanSpeed;

				if (saveGlobalDataIncrementally) {
					// store fundamental diagram data to file
					try {
						detectorFile.writeDouble(globalDensity);
						detectorFile.writeString("\t");

						detectorFile.writeDouble(globalFlow);
						detectorFile.writeString("\t");

						detectorFile.writeDouble(globalSpaceMeanSpeed);
						detectorFile.writeLn();
					}
					catch (Exception exc) {
					}
				}
			}

//			double percentCompleted = 100.0 - (((double) densityStep / (double) (nrOfDensitySteps - 2)) * 100.0);
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

				System.out.println(timeSpentString + " / " + timeLeftString + " [" + String.valueOf((int) Math.floor(percentCompleted)) + "%]");
			}
		}

		if ((!performLocalMeasurements) && (!saveGlobalDataIncrementally)) {
			// store fundamental diagram data to file
			try {
				for (int densityStep = 0; densityStep < nrOfDensitySteps; ++densityStep) {
					detectorFile.writeDouble(globalFundamentalDiagrams[densityStep][0]);
					detectorFile.writeString("\t");

					detectorFile.writeDouble(globalFundamentalDiagrams[densityStep][1]);
					detectorFile.writeString("\t");

					detectorFile.writeDouble(globalFundamentalDiagrams[densityStep][2]);
					detectorFile.writeLn();
				}
			}
			catch (Exception exc) {
			}
		}

		System.out.println("Done.");
	}

	public static void main(String[] args) 
	{
		SimulatorFundamentalDiagramsMultiCell simulator = new SimulatorFundamentalDiagramsMultiCell();
	}
}
