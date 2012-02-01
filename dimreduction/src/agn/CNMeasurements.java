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
/*** Contact: David Corrï¿½a Martins Junior - davidjr@vision.ime.usp.br    ***/
/***          Fabrï¿½cio Martins Lopes - fabriciolopes@vision.ime.usp.br   ***/
/***          Roberto Marcondes Cesar Junior - cesar@vision.ime.usp.br   ***/
/***************************************************************************/
/***************************************************************************/
/*** This class implements complex networks measurements.                ***/
/***************************************************************************/
package agn;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class CNMeasurements {

    public CNMeasurements() {
    }

    public static void FindCycle(float[][] M) {
        int rows = M.length;
        int cols = M[0].length;
        List<Integer> col1 = new ArrayList<Integer>();
        List<String> col2 = new ArrayList<String>();
        List<StringBuffer> features = new ArrayList<StringBuffer>();
        int nrcycles = 0;

        for (int i = 0; i < cols; i++) {
            //carrega a string composta pelos elementos de cada coluna.
            StringBuffer str = new StringBuffer();
            for (int j = 0; j < rows; j++) {
                str.append((int) M[j][i]);
            }
            //busca sequencial por uma string equivalente que ja foi lida.
            for (int k = 0; k < features.size(); k++) {
                if (features.get(k).toString().equals(str.toString())) {
                    col1.add((i + k));
                    col2.add(String.format("%5d and %5d", k, i));
                    nrcycles++;
                }
            }
            features.add(str);
        }
        if (nrcycles == 0) {
            JOptionPane.showMessageDialog(null, "Cycles had not been found.", "Application Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String[] colunas = {"Size", "Between columns"};
            DefaultTableModel model = new DefaultTableModel(col1.size(), colunas.length) {
                // necessário para ordenar as colunas corretamente. 
                // diz que a primeira coluna tem valores inteiros.

                @Override
                public Class<?> getColumnClass(int column) {
                    if (column == 0) {
                        return Integer.class;
                    } else {
                        return super.getColumnClass(column);
                    }
                }

                // não poder editar as células do JTable
                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }
            };

            model.setColumnIdentifiers(colunas);

            for (int i = 0; i < nrcycles; i++) {
                model.setValueAt(col1.get(i), i, 0);
                model.setValueAt(col2.get(i), i, 1);
            }
            new CicleWindow(model);
        }
    }

    public static boolean hasVariation(char[][] M) {//, boolean isperiodic) {
        boolean change = false;
        int lastcol = M[0].length - 1;
        //verifica se ha variacao na ultima coluna.
        for (int row = 0; row < M.length - 1 && !change; row++) {
            if (M[row][lastcol] != M[row + 1][lastcol]) {
                change = true;
            }
        }
        //if (isperiodic) {
        //    if (M[M.length - 2][lastcol] != M[M.length - 1][lastcol]) {
        //        change = true;
        //    }
        //}
        return change;
    }

    public static boolean hasVariation(char[][] M, int col) {
        boolean change = false;
        if (col > M[0].length - 1) {
            return false;
        }
        //verifica se ha variacao na ultima coluna.
        for (int row = 0; row < M.length - 1 && !change; row++) {
            if (M[row][col] != M[row + 1][col]) {
                change = true;
            }
        }
        //if (isperiodic) {
        //    if (M[M.length - 2][lastcol] != M[M.length - 1][lastcol]) {
        //        change = true;
        //    }
        //}
        return change;
    }

    //Euclidean distance between two vertices or genes. Used by Geographical Networks
    public static double EuclideanDistance(Gene g1, Gene g2) {
        double d = Math.sqrt(Math.pow(g1.getX() - g2.getX(), 2)
                + Math.pow(g1.getY() - g2.getY(), 2));
        return d;
    }

    //Euclidean distance between two vertices or genes. Used by Geographical Networks
    public static double EuclideanDistance(int x1, int x2, int y1, int y2) {
        double d = Math.sqrt(Math.pow(x1 - x2, 2)
                + Math.pow(y1 - y2, 2));
        return d;
    }

    //City-block distance between two vertices or genes. Used by Geographical Networks
    public static double CityblockDistance(Gene g1, Gene g2) {
        double d = Math.abs(g1.getX() - g2.getX()) + Math.abs(g1.getY() - g2.getY());
        return d;
    }

    //Method that receive a vector with network rules and return the adjacency matrix.
    public static int[][] AdjacencyMatrix(AGN agn, float threshold) {
        int nrnodes = agn.getNrgenes();
        int[][] am = new int[nrnodes][nrnodes];
        for (int gt = 0; gt < nrnodes; gt++) {
            Gene target = agn.getGenes()[gt];
            for (int p = 0; p < target.getPredictors().size(); p++) {
                int predictor = (Integer) target.getPredictors().get(p);
                if (target.getCfvalues().size() > 0) {
                    //verifica o valor atribuido pela funcao criterio.
                    //se este valor for menor que o threshold, atribui a aresta.
                    float cfvalue = (Float) target.getCfvalues().get(p);
                    if (cfvalue <= threshold) {
                        am[predictor][gt] = 1;
                    } else {
                        am[predictor][gt] = 0;
                    }
                } else {
                    //nao existe valores atribuidos pela funcao criterio.
                    am[predictor][gt] = 1;
                }
            }
        }
        return (am);
    }

    //Method that receive a vector with network rules and return the adjacency matrix.
    //acumulating the frequency on the received matrix.
    public static void AdjacencyMatrixTies(AGN agn, int[][] am,
            float threshold) {
        int nrnodes = agn.getNrgenes();
        for (int gt = 0; gt < nrnodes; gt++) {
            Gene target = agn.getGenes()[gt];
            //include each predictor to adjacency matriz.
            for (int p = 0; p < target.getPredictors().size(); p++) {
                int predictor = (Integer) target.getPredictors().get(p);
                if (target.getCfvalues().size() > 0) {
                    //verifica o valor atribuido pela funcao criterio.
                    //se este valor for menor que o threshold, atribui a aresta.
                    float cfvalue = (Float) target.getCfvalues().get(p);
                    if (cfvalue < threshold) {
                        am[predictor][gt]++;
                    }
                } else {
                    //nao existe valores atribuidos pela funcao criterio.
                    am[predictor][gt]++;
                }
            }
            //include each predictor tied to adjacency matriz.
            if (target.getPredictorsties() != null) {
                for (int t = 0; t < target.getPredictorsties().length; t++) {
                    Vector tie = target.getPredictorsties()[t];
                    for (int p = 0; p < tie.size(); p++) {
                        int predictor = (Integer) tie.get(p);
                        if (target.getCfvalues().size() > 0) {
                            //verifica o valor atribuido pela funcao criterio.
                            //se este valor for menor que o threshold, atribui a aresta.
                            float cfvalue = (Float) target.getCfvalues().get(p);
                            if (cfvalue < threshold) {
                                am[predictor][gt]++;
                            }
                        } else {
                            //nao existe valores atribuidos pela funcao criterio.
                            am[predictor][gt]++;
                        }
                    }
                }
            }
        }
    }

    public static float[] AverageDegrees(Gene[] genes) {
        float[] in_out_degree = new float[2];
        int numnodes = genes.length;
        for (int i = 0; i < numnodes; i++) {
            in_out_degree[0] += genes[i].getPredictors().size();//in-degree
            in_out_degree[1] += genes[i].getTargets().size();//out-degree
        }
        in_out_degree[0] = in_out_degree[0] / numnodes;
        in_out_degree[1] = in_out_degree[1] / numnodes;
        return (in_out_degree);
    }

    public static void addInOutDegrees(AGN network, int[] indegrees, int[] outdegrees) {
        for (int i = 0; i < network.getGenes().length; i++) {
            int indegree = network.getGenes()[i].getPredictors().size();
            int outdegree = network.getGenes()[i].getTargets().size();
            indegrees[indegree]++;
            outdegrees[outdegree]++;
        }
    }

    public static int getMaxDegree(Gene[] genes) {
        int maxd = 0;
        for (int i = 0; i < genes.length; i++) {
            int degree = genes[i].getPredictors().size() + genes[i].getTargets().size();
            if (degree > maxd) {
                maxd = degree;
            }
        }
        return (maxd);
    }

    public static int getMaxInDegree(Gene[] genes) {
        int maxd = 0;
        for (int i = 0; i < genes.length; i++) {
            int degree = genes[i].getPredictors().size();
            if (degree > maxd) {
                maxd = degree;
            }
        }
        return (maxd);
    }

    public static int getMaxOutDegree(Gene[] genes) {
        int maxd = 0;
        for (int i = 0; i < genes.length; i++) {
            int degree = genes[i].getTargets().size();
            if (degree > maxd) {
                maxd = degree;
            }
        }
        return (maxd);
    }

    public static int[] Grade(int[] indegrees, int[] outdegrees, int node) {
        //grau do nï¿½, considereando as posicoes, 0 = in-degree, 1 = out-degree.
        int[] grades = new int[2];
        if (node < indegrees.length && node < outdegrees.length) {
            grades[0] = indegrees[node];
            grades[1] = outdegrees[node];
            return (grades);
        } else {
            return (null);
        }
    }

    public static float[] Average(int[] indegrees, int[] outdegrees) {
        float[] sum = new float[2];
        int numnodes = indegrees.length;
        if (indegrees.length == outdegrees.length) {
            for (int i = 0; i < numnodes; i++) {
                sum[0] += indegrees[i];
                sum[1] += outdegrees[i];
            }
            sum[0] = sum[0] / numnodes;
            sum[1] = sum[1] / numnodes;
            return (sum);
        } else {
            return (null);
        }
    }

    public static double[] StdDeviation(int[] indegrees, int[] outdegrees) {
        float[] avg = Average(indegrees, outdegrees);
        double[] sum = new double[2];
        int numnodes = indegrees.length;
        if (avg != null) {
            for (int i = 0; i < numnodes; i++) {
                sum[0] += Math.pow((indegrees[i] - avg[0]), 2);
                sum[1] += Math.pow((outdegrees[i] - avg[1]), 2);
            }
            sum[0] = sum[0] / numnodes;
            sum[1] = sum[1] / numnodes;

            sum[0] = Math.sqrt(sum[0]);
            sum[1] = Math.sqrt(sum[1]);
            return (sum);
        } else {
            return (null);
        }
    }


    /* Metodo usado comparar as redes original e recuperada.*/
    public static float[] ConfusionMatrix(AGN agn, AGN recovered, float threshold) {
        //vector to save in its positions:
        //0  == True Positives
        //1  == False Positives
        //2  == True Negatives
        //3  == False Negatives
        //4  == PPV
        //5  == SENSITIVITY
        //6  == SPECIFICITY
        //7  == SIMILARITY
        //8  == True Positives, just considering hubs
        //9  == False Positives, just considering hubs
        //10 == True Negatives, just considering hubs
        //11 == False Negatives, just considering hubs
        //12 == PPV, just considering hubs
        //13 == SENSITIVITY, just considering hubs
        //14 == SPECIFICITY, just considering hubs
        //15 == SIMILARITY, just considering hubs
        float[] CM = new float[16];
        int[][] automat = AdjacencyMatrix(agn, threshold);
        int[][] recmat = AdjacencyMatrix(recovered, threshold);
        int[] hubindexes = AGNRoutines.FindHubs(agn);

        //DEBUG
        //IOFile.PrintMatrix(automat);
        //IOFile.PrintMatrix(recmat);
        //END-DEBUG
        for (int i = 0; i < agn.getNrgenes(); i++) {
            for (int j = 0; j < agn.getNrgenes(); j++) {
                //NAO SAO CONSIDERADOS AUTO-RELACIONAMENTOS
                if (i != j) {
                    if (automat[i][j] == 0) {
                        //z1++;
                        if (automat[i][j] == recmat[i][j]) {
                            CM[2]++;//true negative
                        } else {
                            CM[1]++;//false positive
                        }
                    } else if (automat[i][j] == 1) {
                        //u1++;
                        if (automat[i][j] == recmat[i][j]) {
                            CM[0]++;//true positive
                        } else {
                            CM[3]++;//false negative
                        }
                    }
                }

            }
        }
        if ((CM[0] + CM[1]) > 0) {
            CM[4] = (CM[0] / (CM[0] + CM[1]));//PPV = TP / (TP + FP)
        }
        if ((CM[0] + CM[3]) > 0) {
            CM[5] = (CM[0] / (CM[0] + CM[3]));//SENSITIVITY = TP / (TP + FN)
        }
        if ((CM[2] + CM[1]) > 0) {
            CM[6] = (CM[2] / (CM[2] + CM[1]));//SPECIFICITY = TN / (TN + FP)
        }
        CM[7] = (float) Math.pow(CM[4] * CM[5] * CM[6], 1f / 3f);
        System.out.println("\n\nresult PPV = " + CM[4] + " ,SENSITIVITY = " + CM[5] + " ,SPECIFICITY= " + CM[6] + " ,SIMILARITY= " + CM[7]);

        for (int hi = 0; hi < hubindexes.length; hi++) {
            int ii = hubindexes[hi];//para cada hub
            //percorre todas as colunas (o indice ii eh um preditor)
            for (int j = 0; j < agn.getNrgenes(); j++) {
                if (automat[ii][j] == 0) {
                    if (automat[ii][j] == recmat[ii][j]) {
                        CM[10]++;//true negative, just considering hubs
                    } else {
                        CM[9]++;//false positive, just considering hubs
                    }
                } else if (automat[ii][j] == 1) {
                    if (automat[ii][j] == recmat[ii][j]) {
                        CM[8]++;//true positive, just considering hubs
                    } else {
                        CM[11]++;//false negative, just considering hubs
                    }
                }
            }

            //percorre todas as linhas (o indice ii eh um target)
            for (int j = 0; j < agn.getNrgenes(); j++) {
                if (automat[j][ii] == 0) {
                    if (automat[j][ii] == recmat[j][ii]) {
                        CM[10]++;//true negative, just considering hubs
                    } else {
                        CM[9]++;//false positive, just considering hubs
                    }
                } else if (automat[j][ii] == 1) {
                    if (automat[j][ii] == recmat[j][ii]) {
                        CM[8]++;//true positive, just considering hubs
                    } else {
                        CM[11]++;//false negative, just considering hubs
                    }
                }
            }
        }
        if ((CM[8] + CM[9]) > 0) {
            CM[12] = (CM[8] / (CM[8] + CM[9]));//PPV = TP / (TP + FP), just considering hubs
        }
        if ((CM[8] + CM[11]) > 0) {
            CM[13] = (CM[8] / (CM[8] + CM[11]));//SENSITIVITY = TP / (TP + FN), just considering hubs
        }
        if ((CM[10] + CM[9]) > 0) {
            CM[14] = (CM[10] / (CM[10] + CM[9]));//SPECIFICITY = TN / (TN + FP), just considering hubs
        }
        CM[15] = (float) Math.pow(CM[12] * CM[13] * CM[14], 1f / 3f);
        System.out.println("just considering hubs, result PPV = " + CM[12] + " ,SENSITIVITY = " + CM[13] + " ,SPECIFICITY= " + CM[14] + " ,SIMILARITY= " + CM[15]);
        return (CM);
    }


    /* Metodo usado comparar as redes original e recuperada.*/
    public static float[] ConfusionMatrix(
            int[][] originalmatrix,
            int[][] recoveredmatrix) {
        //vector to save in its positions:
        //0  == True Positives
        //1  == False Positives
        //2  == True Negatives
        //3  == False Negatives
        //4  == Ratio between true negatives and total negatives (TNR)
        //5  == Ratio between true positives and total positives (TPR)
        float[] CM = new float[6];

        //debug
        //IOFile.PrintMatrix(originalmatrix);
        //IOFile.PrintMatrix(recoveredmatrix);

        for (int i = 0; i < originalmatrix.length; i++) {
            for (int j = 0; j < originalmatrix[0].length; j++) {
                if (originalmatrix[i][j] == 0) {
                    //z1++;
                    if (originalmatrix[i][j] == recoveredmatrix[i][j]) {
                        CM[2]++;//true negative
                    } else {
                        CM[1]++;//false positive
                    }
                } else if (originalmatrix[i][j] == 1) {
                    //u1++;
                    if (originalmatrix[i][j] == recoveredmatrix[i][j]) {
                        CM[0]++;//true positive
                    } else {
                        CM[3]++;//false negative
                    }
                }

            }
        }
        CM[4] = (CM[2] / (CM[2] + CM[1]));//TNR
        CM[5] = (CM[0] / (CM[0] + CM[3]));//TPR
        System.out.println("result TNR = " + CM[4] + " , TPR = " + CM[5] + " and Similarity = " + Math.sqrt(CM[4] * CM[5]));
        return (CM);
    }

    /* Metodo usado comparar as redes original e recuperada.*/
    public static int[][] ThresholdMatrix(int[][] matrix, int threshold) {
        int[][] tmatrix = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] > threshold) {
                    tmatrix[i][j] = 1;
                }
            }
        }
        return (tmatrix);
    }
}
