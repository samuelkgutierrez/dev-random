import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.TreeMap;
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
 * SIRCA Genetic Algorithm Implementation
 * University of New Mexico
 * @author Samuel K. Gutierrez
 * @version 0.1
 *
 */

public class GeneticAlgorithm {

    ///Flag Indicating Whether or Not Debugging Information Will Be Provided
    private static final boolean DEBUG = false;

    ///Chromosome Length
    private static final int CHROMOSOME_LENGTH = 12;
    ///Population Size Note:Must Be an Even Number.
    private static final int POPULATION_SIZE = 100;
    ///Number of Rows That Will Be Utilized Within The CA Model
    private static final int CA_NUM_ROWS = 20;
    ///Number of Columns That Will Be Utilized Within The CA Model
    private static final int CA_NUM_COLS = 20;
    ///Maximum Number of Iterations Allowed For a Single Run of a CA
    private static final int MAX_CA_ITERATIONS = 30;

    private static final boolean ALLOW_BIRTHS = true;
    ///Tournament Size Utilized in Tournament Selection
    private static final int TOURNAMENT_SIZE = 25;
    ///Maximum Number of Generations Allowed Within Simulation
    private static final int MAX_GENERATIONS = 100;

    //Probability That a Crossover Will Occur
    private static final double PROB_CROSSOVER = 0.25;
    //Probability That a Mutation Will Occur
    private static final double PROB_MUTATION = 0.001;

    ///Population of Bit Strings
    private static SimpleIntMatrix population;

    ///Instance of the World Class Used In Tournaments
    private static World world;

    private static final Random rng = new Random();

    ///List Containing Population's Current Fitness Values
    private static int[] populationFitness = new int[POPULATION_SIZE];

    private static int[] maxFitList = new int[MAX_GENERATIONS];
    private static double[] meanFitList = new double[MAX_GENERATIONS];
    private static SimpleIntMatrix mostFitStrings =
        new SimpleIntMatrix(MAX_GENERATIONS,CHROMOSOME_LENGTH);


    private GeneticAlgorithm() {

        world = new World(CA_NUM_ROWS, CA_NUM_COLS, World.SIR_WORLD,
                World.MOORE, 0, 100, 0, 0);
        world.allowBirths(ALLOW_BIRTHS);
        populateModel();

        population = new SimpleIntMatrix(POPULATION_SIZE, CHROMOSOME_LENGTH);
        //Randomly Fill Populations With 0s and 1s
        population.fillRandomly(2);

        //Calculate and Record Population's Initial Fitness
        updatePopulationFitness(); //Calculate Population's Initial Fitness
        maxFitList[0] = maxFitness(populationFitness);
        meanFitList[0] = meanFitness(populationFitness);
        mostFitStrings.setRowAt(0, population.getRowAt(
                getIndexOfMostFit(populationFitness)));

        if(DEBUG) {
            System.out.println("Initial Population");
            System.out.println("****************************");
            System.out.println(population);
            System.out.println("****************************");
        }
    }

    /**
     * Starts the Genetic Algorithm.
     */
    private final void run() {
        for(int i = 1; i < MAX_GENERATIONS; ++i) {
            //Tournament Selection
            population.set(tournamentSelection());
            //Crossover
            population.set(crossover());
            //Mutation
            population.set(singlePointMutation());
            //Calculate and Record Population's Fitness
            updatePopulationFitness();
            maxFitList[i] = maxFitness(populationFitness);
            meanFitList[i] = meanFitness(populationFitness);
            mostFitStrings.setRowAt(i, population.getRowAt(
                    getIndexOfMostFit(populationFitness)));
        }

        System.out.print("meanFitness = [");
        for(int i = 0; i < meanFitList.length; ++i) {
            System.out.print(meanFitList[i] + " ");
        }
        System.out.println("];");

        System.out.print("maxFitness = [");
        for(int i = 0; i < maxFitList.length; ++i) {
            System.out.print(maxFitList[i] + " ");
        }
        System.out.println("];");

        System.out.println("plot(meanFitness); hold on; plot(maxFitness, 'r')");

        System.out.println("xlabel('Generations'); ylabel('Fitness'); " +
                "legend('Mean fitness','Max Fitness','Location','SouthEast');");

        System.out.println("" +
        "title('Figure 01: Fitness versus Generation')");

        System.out.println(mostFitStrings);
    }

