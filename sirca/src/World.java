import java.util.Random;

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
 * @version 0.05
 *
 */

public final class World {
    ///von Neumann Neighborhood Size
    public static final int VON_NEUMANN = 4;
    ///Moore Neighborhood Size
    public static final int MOORE = 8;

    //Game of Live (GOL) State Constants
    public static final char DEAD = 'D';
    public static final char ALIVE = 'A';

    //Susceptible Infectious Recovered (SIR) State Constants
    public static final char EMPTY = 'E';
    public static final char SUSCEPTIBLE = 'S';
    public static final char INFECTED = 'I';
    public static final char RECOVERED = 'R';
    ///Carrier Uninfected
    public static final char CARRIER_U = 'C';
    ///Carrier Infected
    public static final char CARRIER_I = 'X';

    //Supported World Types
    public static final String SIR_WORLD = "SIR";
    public static final String GOL_WORLD = "GOL";

    ///Time Steps Utilized Before Carrier Cell Are Eligible for Recovery
    private static final int CARRIER_TSBE = 50;

    ///Number of Cell Columns
    private int columns;
    ///Number of Cell Rows
    private int rows;
    ///World 2d Matrix
    private final Cell world[][];
    ///Number of New Infections Produced
    private int numInfections = 0; ///TODO: Add to evolve()
    ///Flag Indicating if World Has Reached Equilibrium
    private boolean stagnant = false;
    ///Number of Generations Passed
    private int age = 0;
    ///Current Neighborhood Size Being Utilized
    private int neighborhoodSize;
    ///String Representing Current World Type, Either: "SIR" or "GOL"
    private String worldType = null;
    ///Flag Indicating if Births Are Considered in Evolution Algorithm
    private boolean allowBirths = false;
    ///Flag Indicating if Carriers Are Considered in Evolution Algorithm
    private boolean allowCarriers = false;
    ///Random Number Generator Utilized In Weighted Cell State Probability
    private static final Random rng = new Random();
    ///"Buckets" Used For Cell State Probability
    private final char buckets[] = new char[100];
    ///Use changeProbabilities() To Populate
    private int probabilityMap[] = {0, 0, 0, 0};
    ///Valid Cell States
    private static final char states[] = {EMPTY, SUSCEPTIBLE, INFECTED,
        CARRIER_U};

    /**
     * Constructs a new World.
     * @param r Number of cell rows that the world will contain.
     * @param c Number of cell columns that the world will contain.
     * @param wType World Type (Supported Types: "SIR" or "GOL").
     * @param nSize Initial neighborhood size
     * (Supported Types: VON_NEUMANN, MOORE).
     * @param emptyPercent Initial empty Cell percentage.
     * @param susPercent Initial susceptible Cell percentage.
     * @param infPercent Initial infected Cell percentage.
     * @param carrierPercent Initial Carrier Cell percentage.
     */
    public World(int r, int c, String wType, int nSize,
            int emptyPercent, int susPercent,
            int infPercent, int carrierPercent) {
        neighborhoodSize = nSize;
        worldType = wType;
        columns = c;
        rows = r;
        world = new Cell[rows][columns];

        changeProbabilities(emptyPercent, susPercent, infPercent,
                carrierPercent);

        if(wType.equals(GOL_WORLD)) {
            for(int i = 0; i < rows; ++i) {
                for(int j = 0; j < columns; ++j) {
                    world[i][j] = new Cell(DEAD);
                }
            }
        }
        else if(wType.equals(SIR_WORLD)) {
            for(int i = 0; i < rows; ++i) {
                for(int j = 0; j < columns; ++j) {
                    world[i][j] = new Cell(EMPTY);
                }
            }
            populateSIR();
        }
        else {
            System.err.println("World::Error While Initializing World Type");
            System.exit(1);
        }
    }

