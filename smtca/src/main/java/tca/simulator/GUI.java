// ------------------------------
// Filename      : GUI.java
// Author        : Sven Maerivoet
// Last modified : 11/05/2005
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ------------------------------

package tca.simulator;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import smtools.miscellaneous.*;
import smtools.swing.util.*;
import tca.automata.*;
import tca.base.*;

public class GUI extends JFrame implements Runnable, ActionListener, WindowListener, AdjustmentListener
{
	// lattice capture switch
	public static boolean kSaveTCALatticeToDisk = false;

	// vehicle-speed color switch
	public static boolean kHighlightStoppedVehicles = false;

	// some action-commands
	private static final String kStart                        = "button-start";
	private static final String kStop                         = "button-stop";
	private static final String kReset                        = "button-reset";
	private static final String kQuit                         = "button-quit";
	private static final String kSelectTCA                    = "button-select-tca";
	private static final String kRed                          = "button-red";
	private static final String kGreen                        = "button-green";
	private static final String kClearPlots                   = "button-clear-plots";
	private static final String kConstructFundamentalDiagrams = "button-construct-fundamental-diagrams";
	private static final String kGlobalLoopDetectorValues     = "checkbox-global-loopdetector-values";
	private static final String kLogLoopDetectorValues        = "checkbox-log-loopdetector-values";
	private static final String kDistributeHomogeneously      = "checkbox-distribute-homogeneously";
	private static final String kStartWithCompactJam          = "checkbox-start-with-compact-jam";

	// the different TCA
	private static final String kCA184               = "1";
	private static final String kStochasticTCA       = "2";
	private static final String kStochasticCCTCA     = "3";
	private static final String kVDRTCA              = "4";
	private static final String kVDRCCTCA            = "5";
	private static final String kFukuiIshibashiTCA   = "6";
	private static final String kOriginalTakayasuTCA = "7";
	private static final String kTakayasuTCA         = "8";
	private static final String kTimeOrientedTCA     = "9";
	private static final String kTASEP               = "10";
	private static final String kEmmerichRank        = "11";
	private static final String kCochinosTCA         = "12";

	// component-sizing contants
	private int              fTXDiagramWidth;
	private int              fTXDiagramHeight;
	private boolean          fTXDiagramShrinking;
	private static final int kVehicleAnimationWidth      = 280;
	private static final int kVehicleAnimationHeight     = 300;
	private static final int kLoopDetectorPlotSize       = 11;
	private int              fLoopDetectorPlotsWidth;
	private int              fLoopDetectorPlotsHeight;
	private static final int kFundamentalDiagramWidth    = 190;
	private static final int kFundamentalDiagramHeight   = 190;
	private static final int kFundamentalDiagramPlotSize = 3;
	private static final int kScreenWidthExcess          = 10;
	private static final int kScreenHeightExcess         = 50;

	// client-windows
	private JPanel fTXDiagramPanel;
	private Image  fTXDiagramImage;
	private JPanel fVehicleAnimationPanel;
	private Image  fVehicleAnimationImage;
	private JPanel fLoopDetectorPlotsPanel;
	private Image  fLoopDetectorVPlotImage;
	private Image  fLoopDetectorQPlotImage;
	private Image  fLoopDetectorKPlotImage;
	private Image  fFundamentalDiagramKQImage;
	private Image  fFundamentalDiagramKVImage;
	private Image  fFundamentalDiagramQVImage;

	// controls
	private JLabel             fNrOfVehiclesLabel;
	private JLabel             fVehicleDensityLabel;
	private JLabel             fMaxSpeedLabel;
	private JLabel             fCurrentTimeLabel;
	private JButton            fStartButton;
	private JButton            fStopButton;
	private JButton            fResetButton;
	private JCheckBox          fTXDiagramCheckBox;
	private JCheckBox          fUseColorCheckBox;
	private JCheckBox          fHighlightStoppedVehiclesCheckBox;
	private JCheckBox          fVehicleAnimationCheckBox;
	private JCheckBox          fLoopDetectorPlotsCheckBox;
	private JCheckBox          fGlobalLoopDetectorValuesCheckBox;
	private JCheckBox          fColorCodeFundamentalDiagramsCheckBox;
	private JCheckBox          fLogLoopDetectorValuesCheckBox;
	private JCheckBox          fDistributeVehiclesHomogeneouslyCheckBox;
	private JCheckBox          fStartWithCompactJamCheckBox;
	private JLabel             fCycleHoldTimeScrollBarLabel;
	private JLabel             fGlobalDensityScrollBarLabel;
	private JLabel             fMaxSpeedScrollBarLabel;
	private JLabel             fLoopDetectorSampleTimeScrollBarLabel;
	private JLabel             fSlowdownProbabilityScrollBarLabel;
	private JLabel             fSlowdownProbabilityWhenStandingStillScrollBarLabel;
	private JLabel             fSlowdownProbabilityWithSmallGapsScrollBarLabel;
	private JLabel             fAverageTimeHeadwayScrollBarLabel;
	private JLabel             fAccelerationProbabilityScrollBarLabel;
	private JLabel             fDecelerationProbabilityScrollBarLabel;
	private JScrollBar         fCycleHoldTimeScrollBar;
	private JScrollBar         fGlobalDensityScrollBar;
	private JScrollBar         fMaxSpeedScrollBar;
	private JScrollBar         fLoopDetectorSampleTimeScrollBar;
	private JScrollBar         fSlowdownProbabilityScrollBar;
	private JScrollBar         fSlowdownProbabilityWhenStandingStillScrollBar;
	private JScrollBar         fSlowdownProbabilityWithSmallGapsScrollBar;
	private JScrollBar         fAverageTimeHeadwayScrollBar;
	private JScrollBar         fAccelerationProbabilityScrollBar;
	private JScrollBar         fDecelerationProbabilityScrollBar;
	private JCheckBox          fTrafficLightControlsEnabledCheckBox;
	private JNumberInputField  fTrafficLightRedCycleInputField;
	private JNumberInputField  fTrafficLightGreenCycleInputField;
	private JProgressBar       fConstructFundamentalDiagramsProgressBar;

	// predefined control-values
	private static final int kTXDiagramTimeHorizon                 = 580;
	private static final int kLoopDetectorPlotTimeHorizon          = 25;
	private static final int kMaxCycleHoldTime                     = 1000;
	private static final int kMaxLoopDetectorSamplingTime          = 600;
	private static final int kSlowdownProbabilityWhenStandingStill = 50;
	private static final int kSlowdownProbabilityWithSmallGaps     = 50;
	private static final int kAverageTimeHeadway                   = 115;
	private static final int kMaximalAverageTimeHeadway            = 500;
	private static final int kAccelerationProbability              = 90;
	private static final int kDecelerationProbability              = 90;
	private static final int kTrafficLightRedCycleTime             = 120;
	private static final int kTrafficLightGreenCycleTime           = 600;
	private static final int kMaxSpaceMeanSpeedForPlotting         = 150;
	private static final int kMaxFlowRateForPlotting               = 3050;
	private static final int kMaxDensityForPlotting                = 150;

	// internal datastructures
	private TrafficCellularAutomaton fTCA;
	private boolean                  fTCARunning;
	private int                      fCycleHoldTime;
	private int                      fTrafficLightTimer;
	private int                      fNrOfDensitySteps;
	private int                      fMultipleOfSampleTimeToSimulate;
	private TextFileWriter           fDetectorFile;
	private TextFileWriter           fLatticeFile;
	private boolean                  fConstructingFundamentalDiagrams;
	private int                      fTransientPeriod;

