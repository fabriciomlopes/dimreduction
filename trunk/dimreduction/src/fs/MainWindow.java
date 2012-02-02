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
/*** This class implements the graphic window to final user interactive. ***/
/***************************************************************************/
package fs;

import agn.AGNRoutines;
import agn.AGN;
import agn.CNMeasurements;
import analisadordecodigo.InputDataPreview;
import charts.Chart;
import charts.PrefusePanel;
import helps.HelpCV;
import helps.HelpFS;
import helps.HelpInput;
import helps.HelpQuantization;
import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import utilities.MathRoutines;
import utilities.RowHeaderRenderer;
import validations.Validation;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.SpinnerNumberModel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import utilities.IOFile;
import utilities.LogExecucao;

public class MainWindow extends javax.swing.JFrame {

    public static String delimiter = String.valueOf(' ') + String.valueOf('\t') + String.valueOf('\n') + String.valueOf('\r') + String.valueOf('\f') + String.valueOf(';') + String.valueOf('"');
    public static float[][] Mo = null;//original matrix of data
    public static float[][] Md = null;//work matrix
    public static int lines = 0; //number of variables
    public static int columns = 0;//number of features/samples
    public static Vector datatitles = null;
    public static Vector featurestitles = null;
    private static float[][] trainingset = null;
    private static float[][] testset = null;
    private static boolean flag_quantization = false;
    //atributo para armazenar as regras geradas automaticamente.
    //public static AGN agn = null;
    //atributo para armazenar as regras recuperadas pelo algoritmo.
    public static AGN recoverednetwork = null;
    private static DefaultCategoryDataset dataset = null;
    private static int has_labels = 0;
    private static JFrame help = null;//janela de exibicao help.
    //Painel para visualizacao da rede com os dados integrados.
    private static PrefusePanel NetworkVisualization = null;
    
    public static boolean colSelected;
    public static boolean lineSelected;