    /**
     * Changes the world state based upon evolution rules.
     */
    public void evolve() {
        //Evolve based upon Conway's Game of Life rules.
        if(worldType.equals(GOL_WORLD)) {
            for(int i = 0; i < rows; ++i) {
                for(int j = 0; j < columns; ++j) {
                    if(getCellState(i, j) == ALIVE) {
                        if(getNeighborCount(ALIVE, i, j) == 0 ||
                                getNeighborCount(ALIVE, i, j) == 1) {
                            world[i][j].setFutureState(DEAD);
                        }
                        else if(getNeighborCount(ALIVE, i, j) >= 4) {
                            world[i][j].setFutureState(DEAD);
                        }
                        else if(getNeighborCount(ALIVE, i, j) == 2 ||
                                getNeighborCount(ALIVE, i, j) == 3) {
                            world[i][j].setFutureState(ALIVE);
                        }
                    }
                    else if(getCellState(i, j) == DEAD) {
                        if(getNeighborCount(ALIVE, i, j) == 3) {
                            world[i][j].setFutureState(ALIVE);
                        }
                        else {
                            world[i][j].setFutureState(DEAD);
                        }
                    }
                }
            }
            stagnant = stagnant();
            updateWorld();
            if(!stagnant) {
                ++age;
            }
        }
        //Evolve based upon Susceptible Infectious Recovered rules.
        else if(worldType.equals(SIR_WORLD)) {
            for(int i = 0; i < rows; ++i) {
                for(int j = 0; j < columns; ++j) {
                    if(allowCarriers) { //Carrier Cells will be considered...
                        if(getCellState(i, j) == EMPTY) {
                            if(allowBirths &&
                                    getNeighborCount(EMPTY, i, j) >= 2) {
                                world[i][j].setFutureState(SUSCEPTIBLE);
                                world[i][j].setFutureAge(0);

                            }
                            else {
                                world[i][j].setFutureState(EMPTY);
                            }
                        }
                        else if(getCellState(i, j) == SUSCEPTIBLE) {
                            if((this.getNeighborCount(INFECTED, i, j) +
                                    getNeighborCount(CARRIER_I, i, j)) >= 2) {
                                world[i][j].setFutureState(INFECTED);
                                ++numInfections; //TODO:Look at me
                                world[i][j].fage();
                            }
                            else {
                                world[i][j].setFutureState(SUSCEPTIBLE);
                                world[i][j].fage();
                            }
                        }
                        else if(getCellState(i, j) == INFECTED) {
                            if((getNeighborCount(INFECTED, i, j) +
                                    getNeighborCount(CARRIER_I, i, j)) <= 4 ) {
                                world[i][j].setFutureState(RECOVERED);
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(INFECTED);
                                world[i][j].fage();
                            }
                        }
                        else if(getCellState(i, j) == RECOVERED) {
                            if(allowBirths &&
                                    getNeighborCount(RECOVERED, i, j) >= 3) {
                                world[i][j].setFutureState(EMPTY);
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(RECOVERED);
                                world[i][j].setFutureAge(0);
                            }
                        }
                        else if(getCellState(i, j) == CARRIER_U) {
                            if( (getNeighborCount(INFECTED, i, j) +
                                    getNeighborCount(CARRIER_I, i, j) >=2)) {
                                world[i][j].setFutureState(CARRIER_I);
                                world[i][j].fage();
                            }
                            else {
                                world[i][j].setFutureState(CARRIER_U);
                                world[i][j].fage();
                            }
                        }
                        else if(getCellState(i, j) == CARRIER_I) {
                            if((getNeighborCount(INFECTED, i, j) +
                                 getNeighborCount(CARRIER_I, i, j)) <= 4 &&
                                 world[i][j].getAge() >= CARRIER_TSBE) {
                                world[i][j].setFutureState(RECOVERED);
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(CARRIER_I);
                                world[i][j].fage();
                            }
                        }
                    }
                    else if(!allowCarriers) { //Simple SIR (No Carrier Cells)
                        if(getCellState(i, j) == EMPTY) {
                            if(allowBirths &&
                                    getNeighborCount(EMPTY, i, j) >= 2) {
                                world[i][j].setFutureState(SUSCEPTIBLE);
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(EMPTY);
                                world[i][j].setFutureAge(0);
                            }
                        }
                        else if(getCellState(i, j) == SUSCEPTIBLE) {
                            if(getNeighborCount(INFECTED, i, j) >= 2) {
                                world[i][j].setFutureState(INFECTED);
                                ++numInfections; //TODO:Look at me...Not being reset properly
                                world[i][j].fage();
                            }
                            else {
                                world[i][j].setFutureState(SUSCEPTIBLE);
                                world[i][j].fage();
                            }
                        }
                        else if(getCellState(i, j) == INFECTED) {
                            if(getNeighborCount(INFECTED, i, j) <= 4) {
                                world[i][j].setFutureState(RECOVERED);
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(INFECTED);
                                world[i][j].fage();
                            }
                        }
                        else if(getCellState(i, j) == RECOVERED) {
                            if(allowBirths &&
                                    getNeighborCount(RECOVERED, i, j) >= 3) {
                                world[i][j].setFutureState(EMPTY);
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(RECOVERED);
                                world[i][j].setFutureAge(0);
                            }
                        }
                    }
                }
            }
            stagnant = stagnant();
            updateWorld();
            if(!stagnant) {
                ++age;
            }
        }
        else {
            System.err.println("World::evolve - World Type Error");
            System.exit(1);
        }
    }

