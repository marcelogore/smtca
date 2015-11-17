// ---------------------------------
// Filename      : MultiCellGUI.java
// Author        : Sven Maerivoet
// Last modified : 15/09/2004
// Target        : Java VM
//
// All rights reserved.
// Katholieke Universiteit Leuven
// ---------------------------------

package tca.simulator;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import smtools.miscellaneous.*;
import smtools.swing.util.*;
import tca.automata.*;
import tca.base.*;

public class MultiCellGUI extends JFrame implements Runnable, ActionListener, WindowListener, AdjustmentListener
{
	// tx-diagram's time horizon
	public static int kTXDiagramTimeHorizon = 600;

	// vehicle-speed color switch
	public static boolean kHighlightStoppedVehicles = false;

	// some action-commands
	private static final String kStart                   = "button-start";
	private static final String kStop                    = "button-stop";
	private static final String kReset                   = "button-reset";
	private static final String kQuit                    = "button-quit";
	private static final String kSelectTCA               = "button-select-tca";
	private static final String kClearPlots              = "button-clear-plots";
	private static final String kTakeSnapshot            = "button-take-snapshot";
	private static final String kDistributeHomogeneously = "checkbox-distribute-homogeneously";

	// the different multi-cell TCA
	private static final String kHSTCA  = "1";
	private static final String kBLTCA  = "2";
	private static final String kKKWTCA = "3";

	// component-sizing contants
	private int              fTXDiagramWidth;
	private int              fTXDiagramHeight;
	private static final int kVehicleAnimationWidth      = 300;
	private static final int kVehicleAnimationHeight     = 300;
	private static final int kFundamentalDiagramWidth    = 190;
	private static final int kFundamentalDiagramHeight   = 190;
	private static final int kFundamentalDiagramPlotSize = 3;
	private static final int kScreenWidthExcess          = 10;
	private static final int kScreenHeightExcess         = 50;
	private static final int kScrollBarExtent            = 50;

	// client-windows
	private JPanel fTXDiagramPanel;
	private Image  fTXDiagramImage;
	private JPanel fVehicleAnimationPanel;
	private Image  fVehicleAnimationImage;

	// controls
	private JLabel             fNrOfVehiclesLabel;
	private JLabel             fMaxSpeedLabel;
	private JLabel             fCurrentTimeLabel;
	private JButton            fStartButton;
	private JButton            fStopButton;
	private JButton            fResetButton;
	private JCheckBox          fTXDiagramCheckBox;
	private JCheckBox          fUseColorCheckBox;
	private JCheckBox          fHighlightStoppedVehiclesCheckBox;
	private JCheckBox          fVehicleAnimationCheckBox;
	private JCheckBox          fDistributeVehiclesHomogeneouslyCheckBox;
	private JLabel             fCycleHoldTimeScrollBarLabel;
	private JLabel             fGlobalDensityScrollBarLabel;
	private JScrollBar         fCycleHoldTimeScrollBar;
	private JScrollBar         fGlobalDensityScrollBar;

	// predefined control-values
	private static final int kMaxCycleHoldTime = 1000;

	// internal datastructures
	private MultiCellTrafficCellularAutomaton fTCA;
	private boolean                           fTCARunning;
	private int                               fCycleHoldTime;
	private int                               fVehicleLength;
	private int                               fMaxSpeed;
	private double                            fCellLength;

