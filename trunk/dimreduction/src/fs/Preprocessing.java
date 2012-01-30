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
/*** This class implements preprocessing methods that can be useful such ***/
/*** as normalization, quantization and matrix transposition (if the     ***/
/*** features are disposed by lines instead of collumns)                 ***/
/***************************************************************************/
package fs;

import java.util.Vector;

public class Preprocessing {

    public static final int skipvalue = -999;
    private static float[] minNeg = null;
    private static float[] maxPos = null;
    private static float[] means = null;
    private static float[] stds = null;

    //encontra os valores extremos, devolve o maximo no indice 0 e minimo no indice 1.
    public static void MaxMinColumn(float[][] M, float[] mm, int col) {
        mm[0] = M[0][col];
        mm[1] = M[0][col];
        for (int i = 0; i < M.length; i++) {
            if (M[i][col] > mm[0]) {
                mm[0] = M[i][col];
            }
            if (M[i][col] < mm[1]) {
                mm[1] = M[i][col];
            }
        }
    }

    //encontra os valores extremos, devolve o maximo no indice 0 e minimo no indice 1.
    public static void MaxMinRow(float[][] M, float[] mm, int row, int label) {
        mm[0] = M[row][0];
        mm[1] = M[row][0];
        for (int j = 0; j < M[0].length - label; j++) {
            if (M[row][j] > mm[0]) {
                mm[0] = M[row][j];
            }
            if (M[row][j] < mm[1]) {
                mm[1] = M[row][j];
            }
        }
    }