    /**
     * Changes the world state based upon the provided evolution rule string.
     * Parsing Breakdown:
     * 111 111 111 111
     *  A   B   C   D
     */
    public void evolve(String ruleString) {
        if(worldType.equals(SIR_WORLD)) {
            for(int i = 0; i < rows; ++i) {
                for(int j = 0; j < columns; ++j) {
                    if(allowCarriers) { //Carrier Cells will be considered...
                        if(getCellState(i, j) == EMPTY) {
                            if(allowBirths &&
                                    getNeighborCount(EMPTY, i, j) >= 2) {
                                world[i][j].setFutureState(SUSCEPTIBLE);
                                world[i][j].setFutureAge(0);

                            }
                            else {
                                world[i][j].setFutureState(EMPTY);
                            }
                        }
                        else if(getCellState(i, j) == SUSCEPTIBLE) {
                            if((this.getNeighborCount(INFECTED, i, j) +
                                    getNeighborCount(CARRIER_I, i, j)) >= 2) {
                                world[i][j].setFutureState(INFECTED);
                                world[i][j].fage();
                            }
                            else {
                                world[i][j].setFutureState(SUSCEPTIBLE);
                                world[i][j].fage();
                            }
                        }
                        else if(getCellState(i, j) == INFECTED) {
                            if((this.getNeighborCount(INFECTED, i, j) +
                                    getNeighborCount(CARRIER_I, i, j)) <= 4 ) {
                                world[i][j].setFutureState(RECOVERED);
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(INFECTED);
                                world[i][j].fage();
                            }
                        }
                        else if(getCellState(i, j) == RECOVERED) {
                            if(allowBirths &&
                                    getNeighborCount(RECOVERED, i, j) >= 3) {
                                world[i][j].setFutureState(EMPTY);
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(RECOVERED);
                                world[i][j].setFutureAge(0);
                            }
                        }
                        else if(getCellState(i, j) == CARRIER_U) {
                            if( (getNeighborCount(INFECTED, i, j) +
                                    getNeighborCount(CARRIER_I, i, j) >= 2)) {
                                world[i][j].setFutureState(CARRIER_I);
                                world[i][j].fage();
                            }
                            else {
                                world[i][j].setFutureState(CARRIER_U);
                                world[i][j].fage();
                            }
                        }
                        else if(getCellState(i, j) == CARRIER_I) {
                            if((getNeighborCount(INFECTED, i, j) +
                                 getNeighborCount(CARRIER_I, i, j)) <= 4 &&
                                 world[i][j].getAge() >= CARRIER_TSBE) {
                                world[i][j].setFutureState(RECOVERED);
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(CARRIER_I);
                                world[i][j].fage();
                            }
                        }
                    }
                    else if(!allowCarriers) { //Simple SIR (No Carrier Cells)
                        //TODO:Note why I'm inc grayDecode...
                        if(getCellState(i, j) == EMPTY) { //State 0
                            if(allowBirths &&
                                    getNeighborCount(EMPTY, i, j) >=
                                        GeneticAlgorithm.grayDecode(
                                                ruleString.substring(0, 3) + 1)) {
                                world[i][j].setFutureState(SUSCEPTIBLE); // S1
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(EMPTY);
                                world[i][j].setFutureAge(0);
                            }
                        }
                        else if(getCellState(i, j) == SUSCEPTIBLE) { //State 1
                            if(getNeighborCount(INFECTED, i, j) >= //S2
                                GeneticAlgorithm.grayDecode(
                                        ruleString.substring(3, 6) + 1)) {
                                world[i][j].setFutureState(INFECTED);
                                ++numInfections;
                                world[i][j].fage();
                            }
                            else {
                                world[i][j].setFutureState(SUSCEPTIBLE);
                                world[i][j].fage();
                            }
                        }
                        else if(getCellState(i, j) == INFECTED) { //State 2
                            if(getNeighborCount(INFECTED, i, j) <=
                                GeneticAlgorithm.grayDecode(
                                        ruleString.substring(6, 9) + 1)) {
                                world[i][j].setFutureState(RECOVERED); //S3
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(INFECTED);
                                world[i][j].fage();
                            }
                        }
                        else if(getCellState(i, j) == RECOVERED) { //State 3
                            if(allowBirths &&
                                    getNeighborCount(RECOVERED, i, j) >=
                                        GeneticAlgorithm.grayDecode(
                                                ruleString.substring(9, 12) + 1)) {
                                world[i][j].setFutureState(EMPTY); //S0
                                world[i][j].setFutureAge(0);
                            }
                            else {
                                world[i][j].setFutureState(RECOVERED);
                                world[i][j].setFutureAge(0);
                            }
                        }
                    }
                }
            }
            stagnant = stagnant();
            updateWorld();
            if(!stagnant) {
                ++age;
            }

            else{
                //System.out.println(this.numInfections);
            }

        }
        else {
            System.err.println("World::evolve - World Type Error");
            System.exit(1);
        }
    }


