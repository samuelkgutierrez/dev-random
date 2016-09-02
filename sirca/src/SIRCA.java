import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//----------------------------------------------------------------------------//
//                                                                            //
//----------------------------------------------------------------------------//
//                                                                            //
//Copyright (c) 2008 Samuel K. Gutierrez All Rights Reserved.                 //
//                                                                            //
//This program is free software; you can redistribute it and/or modify it     //
//under the terms of the GNU General Public License as published by the Free  // 
//Software Foundation; either version 2 of the License, or (at your option)   //
//any later version.                                                          //
//                                                                            //
//This program is distributed in the hope that it will be useful, but WITHOUT //
//ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or       //
//FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for   //
//more details.                                                               //
//                                                                            //
//You should have received a copy of the GNU General Public License along     //
//with this program; if not, write to the Free Software Foundation, Inc., 59  //
//Temple Place, Suite 330, Boston, MA  02111-1307  USA                        //
//                                                                            //
//----------------------------------------------------------------------------//
//                                                                            //
//----------------------------------------------------------------------------//

/**
 * @author Samuel K. Gutierrez
 * @version 0.02
 *
 */

public final class SIRCA extends JFrame implements 
	ActionListener, ChangeListener, PropertyChangeListener {
	///Serial Version UID
	private static final long serialVersionUID = 2L;
	///Title String
	private static final String TITLE = "SIRCA - University of New Mexico";
	///Grid Panel
	final static GridPanel panel =  new GridPanel();

	///speedSlider Minimum Value
	private static final int MIN_SPEED = 5;
	///speedSlider Maximum Value
	private static final int MAX_SPEED = 100;
	///speedSlider Initial Value
	private static final int INIT_SPEED = 50;
	
	///Flag Indicating if Births are to be Allowed
	private static boolean allowBirths = false;
	///Flag Indicating if Carrier Cells are to be Introduced into the SIR Model
	private static boolean allowCarriers = false;
	///Flag Indicating if the Current World Type Mode is SIR
	private static boolean sirMode = true;
	
	///Game of Life Presets
	static final String PRESETS[] = {"Clear", "Row Of Ten", "Glider", 
		"Small Exploder", "Exploder", "Tumbler"};
	
	///JCombo Box Used for the Game of Life Presets
	private static JComboBox presetsComboBox;
	
	//Control Buttons
	private static JButton startButton;
	private static JButton stopButton;
	private static JButton stepButton;
	
	///JSlider Used For Animation Speed Control
	private static JSlider speedSlider;
	
	//Menu Items
	private static JCheckBoxMenuItem birthMenuItem;
	private static JCheckBoxMenuItem carrierMenuItem;
	private static JCheckBoxMenuItem GOLMenuItem;
	private static JCheckBoxMenuItem SIRMenuItem;
	
	/**
	 * Constructs a new SIRCA frame with the specified title.
	 * @param title Frame title String.
	 */
	public SIRCA(String title) {
		super(title);
		panel.addPropertyChangeListener(this);		
	}
	
	/**
	 * Method used to add all GUI components to the specified Container.
	 * @param pane Container to which all GUI components will be added.
	 * @param a
	 * @param e
	 */
	private static void addComponentsToPane(final Container pane, //FIXME static
			ActionListener a, ChangeListener e) {
		
		final JLabel speedLabel = new JLabel("Speed", JLabel.CENTER);
		final JLabel speedLabelSlow = new JLabel("-", JLabel.CENTER);
		final JLabel speedLabelFast = new JLabel("+", JLabel.CENTER);
		speedLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		final JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		
		
		presetsComboBox = new JComboBox(PRESETS);
		presetsComboBox.addActionListener(a);
		presetsComboBox.setSelectedIndex(1);
		presetsComboBox.setEnabled(!sirMode);
	
		
		
		speedSlider = new JSlider(JSlider.HORIZONTAL,
                MIN_SPEED, MAX_SPEED, INIT_SPEED);
		
		speedSlider.addChangeListener(e);
		speedSlider.setMajorTickSpacing(10);
		speedSlider.setPaintTicks(true);
		speedSlider.setInverted(true);
				
		
		startButton = new JButton("Start");
		stepButton = new JButton("Step");
		stopButton = new JButton("Stop");
		
		startButton.addActionListener(a);
		stepButton.addActionListener(a);
		stopButton.addActionListener(a);
		
		stopButton.setEnabled(false);
		
		controlPanel.add(presetsComboBox);
		controlPanel.add(startButton);
		controlPanel.add(stepButton);
		controlPanel.add(stopButton);
		
		final JPanel out = new JPanel();
		out.setLayout(new BoxLayout(out, BoxLayout.X_AXIS));
		final JPanel speedPanel = new JPanel();
		speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.PAGE_AXIS));

		speedPanel.add(speedLabel);
		
		speedPanel.add(speedSlider);
		out.add(speedLabelSlow);
		out.add(speedPanel);
		out.add(speedLabelFast);
		controlPanel.add(out);
		pane.add(panel, BorderLayout.CENTER);
		pane.add(controlPanel, BorderLayout.SOUTH);
		
	}
	
	

	/**
	 * Adds a JMenu to the specified JFrame.
	 * @param frame
	 * @param a
	 */
	private static final void addMenuBar(JFrame frame, ActionListener a) { //FIXME
		final JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		final JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		final JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(a);
		fileMenu.add(exitMenuItem);
		
		final JMenu optionsMenu  = new JMenu("Options");
		final JMenu modeMenu = new JMenu("Mode");
		final JMenu neighborhoodMenu = new JMenu("Neighborhood Type");
		
		
		birthMenuItem = new JCheckBoxMenuItem("Allow Births");
		birthMenuItem.addActionListener(a);
		
		carrierMenuItem = new JCheckBoxMenuItem("Allow Carriers");
		carrierMenuItem.addActionListener(a);
		
		final ButtonGroup modelButtonGroup = new ButtonGroup(); //Add for the radio
		final ButtonGroup neighborSizeButtonGroup = new ButtonGroup();
		
		final JMenuItem cellProbMenuItem = new 
		JMenuItem("Population Probabilities");
		cellProbMenuItem.addActionListener(a);
		
		GOLMenuItem = new JCheckBoxMenuItem("Conway's Game Of Life");
		SIRMenuItem = new JCheckBoxMenuItem("Susceptible Infected Recovered");
		SIRMenuItem.setSelected(true);
		
		final JCheckBoxMenuItem MooreMenuItem = 
			new JCheckBoxMenuItem("Moore Neighborhood");
		final JCheckBoxMenuItem vNeumannMenuItem = 
			new JCheckBoxMenuItem("von Neumann Neighborhood");
		MooreMenuItem.setSelected(true);
		
		
		
		modelButtonGroup.add(GOLMenuItem);
		modelButtonGroup.add(SIRMenuItem);
		
		
		modeMenu.add(SIRMenuItem);
		modeMenu.add(GOLMenuItem);
		
		neighborSizeButtonGroup.add(vNeumannMenuItem);
		neighborSizeButtonGroup.add(MooreMenuItem);
		neighborhoodMenu.add(MooreMenuItem);
		neighborhoodMenu.add(vNeumannMenuItem);
		
		
		GOLMenuItem.addActionListener(a);
		
		SIRMenuItem.addActionListener(a);
		
		MooreMenuItem.addActionListener(a);
		vNeumannMenuItem.addActionListener(a);

		optionsMenu.add(modeMenu);
		optionsMenu.add(neighborhoodMenu);
		optionsMenu.addSeparator();
		optionsMenu.add(birthMenuItem);
		optionsMenu.add(carrierMenuItem);
		optionsMenu.add(cellProbMenuItem);
		
		menuBar.add(optionsMenu);
	}


	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("Exit")) {
			System.exit(0);
		}
		else if(event.getActionCommand().equals("Start")) {		
			panel.begin();
			startButton.setEnabled(!panel.isRunning());
			stopButton.setEnabled(panel.isRunning());
			stepButton.setEnabled(!panel.isRunning());
			presetsComboBox.setEnabled(!sirMode);
		}
		else if(event.getActionCommand().equals("Stop")) {
			panel.stop();
			stopButton.setEnabled(panel.isRunning());
			startButton.setEnabled(!panel.isRunning());
			stepButton.setEnabled(!panel.isRunning());
			presetsComboBox.setEnabled(!sirMode);
			
		}
		else if(event.getActionCommand().equals("Step")) {
			if(panel.isRunning()) {
				panel.stop();
				panel.evolve();
			}
			else {
				panel.evolve();				
			}
			stopButton.setEnabled(panel.isRunning());
			startButton.setEnabled(!panel.isRunning());
			stepButton.setEnabled(!panel.isRunning());
			presetsComboBox.setEnabled(!sirMode);
		}
		else if(event.getActionCommand().equals("Conway's Game Of Life")) {
			if(!sirMode) {
				return;
			}
			birthMenuItem.setEnabled(false);
			carrierMenuItem.setEnabled(false);
			panel.reset("GOL");
			loadPreset(presetsComboBox.getSelectedItem().toString());
			sirMode = false;
			presetsComboBox.setEnabled(!sirMode);
			
		}
		else if(event.getActionCommand().
				equals("Susceptible Infected Recovered")) {
			if(sirMode) {
				return;
			}
			panel.reset("SIR");
			birthMenuItem.setEnabled(true);
			carrierMenuItem.setEnabled(true);
			sirMode = true;
			presetsComboBox.setEnabled(!sirMode);
		}
		else if(event.getActionCommand().equals("von Neumann Neighborhood")) {
			panel.setNeighborhoodSize(4);
		}
		else if(event.getActionCommand().equals("Moore Neighborhood")) {
			panel.setNeighborhoodSize(8);
		}
		else if(event.getActionCommand().equals("Allow Births")) {
			allowBirths = !allowBirths;
			panel.allowBirths(allowBirths);			
		}
		else if(event.getActionCommand().equals("Allow Carriers")) {
			allowCarriers = !allowCarriers;
			panel.allowCarriers(allowCarriers);
			if(allowCarriers) { //TODO:FIXME Hack
				//emp sus inf car
				panel.changeProbabilities(10, 70, 10, 10);
			}
			else {
				panel.changeProbabilities(15, 70, 15, 0);
			}
			panel.populateSIR();
		}
		else if(event.getActionCommand().equals("Population Probabilities")) {
			JOptionPane.showMessageDialog(this, 
					"On My TODO List :-)");
			//TODO:Add Prop Changer thing...
			//new SIRCAOptionDialog(this);
			
			

		}
		else if (event.getActionCommand().equals("Grid Size")) { //TODO:FIXME
			System.out.println("Changing grid size...");
		}
		
		else if(!sirMode && ((JComboBox)event.getSource()).getSelectedItem().
				toString().equals("Glider")) {
			panel.glider();
		}
		else if(!sirMode && ((JComboBox)event.getSource()).getSelectedItem().
				toString().equals("Row Of Ten")) {
			panel.rowOfTen();
			
		}
		else if(!sirMode && ((JComboBox)event.getSource()).getSelectedItem().
				toString().equals("Small Exploder")) {
			panel.smallExploder();
		}
		else if(!sirMode && ((JComboBox)event.getSource()).getSelectedItem().
				toString().equals("Exploder")) {
			panel.exploder();
		}
		else if(!sirMode && ((JComboBox)event.getSource()).getSelectedItem().
				toString().equals("Tumbler")) {
			panel.tumbler();
		}
		else if(!sirMode && ((JComboBox)event.getSource()).getSelectedItem().
				toString().equals("Clear")) {
			panel.clear();
		}
	}
	
	/**
	 * 
	 */
	private static void createAndShowGUI() {
		SIRCA frame = new SIRCA(TITLE);
		addMenuBar(frame, frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		SIRCA.addComponentsToPane(frame.getContentPane(), frame, frame);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * 
	 * @param presetName
	 */
	private static void loadPreset(String presetName) {
		if(presetName.equals("Glider")) {
			panel.glider();
		}
		else if(presetName.equals("Row Of Ten")) {
			panel.rowOfTen();
		}
		else if(presetName.equals("Small Exploder")) {
			panel.smallExploder();
		}
		else if(presetName.equals("Exploder")) {
			panel.exploder();
		}
		else if(presetName.equals("Tumbler")) {
			panel.tumbler();
		}
		else if(presetName.equals("Clear")) {
			panel.clear();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
			int delay = (int)source.getValue();
            panel.setDelay(delay * 10);
        }	
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("STAGNANT")) {			
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
			stepButton.setEnabled(!stopButton.isEnabled());
			presetsComboBox.setEnabled(!sirMode);
			
		}
	}
	
	/*
	private static final class SIRCAOptionDialog extends JDialog 
	implements ActionListener {

		private static final long serialVersionUID = 1L;

		public SIRCAOptionDialog(JFrame ownerFrame) {
			super(ownerFrame, true);
			setTitle("Grid Options");
			JPanel gridSizePanel = new JPanel();
			gridSizePanel.setBorder(BorderFactory.createTitledBorder("Grid Dimensions"));
			//JLabel numColumns = new JLabel("Grid Dimensions: ");
			JLabel numRows = new JLabel("x");
			JFormattedTextField numColumnsTextField = new JFormattedTextField("s");
			JFormattedTextField numRowsTextField = new JFormattedTextField("f");
			numColumnsTextField.setColumns(2);
			numRowsTextField.setColumns(2);
			
			//gridSizePanel.add(numColumns);
			gridSizePanel.add(numColumnsTextField);
			
			gridSizePanel.add(numRows);
			gridSizePanel.add(numRowsTextField);
			setContentPane(gridSizePanel);
			pack();
			setResizable(false);
			setVisible(true);
		}	
	
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}		
		
	} */	

}
