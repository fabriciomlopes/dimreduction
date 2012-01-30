/***************************************************************************/
/*** Interactive Graphic Environment for Dimensionality Reduction        ***/
/***                                                                     ***/
/*** Copyright (C) 2006  David Corrêa Martins Junior                     ***/
/***                     Fabrício Martins Lopes                          ***/
/***                     Roberto Marcondes Cesar Junior                  ***/
/***                                                                     ***/
/*** This library is free software; you can redistribute it and/or       ***/
/*** modify it under the terms of the GNU Lesser General Public          ***/
/*** License as published by the Free Software Foundation; either        ***/
/*** version 2.1 of the License, or (at your option) any later version.  ***/
/***                                                                     ***/
/*** This library is distributed in the hope that it will be useful,     ***/
/*** but WITHOUT ANY WARRANTY; without even the implied warranty of      ***/
/*** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU   ***/
/*** Lesser General Public License for more details.                     ***/
/***                                                                     ***/
/*** You should have received a copy of the GNU Lesser General Public    ***/
/*** License along with this library; if not, write to the Free Software ***/
/*** Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA       ***/
/*** 02110-1301  USA                                                     ***/
/***                                                                     ***/
/*** Contact: David Corr�a Martins Junior - davidjr@vision.ime.usp.br    ***/
/***          Fabr�cio Martins Lopes - fabriciolopes@vision.ime.usp.br   ***/
/***          Roberto Marcondes Cesar Junior - cesar@vision.ime.usp.br   ***/
/***************************************************************************/
/***************************************************************************/
/*** This class is responsible to generate a table of conditional        ***/
/*** probabilities for classification based on the training samples and  ***/
/*** the previously selected features.                                   ***/
/*** Also, this class implements functions to classify test samples      ***/
/*** based on the table generated                                        ***/
/***************************************************************************/
package fs;

import java.util.Random;
import java.util.Vector;
import utilities.RadixSort;

class Classifier {

    Vector table;     // table used for classification
    Vector instances; // instances are the index for the table entries
    int[] labels;    // labels corresponding to the classification of test
    // samples

    //Vector <String> toReturn = new Vector <String>();
    public Classifier() {
        table = new Vector();
        instances = new Vector();
    }

    // Given the training samples (A) sorted by the feature subspace (I)
    // values, checks if the instance given by a certain line number (line)
    // is equal to the previous one
    private boolean equalInstances(int line, Vector I, char[][] A) {
        for (int i = 0; i < I.size(); i++) {
            if (A[line - 1][(Integer) I.elementAt(i)] !=
                    A[line][(Integer) I.elementAt(i)]) {
                return false;
            }
        }
        return true;
    }

    // returns the index corresponding to the maximum element of a vector (v)
    private int indexMaxValue(double[] v) {
        int indexMax = -1;
        double maximum = Integer.MIN_VALUE;
        for (int i = 0; i < v.length; i++) {
            if (maximum < v[i]) {
                indexMax = i;
                maximum = v[i];
            }
        }
        Vector ties = new Vector();
        //int tie = 0;
        for (int i = 0; i < v.length; i++) {
            if (maximum == v[i]) {
                ties.add(i);
                //tie++;
                //if (tie == 1) {
                //    return -1; // more than one class with maximum value (tie)
                //} else {
                    
                //}
            }
        }
        if (ties.size() > 1){
            //sorteia um dos candidatos empatados...
            Random rn = new Random(System.nanoTime());
            int sorteio = rn.nextInt(ties.size());
            return((Integer)ties.get(sorteio));
        }else
            return indexMax;
    }

    // Given a sample, the feature subspace
    // considered (I) and the number of possible values for features (n),
    // returns the index of the instance.
    private double instanceIndex(char[] sample, Vector I, int n) {
        double instance = 0;
        int dim = I.size();
        for (int i = 0; i < I.size(); i++) {
            int feature = (Integer) I.elementAt(dim - i - 1);
            instance += sample[feature] * Math.pow(n, i);
        }
        return instance;
    }

    // adds the considered instance to the instances vector and the considered
    // conditional probabilities to the table of conditional probabilities
    // sampleString: training sample codified by a string
    // I: indexes of the considered features
    // pYdX: array of conditional probabilities (it is only reseted for the
    // mean conditional entropy calculus of the next instance)
    // pX: number of times that the an instance occurred (it is only reseted
    // for the mean conditional entropy calculus of the next instance)
    // n: number of possible values for features
    // c: number of possible classes
    public void addTableLine(char[] sample, Vector I, int[] pYdX, int pX,
            int n, int c) {
        // obtaining the index value of the considered instance
        double instance = instanceIndex(sample, I, n);

        double[] tableLine = new double[c];

        // reseting the conditional probabilities counter
        for (int k = 0; k < c; k++) {
            tableLine[k] = pYdX[k];
            pYdX[k] = 0;
        }
        pX = 0;                  // reseting instance occurrences counter
        instances.add(instance); // adding the instance
        table.add(tableLine);    // adding the corresponding conditional
    // probabilities of the processed instance
    }

    // implements a binary search for instance indexes searching
    public int binarySearch(double value) {
        int start = 0;
        int end = instances.size() - 1;
        while (start <= end) {
            int v = (start + end) / 2;
            if ((Double) instances.elementAt(v) == value) {
                return v;
            } else if ((Double) instances.elementAt(v) < value) {
                start = v + 1;
            } else {
                end = v - 1;
            }
        }
        return -1;
    }