	public GUI(int nrOfCells, int nrOfVehicles, int maxSpeed, double cellLength, int nrOfLoopDetectors, int detectorRange, int nrOfDensitySteps, int multipleOfSampleTimeToSimulate, int transientPeriod)
	{
    // create the TCA's initial state
		State initialState = new State(nrOfCells);
		initialState.distributeVehicles(nrOfVehicles,maxSpeed,false,false);

		fTCA = new StochasticTCA(initialState,maxSpeed,cellLength,nrOfLoopDetectors,detectorRange,60,multipleOfSampleTimeToSimulate,false,0.1);

		fTCARunning = false;
		fCycleHoldTime = 0;
		fTrafficLightTimer = 0;

		fNrOfDensitySteps = nrOfDensitySteps;
		fMultipleOfSampleTimeToSimulate = multipleOfSampleTimeToSimulate;

		fTXDiagramWidth = kTXDiagramTimeHorizon;
		fTXDiagramHeight = fTCA.fState.fCells.length;

		fTXDiagramShrinking = false;
		if (fTXDiagramHeight > kVehicleAnimationHeight) {
			fTXDiagramHeight = kVehicleAnimationHeight;
			fTXDiagramShrinking = true;
		}

		fLoopDetectorPlotsWidth = kLoopDetectorPlotTimeHorizon * kLoopDetectorPlotSize;
		fLoopDetectorPlotsHeight = fTCA.fLoopDetectors.size() * kLoopDetectorPlotSize;

		fConstructingFundamentalDiagrams = false;

		fTransientPeriod = transientPeriod;

		if (kSaveTCALatticeToDisk) {
			try {
				fLatticeFile = new TextFileWriter("tca-lattice.data");
			}
			catch (Exception exc) {
			}
		}

		setIconImage(Toolkit.getDefaultToolkit().getImage("icon.jpg"));
		setTitle("Traffic Cellular Automata + (Sven Maerivoet - 2002-2004)");
		addWindowListener(this);

		JPanel contentPanel = new JPanel();
		JPanel panel = null;
		JPanel subPanel = null;
		TitledBorder titledBorder = null;
		JButton button = null;
		ButtonGroup buttonGroup = null;
		JRadioButton radioButton = null;

		contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));
		contentPanel.setBorder(new EmptyBorder(10,10,10,10));

			// create the animation-panel
			JPanel animationPanel = new JPanel();
			animationPanel.setLayout(new BoxLayout(animationPanel,BoxLayout.X_AXIS));

				// create the tx-diagram-panel
				fTXDiagramPanel = new JPanel();
				fTXDiagramPanel.setLayout(new BorderLayout());
				titledBorder = new TitledBorder(" t-x diagram ");
				titledBorder.setTitleColor(Color.blue);
				fTXDiagramPanel.setBorder(titledBorder);
				fTXDiagramPanel.setAlignmentY(Component.TOP_ALIGNMENT);
					TXDiagram txDiagram = new TXDiagram();
				fTXDiagramPanel.add(txDiagram,BorderLayout.CENTER);
			animationPanel.add(fTXDiagramPanel);
			animationPanel.add(Box.createRigidArea(new Dimension(10,0)));

				// create the vehicle-animation-panel
				fVehicleAnimationPanel = new JPanel();
			fVehicleAnimationPanel.setLayout(new BorderLayout());
				titledBorder = new TitledBorder(" Vehicle animation ");
				titledBorder.setTitleColor(Color.blue);
				fVehicleAnimationPanel.setBorder(titledBorder);
				fVehicleAnimationPanel.setAlignmentY(Component.TOP_ALIGNMENT);
					VehicleAnimation vehicleAnimation = new VehicleAnimation();
				fVehicleAnimationPanel.add(vehicleAnimation,BorderLayout.CENTER);
			animationPanel.add(fVehicleAnimationPanel);

				panel = new JPanel();
				panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
				panel.setAlignmentX(Component.LEFT_ALIGNMENT);
				panel.setAlignmentY(Component.TOP_ALIGNMENT);

					// create the statistics-panel
					JPanel statisticsPanel = new JPanel();
					statisticsPanel.setLayout(new BoxLayout(statisticsPanel,BoxLayout.Y_AXIS));
					titledBorder = new TitledBorder(" Simulation statistics ");
					titledBorder.setTitleColor(Color.blue);
					statisticsPanel.setBorder(titledBorder);
					statisticsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
						subPanel = new JPanel();
						subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.X_AXIS));
						subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
						subPanel.add(new JLabel("#cells : ",JLabel.LEFT));
						subPanel.add(new JLabel(String.valueOf(fTCA.fState.fCells.length),JLabel.LEFT));
					statisticsPanel.add(subPanel);
						subPanel = new JPanel();
						subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.X_AXIS));
						subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
						subPanel.add(new JLabel("Cell-length : ",JLabel.LEFT));
						subPanel.add(new JLabel(String.valueOf(fTCA.fCellLength) + " m",JLabel.LEFT));
					statisticsPanel.add(subPanel);
						subPanel = new JPanel();
						subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.X_AXIS));
						subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
						subPanel.add(new JLabel("Road-length : ",JLabel.LEFT));
						DecimalFormat decimalFormat = new DecimalFormat("0.00");
						String length = String.valueOf(decimalFormat.format(fTCA.fCellLength * fTCA.fState.fCells.length / 1000.0));
						subPanel.add(new JLabel(length + " km",JLabel.LEFT));
					statisticsPanel.add(subPanel);
						subPanel = new JPanel();
						subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.X_AXIS));
						subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
						subPanel.add(new JLabel("#vehicles : ",JLabel.LEFT));
							fNrOfVehiclesLabel = new JLabel("",JLabel.LEFT);
						subPanel.add(fNrOfVehiclesLabel);
					statisticsPanel.add(subPanel);
						subPanel = new JPanel();
						subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.X_AXIS));
						subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
						subPanel.add(new JLabel("Vehicle density : ",JLabel.LEFT));
							fVehicleDensityLabel = new JLabel("",JLabel.LEFT);
						subPanel.add(fVehicleDensityLabel);
					statisticsPanel.add(subPanel);
						subPanel = new JPanel();
						subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.X_AXIS));
						subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
						subPanel.add(new JLabel("Theoretical jam density : ",JLabel.LEFT));
						subPanel.add(new JLabel(String.valueOf((int) Math.round(1000.0 / fTCA.fCellLength)) + " veh/km",JLabel.LEFT));
					statisticsPanel.add(subPanel);
						subPanel = new JPanel();
						subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.X_AXIS));
						subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
						subPanel.add(new JLabel("Maximum vehicle speed : ",JLabel.LEFT));
							fMaxSpeedLabel = new JLabel("",JLabel.LEFT);
						subPanel.add(fMaxSpeedLabel);
					statisticsPanel.add(subPanel);
						subPanel = new JPanel();
						subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.X_AXIS));
						subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
						subPanel.add(new JLabel("Current time : ",JLabel.LEFT));
							fCurrentTimeLabel = new JLabel("",JLabel.LEFT);
						subPanel.add(fCurrentTimeLabel);
					statisticsPanel.add(subPanel);
				panel.add(statisticsPanel);

					// create the traffic-light-quick-controls-panel
					JPanel trafficLightQuickControls = new JPanel();
					trafficLightQuickControls.setLayout(new BoxLayout(trafficLightQuickControls,BoxLayout.Y_AXIS));
					titledBorder = new TitledBorder(" Traffic-light quick controls ");
					titledBorder.setTitleColor(Color.blue);
					trafficLightQuickControls.setBorder(titledBorder);
					trafficLightQuickControls.setAlignmentX(Component.LEFT_ALIGNMENT);
					trafficLightQuickControls.setAlignmentY(Component.TOP_ALIGNMENT);
						subPanel = new JPanel();
						subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.Y_AXIS));
						subPanel.setBorder(new EmptyBorder(10,10,10,10));
						subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
						subPanel.setAlignmentY(Component.TOP_ALIGNMENT);
							button = new JButton("Enter red-phase");
							button.setBackground(Color.red);
							button.setActionCommand(kRed);
							button.addActionListener(this);
						subPanel.add(button);
						subPanel.add(Box.createRigidArea(new Dimension(0,10)));
							button = new JButton("Enter green-phase");
							button.setBackground(Color.green);
							button.setActionCommand(kGreen);
							button.addActionListener(this);
						subPanel.add(button);
					trafficLightQuickControls.add(subPanel);
				panel.add(trafficLightQuickControls);

					// create the clear-plots-button
					JPanel clearPlotsPanel = new JPanel();
					clearPlotsPanel.setLayout(new BoxLayout(clearPlotsPanel,BoxLayout.Y_AXIS));
					clearPlotsPanel.setBorder(new EmptyBorder(10,5,10,10));
					clearPlotsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					clearPlotsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
						button = new JButton("Clear plots and diagrams");
						button.setActionCommand(kClearPlots);
						button.addActionListener(this);
					clearPlotsPanel.add(button);
				panel.add(clearPlotsPanel);

					// create the construct-fundamental-diagrams-button
					JPanel constructFundamentalDiagramsPanel = new JPanel();
					constructFundamentalDiagramsPanel.setLayout(new BoxLayout(constructFundamentalDiagramsPanel,BoxLayout.Y_AXIS));
					constructFundamentalDiagramsPanel.setBorder(new EmptyBorder(0,5,10,10));
					constructFundamentalDiagramsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					constructFundamentalDiagramsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
						button = new JButton("Construct fundamental diagrams");
						button.setForeground(Color.black);
						button.setBackground(Color.white);
						button.setActionCommand(kConstructFundamentalDiagrams);
						button.addActionListener(this);
					constructFundamentalDiagramsPanel.add(button);
				panel.add(constructFundamentalDiagramsPanel);

					// create the construct-fundamental-diagrams-progress-bar
					JPanel constructFundamentalDiagramsProgressPanel = new JPanel();
					constructFundamentalDiagramsProgressPanel.setLayout(new BoxLayout(constructFundamentalDiagramsProgressPanel,BoxLayout.Y_AXIS));
					constructFundamentalDiagramsProgressPanel.setBorder(new EmptyBorder(0,5,0,10));
					constructFundamentalDiagramsProgressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					constructFundamentalDiagramsProgressPanel.setAlignmentY(Component.TOP_ALIGNMENT);
						fConstructFundamentalDiagramsProgressBar = new JProgressBar(0,100);
						fConstructFundamentalDiagramsProgressBar.setMaximumSize(new Dimension(200,30));
						fConstructFundamentalDiagramsProgressBar.setValue(0);
						fConstructFundamentalDiagramsProgressBar.setString("");
						fConstructFundamentalDiagramsProgressBar.setStringPainted(true);
						fConstructFundamentalDiagramsProgressBar.setBorderPainted(false);
						constructFundamentalDiagramsProgressPanel.add(fConstructFundamentalDiagramsProgressBar);
				panel.add(constructFundamentalDiagramsProgressPanel);
			animationPanel.add(panel);

		contentPanel.add(animationPanel);

		contentPanel.add(Box.createRigidArea(new Dimension(0,10)));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));

				JPanel simulatorPanel = new JPanel();
				simulatorPanel.setLayout(new BoxLayout(simulatorPanel,BoxLayout.Y_AXIS));
				simulatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
				simulatorPanel.setAlignmentY(Component.TOP_ALIGNMENT);

					// create the simulator-controls-panel
					JPanel simulatorControlsPanel = new JPanel();
					simulatorControlsPanel.setLayout(new BoxLayout(simulatorControlsPanel,BoxLayout.Y_AXIS));
					simulatorControlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					simulatorControlsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
					titledBorder = new TitledBorder(" Simulator controls ");
					titledBorder.setTitleColor(Color.blue);
					simulatorControlsPanel.setBorder(titledBorder);

						// create general simulator-controls
						panel = new JPanel();
						panel.setPreferredSize(new Dimension(400,50));
						panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
						panel.setAlignmentX(Component.LEFT_ALIGNMENT);
						panel.setBorder(new EmptyBorder(10,10,10,10));
							fStartButton = new JButton("Start");
							fStartButton.setBackground(Color.green);
							fStartButton.setActionCommand(kStart);
							fStartButton.addActionListener(this);
						panel.add(fStartButton);
						panel.add(Box.createRigidArea(new Dimension(10,0)));
							fStopButton = new JButton("Stop");
							fStopButton.setActionCommand(kStop);
							fStopButton.addActionListener(this);
						panel.add(fStopButton);
						panel.add(Box.createRigidArea(new Dimension(10,0)));
							fResetButton = new JButton("Reset");
							fResetButton.setActionCommand(kReset);
							fResetButton.addActionListener(this);
						panel.add(fResetButton);
						panel.add(Box.createRigidArea(new Dimension(10,0)));
							button = new JButton("Quit");
							button.setBackground(Color.yellow);
							button.setActionCommand(kQuit);
							button.addActionListener(this);
						panel.add(button);
						panel.add(Box.createHorizontalGlue());
					simulatorControlsPanel.add(panel);

						panel = new JPanel();
						panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
						panel.setAlignmentX(Component.LEFT_ALIGNMENT);
						panel.setAlignmentY(Component.TOP_ALIGNMENT);

							// create animation-settings-controls
							subPanel = new JPanel();
							subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.Y_AXIS));
							subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
							subPanel.setAlignmentY(Component.TOP_ALIGNMENT);
							subPanel.setBorder(new EmptyBorder(0,10,10,10));
								fTXDiagramCheckBox = new JCheckBox("Enable t-x diagram",true);
								fTXDiagramCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
							subPanel.add(fTXDiagramCheckBox);
								fUseColorCheckBox = new JCheckBox("Use color when plotting",true);
								fUseColorCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
							subPanel.add(fUseColorCheckBox);
								fHighlightStoppedVehiclesCheckBox = new JCheckBox("Highlight stopped vehicles",kHighlightStoppedVehicles);
								fHighlightStoppedVehiclesCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
							subPanel.add(fHighlightStoppedVehiclesCheckBox);
								fVehicleAnimationCheckBox = new JCheckBox("Enable vehicle-animation",true);
								fVehicleAnimationCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
							subPanel.add(fVehicleAnimationCheckBox);
								fLoopDetectorPlotsCheckBox = new JCheckBox("Enable loop detector plots",true);
								fLoopDetectorPlotsCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
							subPanel.add(fLoopDetectorPlotsCheckBox);
								fGlobalLoopDetectorValuesCheckBox = new JCheckBox("Use global macroscopic measurements",false);
								fGlobalLoopDetectorValuesCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
								fGlobalLoopDetectorValuesCheckBox.setActionCommand(kGlobalLoopDetectorValues);
								fGlobalLoopDetectorValuesCheckBox.addActionListener(this);
							subPanel.add(fGlobalLoopDetectorValuesCheckBox);
								fColorCodeFundamentalDiagramsCheckBox = new JCheckBox("Color-code fundamental diagrams",true);
								fColorCodeFundamentalDiagramsCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
							subPanel.add(fColorCodeFundamentalDiagramsCheckBox);
							subPanel.add(new JEtchedLine());
								fLogLoopDetectorValuesCheckBox = new JCheckBox("Log loop detector values",false);
								fLogLoopDetectorValuesCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
								fLogLoopDetectorValuesCheckBox.setActionCommand(kLogLoopDetectorValues);
								fLogLoopDetectorValuesCheckBox.addActionListener(this);
							subPanel.add(fLogLoopDetectorValuesCheckBox);
								fDistributeVehiclesHomogeneouslyCheckBox = new JCheckBox("Distribute vehicles homogeneously",false);
								fDistributeVehiclesHomogeneouslyCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
								fDistributeVehiclesHomogeneouslyCheckBox.setActionCommand(kDistributeHomogeneously);
								fDistributeVehiclesHomogeneouslyCheckBox.addActionListener(this);
							subPanel.add(fDistributeVehiclesHomogeneouslyCheckBox);
								fStartWithCompactJamCheckBox = new JCheckBox("Start with compact jam",false);
								fStartWithCompactJamCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
								fStartWithCompactJamCheckBox.setActionCommand(kStartWithCompactJam);
								fStartWithCompactJamCheckBox.addActionListener(this);
							subPanel.add(fStartWithCompactJamCheckBox);
						panel.add(subPanel);

							// create TCA-type-controls
							subPanel = new JPanel();
							subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.Y_AXIS));
							subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
							subPanel.setAlignmentY(Component.TOP_ALIGNMENT);
							subPanel.setBorder(new EmptyBorder(0,10,10,10));
								buttonGroup = new ButtonGroup();
									radioButton = new JRadioButton("CA-184 (Wolfram)");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kCA184);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("STCA (NaSch)");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kStochasticTCA);
									radioButton.setSelected(true);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("STCA-CC (NaSch)");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kStochasticCCTCA);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("VDR-TCA (NaSch + VDR)");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kVDRTCA);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("VDR-CC-TCA (NaSch + VDR)");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kVDRCCTCA);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("Fukui-Ishibashi TCA");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kFukuiIshibashiTCA);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("Takayasu-Takayasu TCA (original)");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kOriginalTakayasuTCA);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("Takayasu-Takayasu TCA (modified)");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kTakayasuTCA);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("Time-Oriented CA (TOCA)");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kTimeOrientedTCA);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("TASEP TCA");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kTASEP);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("Emmerich-Rank TCA");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kEmmerichRank);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("Cochinos' TCA");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kCochinosTCA);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
						panel.add(subPanel);

					simulatorControlsPanel.add(panel);
				simulatorPanel.add(simulatorControlsPanel);

					// create simulation's slider-controls
					JPanel simulationSettingsPanel = new JPanel();
					simulationSettingsPanel.setLayout(new BoxLayout(simulationSettingsPanel,BoxLayout.X_AXIS));
					simulationSettingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					titledBorder = new TitledBorder(" Simulation settings ");
					titledBorder.setTitleColor(Color.blue);
					simulationSettingsPanel.setBorder(titledBorder);
						panel = new JPanel();
						panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
						panel.setAlignmentX(Component.LEFT_ALIGNMENT);
						panel.setBorder(new EmptyBorder(0,10,10,10));
							fCycleHoldTimeScrollBarLabel = new JLabel("Cycle hold time [" + String.valueOf(fCycleHoldTime) + " ms] :",JLabel.LEFT);
							fGlobalDensityScrollBarLabel = new JLabel("",JLabel.LEFT);
							setGlobalDensityLabel();
							fMaxSpeedScrollBarLabel = new JLabel("Maximum speed [" + String.valueOf(fTCA.fMaxSpeed) + " cells/s] :",JLabel.LEFT);
							fLoopDetectorSampleTimeScrollBarLabel = new JLabel("Detector sampling time [" + String.valueOf(fTCA.fLoopDetectorMeasurementInterval) + " s] :",JLabel.LEFT);
							if (fTCA instanceof StochasticTCA) {
								fSlowdownProbabilityScrollBarLabel = new JLabel(
									"Slowdown probability [" +
									(int) Math.round(((StochasticTCA) fTCA).fSlowdownProbability * 100.0) + " %] :",JLabel.LEFT);
							}
							else {
								fSlowdownProbabilityScrollBarLabel = new JLabel("Slowdown probability [0 %] :",JLabel.LEFT);
								fSlowdownProbabilityScrollBarLabel.setEnabled(false);
							}
							if (fTCA instanceof VDRTCA) {
								fSlowdownProbabilityWhenStandingStillScrollBarLabel = new JLabel(
									"VDR-factor [" +
									(int) Math.round(((VDRTCA) fTCA).fSlowdownProbabilityWhenStandingStill * 100.0) + " %] :",JLabel.LEFT);
							}
							else {
								fSlowdownProbabilityWhenStandingStillScrollBarLabel =
									new JLabel(
										"VDR-factor [" +
										String.valueOf(kSlowdownProbabilityWhenStandingStill) +
										" %] :",JLabel.LEFT);
								fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(false);
							}
							if (fTCA instanceof TakayasuTCA) {
								fSlowdownProbabilityWithSmallGapsScrollBarLabel = new JLabel(
									"Takayasu-factor [" +
									(int) Math.round(((TakayasuTCA) fTCA).fSlowdownProbabilityWithSmallGaps * 100.0) + " %] :",JLabel.LEFT);
							}
							else {
								fSlowdownProbabilityWithSmallGapsScrollBarLabel =
									new JLabel(
										"Takayasu-factor [" +
										String.valueOf(kSlowdownProbabilityWithSmallGaps) +
										" %] :",JLabel.LEFT);
								fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
							}
							if (fTCA instanceof TimeOrientedTCA) {
								fAverageTimeHeadwayScrollBarLabel = new JLabel(
									"Average time-headway [" +
									String.valueOf(((TimeOrientedTCA) fTCA).fAverageTimeHeadway) + " s] :",JLabel.LEFT);
								fAccelerationProbabilityScrollBarLabel = new JLabel(
									"Acceleration probability [" +
									(int) Math.round(((TimeOrientedTCA) fTCA).fAccelerationProbability * 100.0) + " %] :",JLabel.LEFT);
								fDecelerationProbabilityScrollBarLabel = new JLabel(
									"Acceleration probability [" +
									(int) Math.round(((TimeOrientedTCA) fTCA).fAccelerationProbability * 100.0) + " %] :",JLabel.LEFT);
							}
							else {
								fAverageTimeHeadwayScrollBarLabel =
									new JLabel(
										"Average time-headway [" +
										String.valueOf(kAverageTimeHeadway / 100.0) +
										" s] :",JLabel.LEFT);
								fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
								fAccelerationProbabilityScrollBarLabel =
									new JLabel(
										"Acceleration probability [" +
										String.valueOf(kAccelerationProbability) +
										" %] :",JLabel.LEFT);
								fAccelerationProbabilityScrollBarLabel.setEnabled(false);
								fDecelerationProbabilityScrollBarLabel =
									new JLabel(
										"Deceleration probability [" +
										String.valueOf(kDecelerationProbability) +
										" %] :",JLabel.LEFT);
								fDecelerationProbabilityScrollBarLabel.setEnabled(false);
							}
						panel.add(fCycleHoldTimeScrollBarLabel);
						panel.add(fGlobalDensityScrollBarLabel);
						panel.add(fMaxSpeedScrollBarLabel);
						panel.add(fLoopDetectorSampleTimeScrollBarLabel);
						panel.add(fSlowdownProbabilityScrollBarLabel);
						panel.add(fSlowdownProbabilityWhenStandingStillScrollBarLabel);
						panel.add(fSlowdownProbabilityWithSmallGapsScrollBarLabel);
						panel.add(fAverageTimeHeadwayScrollBarLabel);
						panel.add(fAccelerationProbabilityScrollBarLabel);
						panel.add(fDecelerationProbabilityScrollBarLabel);
					simulationSettingsPanel.add(panel);
						panel = new JPanel();
						panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
						panel.setAlignmentX(Component.LEFT_ALIGNMENT);
						panel.setBorder(new EmptyBorder(0,10,10,10));
							fCycleHoldTimeScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,fCycleHoldTime,10,0,kMaxCycleHoldTime + 10);
							fCycleHoldTimeScrollBar.setBorder(new LineBorder(Color.gray));
							fCycleHoldTimeScrollBar.addAdjustmentListener(this);
						panel.add(fCycleHoldTimeScrollBar);
							fGlobalDensityScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,fTCA.fState.fNrOfVehicles,10,0,fTCA.fState.fCells.length + 10);
							fGlobalDensityScrollBar.setBorder(new LineBorder(Color.gray));
							fGlobalDensityScrollBar.addAdjustmentListener(this);
						panel.add(fGlobalDensityScrollBar);
							fMaxSpeedScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,fTCA.fMaxSpeed,10,0,fTCA.fMaxSpeed + 10);
							fMaxSpeedScrollBar.setBorder(new LineBorder(Color.gray));
							fMaxSpeedScrollBar.addAdjustmentListener(this);
						panel.add(fMaxSpeedScrollBar);
							fLoopDetectorSampleTimeScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,fTCA.fLoopDetectorMeasurementInterval,10,1,kMaxLoopDetectorSamplingTime + 10);
							fLoopDetectorSampleTimeScrollBar.setBorder(new LineBorder(Color.gray));
							fLoopDetectorSampleTimeScrollBar.addAdjustmentListener(this);
						panel.add(fLoopDetectorSampleTimeScrollBar);
							if (fTCA instanceof StochasticTCA) {
								fSlowdownProbabilityScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,
									(int) Math.round(((StochasticTCA) fTCA).fSlowdownProbability * 100.0),10,0,100 + 10);
							}
							else {
								fSlowdownProbabilityScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,0,10,0,100 + 10);
								fSlowdownProbabilityScrollBar.setEnabled(false);
							}
							fSlowdownProbabilityScrollBar.setBorder(new LineBorder(Color.gray));
							fSlowdownProbabilityScrollBar.addAdjustmentListener(this);
						panel.add(fSlowdownProbabilityScrollBar);
							if (fTCA instanceof VDRTCA) {
								fSlowdownProbabilityWhenStandingStillScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,
									(int) Math.round(((VDRTCA) fTCA).fSlowdownProbabilityWhenStandingStill * 100.0),10,0,100 + 10);
							}
							else {
								fSlowdownProbabilityWhenStandingStillScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,kSlowdownProbabilityWhenStandingStill,10,0,100 + 10);
								fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(false);
							}
							fSlowdownProbabilityWhenStandingStillScrollBar.setBorder(new LineBorder(Color.gray));
							fSlowdownProbabilityWhenStandingStillScrollBar.addAdjustmentListener(this);
						panel.add(fSlowdownProbabilityWhenStandingStillScrollBar);
							if (fTCA instanceof TakayasuTCA) {
								fSlowdownProbabilityWithSmallGapsScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,
									(int) Math.round(((TakayasuTCA) fTCA).fSlowdownProbabilityWithSmallGaps * 100.0),10,0,100 + 10);
							}
							else {
								fSlowdownProbabilityWithSmallGapsScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,kSlowdownProbabilityWithSmallGaps,10,0,100 + 10);
								fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
							}
							fSlowdownProbabilityWithSmallGapsScrollBar.setBorder(new LineBorder(Color.gray));
							fSlowdownProbabilityWithSmallGapsScrollBar.addAdjustmentListener(this);
						panel.add(fSlowdownProbabilityWithSmallGapsScrollBar);
							if (fTCA instanceof TimeOrientedTCA) {
								fAverageTimeHeadwayScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,
									(int) Math.round(((TimeOrientedTCA) fTCA).fAverageTimeHeadway * 100.0),10,0,500 + 10);
								fAccelerationProbabilityScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,
									(int) Math.round(((TimeOrientedTCA) fTCA).fAccelerationProbability * 100.0),10,0,100 + 10);
								fDecelerationProbabilityScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,
									(int) Math.round(((TimeOrientedTCA) fTCA).fDecelerationProbability * 100.0),10,0,100 + 10);
							}
							else {
								fAverageTimeHeadwayScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,kAverageTimeHeadway,10,0,kMaximalAverageTimeHeadway + 10);
								fAverageTimeHeadwayScrollBar.setEnabled(false);
								fAccelerationProbabilityScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,kAccelerationProbability,10,0,100 + 10);
								fAccelerationProbabilityScrollBar.setEnabled(false);
								fDecelerationProbabilityScrollBar = new JScrollBar(JScrollBar.HORIZONTAL,kDecelerationProbability,10,0,100 + 10);
								fDecelerationProbabilityScrollBar.setEnabled(false);
							}
							fAverageTimeHeadwayScrollBar.setBorder(new LineBorder(Color.gray));
							fAverageTimeHeadwayScrollBar.addAdjustmentListener(this);
							fAccelerationProbabilityScrollBar.setBorder(new LineBorder(Color.gray));
							fAccelerationProbabilityScrollBar.addAdjustmentListener(this);
							fDecelerationProbabilityScrollBar.setBorder(new LineBorder(Color.gray));
							fDecelerationProbabilityScrollBar.addAdjustmentListener(this);
						panel.add(fAverageTimeHeadwayScrollBar);
						panel.add(fAccelerationProbabilityScrollBar);
						panel.add(fDecelerationProbabilityScrollBar);
					simulationSettingsPanel.add(panel);
				simulatorPanel.add(simulationSettingsPanel);

					// create the traffic-light-controls-panel
					JPanel trafficLightControlsPanel = new JPanel();
					trafficLightControlsPanel.setLayout(new BoxLayout(trafficLightControlsPanel,BoxLayout.Y_AXIS));
					trafficLightControlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					trafficLightControlsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
					titledBorder = new TitledBorder(" Traffic-light controls ");
					titledBorder.setTitleColor(Color.blue);
					trafficLightControlsPanel.setBorder(titledBorder);

						// create animation-settings-controls
						panel = new JPanel();
						panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
						panel.setAlignmentX(Component.LEFT_ALIGNMENT);
						panel.setBorder(new EmptyBorder(0,10,10,10));
							fTrafficLightControlsEnabledCheckBox = new JCheckBox("Enable automatic traffic-light controls",false);
							fTrafficLightControlsEnabledCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
						panel.add(fTrafficLightControlsEnabledCheckBox);
							subPanel = new JPanel();
							subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.X_AXIS));
							subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
							subPanel.add(new JLabel("Red cycle-time : ",JLabel.LEFT));
								fTrafficLightRedCycleInputField = new JNumberInputField(kTrafficLightRedCycleTime,true);
								fTrafficLightRedCycleInputField.setMaximumSize(new Dimension(100,20));
							subPanel.add(fTrafficLightRedCycleInputField);
							subPanel.add(new JLabel(" cycles",JLabel.LEFT));
						panel.add(subPanel);
							subPanel = new JPanel();
							subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.X_AXIS));
							subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
							subPanel.add(new JLabel("Green cycle-time : ",JLabel.LEFT));
								fTrafficLightGreenCycleInputField = new JNumberInputField(kTrafficLightGreenCycleTime,true);
								fTrafficLightGreenCycleInputField.setMaximumSize(new Dimension(90,20));
							subPanel.add(fTrafficLightGreenCycleInputField);
							subPanel.add(new JLabel(" cycles",JLabel.LEFT));
						panel.add(subPanel);
					trafficLightControlsPanel.add(panel);
				simulatorPanel.add(trafficLightControlsPanel);

			lowerPanel.add(simulatorPanel);
			lowerPanel.add(Box.createRigidArea(new Dimension(10,0)));

				// create the loop-detector-plots-panel
				fLoopDetectorPlotsPanel = new JPanel();
				fLoopDetectorPlotsPanel.setLayout(new BoxLayout(fLoopDetectorPlotsPanel,BoxLayout.X_AXIS));
				titledBorder = new TitledBorder(" Loop detector plots ");
				titledBorder.setTitleColor(Color.blue);
				fLoopDetectorPlotsPanel.setBorder(titledBorder);
				fLoopDetectorPlotsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
				fLoopDetectorPlotsPanel.setAlignmentY(Component.TOP_ALIGNMENT);

					LoopDetectorPlots loopDetectorPlots = new LoopDetectorPlots();
				fLoopDetectorPlotsPanel.add(loopDetectorPlots);

			lowerPanel.add(fLoopDetectorPlotsPanel);
			lowerPanel.add(Box.createRigidArea(new Dimension(10,0)));

		contentPanel.add(lowerPanel);

			JPanel scrollPaneWrapper = new JPanel();
			scrollPaneWrapper.setLayout(new BorderLayout());
				JScrollPane scrollPane = new JScrollPane(contentPanel);
			scrollPaneWrapper.add(scrollPane,BorderLayout.CENTER);

		setContentPane(scrollPaneWrapper);

		updateStatistics();

		// set the system's look-and-feel
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			SwingUtilities.updateComponentTreeUI(this);
		}
		catch (Exception exc) {
		}

		pack();

		// resize window if necessary
		int applicationWidth = getWidth();
		int applicationHeight = getHeight();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) Math.round(screenSize.getWidth()) - kScreenWidthExcess;
		int screenHeight = (int) Math.round(screenSize.getHeight()) - kScreenHeightExcess;

		int newWidth = applicationWidth;
		int newHeight = applicationHeight;
		if (newWidth > screenWidth) {
			newWidth = screenWidth;
		}
		if (newHeight > screenHeight) {
			newHeight = screenHeight;
		}
		scrollPaneWrapper.setPreferredSize(new Dimension(newWidth,newHeight));
		pack();

		setVisible(true);
	}

	private void updateTXDiagram()
	{
		if (!fTXDiagramCheckBox.isSelected()) {
			return;
		}

		int currentTime = fTCA.fState.fTime;
		if (currentTime >= (kTXDiagramTimeHorizon - 1)) {
			currentTime = kTXDiagramTimeHorizon - 1;
		}

		fTXDiagramWidth = kTXDiagramTimeHorizon;
		fTXDiagramHeight = fTCA.fState.fCells.length;

		// update tx-plot
		if (fTXDiagramImage == null) {
			fTXDiagramImage = createImage(fTXDiagramWidth,fTXDiagramHeight);

			// fill image with the panel's background-color
			Graphics g = fTXDiagramImage.getGraphics();
			g.setColor(fTXDiagramPanel.getBackground());
			g.fillRect(0,0,fTXDiagramWidth,fTXDiagramHeight);
		}

		Graphics g = fTXDiagramImage.getGraphics();

		for (int cellNr = 0; cellNr < fTCA.fState.fCells.length; ++cellNr) {

			Cell cell = fTCA.fState.fCells[cellNr];

			// save space (if necessary) by skipping all odd cells
			if ((fTXDiagramShrinking) && ((cellNr % 2) == 1)) {
				continue;
			}

			if (cell != null) {
				if (fUseColorCheckBox.isSelected()) {
					float factor = ((float) cell.fVehicleID) / fTCA.fState.fNrOfVehicles;
					if (factor < 0.0f) {
						factor = 0.0f;
					}
					else if (factor > 1.0f) {
						factor = 1.0f;
					}
					g.setColor(new Color(factor,factor,0.0f));
				}
				else {
					g.setColor(Color.blue);
				}

				if ((kHighlightStoppedVehicles || fHighlightStoppedVehiclesCheckBox.isSelected()) && (cell.fSpeed == 0)) {
					g.setColor(new Color(1.0f,0.0f,0.0f));
				}
			}
			else {
				g.setColor(fTXDiagramPanel.getBackground());
			}

			g.fillRect(currentTime,fTXDiagramHeight - 1 - cellNr,1,1);
		}

		// scroll past the time horizon
		if (currentTime == (kTXDiagramTimeHorizon - 1)) {
			g.copyArea(1,0,fTXDiagramWidth,fTXDiagramHeight,-1,0);
		}

		fTXDiagramPanel.repaint();
	}

	private void updateVehicleAnimation()
	{
		if (!fVehicleAnimationCheckBox.isSelected()) {
			return;
		}

		// update tx-plot
		if (fVehicleAnimationImage == null) {
			fVehicleAnimationImage = createImage(kVehicleAnimationWidth - 2,kVehicleAnimationHeight - 2);

			// fill image with a uniform dark-green color
			Graphics g = fVehicleAnimationImage.getGraphics();
			g.setColor(new Color(0.0f,0.6f,0.0f));
			g.fillRect(0,0,kVehicleAnimationWidth,kVehicleAnimationHeight);
		}

		Graphics g = fVehicleAnimationImage.getGraphics();

		int outerRadius = ((5 * kVehicleAnimationWidth) / 6) / 2;
		int roadWidth = outerRadius / 5;

		int origX = kVehicleAnimationWidth / 2;
		int origY = kVehicleAnimationHeight / 2;

		// draw an arrow indicating the direction-of-travel
		g.setColor(Color.white);
		int arrowX1 = origX - 30;
		int arrowX2 = origX + 30;
		int arrowY = origY - outerRadius - 15;
		g.drawLine(arrowX1,arrowY,arrowX2,arrowY);
		g.drawLine(arrowX2,arrowY,arrowX2 - 15,arrowY - 5);
		g.drawLine(arrowX2,arrowY,arrowX2 - 15,arrowY + 5);

		// draw circular road
		g.setColor(Color.gray);
		g.fillOval(origX - outerRadius,origY - outerRadius,outerRadius * 2,outerRadius * 2);
		g.setColor(Color.black);
		g.drawOval(origX - outerRadius,origY - outerRadius,outerRadius * 2,outerRadius * 2);

		g.setColor(new Color(0.0f,0.6f,0.0f));
		g.fillOval(
			origX - (outerRadius - roadWidth),
			origY - (outerRadius - roadWidth),
			(outerRadius - roadWidth) * 2,
			(outerRadius - roadWidth) * 2);
		g.setColor(Color.black);
		g.drawOval(
			origX - (outerRadius - roadWidth),
			origY - (outerRadius - roadWidth),
			(outerRadius - roadWidth) * 2,
			(outerRadius - roadWidth) * 2);

		for (int cellNr = 0; cellNr < fTCA.fState.fCells.length; ++cellNr) {

			Cell cell = fTCA.fState.fCells[cellNr];

			if (cell != null) {

				double angle = ((2.0 * Math.PI) / (double) fTCA.fState.fCells.length) * cellNr;

				double cosAngle = Math.cos(angle);
				double sinAngle = Math.sin(angle);

				double angleDelta = ((2.0 * Math.PI) / (double) fTCA.fState.fCells.length) * ((cellNr + 1) % fTCA.fState.fCells.length);

				double cosAngleDelta = Math.cos(angleDelta);
				double sinAngleDelta = Math.sin(angleDelta);

				int vehicleWidth = roadWidth / 2;

				double radius1 = outerRadius - ((roadWidth - vehicleWidth) / 2);
				double radius2 = (outerRadius - roadWidth) + ((roadWidth - vehicleWidth) / 2);

				int x1 = origX + (int) Math.round((radius1 * cosAngle));
				int y1 = origY + (int) Math.round((radius1 * sinAngle));
				int x2 = origX + (int) Math.round((radius2 * cosAngle));
				int y2 = origY + (int) Math.round((radius2 * sinAngle));

				int x3 = origX + (int) Math.round((radius1 * cosAngleDelta));
				int y3 = origY + (int) Math.round((radius1 * sinAngleDelta));
				int x4 = origX + (int) Math.round((radius2 * cosAngleDelta));
				int y4 = origY + (int) Math.round((radius2 * sinAngleDelta));

				Polygon poly = new Polygon();
				poly.addPoint(x1,y1);
				poly.addPoint(x2,y2);
				poly.addPoint(x4,y4);
				poly.addPoint(x3,y3);

				if (fUseColorCheckBox.isSelected()) {
					float factor = ((float) cell.fVehicleID) / fTCA.fState.fNrOfVehicles;
					if (factor < 0.0f) {
						factor = 0.0f;
					}
					else if (factor > 1.0f) {
						factor = 1.0f;
					}
					g.setColor(new Color(factor,factor,0.0f));
				}
				else {
					g.setColor(Color.blue);
				}
				g.fillPolygon(poly);

				g.setColor(Color.white);
				g.drawPolygon(poly);
			}

			++cellNr;
		}

		// draw the location of the traffic light
		if (fTCA.fTrafficLightIsRed) {
			g.setColor(Color.red);
		}
		else {
			g.setColor(Color.green);
		}
		g.fillRect(origX + outerRadius - roadWidth - 15,origY - 5,10,10);
		g.setColor(Color.white);
		g.drawRect(origX + outerRadius - roadWidth - 15,origY - 5,10,10);

		// draw the locations of the loop detectors
		Enumeration loopDetectorEnumeration = fTCA.fLoopDetectors.elements();
		while (loopDetectorEnumeration.hasMoreElements()) {
			LoopDetector loopDetector = (LoopDetector) loopDetectorEnumeration.nextElement();

			double delta = Math.PI / 64.0;
			double angle = ((2.0 * Math.PI) / (double) fTCA.fState.fCells.length) * (loopDetector.fCellNr - 1);

			double cosAngle = Math.cos(angle);
			double sinAngle = Math.sin(angle);

			double angleDelta = angle + (2.0 * delta);

			double cosAngleDelta = Math.cos(angleDelta);
			double sinAngleDelta = Math.sin(angleDelta);

			int x1 = origX + (int) Math.round(((outerRadius + 5) * cosAngle));
			int y1 = origY + (int) Math.round(((outerRadius + 5) * sinAngle));
			int x2 = origX + (int) Math.round(((outerRadius + 10) * cosAngle));
			int y2 = origY + (int) Math.round(((outerRadius + 10) * sinAngle));

			int x3 = origX + (int) Math.round(((outerRadius + 5) * cosAngleDelta));
			int y3 = origY + (int) Math.round(((outerRadius + 5) * sinAngleDelta));
			int x4 = origX + (int) Math.round(((outerRadius + 10) * cosAngleDelta));
			int y4 = origY + (int) Math.round(((outerRadius + 10) * sinAngleDelta));

			Polygon poly = new Polygon();
			poly.addPoint(x1,y1);
			poly.addPoint(x2,y2);
			poly.addPoint(x4,y4);
			poly.addPoint(x3,y3);

			g.setColor(Color.magenta);
			g.fillPolygon(poly);

			g.setColor(Color.black);
			g.drawPolygon(poly);
		}

		fVehicleAnimationPanel.repaint();
	}

	private void updateLoopDetectorPlots()
	{
		if (!fLoopDetectorPlotsCheckBox.isSelected()) {
			return;
		}

		if (fLoopDetectorVPlotImage == null) {

			// show information of local loop detectors
			fLoopDetectorPlotsWidth = kLoopDetectorPlotTimeHorizon * kLoopDetectorPlotSize;
			fLoopDetectorPlotsHeight = fTCA.fLoopDetectors.size() * kLoopDetectorPlotSize;

			// create V-plot
			if (fLoopDetectorVPlotImage == null) {
				fLoopDetectorVPlotImage = createImage(fLoopDetectorPlotsWidth,fLoopDetectorPlotsHeight);

				// fill image with the panel's background-color
				Graphics g = fLoopDetectorVPlotImage.getGraphics();
				g.setColor(fLoopDetectorPlotsPanel.getBackground());
				g.fillRect(0,0,fLoopDetectorPlotsWidth,fLoopDetectorPlotsHeight);
			}

			// create Q-plot
			if (fLoopDetectorQPlotImage == null) {
				fLoopDetectorQPlotImage = createImage(fLoopDetectorPlotsWidth,fLoopDetectorPlotsHeight);

				// fill image with the panel's background-color
				Graphics g = fLoopDetectorQPlotImage.getGraphics();
				g.setColor(fLoopDetectorPlotsPanel.getBackground());
				g.fillRect(0,0,fLoopDetectorPlotsWidth,fLoopDetectorPlotsHeight);
			}

			// create K-plot
			if (fLoopDetectorKPlotImage == null) {
				fLoopDetectorKPlotImage = createImage(fLoopDetectorPlotsWidth,fLoopDetectorPlotsHeight);

				// fill image with the panel's background-color
				Graphics g = fLoopDetectorKPlotImage.getGraphics();
				g.setColor(fLoopDetectorPlotsPanel.getBackground());
				g.fillRect(0,0,fLoopDetectorPlotsWidth,fLoopDetectorPlotsHeight);
			}

			// create kq-plot
			if (fFundamentalDiagramKQImage == null) {
				fFundamentalDiagramKQImage = createImage(kFundamentalDiagramWidth,kFundamentalDiagramHeight);

				// fill image with a uniform dark-green color
				Graphics g = fFundamentalDiagramKQImage.getGraphics();
				g.setColor(new Color(1.0f,1.0f,1.0f));
				g.fillRect(0,0,kFundamentalDiagramWidth,kFundamentalDiagramHeight);
			}

			// create kv-plot
			if (fFundamentalDiagramKVImage == null) {
				fFundamentalDiagramKVImage = createImage(kFundamentalDiagramWidth,kFundamentalDiagramHeight);

				// fill image with a uniform dark-green color
				Graphics g = fFundamentalDiagramKVImage.getGraphics();
				g.setColor(new Color(1.0f,1.0f,1.0f));
				g.fillRect(0,0,kFundamentalDiagramWidth,kFundamentalDiagramHeight);
			}

			// create qv-plot
			if (fFundamentalDiagramQVImage == null) {
				fFundamentalDiagramQVImage = createImage(kFundamentalDiagramWidth,kFundamentalDiagramHeight);

				// fill image with a uniform dark-green color
				Graphics g = fFundamentalDiagramQVImage.getGraphics();
				g.setColor(new Color(1.0f,1.0f,1.0f));
				g.fillRect(0,0,kFundamentalDiagramWidth,kFundamentalDiagramHeight);
			}
		}

		// has the loop detectors's measurement interval elapsed ?
		LoopDetector loopDetector = (LoopDetector) fTCA.fLoopDetectors.elementAt(0);
		if (loopDetector.detectorUpdated()) {

			int currentTime = fTCA.fState.fTime / loopDetector.fMeasurementInterval;
			if (currentTime >= (kLoopDetectorPlotTimeHorizon - 1)) {
				currentTime = kLoopDetectorPlotTimeHorizon - 1;
			}

			Graphics gV = fLoopDetectorVPlotImage.getGraphics();
			Graphics gQ = fLoopDetectorQPlotImage.getGraphics();
			Graphics gK = fLoopDetectorKPlotImage.getGraphics();
			Graphics gKQ = fFundamentalDiagramKQImage.getGraphics();
			Graphics gKV = fFundamentalDiagramKVImage.getGraphics();
			Graphics gQV = fFundamentalDiagramQVImage.getGraphics();

			// scroll past the time horizon
			if (currentTime == (kLoopDetectorPlotTimeHorizon - 1)) {
				gV.copyArea(kLoopDetectorPlotSize,0,fLoopDetectorPlotsWidth,fLoopDetectorPlotsHeight,-kLoopDetectorPlotSize,0);
				gQ.copyArea(kLoopDetectorPlotSize,0,fLoopDetectorPlotsWidth,fLoopDetectorPlotsHeight,-kLoopDetectorPlotSize,0);
				gK.copyArea(kLoopDetectorPlotSize,0,fLoopDetectorPlotsWidth,fLoopDetectorPlotsHeight,-kLoopDetectorPlotSize,0);
			}

			int detectorNr = 0;
			Enumeration loopDetectorEnumeration = fTCA.fLoopDetectors.elements();
			while (loopDetectorEnumeration.hasMoreElements()) {
				loopDetector = (LoopDetector) loopDetectorEnumeration.nextElement();

				if (!fGlobalLoopDetectorValuesCheckBox.isSelected()) {

					if (fLogLoopDetectorValuesCheckBox.isSelected()) {

						// calculate average gapsize
						double sumOfGapSizes = 0.0;
						double sumOfGapSizesSquared = 0.0;
						int nrOfVehicles = 0;
						for (int cellNr = 0; cellNr < fTCA.fState.fCells.length; ++cellNr) {
							Cell cell = (Cell) fTCA.fState.fCells[cellNr];

							if (cell != null) {
								sumOfGapSizes += cell.fSpaceGap;
								sumOfGapSizesSquared += (cell.fSpaceGap * cell.fSpaceGap);
								++nrOfVehicles;
							}
						}

						double averageGapSize = 0.0;
						double gapSizeVariance = 0.0;

						if (nrOfVehicles > 0) {
							averageGapSize = (sumOfGapSizes / (double) nrOfVehicles);

							if (nrOfVehicles > 1) {
								gapSizeVariance =
									(1 / ((double) (nrOfVehicles - 1))) *
									(sumOfGapSizesSquared -
									 (2.0 * sumOfGapSizes * averageGapSize) +
									 (nrOfVehicles * averageGapSize * averageGapSize));
							}
						}

						try {
							fDetectorFile.writeDouble(loopDetector.fMeanFlowRate);
							fDetectorFile.writeString("\t");

							fDetectorFile.writeDouble(loopDetector.fMeanDensity);
							fDetectorFile.writeString("\t");

							fDetectorFile.writeDouble(loopDetector.fSpaceMeanSpeed);
							fDetectorFile.writeString("\t");

							fDetectorFile.writeDouble(averageGapSize);
							fDetectorFile.writeString("\t");

							fDetectorFile.writeDouble(gapSizeVariance);
							fDetectorFile.writeLn();
						}
						catch (Exception exc) {
						}
					}
				}

				int fraction = (int) Math.round((100.0 / kMaxSpaceMeanSpeedForPlotting) * loopDetector.fSpaceMeanSpeed);
				gV.setColor(JGradientColorRamp.interpolate((100.0 - (double) fraction) / 100.0));
				gV.fillRect(
					currentTime * kLoopDetectorPlotSize,
					fLoopDetectorPlotsHeight - (detectorNr * kLoopDetectorPlotSize) - kLoopDetectorPlotSize,
					kLoopDetectorPlotSize,
					kLoopDetectorPlotSize);

				fraction = (int) Math.round((100.0 / kMaxFlowRateForPlotting) * loopDetector.fMeanFlowRate);
				gQ.setColor(JGradientColorRamp.interpolate((100.0 - (double) fraction) / 100.0));
				gQ.fillRect(
					currentTime * kLoopDetectorPlotSize,
					fLoopDetectorPlotsHeight - (detectorNr * kLoopDetectorPlotSize) - kLoopDetectorPlotSize,
					kLoopDetectorPlotSize,
					kLoopDetectorPlotSize);

				fraction = (int) Math.round((100.0 / kMaxDensityForPlotting) * loopDetector.fMeanDensity);
				gK.setColor(JGradientColorRamp.interpolate((double) fraction / 100.0));
				gK.fillRect(
					currentTime * kLoopDetectorPlotSize,
					fLoopDetectorPlotsHeight - (detectorNr * kLoopDetectorPlotSize) - kLoopDetectorPlotSize,
					kLoopDetectorPlotSize,
					kLoopDetectorPlotSize);

				if (!fGlobalLoopDetectorValuesCheckBox.isSelected()) {

					Color plotColor = new Color((float) Math.random(),(float) Math.random(),(float) Math.random());

					int x = (int) Math.round((double) loopDetector.fMeanDensity * ((double) kFundamentalDiagramWidth / kMaxDensityForPlotting));
					int y = kFundamentalDiagramHeight -
						(int) Math.round((double) loopDetector.fMeanFlowRate * ((double) kFundamentalDiagramHeight / kMaxFlowRateForPlotting));

					int speed = (int) Math.round(((double) loopDetector.fSpaceMeanSpeed / kMaxSpaceMeanSpeedForPlotting) * 100.0);
					if (fColorCodeFundamentalDiagramsCheckBox.isSelected()) {
						gKQ.setColor(JGradientColorRamp.interpolate((100.0 - (double) speed) / 100.0));
					}
					else {
						gKQ.setColor(plotColor);
					}
					gKQ.fillRect(
						x - (kFundamentalDiagramPlotSize / 2),y - (kFundamentalDiagramPlotSize / 2),
						kFundamentalDiagramPlotSize - 1,kFundamentalDiagramPlotSize - 1);

					x = (int) Math.round((double) loopDetector.fMeanDensity * ((double) kFundamentalDiagramWidth / kMaxDensityForPlotting));
					y = kFundamentalDiagramHeight -
						(int) Math.round((double) loopDetector.fSpaceMeanSpeed * ((double) kFundamentalDiagramHeight / kMaxSpaceMeanSpeedForPlotting));

					int flow = (int) Math.round(((double) loopDetector.fMeanFlowRate / kMaxFlowRateForPlotting) * 100.0);
					if (fColorCodeFundamentalDiagramsCheckBox.isSelected()) {
						gKV.setColor(JGradientColorRamp.interpolate((100.0 - (double) flow) / 100.0));
					}
					else {
						gKV.setColor(plotColor);
					}
					gKV.fillRect(
						x - (kFundamentalDiagramPlotSize / 2),y - (kFundamentalDiagramPlotSize / 2),
						kFundamentalDiagramPlotSize - 1,kFundamentalDiagramPlotSize - 1);

					x = (int) Math.round((double) loopDetector.fMeanFlowRate * ((double) kFundamentalDiagramHeight / kMaxFlowRateForPlotting));
					y = kFundamentalDiagramHeight -
						(int) Math.round((double) loopDetector.fSpaceMeanSpeed * ((double) kFundamentalDiagramWidth / kMaxSpaceMeanSpeedForPlotting));

					int density = (int) Math.round(((double) loopDetector.fMeanDensity / kMaxDensityForPlotting) * 100.0);
					if (fColorCodeFundamentalDiagramsCheckBox.isSelected()) {
						gQV.setColor(JGradientColorRamp.interpolate((double) density / 100.0));
					}
					else {
						gQV.setColor(plotColor);
					}
					gQV.fillRect(
						x - (kFundamentalDiagramPlotSize / 2),y - (kFundamentalDiagramPlotSize / 2),
						kFundamentalDiagramPlotSize - 1,kFundamentalDiagramPlotSize - 1);
				}

				++detectorNr;
			}

			fLoopDetectorPlotsPanel.repaint();
		}

		if (fGlobalLoopDetectorValuesCheckBox.isSelected()) {

			if (fTCA.globalMeasurementsUpdated()) {

				if (fLogLoopDetectorValuesCheckBox.isSelected()) {

					try {
						fDetectorFile.writeDouble(fTCA.fMeanGlobalFlow);
						fDetectorFile.writeString("\t");

						fDetectorFile.writeDouble(fTCA.fGlobalDensity);
						fDetectorFile.writeString("\t");

						fDetectorFile.writeDouble(fTCA.fSpaceMeanSpeed);
						fDetectorFile.writeString("\t");

						fDetectorFile.writeDouble(fTCA.fAverageGapSize);
						fDetectorFile.writeString("\t");

						fDetectorFile.writeDouble(fTCA.fGapSizeVariance);
						fDetectorFile.writeLn();
					}
					catch (Exception exc) {
					}
				}

				Graphics gKQ = fFundamentalDiagramKQImage.getGraphics();
				Graphics gKV = fFundamentalDiagramKVImage.getGraphics();
				Graphics gQV = fFundamentalDiagramQVImage.getGraphics();

				int x = (int) Math.round(fTCA.fGlobalDensity * ((double) kFundamentalDiagramWidth / kMaxDensityForPlotting));
				int y = kFundamentalDiagramHeight -
					(int) Math.round(fTCA.fMeanGlobalFlow * ((double) kFundamentalDiagramHeight / kMaxFlowRateForPlotting));

				gKQ.setColor(Color.black);
				gKQ.fillRect(
					x - (kFundamentalDiagramPlotSize / 2),y - (kFundamentalDiagramPlotSize / 2),
					kFundamentalDiagramPlotSize - 1,kFundamentalDiagramPlotSize - 1);

				x = (int) Math.round(fTCA.fGlobalDensity * ((double) kFundamentalDiagramWidth / kMaxDensityForPlotting));
				y = kFundamentalDiagramHeight -
					(int) Math.round(fTCA.fSpaceMeanSpeed * ((double) kFundamentalDiagramHeight / kMaxSpaceMeanSpeedForPlotting));

				gKV.setColor(Color.black);
				gKV.fillRect(
					x - (kFundamentalDiagramPlotSize / 2),y - (kFundamentalDiagramPlotSize / 2),
					kFundamentalDiagramPlotSize - 1,kFundamentalDiagramPlotSize - 1);

				x = (int) Math.round(fTCA.fMeanGlobalFlow * ((double) kFundamentalDiagramHeight / kMaxFlowRateForPlotting));
				y = kFundamentalDiagramHeight -
					(int) Math.round(fTCA.fSpaceMeanSpeed * ((double) kFundamentalDiagramWidth / kMaxSpaceMeanSpeedForPlotting));

				gQV.setColor(Color.black);
				gQV.fillRect(
					x - (kFundamentalDiagramPlotSize / 2),y - (kFundamentalDiagramPlotSize / 2),
					kFundamentalDiagramPlotSize - 1,kFundamentalDiagramPlotSize - 1);
			}

			fLoopDetectorPlotsPanel.repaint();
		}
	}

	private void updateStatistics()
	{
		fNrOfVehiclesLabel.setText(String.valueOf(fTCA.fState.fNrOfVehicles));

		double vehicleDensity = (((double) fTCA.fState.fNrOfVehicles) / (fTCA.fState.fCells.length * fTCA.fCellLength) * 1000.0);
		fVehicleDensityLabel.setText(String.valueOf((int) Math.round(vehicleDensity)) + " vehicles/km");

		fMaxSpeedLabel.setText(String.valueOf(fTCA.fMaxSpeed * fTCA.fCellLength * 3.6) + " km/h");

		fCurrentTimeLabel.setText(String.valueOf(fTCA.fState.fTime) + " cycles");
	}

	private void setGlobalDensityLabel()
	{
		double globalDensity = Math.round(((double) fTCA.fState.fNrOfVehicles / fTCA.fState.fCells.length) * 10000.0) / 100.0;
		String densityStr = String.valueOf(globalDensity);

		double fractionalPart = Math.abs(Math.floor(Math.abs(globalDensity)) - Math.abs(globalDensity));
		int decimals = (int) Math.round(fractionalPart * 100.0);

		if ((decimals % 10) == 0) {
			densityStr += "0";
		}
		if (globalDensity < 100.0) {
			densityStr = "0" + densityStr;
		}
		if (globalDensity < 10.0) {
			densityStr = "0" + densityStr;
		}

		fGlobalDensityScrollBarLabel.setText("Global density [" + String.valueOf(densityStr) + " % of road] :");
	}

	private void controlTrafficLight()
	{
		++fTrafficLightTimer;

		if (fTrafficLightControlsEnabledCheckBox.isSelected()) {

			boolean oldTCARunning = fTCARunning;

			if (fTCA.fTrafficLightIsRed) {
				// do we need to switch to green ?
				if (fTrafficLightTimer > fTrafficLightRedCycleInputField.getIntegerValue()) {
					fTCARunning = false;
					fTrafficLightTimer = 0;
					fTCA.fTrafficLightIsRed = false;
					fTCARunning = oldTCARunning;
				} 
			}
			else {
				// do we need to switch to red ?
				if (fTrafficLightTimer > fTrafficLightGreenCycleInputField.getIntegerValue()) {
					fTCARunning = false;
					fTrafficLightTimer = 0;
					fTCA.fTrafficLightIsRed = true;
					fTCARunning = oldTCARunning;
				} 
			}
		}
	}

	private void clearPlots()
	{
		fTXDiagramImage = null;
		fTXDiagramPanel.repaint();
		fVehicleAnimationImage = null;
		fVehicleAnimationPanel.repaint();
		fLoopDetectorVPlotImage = null;
		fLoopDetectorQPlotImage = null;
		fLoopDetectorKPlotImage = null;
		fFundamentalDiagramKQImage = null;
		fFundamentalDiagramKVImage = null;
		fFundamentalDiagramQVImage = null;
		fLoopDetectorPlotsPanel.repaint();
	}

	// the runnable-interface
	synchronized public void run()
	{
		while (true) {
			if (fTCARunning) {

				// advance cellular automaton one step
				fTCA.advanceOneStep();

				controlTrafficLight();

				updateStatistics();

				// update graphical components
				updateTXDiagram();
				updateVehicleAnimation();
				updateLoopDetectorPlots();

				// if necessary, wait some time
				if (fCycleHoldTime != 0) {
					try {
						Thread.sleep(fCycleHoldTime);
					}
					catch (InterruptedException exc) {
					}
				}

				if (kSaveTCALatticeToDisk) {

					// save TCA's lattice to file (each timestep)
					try {
						for (int cellNr = 0; cellNr < fTCA.fState.fCells.length; ++cellNr) {
							Cell cell = fTCA.fState.fCells[cellNr];

							if (cell != null) {
								fLatticeFile.writeString(" " + String.valueOf(cell.fSpeed) + " ");
							}
							else {
								fLatticeFile.writeString("-1 ");
							}
						}

						fLatticeFile.writeLn();
					}
					catch (Exception exc) {
					}
				}
			}
		}
	}

	// the action-listener
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		if (command.startsWith(kSelectTCA)) {

			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			String tcaType = command.substring(command.indexOf(' '));

			int maxSpeed = fTCA.fMaxSpeed;
			boolean trafficLightIsRed = fTCA.fTrafficLightIsRed;

			State state = fTCA.fState;
			fTCA.reset();

			fTCA.fMaxSpeed = maxSpeed;
			fTCA.fTrafficLightIsRed = trafficLightIsRed;

			if (tcaType.equalsIgnoreCase(" " + kCA184)) {

				TrafficCellularAutomaton tca = new CA184(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected());
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(false);
				fSlowdownProbabilityScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
				fAverageTimeHeadwayScrollBar.setEnabled(false);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
				fAccelerationProbabilityScrollBar.setEnabled(false);
				fAccelerationProbabilityScrollBarLabel.setEnabled(false);
				fDecelerationProbabilityScrollBar.setEnabled(false);
				fDecelerationProbabilityScrollBarLabel.setEnabled(false);
			}
			else if (tcaType.equalsIgnoreCase(" " + kStochasticTCA)) {

				TrafficCellularAutomaton tca = new StochasticTCA(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected(),
					fSlowdownProbabilityScrollBar.getValue() / 100.0f);
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(true);
				fSlowdownProbabilityScrollBarLabel.setEnabled(true);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
				fAverageTimeHeadwayScrollBar.setEnabled(false);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
				fAccelerationProbabilityScrollBar.setEnabled(false);
				fAccelerationProbabilityScrollBarLabel.setEnabled(false);
				fDecelerationProbabilityScrollBar.setEnabled(false);
				fDecelerationProbabilityScrollBarLabel.setEnabled(false);
			}
			else if (tcaType.equalsIgnoreCase(" " + kStochasticCCTCA)) {

				TrafficCellularAutomaton tca = new StochasticCCTCA(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected(),
					fSlowdownProbabilityScrollBar.getValue() / 100.0f);
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(true);
				fSlowdownProbabilityScrollBarLabel.setEnabled(true);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
				fAverageTimeHeadwayScrollBar.setEnabled(false);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
				fAccelerationProbabilityScrollBar.setEnabled(false);
				fAccelerationProbabilityScrollBarLabel.setEnabled(false);
				fDecelerationProbabilityScrollBar.setEnabled(false);
				fDecelerationProbabilityScrollBarLabel.setEnabled(false);
			}
			else if (tcaType.equalsIgnoreCase(" " + kVDRTCA)) {

				TrafficCellularAutomaton tca = new VDRTCA(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected(),
					fSlowdownProbabilityScrollBar.getValue() / 100.0f,
					fSlowdownProbabilityWhenStandingStillScrollBar.getValue() / 100.0f);
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(true);
				fSlowdownProbabilityScrollBarLabel.setEnabled(true);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(true);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(true);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
				fAverageTimeHeadwayScrollBar.setEnabled(false);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
				fAccelerationProbabilityScrollBar.setEnabled(false);
				fAccelerationProbabilityScrollBarLabel.setEnabled(false);
				fDecelerationProbabilityScrollBar.setEnabled(false);
				fDecelerationProbabilityScrollBarLabel.setEnabled(false);
			}
			else if (tcaType.equalsIgnoreCase(" " + kVDRCCTCA)) {

				TrafficCellularAutomaton tca = new VDRCCTCA(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected(),
					fSlowdownProbabilityScrollBar.getValue() / 100.0f,
					fSlowdownProbabilityWhenStandingStillScrollBar.getValue() / 100.0f);
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(true);
				fSlowdownProbabilityScrollBarLabel.setEnabled(true);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(true);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(true);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
				fAverageTimeHeadwayScrollBar.setEnabled(false);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
				fAccelerationProbabilityScrollBar.setEnabled(false);
				fAccelerationProbabilityScrollBarLabel.setEnabled(false);
				fDecelerationProbabilityScrollBar.setEnabled(false);
				fDecelerationProbabilityScrollBarLabel.setEnabled(false);
			}
			else if (tcaType.equalsIgnoreCase(" " + kFukuiIshibashiTCA)) {

				TrafficCellularAutomaton tca = new FukuiIshibashiTCA(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected(),
					fSlowdownProbabilityScrollBar.getValue() / 100.0f);
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(true);
				fSlowdownProbabilityScrollBarLabel.setEnabled(true);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
				fAverageTimeHeadwayScrollBar.setEnabled(false);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
				fAccelerationProbabilityScrollBar.setEnabled(false);
				fAccelerationProbabilityScrollBarLabel.setEnabled(false);
				fDecelerationProbabilityScrollBar.setEnabled(false);
				fDecelerationProbabilityScrollBarLabel.setEnabled(false);
			}
			else if (tcaType.equalsIgnoreCase(" " + kOriginalTakayasuTCA)) {

				TrafficCellularAutomaton tca = new OriginalTakayasuTCA(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected());
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(false);
				fSlowdownProbabilityScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
				fAverageTimeHeadwayScrollBar.setEnabled(false);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
				fAccelerationProbabilityScrollBar.setEnabled(false);
				fAccelerationProbabilityScrollBarLabel.setEnabled(false);
				fDecelerationProbabilityScrollBar.setEnabled(false);
				fDecelerationProbabilityScrollBarLabel.setEnabled(false);
			}
			else if (tcaType.equalsIgnoreCase(" " + kTakayasuTCA)) {

				TrafficCellularAutomaton tca = new TakayasuTCA(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected(),
					fSlowdownProbabilityScrollBar.getValue() / 100.0f,
					fSlowdownProbabilityWithSmallGapsScrollBar.getValue() / 100.0f);
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(true);
				fSlowdownProbabilityScrollBarLabel.setEnabled(true);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(true);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(true);
				fAverageTimeHeadwayScrollBar.setEnabled(false);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
				fAccelerationProbabilityScrollBar.setEnabled(false);
				fAccelerationProbabilityScrollBarLabel.setEnabled(false);
				fDecelerationProbabilityScrollBar.setEnabled(false);
				fDecelerationProbabilityScrollBarLabel.setEnabled(false);
			}
			else if (tcaType.equalsIgnoreCase(" " + kTimeOrientedTCA)) {

				TrafficCellularAutomaton tca = new TimeOrientedTCA(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected(),
					fAverageTimeHeadwayScrollBar.getValue() / 100.0f,
					fAccelerationProbabilityScrollBar.getValue() / 100.0f,
					fDecelerationProbabilityScrollBar.getValue() / 100.0f);
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(false);
				fSlowdownProbabilityScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
				fAverageTimeHeadwayScrollBar.setEnabled(true);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(true);
				fAccelerationProbabilityScrollBar.setEnabled(true);
				fAccelerationProbabilityScrollBarLabel.setEnabled(true);
				fDecelerationProbabilityScrollBar.setEnabled(true);
				fDecelerationProbabilityScrollBarLabel.setEnabled(true);
			}
			else if (tcaType.equalsIgnoreCase(" " + kTASEP)) {

				TrafficCellularAutomaton tca = new TASEP(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected());
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(false);
				fSlowdownProbabilityScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
				fAverageTimeHeadwayScrollBar.setEnabled(false);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
				fAccelerationProbabilityScrollBar.setEnabled(false);
				fAccelerationProbabilityScrollBarLabel.setEnabled(false);
				fDecelerationProbabilityScrollBar.setEnabled(false);
				fDecelerationProbabilityScrollBarLabel.setEnabled(false);
			}
			else if (tcaType.equalsIgnoreCase(" " + kEmmerichRank)) {

				TrafficCellularAutomaton tca = new EmmerichRankTCA(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected(),
					fSlowdownProbabilityScrollBar.getValue() / 100.0f);
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(true);
				fSlowdownProbabilityScrollBarLabel.setEnabled(true);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
				fAverageTimeHeadwayScrollBar.setEnabled(false);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
				fAccelerationProbabilityScrollBar.setEnabled(false);
				fAccelerationProbabilityScrollBarLabel.setEnabled(false);
				fDecelerationProbabilityScrollBar.setEnabled(false);
				fDecelerationProbabilityScrollBarLabel.setEnabled(false);
			}
			else if (tcaType.equalsIgnoreCase(" " + kCochinosTCA)) {

				TrafficCellularAutomaton tca = new CochinosTCA(
					state,
					fTCA.fMaxSpeed,
					fTCA.fCellLength,
					fTCA.fLoopDetectors.size(),
					fTCA.fDetectorRange,
					fTCA.fLoopDetectorMeasurementInterval,
					fMultipleOfSampleTimeToSimulate,
					fGlobalLoopDetectorValuesCheckBox.isSelected());
				fTCA = tca;

				fSlowdownProbabilityScrollBar.setEnabled(false);
				fSlowdownProbabilityScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBar.setEnabled(false);
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBar.setEnabled(false);
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setEnabled(false);
				fAverageTimeHeadwayScrollBar.setEnabled(false);
				fAverageTimeHeadwayScrollBarLabel.setEnabled(false);
				fAccelerationProbabilityScrollBar.setEnabled(false);
				fAccelerationProbabilityScrollBarLabel.setEnabled(false);
				fDecelerationProbabilityScrollBar.setEnabled(false);
				fDecelerationProbabilityScrollBarLabel.setEnabled(false);
			}

			fTCARunning = wasRunning;
		}
		else if (command.equalsIgnoreCase(kStart)) {
			fTCARunning = true;
			fStartButton.setBackground(Color.lightGray);
			fStopButton.setBackground(Color.red);
			fResetButton.setBackground(Color.red);
		}
		else if (command.equalsIgnoreCase(kStop)) {
			fTCARunning = false;
			fStartButton.setBackground(Color.green);
			fStopButton.setBackground(Color.lightGray);
			fResetButton.setBackground(Color.lightGray);
		}
		else if (command.equalsIgnoreCase(kReset)) {
			fTCARunning = false;
			fTCA.reset();
			fTCA.fState.fTime = 0;
			clearPlots();
			fStartButton.setBackground(Color.green);
			fStopButton.setBackground(Color.lightGray);
			fResetButton.setBackground(Color.lightGray);
			fMaxSpeedScrollBar.setValue(fTCA.fMaxSpeed);
			fGlobalDensityScrollBar.setValue(fTCA.fState.fNrOfVehicles);
			setGlobalDensityLabel();
			updateStatistics();
			fTrafficLightTimer = 0;
		}
		else if (command.equalsIgnoreCase(kQuit)) {
			windowClosing(null);
		}
		else if (command.equalsIgnoreCase(kRed)) {
			fTCA.fTrafficLightIsRed = true;
		}
		else if (command.equalsIgnoreCase(kGreen)) {
			fTCA.fTrafficLightIsRed = false;
		}
		else if (command.equalsIgnoreCase(kClearPlots)) {
			fTCA.fState.fTime = 0;
			clearPlots();
		}
		else if (command.equalsIgnoreCase(kConstructFundamentalDiagrams)) {

			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			fConstructFundamentalDiagramsProgressBar.setMaximum(fNrOfDensitySteps);
			fConstructFundamentalDiagramsProgressBar.setString("Computing time left...");
			fConstructFundamentalDiagramsProgressBar.setBorderPainted(true);

			if (fLogLoopDetectorValuesCheckBox.isSelected()) {
				try {
					fDetectorFile = new TextFileWriter("detector-values.data");
				}
				catch (Exception exc) {
				}
			}

			int oldDensity = fGlobalDensityScrollBar.getValue();

			fConstructingFundamentalDiagrams = true;

			Chrono chrono = new Chrono();

			for (int densityStep = 1; densityStep < fNrOfDensitySteps; ++densityStep) {

				fTCA.reset();

				// calculate a monotonically increasing density corresponding to the density-step
				State state = fTCA.fState;
				int time = state.fTime;
				state.clear();
				double density = ((double) densityStep / ((double) fNrOfDensitySteps - 1)) * fTCA.fState.fCells.length;
				fGlobalDensityScrollBar.setValue((int) Math.floor(density));

				state.distributeVehicles(
					fGlobalDensityScrollBar.getValue(),
					fTCA.fMaxSpeed,
					fDistributeVehiclesHomogeneouslyCheckBox.isSelected(),
					fStartWithCompactJamCheckBox.isSelected());
				state.fTime = time;
				fTCA.setState(state);
				setGlobalDensityLabel();


				// remove transient period
				for (int timeStep = 0; timeStep < fTransientPeriod; ++timeStep) {

					// advance cellular automaton one step
					fTCA.advanceOneStep();
				}

				// simulate a finite number of steps (i.e., a multiple of an LD's measurement period) and gather the data
				for (int timeStep = 0; timeStep < (fMultipleOfSampleTimeToSimulate * fLoopDetectorSampleTimeScrollBar.getValue()); ++timeStep) {

					updateLoopDetectorPlots();

					// advance cellular automaton one step
					fTCA.advanceOneStep();
				}

				fConstructFundamentalDiagramsProgressBar.setValue(densityStep);

				// repaint entire display
				update(getGraphics());

				double percentCompleted = ((double) densityStep / (double) fNrOfDensitySteps) * 100.0;

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

					fConstructFundamentalDiagramsProgressBar.setString(timeSpentString + " / " + timeLeftString);
				}
			}

			fConstructFundamentalDiagramsProgressBar.setValue(0);
			fConstructFundamentalDiagramsProgressBar.setString("");
			fConstructFundamentalDiagramsProgressBar.setBorderPainted(false);
			fConstructFundamentalDiagramsProgressBar.repaint();

			fConstructingFundamentalDiagrams = false;

			updateStatistics();

			// update graphical components
			updateVehicleAnimation();
			updateLoopDetectorPlots();

			fGlobalDensityScrollBar.setValue(oldDensity);

			fTCARunning = wasRunning;
		}
		else if (command.equalsIgnoreCase(kGlobalLoopDetectorValues)) {
			fTCA.fPerformGlobalMeasurements = fGlobalLoopDetectorValuesCheckBox.isSelected();
		}		
		else if (command.equalsIgnoreCase(kLogLoopDetectorValues)) {
			if (fLogLoopDetectorValuesCheckBox.isSelected()) {
				try {
					fDetectorFile = new TextFileWriter("detector-values.data");
				}
				catch (Exception exc) {
				}
			}
		}		
		else if (command.equalsIgnoreCase(kDistributeHomogeneously)) {

			if (fStartWithCompactJamCheckBox.isSelected()) {
				fStartWithCompactJamCheckBox.setSelected(false);
			}
		}		
		else if (command.equalsIgnoreCase(kStartWithCompactJam)) {

			if (fDistributeVehiclesHomogeneouslyCheckBox.isSelected()) {
				fDistributeVehiclesHomogeneouslyCheckBox.setSelected(false);
			}
		}
	}
           
	// the window-listener
	public void windowActivated(WindowEvent e) { }
	public void windowClosed(WindowEvent e) { }
	public void windowClosing(WindowEvent e)
	{
		// quit the running program
		System.exit(0);
	}
	public void windowDeactivated(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }

	// the adjustment-listener
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		JScrollBar source = (JScrollBar) e.getSource();

		if (source == fCycleHoldTimeScrollBar) {
			fCycleHoldTime = fCycleHoldTimeScrollBar.getValue();
			fCycleHoldTimeScrollBarLabel.setText("Cycle hold time [" + String.valueOf(fCycleHoldTime) + " ms] :");
		}
		else if (source == fGlobalDensityScrollBar) {
			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			State state = fTCA.fState;
			int time = state.fTime;
			state.clear();
			state.distributeVehicles(
				fGlobalDensityScrollBar.getValue(),
				fTCA.fMaxSpeed,
				fDistributeVehiclesHomogeneouslyCheckBox.isSelected(),
				fStartWithCompactJamCheckBox.isSelected());
			state.fTime = time;
			fTCA.setState(state);
			setGlobalDensityLabel();

			fTCARunning = wasRunning;
		}
		else if (source == fMaxSpeedScrollBar) {
			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			fTCA.setGlobalMaxSpeed(fMaxSpeedScrollBar.getValue());
			fMaxSpeedScrollBarLabel.setText("Maximum speed [" + String.valueOf(fTCA.fMaxSpeed) + " cells/cycle] :");

			fTCARunning = wasRunning;
		}
		else if (source == fLoopDetectorSampleTimeScrollBar) {
			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			fTCA.setLoopDetectorsMeasurementInterval(fLoopDetectorSampleTimeScrollBar.getValue());
			fLoopDetectorSampleTimeScrollBarLabel.setText("Detector sampling time [" + String.valueOf(fTCA.fLoopDetectorMeasurementInterval) + " s] :");

			fTCARunning = wasRunning;
		}
		else if (source == fSlowdownProbabilityScrollBar) {
			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			if (fTCA instanceof StochasticTCA) {
				((StochasticTCA) fTCA).fSlowdownProbability = ((double) fSlowdownProbabilityScrollBar.getValue()) / 100.0;
				fSlowdownProbabilityScrollBarLabel.setText("Slowdown probability [" +
									(int) Math.round(((StochasticTCA) fTCA).fSlowdownProbability * 100.0) + " %] :");
			}

			fTCARunning = wasRunning;
		}
		else if (source == fSlowdownProbabilityWhenStandingStillScrollBar) {
			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			if (fTCA instanceof VDRTCA) {
				((VDRTCA) fTCA).fSlowdownProbabilityWhenStandingStill = ((double) fSlowdownProbabilityWhenStandingStillScrollBar.getValue()) / 100.0;
				fSlowdownProbabilityWhenStandingStillScrollBarLabel.setText("VDR-factor  [" +
									(int) Math.round(((VDRTCA) fTCA).fSlowdownProbabilityWhenStandingStill * 100.0) + " %] :");
			}

			fTCARunning = wasRunning;
		}
		else if (source == fSlowdownProbabilityWithSmallGapsScrollBar) {
			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			if (fTCA instanceof TakayasuTCA) {
				((TakayasuTCA) fTCA).fSlowdownProbabilityWithSmallGaps = ((double) fSlowdownProbabilityWithSmallGapsScrollBar.getValue()) / 100.0;
				fSlowdownProbabilityWithSmallGapsScrollBarLabel.setText("Takayasu-factor  [" +
									(int) Math.round(((TakayasuTCA) fTCA).fSlowdownProbabilityWithSmallGaps * 100.0) + " %] :");
			}

			fTCARunning = wasRunning;
		}
		else if (source == fAverageTimeHeadwayScrollBar) {
			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			if (fTCA instanceof TimeOrientedTCA) {
				((TimeOrientedTCA) fTCA).fAverageTimeHeadway = ((double) fAverageTimeHeadwayScrollBar.getValue()) / 100.0;
				fAverageTimeHeadwayScrollBarLabel.setText("Average time-headway [" +
									String.valueOf(((TimeOrientedTCA) fTCA).fAverageTimeHeadway) + " s] :");
			}

			fTCARunning = wasRunning;
		}
		else if (source == fAccelerationProbabilityScrollBar) {
			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			if (fTCA instanceof TimeOrientedTCA) {
				((TimeOrientedTCA) fTCA).fAccelerationProbability = ((double) fAccelerationProbabilityScrollBar.getValue());
				fAccelerationProbabilityScrollBarLabel.setText("Acceleration probability [" +
									(int) Math.round(((TimeOrientedTCA) fTCA).fAccelerationProbability) + " %] :");
			}

			fTCARunning = wasRunning;
		}
		else if (source == fDecelerationProbabilityScrollBar) {
			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			if (fTCA instanceof TimeOrientedTCA) {
				((TimeOrientedTCA) fTCA).fDecelerationProbability = ((double) fDecelerationProbabilityScrollBar.getValue());
				fDecelerationProbabilityScrollBarLabel.setText("Deceleration probability [" +
									(int) Math.round(((TimeOrientedTCA) fTCA).fDecelerationProbability) + " %] :");
			}

			fTCARunning = wasRunning;
		}

		updateStatistics();
	}

	private class TXDiagram extends JPanel
	{
		public TXDiagram()
		{
			super();
			setPreferredSize(new Dimension(fTXDiagramWidth + 30,fTXDiagramHeight + 20));
		}

		public void update(Graphics g)
		{
			paint(g);
		}

		public void paint(Graphics g)
		{
			if (fConstructingFundamentalDiagrams) {
				return;
			}

			g.setColor(Color.black);
			g.drawRect(20,0,(fTXDiagramWidth + 2) - 1,(fTXDiagramHeight + 2) - 1);
			g.drawString("0",5,(fTXDiagramHeight + 2) - 1 + 20);
			g.drawString("t",15 + (fTXDiagramWidth + 2) - 1,(fTXDiagramHeight + 2) - 1 + 20);
			g.drawString("x",5,10);
			g.drawString("Visible time horizon = " + String.valueOf(fTXDiagramWidth) + " seconds",50,(fTXDiagramHeight + 2) - 1 + 20);

			if (fTXDiagramImage != null) {
				g.drawImage(fTXDiagramImage,21,1,this);
			}
		}

		public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h)
		{
			// only allow repainting of this canvas when the image is completely loaded
			// this prevents flickering
			if ((flags & ALLBITS) != 0) {
				repaint();
			}
			return ((flags & (ALLBITS | ERROR)) == 0);
		}
	}

	private class VehicleAnimation extends JPanel
	{
		public VehicleAnimation()
		{
			super();
			setPreferredSize(new Dimension(kVehicleAnimationWidth + 80,kVehicleAnimationHeight + 10));
		}

		public void update(Graphics g)
		{
			paint(g);
		}

		public void paint(Graphics g)
		{
			if (fConstructingFundamentalDiagrams) {
				return;
			}

			// show road
			g.setColor(Color.black);
			g.drawRect(10,0,kVehicleAnimationWidth - 1,kVehicleAnimationHeight - 1);

			if (fVehicleAnimationImage != null) {
				g.drawImage(fVehicleAnimationImage,11,1,this);
			}

			// show traffic light
			int width = 50;
			int height = 100;
			int radius = 20;

			g.setColor(Color.gray);
			g.fillRect(15 + kVehicleAnimationWidth - 1 + 10,50,width,height);
			g.setColor(Color.black);
			g.drawRect(15 + kVehicleAnimationWidth - 1 + 10,50,width,height);

			if (fTCA.fTrafficLightIsRed) {
				g.setColor(Color.red);
				g.fillOval(15 + kVehicleAnimationWidth - 1 + 10 + (width / 2) - radius,50 + (height / 4) - radius,2 * radius,2 * radius);
				g.setColor(Color.gray);
				g.fillOval(15 + kVehicleAnimationWidth - 1 + 10 + (width / 2) - radius,50 + ((3 * height) / 4) - radius,2 * radius,2 * radius);
			}
			else {
				g.setColor(Color.gray);
				g.fillOval(15 + kVehicleAnimationWidth - 1 + 10 + (width / 2) - radius,50 + (height / 4) - radius,2 * radius,2 * radius);
				g.setColor(Color.green);
				g.fillOval(15 + kVehicleAnimationWidth - 1 + 10 + (width / 2) - radius,50 + ((3 * height) / 4) - radius,2 * radius,2 * radius);
			}
			g.setColor(Color.black);
			g.drawOval(15 + kVehicleAnimationWidth - 1 + 10 + (width / 2) - radius,50 + (height / 4) - radius,2 * radius,2 * radius);
			g.drawOval(15 + kVehicleAnimationWidth - 1 + 10 + (width / 2) - radius,50 + ((3 * height) / 4) - radius,2 * radius,2 * radius);
		}

		public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h)
		{
			// only allow repainting of this canvas when the image is completely loaded
			// this prevents flickering
			if ((flags & ALLBITS) != 0) {
				repaint();
			}
			return ((flags & (ALLBITS | ERROR)) == 0);
		}
	}

	private class LoopDetectorPlots extends JPanel
	{
		public LoopDetectorPlots()
		{
			super();
			setPreferredSize(new Dimension(fLoopDetectorPlotsWidth + (kFundamentalDiagramWidth * 2) + 150,(fLoopDetectorPlotsHeight * 3) + 50));
		}

		public void update(Graphics g)
		{
			paint(g);
		}

		public void paint(Graphics g)
		{
			if (fConstructingFundamentalDiagrams) {
				return;
			}

			// ------------------------------
			// show the space-mean-speed plot
			// ------------------------------
			g.setColor(Color.black);
			g.drawRect(40,0,(fLoopDetectorPlotsWidth + 2) - 1,(fLoopDetectorPlotsHeight + 2) - 1);
			g.drawString("v(t,x)",5,10);

			// draw legend
			for (int i = 0; i < 100; ++i) {
				g.setColor(JGradientColorRamp.interpolate((100.0 - (double) i) / 100.0));
				g.drawLine(40 + (fLoopDetectorPlotsWidth + 15),i,40 + (fLoopDetectorPlotsWidth + 15) + 15,i);
			}
			g.setColor(Color.black);
			g.drawRect(40 + (fLoopDetectorPlotsWidth + 15),0,15,100);
			g.drawString(String.valueOf(kMaxSpaceMeanSpeedForPlotting),40 + (fLoopDetectorPlotsWidth + 15) + 15 + 10,100);
			g.drawString("0",40 + (fLoopDetectorPlotsWidth + 15) + 15 + 10,10);

			if (fLoopDetectorVPlotImage != null) {
				g.drawImage(fLoopDetectorVPlotImage,41,1,this);
			}

			int yOffset = 1 + fLoopDetectorPlotsHeight + 15;

			// -----------------------
			// show the flow-rate plot
			// -----------------------
			g.setColor(Color.black);
			g.drawRect(40,yOffset,(fLoopDetectorPlotsWidth + 2) - 1,(fLoopDetectorPlotsHeight + 2) - 1);
			g.drawString("q(t,x)",5,yOffset + 10);

			// draw legend
			for (int i = 0; i < 100; ++i) {
				g.setColor(JGradientColorRamp.interpolate((100.0 - (double) i) / 100.0));
				g.drawLine(40 + (fLoopDetectorPlotsWidth + 15),yOffset + i,40 + (fLoopDetectorPlotsWidth + 15) + 15,yOffset + i);
			}
			g.setColor(Color.black);
			g.drawRect(40 + (fLoopDetectorPlotsWidth + 15),yOffset,15,100);
			g.drawString(String.valueOf(kMaxFlowRateForPlotting),40 + (fLoopDetectorPlotsWidth + 15) + 15 + 10,yOffset + 100);
			g.drawString("0",40 + (fLoopDetectorPlotsWidth + 15) + 15 + 10,yOffset + 10);

			if (fLoopDetectorQPlotImage != null) {
				g.drawImage(fLoopDetectorQPlotImage,41,yOffset + 1,this);
			}

			yOffset += fLoopDetectorPlotsHeight + 15;

			// ---------------------
			// show the density plot
			// ---------------------
			g.setColor(Color.black);
			g.drawRect(40,yOffset,(fLoopDetectorPlotsWidth + 2) - 1,(fLoopDetectorPlotsHeight + 2) - 1);
			g.drawString("k(t,x)",5,yOffset + 10);

			// draw legend
			for (int i = 0; i < 100; ++i) {
				g.setColor(JGradientColorRamp.interpolate((100.0 - (double) i) / 100.0));
				g.drawLine(40 + (fLoopDetectorPlotsWidth + 15),yOffset + i,40 + (fLoopDetectorPlotsWidth + 15) + 15,yOffset + i);
			}
			g.setColor(Color.black);
			g.drawRect(40 + (fLoopDetectorPlotsWidth + 15),yOffset,15,100);
			g.drawString("0",40 + (fLoopDetectorPlotsWidth + 15) + 15 + 10,yOffset + 100);
			g.drawString(String.valueOf(kMaxDensityForPlotting),40 + (fLoopDetectorPlotsWidth + 15) + 15 + 10,yOffset + 10);

			if (fLoopDetectorKPlotImage != null) {
				g.drawImage(fLoopDetectorKPlotImage,41,yOffset + 1,this);
			}

			// -------------------------------
			// show the kq-fundamental-diagram
			// -------------------------------
			int xOffset = 40 + (fLoopDetectorPlotsWidth + 15) + 15 + 45;
			g.setColor(Color.black);
			g.drawRect(xOffset,20,(kFundamentalDiagramWidth + 2) - 1,(kFundamentalDiagramHeight + 2) - 1);
			g.drawString("(k,q)",xOffset,10);

			if (fFundamentalDiagramKQImage != null) {
				g.drawImage(fFundamentalDiagramKQImage,xOffset + 1,21,this);
			}

			// -------------------------------
			// show the qv-fundamental-diagram
			// -------------------------------
			yOffset = 20 + kFundamentalDiagramHeight + 20;
			g.setColor(Color.black);
			g.drawRect(xOffset,yOffset + 20,(kFundamentalDiagramWidth + 2) - 1,(kFundamentalDiagramHeight + 2) - 1);
			g.drawString("(q,v)",xOffset,yOffset + 10);

			if (fFundamentalDiagramQVImage != null) {
				g.drawImage(fFundamentalDiagramQVImage,xOffset + 1,yOffset + 21,this);
			}

			// -------------------------------
			// show the kv-fundamental-diagram
			// -------------------------------
			xOffset += kFundamentalDiagramWidth + 15;
			g.setColor(Color.black);
			g.drawRect(xOffset,20,(kFundamentalDiagramWidth + 2) - 1,(kFundamentalDiagramHeight + 2) - 1);
			g.drawString("(k,v)",xOffset,10);

			if (fFundamentalDiagramKVImage != null) {
				g.drawImage(fFundamentalDiagramKVImage,xOffset + 1,21,this);
			}
		}

		public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h)
		{
			// only allow repainting of this canvas when the image is completely loaded
			// this prevents flickering
			if ((flags & ALLBITS) != 0) {
				repaint();
			}
			return ((flags & (ALLBITS | ERROR)) == 0);
		}
	}
}
