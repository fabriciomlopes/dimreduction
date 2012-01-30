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
/*** Contact: David Corrêa Martins Junior - davidjr@vision.ime.usp.br    ***/
/***          Fabrício Martins Lopes - fabriciolopes@vision.ime.usp.br   ***/
/***          Roberto Marcondes Cesar Junior - cesar@vision.ime.usp.br   ***/
/***************************************************************************/
/***************************************************************************/
/*** This class implements the RadixSort algorithm used to sort the ********/
/*** samples for the mean conditional entropy calculus              ********/
/***************************************************************************/
package utilities;

import java.util.Vector;

public class RadixSort {
    // INPUT: an array of strings indicating the samples (v), a vector of indexes
    // of the feature subspace (I) and a integer indicating the number of possible
    // values of the features (n)
    // OUTPUT: the array of samples sorted by the values of the features indexed
    // by the vector of indexes (I)

    public static void radixSort(char[][] v, Vector I, int n) {
        int lines = v.length;
        FIFOQueue queues[] = createQueues(n, lines);
        for (int pos = I.size() - 1; pos >= 0; pos--) {
            for (int i = 0; i < lines; i++) {
                int q = queueNo(v[i], (Integer) I.elementAt(pos));
                queues[q].add(v[i]);
            }
            restore(queues, v);
        }
        queues = null;
        System.gc();
    }

    // used in the Radix Sort algorithm
    private static void restore(FIFOQueue[] qs, char[][] v) {
        int contv = 0;
        for (int q = 0; q < qs.length; q++) {
            while (!qs[q].isEmpty()) {
                v[contv++] = (char[]) qs[q].deq();
            }
        }
    }

    // creates the queues used in Radix Sort algorithm
    private static FIFOQueue[] createQueues(int n, int lines) {
        FIFOQueue[] result = new FIFOQueue[n];
        for (int i = 0; i < n; i++) {
            result[i] = new FIFOQueue(lines);
        }
        return result;
    }

    // returns the right queue for a character in a string
    private static int queueNo(char[] v, int pos) {
        return v[pos];
    }
}
