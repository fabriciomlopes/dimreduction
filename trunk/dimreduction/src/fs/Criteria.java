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
/*** This class implements the feature selection criteria based on mean  ***/
/*** conditional entropy and coefficient of determination (COD). There   ***/
/*** are two types of penalization of non-observed instances: no_obs     ***/
/*** (penalty for non-observed instances) and poor_obs                   ***/
/*** (penalty for poorly observed instances). The Tsallis entropy        ***/
/*** (q-entropy) is adopted here, and q is a parameter. The traditional  ***/
/*** Shannon entropy is obtained for q = 1.                              ***/
/***************************************************************************/
package fs;

import agn.AGN;
import java.util.Vector;
import utilities.RadixSort;

class Criteria {

    //vector to keep the conditional probability distribution of the predictors given a target gene.
    public static Vector probtable;

    // Given the training samples (A) sorted by the feature subspace (I)
    // values, checks if the instance given by a certain line number (line)
    // is equal to the previous one
    private static int getPositionofInstances(int line, Vector I, char[][] A) {
        StringBuffer binnumber = new StringBuffer();
        for (int i = 0; i < I.size(); i++) {
            binnumber.append((int) A[line - 1][(Integer) I.elementAt(i)]);
        }
        int position = utilities.MathRoutines.Bin2Dec(binnumber.toString());
        return (position);
    }