    // given an instance index, the number of possible values which the
    // features assume and the dimension (number of features), returns its
    // corresponding feature vector
    public int[] instanceVector(double instanceIndex, int n, int d) {
        int[] V = new int[d];
        for (int i = d - 1; i >= 0; i--) {
            if (instanceIndex == 0) {
                break;
            }
            V[i] = (int) instanceIndex % n;
            instanceIndex = (double) Math.floor(instanceIndex / n);
        }
        return V;
    }

    // calculates the Euclidean distance between two vectors v1 and v2
    public double euclideanDistance(int[] v1, int[] v2) {
        double quadraticSum = 0;
        for (int i = 0; i < v1.length; i++) {
            quadraticSum += Math.pow((double) v1[i] - v2[i], 2);
        }
        return Math.sqrt(quadraticSum);
    }

    // Given the index of an instance, the number of values which each feature
    // can assume (n), the dimension (d) (number of features) and number of
    // classes (c), it implements Nearest Neighbors (NN) from the instanceIndex
    // to all instances observed. The idea is to sum the
    // conditional probabilities of the classes given all instances with
    // minimum Euclidan distance to instanceIndex. In case of tie, it continues
    // analyzing the next instances with minimum distance until the tie be
    // broken. It returns the label with major conditional probability.
    public int nearestNeighbors(double instanceIndex, int n, int d, int c) {

        // getting the feature vector corresponding to the considered instance
        int[] instanceValues = instanceVector(instanceIndex, n, d);
        double[] distances = new double[instances.size()];

        // calculating all distances from the input instance to all observed
        // instances
        for (int i = 0; i < instances.size(); i++) {
            int[] currentInstance = instanceVector((Double) instances.elementAt(i), n, d);
            distances[i] = euclideanDistance(instanceValues, currentInstance);
        }
        double[] pYdX = new double[c];

        // this loop will finish only when the tie is broken
        while (true) {
            // obtaining the minimum distance to the input instance
            double minDist = Double.MAX_VALUE;
            for (int i = 0; i < instances.size(); i++) {
                if (distances[i] < minDist) {
                    minDist = distances[i];
                }
            }

            // summing all conditional probilities of the classes given each
            // instance with minimum distance
            for (int i = 0; i < instances.size(); i++) {
                if (distances[i] == minDist) {
                    double[] temp = (double[]) table.elementAt(i);
                    for (int j = 0; j < c; j++) {
                        pYdX[j] += temp[j];
                    }
                    distances[i] = Double.MAX_VALUE; // conditional probabilities
                // of this instances summed,
                // setting maximum value to its
                // distance
                }
            }
            // obtaining the label with maximum conditional probability
            int indexMax = indexMaxValue((double[]) pYdX);
            if (indexMax > -1) // the tie is broken?
            {
                return indexMax; // if so, returns the winner label
            }            // (otherwise, continues in the loop...)
        }
    }

    // constructs the conditional probabilities table
    // A: training samples indexed by strings
    // I: indexes of the considered features
    // n: number of possible values for features
    // c: number of possible classes
    public void classifierTable(char[][] A, Vector I, int n, int c) {
        int lines = A.length;
        int pX = 0;
        int[] pYdX = new int[c];
        RadixSort.radixSort(A, I, n); // sorting the samples
        for (int j = 0; j < lines; j++) // for each sample...
        {
            if (j > 0 && !equalInstances(j, I, A)) // next instance?
            // adding the conditional probabilities corresponding to the
            // previous instance and reseting pYdX and pX in order to process
            // the coming instance
            {
                addTableLine(A[j - 1], I, pYdX, pX, n, c);
            }
            // accounting an observation of the given label for through the
            // current instance
            pYdX[A[j][A[j].length - 1]]++;
            pX++;
        }
        // adding the conditional probabilities corresponding to the last
        // instance
        addTableLine(A[lines - 1], I, pYdX, pX, n, c);
    }

    // based on the table of conditional probabilities constructed, the
    // features selected (I), the number of possible values for features (n)
    // and number of possible classes (c),  this function classify testing
    // samples (A)
    public double[] classifyTestSamples(char[][] A, Vector I, int n, int c) {
        int lines = A.length;
        labels = new int[lines];
        double[] testInstances = new double[lines];

        for (int i = 0; i < lines; i++) { 
            testInstances[i] = instanceIndex(A[i], I, n);
            int index = binarySearch(testInstances[i]);
            // the indexOf function of the class Vector is very
            // inefficient to be used a lot of times (in the case of large
            // number of observed instances)

            if (index == -1) // if the instance didn�t appear in training
            // samples...
            //labels[i] = c; // assigns the label "c" (unknown)
            // gets the most appropriate label by applying Nearest Neighbors (NN)
            {
                labels[i] = nearestNeighbors(testInstances[i], n, I.size(), c);
            } else {
                // if the instance occurred, it assigns the label with major
                // conditional probability (Bayesian)
                labels[i] = indexMaxValue((double[]) table.elementAt(index));

                // but if there was a tie between labels with major conditional
                // probability, it applies Nearest Neighbors in order to break the
                // tie
                if (labels[i] == -1) {
                    labels[i] = nearestNeighbors(testInstances[i], n, I.size(), c);
                }
            }
        }
        return (testInstances);
    }
} 
