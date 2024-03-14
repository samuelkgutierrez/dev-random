import java.util.Random;

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
 * @version 0.1
 *
 */
public class SimpleIntMatrix {
    private final int numRows;
    private final int numColumns;
    private final int[][] matrix;
    private static Random rng;

    public SimpleIntMatrix(int m, int n) {
        rng = new Random();
        numRows = m;
        numColumns = n;
        matrix = new int[numRows][numColumns];
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int[] getColumnAt(int columnIndex) {
        int[] columnVector = new int[numRows];
        for(int i = 0; i < numRows; ++i) {
            columnVector[i] = matrix[i][columnIndex];
        }
        return columnVector;
    }

    public int[] getRowAt(int rowIndex) {
        int[] rowVector = new int[numColumns];
        for(int j = 0; j < numColumns; ++j) {
            rowVector[j] = matrix[rowIndex][j];
        }
        return rowVector;
    }

    public String getRowStringAt(int rowIndex) {
        StringBuffer vectorStringBuffer = new StringBuffer();
        for(int j = 0; j < numColumns; ++j) {
            vectorStringBuffer.append(matrix[rowIndex][j]);
        }
        return vectorStringBuffer.toString();
    }

    public String getColumnStringAt(int columnIndex) {
        StringBuffer vectorStringBuffer = new StringBuffer();
        for(int i = 0; i < numRows; ++i) {
            vectorStringBuffer.append(matrix[i][columnIndex]);
        }
        return vectorStringBuffer.toString();
    }

    public void set(int rowIndex, int colIndex, int newValue) {
        matrix[rowIndex][colIndex] = newValue;
    }

    public void set(int[][] intMatrix) {
        if(numRows != intMatrix.length || numColumns != intMatrix[0].length) {
            return;
        }

        for(int i = 0; i < numRows; ++i) {
            for(int j = 0; j < numColumns; ++j) {
                matrix[i][j] = intMatrix[i][j];
            }
        }
    }

    public void set(SimpleIntMatrix sim) {
        if(numRows != sim.numRows || numColumns != sim.numColumns) {
            return;
        }

        for(int i = 0; i < numRows; ++i) {
            for(int j = 0; j < numColumns; ++j) {
                matrix[i][j] = sim.matrix[i][j];
            }
        }
    }

    public void fill(int fillValue) {
        for(int i = 0; i < numRows; ++i) {
            for(int j = 0; j < numColumns; ++j) {
                matrix[i][j] = fillValue;
            }
        }
    }

    public void fillRandomly(int rLimit) {
        for(int i = 0; i < numRows; ++i) {
            for(int j = 0; j < numColumns; ++j) {
                matrix[i][j] = rng.nextInt(rLimit);
            }
        }
    }

    public boolean sameDimensions(int[][] intMatrix) {
        return (numRows == intMatrix.length &&
                numColumns == intMatrix[0].length);
    }

    public boolean sameDimensions(SimpleIntMatrix sim) {
        return (numRows == sim.numRows && numColumns == sim.numColumns);
    }

    public void setRowAt(int rowIndex, int[] rowVector) {
        if(numColumns != rowVector.length) {
            return;
        }
        for(int j = 0; j < numColumns; ++j) {
            matrix[rowIndex][j] = rowVector[j];
        }
    }

    public void setRowAt(int rowIndex, String bitString) {
        if(numColumns != bitString.length()) {
            return;
        }

        for(int j = 0; j < numColumns; ++j) {
            matrix[rowIndex][j] =
                Integer.parseInt(bitString.substring(j, j + 1));
        }
    }

    public void setColumnAt(int columnIndex, int[] columnVector) {
        if(numRows != columnVector.length) {
            return;
        }
        for(int i = 0; i < numRows; ++i) {
            matrix[i][columnIndex] = columnVector[i];
        }
    }

    public int get(int rowIndex, int colIndex) {
        return matrix[rowIndex][colIndex];
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < numRows; ++i) {
            sb.append("|");
            sb.append(i);
            sb.append("|: ");
            for(int j = 0; j < numColumns; ++j) {
                sb.append(matrix[i][j]);
                sb.append(" ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