    public void printMatlabReport() {
        //TODO
    }

    /**
     * Initialize Cellular Automaton with a fully susceptible population.
     * Adds three infectious cells to the center of the CA Model.
     */
    public final void populateModel() {
        for(int i = 0; i < CA_NUM_ROWS; ++i) {
            for(int j = 0; j < CA_NUM_COLS; ++j) {
                world.setCellState(i, j, World.SUSCEPTIBLE);
            }
        }
        //Add infectious Cells to approximately the center of the CA.
        int centerColumn =  CA_NUM_COLS/2;
        int centerRow = CA_NUM_ROWS/2;
        world.setCellState(centerRow, centerColumn - 1, World.INFECTED);
        world.setCellState(centerRow, centerColumn,     World.INFECTED);
        world.setCellState(centerRow, centerColumn + 1, World.INFECTED);
    }

    public SimpleIntMatrix tournamentSelection() {
        SimpleIntMatrix newPopulation =
            new SimpleIntMatrix(population.getNumRows(),
                    population.getNumColumns());

        for(int i = 0; i < POPULATION_SIZE; ++i) {
            newPopulation.setRowAt(i, population.getRowAt(compete()));
            if(DEBUG) {
                System.out.println("****************************");
                System.out.println(newPopulation);
            }
        }

        if(DEBUG) {
            System.out.println("Final Population After Tournament: ");
            System.out.println(newPopulation);
        }

        return newPopulation;
    }

    public SimpleIntMatrix crossover() {
        ArrayList<Integer> matingList = new ArrayList<Integer>(POPULATION_SIZE);
        for(int i = 0; i < POPULATION_SIZE; ++i) {
            matingList.add(i, i);
        }
        Collections.shuffle(matingList);

        SimpleIntMatrix newPopulation =
            new SimpleIntMatrix(population.getNumRows(),
                    population.getNumColumns());

        for(int i = 0; !matingList.isEmpty(); i += 2) {
            String a = population.getRowStringAt(matingList.get(0));
            matingList.remove(0);
            String b = population.getRowStringAt(matingList.get(0));
            matingList.remove(0);
            String[] offspring = randTwoPointCrossover(a, b); //Change CO Type Here...
            newPopulation.setRowAt(i, offspring[0]);
            newPopulation.setRowAt(i + 1, offspring[1]);
        }

        return newPopulation;
    }

    public SimpleIntMatrix singlePointMutation() {
        SimpleIntMatrix newPopulation =
            new SimpleIntMatrix(population.getNumRows(),
                    population.getNumColumns());
        for(int i = 0; i < POPULATION_SIZE; ++i) {
            for(int j = 0; j < CHROMOSOME_LENGTH; ++j) {
                if(rng.nextDouble() < PROB_MUTATION) {
                    newPopulation.set(i, j, flip(population.get(i, j)));
                }
                else {
                    newPopulation.set(i, j, population.get(i, j));
                }
            }
        }

        return newPopulation;
    }

    public int[] flipAllBits(int[] bitArray) {
        int[] flippedBitArray = new int[bitArray.length];
        for(int i = 0; i < bitArray.length; ++i) {
            flippedBitArray[i] = 1 & ~bitArray[i]; //Flip Bits
        }
        return flippedBitArray;
    }

    public int[] flipOneBit(int[] bitArray) {
        int[] flippedBitArray = new int[bitArray.length];
        int randLoc = rng.nextInt(bitArray.length);
        for(int i = 0; i < bitArray.length; ++i) {
            flippedBitArray[i] = bitArray[i]; //Flip Bits
        }
        flippedBitArray[randLoc] = 1 & ~bitArray[randLoc]; //Flip Bits
        return flippedBitArray;
    }

