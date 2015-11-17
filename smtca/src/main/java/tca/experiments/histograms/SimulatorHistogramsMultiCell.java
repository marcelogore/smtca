// -------------------------------------------------
// Filename      : SimulatorHistogramsMultiCell.java
// Author        : Sven Maerivoet
// Last modified : 10/09/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// -------------------------------------------------

// tca.experiments.histograms.SimulatorHistogramsMultiCell

package tca.experiments.histograms;

import smtools.exceptions.*;
import smtools.miscellaneous.*;
import tca.automata.*;
import tca.base.*;

public class SimulatorHistogramsMultiCell
{
	public SimulatorHistogramsMultiCell()
	{
		int nrOfCells        = 300;
		int nrOfDensitySteps = 150;
		int transientPeriod  = 500;
		int simulationPeriod = 50000;

///*
		// =================
		// STCA (multi-cell)
		// =================
		double cellLength          = 7.5;
		double timeStepDuration    = 1.0;
		int    vehicleLength       = 1;
		nrOfCells                 *= vehicleLength;
		int    maxSpeed            = 5;
		double slowdownProbability = 0.0;
//*/

/*
		// =========================
		// Helbing-Schreckenberg TCA
		// =========================
		double cellLength          = 2.5;
		double timeStepDuration    = 1.0;
		int    vehicleLength       = 2;
		nrOfCells                 *= vehicleLength;
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
		nrOfCells              *= vehicleLength;
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
		nrOfCells                    *= vehicleLength;
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

		boolean distributeHomogeneously  = true;

		Chrono chrono = new Chrono();

//		for (int densityStep = nrOfDensitySteps; densityStep >= 1; --densityStep) {
		for (int densityStep = 1; densityStep <= nrOfDensitySteps; ++densityStep) {

			String filenamePrefix =
				"e:\\" +
				"TCA-HISTOGRAMS\\MCSTCA\\";
//				"TCA-HISTOGRAMS\\HS-TCA\\";
//				"TCA-HISTOGRAMS\\BL-TCA\\";
//				"TCA-HISTOGRAMS\\KKW-TCA\\";

			String filenamePostfix ="";

			if (distributeHomogeneously) {
				filenamePostfix += "homogeneous-";
			}
			else {
				filenamePostfix += "compactjam-";
			}

			filenamePostfix +=
				"mcstca" +
//				"hstca" +
//				"bltca" +
//				"kkwtca" +
				"-c" + String.valueOf(nrOfCells) +
				"-s" + String.valueOf(nrOfDensitySteps) +
				"-d" + String.valueOf(StringTools.alignRight(String.valueOf(densityStep),3,'0')) +
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

			filenamePostfix += ".data";

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

			for (int timeStep = 0; timeStep < simulationPeriod; ++timeStep) {
				// advance cellular automaton one step
				fTCA.advanceOneStep();

				// record all speeds, space and time gaps
				for (int cellNr = 0; cellNr < nrOfCells; ++cellNr) {
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
		SimulatorHistogramsMultiCell simulator = new SimulatorHistogramsMultiCell();
	}
}
