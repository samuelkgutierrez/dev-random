import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;

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

public final class GridPanel extends JPanel 
implements MouseListener, Runnable {	
	///Serial Version UID
	private static final long serialVersionUID = 1L;
	
	///Grid Line Color
	private static final Color GRID_LINE_COLOR = Color.LIGHT_GRAY;
	
	//Game of Life Colors
	///Color Used To Represent a Cell that is Alive 
	private static Color aliveColor = Color.RED;
	///Color Used To Represent a Cell that is Empty
	private static Color emptyColor = Color.GRAY;
	
	//SIR Colors
	///Color Used To Represent a Cell that is Susceptible 
	private static Color susceptibleColor = Color.BLUE;
	///Color Used To Represent a Cell that is Infectious 
	private static Color infectiousColor = Color.GREEN;
	///Color Used To Represent a Cell that is Recovered
	private static Color recoveredColor = Color.GREEN;
	///Color Used To Represent an Infectious Carrier Cell 
	private static Color carrieriColor = Color.CYAN;
	///Color Used To Represent a Carrier Cell 
	private static Color carrierColor = Color.BLACK;	
	
	///The Pixel Width of a Cell
	private static final int CELL_WIDTH = 20;
	///The Pixel Height of a Cell
	private static final int CELL_HEIGHT = CELL_WIDTH;
	///Padding Utilized Between Frame Edges and Cell Grid
	private static final int BORDER_PADDING = 2 * CELL_WIDTH;
	///Padding Utilized Between Cells
	private static final int CELL_PADDING = 1;
	
	///Sleep Time, in milliseconds, Utilized By the Animation Thread 
	private static int delay;
	
	///Number of Horizontal Lines Needed to Draw World Grid
	private static int horizontalLineCount;
	///Number of Vertical Lines Needed to Draw World Grid
	private static int verticalLineCount;
	//15 70 15 - Regular Percent Settings
	///0 100 0 - GA Testing Percent Settings
	///Percent chance that an empty Cell will be chosen.	
	private static int probEmpty = 15;
	///Percent chance that an susceptible Cell will be chosen.
	private static int probSusceptible = 70;
	///Percent chance that an infectious Cell will be chosen.
	private static int probInfected = 15;
	///Percent chance that an Carrier Cell will be chosen.
	private static int PROB_CARRIER_U = 0;
	
	///World Instance Being Represented Graphically
	private static World world = new World(30, 40, World.SIR_WORLD, //FIXME:Size 
			World.MOORE, probEmpty, probSusceptible, 
			probInfected, PROB_CARRIER_U);
	
	///Animation Thread
	private static Thread animationThread = null;
	
	///Generation Label
	private static final JLabel generationLabel = new JLabel();
	
	/**
	 * Constructs a new GridPanel, that is, a graphical representation of the 
	 * underlying Cellular Automaton's state. 
	 */
	public GridPanel() {		
		super(true);
		setPreferredSize(new Dimension(world.getNumColumns() * 
				CELL_WIDTH + BORDER_PADDING, 
				world.getNumRows() * CELL_HEIGHT + BORDER_PADDING));
		delay = 500;
		
		horizontalLineCount = world.getNumRows() + 2;
		verticalLineCount = world.getNumColumns() + 2; 
		
		addMouseListener(this);
		add(generationLabel);
	}	
	
	/**
	 * Sets the sleep time (in milliseconds) utilized by the animation thread 
	 * between generation updates.
	 * @param msDelay Sleep time (in milliseconds) that is to be utilized by 
	 * the animation thread.
	 */
	public void setDelay(int msDelay) {
		delay = msDelay;
	}
	
	/**
	 * Returns the sleep time (in milliseconds) that is currently being
	 * utilized by the animation thread between generation updates.
	 * @return Sleep time (in milliseconds) that is currently being
	 * utilized by the animation thread.
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * Method utilized to create the grid structure.  This method assumes that
	 * CELL_HEIGHT and CELL_WIDTH are equal.
	 */
	private static void paintGrid(Graphics g) {
		g.setColor(GRID_LINE_COLOR);
		
		for(int i = 1; i < horizontalLineCount; ++i) {
			g.drawLine(CELL_HEIGHT, i * CELL_HEIGHT, (verticalLineCount - 1) * 
					CELL_HEIGHT, i * CELL_HEIGHT);
		}
		for(int j = 1; j < verticalLineCount; ++j) {
			g.drawLine(j * CELL_WIDTH, CELL_WIDTH, j * 
					CELL_WIDTH, (horizontalLineCount - 1) * CELL_WIDTH);
		}
	}
	
	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {		
		super.paintComponent(g);
		StringBuilder sb = new StringBuilder("Generation: ");
		sb.append(world.getNumGenerations());		
		generationLabel.setText(sb.toString());
		
		paintGrid(g);
		drawWorldState(g);
	}
	

	/**
	 * Draws the current state of all the Cells contained within the World.
	 */
	public void drawWorldState(Graphics g) {	
		for(int i = 0; i < world.getNumRows(); ++i) {
			for(int j = 0; j < world.getNumColumns(); ++j) {
				if(world.getCellState(i, j) == World.ALIVE) {
					g.setColor(aliveColor);
					g.fillRect(CELL_WIDTH + CELL_PADDING + j * CELL_WIDTH, 
							CELL_HEIGHT + CELL_PADDING + i * CELL_HEIGHT, 
							CELL_WIDTH - CELL_PADDING, 
							CELL_HEIGHT - CELL_PADDING);
				}				
				else if(world.getCellState(i, j) == World.EMPTY) {
					g.setColor(emptyColor);
					g.fillRect(CELL_WIDTH + CELL_PADDING + j * CELL_WIDTH, 
							CELL_HEIGHT + CELL_PADDING + i * CELL_HEIGHT, 
							CELL_WIDTH - CELL_PADDING, 
							CELL_HEIGHT - CELL_PADDING);
				}	
				else if(world.getCellState(i, j) == World.SUSCEPTIBLE) {
					g.setColor(susceptibleColor);
					g.fillRect(CELL_WIDTH + CELL_PADDING + j * CELL_WIDTH, 
							CELL_HEIGHT + CELL_PADDING + i * CELL_HEIGHT, 
							CELL_WIDTH - CELL_PADDING, 
							CELL_HEIGHT - CELL_PADDING);
				}
				else if(world.getCellState(i, j) == World.INFECTED) {
					g.setColor(infectiousColor);
					g.fillRect(CELL_WIDTH + CELL_PADDING + j * CELL_WIDTH, 
							CELL_HEIGHT + CELL_PADDING + i * CELL_HEIGHT, 
							CELL_WIDTH - CELL_PADDING, 
							CELL_HEIGHT - CELL_PADDING);
				}
				else if(world.getCellState(i, j) == World.RECOVERED) {
					g.setColor(recoveredColor);
					g.fillRect(CELL_WIDTH + CELL_PADDING + j * CELL_WIDTH, 
							CELL_HEIGHT + CELL_PADDING + i * CELL_HEIGHT, 
							CELL_WIDTH - CELL_PADDING, 
							CELL_HEIGHT - CELL_PADDING);
				}
				else if(world.getCellState(i, j) == World.CARRIER_I) {
					g.setColor(carrieriColor);
					g.fillRect(CELL_WIDTH + CELL_PADDING + j * CELL_WIDTH, 
							CELL_HEIGHT + CELL_PADDING + i * CELL_HEIGHT, 
							CELL_WIDTH - CELL_PADDING, 
							CELL_HEIGHT - CELL_PADDING);
				}
				else if(world.getCellState(i, j) == World.CARRIER_U) {
					g.setColor(carrierColor);
					g.fillRect(CELL_WIDTH + CELL_PADDING + j * CELL_WIDTH, 
							CELL_HEIGHT + CELL_PADDING + i * CELL_HEIGHT, 
							CELL_WIDTH - CELL_PADDING, 
							CELL_HEIGHT - CELL_PADDING);
				}
			}
		}
	}
	
	/**
	 * Moves World evolution forward.  Panel is repainted only if the world has 
	 * not reached equilibrium.
	 */
	public final void evolve() {
		world.evolve();
		if(world.isStagnant()) {
			stop();
			firePropertyChange("STAGNANT", false, true);			
			return;
		}
		repaint();
	}	
	
	/**
	 * Maps the cursor's position to a corresponding coordinate pair within the 
	 * grid of Cells. A Cell's state is only toggled if the World is currently 
	 * in the Game of Life mode.
	 */
	private void map(MouseEvent event) {
		int mappedX = event.getX() / CELL_WIDTH - 1;
		int mappedY = event.getY() / CELL_HEIGHT - 1;
		if(mappedX < 0 || mappedX > world.getNumColumns() - 1 || 
				mappedY < 0 || mappedY > world.getNumRows() - 1) {
			return;
		}
		toggleCellState(mappedY, mappedX);
		repaint();
	}

	/**
	 * Toggles the state of the Cell located at the provided row and column.
	 * @param row Row index (Base zero) of the Cell whose state is to be 
	 * toggled.
	 * @param column Column index (Base zero) of the Cell whose state is to be 
	 * toggled.
	 */
	private void toggleCellState(int row, int column) {
		if(world.getCellState(row, column) == World.ALIVE) {
			world.setCellState(row, column, World.DEAD);
			return;
		}
		world.setCellState(row, column, World.ALIVE);		
	}
	
	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent mouseEvent) {
		if(mouseEvent.getButton() == 1 && world.getWorldType().
				equals(World.GOL_WORLD)) { //Left Click
			map(mouseEvent);			
		}
		else if (mouseEvent.getButton() == 3) { //Right Click
			reset(world.getWorldType());
		}
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased
	 * (java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(animationThread != null) {			
			evolve();
			try {
				Thread.sleep(delay);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();				
			}
		}		
	}
	
	/**
	 * Stops the animation thread.
	 */
	public void stop() {
		animationThread = null;
	}
	
	/**
	 * Starts the animation thread.
	 */
	public synchronized void begin() {
		if (animationThread == null) {
			animationThread = new Thread(this);
			animationThread.start();			
		}
		else return;
	}
	
	/**
	 * Returns whether the animation thread is running.
	 * @return Whether the animation thread is running.
	 */
	public boolean isRunning() {
		return animationThread != null;
	}
	
	/**
	 * Resets the world's state.
	 * Repaints this component to reflect any changes that may have occurred.
	 * @param worldType String specifying World type.
	 */
	public void reset(String worldType) {
		world.setWorldType(worldType);
		
		if(worldType.equals(World.GOL_WORLD)) {
			world.reset(World.DEAD);
		}
		else if(worldType.equals(World.SIR_WORLD)) {
			world.reset();
			world.populateSIR();
		}
		repaint();
	}
	
	/**
	 * @see World.setNeighborhoodSize(int newSize) 
	 */
	public void setNeighborhoodSize(int newSize) {
		world.setNeighborhoodSize(newSize);
	}
	
	/**
	 * Populates the SIR model and repaints to reflect changes.
	 * Repaints this component to reflect any changes that may have occurred.
	 */
	public void populateSIR() {
		world.populateSIR();
		repaint();
	}	
	
	/**
	 * @see World.reset(char state).
	 * Repaints this component to reflect any changes that may have occurred. 
	 */
	public void clear() {
		reset(World.DEAD);
		repaint();
	}	
	
	/**
	 * Populates the world according to the Game of Life Glider preset.
	 * Repaints this component to reflect any changes that may have occurred. 
	 */
	public void glider() {
		int centerRow = world.getNumRows() / 2;
		int centerColumn = world.getNumColumns() / 2;
		reset(World.DEAD);
		world.setCellState(centerRow + 1, centerColumn - 1, 'A');
		world.setCellState(centerRow + 1, centerColumn,     'A');
		world.setCellState(centerRow + 1, centerColumn + 1, 'A');
		world.setCellState(centerRow,     centerColumn + 1, 'A');
		world.setCellState(centerRow - 1, centerColumn,     'A');
		repaint();
	}
	
	
	/**
	 * Populates the world according to the Game of Life Row of Ten preset.
	 * Repaints this component to reflect any changes that may have occurred. 
	 */
	public void rowOfTen() {
		int centerRow = world.getNumRows() / 2;
		int centerColumn = world.getNumColumns() / 2;
		reset(World.DEAD);
		world.setCellState(centerRow, centerColumn - 5, 'A');
		world.setCellState(centerRow, centerColumn - 4, 'A');
		world.setCellState(centerRow, centerColumn - 3, 'A');
		world.setCellState(centerRow, centerColumn - 2, 'A');
		world.setCellState(centerRow, centerColumn - 1, 'A');
		world.setCellState(centerRow, centerColumn,     'A');
		world.setCellState(centerRow, centerColumn + 1, 'A');
		world.setCellState(centerRow, centerColumn + 2, 'A');
		world.setCellState(centerRow, centerColumn + 3, 'A');
		world.setCellState(centerRow, centerColumn + 4, 'A');
		repaint();
	}
	
	/**
	 * Populates the world according to the Game of Life Small Exploder preset.
	 * Repaints this component to reflect any changes that may have occurred. 
	 */
	public void smallExploder() {
		int centerRow = world.getNumRows() / 2;
		int centerColumn = world.getNumColumns() / 2;
		reset(World.DEAD);
		world.setCellState(centerRow - 2, centerColumn    , 'A');		
		world.setCellState(centerRow - 1, centerColumn - 1, 'A');
		world.setCellState(centerRow - 1, centerColumn    , 'A');
		world.setCellState(centerRow - 1, centerColumn + 1, 'A');		
		world.setCellState(centerRow, centerColumn - 1,     'A');
		world.setCellState(centerRow, centerColumn + 1,     'A');		
		world.setCellState(centerRow + 1, centerColumn    , 'A');
		repaint();		
	}
	
	/**
	 * Populates the world according to the Game of Life Exploder preset.
	 * Repaints this component to reflect any changes that may have occurred. 
	 */
	public void exploder() {
		int centerRow = world.getNumRows() / 2;
		int centerColumn = world.getNumColumns() / 2;
		reset(World.DEAD);		
		world.setCellState(centerRow - 2, centerColumn - 2, 'A');
		world.setCellState(centerRow - 2, centerColumn    , 'A');
		world.setCellState(centerRow - 1, centerColumn - 2, 'A');
		world.setCellState(centerRow - 2, centerColumn + 2, 'A');
		world.setCellState(centerRow - 1, centerColumn + 2, 'A');		
		world.setCellState(centerRow, centerColumn - 2,     'A');
		world.setCellState(centerRow, centerColumn + 2,     'A');		
		world.setCellState(centerRow + 1, centerColumn + 2, 'A');
		world.setCellState(centerRow + 2, centerColumn + 2, 'A');
		world.setCellState(centerRow + 2, centerColumn    , 'A');
		world.setCellState(centerRow + 1, centerColumn - 2, 'A');
		world.setCellState(centerRow + 2, centerColumn - 2, 'A');		
		repaint();		
	}
	
	/**
	 * Populates the world according to the Game of Life Tumbler preset.
	 * Repaints this component to reflect any changes that may have occurred. 
	 */
	public void tumbler() {
		int centerRow = world.getNumRows() / 2;
		int centerColumn = world.getNumColumns() / 2;
		reset(World.DEAD);		
		world.setCellState(centerRow - 2, centerColumn - 1,     'A');
		world.setCellState(centerRow - 1, centerColumn - 1,     'A');
		world.setCellState(centerRow,     centerColumn - 1,     'A');		
		world.setCellState(centerRow + 1, centerColumn - 1,     'A');
		world.setCellState(centerRow + 2, centerColumn - 1,     'A');		
		world.setCellState(centerRow - 1, centerColumn - 2,     'A');
		world.setCellState(centerRow - 2, centerColumn - 2,     'A');		
		world.setCellState(centerRow + 3, centerColumn + 2,     'A');
		world.setCellState(centerRow + 3, centerColumn + 3,     'A');		
		world.setCellState(centerRow + 1, centerColumn + 3,     'A');
		world.setCellState(centerRow + 2, centerColumn + 3,     'A');		
		world.setCellState(centerRow - 2, centerColumn + 1,     'A');
		world.setCellState(centerRow - 1, centerColumn + 1,     'A');
		world.setCellState(centerRow,     centerColumn + 1,     'A');		
		world.setCellState(centerRow + 1, centerColumn + 1,     'A');
		world.setCellState(centerRow + 2, centerColumn + 1,     'A');		
		world.setCellState(centerRow - 1, centerColumn + 2,     'A');
		world.setCellState(centerRow - 2, centerColumn + 2,     'A');		
		world.setCellState(centerRow + 3, centerColumn - 2,     'A');
		world.setCellState(centerRow + 3, centerColumn - 3,     'A');		
		world.setCellState(centerRow + 1, centerColumn - 3,     'A');
		world.setCellState(centerRow + 2, centerColumn - 3,     'A');		
		repaint();
	}
	

	/**
	 * @see World.reset() 
	 */
	public void reset() {
		world.reset();
	}
	
	/**
	 * @see World.reset(char state)
	 */
	public void reset(char state) {
		world.reset(state);
	}
	
	/**
	 * @see World.allowBirths(boolean allow)
	 */
	public void allowBirths(boolean allow) {
		world.allowBirths(allow);
	}
	
	/**
	 * @see World.allowCarriers(boolean allow)
	 */
	public void allowCarriers(boolean allow) {
		world.allowCarriers(allow);
	}
	
	/**
	 * @see World.changeProbabilities()
	 */
	public void changeProbabilities(int emptyPercent, int susPercent, 
			int infPercent, int carrierPercent) {
		world.changeProbabilities(emptyPercent, susPercent, 
				infPercent, carrierPercent);
	}
}
