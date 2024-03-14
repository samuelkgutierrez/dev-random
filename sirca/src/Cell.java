//----------------------------------------------------------------------------//
//                                                                            //
//----------------------------------------------------------------------------//
//                                                                            //
// Copyright (c) 2008-2024 Samuel K. Gutierrez All Rights Reserved.           //
//                                                                            //
// This program is free software; you can redistribute it and/or modify it    //
// under the terms of the GNU General Public License as published by the Free //
// Software Foundation; either version 2 of the License, or (at your option)  //
// any later version.                                                         //
//                                                                            //
// This program is distributed in the hope that it will be useful, but WITHOUT//
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or      //
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for  //
// more details.                                                              //
//                                                                            //
// You should have received a copy of the GNU General Public License along    //
// with this program; if not, write to the Free Software Foundation, Inc., 59 //
// Temple Place, Suite 330, Boston, MA  02111-1307  USA                       //
//                                                                            //
//----------------------------------------------------------------------------//
//                                                                            //
//----------------------------------------------------------------------------//

/**
 * @author Samuel K. Gutierrez
 * @version 0.04
 */

public final class Cell {

    public static final char UNDEFINED = 'U';
    ///Cell's current state (Cell's state at t, S(t))
    private char state;
    ///Cell's future state (Cell's state at t+1, S(t+1))
    private char futureState;
    ///Cell's current age (Cell's age at t, A(t))
    private int age;
    ///Cell's future age (Cell's age at t+1, A(t+1))
    private int futureAge;

    /**
     * Constructs a new Cell and initializes its current and future states to
     * the value of UNDEFINED. Also initializes the Cell's current and future
     * ages to zero.
     */
    public Cell() {
        state = UNDEFINED;
        futureState = UNDEFINED;
        age = 0;
        futureAge = 0;
    }

    /**
     * Constructs a new Cell and initializes its current state to the specified
     * character, initializes its future state to the value of UNDEFINED, and
     * initializes its current and future ages to zero.
     * @param initialState Cell's initial current state.
     */
    public Cell(char initialState) {
        state = initialState;
        futureState = UNDEFINED;
        age = 0;
        futureAge = 0;
    }

    /**
     * Sets all the Cell's attributes to those of the provided Cell.
     * @param cell Cell whose attribute values will be utilized.
     */
    public void clone(Cell cell) {
        state = cell.state;
        futureState = cell.futureState;
        age = cell.age;
        futureAge = cell.futureAge;
    }

    /**
     * Sets the Cell's current state to the specified character.
     * @param newState The value that is to be the Cell's new current state.
     */
    public void setState(char newState) {
        state = newState;
    }

    /**
     * Sets the Cell's future state to the specified character.
     * @param newState The value that is to be the Cell's new future state.
     */
    public void setFutureState(char newState) {
        futureState = newState;
    }

    /**
     * Returns the Cell's current state.
     * @return Cell's current state.
     */
    public char getState() {
        return state;
    }

    /**
     * Returns the Cell's future state.
     * @return Cell's future state.
     */
    public char getFutureState() {
        return futureState;
    }

    /**
     * Sets the Cell's current age to the specified integer value.  Provided for
     * convenience.
     * @param newAge The integer that is to be the cell's new current age.
     */
    public void setAge(int newAge) {
        age = newAge;
    }

    /**
     * Sets the Cell's future age to the specified integer value.  Provided for
     * convenience.
     * @param newAge The integer that is to be the cell's new future age.
     */
    public void setFutureAge(int newAge) {
        futureAge = newAge;
    }

    /**
     * Returns the Cell's current age.
     * @return Cell's current age.
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns the Cell's future age.
     * @return Cell's future age.
     */
    public int getFutureAge() {
        return futureAge;
    }

    /**
     * Increments Cell's current age by one.
     */
    public void age() {
        ++age;
    }

    /**
     * Increments Cell's future age by one.
     */
    public void fage() {
        ++futureAge;
    }

    /**
     * Sets the current state to the future state.  Also sets the current age
     * to the future age.
     */
    public void update() {
        state = futureState;
        age = futureAge;
    }

    /**
     * Overrides toString.
     * @return String in the form: [ Current State, Future State, Current Age,
     *  Future Age ].
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "[" + state + "]";
    }
}
