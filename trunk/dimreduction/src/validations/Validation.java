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
/*** This class implements the validations like Cross-Validation for     ***/
/*** verification of mean errors of the classifiers.                     ***/
/***************************************************************************/

package validations;

import java.util.Random;

public class Validation {
    
    public Validation() {
    }

    //Bootstrap
    public static synchronized void GenerateSubSetsBS(float [][] data, float [][] trainingset,  float [][] testset ){
        int totalrows = data.length;
        int columns = data[0].length;
        Random rn = new Random(System.nanoTime());
        
        int trainingsetsize = trainingset.length;
        for (int i = 0; i < trainingsetsize; i++){
            int selectedindex = rn.nextInt(totalrows);
            System.arraycopy(data[selectedindex], 0, trainingset[i], 0, columns);
        }

        int testsetsize = testset.length;
        for (int i = 0; i < testsetsize; i++){
            int selectedindex = rn.nextInt(totalrows);
            System.arraycopy(data[selectedindex], 0, testset[i], 0, columns);
        }
    }

    public static synchronized void GenerateSubSets(float [][] data, float percent_trainning,  float [][] trainingset,  float [][] testset ){
        int lines = data.length;
        int columns = data[0].length;

        int total_samples = (int) ( lines * percent_trainning );
        int selected_samples = 0;
        int selected_index = 0;
        int [] subsetindex = new int[lines];

        while ( selected_samples < total_samples ){
            selected_index = (int) (Math.random() * lines);
            while ( subsetindex[selected_index] == 1)
                selected_index = (int) (Math.random() * lines);

            subsetindex[selected_index] = 1;
            System.arraycopy(data[selected_index], 0, trainingset[selected_samples], 0, columns);

            selected_samples++;
        }

        int indextest = 0;
        for ( int i = 0; i < lines; i++ ){
            if (subsetindex[i]==0){
                System.arraycopy(data[i], 0, testset[indextest], 0, columns);

                subsetindex[i] = 1;
                indextest++;
            }
        }
    }
    
    
    public static synchronized void GenerateSubSets( String [] data, float percent_trainning,  String [] trainingset,  String [] testset ){
        int lines = data.length;
        
        int total_samples = (int) ( lines * percent_trainning );
        int selected_samples = 0;
        int selected_index = 0;
        int [] subsetindex = new int[lines];
        
        while ( selected_samples < total_samples ){
            selected_index = (int) (Math.random() * lines);
            while ( subsetindex[selected_index] == 1)
                selected_index = (int) (Math.random() * lines);
            
            subsetindex[selected_index] = 1;
            
            trainingset[selected_samples] = data[selected_index];
            
            selected_samples++;
        }
        
        int indextest = 0;
        for ( int i = 0; i < lines; i++){
            if ( subsetindex[i]==0 ){
                testset[indextest] = data[i];
                
                subsetindex[i] = 1;
                indextest++;
            }
        }
    }
}