    /**
     * Sets whether Cell births will be allowed in the SIR model.
     * @param allow Indicates whether Cell births will be allowed in
     * the SIR model.
     */
    public void allowBirths(boolean allow) {
        allowBirths = allow;
    }

    /**
     * Returns the current number of generations that have passed.
     * @return Current number of generations that have passed.
     */
    public int getNumGenerations() {
        return age;
    }

    /**
     * Returns the number of Cell columns being utilized.
     * @return Number of Cell columns being utilized.
     */
    public final int getNumColumns() {
        return columns;
    }

    /**
     * Returns the number of Cell rows being utilized.
     * @return Number of Cell rows being utilized.
     */
    public int getNumRows() {
        return rows;
    }

    /**
     * @return
     */
    public int getNewInfectionCount() {
        return numInfections;
    }

    /**
     * Sets the current state of the Cell located at the specified row and
     * column to the provided state.
     * @param row  Row index (Base zero) of the Cell whose current state is to
     * be changed.
     * @param column Column index (Base zero) of the Cell whose current state
     * is to be changed.
     * @param newState The state value that is to be utilized.
     */
    public void setCellState(int row, int column, char newState) {
        world[row][column].setState(newState);
    }

    /**
     * Returns whether the world has reached equilibrium.
     * @return true if the world has reached equilibrium.  Returns false
     * otherwise.
     */
    public boolean isStagnant() {
        return stagnant;
    }

    /**
     * Updates World state.  In particular, updates all Cells in the World to
     * reflect their new state and age.
     */
    private void updateWorld() {
        for(int i = 0; i < rows; ++i) {
            for(int j = 0; j < columns; ++j) {
                world[i][j].update();
            }
        }
    }

    /**
     * Sets the World type to the specified String.  Supported World type
     * Options: SIR, GOL.
     * @param wt String specifying World type.
     */
    public final void setWorldType(String wt) {
        worldType = wt;
    }

    /**
     * Populates world based upon Cell probabilities.
     * @see changeProbabilities
     */
    public final void populateSIR() {
        for(int i = 0; i < rows; ++i) {
            for(int j = 0; j < columns; ++j) {
                world[i][j].setState(buckets[rng.nextInt(100)]);
            }
        }
        //FIXME:Remove...used for GA testing.  Also take a look at the probs.
        //world[10][9].setState(World.INFECTED);
        //world[10][10].setState(World.INFECTED);
        //world[10][11].setState(World.INFECTED);
    }

    /**
     * Changes the Cell population probabilities used when populating SIR model.
     * @param emptyPercent Percent chance that an empty Cell will be chosen when
     * populating the SIR model.
     * @param susPercent Percent chance that a susceptible Cell will be chosen
     * when populating the SIR model.
     * @param infPercent Percent chance that an infectious Cell will be chosen
     * when populating the SIR model.
     * @param carrierPercent Percent chance that a Carrier (uninfected) Cell
     * will be chosen when populating the SIR model.
     *TODO: Verify that provided values sum to 100.
     */
    public final void changeProbabilities(int emptyPercent, int susPercent,
            int infPercent, int carrierPercent) {
        probabilityMap[0] = emptyPercent;
        probabilityMap[1] = susPercent;
        probabilityMap[2] = infPercent;
        probabilityMap[3] = carrierPercent;
        int index = 0;
        for(int i = 0; i < probabilityMap.length; ++i) {
            for(int k = 0; k < probabilityMap[i]; ++k) {
                buckets[index] = states[i];
                ++index;
            }
        }
    }