	public MultiCellGUI(int nrOfCells, int nrOfVehicles)
	{
		// the HS-TCA's settings
		fVehicleLength             = 2;
		fMaxSpeed                  = 15;
		fCellLength                = 2.5;
		double slowdownProbability = 0.001;
		double lambda              = 1.0 / 1.3;

    // create the TCA's initial state
		MultiCellState initialState = new MultiCellState(nrOfCells);
		initialState.distributeVehicles(nrOfVehicles,fMaxSpeed,fVehicleLength,true);

		fTCA = new HelbingSchreckenbergTCA(initialState,fMaxSpeed,fCellLength,slowdownProbability,lambda);

		fTCARunning = false;
		fCycleHoldTime = 0;

		fTXDiagramWidth = kTXDiagramTimeHorizon;
		fTXDiagramHeight = fTCA.fState.fCells.length;

		setIconImage(Toolkit.getDefaultToolkit().getImage("icon.jpg"));
		setTitle("Traffic Cellular Automata + [MULTI-CELL] (Sven Maerivoet - 2002-2004)");
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

			animationPanel.add(panel);

		contentPanel.add(animationPanel);

		contentPanel.add(Box.createRigidArea(new Dimension(0,10)));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));

				JPanel simulatorPanel = new JPanel();
				simulatorPanel.setLayout(new BoxLayout(simulatorPanel,BoxLayout.X_AXIS));
				simulatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
				simulatorPanel.setAlignmentY(Component.TOP_ALIGNMENT);

					JPanel verticalPanel = new JPanel();
					verticalPanel.setLayout(new BoxLayout(verticalPanel,BoxLayout.Y_AXIS));
					verticalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					verticalPanel.setAlignmentY(Component.TOP_ALIGNMENT);

					// create the simulator-controls-panel
					JPanel simulatorControlsPanel = new JPanel();
					simulatorControlsPanel.setLayout(new BoxLayout(simulatorControlsPanel,BoxLayout.Y_AXIS));
					simulatorControlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					simulatorControlsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
					simulatorControlsPanel.setMaximumSize(new Dimension(500,200));
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
							subPanel.add(new JEtchedLine());
								fDistributeVehiclesHomogeneouslyCheckBox = new JCheckBox("Distribute vehicles homogeneously",false);
								fDistributeVehiclesHomogeneouslyCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
								fDistributeVehiclesHomogeneouslyCheckBox.setActionCommand(kDistributeHomogeneously);
								fDistributeVehiclesHomogeneouslyCheckBox.setSelected(true);
								fDistributeVehiclesHomogeneouslyCheckBox.addActionListener(this);
							subPanel.add(fDistributeVehiclesHomogeneouslyCheckBox);
						panel.add(subPanel);

							// create TCA-type-controls
							subPanel = new JPanel();
							subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.Y_AXIS));
							subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
							subPanel.setAlignmentY(Component.TOP_ALIGNMENT);
							subPanel.setBorder(new EmptyBorder(0,10,10,10));
								buttonGroup = new ButtonGroup();
									radioButton = new JRadioButton("Helbing-Schreckenberg TCA");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kHSTCA);
									radioButton.setSelected(true);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("Brake Light TCA");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kBLTCA);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
									radioButton = new JRadioButton("Kerner-Klenov-Wolf TCA");
									radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
									radioButton.setActionCommand(kSelectTCA + " " + kKKWTCA);
									radioButton.setSelected(false);
									radioButton.addActionListener(this);
								buttonGroup.add(radioButton);
							subPanel.add(radioButton);
						panel.add(subPanel);

					simulatorControlsPanel.add(panel);
				verticalPanel.add(simulatorControlsPanel);

					// create simulation's slider-controls
					JPanel simulationSettingsPanel = new JPanel();
					simulationSettingsPanel.setLayout(new BoxLayout(simulationSettingsPanel,BoxLayout.X_AXIS));
					simulationSettingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					simulationSettingsPanel.setMaximumSize(new Dimension(500,200));
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
						panel.add(fCycleHoldTimeScrollBarLabel);
						panel.add(fGlobalDensityScrollBarLabel);
					simulationSettingsPanel.add(panel);

						panel = new JPanel();
						panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
						panel.setAlignmentX(Component.LEFT_ALIGNMENT);
						panel.setBorder(new EmptyBorder(0,10,10,10));
							fCycleHoldTimeScrollBar = new JScrollBar(
								JScrollBar.HORIZONTAL,fCycleHoldTime,kScrollBarExtent,0,kMaxCycleHoldTime + kScrollBarExtent);
							fCycleHoldTimeScrollBar.setBorder(new LineBorder(Color.gray));
							fCycleHoldTimeScrollBar.addAdjustmentListener(this);
						panel.add(fCycleHoldTimeScrollBar);
							fGlobalDensityScrollBar = new JScrollBar(
								JScrollBar.HORIZONTAL,
								fTCA.fState.fNrOfVehicles,
								kScrollBarExtent,
								0,
								(int) Math.floor((double) fTCA.fState.fCells.length / (double) fVehicleLength) + kScrollBarExtent);
							fGlobalDensityScrollBar.setBorder(new LineBorder(Color.gray));
							fGlobalDensityScrollBar.addAdjustmentListener(this);
						panel.add(fGlobalDensityScrollBar);
					simulationSettingsPanel.add(panel);
				verticalPanel.add(simulationSettingsPanel);
				simulatorPanel.add(verticalPanel);

				simulatorPanel.add(Box.createRigidArea(new Dimension(10,0)));

					verticalPanel = new JPanel();
					verticalPanel.setLayout(new BoxLayout(verticalPanel,BoxLayout.Y_AXIS));
					verticalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					verticalPanel.setAlignmentY(Component.TOP_ALIGNMENT);

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
				verticalPanel.add(statisticsPanel);

					// create the clear-plots-button
					JPanel clearPlotsPanel = new JPanel();
					clearPlotsPanel.setLayout(new BoxLayout(clearPlotsPanel,BoxLayout.Y_AXIS));
					clearPlotsPanel.setBorder(new EmptyBorder(10,5,10,10));
					clearPlotsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					clearPlotsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
						button = new JButton("Clear plots and diagrams");
						button.setBackground(Color.yellow);
						button.setActionCommand(kClearPlots);
						button.addActionListener(this);
					clearPlotsPanel.add(button);
				verticalPanel.add(clearPlotsPanel);

					// create the take-screenshot-button
					JPanel takeScreenshotPanel = new JPanel();
					takeScreenshotPanel.setLayout(new BoxLayout(takeScreenshotPanel,BoxLayout.Y_AXIS));
					takeScreenshotPanel.setBorder(new EmptyBorder(10,5,10,10));
					takeScreenshotPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					takeScreenshotPanel.setAlignmentY(Component.TOP_ALIGNMENT);
						button = new JButton("Take tx-diagram snapshot");
						button.setBackground(Color.green);
						button.setActionCommand(kTakeSnapshot);
						button.addActionListener(this);
					takeScreenshotPanel.add(button);
				verticalPanel.add(takeScreenshotPanel);
				simulatorPanel.add(verticalPanel);

			lowerPanel.add(simulatorPanel);
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

		int vehicleLength = 1;

		for (int cellNr = 0; cellNr < fTCA.fState.fCells.length; cellNr += vehicleLength) {

			Cell cell = fTCA.fState.fCells[cellNr];

			vehicleLength = 1;

			if (cell != null) {

				vehicleLength = cell.fVehicleLength;

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

			g.fillRect(currentTime,(fTXDiagramHeight - 1 - cellNr) - (vehicleLength - 1),1,vehicleLength);
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

				double angleDelta = ((2.0 * Math.PI) / (double) fTCA.fState.fCells.length) * ((cellNr + cell.fVehicleLength) % fTCA.fState.fCells.length);

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

		fVehicleAnimationPanel.repaint();
	}

	private void updateStatistics()
	{
		fNrOfVehiclesLabel.setText(String.valueOf(fTCA.fState.fNrOfVehicles));

		double vehicleDensity = (((double) fTCA.fState.fNrOfVehicles) / (fTCA.fState.fCells.length * fTCA.fCellLength) * 1000.0);

		fMaxSpeedLabel.setText(String.valueOf(fTCA.fMaxSpeed * fTCA.fCellLength * 3.6) + " km/h");

		fCurrentTimeLabel.setText(String.valueOf(fTCA.fState.fTime) + " cycles");
	}

	private void setGlobalDensityLabel()
	{
		double globalDensity =
			Math.round((((double) fTCA.fState.fNrOfVehicles * (double) fVehicleLength) / (double) fTCA.fState.fCells.length) * 100.0);

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

	private void clearPlots()
	{
		fTXDiagramImage = null;
		fTXDiagramPanel.repaint();
		fVehicleAnimationImage = null;
		fVehicleAnimationPanel.repaint();
	}

	// the runnable-interface
	synchronized public void run()
	{
		while (true) {
			if (fTCARunning) {

				// advance cellular automaton one step
				fTCA.advanceOneStep();

				updateStatistics();

				// update graphical components
				updateTXDiagram();
				updateVehicleAnimation();

				// if necessary, wait some time
				if (fCycleHoldTime != 0) {
					try {
						Thread.sleep(fCycleHoldTime);
					}
					catch (InterruptedException exc) {
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

			if (tcaType.equalsIgnoreCase(" " + kHSTCA)) {

				double oldGlobalDensity =
					((double) fTCA.fState.fNrOfVehicles * (double) fVehicleLength) / (double) fTCA.fState.fCells.length;

				fVehicleLength             = 2;
				fMaxSpeed                  = 15;
				fCellLength                = 2.5;
				double slowdownProbability = 0.001;
				double lambda              = 1.0 / 1.3;

    		// create the TCA's initial state
				MultiCellState initialState = new MultiCellState(fTCA.fState.fCells.length);

				// convert the global density
				int nrOfVehicles =
					(int) Math.floor(oldGlobalDensity * ((double) fTCA.fState.fCells.length / (double) fVehicleLength));

				initialState.distributeVehicles(nrOfVehicles,fMaxSpeed,fVehicleLength,true);
				nrOfVehicles = initialState.fNrOfVehicles;

				fTCA = new HelbingSchreckenbergTCA(
					initialState,
					fMaxSpeed,
					fCellLength,
					slowdownProbability,lambda);

				fGlobalDensityScrollBar.setValues(
					fTCA.fState.fNrOfVehicles,kScrollBarExtent,
					0,(int) Math.floor((double) fTCA.fState.fCells.length / (double) fVehicleLength) + kScrollBarExtent);

				clearPlots();
			}
			else if (tcaType.equalsIgnoreCase(" " + kBLTCA)) {

				double oldGlobalDensity =
					((double) fTCA.fState.fNrOfVehicles * (double) fVehicleLength) / (double) fTCA.fState.fCells.length;

				fVehicleLength     = 5;
				fMaxSpeed          = 20;
				fCellLength        = 1.5;
				double pb          = 0.94;
				double p0          = 0.5;
				double pd          = 0.1;
				int    h           = 6;
				int    securityGap = 7;

    		// create the TCA's initial state
				MultiCellState initialState = new MultiCellState(fTCA.fState.fCells.length);

				// convert the global density
				int nrOfVehicles =
					(int) Math.floor(oldGlobalDensity * ((double) fTCA.fState.fCells.length / (double) fVehicleLength));

				initialState.distributeVehicles(nrOfVehicles,fMaxSpeed,fVehicleLength,true);
				nrOfVehicles = initialState.fNrOfVehicles;

				fTCA = new BrakeLightTCA(
					initialState,
					fMaxSpeed,
					fCellLength,
					pb,p0,pd,h,securityGap);

				fGlobalDensityScrollBar.setValues(
					fTCA.fState.fNrOfVehicles,kScrollBarExtent,
					0,(int) Math.floor((double) fTCA.fState.fCells.length / (double) fVehicleLength) + kScrollBarExtent);

				clearPlots();
			}
			else if (tcaType.equalsIgnoreCase(" " + kKKWTCA)) {

				double oldGlobalDensity =
					((double) fTCA.fState.fNrOfVehicles * (double) fVehicleLength) / (double) fTCA.fState.fCells.length;

				fVehicleLength                = 15;
				fMaxSpeed                     = 60;
				fCellLength                   = 0.5;
				double slowdownProbability    = 0.04;
				double slowToStartProbability = 0.425;
				int    a                      = 1;
				int    b                      = 1;
				int    l                      = fVehicleLength;
				int    D0                     = 60;
				double k                      = 2.55;
				double pa1                    = 0.2;
				double pa2                    = 0.052;
				double vp                     = 28;

    		// create the TCA's initial state
				MultiCellState initialState = new MultiCellState(fTCA.fState.fCells.length);

				// convert the global density
				int nrOfVehicles =
					(int) Math.floor(oldGlobalDensity * ((double) fTCA.fState.fCells.length / (double) fVehicleLength));

				initialState.distributeVehicles(nrOfVehicles,fMaxSpeed,fVehicleLength,true);
				nrOfVehicles = initialState.fNrOfVehicles;

				fTCA = new KernerKlenovWolfTCA(
					initialState,
					fMaxSpeed,
					fCellLength,
					a,b,l,D0,k,pa1,pa2,vp,slowdownProbability,slowToStartProbability);

				fGlobalDensityScrollBar.setValues(
					fTCA.fState.fNrOfVehicles,kScrollBarExtent,
					0,(int) Math.floor((double) fTCA.fState.fCells.length / (double) fVehicleLength) + kScrollBarExtent);

				clearPlots();
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
			/*
				Don't reset the TCA itself (since this would cause it to revert to its
				initial state; instead, clear the displays and set the time to zero.

				fTCA.reset();
			*/
			fTCA.fState.fTime = 0;
			clearPlots();
			fStartButton.setBackground(Color.green);
			fStopButton.setBackground(Color.lightGray);
			fResetButton.setBackground(Color.lightGray);
			fGlobalDensityScrollBar.setValue(fTCA.fState.fNrOfVehicles);
			setGlobalDensityLabel();
			updateStatistics();
		}
		else if (command.equalsIgnoreCase(kQuit)) {
			windowClosing(null);
		}
		else if (command.equalsIgnoreCase(kClearPlots)) {
			fTCA.fState.fTime = 0;
			clearPlots();
		}
		else if (command.equalsIgnoreCase(kTakeSnapshot)) {

			try {
				PngEncoder png =  new PngEncoder(fTXDiagramImage,PngEncoder.NO_ALPHA,0,1);
				byte[] pngBytes;

				String filename = "tx-diagram-";

				if (fTCA instanceof HelbingSchreckenbergTCA) {
					filename += "hstca-";
				}
				else if (fTCA instanceof BrakeLightTCA) {
					filename += "bltca-";
				}
				else if (fTCA instanceof KernerKlenovWolfTCA) {
					filename += "kkwtca-";
				}

				double globalDensity =
					((double) fTCA.fState.fNrOfVehicles * (double) fVehicleLength) / (double) fTCA.fState.fCells.length;

				filename += "k" + StringTools.convertDoubleToString(globalDensity,2) + "-large.png";

				FileOutputStream outfile = new FileOutputStream(filename);
				pngBytes = png.pngEncode();
				if (pngBytes != null) {
					outfile.write(pngBytes);
				}
			 	outfile.flush();
				outfile.close();
			}
			catch (Exception exc) {
			}
		}
		else if (command.equalsIgnoreCase(kDistributeHomogeneously)) {

			boolean wasRunning = fTCARunning;
			fTCARunning = false;

			MultiCellState state = fTCA.fState;
			int time = state.fTime;
			state.clear();

			double globalDensity =
				((double) fGlobalDensityScrollBar.getValue() * (double) fVehicleLength) / (double) fTCA.fState.fCells.length;

			int nrOfVehicles =
				(int) Math.round(globalDensity * ((double) fTCA.fState.fCells.length / (double) fVehicleLength));

			state.distributeVehicles(
				nrOfVehicles,
				fMaxSpeed,
				fVehicleLength,
				fDistributeVehiclesHomogeneouslyCheckBox.isSelected());

			state.fTime = time;
			fTCA.setState(state);
			setGlobalDensityLabel();

			fTCARunning = wasRunning;
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

			MultiCellState state = fTCA.fState;
			int time = state.fTime;
			state.clear();

			double globalDensity =
				((double) fGlobalDensityScrollBar.getValue() * (double) fVehicleLength) / (double) fTCA.fState.fCells.length;

			int nrOfVehicles =
				(int) Math.round(globalDensity * ((double) fTCA.fState.fCells.length / (double) fVehicleLength));

			state.distributeVehicles(
				nrOfVehicles,
				fMaxSpeed,
				fVehicleLength,
				fDistributeVehiclesHomogeneouslyCheckBox.isSelected());

			state.fTime = time;
			fTCA.setState(state);
			setGlobalDensityLabel();

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
			// show road
			g.setColor(Color.black);
			g.drawRect(10,0,kVehicleAnimationWidth - 1,kVehicleAnimationHeight - 1);

			if (fVehicleAnimationImage != null) {
				g.drawImage(fVehicleAnimationImage,11,1,this);
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
