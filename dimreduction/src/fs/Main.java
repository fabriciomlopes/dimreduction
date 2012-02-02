/***************************************************************************/
/*** Interactive Graphic Environment for Dimensionality Reduction        ***/
/***                                                                     ***/
/*** Copyright (C) 2006  David Correa Martins Junior                     ***/
/***                     Fabricio Martins Lopes                          ***/
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
/*** This class implements the Main function of the software.            ***/
/***************************************************************************/
package fs;

import agn.AGNRoutines;
import agn.AGN;
import java.io.IOException;
import java.util.Vector;
import utilities.IOFile;

public class Main {

    public static String delimiter = String.valueOf(' ') + String.valueOf('\t') + String.valueOf('\n') + String.valueOf('\r') + String.valueOf('\f') + String.valueOf(';') + String.valueOf('"');

    public static int maximumValue(float[][] M, int startLine, int endLine, int startCollumn, int endCollumn) {
        int i, j;
        int maximum = 0;
        for (i = startLine; i <= endLine; i++) {
            for (j = startCollumn; j <= endCollumn; j++) {
                if (M[i][j] > maximum) {
                    maximum = (int) M[i][j];
                }
            }
        }
        return maximum;
    }

    public static int maximumValue(char[][] M, int startLine, int endLine, int startCollumn, int endCollumn) {
        int i, j;
        int maximum = 0;
        for (i = startLine; i <= endLine; i++) {
            for (j = startCollumn; j <= endCollumn; j++) {
                if (M[i][j] > maximum) {
                    maximum = M[i][j];
                }
            }
        }
        return maximum;
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-v")) {
                //Vector genenames = null;
                Vector featurestitles = null;
                String inpath = args[1];
                String outpath = args[2];

                int datatype = 1; //datatype: 1==temporal, 2==steady-state.
                float threshold_entropy = 1;
                String type_entropy = "no_obs";
                float alpha = 1;
                float q_entropy = 1;
                float beta = 0.8f;
                int maxfeatures = 5;
                int resultsetsize = 1;

                featurestitles = IOFile.readDataFirstRow(inpath, 0, 1, delimiter);
                int startrow = 1;
                Vector collumns = new Vector(2);
                collumns.add(0);
                collumns.add(1);
                Vector[] geneids = IOFile.readDataCollumns(inpath, 1, collumns, delimiter);
                //genenames = IOFile.readDataFirstCollum(inpath, startrow, delimiter);
                int startcolumn = 2;
                float[][] expressiondata = IOFile.readMatrix(inpath, startrow, startcolumn, delimiter);

                int nrgenes = expressiondata.length;
                int signalsize = expressiondata[0].length;

                float[] mean = new float[signalsize];
                float[] std = new float[signalsize];
                float[] lowthreshold = new float[signalsize];
                float[] hithreshold = new float[signalsize];
                
                //data quantization - normal cases...
                //float[][] quantizeddata = Preprocessing.copyMatrix(expressiondata);//copy of matrix
                //Preprocessing.quantizecolumns(quantizeddata, 2, true, 0);
                
                int[][] quantizeddata = new int[nrgenes][signalsize];
                float[][] normalizeddata = Preprocessing.quantizecolumnsMAnormal(
                        expressiondata,
                        quantizeddata,
                        3,
                        mean,
                        std,
                        lowthreshold,
                        hithreshold);
                IOFile.writeMatrix(outpath + "quantized-data.txt", quantizeddata, ";");

                AGN recoverednetwork = new AGN(nrgenes, signalsize, 2, datatype);
                recoverednetwork.setMean(mean);
                recoverednetwork.setStd(std);
                recoverednetwork.setLowthreshold(lowthreshold);
                recoverednetwork.setHithreshold(hithreshold);
                recoverednetwork.setTemporalsignal(expressiondata);
                recoverednetwork.setTemporalsignalquantized(quantizeddata);
                recoverednetwork.setTemporalsignalnormalized(normalizeddata);
                recoverednetwork.setLabelstemporalsignal(featurestitles);
                
                //AGNRoutines.setGeneNames(recoverednetwork, genenames);
                AGNRoutines.setNameandPSN(recoverednetwork, geneids);

                AGNRoutines.RecoverNetworkfromExpressions(
                        recoverednetwork,
                        null,
                        datatype, //datatype: 1==temporal, 2==steady-state.
                        false,//Is the signal periodic?
                        threshold_entropy,//Threshold value
                        type_entropy,//Penalization Method
                        alpha,//alpha value for penalization method
                        beta,//beta value for penalization method
                        q_entropy,//q-entropy value for penalization method (0 == CoD, 1 == Entropy)
                        null, //target indexes, null == all genes are considered as targets
                        maxfeatures,//max features == dimension considered by search method
                        3,//1==SFS, 2==Exhaustive, 3==SFFS, 4==SFFS_stack(expandindo todos os empates encontrados).
                        false,//The targets are considered as predictors?
                        resultsetsize,
                        null,
                        3//stacksize, used only for SFFS_Stack (option 4) == tamanho da expansao dos empatados.
                        );
                //armazenamento dos resultados
                IOFile.writeAGNtoFile(recoverednetwork, outpath + "rede-completa.agn");
            } else {
                //execution with graphical interface...
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new MainWindow().setVisible(true);
                    }
                });
            }
        } else {
            //execution with graphical interface...
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new MainWindow().setVisible(true);
                }
            });
        }
    }
}