    /**
     * Returns whether the world has reached equilibrium.  Does not update
     * stagnant flag before returning.
     * @return true if the world has reached equilibrium.  Returns false
     * otherwise.
     */
    private final boolean stagnant() {
        for(int i = 0; i < rows; ++i) {
            for(int j = 0; j < columns; ++j) {
                if(world[i][j].getState() != world[i][j].getFutureState()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets the neighborhood size that is to be utilized by the World's
     * evolution algorithm.  Two neighborhood types are currently supported:
     * von Neumann and Moore.
     * @param newSize New neighborhood size that is to be utilized by the
     * World's evolution algorithm.
     */
    public void setNeighborhoodSize(int newSize) {
        neighborhoodSize = newSize;
    }

    /**
     * Returns the number of neighbors being acknowledged by the World's
     * evolution algorithm.
     * @return Number of neighbors being acknowledged by the World's
     * evolution algorithm.
     */
    public int getNeighborhoodSize() {
        return neighborhoodSize;
    }

    /**
     * Overrides toString.
     * Returns a String that contains state information of all the Cells
     * contained within the World.  This method relies on the Cell's toString
     * formatting to get relevant Cell state information.
     * @return String containing World state information.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder worldString =  new StringBuilder();
        for(int i = 0; i < rows; ++i) {
            for(int j = 0; j < columns; ++j) {
                worldString.append(world[i][j]);
            }
            worldString.append("\n");
        }
        worldString.append("\n");
        return worldString.toString();
    }


    /**
     * Returns the neighbor's state northeast of the Cell located at the
     * specified row and column.
     * row column.
     * @param row  Row index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @param column Column index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @return The Cell's northeastern neighbor's state.  If the neighbor lies
     * outside valid bounds, the value of UNDEFINED is returned.
     */
    private char getCNENState(int row, int column) {
        if(row == 0 || column == columns - 1) {
            return Cell.UNDEFINED;
        }
        else {
            return getCellState(row - 1, column + 1);
        }
    }

    /**
     * Returns the neighbor's state north of the Cell located at the
     * specified row and column.
     * row column.
     * @param row  Row index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @param column Column index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @return The Cell's northern neighbor's state.  If the neighbor lies
     * outside valid bounds, the value of UNDEFINED is returned.
     */
    private char getCNNState(int row, int column) {
        if(row == 0) {
            return Cell.UNDEFINED;
        }
        else {
            return getCellState(row - 1, column);
        }
    }

    /**
     * Returns the neighbor's state east of the Cell located at the
     * specified row and column.
     * row column.
     * @param row  Row index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @param column Column index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @return The Cell's eastern neighbor's state.  If the neighbor lies
     * outside valid bounds, the value of UNDEFINED is returned.
     */
    private char getCENState(int row, int column) {
        if(column == columns - 1) {
            return Cell.UNDEFINED;
        }
        else {
            return getCellState(row, column + 1);
        }
    }

    /**
     * Returns the neighbor's state southeast of the Cell located at the
     * specified row and column.
     * row column.
     * @param row  Row index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @param column Column index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @return The Cell's southeastern neighbor's state.  If the neighbor lies
     * outside valid bounds, the value of UNDEFINED is returned.
     */
    private char getCSENState(int row, int column) {
        if(row == rows - 1 || column == columns - 1) {
            return Cell.UNDEFINED;
        }
        else {
            return getCellState(row + 1, column + 1);
        }
    }

    /**
     * Returns the neighbor's state south of the Cell located at the
     * specified row and column.
     * row column.
     * @param row  Row index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @param column Column index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @return The Cell's southern neighbor's state.  If the neighbor lies
     * outside valid bounds, the value of UNDEFINED is returned.
     */
    private char getCSNState(int row, int column) {
        if(row == rows - 1) {
            return Cell.UNDEFINED;
        }
        else {
            return getCellState(row + 1, column);
        }
    }

    /**
     * Returns the neighbor's state southwest of the Cell located at the
     * specified row and column.
     * row column.
     * @param row  Row index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @param column Column index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @return The Cell's southwestern neighbor's state.  If the neighbor lies
     * outside valid bounds, the value of UNDEFINED is returned.
     */
    private char getCSWNState(int row, int column) {
        if(row == rows - 1 || column == 0) {
            return Cell.UNDEFINED;
        }
        else {
            return getCellState(row + 1, column - 1);
        }
    }

    /**
     * Returns the neighbor's state west of the Cell located at the
     * specified row and column.
     * row column.
     * @param row  Row index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @param column Column index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @return The Cell's western neighbor's state.  If the neighbor lies
     * outside valid bounds, the value of UNDEFINED is returned.
     */
    private char getCWNState(int row, int column) {
        if(column == 0) {
            return Cell.UNDEFINED;
        }
        else {
            return getCellState(row, column - 1);
        }
    }

    /**
     * Returns the neighbor's state northwest of the Cell located at the
     * specified row and column.
     * row column.
     * @param row  Row index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @param column Column index (Base zero) of the Cell whose neighbor is
     * being queried.
     * @return The Cell's northwestern neighbor's state.  If the neighbor lies
     * outside valid bounds, the value of UNDEFINED is returned.
     */
    private char getCNWNState(int row, int column) {
        if(row == 0 || column == 0) {
            return Cell.UNDEFINED;
        }
        else {
            return getCellState(row - 1, column - 1);
        }
    }

    /**
     * Returns the number of neighbors (in the specified state) of the
     * Cell located at the specified row and column.  The return value takes
     * neighborhood size into consideration.
     * @param state  Cell state that count takes into consideration.
     * @param row Row index (Base zero) of the Cell whose neighbors are
     * being counted.
     * @param column Column index (Base zero) of the Cell whose neighbors are
     * being counted.
     * @return Neighbor count of the Cell located at the specified row and
     * column.
     */
    private int getNeighborCount(char state, int row, int column) {
        int nc = 0; //Neighbor Count

        if(getCNNState(row, column) == state) { ++nc; }
        if(getCENState(row, column) == state) { ++nc; }
        if(getCSNState(row, column) == state) { ++nc; }
        if(getCWNState(row, column) == state) { ++nc; }

        if(neighborhoodSize == MOORE) {
            if(getCNENState(row, column) == state) { ++nc; }
            if(getCSENState(row, column) == state) { ++nc; }
            if(getCSWNState(row, column) == state) { ++nc; }
            if(getCNWNState(row, column) == state) { ++nc; }
        }
        return nc;
    }

    /**
     * Resets the World's state.  In particular, sets the World's age to zero,
     * sets the stagnant flag to false, and sets the World's infection count to
     * zero.
     */
    public void reset() {
        stagnant = false;
        age = 0;
        numInfections = 0;
    }

    /**
     * Resets the World's state.  In particular, sets the World's age to zero;
     * sets the stagnant flag to false; sets the World's infection count to
     * zero; sets the current state of all Cells contained within the World to
     * the value of the specified state; sets the future state of all Cells
     * contained within the World to the value of UNDEFINED; and sets the
     * (current and future) age of all Cells contained within the World to zero.
     * @param state Current state that all Cells contained within the World
     * will take.
     */
    public void reset(char state) {
        stagnant = false;
        age = 0;
        numInfections = 0;
        for(int i = 0; i < rows; ++i) {
            for(int j = 0; j < columns; ++j) {
                world[i][j].setState(state);
                world[i][j].setAge(0);
                world[i][j].setFutureState(Cell.UNDEFINED);
                world[i][j].setFutureAge(0);
            }
        }
    }

    /**
     * Sets whether Carrier Cells will be introduced into the SIR model.
     * @param allow Indicates whether Carrier Cells will be
     * introduced into the SIR model.
     */
    public void allowCarriers(boolean allow) {
        allowCarriers = allow;
    }

    /**
     * Returns the World's current type.  The type String is utilized in
     * determining the evolution rules that are applied to the Cell grid.
     * @return World's current type.
     */
    public String getWorldType() {
        return worldType;
    }

    /**
     * Returns the current state of the Cell located at the specified row and
     * column within the World.
     * @param row Row index (Base zero).
     * @param column Column index (Base zero).
     * @return Current state of the Cell located the specified row and column
     * within the World grid.
     */
    public char getCellState(int row, int column) {
        return world[row][column].getState();
    }
}