    /*private static int getPositionofInstances_Gensips2011(int line, Vector I, char[][] A) {
    StringBuffer binnumber = new StringBuffer();
    for (int i = 0; i < I.size(); i++) {
    while (((int) A[line - 1][(Integer) I.elementAt(i)]) == 3) {
    line--;
    }
    binnumber.append((int) A[line - 1][(Integer) I.elementAt(i)]);
    }
    int position = utilities.MathRoutines.Bin2Dec(binnumber.toString());
    return (position);
    }*/
    private static boolean equalInstances(int line, Vector I, char[][] A) {
        for (int i = 0; i < I.size(); i++) {
            int predictor = (Integer) I.elementAt(i);
            if (A[line - 1][predictor]
                    != A[line][predictor]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidValue(int line, Vector I, char[][] A) {
        //verifica valor do alvo (target)
        if (A[line][A[line].length - 1] == 3) {
            return false;
        }

        for (int i = 0; i < I.size(); i++) {
            int predictor = (Integer) I.elementAt(i);
            if (A[line][predictor] == 3) {
                return false;
            }
        }
        return true;
    }

    //n = quantization values, c = quantization of classes
    public static float jointentropy(int n, Vector predictors, char[][] A, int c) {
        float H = 0;
        //IOFile.PrintMatrix(A);
        RadixSort.radixSort(A, predictors, n); // sorting the samples
        //IOFile.PrintMatrix(A);
        int lines = A.length;
        float pxy = 0;
        for (int j = 0; j < lines; j++) // for each sample
        {
            if (j > 0 && !equalInstances(j, predictors, A)) // next instance?
            {
                pxy /= lines;
                // calculates the entropy of previous instance
                H -= pxy * (Math.log((double) pxy) / Math.log((double) c));
                // reset the conditional probabilities to process the next instance
                pxy = 0;
            }
            pxy++;// counter of observed instances
        }
        pxy /= lines;
        H -= pxy * (Math.log((double) pxy) / Math.log((double) c));

        //debug
        //System.out.println("Entropy = " + H);
        //System.out.print("Predictors: ");
        //for (int pred = 0; pred < predictors.size(); pred++) {
        //    System.out.print(predictors.get(pred) + "  ");
        //}
        //System.out.print("\n\n");
        //fim debug

        return (H);
    }

    // calculates the entropy of the considered instance
    // pydx: conditional probabilities
    // px: probability of the considered index occur
    // type: penalty type (poor_obs or no_obs)
    // alpha: value for penalty
    // lines: number of samples
    // n: number of possible values for features
    // dim: dimension of the feature subspace being evaluated
    // c: number of possible classes
    // q: Tsallis entropy (q-entropy) parameter
    public static float instanceCriterion(float[] pydx, float px,
            String type, float alpha, float beta, int lines,
            int n, int dim, int c, float q) {
        float H = 0;
        // pydx becoming probabilities
        if (type.equals("poor_obs")) {
            if (px == 1) {
                for (int k = 0; k < c; k++) {
                    if (pydx[k] > 0) {
                        pydx[k] = beta;
                    } else {
                        pydx[k] = (1 - beta) / (c - 1);
                    }
                }
            } else {
                for (int k = 0; k < c; k++) {
                    pydx[k] /= px;
                }
            }
            px /= lines;
        } else if (type.equals("no_obs")) {
            for (int k = 0; k < c; k++) {
                pydx[k] /= px;
            }
            // attributing a positive mass of probabilities
            // to non-observed instances
            //notacoes usadas no artigo da BMC Bioinformatics como comentario
            px += alpha;//(fi+alpha)//ORIGINAL
            px /= (lines + alpha * Math.pow((double) n, (double) dim));//(fi+alpha)/(alpha * M + s)//ORIGINAL

            //INICIO-NOVO-FABRICIO-TESTE
            //px = (float)(1 / Math.pow((double) n, (double) dim));
            //FIM-NOVO-FABRICIO-TESTE
        }

        if (q >= 0 && q <= 0.0001) // q == 0 -> COD
        {
            float maxProb = 0;
            for (int k = 0; k < c; k++) {
                if (pydx[k] > maxProb) {
                    maxProb = pydx[k];
                }
            }
            H = px * (1 - maxProb);
            return H;
        } else if (Math.abs(q - 1) >= 0 && Math.abs(q - 1) <= 0.0001) // if (q==1 -> Shannon Entropy)
        {
            H = 0;
        } else // if (Tsallis Entropy for q != 1)
        {
            H = 1;
        }
        for (int k = 0; k < c; k++) {
            if (pydx[k] > 0) {
                if (Math.abs(q - 1) >= 0 && Math.abs(q - 1) <= 0.0001) // if (q==1 -> Shannon Entropy)
                {
                    H -= pydx[k] * (Math.log((double) pydx[k]) / Math.log((double) c));
                } else // if (Tsallis Entropy for q != 1)
                {
                    H -= Math.pow(pydx[k], q);
                }
            }
        }
        if (Math.abs(q - 1) > 0.0001)// if (Tsallis Entropy for q != 1)
        {
            H /= q - 1;
            //ponderacao para recuperar o valor real da entropia na escala utilizada (q).
            //double entropiamaxima = ((1 - c * Math.pow((double) 1 / c, q)) / (q - 1));
            //H /= ((1 - c * Math.pow((double) 1 / c, q)) / (q - 1));//REMOVIDO PARA EXPERIMENTO ARTIGO TSALLIS.
        }
        H *= px;
        return H;
    }

    // calculates the mean conditional entropy or Coedfficient of Determination
    // of a given feature subspace (I) based on training samples (A)
    // type: penalty type (poor_obs or no_obs)
    // alpha: value for penalty
    // n: number of possible values for features
    // c: number of possible classes
    // q: Tsallis entropy (q-entropy) parameter (q = 0 -> COD)
    public static float MCE_COD(String type, float alpha, float beta, int n,
            int c, Vector I, char[][] A, float q) {
        float[] pYdX = new float[c];
        float[] pY = new float[c]; // probability distribution of the classes
        float pX = 0;
        float H = 0;
        float HY = 0;
        int lines = A.length;
        int no_obs = (int) Math.pow(n, I.size()); // number of non-observed
        // instances (initially it is set to number of possible instances)
        RadixSort.radixSort(A, I, n); // sorting the samples
        //IOFile.PrintMatrix(A);
        probtable = new Vector(no_obs);
        for (int comb = 0; comb < no_obs; comb++) {
            probtable.add(comb, new float[c]);
        }

        for (int j = 0; j < lines; j++) // for each sample
        {
            if (j > 0 && !equalInstances(j, I, A)) // next instance?
            {
                no_obs--; // one more observed instance

                int position = getPositionofInstances(j, I, A);
                probtable.setElementAt(pYdX.clone(), position);

                // calculates the entropy of previous instance
                H += instanceCriterion(pYdX, pX, type, alpha, beta, lines, n, I.size(), c, q);
                // reset the conditional probabilities to process the next instance
                for (int k = 0; k < c; k++) {
                    pYdX[k] = 0;
                }
                pX = 0;
            }
            pYdX[A[j][A[j].length - 1]]++;//table of number of observations of
            //the target value given the pattern of the predictors

            pY[A[j][A[j].length - 1]]++;  // counting the number of
            // observations of each class
            pX++;// counter of observed instances
        }
        int position = getPositionofInstances(lines, I, A);
        probtable.setElementAt(pYdX.clone(), position);
        H += instanceCriterion(pYdX, pX, type, alpha, beta, lines, n, I.size(), c, q);
        no_obs--;
        // Calculating the entropy of Y if the criterion is entropy, or
        // the prior error otherwise
        HY = instanceCriterion(pY, lines, "poor_obs", 0, 1, lines, 0, 0, c, q);
        if (type.equals("no_obs") && no_obs > 0) {
            double penalization = (alpha * no_obs * HY) / (lines + alpha * Math.pow(n, I.size()));
            H += penalization;
        }
        if (q >= 0 && q <= 0.00001) // q == 0 -> COD
        {
            return H / HY;
        }
        // mean conditional entropy
        return H;
    }

    // calculates the mean conditional entropy or Coedfficient of Determination
    // of a given feature subspace (I) based on training samples (A)
    // type: penalty type (poor_obs or no_obs)
    // alpha: value for penalty
    // n: number of possible values for features
    // c: number of possible classes
    // q: Tsallis entropy (q-entropy) parameter (q = 0 -> COD)
    public static float MCE_COD_Gensips2011(String type, float alpha, float beta, int n,
            int c, Vector I, char[][] A, float q, AGN goldstandard, int targetindex, float weight) {
        float[] pYdX = new float[c];
        float[] pY = new float[c]; // probability distribution of the classes
        float pX = 0;
        float H = 0;
        float HY = 0;
        int lines = A.length;
        int no_obs = (int) Math.pow(n, I.size()); // number of non-observed
        // instances (initially it is set to number of possible instances)
        RadixSort.radixSort(A, I, n + 1); // sorting the samples
        //IOFile.PrintMatrix(A);
        probtable = new Vector(no_obs);
        for (int comb = 0; comb < no_obs; comb++) {
            probtable.add(comb, new float[c]);
        }

        for (int j = 0; j < lines; j++) // for each sample
        {
            if (isValidValue(j, I, A)) {//descarta todas as observacoes que possuem valor == 3
                if (j > 0 && !equalInstances(j, I, A)) // next instance?
                {
                    no_obs--; // one more observed instance

                    int position = getPositionofInstances(j, I, A);
                    probtable.setElementAt(pYdX.clone(), position);

                    // calculates the entropy of previous instance
                    H += instanceCriterion(pYdX, pX, type, alpha, beta, lines, n, I.size(), c, q);
                    // reset the conditional probabilities to process the next instance
                    for (int k = 0; k < c; k++) {
                        pYdX[k] = 0;
                    }
                    pX = 0;
                }
                pYdX[A[j][A[j].length - 1]]++;//table of number of observations of
                //the target value given the pattern of the predictors

                pY[A[j][A[j].length - 1]]++;  // counting the number of
                // observations of each class
                pX++;// counter of observed instances
            }
        }
        int position = getPositionofInstances(lines, I, A);
        while (position >= probtable.size()) {
            position--;
        }
        probtable.setElementAt(pYdX.clone(), position);
        H += instanceCriterion(pYdX, pX, type, alpha, beta, lines, n, I.size(), c, q);
        no_obs--;
        // Calculating the entropy of Y if the criterion is entropy, or
        // the prior error otherwise
        HY = instanceCriterion(pY, lines, "poor_obs", 0, 1, lines, 0, 0, c, q);
        if (type.equals("no_obs") && no_obs > 0) {
            double penalization = (alpha * no_obs * HY) / (lines + alpha * Math.pow(n, I.size()));
            H += penalization;
        }
        if (q >= 0 && q <= 0.00001) // q == 0 -> COD
        {
            return H / HY;
        }

        float pesoentropia = 1 - weight;
        //float pesopreditor = weight/I.size();
        float pesopreditor = 0;
        if (goldstandard.getGenes()[targetindex].getPredictors().size() > 0) {
            pesopreditor = weight / goldstandard.getGenes()[targetindex].getPredictors().size();
        }

        //float pesoadicionado = 0;
        float pesoadicionado = weight;
        for (int i = 0; i < I.size(); i++) {
            int predictorindex = (Integer) I.get(i);
            //para encontrar o indice real do preditor
            if (predictorindex >= targetindex) {
                predictorindex++;
            }

            //adiciona o peso recebido da informacao biologica, caso o preditor
            //nao faca parte do conjunto resposta contido no gold-standard.
            if (goldstandard.getGenes()[targetindex].getPredictors().contains(predictorindex)) {
                pesoadicionado -= pesopreditor;
                //DEBUG
                //System.out.println("Encontrou um preditor valido. Target: "
                //        + goldstandard.getGenes()[targetindex].getName()
                //        + " predictor: " + goldstandard.getGenes()[predictorindex].getName());
                //FIM-DEBUG   
            }

        }
        H = H * pesoentropia + pesoadicionado;
        // mean conditional entropy
        return H;
    }
}