    public int flip(int a) {
        return 1 & ~a;
    }

    /**
     * Updates all the values contained within the populationFitness array
     * to reflect the population's current fitness.
     */
    public void updatePopulationFitness() {
        for(int i = 0; i < POPULATION_SIZE; ++i) {
            populationFitness[i] = runCA(population.getRowStringAt(i));
        }
    }

    /**
     * Returns the largest value contained within the provided array.
     * @param data Input Data.
     * @return Largest value contained within the provided array.
     */
    public int maxFitness(int[] data) {
        int max = data[0];
        for(int i = 1; i < data.length; ++i) {
            if(data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }

    /**
     * Calculates the mean value of the given array.
     * @param data Input Data.
     * @return Mean value of the given array.
     */
    public double meanFitness(int[] data) {
        double runningTotal = 0.0;
        for(int i = 0; i < data.length; ++i) {
            runningTotal += data[i];
        }
        return runningTotal/data.length;
    }

    public int getIndexOfMostFit(int[] data) {
        int maxValue = data[0];
        int maxIndex = 0;
        for(int i = 1; i < data.length; ++i) {
            if(data[i] > maxValue) {
                maxValue = data[i];
                maxIndex = i;
            }
        }
        return maxIndex;

    }

    public String[] onePointCrossover(String a, String b) {

        if(rng.nextDouble() > PROB_CROSSOVER) {
            return new String[]{a,b};
        }

        int crossPoint = rng.nextInt(a.length());
        String aHat = a.substring(0, crossPoint);
        String bHat = b.substring(0, crossPoint);
        aHat += b.substring(crossPoint, b.length());
        bHat += a.substring(crossPoint, a.length());

        if(DEBUG) {
            System.out.println("Crossed At: " + crossPoint);
            System.out.println("A: " + a);
            System.out.println("B: " + b);
            System.out.println("New A:" + aHat);
            System.out.println("New B:" + bHat);
        }
        return new String[] {aHat, bHat};
    }

    /**
     * @param a
     * @param b
     * @return //Sttring[0] -> ahat String[1] -> bhat
     */
    public String[] randTwoPointCrossover(String a, String b) {

        if(rng.nextDouble() > PROB_CROSSOVER) {
            return new String[]{a,b};
        }

        int crossPointA = rng.nextInt(a.length());
        int crossPointB = rng.nextInt(a.length());

        if(crossPointA > crossPointB) {
            int temp = crossPointA;
            crossPointA = crossPointB;
            crossPointB = temp;
        }

        //Assuming crossPointA < crossPointB
        StringBuffer aHat = new StringBuffer(a.substring(0, crossPointA));
        StringBuffer bHat = new StringBuffer(b.substring(0, crossPointA));

        aHat.append(b.substring(crossPointA, crossPointB));
        bHat.append(a.substring(crossPointA, crossPointB));

        aHat.append(a.substring(crossPointB, a.length()));
        bHat.append(b.substring(crossPointB, b.length()));

        if(DEBUG) {
            System.out.println("Crossed At: " + crossPointA + " And " +
                    crossPointB);
            System.out.println("A: " + a);
            System.out.println("B: " + b);
            System.out.println("New A:" + aHat);
            System.out.println("New B:" + bHat);
        }

        return new String[] {aHat.toString(), bHat.toString()};
    }

    //Crossover only within rule bit substring.  Assumes strings of length 12.
    public String[] twoPointCrossover(String a, String b) {
        if(rng.nextDouble() > PROB_CROSSOVER) {
            return new String[]{a,b};
        }


        int crossPointA = rng.nextInt(4) * 3;
        int crossPointB = crossPointA + 3;

        //Assuming crossPointA < crossPointB
        StringBuffer aHat = new StringBuffer(a.substring(0, crossPointA));
        StringBuffer bHat = new StringBuffer(b.substring(0, crossPointA));

        aHat.append(b.substring(crossPointA, crossPointB));
        bHat.append(a.substring(crossPointA, crossPointB));

        aHat.append(a.substring(crossPointB, a.length()));
        bHat.append(b.substring(crossPointB, b.length()));

        if(DEBUG) {
            System.out.println("Crossed At: " + crossPointA + " And " +
                    crossPointB);
            System.out.println("A: " + a);
            System.out.println("B: " + b);
            System.out.println("New A:" + aHat);
            System.out.println("New B:" + bHat);
        }

        return new String[] {aHat.toString(), bHat.toString()};
    }

    private int runCA(String ruleString) {
        world.reset();
        populateModel();
        //Note: I take the world's state into consideration to save on
        //some time.  && !world.isStagnant() can be removed.
        for(int i = 0; i < MAX_CA_ITERATIONS; ++i) {
            world.evolve(ruleString);
        }
        return world.getNewInfectionCount();
    }

    /**
     * @return Index of winning individual
     */
    public final int compete() {
        //Key: Number of New Infections
        //Value: Index of Winning Individual
        TreeMap<Integer, Integer> tournamentTable =
            new TreeMap<Integer, Integer>();

        for(int t = 0; t < TOURNAMENT_SIZE; ++t) {
            int mfi = rng.nextInt(POPULATION_SIZE);
            int infCount = populationFitness[mfi];
            if(DEBUG) {
                System.out.println("Putting <Infected: " + infCount +
                        ", Index: " + mfi + ">");
            }
            tournamentTable.put(infCount, mfi);
        }
        if(DEBUG) {
            System.out.println("Returning Row Index: " +
                    tournamentTable.get(tournamentTable.lastKey()));
        }
        return tournamentTable.get(tournamentTable.lastKey());
    }

    public static final int[] grayEncode(int[] binaryBitArray) {
        int grayBitArray[] = new int[binaryBitArray.length];
        grayBitArray[0] = binaryBitArray[0];
        for(int i  = 1; i < grayBitArray.length; ++i) {
            grayBitArray[i] = binaryBitArray[i] ^ binaryBitArray[i - 1];
        }
        return grayBitArray;
    }

    public static final int[] grayEncode(String binaryBitArrayString) {
        int grayBitArray[] = new int[binaryBitArrayString.length()];
        grayBitArray[0] = Integer.parseInt(
                binaryBitArrayString.substring(0, 1));
        for(int i  = 1; i < grayBitArray.length; ++i) {
            grayBitArray[i] = Integer.parseInt(
                    binaryBitArrayString.substring(i, i + 1)) ^
            Integer.parseInt(binaryBitArrayString.substring(i - 1, i));
        }
        return grayBitArray;
    }

    public static final int[] grayDecode(int[] grayBitArray) {
        int binaryBitArray[] = new int[grayBitArray.length];
        binaryBitArray[0] = grayBitArray[0];
        for(int i  = 1; i < binaryBitArray.length; ++i) {
            binaryBitArray[i] = binaryBitArray[i - 1] ^ grayBitArray[i];
        }
        return binaryBitArray;
    }

    public static final int grayDecode(String grayBitArrayString) {
        int binaryBitArray[] = new int[grayBitArrayString.length()];
        StringBuffer sb = new StringBuffer();
        binaryBitArray[0] = Integer.parseInt(
                grayBitArrayString.substring(0, 1));
        for(int i  = 1; i < binaryBitArray.length; ++i) {
            binaryBitArray[i] = binaryBitArray[i - 1] ^
                    Integer.parseInt(grayBitArrayString.substring(i, i + 1));
        }
        for(int i = 0; i < binaryBitArray.length; ++i) {
            sb.append(binaryBitArray[i]);
        }
        return Integer.parseInt(sb.toString(), 2);
    }

    public static void main(String args[]) {
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.run();
    }
}