    public MainWindow() {
        initComponents();
        //inicializacao do modelo numerido usado na definicao dos parametros.
        jS_QuantizationValue.setModel(new SpinnerNumberModel(2, 1, Integer.MAX_VALUE, 1));
        jS_MaxResultListSE.setModel(new SpinnerNumberModel(3, 1, Integer.MAX_VALUE, 1));
        jS_MaxSetSizeSE.setModel(new SpinnerNumberModel(3, 1, Integer.MAX_VALUE, 1));
        jS_MaxSetSizeCV.setModel(new SpinnerNumberModel(3, 1, Integer.MAX_VALUE, 1));
        jS_NrExecutionsCV.setModel(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
        jS_ThresholdEntropy.setModel(new SpinnerNumberModel(0.3, 0, 1, 0.05));
        jS_QEntropyCV.setModel(new SpinnerNumberModel(1d, 0.1d, Double.MAX_VALUE, 0.1d));
        jS_AlphaCV.setModel(new SpinnerNumberModel(1d, 0d, Double.MAX_VALUE, 0.1d));
        jS_QEntropySE.setModel(new SpinnerNumberModel(1d, 0.1d, Double.MAX_VALUE, 0.1d));
        jS_AlphaSE.setModel(new SpinnerNumberModel(1d, 0d, Double.MAX_VALUE, 0.1d));

        //captura teclas pressionadas, se for F1 aciona o JFrame Help.
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
                addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F1
                        && e.getID() == KeyEvent.KEY_PRESSED) {
                    if (help != null) {
                        help.setVisible(false);
                        help.dispose();
                    }
                    if (jTabbedPane1.getSelectedIndex() == 0) {
                        help = new HelpInput();
                    } else if (jTabbedPane1.getSelectedIndex() == 1) {
                        help = new HelpQuantization();
                    } else if (jTabbedPane1.getSelectedIndex() == 2) {
                        help = new HelpFS();
                    } else if (jTabbedPane1.getSelectedIndex() == 3) {
                        help = new HelpCV();
                    }
                    help.setVisible(true);
                    return true;
                }
                return false;
            }
        });
    }

    /*ROTINA PARA LEITURA DOS DADOS DE ENTRADA, SEU ARMAZENAMENTO NA MATRIZ
    Md e sua exibicao grafica no objeto JT_InputData. */
    public synchronized void ReadInputData(String path) {
        int startrow = 0;
        int startcolumn = 0;
        try {
            if (path.endsWith("agn")) {
                recoverednetwork = IOFile.readAGNfromFile(path);
                featurestitles = recoverednetwork.getLabelstemporalsignal();
                datatitles = new Vector();
                for (int i = 0; i < recoverednetwork.getGenes().length; i++) {
                    datatitles.add(i, recoverednetwork.getGenes()[i].getName());
                }
                Mo = recoverednetwork.getTemporalsignal();

                if (Mo != null) {
                    int[][] aux = recoverednetwork.getTemporalsignalquantized();
                    if (aux != null) {
                        Md = new float[aux.length][aux[0].length];
                        for (int row = 0; row < aux.length; row++) {
                            for (int col = 0; col < aux[0].length; col++) {
                                Md[row][col] = (float) aux[row][col];
                            }
                        }
                    }
                }
            } else {
//                if (jCB_ColumnDescription.isSelected()) {
                if (colSelected) {
                    featurestitles = IOFile.readDataFirstRow(path, 0, 0, delimiter);
                    startrow = 1;
                }
//                if (jCB_DataTitlesColumns.isSelected()) {
                if (lineSelected) {
                    datatitles = IOFile.readDataFirstCollum(path, startrow, delimiter);
                    startcolumn = 1;
                }
                Mo = IOFile.readMatrix(path, startrow, startcolumn, delimiter);
                Md = Preprocessing.copyMatrix(Mo);//copy of matrix
            }

            if (jCB_TransposeMatrix.isSelected()) {
                Mo = MathRoutines.TransposeMatrix(Mo);
                String zz = null;
                if (featurestitles != null) {
                    zz = (String) featurestitles.remove(0);
                    if (datatitles != null) {
                        datatitles.add(0, zz);
                    }
                }
            }
        } catch (IOException error) {
            throw new FSException("Error when reading input file. " + error,
                    false);
        } catch (NumberFormatException error) {
            throw new FSException("Error when reading input file. " + error,
                    false);
        }
        lines = Mo.length;
        columns = Mo[0].length;
    }

    public JTable GenerateTableLabels(Vector ColumnDescriptions,
            Vector RowDescriptions, float[][] data) {

        int cols = data[0].length;
        if (RowDescriptions != null) {
            cols++;
        }

        Object[] titles = new Object[cols];//column titles
        Object[][] table = new Object[data.length][cols];


        //add column description
        for (int j = 0; j < ColumnDescriptions.size(); j++) {
            titles[j] = j;
            if (ColumnDescriptions != null) {
                if (ColumnDescriptions.size() < cols) {
                    titles[0] = "variables";
                    titles[j + 1] = (String) ColumnDescriptions.get(j);
                } else {
                    titles[j] = (String) ColumnDescriptions.get(j);
                }
            }
        }

        int jt = 0;
        if (RowDescriptions != null) {
            //add a new column with rows descriptions in visual table
            for (int i = 0; i < data.length; i++) {
                table[i][0] = (String) RowDescriptions.get(i);
            }
            jt = 1;//it was added one extra column in a table.
        }

        //add the numeric values (data) in table.
        for (int j = 0; j < data[0].length; j++, jt++) {
            for (int i = 0; i < data.length; i++) {
                table[i][jt] = (Float) data[i][j];
            }
        }
        JTable jTable = new JTable(table, titles);
        return (jTable);
    }

    public JTable GenerateTable(float[][] M) {
        Object[] titles = new Object[M[0].length];
        Object[][] data = new Object[M.length][M[0].length];
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[0].length; j++) {
                if (i == 0) {
                    titles[j] = j;
                }
                data[i][j] = M[i][j];
            }
        }
        JTable jTable = new JTable(data, titles);
        return (jTable);
    }

    //ROTINA PARA APLICACAO DE QUANTIZACAO NOS DADOS DE ENTRADA.
    //type representa se sera realizada a quantizacao ou apenas a normalizacao.
    public void ApplyQuantization(int qtvalues, int type) {
        if (Mo != null) {
            jP_QuantizedData.remove(jT_QuantizedData);
            Md = Preprocessing.copyMatrix(Mo);//copy of matrix

            if (type == 1) //traditional quantization, apply normalization and
            //creates threshold values to positive and negative values.
            {
                Preprocessing.quantizecolumns(Md, qtvalues, true, has_labels);
            } else {
                Preprocessing.quantizerows(Md, qtvalues, true, has_labels);
                //Preprocessing.normalize(Md, qtvalues, has_labels);
            }
            if (jCB_TransposeMatrix.isSelected()) {
                jT_QuantizedData = GenerateTableLabels(datatitles, featurestitles, Md);
            } else {
                jT_QuantizedData = GenerateTableLabels(featurestitles, datatitles, Md);
            }
            flag_quantization = true;
            jT_QuantizedData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            jT_QuantizedData.setAlignmentX(JTable.CENTER_ALIGNMENT);
            jSP_QuantizedData.setViewportView(jT_QuantizedData);
        } else {
            JOptionPane.showMessageDialog(null, "Execution Error:Select and"
                    + " read input file first.", "Application Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /*EXECUCAO DA SELECAO DE CARACTERISTICAS.*/
    public void ExecuteFeatureSelection(int selector) throws IOException {
        String penalization_type = (String) jCB_PenalizationSE.getSelectedItem();
        float alpha = ((Double) jS_AlphaSE.getValue()).floatValue();
        float q_entropy = ((Double) jS_QEntropySE.getValue()).floatValue();
        float beta = ((float) jSliderBetaSE.getValue() / 100);

        //if selected criterion function is CoD, q_entropy = 0
        if (jCB_CriterionFunctionSE.getSelectedIndex() == 1) {
            q_entropy = 0;
        }//CoD

        if (q_entropy < 0 || alpha < 0) {
            //entrada de dados invalida.
            JOptionPane.showMessageDialog(this, "Error on parameter value:The"
                    + " values of q-entropy and Alpha must be positives.",
                    "Application Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        jTA_SaidaSE.setText("");
        jTA_SelectedFeaturesSE.setText("");

        // n = quantidade de valores assumidos pelas caracteristicas.
        int n = Main.maximumValue(Md, 0, lines - 1, 0, columns - 2) + 1;

        //c = numero de rotulos possiveis para as classes.
        int c = Main.maximumValue(Md, 0, lines - 1, columns - 1, columns - 1) + 1;

        jProgressBarSE.setValue(5);
        Thread.yield();

        char[][] strainingset = MathRoutines.float2char(Md);
        char[][] stestset = null;

        if (!jTF_InputTestSE.getText().equals("")) {
            stestset = IOFile.readMatrix(jTF_InputTestSE.getText(), delimiter);
        } else {
            stestset = MathRoutines.float2char(Md);
        }

        int resultsetsize = 1;
        try {
            //vetor com os resultados da selecao de caracteristica.
            resultsetsize = (Integer) jS_MaxResultListSE.getValue();
            if (resultsetsize < 1) {
                Thread.yield();
                JOptionPane.showMessageDialog(this, "Error on parameter value:"
                        + " The Size of the Result List must be a integer value"
                        + " greater or equal to 1.", "Application Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException error) {
            Thread.yield();
            JOptionPane.showMessageDialog(this, "Error on parameter value: The"
                    + " Size of the Result List must be a integer value greater"
                    + " or equal to 1.", "Application Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        /* SELETOR DE CARACTERISTICAS PARA O TREINAMENTO. */
        FS fs = new FS(strainingset, n, c, penalization_type, alpha, beta, q_entropy, resultsetsize);

        jProgressBarSE.setValue(10);
        Thread.yield();

        int maxfeatures = (Integer) jS_MaxSetSizeSE.getValue();
        if (maxfeatures <= 0) {
            JOptionPane.showMessageDialog(this, "Error on parameter value: The"
                    + " Maximum Set Size be a integer value greater"
                    + " or equal to 1.", "Application Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selector == 1) {
            fs.runSFS(false, maxfeatures);
            jProgressBarSE.setValue(90);
            Thread.yield();
        } else if (selector == 3) {
            fs.runSFFS(maxfeatures, -1, null);
            jProgressBarSE.setValue(90);
            Thread.yield();
        } else if (selector == 2) {
            fs.runSFS(true, maxfeatures); /* a call to SFS is made in order to get the
            //ideal dimension to run the exhaustive search;*/
            int itmax = fs.itmax;
            if (itmax < maxfeatures) {
                itmax = maxfeatures;
            }
            /*calculating the estimated time to be completed in rea
            computer of 2 GHz*/
            int combinations = 0;
            for (int i = 1; i <= itmax; i++) {
                combinations += MathRoutines.numberCombinations(columns - 1, i);
            }
            double estimatedTime = (0.0062 + 3.2334e-7 * strainingset.length)
                    * combinations * Math.log(combinations) / Math.log(2);
            int answer = JOptionPane.showConfirmDialog(null, "Estimated "
                    + "time to finish: " + estimatedTime + " s.\n Do you want to"
                    + " continue?", "Exhaustive Search",
                    JOptionPane.YES_NO_OPTION);
            if (answer == 1) {
                jProgressBarSE.setValue(0);
                return;
            }
            //System.out.println("Estimated time to finish: "+estimatedTime+" s");
            float new_itmax = itmax;
            FS fsPrev = new FS(strainingset, n, c, penalization_type, alpha, beta,
                    q_entropy, resultsetsize);
            for (int i = 1; i <= itmax; i++) {
                System.out.println("Iteration " + i);
                fs = new FS(strainingset, n, c, penalization_type, alpha, beta,
                        q_entropy, resultsetsize);
                fs.itmax = i;
                fs.runExhaustive(0, 0, fs.I);
                //if (fs.hGlobal == 0) {
                //    break;
                //}
                if (fs.hGlobal < fsPrev.hGlobal) {
                    fsPrev = fs;
                } else {
                    fs = fsPrev;
                    break;
                }
                float new_i = i;
                float pb = (new_i / new_itmax);
                pb = (pb * 80.0f);
                jProgressBarSE.setValue(10 + (int) pb);
                Thread.yield();
            }
        }

        /*
        jTA_SelectedFeaturesSE.setText("1st Global Criterion Function Value: " + fs.hGlobal);
        jTA_SelectedFeaturesSE.append("\nSelected Features: ");
        for (int i = 0; i < fs.I.size(); i++) {
        jTA_SelectedFeaturesSE.append(fs.I.elementAt(i) + " ");
        }
         * substituido pelo codigo abaixo para exibir uma lista de resultados
         * de tamanho escolhido pelo usuario.
         */
        for (int i = 0; i < fs.resultlist.size(); i++) {
            float fsvalue = ((Float) fs.resultlist.get(i).get(0));
            if (i == 0) {
                jTA_SelectedFeaturesSE.setText((i + 1) + "st Global Criterion"
                        + " Function Value: " + fsvalue);
            } else if (i == 1) {
                jTA_SelectedFeaturesSE.append("\n\n" + (i + 1) + "nd Global"
                        + " Criterion Function Value: " + fsvalue);
            } else if (i == 2) {
                jTA_SelectedFeaturesSE.append("\n\n" + (i + 1) + "rd Global"
                        + " Criterion Function Value: " + fsvalue);
            } else {
                jTA_SelectedFeaturesSE.append("\n\n" + (i + 1) + "th Global"
                        + " Criterion Function Value: " + fsvalue);
            }
            jTA_SelectedFeaturesSE.append("\nSelected Features: ");
            Vector features = (Vector) fs.resultlist.get(i).get(1);
            for (int j = 0; j < features.size(); j++) {
                jTA_SelectedFeaturesSE.append((Integer) features.get(j) + " ");
            }
        }

        // CLASSIFICADOR.
        Classifier clas = new Classifier();
        clas.classifierTable(strainingset, fs.I, n, c);
        for (int i = 0; i < clas.table.size(); i++) {
            double[] tableLine = (double[]) clas.table.elementAt(i);
            double instance = (Double) clas.instances.elementAt(i);
            System.out.print(instance + " ");
            for (int j = 0; j < c; j++) {
                System.out.print((int) tableLine[j] + " ");
            }
            System.out.println();
        }
        jProgressBarSE.setValue(95);
        Thread.yield();
        double[] instances = clas.classifyTestSamples(stestset, fs.I, n, c);
        jTA_SaidaSE.setText("Correct Labels  -  Classified Labels - "
                + "Classification Instances\n(Considering the first selected"
                + " features)\n");
        double hits = 0;
        for (int i = 0; i < clas.labels.length; i++) {
            int correct_label = (int) stestset[i][columns - 1];
            int classified_label = (int) clas.labels[i];
            jTA_SaidaSE.append("\n" + correct_label + "  -  "
                    + classified_label + "  -  " + instances[i]);
            if (correct_label == classified_label) {
                hits++;
            }
            Thread.yield();
        }
        double hit_rate = hits / clas.labels.length;
        jTA_SaidaSE.append("\nrate of hits = " + hit_rate);
        jProgressBarSE.setValue(100);
        Thread.yield();
    }

    public void CrossValidation(int nr_executions, float percent_trainning,
            int selector) throws IOException {
        String penalization_type = (String) jCB_PenalizationCV.getSelectedItem();
        float alpha = ((Double) jS_AlphaCV.getValue()).floatValue();
        float q_entropy = ((Double) jS_QEntropyCV.getValue()).floatValue();
        float beta = ((float) jSliderBetaCV.getValue() / 100);

        //if selected criterion function is CoD, q_entropy = 0
        if (jCB_CriterionFunctionCV.getSelectedIndex() == 1) {
            q_entropy = 0;
        }//CoD

        if (q_entropy < 0 || alpha < 0) {
            //entrada de dados invalida.
            JOptionPane.showMessageDialog(null, "Error on parameter value:"
                    + "The values of q-entropy and Alpha must be positives.",
                    "Application Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dataset = new DefaultCategoryDataset();
        double rate = 0;
        int n = Main.maximumValue(Md, 0, lines - 1, 0, columns - 2) + 1;
        int c = Main.maximumValue(Md, 0, lines - 1, columns - 1, columns - 1) + 1;

        jProgressBarCV.setValue(1);

        int total_samples = (int) (lines * percent_trainning);

        trainingset = new float[total_samples][columns];
        testset = new float[lines - total_samples][columns];
        //testset = new float[total_samples][columns];

        char[][] strainingset = null;
        char[][] stestset = null;

        jProgressBarCV.setValue(3);

        float ex = (96 / nr_executions);

        /* calculating the estimated time to be completed in a
        computer of 2 GHz*/
        int combinations = MathRoutines.numberCombinations(columns - 1, 1);

        double estimatedTime = (0.0062 + 3.2334e-7
                * Mo.length) * combinations
                * Math.log(combinations) / Math.log(2);

        estimatedTime *= nr_executions;

        System.out.println("Estimated time to finish: " + estimatedTime + " s");
        int answer = JOptionPane.showConfirmDialog(this,
                "Estimated time to finish: " + estimatedTime + " s.\n "
                + "Do you want to continue?", "Cross Validation",
                JOptionPane.YES_NO_OPTION);
        if (answer == 1) {
            jProgressBarCV.setValue(0);
            return;
        }

        //vetor com os resultados da selecao de caracteristica.
        int resultsetsize = 1;
        //vetor com os resultados da selecao de caracteristica.

        for (int executions = 0; executions < nr_executions; executions++) {
            if (flag_quantization) {
                Validation.GenerateSubSets(Md, percent_trainning, trainingset, testset);
                //Preprocessing.quantizecolumnsavg(trainingset, (Integer) jS_QuantizationValue.getValue(), true, has_labels);
                //Preprocessing.quantizecolumnsavg(testset, (Integer) jS_QuantizationValue.getValue(), false, has_labels);
            } else {
                Validation.GenerateSubSets(Mo, percent_trainning, trainingset, testset);
            }

            strainingset = MathRoutines.float2char(trainingset);
            stestset = MathRoutines.float2char(testset);

            int maxfeatures = (Integer) jS_MaxSetSizeCV.getValue();
            if (maxfeatures <= 0) {
                JOptionPane.showMessageDialog(this, "Error on parameter value: The"
                        + " Maximum Set Size be a integer value greater"
                        + " or equal to 1.", "Application Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            FS fs = new FS(strainingset, n, c, penalization_type, alpha, beta, q_entropy, resultsetsize);
            if (selector == 1) {
                fs.runSFS(false, maxfeatures);
            } else if (selector == 3) {
                fs.runSFFS(maxfeatures, -1, null);
            } else if (selector == 2) {
                fs.runSFS(true, maxfeatures); /* a call to SFS is made in order to get
                the ideal dimension for the exhaustive search. */
                int itmax = fs.itmax;
                FS fsPrev = new FS(strainingset, n, c, penalization_type, alpha,
                        beta, q_entropy, resultsetsize);
                for (int i = 1; i <= itmax; i++) {
                    fs = new FS(strainingset, n, c, penalization_type, alpha, beta,
                            q_entropy, resultsetsize);
                    fs.itmax = i;
                    fs.runExhaustive(0, 0, fs.I);
                    if (fs.hGlobal == 0) {
                        break;
                    }
                    if (fs.hGlobal < fsPrev.hGlobal) {
                        fsPrev = fs;
                    } else {
                        fs = fsPrev;
                        break;
                    }
                }
            }
            if (executions == 0) {
                jTA_SelectedFeaturesCV.setText("Execution " + (executions + 1)
                        + " - Global Criterion Function Value: " + fs.hGlobal + "\n");
            } else {
                jTA_SelectedFeaturesCV.append("\n\nExecution " + (executions + 1)
                        + " - Global Criterion Function Value: " + fs.hGlobal + "\n");
            }

            jTA_SelectedFeaturesCV.append("Selected Features: ");
            for (int i = 0; i < fs.I.size(); i++) {
                jTA_SelectedFeaturesCV.append(fs.I.elementAt(i) + " ");
            }

            jProgressBarCV.setValue(jProgressBarCV.getValue() + (int) (ex / 4));
            Thread.yield();

            /* CLASSIFICADOR. */
            Classifier clas = new Classifier();
            clas.classifierTable(strainingset, fs.I, n, c);

            jProgressBarCV.setValue(jProgressBarCV.getValue() + (int) (ex / 4));
            Thread.yield();

            for (int i = 0; i < clas.table.size(); i++) {
                double[] tableLine = (double[]) clas.table.elementAt(i);
                double instance = (Double) clas.instances.elementAt(i);
                System.out.print(instance + " ");
                for (int j = 0; j < c; j++) {
                    System.out.print((int) tableLine[j] + " ");
                }
                System.out.println();
                Thread.yield();
            }
            //stestset = strainingset;
            double[] instances = clas.classifyTestSamples(stestset, fs.I, n, c);
            jProgressBarCV.setValue(jProgressBarCV.getValue() + (int) (ex / 4));
            Thread.yield();
            if (executions == 0) {
                jTA_SaidaCV.setText("Execution " + (executions + 1)
                        + " - Correct Labels  -  Classified Labels - "
                        + "Classification Instances\n");
            } else {
                jTA_SaidaCV.append("\n\nExecution " + (executions + 1)
                        + " - Correct Labels  -  Classified Labels - "
                        + "Classification Instances\n");
            }
            double hits = 0;
            for (int i = 0; i < clas.labels.length; i++) {
                //char correct_char = stestset[i].charAt(collumns-1);
                int correct_label = stestset[i][columns - 1];
                //int correct_label = correct_char;
                int classified_label = clas.labels[i];
                jTA_SaidaCV.append("\n" + correct_label + "  -  "
                        + classified_label + "  -  " + instances[i]);
                if (correct_label == classified_label) {
                    hits++;
                }
            }
            double hit_rate = hits / clas.labels.length;
            if (rate > 0) {
                rate = (rate + hit_rate) / 2;
            } else {
                rate = hit_rate;
            }
            jTA_SaidaCV.append("\nrate of hits = " + hit_rate);
            dataset.addValue(1 - rate, "Error Cross Validation",
                    String.valueOf(executions + 1));

            jProgressBarCV.setValue(jProgressBarCV.getValue() + (int) (ex / 4));
            Thread.yield();
        }
        Chart.LineChart(dataset, "Cross Validation Error with " + (int) (percent_trainning * 100) + "% of samples on Training Set",
                "Number of executions", "Mean Errors", false, 0, 0);
        jProgressBarCV.setValue(100);
        Thread.yield();
    }

    public void XYZScatterPlot(int f1, int f2, int f3) {
        JOptionPane.showMessageDialog(null, "Execution Information: The 3D "
                + "Scatter Plot was not implemented yet.",
                "Application Warning", JOptionPane.INFORMATION_MESSAGE);
        //to be implemented
    }

    public void XYScatterPlot(int f1, int f2) {
        XYSeriesCollection seriesc = new XYSeriesCollection();
        if (jCB_HasLabels.isSelected()) {
            int c = Main.maximumValue(Mo, 0, lines - 1, columns - 1, columns - 1) + 1;
            XYSeries[] series = new XYSeries[c];
            for (int i = 0; i < c; i++) {
                series[i] = null;
            }
            for (int i = 0; i < lines; i++) {
                int classe = ((int) Mo[i][columns - 1]);
                if (series[classe] == null) {
                    series[classe] = new XYSeries(classe);
                }
                series[classe].add(Mo[i][f1], Mo[i][f2]);
            }
            for (int i = 0; i < c; i++) {
                if (series[i] != null) {
                    seriesc.addSeries(series[i]);
                }
            }
        } else {
            XYSeries serie = new XYSeries("variables");
            for (int i = 0; i < lines; i++) {
                serie.add(Mo[i][f1], Mo[i][f2]);
            }
            seriesc.addSeries(serie);
        }
        if (featurestitles != null) {
            if (datatitles != null) {
                Chart.ScatterPlot(seriesc, "ScatterPlot of the features: "
                        + (String) featurestitles.get(f1 + 1) + " x "
                        + (String) featurestitles.get(f2 + 1),
                        (String) featurestitles.get(f1 + 1),
                        (String) featurestitles.get(f2 + 1));
            } else {
                Chart.ScatterPlot(seriesc, "ScatterPlot of the features: "
                        + (String) featurestitles.get(f1) + " x "
                        + (String) featurestitles.get(f2),
                        (String) featurestitles.get(f1),
                        (String) featurestitles.get(f2));
            }
        } else {
            Chart.ScatterPlot(seriesc, "ScatterPlot of the features: " + f1 + " x "
                    + f2, "feature " + f1, "feature " + f2);
        }
    }

    public void XScatterPlot(int f) {
        DefaultCategoryDataset datasetplot = new DefaultCategoryDataset();
        for (int i = 0; i < lines; i++) {
            Number value = Mo[i][f];
            if (featurestitles != null) {
                if (datatitles != null) {
                    datasetplot.addValue(value, 0, (String) featurestitles.get(f + 1));
                } else {
                    datasetplot.addValue(value, 0, (String) featurestitles.get(f));
                }
            } else {
                datasetplot.addValue(value, 0, String.valueOf(f));
            }
        }
        Chart.BarChart(datasetplot, "ScatterPlot of the feature: " + f,
                "variables", "values", true);
    }

    public void ScatterPlot() throws IOException {
        String strfeatures = JOptionPane.showInputDialog("Select features "
                + "(columns) to scatter plot (maximum two):", "0 1");
        if (strfeatures != null) {
            StringTokenizer s = new StringTokenizer(strfeatures);
            int[] features = ExtractIndices(s);
            if (features.length == 1) {
                XScatterPlot(features[0]);
            } else if (features.length == 2) {
                XYScatterPlot(features[0], features[1]);
            } else if (features.length == 3) {
                XYZScatterPlot(features[0], features[1], features[2]);
            } else {
                JOptionPane.showMessageDialog(null, "Execution Warning: The "
                        + "implemented Scatter Plot accept at least one and a "
                        + "maximum of two features.",
                        "Application Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Execution Warning: The "
                    + "implemented Scatter Plot accept at least one and a "
                    + "maximum of two features.",
                    "Application Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    //plota o sinal original ligado por segmentos de reta.
    public static Vector SinalLinePlot(int[] lineindex, boolean showlegend, int startcol) {
        DefaultCategoryDataset[] datasets = new DefaultCategoryDataset[lineindex.length];
        float ls = 0;
        float li = 0;
        for (int i = 0; i < lineindex.length; i++) {
            String label = null;
            if (datatitles != null) {
                label = (String) datatitles.get(lineindex[i]);
            } else {
                label = String.valueOf("variables " + lineindex[i]);
            }
            float[] maxmin = new float[2];
            Preprocessing.MaxMin(Mo[lineindex[i]], maxmin);
            //encontra os valores limites para plotar o grafico.
            if (ls <= maxmin[0]) {
                ls = (float) maxmin[0] + 0.5f;
            }
            if (li > maxmin[1]) {
                li = (float) maxmin[1] - 0.5f;
            }
            datasets[i] = new DefaultCategoryDataset();
            for (int c = 0; c < Mo[0].length; c++) {
                if ((int) Mo[lineindex[i]][c] != Preprocessing.skipvalue) {
                    if (featurestitles != null) {
                        if (datatitles != null) {
                            datasets[i].addValue(Mo[lineindex[i]][c], label, (String) featurestitles.get(c + 1 + startcol));
                        } else {
                            datasets[i].addValue(Mo[lineindex[i]][c], label, (String) featurestitles.get(c + startcol));
                        }
                    } else {
                        datasets[i].addValue(Mo[lineindex[i]][c], label, String.valueOf(c));
                    }
                }
            }
        }
        return (Chart.MultipleLineChart(datasets, "Time Series", "Instants of time",
                "Expression Value", showlegend, ls, li, true));
    }

    //plota o sinal original ligado por curvas splines.
    public JFreeChart SinalSplinePlot(int[] lineindex, boolean showlegend) {
        XYDataset[] datasets = new XYDataset[lineindex.length];
        float ls = 0;
        float li = 0;
        for (int i = 0; i < lineindex.length; i++) {
            if ((int) Mo[lineindex[i]][0] != Preprocessing.skipvalue) {
                XYSeries serie = null;
                if (datatitles != null) {
                    serie = new XYSeries((String) datatitles.get(lineindex[i]));
                } else {
                    serie = new XYSeries("variable " + lineindex[i]);
                }
                float[] maxmin = new float[2];
                Preprocessing.MaxMin(Mo[lineindex[i]], maxmin);
                //encontra os valores limites para plotar o grafico.
                if (ls <= maxmin[0]) {
                    ls = (float) maxmin[0] + 0.5f;
                }
                if (li > maxmin[1]) {
                    li = (float) maxmin[1] - 0.5f;
                }
                for (int c = 0; c < columns; c++) {
                    serie.add(c, Mo[lineindex[i]][c]);
                }
                datasets[i] = new XYSeriesCollection(serie);
            }
        }
        return (Chart.MultipleSplineLineChart(datasets,
                "Time-Series Expression", "Time", "Value", true, ls, li, true));
    }
    //plota o sinal quantizado usando cor e o sinal normalizado em cinza
    //de forma sobreposta. O parametro maxvalue eh usado para normalizar o
    //sinal nao quantizado.

    public static Vector SinalPlot(int[] lineindex, boolean showlegend,
            float maxvalue, int startcol) {
        /*
        //codigo para plotar sinais em um mesmo grafico (sobreposto).
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < lineindex.length; i++)
        for (int c=0; c < columns; c++)
        dataset.addValue(Md[ lineindex[i] ][c], "variable "+lineindex[i],
        String.valueOf(c));
        
        if (flag_quantization)
        Chart.StepChart(dataset, "Line Chart", "features", "values",
        true, Float.valueOf(jTF_QtValues.getText())-1+0.03f, -0.03f);
        else
        Chart.StepChart(dataset, "Line Chart", "features", "values",
        true, 0, 0);
         */
        //codigo para gerar os dados para o grafico MultipleStepChart - usando CategoryDataset
        DefaultCategoryDataset[] datasets1 = new DefaultCategoryDataset[lineindex.length];
        DefaultCategoryDataset[] datasets2 = new DefaultCategoryDataset[lineindex.length];

        for (int i = 0; i < lineindex.length; i++) {
            datasets1[i] = new DefaultCategoryDataset();
            datasets2[i] = new DefaultCategoryDataset();
            float[] maxmin = new float[2];
            Preprocessing.MaxMin(Mo[lineindex[i]], maxmin);
            double valornormalizado = 0;

            for (int c = 0; c < Mo[0].length; c++) {
                if ((int) Mo[lineindex[i]][c] != Preprocessing.skipvalue) {
                    //if ((int) Mo[lineindex[i]][0] != Preprocessing.skipvalue) {
                    //for (int c = 0; c < Md[0].length; c++) {
                    //valornormalizado = (Mo[lineindex[i]][c] - maxmin[1]) / (maxmin[0] - maxmin[1]);
                    valornormalizado = Mo[lineindex[i]][c];

                    if (datatitles != null && featurestitles != null) {
                        datasets1[i].addValue(
                                Md[lineindex[i]][c],
                                (String) datatitles.get(lineindex[i]),
                                (String) featurestitles.get(c + 1 + startcol));
                        datasets2[i].addValue(
                                valornormalizado,
                                (String) datatitles.get(lineindex[i]),
                                (String) featurestitles.get(c + 1 + startcol));
                    } else if (datatitles != null) {
                        datasets1[i].addValue(
                                Md[lineindex[i]][c],
                                (String) datatitles.get(lineindex[i]),
                                String.valueOf(c));
                        datasets2[i].addValue(
                                valornormalizado,
                                (String) datatitles.get(lineindex[i]),
                                String.valueOf(c));
                    } else if (featurestitles != null) {
                        datasets1[i].addValue(
                                Md[lineindex[i]][c],
                                "variable " + lineindex[i],
                                (String) featurestitles.get(c + startcol));
                        datasets2[i].addValue(
                                valornormalizado,
                                "variable " + lineindex[i],
                                (String) featurestitles.get(c + startcol));
                    } else {
                        datasets1[i].addValue(
                                Md[lineindex[i]][c],
                                "variable " + lineindex[i],
                                String.valueOf(c));
                        datasets2[i].addValue(
                                valornormalizado,
                                "variable " + lineindex[i],
                                String.valueOf(c));
                    }
                }
            }
        }
        return (Chart.MultipleStepChartOverlayed(datasets1, datasets2, "Time-Series Data", "Time", "Value",
                showlegend, maxvalue + 0.03f, -0.03f, true));

        /*
        //codigo para gerar os dados para o grafico MultipleStepChart - usando XYDataset
        XYDataset[] datasets1 = new XYDataset[lineindex.length];
        XYDataset[] datasets2 = new XYDataset[lineindex.length];
        float[] maxmin = new float[2];
        maxvalue -= 1;
        for (int i = 0; i < lineindex.length; i++) {
        XYSeries serie1 = null;//new XYSeries("variable " + lineindex[i]);
        XYSeries serie2 = null;//new XYSeries("variable " + lineindex[i]);
        if (datatitles != null) {
        serie1 = new XYSeries((String) datatitles.get(lineindex[i]));
        serie2 = new XYSeries((String) datatitles.get(lineindex[i]));
        } else {
        serie1 = new XYSeries("variable " + lineindex[i]);
        serie2 = new XYSeries("variable " + lineindex[i]);
        }
        
        Preprocessing.MaxMin(Mo[lineindex[i]], maxmin);
        //if (maxvalue < maxmin[0])
        //    maxvalue = (float) maxmin[0];
        double valornormalizado = 0;
        for (int c = 0; c < columns; c++) {
        serie1.add(c, Md[lineindex[i]][c]);
        valornormalizado = maxvalue * (Mo[lineindex[i]][c] - maxmin[1]) /
        (maxmin[0] - maxmin[1]);
        
        serie2.add(c, valornormalizado);
        }
        datasets1[i] = new XYSeriesCollection(serie1);
        datasets2[i] = new XYSeriesCollection(serie2);
        }
        Chart.MultipleStepChartOverlayed(datasets1,
        datasets2, "Time-Series Data", "Time", "Value", true,
        maxvalue + 0.03f, -0.03f, true);
         */

        /*
        //codigo para gerar e salvar imagem de saida.
        try{
        String imgoutfile = "C:/teste-sinal.png";
        BufferedImage img = chart.createBufferedImage(800,600);//(BufferedImage) chart.getBackgroundImage();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(imgoutfile));
        ImageIO.write(img, "png", out);
        }catch(IOException error){
        System.out.println("Erro ao gerar imagem de saida: "+error);
        };
         */

        /*
        //codigo para gerar os dados para o grafico MultipleLineChart
        XYDataset [] xydatasets = new XYDataset[lineindex.length];
        for (int i = 0; i < lineindex.length; i++){
        XYSeries serie = new XYSeries("variable "+lineindex[i]);
        for (int c=0; c < columns; c++)
        serie.add(c, Md[ lineindex[i] ][c]);
        //datasets[i].addValue(Md[ lineindex[i] ][c], String.valueOf(c));
        xydatasets[i] = new XYSeriesCollection(serie);
        }
        Chart.MultipleLineChart(xydatasets, "Line Chart", "features", "values",
        true, Float.valueOf(jTF_QtValues.getText())-1+0.03f, -0.03f);
         */
    }

    public void SinalPlotReadValues() throws IOException {
        String strvars = JOptionPane.showInputDialog("Select variables (rows)"
                + " to signal plot:", "0 1 2");
        if (strvars != null) {
            StringTokenizer s = new StringTokenizer(strvars);
            int[] vars = ExtractIndices(s);
            if (flag_quantization) {
                SinalPlot(vars, true, (Integer) jS_QuantizationValue.getValue() - 1, 0);
            } else {
                //SinalSplinePlot(vars, true);
                SinalLinePlot(vars, true, 0);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel7 = new javax.swing.JPanel();
        buttonGroup_SE = new javax.swing.ButtonGroup();
        jPanel4 = new javax.swing.JPanel();
        buttonGroup_CV = new javax.swing.ButtonGroup();
        buttonGroup_DataType = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jP_Input = new javax.swing.JPanel();
        jP_InputData = new javax.swing.JPanel();
        jSP_InputData = new javax.swing.JScrollPane();
        jT_InputData = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jB_ReadData = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jCB_TransposeMatrix = new javax.swing.JCheckBox();
        jButton32 = new javax.swing.JButton();
        jCB_DataTitlesColumns = new javax.swing.JCheckBox();
        jCB_ColumnDescription = new javax.swing.JCheckBox();
        jTF_InputFile = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jP_Quantization = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jB_ApplyQuantization = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jCB_HasLabels = new javax.swing.JCheckBox();
        jB_ApplyQuantization1 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jS_QuantizationValue = new javax.swing.JSpinner();
        jP_QuantizedData = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jSP_QuantizedData = new javax.swing.JScrollPane();
        jT_QuantizedData = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jP_SFS = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTA_SelectedFeaturesSE = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTA_SaidaSE = new javax.swing.JTextArea();
        jBtnSaveResultsSE = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jP_SelectSearchAlgorithm = new javax.swing.JPanel();
        jRB_SFSSE = new javax.swing.JRadioButton();
        jRB_SFFSSE = new javax.swing.JRadioButton();
        jRB_ESSE = new javax.swing.JRadioButton();
        jLabel29 = new javax.swing.JLabel();
        jS_MaxSetSizeSE = new javax.swing.JSpinner();
        jLabel31 = new javax.swing.JLabel();
        jS_MaxResultListSE = new javax.swing.JSpinner();
        jPanel12 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jCB_PenalizationSE = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTF_InputTestSE = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jProgressBarSE = new javax.swing.JProgressBar();
        jButton35 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jCB_CriterionFunctionSE = new javax.swing.JComboBox();
        jLabelConfidenceSE = new javax.swing.JLabel();
        jSliderBetaSE = new javax.swing.JSlider();
        jS_QEntropySE = new javax.swing.JSpinner();
        jS_AlphaSE = new javax.swing.JSpinner();
        jPanel15 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jCB_TargetsAsPredictors = new javax.swing.JCheckBox();
        jCB_Periodic = new javax.swing.JCheckBox();
        jLabel_TargetsPredictors = new javax.swing.JLabel();
        jTF_Target = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jS_ThresholdEntropy = new javax.swing.JSpinner();
        jRBSteadyState = new javax.swing.JRadioButton();
        jRBTimeSeries = new javax.swing.JRadioButton();
        jPanel13 = new javax.swing.JPanel();
        jButton15 = new javax.swing.JButton();
        jBtn_ParallelCoord_SE = new javax.swing.JButton();
        jCB_NormalizeValuesSE = new javax.swing.JCheckBox();
        jButton28 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jCB_MeanParallelCoordSE = new javax.swing.JCheckBox();
        jButton8 = new javax.swing.JButton();
        jP_NetVis = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jChBoxOnlineSearch = new javax.swing.JCheckBox();
        jP_CV = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jBtn_ScatterPlot = new javax.swing.JButton();
        jBtn_ParallelCoord_CV = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jCB_MeanParallelCoordCV = new javax.swing.JCheckBox();
        jCB_NormalizeValuesCV = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        jRB_SFSCV = new javax.swing.JRadioButton();
        jRB_SFFSCV = new javax.swing.JRadioButton();
        jRB_ESCV = new javax.swing.JRadioButton();
        jLabel30 = new javax.swing.JLabel();
        jS_MaxSetSizeCV = new javax.swing.JSpinner();
        jPanel11 = new javax.swing.JPanel();
        jProgressBarCV = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();
        jSliderCV = new javax.swing.JSlider();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jCB_PenalizationCV = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jButton34 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jCB_CriterionFunctionCV = new javax.swing.JComboBox();
        jLabelConfidenceCV = new javax.swing.JLabel();
        jSliderBetaCV = new javax.swing.JSlider();
        jS_NrExecutionsCV = new javax.swing.JSpinner();
        jS_QEntropyCV = new javax.swing.JSpinner();
        jS_AlphaCV = new javax.swing.JSpinner();
        jPanel6 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTA_SelectedFeaturesCV = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTA_SaidaCV = new javax.swing.JTextArea();
        jBtnSaveResultsCV = new javax.swing.JButton();

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel4.setPreferredSize(new java.awt.Dimension(10, 325));
        jPanel4.setLayout(null);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Interactive Graphic Environment for Dimensionality Reduction");
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jP_Input.setLayout(new java.awt.BorderLayout(5, 5));

        jP_InputData.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jP_InputData.setPreferredSize(new java.awt.Dimension(100, 10));
        jP_InputData.setLayout(new java.awt.BorderLayout(5, 5));

        jSP_InputData.setAutoscrolls(true);
        jSP_InputData.setFocusCycleRoot(true);

        jT_InputData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jT_InputData.setToolTipText("This table shows the read data. If your input data file has variable titles or descriptions, you can edit them.");
        jT_InputData.setCellSelectionEnabled(true);
        jSP_InputData.setViewportView(jT_InputData);

        jP_InputData.add(jSP_InputData, java.awt.BorderLayout.CENTER);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel12.setForeground(new java.awt.Color(0, 0, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setOpaque(true);
        jLabel12.setPreferredSize(new java.awt.Dimension(40, 30));
        jP_InputData.add(jLabel12, java.awt.BorderLayout.NORTH);

        jButton13.setFont(new java.awt.Font("Tahoma", 1, 12));
        jButton13.setForeground(new java.awt.Color(0, 0, 255));
        jButton13.setText("Save Read Data");
        jButton13.setToolTipText("Click to save the read data into a text file.");
        jButton13.setPreferredSize(new java.awt.Dimension(163, 30));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        jP_InputData.add(jButton13, java.awt.BorderLayout.SOUTH);

        jP_Input.add(jP_InputData, java.awt.BorderLayout.CENTER);

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel9.setPreferredSize(new java.awt.Dimension(100, 110));
        jPanel9.setLayout(null);

        jButton2.setText("File");
        jButton2.setToolTipText("Click here to select the input data file.");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton2);
        jButton2.setBounds(620, 30, 80, 30);

        jB_ReadData.setText("Read Data");
        jB_ReadData.setToolTipText("After select the input file, click here to read the data.");
        jB_ReadData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_ReadDataActionPerformed(evt);
            }
        });
        jPanel9.add(jB_ReadData);
        jB_ReadData.setBounds(720, 30, 150, 30);

        jButton6.setText("Next Step");
        jButton6.setToolTipText("Go to next step.");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton6);
        jButton6.setBounds(890, 30, 110, 30);

        jCB_TransposeMatrix.setText("Transpose the matrix?");
        jCB_TransposeMatrix.setToolTipText("The variables must be at rows and features/samples or time series must be at columns. Mark this check box if needed.");
        jCB_TransposeMatrix.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jPanel9.add(jCB_TransposeMatrix);
        jCB_TransposeMatrix.setBounds(720, 70, 190, 35);

        jButton32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/help.jpg"))); // NOI18N
        jButton32.setToolTipText("Shows help information about this panel.");
        jButton32.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton32);
        jButton32.setBounds(960, 70, 35, 35);

        jCB_DataTitlesColumns.setText("There are titles in first column?");
        jCB_DataTitlesColumns.setToolTipText("If data file has variable descriptions or titles in first column, mark this check box.");
        jPanel9.add(jCB_DataTitlesColumns);
        jCB_DataTitlesColumns.setBounds(420, 70, 290, 35);

        jCB_ColumnDescription.setText("There are column description in first row?");
        jCB_ColumnDescription.setToolTipText("If data file has column descriptions in first row, mark this check box.");
        jPanel9.add(jCB_ColumnDescription);
        jCB_ColumnDescription.setBounds(70, 70, 330, 35);

        jTF_InputFile.setToolTipText("Click on File button or fill this text box with the path of the input data file.");
        jTF_InputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTF_InputFileActionPerformed(evt);
            }
        });
        jPanel9.add(jTF_InputFile);
        jTF_InputFile.setBounds(140, 30, 480, 30);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Input File: ");
        jPanel9.add(jLabel16);
        jLabel16.setBounds(10, 30, 130, 30);

        jP_Input.add(jPanel9, java.awt.BorderLayout.NORTH);

        jTabbedPane1.addTab("Input Data", jP_Input);

        jP_Quantization.setLayout(new java.awt.BorderLayout(5, 5));

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel8.setMinimumSize(new java.awt.Dimension(0, 110));
        jPanel8.setPreferredSize(new java.awt.Dimension(14, 110));
        jPanel8.setLayout(null);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Quantity of Values: ");
        jPanel8.add(jLabel3);
        jLabel3.setBounds(10, 30, 140, 30);

        jB_ApplyQuantization.setText("Apply Quantization (columns)");
        jB_ApplyQuantization.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_ApplyQuantizationActionPerformed(evt);
            }
        });
        jPanel8.add(jB_ApplyQuantization);
        jB_ApplyQuantization.setBounds(230, 30, 280, 30);

        jButton11.setText("Next Step");
        jButton11.setToolTipText("Go to next step.");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton11);
        jButton11.setBounds(890, 30, 110, 30);

        jCB_HasLabels.setText("The last column stores the labels of the classes.");
        jCB_HasLabels.setToolTipText("If data file has labels of the classes, these labels must be at last column and you must mark this check box.");
        jCB_HasLabels.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCB_HasLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_HasLabelsActionPerformed(evt);
            }
        });
        jPanel8.add(jCB_HasLabels);
        jCB_HasLabels.setBounds(150, 70, 580, 30);

        jB_ApplyQuantization1.setText("Apply Quantization (rows)");
        jB_ApplyQuantization1.setToolTipText("After selecting discretization value, click here to perform the quantization by rows.");
        jB_ApplyQuantization1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_ApplyQuantization1ActionPerformed(evt);
            }
        });
        jPanel8.add(jB_ApplyQuantization1);
        jB_ApplyQuantization1.setBounds(530, 30, 240, 30);

        jButton33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/help.jpg"))); // NOI18N
        jButton33.setToolTipText("Shows help information about this panel.");
        jButton33.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton33);
        jButton33.setBounds(960, 70, 35, 35);

        jButton18.setText("Cycle?");
        jButton18.setToolTipText("Search for cycles among columns.");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton18);
        jButton18.setBounds(790, 30, 90, 30);
        jButton18.getAccessibleContext().setAccessibleDescription("Search for cycles among columns that present equal values.");

        jS_QuantizationValue.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(2), Integer.valueOf(1), null, Integer.valueOf(1)));
        jS_QuantizationValue.setToolTipText("Fill this text box with an integer value, which represents the quantization level used by quantization process.");
        jPanel8.add(jS_QuantizationValue);
        jS_QuantizationValue.setBounds(150, 30, 60, 30);

        jP_Quantization.add(jPanel8, java.awt.BorderLayout.NORTH);

        jP_QuantizedData.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jP_QuantizedData.setLayout(new java.awt.BorderLayout(5, 5));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel13.setForeground(new java.awt.Color(0, 0, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Quantized data");
        jLabel13.setOpaque(true);
        jLabel13.setPreferredSize(new java.awt.Dimension(40, 30));
        jP_QuantizedData.add(jLabel13, java.awt.BorderLayout.NORTH);

        jSP_QuantizedData.setAutoscrolls(true);

        jT_QuantizedData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jT_QuantizedData.setToolTipText("This table shows the quantized data.");
        jSP_QuantizedData.setViewportView(jT_QuantizedData);

        jP_QuantizedData.add(jSP_QuantizedData, java.awt.BorderLayout.CENTER);

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 12));
        jButton4.setForeground(new java.awt.Color(0, 0, 255));
        jButton4.setText("Save Quantized Data");
        jButton4.setToolTipText("Click to save the quantized data into a text file.");
        jButton4.setPreferredSize(new java.awt.Dimension(163, 30));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jP_QuantizedData.add(jButton4, java.awt.BorderLayout.SOUTH);

        jP_Quantization.add(jP_QuantizedData, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Quantization", jP_Quantization);

        jP_SFS.setLayout(new java.awt.BorderLayout(3, 3));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setMinimumSize(new java.awt.Dimension(150, 90));
        jPanel3.setPreferredSize(new java.awt.Dimension(500, 250));
        jPanel3.setLayout(new java.awt.BorderLayout(3, 3));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel14.setForeground(new java.awt.Color(0, 0, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Results");
        jLabel14.setOpaque(true);
        jLabel14.setPreferredSize(new java.awt.Dimension(40, 30));
        jPanel3.add(jLabel14, java.awt.BorderLayout.NORTH);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(460, 100));

        jTA_SelectedFeaturesSE.setColumns(20);
        jTA_SelectedFeaturesSE.setRows(5);
        jTA_SelectedFeaturesSE.setToolTipText("Feature selection results.");
        jTA_SelectedFeaturesSE.setPreferredSize(null);
        jScrollPane1.setViewportView(jTA_SelectedFeaturesSE);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.EAST);

        jTA_SaidaSE.setColumns(20);
        jTA_SaidaSE.setRows(5);
        jTA_SaidaSE.setToolTipText("Classification results.");
        jTA_SaidaSE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTA_SaidaSEMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTA_SaidaSE);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jBtnSaveResultsSE.setFont(new java.awt.Font("Tahoma", 1, 12));
        jBtnSaveResultsSE.setForeground(new java.awt.Color(0, 0, 255));
        jBtnSaveResultsSE.setText("Save Results");
        jBtnSaveResultsSE.setToolTipText("Click to save results into a text file.");
        jBtnSaveResultsSE.setPreferredSize(new java.awt.Dimension(104, 30));
        jBtnSaveResultsSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnSaveResultsSEActionPerformed(evt);
            }
        });
        jPanel3.add(jBtnSaveResultsSE, java.awt.BorderLayout.SOUTH);

        jP_SFS.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setPreferredSize(new java.awt.Dimension(310, 380));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel14.setPreferredSize(new java.awt.Dimension(450, 220));
        jPanel14.setLayout(new java.awt.BorderLayout());

        jP_SelectSearchAlgorithm.setBorder(javax.swing.BorderFactory.createTitledBorder("Search Method"));
        jP_SelectSearchAlgorithm.setDoubleBuffered(false);
        jP_SelectSearchAlgorithm.setMinimumSize(new java.awt.Dimension(130, 30));
        jP_SelectSearchAlgorithm.setPreferredSize(new java.awt.Dimension(200, 80));
        jP_SelectSearchAlgorithm.setLayout(new java.awt.GridLayout(7, 1));

        buttonGroup_SE.add(jRB_SFSSE);
        jRB_SFSSE.setText("SFS");
        jRB_SFSSE.setToolTipText("Apply SFS algorithm for feature selection.");
        jRB_SFSSE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRB_SFSSE.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jRB_SFSSE.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jRB_SFSSE.setPreferredSize(new java.awt.Dimension(41, 30));
        jRB_SFSSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_SFSSEActionPerformed(evt);
            }
        });
        jP_SelectSearchAlgorithm.add(jRB_SFSSE);

        buttonGroup_SE.add(jRB_SFFSSE);
        jRB_SFFSSE.setSelected(true);
        jRB_SFFSSE.setText("SFFS");
        jRB_SFFSSE.setToolTipText("Apply SFFS algorithm for feature selection.");
        jRB_SFFSSE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRB_SFFSSE.setPreferredSize(new java.awt.Dimension(41, 30));
        jRB_SFFSSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_SFFSSEActionPerformed(evt);
            }
        });
        jP_SelectSearchAlgorithm.add(jRB_SFFSSE);

        buttonGroup_SE.add(jRB_ESSE);
        jRB_ESSE.setText("Exhaustive Search");
        jRB_ESSE.setToolTipText("Apply Exhaustive algorithm for feature selection.");
        jRB_ESSE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRB_ESSE.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jRB_ESSE.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jRB_ESSE.setPreferredSize(new java.awt.Dimension(41, 30));
        jRB_ESSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_ESSEActionPerformed(evt);
            }
        });
        jP_SelectSearchAlgorithm.add(jRB_ESSE);

        jLabel29.setText("Maximum Set Size:");
        jLabel29.setPreferredSize(new java.awt.Dimension(41, 30));
        jP_SelectSearchAlgorithm.add(jLabel29);

        jS_MaxSetSizeSE.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(5), Integer.valueOf(1), null, Integer.valueOf(1)));
        jS_MaxSetSizeSE.setToolTipText("Select the maximum cardinality of the feature set to perform the search (SFFS only).");
        jP_SelectSearchAlgorithm.add(jS_MaxSetSizeSE);

        jLabel31.setText("Size of the Result List:");
        jLabel31.setPreferredSize(new java.awt.Dimension(41, 30));
        jP_SelectSearchAlgorithm.add(jLabel31);

        jS_MaxResultListSE.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(5), Integer.valueOf(1), null, Integer.valueOf(1)));
        jS_MaxResultListSE.setToolTipText("Select the maximum size of the result dataset.");
        jS_MaxResultListSE.setPreferredSize(new java.awt.Dimension(41, 30));
        jS_MaxResultListSE.setValue(5);
        jP_SelectSearchAlgorithm.add(jS_MaxResultListSE);

        jPanel14.add(jP_SelectSearchAlgorithm, java.awt.BorderLayout.EAST);
        jP_SelectSearchAlgorithm.getAccessibleContext().setAccessibleName("Select Feature Selector\n");

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Execution of the Feature Selector and Classifier"));
        jPanel12.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel12.setPreferredSize(new java.awt.Dimension(100, 180));
        jPanel12.setLayout(null);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Penalization Method: ");
        jPanel12.add(jLabel4);
        jLabel4.setBounds(10, 70, 170, 30);

        jCB_PenalizationSE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "no_obs", "poor_obs" }));
        jCB_PenalizationSE.setToolTipText("no_obs (penalty for non-observed instances)  poor_obs (penalty for poorly observed instances)");
        jCB_PenalizationSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_PenalizationSEActionPerformed(evt);
            }
        });
        jPanel12.add(jCB_PenalizationSE);
        jCB_PenalizationSE.setBounds(180, 70, 120, 30);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Alpha (value for penalty): ");
        jPanel12.add(jLabel5);
        jLabel5.setBounds(310, 70, 210, 30);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("q-Entropy (Tsallis): ");
        jPanel12.add(jLabel2);
        jLabel2.setBounds(310, 30, 210, 30);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Input Test Set (optional): ");
        jPanel12.add(jLabel11);
        jLabel11.setBounds(5, 160, 185, 30);

        jTF_InputTestSE.setToolTipText("Click on File button or fill this text box with the path of the test data file.");
        jPanel12.add(jTF_InputTestSE);
        jTF_InputTestSE.setBounds(190, 160, 530, 30);

        jButton3.setText("File");
        jButton3.setToolTipText("Click here to select a test set file.");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel12.add(jButton3);
        jButton3.setBounds(720, 160, 80, 30);

        jButton12.setText("Execute Feature Selector");
        jButton12.setToolTipText("Execute feature selection on input data. The input data must have variable labels on its last column.");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel12.add(jButton12);
        jButton12.setBounds(50, 200, 260, 30);

        jProgressBarSE.setToolTipText("Display the progress of the execution.");
        jProgressBarSE.setStringPainted(true);
        jPanel12.add(jProgressBarSE);
        jProgressBarSE.setBounds(330, 200, 470, 30);

        jButton35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/help.jpg"))); // NOI18N
        jButton35.setToolTipText("Shows help information about this panel.");
        jButton35.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });
        jPanel12.add(jButton35);
        jButton35.setBounds(765, 15, 35, 35);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Criterion Function: ");
        jPanel12.add(jLabel7);
        jLabel7.setBounds(10, 30, 170, 30);

        jCB_CriterionFunctionSE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Entropy", "CoD" }));
        jCB_CriterionFunctionSE.setToolTipText("Select the criterion function based on classifier information (mean conditional entropy) or based on classifier error (CoD - Coefficient of Determination).");
        jCB_CriterionFunctionSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_CriterionFunctionSEActionPerformed(evt);
            }
        });
        jPanel12.add(jCB_CriterionFunctionSE);
        jCB_CriterionFunctionSE.setBounds(180, 30, 120, 30);

        jLabelConfidenceSE.setText("Beta (value of confidence) 80% : ");
        jPanel12.add(jLabelConfidenceSE);
        jLabelConfidenceSE.setBounds(180, 110, 290, 40);

        jSliderBetaSE.setMajorTickSpacing(20);
        jSliderBetaSE.setMinorTickSpacing(5);
        jSliderBetaSE.setPaintLabels(true);
        jSliderBetaSE.setToolTipText("Beta value is attributed to the conditional probability of the observed class, given the instance (poor_obs only).");
        jSliderBetaSE.setValue(80);
        jSliderBetaSE.setEnabled(false);
        jSliderBetaSE.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderBetaSEStateChanged(evt);
            }
        });
        jPanel12.add(jSliderBetaSE);
        jSliderBetaSE.setBounds(390, 110, 330, 40);

        jS_QEntropySE.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(0.1f), null, Float.valueOf(0.1f)));
        jS_QEntropySE.setToolTipText("Use 1 to apply Shannon Entropy.  Use a value <> 1 to apply Tsallis Entropy (Entropy only).");
        jPanel12.add(jS_QEntropySE);
        jS_QEntropySE.setBounds(520, 30, 60, 30);

        jS_AlphaSE.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(0.0f), null, Float.valueOf(0.1f)));
        jS_AlphaSE.setToolTipText("Alpha value represents the probability mass for the non-observed instances (no_obs only).");
        jPanel12.add(jS_AlphaSE);
        jS_AlphaSE.setBounds(520, 70, 60, 30);

        jPanel14.add(jPanel12, java.awt.BorderLayout.CENTER);

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Network Identification"));
        jPanel15.setMinimumSize(new java.awt.Dimension(100, 65));
        jPanel15.setPreferredSize(new java.awt.Dimension(100, 65));
        jPanel15.setLayout(null);

        jButton5.setText("Network Inference");
        jButton5.setToolTipText("Apply feature selection algorithm to find relationship among variables (genes), and as a result, displays a graph in which variables are pesented as nodes and its relationships as edges.");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel15.add(jButton5);
        jButton5.setBounds(810, 20, 190, 30);

        jCB_TargetsAsPredictors.setText("Targets as Predictors?");
        jCB_TargetsAsPredictors.setToolTipText("Select this option to generate graph from targets (not selected) or predictors (selected).");
        jCB_TargetsAsPredictors.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCB_TargetsAsPredictors.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCB_TargetsAsPredictors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_TargetsAsPredictorsActionPerformed(evt);
            }
        });
        jPanel15.add(jCB_TargetsAsPredictors);
        jCB_TargetsAsPredictors.setBounds(490, 15, 190, 25);

        jCB_Periodic.setText("Is it periodic?");
        jCB_Periodic.setToolTipText("Mark this option to assume that time series is periodic, i. e. the last instant of time is connected with first instant of time.");
        jCB_Periodic.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCB_Periodic.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel15.add(jCB_Periodic);
        jCB_Periodic.setBounds(490, 35, 190, 25);

        jLabel_TargetsPredictors.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel_TargetsPredictors.setText("Target's indexes: ");
        jPanel15.add(jLabel_TargetsPredictors);
        jLabel_TargetsPredictors.setBounds(10, 20, 140, 30);

        jTF_Target.setToolTipText("Fill this text box with predictors or targets indexes to find others genes related with them. \nIf this text box is empty, all genes are considered as target for the Network Identification.");
        jTF_Target.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTF_TargetActionPerformed(evt);
            }
        });
        jPanel15.add(jTF_Target);
        jTF_Target.setBounds(150, 20, 150, 30);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Threshold: ");
        jPanel15.add(jLabel21);
        jLabel21.setBounds(640, 20, 110, 30);

        jS_ThresholdEntropy.setToolTipText("Choose a real value to visualize all graph edges (near 1) or only the most representative ones (near 0).");
        jPanel15.add(jS_ThresholdEntropy);
        jS_ThresholdEntropy.setBounds(750, 20, 50, 30);

        buttonGroup_DataType.add(jRBSteadyState);
        jRBSteadyState.setText("steady-state data");
        jRBSteadyState.setToolTipText("Select this option if your data represents an independent gene expressions, i.e., it is considered by the method relationships among variables/genes (rows) only within each experiment (columns).");
        jRBSteadyState.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSteadyStateActionPerformed(evt);
            }
        });
        jPanel15.add(jRBSteadyState);
        jRBSteadyState.setBounds(310, 35, 180, 25);

        buttonGroup_DataType.add(jRBTimeSeries);
        jRBTimeSeries.setSelected(true);
        jRBTimeSeries.setText("time-series data");
        jRBTimeSeries.setToolTipText("Select this option if your data represents a time-series gene expressions, i.e., it is considered by the method time-dependent relationship among the variables/genes (rows) and its observations/samples (columns). The predictors are observed at time t and the targets are observed at time t+1");
        jRBTimeSeries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBTimeSeriesActionPerformed(evt);
            }
        });
        jPanel15.add(jRBTimeSeries);
        jRBTimeSeries.setBounds(310, 15, 180, 25);

        jPanel14.add(jPanel15, java.awt.BorderLayout.SOUTH);

        jPanel5.add(jPanel14, java.awt.BorderLayout.CENTER);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Graphics and Utilities"));
        jPanel13.setMinimumSize(new java.awt.Dimension(100, 65));
        jPanel13.setPreferredSize(new java.awt.Dimension(100, 65));
        jPanel13.setLayout(null);

        jButton15.setText("Scatter Plot");
        jButton15.setToolTipText("Display a scatter plot chart with two selected features indexes (columns) of the input data.");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });
        jPanel13.add(jButton15);
        jButton15.setBounds(5, 20, 120, 30);

        jBtn_ParallelCoord_SE.setText("Parallel Coordinates");
        jBtn_ParallelCoord_SE.setToolTipText("Display parallel coordinates chart with selected features indexes (columns) from the input data.");
        jBtn_ParallelCoord_SE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_ParallelCoord_SEActionPerformed(evt);
            }
        });
        jPanel13.add(jBtn_ParallelCoord_SE);
        jBtn_ParallelCoord_SE.setBounds(130, 20, 180, 30);

        jCB_NormalizeValuesSE.setText("Normalize values");
        jCB_NormalizeValuesSE.setToolTipText("Mark this check box to plot normalized values between 0 and 1 in parallel coordinates chart.");
        jCB_NormalizeValuesSE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jPanel13.add(jCB_NormalizeValuesSE);
        jCB_NormalizeValuesSE.setBounds(315, 35, 170, 20);

        jButton28.setText("Signal Plot");
        jButton28.setToolTipText("Display a signal plot of the selected variables indexes (rows) of the input data.");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });
        jPanel13.add(jButton28);
        jButton28.setBounds(490, 20, 110, 30);

        jButton16.setText("Correlation Coefficient");
        jButton16.setToolTipText("Display a table with correlation coefficient of the selected features indexes (columns) of the input data.");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        jPanel13.add(jButton16);
        jButton16.setBounds(605, 20, 195, 30);

        jCB_MeanParallelCoordSE.setText("Average values only");
        jCB_MeanParallelCoordSE.setToolTipText("Mark this check box to plot only the average values of the features/samples (considering all variables) in a parallel coordinates chart.");
        jCB_MeanParallelCoordSE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jPanel13.add(jCB_MeanParallelCoordSE);
        jCB_MeanParallelCoordSE.setBounds(315, 15, 180, 20);

        jButton8.setText("View Inferred Network");
        jButton8.setToolTipText("Displays a graph in which variables are pesented as nodes and its relationships as edges.");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel13.add(jButton8);
        jButton8.setBounds(805, 20, 200, 30);

        jPanel5.add(jPanel13, java.awt.BorderLayout.SOUTH);

        jP_SFS.add(jPanel5, java.awt.BorderLayout.NORTH);

        jTabbedPane1.addTab("Single Execution / Network Identification", jP_SFS);

        jP_NetVis.setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);

        jButton9.setText("Open");
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton9);

        jButton10.setText("Save Network");
        jButton10.setFocusable(false);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton10);

        jButton14.setText("Save Image");
        jButton14.setFocusable(false);
        jButton14.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton14.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton14);

        jButton19.setText("Change Color");
        jButton19.setFocusable(false);
        jButton19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton19.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton19);

        jChBoxOnlineSearch.setText("Online Search?");
        jChBoxOnlineSearch.setFocusable(false);
        jChBoxOnlineSearch.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jChBoxOnlineSearch.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jChBoxOnlineSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jChBoxOnlineSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChBoxOnlineSearchActionPerformed(evt);
            }
        });
        jToolBar1.add(jChBoxOnlineSearch);

        jP_NetVis.add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jTabbedPane1.addTab("Network Visualization", jP_NetVis);

        jP_CV.setLayout(new java.awt.BorderLayout(3, 3));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setPreferredSize(new java.awt.Dimension(409, 330));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Graphics and Utilities"));
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 150));
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 65));
        jPanel1.setLayout(null);

        jButton7.setText("CV Results");
        jButton7.setToolTipText("Display a chart with classification results, obtained by cross validation execution.");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton7);
        jButton7.setBounds(805, 20, 200, 30);

        jBtn_ScatterPlot.setText("Scatter Plot");
        jBtn_ScatterPlot.setToolTipText("Display a scatter plot chart with two selected features indexes (columns) of the input data.");
        jPanel1.add(jBtn_ScatterPlot);
        jBtn_ScatterPlot.setBounds(5, 20, 120, 30);

        jBtn_ParallelCoord_CV.setText("Parallel Coordinates");
        jBtn_ParallelCoord_CV.setToolTipText("Display parallel coordinates chart with selected features indexes (columns) from the input data.");
        jBtn_ParallelCoord_CV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_ParallelCoord_CVActionPerformed(evt);
            }
        });
        jPanel1.add(jBtn_ParallelCoord_CV);
        jBtn_ParallelCoord_CV.setBounds(130, 20, 180, 30);

        jButton17.setText("Correlation Coefficient");
        jButton17.setToolTipText("Display a table with correlation coefficient of the selected features indexes (columns) of the input data.");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton17);
        jButton17.setBounds(605, 20, 195, 30);

        jButton36.setText("Signal Plot");
        jButton36.setToolTipText("Display a signal plot of the selected variable indexes (rows) of the input data.");
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton36);
        jButton36.setBounds(490, 20, 110, 30);

        jCB_MeanParallelCoordCV.setText("Average values only");
        jCB_MeanParallelCoordCV.setToolTipText("Mark this check box to plot only the average values of the features (considering all variables) in a parallel coordinates chart.");
        jCB_MeanParallelCoordCV.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jPanel1.add(jCB_MeanParallelCoordCV);
        jCB_MeanParallelCoordCV.setBounds(315, 15, 180, 20);

        jCB_NormalizeValuesCV.setText("Normalize values");
        jCB_NormalizeValuesCV.setToolTipText("Mark this check box to plot normalized values between 0 and 1 in parallel coordinates chart.");
        jCB_NormalizeValuesCV.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jPanel1.add(jCB_NormalizeValuesCV);
        jCB_NormalizeValuesCV.setBounds(315, 35, 180, 20);

        jPanel2.add(jPanel1, java.awt.BorderLayout.SOUTH);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Search Method"));
        jPanel10.setMinimumSize(new java.awt.Dimension(130, 30));
        jPanel10.setPreferredSize(new java.awt.Dimension(200, 80));
        jPanel10.setLayout(new java.awt.GridLayout(7, 1));

        buttonGroup_CV.add(jRB_SFSCV);
        jRB_SFSCV.setText("SFS");
        jRB_SFSCV.setToolTipText("Apply SFS algorithm for feature selection.");
        jRB_SFSCV.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRB_SFSCV.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jRB_SFSCV.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jRB_SFSCV.setPreferredSize(new java.awt.Dimension(107, 30));
        jRB_SFSCV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_SFSCVActionPerformed(evt);
            }
        });
        jPanel10.add(jRB_SFSCV);

        buttonGroup_CV.add(jRB_SFFSCV);
        jRB_SFFSCV.setSelected(true);
        jRB_SFFSCV.setText("SFFS");
        jRB_SFFSCV.setToolTipText("Apply SFFS algorithm for feature selection.");
        jRB_SFFSCV.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRB_SFFSCV.setPreferredSize(new java.awt.Dimension(107, 30));
        jRB_SFFSCV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_SFFSCVActionPerformed(evt);
            }
        });
        jPanel10.add(jRB_SFFSCV);

        buttonGroup_CV.add(jRB_ESCV);
        jRB_ESCV.setText("Exhaustive Search");
        jRB_ESCV.setToolTipText("Apply Exhaustive algorithm for feature selection.");
        jRB_ESCV.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRB_ESCV.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jRB_ESCV.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jRB_ESCV.setPreferredSize(new java.awt.Dimension(107, 30));
        jRB_ESCV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_ESCVActionPerformed(evt);
            }
        });
        jPanel10.add(jRB_ESCV);

        jLabel30.setText("Maximum Set Size:");
        jLabel30.setPreferredSize(new java.awt.Dimension(107, 30));
        jPanel10.add(jLabel30);

        jS_MaxSetSizeCV.setToolTipText("Select the maximum cardinality of the feature set to perform the search (SFFS only).");
        jS_MaxSetSizeCV.setPreferredSize(new java.awt.Dimension(107, 30));
        jPanel10.add(jS_MaxSetSizeCV);

        jPanel2.add(jPanel10, java.awt.BorderLayout.EAST);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Execution of the Cross Validation"));
        jPanel11.setMinimumSize(new java.awt.Dimension(200, 180));
        jPanel11.setLayout(null);

        jProgressBarCV.setToolTipText("Display the progress of the cross validation execution.");
        jProgressBarCV.setStringPainted(true);
        jPanel11.add(jProgressBarCV);
        jProgressBarCV.setBounds(330, 215, 470, 30);

        jButton1.setText("Execute Cross Validation");
        jButton1.setToolTipText("Execute cross validation on input data.");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton1);
        jButton1.setBounds(50, 215, 260, 30);

        jSliderCV.setMajorTickSpacing(20);
        jSliderCV.setMinorTickSpacing(5);
        jSliderCV.setPaintLabels(true);
        jSliderCV.setToolTipText("Select training set size to perform cross validation.");
        jSliderCV.setValue(80);
        jSliderCV.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderCVStateChanged(evt);
            }
        });
        jPanel11.add(jSliderCV);
        jSliderCV.setBounds(180, 155, 400, 50);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Training set size (80%):");
        jPanel11.add(jLabel10);
        jLabel10.setBounds(10, 165, 170, 40);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Number of executions: ");
        jPanel11.add(jLabel6);
        jLabel6.setBounds(570, 165, 170, 40);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Penalization Method: ");
        jPanel11.add(jLabel9);
        jLabel9.setBounds(10, 70, 170, 30);

        jCB_PenalizationCV.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "no_obs", "poor_obs" }));
        jCB_PenalizationCV.setToolTipText("no_obs (penalty for non-observed instances)   poor_obs (penalty for poorly observed instances)");
        jCB_PenalizationCV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_PenalizationCVActionPerformed(evt);
            }
        });
        jPanel11.add(jCB_PenalizationCV);
        jCB_PenalizationCV.setBounds(180, 70, 120, 30);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Alpha (value for penalty): ");
        jPanel11.add(jLabel18);
        jLabel18.setBounds(300, 70, 220, 30);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("q-Entropy (Tsallis): ");
        jPanel11.add(jLabel19);
        jLabel19.setBounds(310, 30, 210, 30);

        jButton34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/help.jpg"))); // NOI18N
        jButton34.setToolTipText("Shows help information about this panel.");
        jButton34.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton34);
        jButton34.setBounds(765, 15, 35, 35);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Criterion Function: ");
        jPanel11.add(jLabel8);
        jLabel8.setBounds(10, 30, 170, 30);

        jCB_CriterionFunctionCV.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Entropy", "CoD" }));
        jCB_CriterionFunctionCV.setToolTipText("Select the criterion function based on classifier information (mean conditional entropy) or based on classifier error (CoD - Coefficient of Determination).");
        jCB_CriterionFunctionCV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_CriterionFunctionCVActionPerformed(evt);
            }
        });
        jPanel11.add(jCB_CriterionFunctionCV);
        jCB_CriterionFunctionCV.setBounds(180, 30, 120, 30);

        jLabelConfidenceCV.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelConfidenceCV.setText("Beta (value of confidence) 80% : ");
        jPanel11.add(jLabelConfidenceCV);
        jLabelConfidenceCV.setBounds(180, 110, 290, 40);

        jSliderBetaCV.setMajorTickSpacing(20);
        jSliderBetaCV.setMinorTickSpacing(5);
        jSliderBetaCV.setPaintLabels(true);
        jSliderBetaCV.setToolTipText("Beta value is attributed to the conditional probability of the observed class, given the instance (poor_obs only).");
        jSliderBetaCV.setValue(80);
        jSliderBetaCV.setEnabled(false);
        jSliderBetaCV.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderBetaCVStateChanged(evt);
            }
        });
        jPanel11.add(jSliderBetaCV);
        jSliderBetaCV.setBounds(470, 100, 330, 37);

        jS_NrExecutionsCV.setToolTipText("Fill this text box with an integer value, which represents the number of executions of training and test of the cross validation.");
        jPanel11.add(jS_NrExecutionsCV);
        jS_NrExecutionsCV.setBounds(740, 165, 60, 40);

        jS_QEntropyCV.setToolTipText("Use 1 to apply Shannon Entropy.  Use a value <> 1 to apply Tsallis Entropy (Entropy only).");
        jPanel11.add(jS_QEntropyCV);
        jS_QEntropyCV.setBounds(520, 30, 60, 30);

        jS_AlphaCV.setToolTipText("Alpha value represents the probability mass for the non-observed instances (no_obs only).");
        jPanel11.add(jS_AlphaCV);
        jS_AlphaCV.setBounds(520, 70, 60, 30);

        jPanel2.add(jPanel11, java.awt.BorderLayout.CENTER);

        jP_CV.add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel6.setMinimumSize(new java.awt.Dimension(150, 90));
        jPanel6.setPreferredSize(new java.awt.Dimension(500, 250));
        jPanel6.setLayout(new java.awt.BorderLayout(3, 3));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel15.setForeground(new java.awt.Color(0, 0, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Results");
        jLabel15.setOpaque(true);
        jLabel15.setPreferredSize(new java.awt.Dimension(40, 30));
        jPanel6.add(jLabel15, java.awt.BorderLayout.NORTH);

        jScrollPane3.setPreferredSize(new java.awt.Dimension(460, 100));

        jTA_SelectedFeaturesCV.setColumns(20);
        jTA_SelectedFeaturesCV.setRows(5);
        jTA_SelectedFeaturesCV.setToolTipText("Feature selection results.");
        jScrollPane3.setViewportView(jTA_SelectedFeaturesCV);

        jPanel6.add(jScrollPane3, java.awt.BorderLayout.EAST);

        jTA_SaidaCV.setColumns(20);
        jTA_SaidaCV.setRows(5);
        jTA_SaidaCV.setToolTipText("Classification results.");
        jScrollPane4.setViewportView(jTA_SaidaCV);

        jPanel6.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        jBtnSaveResultsCV.setFont(new java.awt.Font("Tahoma", 1, 12));
        jBtnSaveResultsCV.setForeground(new java.awt.Color(0, 0, 255));
        jBtnSaveResultsCV.setText("Save Results");
        jBtnSaveResultsCV.setToolTipText("Click to save results into a text file.");
        jBtnSaveResultsCV.setPreferredSize(new java.awt.Dimension(104, 30));
        jBtnSaveResultsCV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnSaveResultsCVActionPerformed(evt);
            }
        });
        jPanel6.add(jBtnSaveResultsCV, java.awt.BorderLayout.SOUTH);

        jP_CV.add(jPanel6, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Cross Validation", jP_CV);

        getContentPane().add(jTabbedPane1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1024)/2, (screenSize.height-768)/2, 1024, 768);
    }// </editor-fold>//GEN-END:initComponents

    private void jCB_PenalizationCVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_PenalizationCVActionPerformed
        if (jCB_PenalizationCV.getSelectedIndex() == 0) {
            jS_AlphaCV.setEnabled(true);
            jSliderBetaCV.setEnabled(false);
        } else {
            jS_AlphaCV.setEnabled(false);
            jSliderBetaCV.setEnabled(true);
        }
}//GEN-LAST:event_jCB_PenalizationCVActionPerformed

    private void jSliderBetaCVStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderBetaCVStateChanged
        jLabelConfidenceCV.setText("Beta (value of confidence) "
                + jSliderBetaCV.getValue() + "% : ");
    }//GEN-LAST:event_jSliderBetaCVStateChanged

    private void jSliderBetaSEStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderBetaSEStateChanged
        jLabelConfidenceSE.setText("Beta (value of confidence) "
                + jSliderBetaSE.getValue() + "% : ");
    }//GEN-LAST:event_jSliderBetaSEStateChanged

    private void jCB_CriterionFunctionCVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_CriterionFunctionCVActionPerformed
        if (jCB_CriterionFunctionCV.getSelectedIndex() == 1) {
            jS_QEntropyCV.setEnabled(false);
        } else {
            jS_QEntropyCV.setEnabled(true);
        }
}//GEN-LAST:event_jCB_CriterionFunctionCVActionPerformed

    private void jCB_CriterionFunctionSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_CriterionFunctionSEActionPerformed
        if (jCB_CriterionFunctionSE.getSelectedIndex() == 1) {
            jS_QEntropySE.setEnabled(false);
        } else {
            jS_QEntropySE.setEnabled(true);
        }
}//GEN-LAST:event_jCB_CriterionFunctionSEActionPerformed

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        if (Mo != null) {
            try {
                SinalPlotReadValues();
            } catch (IOException error) {
                throw new FSException("Error on ScatterPlot." + error, false);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Execution Error:Select"
                    + " and read input file first.", "Application Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_F1));
    }//GEN-LAST:event_jButton35ActionPerformed

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_F1));
    }//GEN-LAST:event_jButton34ActionPerformed

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_F1));
    }//GEN-LAST:event_jButton33ActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_F1));
    }//GEN-LAST:event_jButton32ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        if (Mo != null) {
            try {
                SinalPlotReadValues();
            } catch (IOException error) {
                throw new FSException("Error on ScatterPlot." + error, false);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Execution Error:Select"
                    + " and read input file first.", "Application Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        if (Md != null) {
            CNMeasurements.FindCycle(Md);
        } else {
            JOptionPane.showMessageDialog(null, "Execution Error:Select and read input file first.", "Application Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        IOFile.saveTableInFile(jT_InputData);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        jBtn_ScatterPlotActionPerformed(evt);
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jBtn_ParallelCoord_SEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_ParallelCoord_SEActionPerformed
        if (Mo != null) {
            ParallelPlot(jCB_MeanParallelCoordSE.isSelected(), jCB_NormalizeValuesSE.isSelected());
        } else {
            JOptionPane.showMessageDialog(null, "Execution Error:Select and read input file first.", "Application Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jBtn_ParallelCoord_SEActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        if (Mo != null) {
            Correlation();
        } else {
            JOptionPane.showMessageDialog(null, "Execution Error:Select and read input file first.", "Application Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        float threshold_entropy = ((Double) jS_ThresholdEntropy.getValue()).floatValue();
        String type_entropy = (String) jCB_PenalizationSE.getSelectedItem();
        float alpha = ((Double) jS_AlphaSE.getValue()).floatValue();
        float q_entropy = ((Double) jS_QEntropySE.getValue()).floatValue();
        float beta = ((float) jSliderBetaSE.getValue() / 100);
        int search_alg = 0;
        //jTA_SelectedFeaturesSE.setText("");
        String path = null;//IOFile.saveFile();
        StringBuffer txt = null;
        int maxf = (Integer) jS_MaxSetSizeSE.getValue();
        if (q_entropy < 0 || alpha < 0) {
            //entrada de dados invalida.
            JOptionPane.showMessageDialog(null, "Error on parameter value: "
                    + "The values of q-entropy and Alpha must be positives.",
                    "Application Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (jRB_SFSSE.isSelected()) {
            search_alg = 1;
        } //SFS
        else if (jRB_ESSE.isSelected()) {
            search_alg = 9;
        }//Exhaustive
        else if (jRB_SFFSSE.isSelected()) {
            search_alg = 2;
        }//SFFS

        Vector targets = null;
        if (!jTF_Target.getText().equalsIgnoreCase("")) {
            targets = new Vector();
            String strtargets = jTF_Target.getText();
            String str = "";
            for (int i = 0; i < strtargets.length(); i++) {
                if (strtargets.charAt(i) == ' ') {
                    if (!str.equalsIgnoreCase("")) {
                        targets.add(str);
                        str = "";
                    }
                } else {
                    str += strtargets.charAt(i);
                }
            }
            //para o ultimo target da string
            if (!str.equalsIgnoreCase("")) {
                targets.add(str);
                str = "";
            }
        }

        //inverte os tempos de expressao, de forma que os targets passem
        //a ser os preditores, i.e., os preditores passem a considerar o valor
        //do target no instante de tempo posterior.
        //IOFile.printMatrix(Md);
        if (jCB_TargetsAsPredictors.isSelected()) {
            Md = Preprocessing.InvertColumns(Md);
            //IOFile.printMatrix(Md);
        }

        //vetor com os resultados da selecao de caracteristica.
        //aqui so sera analisada a primeira resposta.
        int resultsetsize = 1;//(Integer)jS_MaxResultListSE.getValue();
        int n = Main.maximumValue(Md, 0, Md.length - 1, 0, Md[0].length - 1) + 1;
        //define o parametro do tipo de dados:
        //1 == temporal
        //2 == steady state
        int datatype = 2;
        if (jRBTimeSeries.isSelected()) {
            datatype = 1;
        }

        recoverednetwork = new AGN(Md.length, Md[0].length, n, datatype);
        recoverednetwork.setTemporalsignal(Mo);
        recoverednetwork.setTemporalsignalquantized(Md);
        if (featurestitles != null) {
            recoverednetwork.setLabelstemporalsignal(featurestitles);
        }
        if (datatitles != null) {
            AGNRoutines.setGeneNames(recoverednetwork, datatitles);
            AGNRoutines.setGeneIds(recoverednetwork, datatitles);
        }
        txt = AGNRoutines.RecoverNetworkfromExpressions(
                recoverednetwork,
                null,
                datatype,//datatype: 1==temporal, 2==steady-state.
                jCB_Periodic.isSelected(),
                threshold_entropy,
                type_entropy,
                alpha,
                beta,
                q_entropy,
                targets,//target indexes
                maxf,
                search_alg,//1==SFS, 2==SFFS, 3==SFFS_stack(expandindo todos os empates encontrados), 9==Exhaustive.
                jCB_TargetsAsPredictors.isSelected(),
                resultsetsize,
                null,
                3//stacksize, used only for SFFS_Stack (option 4) == tamanho da expansao dos empatados.
                );
        LogExecucao.gravarResultado(txt.toString());
        jTA_SelectedFeaturesSE.setText(txt.toString());
        //AGNRoutines.ViewAGN(recoverednetwork, true, datatype);

        if (NetworkVisualization != null) {
            jP_NetVis.remove(NetworkVisualization);
        }

        //nova implementacao para visualizacao das redes.
        NetworkVisualization = new PrefusePanel();
        NetworkVisualization.setNetwork(recoverednetwork);
        NetworkVisualization.CreateNetwork();
        jTabbedPane1.setSelectedIndex(jTabbedPane1.getSelectedIndex() + 1);//aba da visualizacao de rede
        jP_NetVis.add(NetworkVisualization, java.awt.BorderLayout.CENTER);
        NetworkVisualization.repaint();
        dispatchEvent(new WindowEvent(this, WindowEvent.COMPONENT_RESIZED, this));
        //para atualizar a janela sem a necessidade de move-la.
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        Correlation();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jBtnSaveResultsCVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnSaveResultsCVActionPerformed
        IOFile.saveFile(jTA_SelectedFeaturesCV.getText() + "\n"
                + jTA_SaidaCV.getText());
    }//GEN-LAST:event_jBtnSaveResultsCVActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String path = IOFile.openPath();
        if (path != null) {
            jTF_InputTestSE.setText(path);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        jProgressBarSE.setValue(0);
        double alpha = (Double) jS_AlphaSE.getValue();
        double q_entropy = (Double) jS_QEntropySE.getValue();

        if (q_entropy < 0 || alpha < 0) {
            //entrada de dados invalida.
            JOptionPane.showMessageDialog(null, "Error on parameter value:"
                    + " The values of q-entropy and Alpha must be positives.",
                    "Application Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        class Thread1 extends Thread {

            @Override
            public void run() {
                try {
                    if (jRB_SFSSE.isSelected()) {
                        ExecuteFeatureSelection(1);// SFS

                    } else if (jRB_ESSE.isSelected()) {
                        ExecuteFeatureSelection(2);// ExhaustiveSearch

                    } else if (jRB_SFFSSE.isSelected()) {
                        ExecuteFeatureSelection(3);// SFFS

                    } else {
                        JOptionPane.showMessageDialog(null, "Select Feature "
                                + "Selector must be marked.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException error) {
                    throw new FSException("Error on Execution of the Search"
                            + " Method." + error, false);
                }
            }
        }
        Thread thread = new Thread1();
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.setName("SE");
        thread.start();
    }//GEN-LAST:event_jButton12ActionPerformed

    private final String[] getClasses() {
        int classes = fs.Main.maximumValue(Md, 0, lines - 1, columns - 1, columns - 1) + 1;
        String[] out = new String[classes];
        for (int i = 0; i < classes; i++) {
            out[i] = String.valueOf(i);
        }
        return (out);
    }

    public void GenerateTable(Object[][] data, String title, String subtitle,
            final String[] titles) {
        JFrame window = new JFrame(title);
        window.setLayout(new java.awt.BorderLayout(5, 5));
        JLabel label = new JLabel(subtitle);
        label.setFont(new java.awt.Font("Tahoma", 1, 12));
        label.setForeground(new java.awt.Color(0, 0, 255));
        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label.setOpaque(true);
        label.setPreferredSize(new java.awt.Dimension(50, 30));

        ListModel lm = new AbstractListModel() {

            String headers[] = titles;

            @Override
            public int getSize() {
                return headers.length;
            }

            @Override
            public Object getElementAt(int index) {
                return headers[index];
            }
        };

//        String[] titles = getClasses();
        JList rowHeader = new JList(lm);
        rowHeader.setFixedCellWidth(50);
        JTable jTable = new JTable(data, titles);
        rowHeader.setFixedCellHeight(jTable.getRowHeight());
        rowHeader.setCellRenderer(new RowHeaderRenderer(jTable));
        JScrollPane scroll = new JScrollPane(jTable);
        scroll.setRowHeaderView(rowHeader);
        getContentPane().add(scroll, BorderLayout.CENTER);

        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTable.setAlignmentX(JTable.CENTER_ALIGNMENT);

        window.add(label, java.awt.BorderLayout.NORTH);
        window.add(scroll, java.awt.BorderLayout.CENTER);

        java.awt.Dimension screenSize =
                java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        window.setBounds((screenSize.width - 800) / 2,
                (screenSize.height - 270) / 2, 800, 270);
        window.setVisible(true);
    }

    public void Correlation() {
        try {
            String features = JOptionPane.showInputDialog("Select features"
                    + " (columns) to calculate correlation coeficient:", "0 1 2");
            if (features != null) {
                StringTokenizer s = new StringTokenizer(features);
                int nrfeatures = s.countTokens();
                int[] selectedfeatures = new int[nrfeatures];
                for (int n = 0; n < nrfeatures; n++) {
                    selectedfeatures[n] = Integer.valueOf(s.nextToken());
                }

                //correlation coeficient among classes or among features.
                if (jCB_HasLabels.isSelected()) {
                    String[] titles = getClasses();
                    GenerateTable(MathRoutines.getCorrelationCoeficientClasses(
                            Mo, selectedfeatures),
                            "Correlation coeficients among classes",
                            "Correlation coeficients among classes using "
                            + "the average values of the features: " + features,
                            titles);
                } else {
                    String[] titles = new String[selectedfeatures.length];
                    for (int i = 0; i < selectedfeatures.length; i++) {
                        titles[i] = String.valueOf(selectedfeatures[i]);
                    }
                    GenerateTable(MathRoutines.getCorrelationCoeficientFeatures(
                            Mo, selectedfeatures),
                            "Correlation coeficients among features",
                            "Correlation coeficients among features: "
                            + features, titles);
                }
            }
        } catch (Exception error) {
            throw new FSException("Error on select features to correlation"
                    + " coeficient. " + error, false);
        }
    }

    public int[] ExtractIndices(StringTokenizer strfeatures) {
        int nrfeatures = strfeatures.countTokens();
        int[] features = new int[nrfeatures];
        for (int n = 0; n < nrfeatures; n++) {
            features[n] = Integer.valueOf(strfeatures.nextToken());
        }
        return (features);
    }

    public void ParallelPlot(boolean meanvaluesonly, boolean normalize) {
        try {
            String strfeatures = JOptionPane.showInputDialog("Select features"
                    + " (columns) to plot parallel coordinates:", "0 1 2");
            if (strfeatures != null) {
                int[] features = null;
                if (strfeatures.isEmpty()) {
                    //if is empty, include all features
                    features = new int[columns - has_labels];
                    for (int i = 0; i < columns - has_labels; i++) {
                        features[i] = i;
                    }
                } else {
                    StringTokenizer s = new StringTokenizer(strfeatures);
                    features = ExtractIndices(s);
                }
                //find classes.
                int classes = fs.Main.maximumValue(Mo, 0, lines - 1,
                        columns - 1, columns - 1) + 1;
                float[][] M = Preprocessing.copyMatrix(Mo);
                //normaliza os valores entre 0..1 dos features antes de plotar.
                if (normalize) {
                    Preprocessing.ScaleColumn(M, 2, has_labels);
                    //Preprocessing.ScaleRow(M, 2, has_labels);
                }
                if (meanvaluesonly) {
                    Chart.PlotMeanParallelCoordinates("Parallel Coordinates"
                            + " (Average Values)", "features", "values", M,
                            classes, features, featurestitles, datatitles);
                } else {
                    Chart.PlotParallelCoordinates("Parallel Coordinates",
                            "features", "values", M, classes, features,
                            featurestitles, datatitles);
                }
            }
        } catch (Exception error) {
            throw new FSException("Error on select features to plot parallel"
                    + " coordinates. " + error, false);
        }
    }

    private void jBtn_ParallelCoord_CVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_ParallelCoord_CVActionPerformed
        if (Mo != null) {
            ParallelPlot(jCB_MeanParallelCoordCV.isSelected(),
                    jCB_NormalizeValuesCV.isSelected());
        } else {
            JOptionPane.showMessageDialog(null, "Execution Error:Select and"
                    + " read input file first.", "Application Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jBtn_ParallelCoord_CVActionPerformed

    private void jBtn_ScatterPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_ScatterPlotActionPerformed
        if (Mo != null) {
            try {
                ScatterPlot();
            } catch (IOException error) {
                throw new FSException("Error on ScatterPlot." + error, false);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Execution Error: Select and"
                    + " read input file first.", "Application Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jBtn_ScatterPlotActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        Chart.LineChart(dataset, "Cross Validation Error with "
                + jSliderCV.getValue() + "% of samples on Training Set",
                "Number of executions", "Mean Errors", false, 0, 0);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        IOFile.saveTableInFile(jT_QuantizedData);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jSliderCVStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderCVStateChanged
        jLabel10.setText("Training set size (" + jSliderCV.getValue() + "%):");
    }//GEN-LAST:event_jSliderCVStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        double alpha = (Double) jS_AlphaCV.getValue();
        double q_entropy = (Double) jS_QEntropyCV.getValue();

        if (q_entropy < 0 || alpha < 0) {
            //entrada de dados invalida.
            JOptionPane.showMessageDialog(null, "Error on parameter value:"
                    + " The values of q-entropy and Alpha must be positives.",
                    "Application Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        class ThreadCV extends Thread {

            @Override
            public void run() {
                try {
                    if (jRB_SFSCV.isSelected()) {
                        CrossValidation((Integer) jS_NrExecutionsCV.getValue(),
                                ((float) jSliderCV.getValue()) / 100, 1);// SFS

                    } else if (jRB_ESCV.isSelected()) {
                        CrossValidation((Integer) jS_NrExecutionsCV.getValue(),
                                ((float) jSliderCV.getValue()) / 100, 2);//ExhaustiveSearch

                    } else if (jRB_SFFSCV.isSelected()) {
                        CrossValidation((Integer) jS_NrExecutionsCV.getValue(),
                                ((float) jSliderCV.getValue()) / 100, 3);// SFFS

                    } else {
                        JOptionPane.showMessageDialog(null, "Select Feature "
                                + "Selector must be marked.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException error) {
                    throw new FSException("Error on Cross-validation." + error, false);
                }
            }
        }
        Thread thread = new ThreadCV();
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.setName("CV");
        thread.start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jBtnSaveResultsSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnSaveResultsSEActionPerformed
        IOFile.saveFile(jTA_SelectedFeaturesSE.getText() + "\n"
                + jTA_SaidaSE.getText());
    }//GEN-LAST:event_jBtnSaveResultsSEActionPerformed

    private void jB_ApplyQuantizationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_ApplyQuantizationActionPerformed
        ApplyQuantization((Integer) jS_QuantizationValue.getValue(), 1);
    }//GEN-LAST:event_jB_ApplyQuantizationActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        jTabbedPane1.setSelectedIndex(jTabbedPane1.getSelectedIndex() + 1);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jB_ReadDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_ReadDataActionPerformed
        datatitles = null;
        featurestitles = null;
        flag_quantization = false;
        jP_InputData.remove(jT_InputData);
        jP_QuantizedData.remove(jT_QuantizedData);

        InputDataPreview idp = new InputDataPreview(jTF_InputFile.getText());
        idp.setVisible(true);
        
        ReadInputData(jTF_InputFile.getText());

        if (jCB_TransposeMatrix.isSelected()) {
            if (Mo != null) {
                jT_InputData = GenerateTableLabels(datatitles, featurestitles, Mo);
            }
            if (Md != null) {
                jT_QuantizedData = GenerateTableLabels(datatitles, featurestitles, Md);
            }
        } else {
            if (Mo != null) {
                jT_InputData = GenerateTableLabels(featurestitles, datatitles, Mo);
            }
            if (Md != null) {
                jT_QuantizedData = GenerateTableLabels(featurestitles, datatitles, Md);
            }
        }
        //jT_InputData = GenerateTable(Mo);
        if (Mo != null) {
            jT_InputData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            jT_InputData.setAlignmentX(JTable.CENTER_ALIGNMENT);
            jSP_InputData.setViewportView(jT_InputData);
        }

        if (Md != null) {
            flag_quantization = true;
            jT_QuantizedData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            jT_QuantizedData.setAlignmentX(JTable.CENTER_ALIGNMENT);
            jSP_QuantizedData.setViewportView(jT_QuantizedData);
        }

    }//GEN-LAST:event_jB_ReadDataActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        jTabbedPane1.setSelectedIndex(jTabbedPane1.getSelectedIndex() + 1);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String path = IOFile.openPath();
        if (path != null) {
            jTF_InputFile.setText(path);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTF_TargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTF_TargetActionPerformed
                                        }//GEN-LAST:event_jTF_TargetActionPerformed

    private void jCB_PenalizationSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_PenalizationSEActionPerformed
        if (jCB_PenalizationSE.getSelectedIndex() == 0) {
            jS_AlphaSE.setEnabled(true);
            jSliderBetaSE.setEnabled(false);
        } else {
            jS_AlphaSE.setEnabled(false);
            jSliderBetaSE.setEnabled(true);
        }
    }//GEN-LAST:event_jCB_PenalizationSEActionPerformed

    private void jCB_TargetsAsPredictorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_TargetsAsPredictorsActionPerformed
        if (jCB_TargetsAsPredictors.isSelected()) {
            jLabel_TargetsPredictors.setText("Predictor's indexes: ");
        } else {
            jLabel_TargetsPredictors.setText("Target's indexes: ");
        }
    }//GEN-LAST:event_jCB_TargetsAsPredictorsActionPerformed

    private void jB_ApplyQuantization1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_ApplyQuantization1ActionPerformed
        ApplyQuantization((Integer) jS_QuantizationValue.getValue(), 2);
    }//GEN-LAST:event_jB_ApplyQuantization1ActionPerformed

    private void jRB_SFSSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_SFSSEActionPerformed
    }//GEN-LAST:event_jRB_SFSSEActionPerformed

    private void jRB_SFFSSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_SFFSSEActionPerformed
        jS_MaxSetSizeSE.setEnabled(true);
    }//GEN-LAST:event_jRB_SFFSSEActionPerformed

    private void jRB_ESSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_ESSEActionPerformed
        jS_MaxSetSizeSE.setEnabled(false);
    }//GEN-LAST:event_jRB_ESSEActionPerformed

    private void jRB_SFSCVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_SFSCVActionPerformed
        jS_MaxSetSizeCV.setEnabled(false);
    }//GEN-LAST:event_jRB_SFSCVActionPerformed

    private void jRB_SFFSCVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_SFFSCVActionPerformed
        jS_MaxSetSizeCV.setEnabled(true);
    }//GEN-LAST:event_jRB_SFFSCVActionPerformed

    private void jRB_ESCVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_ESCVActionPerformed
        jS_MaxSetSizeCV.setEnabled(false);
    }//GEN-LAST:event_jRB_ESCVActionPerformed

    private void jCB_HasLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_HasLabelsActionPerformed
        if (jCB_HasLabels.isSelected()) {
            has_labels = 1;
        } else {
            has_labels = 0;
        }
    }//GEN-LAST:event_jCB_HasLabelsActionPerformed

    private void jTF_InputFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTF_InputFileActionPerformed
    }//GEN-LAST:event_jTF_InputFileActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        if (recoverednetwork != null) {
            //AGNRoutines.ViewAGN(recoverednetwork, true, 1);

            NetworkVisualization = new PrefusePanel();
            NetworkVisualization.setNetwork(recoverednetwork);
            NetworkVisualization.CreateNetwork();
            jTabbedPane1.setSelectedIndex(jTabbedPane1.getSelectedIndex() + 1);//aba da visualizacao de rede
            jP_NetVis.add(NetworkVisualization, java.awt.BorderLayout.CENTER);
            NetworkVisualization.repaint();
            repaint();
            dispatchEvent(new WindowEvent(this, WindowEvent.COMPONENT_RESIZED, this));
        } else {
            JOptionPane.showMessageDialog(this, "Execution Error: Click on Network Inference Button first.", "Application Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jRBSteadyStateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSteadyStateActionPerformed
        jCB_TargetsAsPredictors.setSelected(false);
        jCB_Periodic.setSelected(false);
        jCB_TargetsAsPredictorsActionPerformed(evt);
        jCB_TargetsAsPredictors.setEnabled(false);
        jCB_Periodic.setEnabled(false);
    }//GEN-LAST:event_jRBSteadyStateActionPerformed

    private void jRBTimeSeriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBTimeSeriesActionPerformed
        jCB_TargetsAsPredictors.setEnabled(true);
        jCB_Periodic.setEnabled(true);
    }//GEN-LAST:event_jRBTimeSeriesActionPerformed

    private void jTA_SaidaSEMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTA_SaidaSEMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTA_SaidaSEMouseClicked

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        NetworkVisualization.SaveGraphandPositions();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        //abrir arquivos .agn
        String path = IOFile.openAGNFile();
        if (path != null) {
            if (NetworkVisualization != null) {
                jP_NetVis.remove(NetworkVisualization);
            }
            recoverednetwork = IOFile.readAGNfromFile(path);
            NetworkVisualization = new PrefusePanel();
            NetworkVisualization.setNetwork(recoverednetwork);
            NetworkVisualization.CreateNetwork();
            jP_NetVis.add(NetworkVisualization, java.awt.BorderLayout.CENTER);
            NetworkVisualization.repaint();
            repaint();
            dispatchEvent(new WindowEvent(this, WindowEvent.COMPONENT_RESIZED, this));
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        NetworkVisualization.SaveGraphasImage();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        NetworkVisualization.ChangeClassColor();
        NetworkVisualization.repaint();
        repaint();
        dispatchEvent(new WindowEvent(this, WindowEvent.COMPONENT_RESIZED, this));

    }//GEN-LAST:event_jButton19ActionPerformed

    private void jChBoxOnlineSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChBoxOnlineSearchActionPerformed
        if (NetworkVisualization != null){
            NetworkVisualization.setSearchonline(jChBoxOnlineSearch.isSelected());
        }
    }//GEN-LAST:event_jChBoxOnlineSearchActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup_CV;
    private javax.swing.ButtonGroup buttonGroup_DataType;
    private javax.swing.ButtonGroup buttonGroup_SE;
    private javax.swing.JButton jB_ApplyQuantization;
    private javax.swing.JButton jB_ApplyQuantization1;
    private javax.swing.JButton jB_ReadData;
    private javax.swing.JButton jBtnSaveResultsCV;
    private javax.swing.JButton jBtnSaveResultsSE;
    private javax.swing.JButton jBtn_ParallelCoord_CV;
    private javax.swing.JButton jBtn_ParallelCoord_SE;
    private javax.swing.JButton jBtn_ScatterPlot;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    public javax.swing.JCheckBox jCB_ColumnDescription;
    private javax.swing.JComboBox jCB_CriterionFunctionCV;
    private javax.swing.JComboBox jCB_CriterionFunctionSE;
    public javax.swing.JCheckBox jCB_DataTitlesColumns;
    private javax.swing.JCheckBox jCB_HasLabels;
    private javax.swing.JCheckBox jCB_MeanParallelCoordCV;
    private javax.swing.JCheckBox jCB_MeanParallelCoordSE;
    private javax.swing.JCheckBox jCB_NormalizeValuesCV;
    private javax.swing.JCheckBox jCB_NormalizeValuesSE;
    private javax.swing.JComboBox jCB_PenalizationCV;
    private javax.swing.JComboBox jCB_PenalizationSE;
    private javax.swing.JCheckBox jCB_Periodic;
    private javax.swing.JCheckBox jCB_TargetsAsPredictors;
    private javax.swing.JCheckBox jCB_TransposeMatrix;
    private javax.swing.JCheckBox jChBoxOnlineSearch;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelConfidenceCV;
    private javax.swing.JLabel jLabelConfidenceSE;
    private javax.swing.JLabel jLabel_TargetsPredictors;
    private javax.swing.JPanel jP_CV;
    private javax.swing.JPanel jP_Input;
    private javax.swing.JPanel jP_InputData;
    private javax.swing.JPanel jP_NetVis;
    private javax.swing.JPanel jP_Quantization;
    private javax.swing.JPanel jP_QuantizedData;
    private javax.swing.JPanel jP_SFS;
    private javax.swing.JPanel jP_SelectSearchAlgorithm;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBarCV;
    private javax.swing.JProgressBar jProgressBarSE;
    private javax.swing.JRadioButton jRBSteadyState;
    private javax.swing.JRadioButton jRBTimeSeries;
    private javax.swing.JRadioButton jRB_ESCV;
    private javax.swing.JRadioButton jRB_ESSE;
    private javax.swing.JRadioButton jRB_SFFSCV;
    private javax.swing.JRadioButton jRB_SFFSSE;
    private javax.swing.JRadioButton jRB_SFSCV;
    private javax.swing.JRadioButton jRB_SFSSE;
    private javax.swing.JScrollPane jSP_InputData;
    private javax.swing.JScrollPane jSP_QuantizedData;
    private javax.swing.JSpinner jS_AlphaCV;
    private javax.swing.JSpinner jS_AlphaSE;
    private javax.swing.JSpinner jS_MaxResultListSE;
    private javax.swing.JSpinner jS_MaxSetSizeCV;
    private javax.swing.JSpinner jS_MaxSetSizeSE;
    private javax.swing.JSpinner jS_NrExecutionsCV;
    private javax.swing.JSpinner jS_QEntropyCV;
    private javax.swing.JSpinner jS_QEntropySE;
    private javax.swing.JSpinner jS_QuantizationValue;
    private javax.swing.JSpinner jS_ThresholdEntropy;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSlider jSliderBetaCV;
    private javax.swing.JSlider jSliderBetaSE;
    private javax.swing.JSlider jSliderCV;
    private javax.swing.JTextArea jTA_SaidaCV;
    private javax.swing.JTextArea jTA_SaidaSE;
    private javax.swing.JTextArea jTA_SelectedFeaturesCV;
    private javax.swing.JTextArea jTA_SelectedFeaturesSE;
    private javax.swing.JTextField jTF_InputFile;
    private javax.swing.JTextField jTF_InputTestSE;
    private javax.swing.JTextField jTF_Target;
    private javax.swing.JTable jT_InputData;
    private javax.swing.JTable jT_QuantizedData;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