    //encontra os valores extremos maximo no indice 0 e minimo no indice 1.
    public static void MaxMin(float[][] M, float[] mm) {
        mm[0] = M[0][0];
        mm[1] = M[0][0];
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[0].length; j++) {
                if (M[i][j] > mm[0]) {
                    mm[0] = M[i][j];
                }
                if (M[i][j] < mm[1]) {
                    mm[1] = M[i][j];
                }
            }
        }
    }
    //encontra os valores extremos maximo no indice 0 e minimo no indice 1.

    public static void MaxMin(float[] M, float[] mm) {
        mm[0] = M[0];
        mm[1] = M[0];
        for (int i = 1; i < M.length; i++) {
            if ((int) M[i] != skipvalue) {
                if (M[i] > mm[0]) {
                    mm[0] = M[i];
                }
                if (M[i] < mm[1]) {
                    mm[1] = M[i];
                }
            }
        }
    }

    public static float[][] FilterMA(float[][] expressiondata, Vector[] geneids,
            Vector remaingenes, Vector removedgenes) {
        //remover todas as linhas que apresentem 1 ou mais zeros...missing values e que sao genes controle do array == AFFX
        for (int lin = 0; lin < expressiondata.length; lin++) {
            int contz = 0;
            for (int col = 0; col < expressiondata[0].length; col++) {
                if (expressiondata[lin][col] == 0) {
                    contz++;
                }
            }
            //remove os genes
            if (contz < 1) {// && !((String) geneids[0].get(lin)).startsWith("AFFX")) {
                remaingenes.add(lin);
            } else {
                removedgenes.add(geneids[0].get(lin));
                System.out.println("Gene " + (String) geneids[0].get(lin) + " was removed by filter.");
            }
        }
        System.out.println(removedgenes.size() + " removed genes.");

        //gera a nova matriz de dados, apenas com os dados que passaram pelo filtro.
        float[][] filtereddata = new float[remaingenes.size()][expressiondata[0].length];
        for (int i = 0; i < remaingenes.size(); i++) {
            int lin = (Integer) remaingenes.get(i);
            for (int col = 0; col < expressiondata[0].length; col++) {
                filtereddata[i][col] = expressiondata[lin][col];
            }
        }
        return (filtereddata);
    }

    public static float[][] ApplyLog2(float[][] expressiondata) {
        /*
        //calcula a media e desvio padrao de cada coluna (feature, instante de tempo).
        float[] mean = new float[expressiondata[0].length];
        float[] std = new float[expressiondata[0].length];
        boolean [] zerorows = new boolean[expressiondata.length];
        for (int col = 0; col < expressiondata[0].length; col++) {
        mean[col] = 0;
        int contz = 0;
        for (int row = 0; row < expressiondata.length; row++) {
        if (expressiondata[row][col] == 0) {
        zerorows[row] = true;
        contz++;
        }else{
        mean[col] += expressiondata[row][col];
        }
        }
        mean[col] /= (expressiondata.length-contz);
        std[col] = 0;
        for (int row = 0; row < expressiondata.length; row++) {
        if (!zerorows[row]){
        std[col] += (expressiondata[row][col] - mean[col]) * (expressiondata[row][col] - mean[col]);
        }
        }
        std[col] /= (expressiondata.length-contz-1);
        std[col] = ((Double) Math.sqrt(std[col])).floatValue();
        }*/

        //aplica o LOG2 para suavizar os picos, mantendo as pequenas variacoes.
        float[][] filtereddata = new float[expressiondata.length][expressiondata[0].length];
        for (int row = 0; row < expressiondata.length; row++) {
            for (int col = 0; col < expressiondata[0].length; col++) {
                if (expressiondata[row][col] != skipvalue) {
                    filtereddata[row][col] = ((Double) (Math.log(expressiondata[row][col]) / Math.log(2))).floatValue();
                } else {
                    filtereddata[row][col] = expressiondata[row][col];
                }
            }
        }
        return (filtereddata);
    }

    public static void QuickSort(float[][] M, int inicio, int fim) {
        int i, j;
        float[] aux = new float[2];
        float pivo = M[inicio][0];
        i = inicio;
        j = fim;
        while (i < j) {
            while (M[i][0] <= pivo && i < fim) {
                i++;
            }
            while (M[j][0] >= pivo && j > inicio) {
                j--;
            }
            if (i < j) {
                aux[0] = M[i][0];
                aux[1] = M[i][1];

                M[i][0] = M[j][0];
                M[i][1] = M[j][1];

                M[j][0] = aux[0];
                M[j][1] = aux[1];
            }
        }
        if (inicio != j) {
            aux[0] = M[inicio][0];
            aux[1] = M[inicio][1];

            M[inicio][0] = M[j][0];
            M[inicio][1] = M[j][1];

            M[j][0] = aux[0];
            M[j][1] = aux[1];
        }
        if (inicio < j - 1) {
            QuickSort(M, inicio, j - 1);
        }
        if (fim > j + 1) {
            QuickSort(M, j + 1, fim);
        }
    }

    public static void QuickSort(float[] M, int inicio, int fim) {
        int i, j;
        float aux;
        float pivo = M[inicio];
        i = inicio;
        j = fim;
        while (i < j) {
            while (M[i] <= pivo && i < fim) {
                i++;
            }
            while (M[j] >= pivo && j > inicio) {
                j--;
            }
            if (i < j) {
                aux = M[i];
                M[i] = M[j];
                M[j] = aux;
            }
        }
        if (inicio != j) {
            aux = M[inicio];
            M[inicio] = M[j];
            M[j] = aux;
        }
        if (inicio < j - 1) {
            QuickSort(M, inicio, j - 1);
        }
        if (fim > j + 1) {
            QuickSort(M, j + 1, fim);
        }
    }

    public static void QuickSortASC(float[][] M, int inicio, int fim, int[] index) {
        int i, j, k;
        float[] aux = new float[M[0].length];
        float[] pivo = new float[index.length];

        for (i = 0; i < index.length; i++) {
            pivo[i] = M[inicio][index[i]];
        }
        i = inicio;
        j = fim;

        while (i < j) {
            while (M[i][index[1]] <= pivo[1] && i < fim) {
                if (M[i][index[1]] == pivo[1]) {
                    if (M[i][index[0]] <= pivo[0]) {
                        i++;
                    } else {
                        break;
                    }
                } else {
                    i++;
                }
            }

            while (M[j][index[1]] >= pivo[1] && j > inicio) {
                if (M[j][index[1]] == pivo[1]) {
                    if (M[j][index[0]] >= pivo[0]) {
                        j--;
                    } else {
                        break;
                    }
                } else {
                    j--;
                }
            }

            if (i < j) {
                //faz a troca
                for (k = 0; k < M[0].length; k++) {
                    aux[k] = M[i][k];
                }
                for (k = 0; k < M[0].length; k++) {
                    M[i][k] = M[j][k];
                }
                for (k = 0; k < M[0].length; k++) {
                    M[j][k] = aux[k];
                    //i++;
                    //j--;
                }
            }
        }

        if (inicio != j) {
            for (k = 0; k < M[0].length; k++) {
                aux[k] = M[inicio][k];
            }
            for (k = 0; k < M[0].length; k++) {
                M[inicio][k] = M[j][k];
            }
            for (k = 0; k < M[0].length; k++) {
                M[j][k] = aux[k];            //aux = M[inicio];
                //M[inicio] = M[j];
                //M[j] = aux;
            }
        }
        if (inicio < j - 1) {
            QuickSortASC(M, inicio, j - 1, index);
        }
        if (fim > j + 1) {
            QuickSortASC(M, j + 1, fim, index);
        }
    }

    public static void QuickSortDSC(Vector M, int inicio, int fim, int[] index) {
        int i, j;
        Vector aux;
        double[] pivo = new double[index.length];

        for (i = 0; i < index.length; i++) {
            pivo[i] = ((double[]) ((Vector) M.get(inicio)).get(0))[index[i]];
        }
        i = inicio;
        j = fim;

        while (i < j) {
            while (((double[]) ((Vector) M.get(i)).get(0))[index[0]] <= pivo[0] && i < fim) {
                if (((double[]) ((Vector) M.get(i)).get(0))[index[0]] == pivo[0]) {
                    if (((double[]) ((Vector) M.get(i)).get(0))[index[1]] >= pivo[1]) {
                        i++;
                    } else {
                        break;
                    }
                } else {
                    i++;
                }
            }

            while (((double[]) ((Vector) M.get(j)).get(0))[index[0]] >= pivo[0] && j > inicio) {
                if (((double[]) ((Vector) M.get(j)).get(0))[index[0]] == pivo[0]) {
                    if (((double[]) ((Vector) M.get(j)).get(0))[index[1]] <= pivo[1]) {
                        j--;
                    } else {
                        break;
                    }
                } else {
                    j--;
                }
            }

            if (i < j) {
                aux = (Vector) M.get(i);
                M.set(i, M.get(j));
                M.set(j, aux);
            }
        }

        if (inicio != j) {
            aux = (Vector) M.get(inicio);
            M.set(inicio, M.get(j));
            M.set(j, aux);
        }
        if (inicio < j - 1) {
            QuickSortDSC(M, inicio, j - 1, index);
        }
        if (fim > j + 1) {
            QuickSortDSC(M, j + 1, fim, index);
        }
    }

    public static void SelectionSortint(Vector M) {
        int aux;
        int minvalue = 0;
        int minposition = 0;
        for (int j = 0; j < M.size() - 1; j++) {
            minvalue = (Integer) M.get(j);
            minposition = j;
            for (int i = j + 1; i < M.size(); i++) {
                if ((Integer) M.get(i) < minvalue) {
                    minvalue = (Integer) M.get(i);
                    minposition = i;
                }
            }
            if (minposition != j) {
                aux = (Integer) M.get(j);
                M.set(j, M.get(minposition));
                M.set(minposition, aux);
            }
        }
    }

    public static void SelectionSort(Vector<Vector> M) {
        Vector aux;
        float minvalue = 0;
        int minposition = 0;
        for (int j = 0; j < M.size() - 1; j++) {
            minvalue = (Float) M.get(j).get(0);
            minposition = j;
            for (int i = j + 1; i < M.size(); i++) {
                if ((Float) M.get(i).get(0) < minvalue) {
                    minvalue = (Float) M.get(i).get(0);
                    minposition = i;
                }
            }
            if (minposition != j) {
                aux = M.get(j);
                M.set(j, M.get(minposition));
                M.set(minposition, aux);
            }
        }
    }

    public static void SelectionSort(Vector<Vector> M, int index) {
        Vector aux;
        Integer minvalue = 0;
        int minposition = 0;
        for (int j = 0; j < M.size() - 1; j++) {
            minvalue = (Integer) M.get(j).get(index);
            minposition = j;
            for (int i = j + 1; i < M.size(); i++) {
                if ((Integer) M.get(i).get(index) < minvalue) {
                    minvalue = (Integer) M.get(i).get(index);
                    minposition = i;
                }
            }
            if (minposition != j) {
                aux = M.get(j);
                M.set(j, M.get(minposition));
                M.set(minposition, aux);
            }
        }
    }

    public static void BubbleSort(Vector<Vector> M) {
        Vector aux;
        boolean change = true;
        while (change) {
            change = false;
            for (int i = 0; i < M.size() - 1; i++) {
                if ((Double) M.get(i).get(0) > (Double) M.get(i + 1).get(0)) {
                    aux = M.get(i);
                    M.set(i, M.get(i + 1));
                    M.set(i + 1, aux);
                    change = true;
                }
            }
        }
    }

    //return ordened index in its return and ordered values in the input parameter
    public static int[] BubbleSortDEC(int[] values) {
        boolean change = true;
        int aux;
        int[] indexes = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            indexes[i] = i;
        }
        while (change) {
            change = false;
            for (int i = 0; i < values.length - 1; i++) {
                if (values[i] < values[i + 1]) {
                    //change values order
                    aux = values[i];
                    values[i] = values[i + 1];
                    values[i + 1] = aux;

                    //change indexes order
                    aux = indexes[i];
                    indexes[i] = indexes[i + 1];
                    indexes[i + 1] = aux;

                    change = true;
                }
            }
        }
        return (indexes);
    }

    public static void QuickSort(Vector<Vector> M, int inicio, int fim) {
        int i, j;
        Vector aux;
        float pivo = (Float) M.get(inicio).get(0);

        i = inicio;
        j = fim;

        while (i < j) {
            while ((Float) M.get(i).get(0) <= pivo && i < fim) {
                i++;
            }
            while ((Float) M.get(j).get(0) >= pivo && j > inicio) {
                j--;
            }
            if (i < j) {
                aux = M.get(i);
                M.set(i, M.get(j));
                M.set(j, aux);
            }
        }

        if (inicio != j) {
            aux = M.get(inicio);
            M.set(inicio, M.get(j));
            M.set(j, aux);
        }
        if (inicio < j - 1) {
            QuickSort(M, inicio, j - 1);
        }
        if (fim > j + 1) {
            QuickSort(M, j + 1, fim);
        }
    }

    public static void QuickSortASC(Vector M, int inicio, int fim) {
        int i, j;
        double aux;
        double pivo = (Double) M.get(inicio);

        i = inicio;
        j = fim;

        while (i < j) {
            while ((Double) M.get(i) <= pivo && i < fim) {
                i++;
            }
            while ((Double) M.get(j) >= pivo && j > inicio) {
                j--;
            }
            if (i < j) {
                aux = (Double) M.get(i);
                M.set(i, M.get(j));
                M.set(j, aux);
            }
        }

        if (inicio != j) {
            aux = (Double) M.get(inicio);
            M.set(inicio, M.get(j));
            M.set(j, aux);
        }
        if (inicio < j - 1) {
            QuickSortASC(M, inicio, j - 1);
        }
        if (fim > j + 1) {
            QuickSortASC(M, j + 1, fim);
        }
    }

    public static StringBuffer MakeResultList(float[][] R, Vector ordenado) {
        int[] I = new int[2];
        I[0] = 0;
        I[1] = 2;
        //IOFile.PrintMatrix(R);
        QuickSortASC(R, 0, R.length - 1, I); // sorting the samples
        //IOFile.PrintMatrix(R);
        int i = 0;
        int maxc = 0;
        while (i < R.length) {
            Vector preditos = new Vector();
            preditos.add((int) R[i][1]);
            int preditor = (int) R[i][0];
            float entropia = (float) R[i][2];
            int count = 1;
            i++;
            while (i < R.length && R[i][0] == preditor && Math.abs(R[i][2] - entropia) < 0.001) {
                preditos.add((int) R[i][1]);
                count++;
                i++;
            }
            if (preditos.size() > maxc) {
                maxc = preditos.size();
            }
            Vector item = new Vector();
            item.add(new double[]{preditor, entropia, count});
            item.add(preditos);
            ordenado.add(item);
        }

        I[0] = 1;
        I[1] = 2;
        QuickSortDSC(ordenado, 0, ordenado.size() - 1, I); // sorting the samples
        StringBuffer res = new StringBuffer("predictor\tentropy \tfrequency \n");
        for (i = 0; i < ordenado.size(); i++) {
            Vector item = (Vector) ordenado.get(i);
            double[] item1 = (double[]) item.get(0);

            int preditor = (int) item1[0];
            float entropia = (float) item1[1];
            int count = (int) item1[2];
            res.append(preditor + "\t\t" + entropia + "\t\t" + count + "\n");
        }
        return (res);
    }
    // receives a matrix M and returns the transposition of M (M')

    public static double[][] transpose(double[][] M) {
        int lines = M.length;
        int columns = M[0].length;
        double[][] Mtrans = new double[columns][lines];
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < columns; j++) {
                Mtrans[j][i] = M[i][j];
            }
        }
        return Mtrans;
    }
    //inverte os tempos de expressao, de forma que os targets passem
    //a ser os preditores, i.e. os preditores passem a considerar o valor 
    //do target no instante de tempo posterior.

    public static float[][] InvertColumns(float[][] M) {
        int lines = M.length;
        int columns = M[0].length;
        float[][] MInv = new float[lines][columns];
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < columns; j++) {
                MInv[i][j] = M[i][columns - j - 1];
            }
        }
        return MInv;
    }
    // receives a matrix M and returns a copy of M (copy M)

    public static float[][] copyMatrix(float[][] M) {
        int lines = M.length;
        int columns = M[0].length;
        float[][] cM = new float[lines][columns];
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < columns; j++) {
                cM[i][j] = M[i][j];
            }
        }
        return cM;
    }
    // applies a normal transformation to the matrix M
    //foi adicionado a variavel label, para indicar se os rotulos est�o na 
    //ultima columa (label = 1) ou se nao existe rotulo (label=0).

    public static void normalTransformcolumns(float[][] M, boolean extreme_values,
            int label) {
        int lines = M.length;
        int columns = M[0].length;

        if (extreme_values) {
            means = new float[columns - label];//CLASS LABEL AT THE FINAL COLUMN?
            stds = new float[columns - label];//CLASS LABEL AT THE FINAL COLUMN?
        }// else if (minNeg == null) {
        //    new fs.FSException("Error on applying normal transform.", false);
        //}

        for (int j = 0; j < columns - label; j++) // for each feature
        {
            if (M[0][j] != skipvalue) {
                if (extreme_values) {
                    // calculating the mean of the feature values
                    float sum = 0;
                    for (int i = 0; i < lines; i++) {
                        sum += M[i][j];
                    }
                    means[j] = sum / lines;

                    // calculating the standard deviation of the feature values
                    stds[j] = 0f;
                    for (int i = 0; i < lines; i++) {
                        stds[j] += (M[i][j] - means[j]) * (M[i][j] - means[j]);
                    }
                    stds[j] /= (lines - 1);
                    stds[j] = ((Double) Math.sqrt(stds[j])).floatValue();
                }
                // are the values of the reffered feature the same for all samples?
                if (stds[j] > 0) {
                    // each feature value is subtracted by the mean and is divided
                    // by the standard deviation
                    for (int i = 0; i < lines; i++) {
                        M[i][j] -= means[j];
                        M[i][j] /= stds[j];
                    }
                } else {
                    for (int i = 0; i < lines; i++) {
                        M[i][j] = 0;
                    }
                }
            }
        }
    }
    // applies a normal transformation to the matrix M
    //foi adicionado a variavel label, para indicar se os rotulos est�o na 
    //ultima columa (label = 1) ou se nao existe rotulo (label=0).

    public static void normalTransformlines(float[][] M, boolean extreme_values,
            int label) {
        int lines = M.length;
        int columns = M[0].length;

        if (extreme_values) {
            means = new float[lines];
            stds = new float[lines];
        } else if (minNeg == null) {
            new fs.FSException("Error on applying normal transform.", false);
        }

        for (int j = 0; j < lines; j++) // for each sample
        {
            if (extreme_values) {
                // calculating the mean of the sample values
                double sum = 0;
                for (int i = 0; i < columns - label; i++) {
                    sum += M[j][i];
                }
                means[j] = ((Double) (sum / (columns - label))).floatValue();

                // calculating the standard deviation of the feature values
                stds[j] = 0;
                for (int i = 0; i < columns - label; i++) {
                    stds[j] += (M[j][i] - means[j]) * (M[j][i] - means[j]);
                }
                stds[j] /= (columns - label - 1);
                stds[j] = ((Double) Math.sqrt(((Float) stds[j]).doubleValue())).floatValue();
            }
            // are the values of the reffered feature the same for all samples?
            if (stds[j] > 0) {
                // each feature value is subtracted by the mean and is divided
                // by the standard deviation
                for (int i = 0; i < columns - label; i++) {
                    M[j][i] -= means[j];
                    M[j][i] /= stds[j];
                }
            } else {
                for (int i = 0; i < columns - label; i++) {
                    M[j][i] = 0;
                }
            }
        }
    }

    public static void ScaleColumn(float[][] M, int maxvalue, int label) {
        float[] maxmin = new float[2];
        float normalizedvalue = 0;
        for (int col = 0; col < M[0].length - label; col++) {
            //encontra os valores extremos maximo no indice 0 e minimo no indice 1.
            MaxMinColumn(M, maxmin, col);
            for (int lin = 0; lin < M.length; lin++) {
                normalizedvalue = (maxvalue - 1) * (M[lin][col] - maxmin[1])
                        / (maxmin[0] - maxmin[1]);
                M[lin][col] = normalizedvalue;
            }
        }
    }

    public static void ScaleRow(float[][] M, int maxvalue, int label) {
        float[] maxmin = new float[2];
        float normalizedvalue = 0;
        for (int row = 0; row < M.length; row++) {
            //encontra os valores extremos maximo no indice 0 e minimo no indice 1.
            MaxMinRow(M, maxmin, row, label);
            for (int col = 0; col < M[0].length - label; col++) {
                normalizedvalue = (maxvalue - 1) * (M[row][col] - maxmin[1])
                        / (maxmin[0] - maxmin[1]);
                M[row][col] = normalizedvalue;
            }
        }
    }

    // Given a matrix of double values (M) and a quantization degree (qd), this
    // function normalize (scale) M to integer values of 0 to qd
    //foi adicionado a variavel label, para indicar se os rotulos est�o na 
    //ultima columa (label = 1) ou se nao existe rotulo (label=0).
    public static void normalize(float[][] M, int qd, int label) {
        float[] threshold = new float[qd - 1];
        float[] maxmin = new float[2];
        float normalizedvalue = 0;

        float increment = (qd - 1) / ((float) qd);

        for (int k = 0; k < (qd - 1); k++) {
            threshold[k] = increment;
            increment += increment;
        }

        for (int lin = 0; lin < M.length; lin++) {
            //encontra os valores extremos maximo no indice 0 e minimo no indice 1.
            MaxMin(M[lin], maxmin);
            for (int col = 0; col < M[lin].length - label; col++) {
                normalizedvalue = (qd - 1) * (M[lin][col] - maxmin[1])
                        / (maxmin[0] - maxmin[1]);

                int k = 0;
                for (k = 0; k < (qd - 1); k++) {
                    if (threshold[k] >= normalizedvalue) {
                        break;
                    }
                }
                M[lin][col] = k;
                /*
                // obtaining the thresholds for quantization
                int indThreshold = 0;
                double increment = - maxmin[1] / ((double) qd / 2);
                double [] threshold = new double [qd - 1];
                for (double i = maxmin[1] + increment; i < 0; i += increment, indThreshold++)
                threshold[indThreshold] = i;
                increment = maxmin[0] / ((double) qd / 2);
                indThreshold = qd - 2;
                for (double i = maxmin[0] - increment; i > 0 ; i -= increment, indThreshold--)
                threshold[indThreshold] = i;

                // quantizing the feature values
                int k = 0;
                for (k = 0; k < qd; k++)
                if (threshold[k] >= M[lin][col])
                break;
                M[lin][col] = k;
                 */
            }
        }
    }
    // Given a matrix of double values (M) and a quantization degree (qd), this
    // function quantize M to integer values of 0 to qd - 1
    //foi adicionado a variavel label, para indicar se os rotulos est�o na ultima columa (label = 1) ou se nao existe rotulo (label=0).

    public static void quantizerows(float[][] M, int qd, boolean extreme_values, int label) {
        int lines = M.length;
        int columns = M[0].length;
        normalTransformlines(M, extreme_values, label); // applying a normal transformation to the matrix M

        /*
        if (extreme_values) {
        minNeg = new float[lines];
        maxPos = new float[lines];
        } else if (minNeg == null) {
        new fs.FSException("Error on data quantization.", false);
        }
         *
         */

        //foi adicionado a variavel label, para indicar se os rotulos est�o 
        //na ultima columa (label = 1) ou se nao existe rotulo (label=0).
        for (int j = 0; j < lines; j++) // for each feature
        {
            if (M[0][j] != skipvalue) {
                // retrieving the negative and positive values of the considered
                // feature
                Vector negatives = new Vector();
                Vector positives = new Vector();
                float meanneg = 0;
                float meanpos = 0;
                for (int i = 0; i < columns - label; i++) {
                    if (M[j][i] < 0) {
                        negatives.add(M[j][i]);
                        meanneg += M[j][i];
                    } else {
                        positives.add(M[j][i]);
                        meanpos += M[j][i];
                    }
                }
                meanneg /= negatives.size();
                meanpos /= positives.size();

                /*
                if (extreme_values) {
                // are the values of the reffered feature the same for all
                // samples?
                if (stds[j] == 0) {
                continue;
                // retrieving the smallest negative value
                }

                minNeg[j] = (Float) negatives.elementAt(0);
                for (int i = 1; i < negatives.size(); i++) {
                if (minNeg[j] > (Float) negatives.elementAt(i)) {
                minNeg[j] = (Float) negatives.elementAt(i);                    // retrieving the largest positive value
                }
                }
                maxPos[j] = (Float) positives.elementAt(0);
                for (int i = 1; i < positives.size(); i++) {
                if (maxPos[j] < (Float) positives.elementAt(i)) {
                maxPos[j] = (Float) positives.elementAt(i);
                }
                }
                }
                 *
                 */
                // obtaining the thresholds for quantization
                int indThreshold = 0;
                double increment = -meanneg / ((double) qd / 2);
                double[] threshold = new double[qd - 1];
                for (double i = meanneg + increment; i < 0; i += increment, indThreshold++) {
                    threshold[indThreshold] = i;
                }
                increment = meanpos / ((double) qd / 2);
                indThreshold = qd - 2;
                for (double i = meanpos - increment; i > 0; i -= increment, indThreshold--) {
                    threshold[indThreshold] = i;
                    // quantizing the feature values
                }
                for (int i = 0; i < columns - label; i++) {
                    int k;
                    for (k = 0; k < qd - 1; k++) {
                        if (threshold[k] >= M[j][i]) {
                            break;
                        }
                    }
                    M[j][i] = k;
                }
            }
        }
    }

    // Given a matrix of double values (M) and a quantization degree (qd), this
    // function quantize M to integer values of 0 to qd - 1
    //foi adicionado a variavel label, para indicar se os rotulos est�o na ultima columa (label = 1) ou se nao existe rotulo (label=0).
    public static void quantizecolumns(
            float[][] M,
            int qd,
            boolean extreme_values,
            int label,
            float[] mean,
            float[] std,
            float[] lowthreshold,
            float[] hithreshold) {
        int lines = M.length;
        int columns = M[0].length;
        normalTransformcolumns(M, extreme_values, label); // applying a normal transformation to the matrix M

        std = stds;
        mean = means;

        if (extreme_values) {
            minNeg = new float[columns - label];
            maxPos = new float[columns - label];
        } else if (minNeg == null) {
            new fs.FSException("Error on data quantization.", false);
        }

        //foi adicionado a variavel label, para indicar se os rotulos est�o
        //na ultima columa (label = 1) ou se nao existe rotulo (label=0).
        for (int j = 0; j < columns - label; j++) // for each feature
        {
            if (M[0][j] != skipvalue) {
                // retrieving the negative and positive values of the considered
                // feature
                Vector negatives = new Vector();
                Vector positives = new Vector();
                for (int i = 0; i < lines; i++) {
                    if (M[i][j] < 0) {
                        negatives.add(M[i][j]);
                    } else {
                        positives.add(M[i][j]);
                        /*
                        //ideia: ordenar valores e extrair mediana dos positivos e dos negativos.
                        //percebi que os valores das medianas ficam muito baixos...nao seria representativo se
                        //fossem usados como limiares da quantizacao.
                        QuickSortASC(positives, 0, positives.size()-1);
                        int pos = positives.size()/2;
                        double mp = (Double)positives.get(pos);
                        QuickSortASC(negatives, 0, negatives.size()-1);
                        pos = negatives.size()/2;
                        double mn = (Double)negatives.get(pos);
                         */
                    }
                }
                if (extreme_values) {
                    // are the values of the reffered feature the same for all
                    // samples?
                    if (stds[j] == 0) {
                        continue;
                        // retrieving the smallest negative value
                    }
                    if (negatives.isEmpty() || positives.isEmpty()) {
                        continue;
                    }
                    minNeg[j] = (Float) negatives.elementAt(0);
                    for (int i = 1; i < negatives.size(); i++) {
                        if (minNeg[j] > (Float) negatives.elementAt(i)) {
                            minNeg[j] = (Float) negatives.elementAt(i);                    // retrieving the largest positive value
                        }
                    }
                    maxPos[j] = (Float) positives.elementAt(0);
                    for (int i = 1; i < positives.size(); i++) {
                        if (maxPos[j] < (Float) positives.elementAt(i)) {
                            maxPos[j] = (Float) positives.elementAt(i);
                        }
                    }
                }
                // obtaining the thresholds for quantization
                int indThreshold = 0;
                double increment = -minNeg[j] / ((double) qd / 2);
                double[] threshold = new double[qd - 1];
                for (double i = minNeg[j] + increment; i < 0; i += increment, indThreshold++) {
                    threshold[indThreshold] = i;
                }
                increment = maxPos[j] / ((double) qd / 2);
                indThreshold = qd - 2;
                for (double i = maxPos[j] - increment; i > 0; i -= increment, indThreshold--) {
                    threshold[indThreshold] = i;
                    // quantizing the feature values
                }

                lowthreshold[j] = (float) threshold[0];
                hithreshold[j] = (float) threshold[qd - 2];

                for (int i = 0; i < lines; i++) {
                    int k;
                    for (k = 0; k < qd - 1; k++) {
                        if (threshold[k] >= M[i][j]) {
                            break;
                        }
                    }
                    M[i][j] = k;
                }
            }
        }
    }

    // Given a matrix of double values (M) and a quantization degree (qd), this
    // function quantize M to integer values of 0 to qd - 1
    //foi adicionado a variavel label, para indicar se os rotulos est?o na ultima columa (label = 1) ou se nao existe rotulo (label=0).
    public static void quantizecolumns(float[][] M, int qd, boolean extreme_values, int label) {
        int lines = M.length;
        int columns = M[0].length;
        normalTransformcolumns(M, extreme_values, label); // applying a normal transformation to the matrix M

        if (extreme_values) {
            minNeg = new float[columns - label];
            maxPos = new float[columns - label];
        } else if (minNeg == null) {
            new fs.FSException("Error on data quantization.", false);
        }

        //foi adicionado a variavel label, para indicar se os rotulos est?o
        //na ultima columa (label = 1) ou se nao existe rotulo (label=0).
        for (int j = 0; j < columns - label; j++) // for each feature
        {
            if (M[0][j] != skipvalue) {
                // retrieving the negative and positive values of the considered
                // feature
                Vector negatives = new Vector();
                Vector positives = new Vector();
                for (int i = 0; i < lines; i++) {
                    if (M[i][j] < 0) {
                        negatives.add(M[i][j]);
                    } else {
                        positives.add(M[i][j]);
                        /*
                        //ideia: ordenar valores e extrair mediana dos positivos e dos negativos.
                        //percebi que os valores das medianas ficam muito baixos...nao seria representativo se
                        //fossem usados como limiares da quantizacao.
                        QuickSortASC(positives, 0, positives.size()-1);
                        int pos = positives.size()/2;
                        double mp = (Double)positives.get(pos);
                        QuickSortASC(negatives, 0, negatives.size()-1);
                        pos = negatives.size()/2;
                        double mn = (Double)negatives.get(pos);
                         */
                    }
                }
                if (extreme_values) {
                    // are the values of the reffered feature the same for all
                    // samples?
                    if (stds[j] == 0) {
                        continue;
                        // retrieving the smallest negative value
                    }
                    if (negatives.size() == 0 || positives.size() == 0) {
                        continue;
                    }
                    minNeg[j] = (Float) negatives.elementAt(0);
                    for (int i = 1; i < negatives.size(); i++) {
                        if (minNeg[j] > (Float) negatives.elementAt(i)) {
                            minNeg[j] = (Float) negatives.elementAt(i);                    // retrieving the largest positive value
                        }
                    }
                    maxPos[j] = (Float) positives.elementAt(0);
                    for (int i = 1; i < positives.size(); i++) {
                        if (maxPos[j] < (Float) positives.elementAt(i)) {
                            maxPos[j] = (Float) positives.elementAt(i);
                        }
                    }
                }
                // obtaining the thresholds for quantization
                int indThreshold = 0;
                double increment = -minNeg[j] / ((double) qd / 2);
                double[] threshold = new double[qd - 1];
                for (double i = minNeg[j] + increment; i < 0; i += increment, indThreshold++) {
                    threshold[indThreshold] = i;
                }
                increment = maxPos[j] / ((double) qd / 2);
                indThreshold = qd - 2;
                for (double i = maxPos[j] - increment; i > 0; i -= increment, indThreshold--) {
                    threshold[indThreshold] = i;
                    // quantizing the feature values
                }
                for (int i = 0; i < lines; i++) {
                    int k;
                    for (k = 0; k < qd - 1; k++) {
                        if (threshold[k] >= M[i][j]) {
                            break;
                        }
                    }
                    M[i][j] = k;
                }
            }
        }
    }

    // Given a matrix of double values (M) and a quantization degree (qd), this
    // function quantize M to integer values of 0 to qd - 1
    //foi adicionado a variavel label, para indicar se os rotulos est�o na ultima columa (label = 1) ou se nao existe rotulo (label=0).
    public static void quantizecolumnsavg(
            float[][] M,
            int qd,
            boolean extreme_values,
            int label) {
        int lines = M.length;
        int columns = M[0].length;
        normalTransformcolumns(M, extreme_values, label); // applying a normal transformation to the matrix M
        /*
        if (extreme_values) {
        minNeg = new float[columns - label];
        maxPos = new float[columns - label];
        } else if (minNeg == null) {
        new fs.FSException("Error on data quantization.", false);
        }
         *
         */

        //foi adicionado a variavel label, para indicar se os rotulos est�o
        //na ultima columa (label = 1) ou se nao existe rotulo (label=0).
        for (int j = 0; j < columns - label; j++) // for each feature
        {
            if (M[0][j] != skipvalue) {
                // retrieving the negative and positive values of the considered
                // feature
                Vector negatives = new Vector();
                Vector positives = new Vector();
                float meanneg = 0;
                float meanpos = 0;
                for (int i = 0; i < lines; i++) {
                    if (M[i][j] < 0) {
                        negatives.add(M[i][j]);
                        meanneg += M[i][j];
                    } else {
                        positives.add(M[i][j]);
                        meanpos += M[i][j];
                    }
                }
                meanneg /= negatives.size();
                meanpos /= positives.size();

                /*
                if (extreme_values) {
                // are the values of the reffered feature the same for all
                // samples?
                if (stds[j] == 0) {
                continue;
                // retrieving the smallest negative value
                }
                if (negatives.isEmpty() || positives.isEmpty()) {
                continue;
                }
                /* REMOVED. NOW CONSIDERING THE AVERAGES.
                minNeg[j] = (Float) negatives.elementAt(0);
                for (int i = 1; i < negatives.size(); i++) {
                if (minNeg[j] > (Float) negatives.elementAt(i)) {
                minNeg[j] = (Float) negatives.elementAt(i);                    // retrieving the largest positive value
                }
                }
                maxPos[j] = (Float) positives.elementAt(0);
                for (int i = 1; i < positives.size(); i++) {
                if (maxPos[j] < (Float) positives.elementAt(i)) {
                maxPos[j] = (Float) positives.elementAt(i);
                }
                }
                }*/

                // obtaining the thresholds for quantization
                int indThreshold = 0;
                double increment = -meanneg / ((double) qd / 2);
                double[] threshold = new double[qd - 1];
                for (double i = meanneg + increment; i < 0; i += increment, indThreshold++) {
                    threshold[indThreshold] = i;
                }
                increment = meanpos / ((double) qd / 2);
                indThreshold = qd - 2;
                for (double i = meanpos - increment; i > 0; i -= increment, indThreshold--) {
                    threshold[indThreshold] = i;
                    // quantizing the feature values
                }
                for (int i = 0; i < lines; i++) {
                    int k;
                    for (k = 0; k < qd - 1; k++) {
                        if (threshold[k] >= M[i][j]) {
                            break;
                        }
                    }
                    M[i][j] = k;
                }
            }
        }
    }

    public static float[][] quantizecolumnsMAnormal(
            float[][] M,
            int[][] quantizeddata,
            int qd,
            float[] mean,
            float[] std,
            float[] lowthreshold,
            float[] hithreshold) {

        int totalrows = M.length;
        int totalcols = M[0].length;
        //int[][] quantizeddata = new int[totalrows][totalcols];
        float[][] auxM = Preprocessing.copyMatrix(M);//copy of matrix
        normalTransformcolumns(auxM, true, 0); // applying a normal transformation to the matrix M
        minNeg = new float[totalcols];
        maxPos = new float[totalcols];

        //foi adicionado a variavel label, para indicar se os rotulos est�o
        //na ultima columa (label = 1) ou se nao existe rotulo (label=0).
        for (int col = 0; col < totalcols; col++) // for each feature
        {
            if (M[0][col] != skipvalue) {
                // calculating the mean of the feature values
                float sum = 0;
                float[] colvalues = new float[totalrows];
                for (int row = 0; row < totalrows; row++) {
                    sum += M[row][col];
                    colvalues[row] = auxM[row][col];
                }
                mean[col] = sum / totalrows;
                std[col] = 0;
                for (int row = 0; row < totalrows; row++) {
                    std[col] += (M[row][col] - mean[col]) * (M[row][col] - mean[col]);
                }
                std[col] /= (totalrows - 1);
                std[col] = ((Double) Math.sqrt(std[col])).floatValue();

                // retrieving the negative and positive values of the considered feature
                Vector negatives = new Vector();
                Vector positives = new Vector();
                float meanpos = 0;
                float meanneg = 0;
                float meantotal = 0;
                for (int i = 0; i < totalrows; i++) {
                    if (auxM[i][col] < 0) {
                        negatives.add(auxM[i][col]);
                        meanneg += auxM[i][col];
                    } else {
                        positives.add(auxM[i][col]);
                        meanpos += auxM[i][col];
                    }
                    meantotal += auxM[i][col];
                }
                // are the values of the refered feature the same for all samples?
                if (std[col] == 0) {
                    continue;
                    // retrieving the smallest negative value
                }
                if (negatives.isEmpty() || positives.isEmpty()) {
                    continue;
                }
                meanneg /= negatives.size();
                meanpos /= positives.size();
                meantotal /= totalrows;

                minNeg[col] = (Float) negatives.elementAt(0);
                for (int i = 1; i < negatives.size(); i++) {
                    if (minNeg[col] > (Float) negatives.elementAt(i)) {
                        minNeg[col] = (Float) negatives.elementAt(i);// retrieving the largest positive value
                    }
                }
                maxPos[col] = (Float) positives.elementAt(0);
                for (int i = 1; i < positives.size(); i++) {
                    if (maxPos[col] < (Float) positives.elementAt(i)) {
                        maxPos[col] = (Float) positives.elementAt(i);
                    }
                }

                // obtaining the thresholds for quantization
                int indThreshold = 0;
                float increment = -minNeg[col] / ((float) qd / 2);
                float[] threshold = new float[qd - 1];
                for (float i = minNeg[col] + increment; i < 0; i += increment, indThreshold++) {
                    threshold[indThreshold] = i;
                }
                increment = maxPos[col] / ((float) qd / 2);
                indThreshold = qd - 2;
                for (float i = maxPos[col] - increment; i > 0; i -= increment, indThreshold--) {
                    threshold[indThreshold] = i;
                    // quantizing the feature values
                }

                if (threshold.length > 1) {
                    lowthreshold[col] = threshold[0];
                    hithreshold[col] = threshold[1];
                } else {
                    hithreshold[col] = threshold[0];
                }

                for (int i = 0; i < totalrows; i++) {
                    int k;
                    for (k = 0; k < qd - 1; k++) {
                        if (threshold[k] >= auxM[i][col]) {
                            break;
                        }
                    }
                    //dados Marie-Anne
                    if (k == 2 || k == 0) {
                        k = 1;
                    } else if (k == 1) {
                        k = 0;
                    }
                    quantizeddata[i][col] = k;
                }
                //System.out.println("Totais quantizados na coluna " + col + ": " + (count0 + count1));
                //System.out.println("zeros = " + count0);
                //System.out.println("ums = " + count1);
                //System.out.println();
            } else {
                for (int i = 0; i < totalrows; i++) {
                    quantizeddata[i][col] = (int) M[i][col];
                }
            }
        }
        return (auxM);
    }

    public static float[][] quantizecolumnsErika(
            float[][] M,
            int[][] quantizeddata,
            int qd,
            float[] mean,
            float[] std,
            float[] lowthreshold,
            float[] hithreshold) {

        int totalrows = M.length;
        int totalcols = M[0].length;
        //int[][] quantizeddata = new int[totalrows][totalcols];
        float[][] auxM = Preprocessing.copyMatrix(M);//copy of matrix
        //normalTransformcolumns(auxM, true, 0); // applying a normal transformation to the matrix M
        minNeg = new float[totalcols];
        maxPos = new float[totalcols];

        //foi adicionado a variavel label, para indicar se os rotulos est�o
        //na ultima columa (label = 1) ou se nao existe rotulo (label=0).
        for (int col = 0; col < totalcols; col++) // for each feature
        {
            if (M[0][col] != skipvalue) {
                // calculating the mean of the feature values
                float sum = 0;
                float[] colvalues = new float[totalrows];
                for (int row = 0; row < totalrows; row++) {
                    sum += M[row][col];
                    colvalues[row] = auxM[row][col];
                }
                mean[col] = sum / totalrows;
                std[col] = 0;
                for (int row = 0; row < totalrows; row++) {
                    std[col] += (M[row][col] - mean[col]) * (M[row][col] - mean[col]);
                }
                std[col] /= (totalrows - 1);
                std[col] = ((Double) Math.sqrt(std[col])).floatValue();

                // retrieving the negative and positive values of the considered feature
                Vector negatives = new Vector();
                Vector positives = new Vector();
                float meanpos = 0;
                float meanneg = 0;
                float meantotal = 0;
                for (int i = 0; i < totalrows; i++) {
                    if (auxM[i][col] < 0) {
                        negatives.add(auxM[i][col]);
                        meanneg += auxM[i][col];
                    } else {
                        positives.add(auxM[i][col]);
                        meanpos += auxM[i][col];
                    }
                    meantotal += auxM[i][col];
                }
                // are the values of the refered feature the same for all samples?
                if (std[col] == 0) {
                    continue;
                    // retrieving the smallest negative value
                }
                //if (negatives.isEmpty() || positives.isEmpty()) {
                //    continue;
                //}
                //meanneg /= negatives.size();
                meanpos /= positives.size();
                meantotal /= totalrows;

                /*
                minNeg[col] = (Float) negatives.elementAt(0);
                for (int i = 1; i < negatives.size(); i++) {
                if (minNeg[col] > (Float) negatives.elementAt(i)) {
                minNeg[col] = (Float) negatives.elementAt(i);// retrieving the largest positive value
                }
                }
                 * 
                 */
                maxPos[col] = (Float) positives.elementAt(0);
                for (int i = 1; i < positives.size(); i++) {
                    if (maxPos[col] < (Float) positives.elementAt(i)) {
                        maxPos[col] = (Float) positives.elementAt(i);
                    }
                }


                float[] threshold = new float[qd - 1];
                threshold[0] = meanpos;
                /*
                // obtaining the thresholds for quantization
                int indThreshold = 0;
                float increment = -minNeg[col] / ((float) qd / 2);
                for (float i = minNeg[col] + increment; i < 0; i += increment, indThreshold++) {
                threshold[indThreshold] = i;
                }
                increment = maxPos[col] / ((float) qd / 2);
                indThreshold = qd - 2;
                for (float i = maxPos[col] - increment; i > 0; i -= increment, indThreshold--) {
                threshold[indThreshold] = i;
                // quantizing the feature values
                }
                 *
                 */
                if (threshold.length > 1) {
                    lowthreshold[col] = threshold[0];
                    hithreshold[col] = threshold[1];
                } else {
                    hithreshold[col] = threshold[0];
                }

                int count0 = 0;
                int count1 = 0;
                for (int i = 0; i < totalrows; i++) {
                    int k;
                    for (k = 0; k < qd - 1; k++) {
                        if (threshold[k] >= auxM[i][col]) {
                            break;
                        }
                    }
                    /*//dados Marie-Anne
                    if (k == 2 || k == 0) {
                    k = 1;
                    } else if (k == 1) {
                    k = 0;
                    }*/
                    quantizeddata[i][col] = k;
                    if (k == 0) {
                        count0++;
                    } else if (k == 1) {
                        count1++;
                    }
                }
                System.out.println("Totais quantizados na coluna " + col + ": " + (count0 + count1));
                System.out.println("zeros = " + count0);
                System.out.println("ums = " + count1);
                System.out.println();
            } else {
                for (int i = 0; i < totalrows; i++) {
                    quantizeddata[i][col] = (int) M[i][col];
                }
            }
        }
        return (auxM);
    }

    /*  public static void main (String [] args) throws IOException{
    double [][] M = ReadFile.readMatrixDouble(args[0]);
    int qd = Integer.parseInt(args[1]);
    int lines = M.length;
    int collumns = M[0].length;
    quantize(M,qd);
    for (int i = 0; i < lines; i++)
    {
    for (int j = 0; j < collumns; j++)
    System.out.print(M[i][j]+" ");
    System.out.println();
    }
    } */
}
