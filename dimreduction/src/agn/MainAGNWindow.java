/***************************************************************************/
/*** Interactive Graphic Environment for Dimensionality Reduction        ***/
/***                                                                     ***/
/*** Copyright (C) 2008  Fabricio Martins Lopes                          ***/
/***                     Roberto Marcondes Cesar Junior                  ***/
/***                     Luciano da Fontoura Costa                       ***/
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
/*** Contact: Fabricio Martins Lopes - fabriciolopes@vision.ime.usp.br   ***/
/***          Roberto Marcondes Cesar Junior - cesar@vision.ime.usp.br   ***/
/***          Luciano da Fontoura Costa - luciano@ifsc.usp.br            ***/
/***************************************************************************/
/***************************************************************************/
/*** This class implements the AGN Simulation and Validation Model.      ***/
/***                                                                     ***/
/*** Lopes FM, Cesar-Jr RM, da F Costa L: AGN Simulation and Validation  ***/
/*** Model. In Advances in Bioinformatics and Computational Biology,     ***/
/*** Third Brazilian Symposium on Bioinformatics, Volume 5167 of         ***/
/*** LNBI, Springer 2008:169-173.                                        ***/
/***                                                                     ***/
/***************************************************************************/
package agn;

import charts.Chart;
import fs.FSException;
import fs.Preprocessing;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import utilities.IOFile;
import utilities.MathRoutines;

public class MainAGNWindow extends javax.swing.JFrame {

    public static final String delimiter = String.valueOf(' ') + String.valueOf('\t') + String.valueOf('\n') + String.valueOf('\r') + String.valueOf('\f') + String.valueOf(';');
    private float[][] Mo = null;//original matrix of data
    private float[][] Md = null;//work matrix
    private int lines = 0; //number of samples
    private int columns = 0;//number of features
    //atributo para armazenar as regras geradas automaticamente.
    public AGN agn = null;
    //atributo para armazenar as regras recuperadas pelo algoritmo.
    public AGN recoverednetwork = null;
    //vetor para armazenar os rotulos dos dados
    public Vector datatitles = null;

    public MainAGNWindow() {
        initComponents();
    }

    public void ShowTemporalExpressionProfile() {
        if (agn != null) {
            //retira o componente visual da tela.
            jP_GeneratedData.remove(jT_GeneratedData);

            //recupera o sinal armazenado na rede.
            int[][] generated_data = agn.getTemporalsignalquantized();

            //exibe os dados gerados no componente visual JTable.
            Object[] titles = new Object[generated_data[0].length];
            Object[][] data = new Object[generated_data.length][generated_data[0].length];
            for (int i = 0; i < generated_data.length; i++) {
                for (int j = 0; j < generated_data[0].length; j++) {
                    if (i == 0) {
                        titles[j] = j;
                    }
                    data[i][j] = generated_data[i][j];
                }
            }
            jT_GeneratedData = new JTable(data, titles);
            jT_GeneratedData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            jT_GeneratedData.setAlignmentX(JTable.CENTER_ALIGNMENT);
            jSP_GeneratedData.setViewportView(jT_GeneratedData);
        } else {
            JOptionPane.showMessageDialog(null, "Execution Error: Inadequate choice of parameters.", "Application Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel12 = new javax.swing.JPanel();
        jP_SelectSearchAlgorithm = new javax.swing.JPanel();
        jRB_SFSSE = new javax.swing.JRadioButton();
        jRB_SFFSSE = new javax.swing.JRadioButton();
        jRB_ESSE = new javax.swing.JRadioButton();
        jLabel29 = new javax.swing.JLabel();
        jS_MaxSetSizeSE = new javax.swing.JSpinner();
        jLabel38 = new javax.swing.JLabel();
        jS_MaxResultListSE = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jLabelConfidenceSE = new javax.swing.JLabel();
        jCB_EntropySE = new javax.swing.JComboBox();
        jS_QEntropySE = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jS_AlphaSE = new javax.swing.JSpinner();
        jCB_CriterionFucntionSE = new javax.swing.JComboBox();
        jTF_InputTestSE = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton35 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        jSliderBetaSE = new javax.swing.JSlider();
        jProgressBarSE = new javax.swing.JProgressBar();
        jLabel4 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jCB_TargetsAsPredictors = new javax.swing.JCheckBox();
        jCB_Periodic = new javax.swing.JCheckBox();
        jLabel_TargetsPredictors = new javax.swing.JLabel();
        jTF_Target = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jS_ThresholdEntropy = new javax.swing.JSpinner();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jP_NetGeneration = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jB_ReadData1 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jTF_NrGenes = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTF_AvgEdges = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jTF_Quantization = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jTF_InputRules = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        jB_ReadData2 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jCB_NetType = new javax.swing.JComboBox();
        jLabel34 = new javax.swing.JLabel();
        jTF_NrObs = new javax.swing.JTextField();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jLabel35 = new javax.swing.JLabel();
        jTF_NrInitializations = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jCB_RulesType = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jCB_AllBooleanFunctions = new javax.swing.JCheckBox();
        jBHistogram = new javax.swing.JButton();
        jB_ShowTemporalData = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jCB_SeparateSignal = new javax.swing.JCheckBox();
        jP_GeneratedData = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jSP_GeneratedData = new javax.swing.JScrollPane();
        jT_GeneratedData = new javax.swing.JTable();
        jButton9 = new javax.swing.JButton();
        jP_NetResults = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jTF_InputResultFile = new javax.swing.JTextField();
        jButton20 = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jTF_InputLabelsFile = new javax.swing.JTextField();
        jButton21 = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        jTF_InputSpotType = new javax.swing.JTextField();
        jButton22 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jTF_InputExpressionFile = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jTF_QuantizationResults = new javax.swing.JTextField();
        jCB_HasLabels = new javax.swing.JCheckBox();
        jPanel15 = new javax.swing.JPanel();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Execution of the Feature Selector and Classifier"));
        jPanel12.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel12.setLayout(new java.awt.BorderLayout());

        jP_SelectSearchAlgorithm.setBorder(javax.swing.BorderFactory.createTitledBorder("Search Method"));
        jP_SelectSearchAlgorithm.setDoubleBuffered(false);
        jP_SelectSearchAlgorithm.setMinimumSize(new java.awt.Dimension(130, 30));
        jP_SelectSearchAlgorithm.setPreferredSize(new java.awt.Dimension(200, 80));
        jP_SelectSearchAlgorithm.setLayout(new java.awt.GridLayout(7, 1));

        jRB_SFSSE.setText("SFS");
        jRB_SFSSE.setToolTipText("Apply SFS algorithm for feature selection.");
        jRB_SFSSE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRB_SFSSE.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jRB_SFSSE.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jRB_SFSSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_SFSSEActionPerformed(evt);
            }
        });
        jP_SelectSearchAlgorithm.add(jRB_SFSSE);

        jRB_SFFSSE.setSelected(true);
        jRB_SFFSSE.setText("SFFS");
        jRB_SFFSSE.setToolTipText("Apply SFFS algorithm for feature selection.");
        jRB_SFFSSE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRB_SFFSSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_SFFSSEActionPerformed(evt);
            }
        });
        jP_SelectSearchAlgorithm.add(jRB_SFFSSE);

        jRB_ESSE.setText("Exhaustive Search");
        jRB_ESSE.setToolTipText("Apply Exhaustive algorithm for feature selection.");
        jRB_ESSE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRB_ESSE.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jRB_ESSE.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jRB_ESSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_ESSEActionPerformed(evt);
            }
        });
        jP_SelectSearchAlgorithm.add(jRB_ESSE);

        jLabel29.setText("Maximum Set Size:");
        jP_SelectSearchAlgorithm.add(jLabel29);

        jS_MaxSetSizeSE.setToolTipText("Select the maximum cardinality of the feature set to perform the search (SFFS only).");
        jP_SelectSearchAlgorithm.add(jS_MaxSetSizeSE);

        jLabel38.setText("Size of the Result List:");
        jP_SelectSearchAlgorithm.add(jLabel38);

        jS_MaxResultListSE.setToolTipText("Select the maximum size of the result dataset.");
        jS_MaxResultListSE.setValue(5);
        jP_SelectSearchAlgorithm.add(jS_MaxResultListSE);

        jPanel12.add(jP_SelectSearchAlgorithm, java.awt.BorderLayout.EAST);

        jPanel1.setLayout(null);

        jButton3.setText("File");
        jButton3.setToolTipText("Click here to select a test set file.");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);
        jButton3.setBounds(620, 160, 80, 30);

        jLabelConfidenceSE.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelConfidenceSE.setText("Beta (value of confidence) 80% : ");
        jPanel1.add(jLabelConfidenceSE);
        jLabelConfidenceSE.setBounds(180, 110, 290, 40);

        jCB_EntropySE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "no_obs", "poor_obs" }));
        jCB_EntropySE.setToolTipText("no_obs (penalty for non-observed instances)  poor_obs (penalty for poorly observed instances)");
        jCB_EntropySE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_EntropySEActionPerformed(evt);
            }
        });
        jPanel1.add(jCB_EntropySE);
        jCB_EntropySE.setBounds(180, 70, 120, 30);

        jS_QEntropySE.setToolTipText("Use 1 to apply Shannon Entropy.  Use a value <> 1 to apply Tsallis Entropy (Entropy only).");
        jPanel1.add(jS_QEntropySE);
        jS_QEntropySE.setBounds(470, 30, 60, 30);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Criterion Function: ");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(10, 30, 170, 30);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Input Test Set (optional): ");
        jPanel1.add(jLabel11);
        jLabel11.setBounds(10, 160, 170, 30);

        jS_AlphaSE.setToolTipText("Alpha value represents the probability mass for the non-observed instances (no_obs only).");
        jPanel1.add(jS_AlphaSE);
        jS_AlphaSE.setBounds(470, 70, 60, 30);

        jCB_CriterionFucntionSE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Entropy", "CoD" }));
        jCB_CriterionFucntionSE.setToolTipText("Select the criterion function based on classifier information (mean conditional entropy) or based on classifier error (CoD - Coefficient of Determination).");
        jCB_CriterionFucntionSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_CriterionFucntionSEActionPerformed(evt);
            }
        });
        jPanel1.add(jCB_CriterionFucntionSE);
        jCB_CriterionFucntionSE.setBounds(180, 30, 120, 30);

        jTF_InputTestSE.setToolTipText("Click on File button or fill this text box with the path of the test data file.");
        jPanel1.add(jTF_InputTestSE);
        jTF_InputTestSE.setBounds(180, 160, 440, 30);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Alpha (value for penalty): ");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(310, 70, 160, 30);

        jButton35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/help.jpg"))); // NOI18N
        jButton35.setToolTipText("Shows help information about this panel.");
        jButton35.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton35);
        jButton35.setBounds(670, 13, 35, 35);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("q-Entropy (Tsallis): ");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(310, 30, 160, 30);

        jButton12.setText("Execute Feature Selector");
        jButton12.setToolTipText("Execute feature selection on input data. The input data must have sample labels in its last column.");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton12);
        jButton12.setBounds(50, 200, 260, 30);

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
        jPanel1.add(jSliderBetaSE);
        jSliderBetaSE.setBounds(470, 110, 230, 40);

        jProgressBarSE.setToolTipText("Display the progress of the execution.");
        jProgressBarSE.setStringPainted(true);
        jPanel1.add(jProgressBarSE);
        jProgressBarSE.setBounds(330, 200, 370, 30);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Penalization Method: ");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(10, 70, 170, 30);

        jPanel12.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder("Network Identification"));
        jPanel18.setMinimumSize(new java.awt.Dimension(200, 150));
        jPanel18.setLayout(null);

        jButton5.setText("Generate Graph");
        jButton5.setToolTipText("Apply feature selection algorithm to find relationship among samples (genes), and as result, display a graph in which samples are pesented as nodes and its relationships as edges.");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel18.add(jButton5);
        jButton5.setBounds(750, 20, 150, 30);

        jCB_TargetsAsPredictors.setText("Targets as Predictors?");
        jCB_TargetsAsPredictors.setToolTipText("Select this option to generate graph from targets (not selected) or predictors (selected).");
        jCB_TargetsAsPredictors.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCB_TargetsAsPredictors.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCB_TargetsAsPredictors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_TargetsAsPredictorsActionPerformed(evt);
            }
        });
        jPanel18.add(jCB_TargetsAsPredictors);
        jCB_TargetsAsPredictors.setBounds(10, 20, 150, 30);

        jCB_Periodic.setText("Is it periodic?");
        jCB_Periodic.setToolTipText("Mark this option to assume that time series is periodic, i. e. the last instant of time is connected with first instant of time.");
        jCB_Periodic.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCB_Periodic.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCB_Periodic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_PeriodicActionPerformed(evt);
            }
        });
        jPanel18.add(jCB_Periodic);
        jCB_Periodic.setBounds(460, 20, 130, 30);

        jLabel_TargetsPredictors.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel_TargetsPredictors.setText("Targets: ");
        jPanel18.add(jLabel_TargetsPredictors);
        jLabel_TargetsPredictors.setBounds(160, 20, 100, 30);

        jTF_Target.setToolTipText("Fill this text box with predictors or targets indexes to find others genes related with them. If this text box is empty, all genes are considered to graph generation.");
        jTF_Target.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTF_TargetActionPerformed(evt);
            }
        });
        jPanel18.add(jTF_Target);
        jTF_Target.setBounds(260, 20, 190, 30);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Threshold: ");
        jPanel18.add(jLabel21);
        jLabel21.setBounds(570, 20, 110, 30);

        jS_ThresholdEntropy.setToolTipText("Choose a real value to visualize all graph edges (near 1) or only the most representative ones (near 0).");
        jPanel18.add(jS_ThresholdEntropy);
        jS_ThresholdEntropy.setBounds(680, 20, 50, 30);

        jPanel12.add(jPanel18, java.awt.BorderLayout.SOUTH);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("AGN Simulation and Validation Model");
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jP_NetGeneration.setEnabled(false);
        jP_NetGeneration.setLayout(new java.awt.BorderLayout());

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel16.setPreferredSize(new java.awt.Dimension(110, 180));
        jPanel16.setLayout(null);

        jB_ReadData1.setText("Generate AGN and Temporal Data");
        jB_ReadData1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_ReadData1ActionPerformed(evt);
            }
        });
        jPanel16.add(jB_ReadData1);
        jB_ReadData1.setBounds(685, 5, 220, 30);

        jButton10.setText("Visualize Network");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton10);
        jButton10.setBounds(685, 35, 220, 30);

        jButton14.setText("Save Generated AGN");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton14);
        jButton14.setBounds(685, 65, 220, 30);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Number of genes: ");
        jPanel16.add(jLabel20);
        jLabel20.setBounds(10, 10, 120, 30);

        jTF_NrGenes.setText("100");
        jPanel16.add(jTF_NrGenes);
        jTF_NrGenes.setBounds(130, 10, 40, 30);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Edges Average: ");
        jPanel16.add(jLabel22);
        jLabel22.setBounds(10, 50, 120, 30);

        jTF_AvgEdges.setText("5");
        jPanel16.add(jTF_AvgEdges);
        jTF_AvgEdges.setBounds(130, 50, 40, 30);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Quantization: ");
        jLabel25.setToolTipText("");
        jPanel16.add(jLabel25);
        jLabel25.setBounds(170, 10, 120, 30);

        jTF_Quantization.setText("2");
        jTF_Quantization.setToolTipText("número máximo de vezes que um determinado gene pode ser preditor de outros genes.");
        jPanel16.add(jTF_Quantization);
        jTF_Quantization.setBounds(290, 10, 40, 30);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("Network File:");
        jPanel16.add(jLabel26);
        jLabel26.setBounds(5, 140, 110, 30);

        jTF_InputRules.setText("D:\\doutorado\\submissoes-apresentacoes\\2010-xx-agn-extendido-JCM\\resultados\\resultados-agn-jcb-n100-stack\\recovered-network-ER-nexe.01-nrnodes.100-avgedges.2-reduced.bfs-no_obs-signalsize.060-qentropy.1.0.agn");
        jPanel16.add(jTF_InputRules);
        jTF_InputRules.setBounds(120, 140, 480, 30);

        jButton8.setText("File");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton8);
        jButton8.setBounds(600, 140, 70, 30);

        jB_ReadData2.setText("Load Network");
        jB_ReadData2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_ReadData2ActionPerformed(evt);
            }
        });
        jPanel16.add(jB_ReadData2);
        jB_ReadData2.setBounds(675, 140, 130, 30);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("Type: ");
        jLabel27.setToolTipText("número máximo de vezes que um determinado gene pode ser preditor de outros genes.");
        jPanel16.add(jLabel27);
        jLabel27.setBounds(330, 10, 120, 30);

        jCB_NetType.setMaximumRowCount(4);
        jCB_NetType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ER-Erdös-Rényi (random)", "BA-Barabási-Albert (scale-free)", "GG-Geographical", "WS-Small-World" }));
        jPanel16.add(jCB_NetType);
        jCB_NetType.setBounds(450, 10, 230, 30);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel34.setText("Time Observations: ");
        jLabel34.setToolTipText("número máximo de vezes que um determinado gene pode ser preditor de outros genes.");
        jPanel16.add(jLabel34);
        jLabel34.setBounds(170, 50, 120, 30);

        jTF_NrObs.setText("10");
        jTF_NrObs.setToolTipText("número máximo de vezes que um determinado gene pode ser preditor de outros genes.");
        jPanel16.add(jTF_NrObs);
        jTF_NrObs.setBounds(290, 50, 40, 30);

        jButton25.setText("Measures");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton25);
        jButton25.setBounds(910, 35, 160, 30);

        jButton26.setText("Comparison");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton26);
        jButton26.setBounds(910, 95, 160, 30);

        jButton27.setText("Save Recovered Network");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton27);
        jButton27.setBounds(685, 95, 220, 30);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("# Initializations: ");
        jLabel35.setToolTipText("número máximo de vezes que um determinado gene pode ser preditor de outros genes.");
        jPanel16.add(jLabel35);
        jLabel35.setBounds(330, 50, 120, 30);

        jTF_NrInitializations.setText("0");
        jTF_NrInitializations.setToolTipText("número máximo de vezes que um determinado gene pode ser preditor de outros genes.");
        jPanel16.add(jTF_NrInitializations);
        jTF_NrInitializations.setBounds(450, 50, 40, 30);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText("Rules Type: ");
        jLabel28.setToolTipText("número máximo de vezes que um determinado gene pode ser preditor de outros genes.");
        jPanel16.add(jLabel28);
        jLabel28.setBounds(170, 90, 120, 30);

        jCB_RulesType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Boolean" }));
        jPanel16.add(jCB_RulesType);
        jCB_RulesType.setBounds(290, 90, 200, 30);

        jButton1.setText("Adjacency Matrix");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton1);
        jButton1.setBounds(910, 5, 160, 30);

        jCB_AllBooleanFunctions.setText("All Boolean Functions?");
        jPanel16.add(jCB_AllBooleanFunctions);
        jCB_AllBooleanFunctions.setBounds(3, 90, 190, 30);

        jBHistogram.setText("Histogram");
        jBHistogram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBHistogramActionPerformed(evt);
            }
        });
        jPanel16.add(jBHistogram);
        jBHistogram.setBounds(910, 65, 160, 30);

        jB_ShowTemporalData.setText("Show Temporal Data");
        jB_ShowTemporalData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_ShowTemporalDataActionPerformed(evt);
            }
        });
        jPanel16.add(jB_ShowTemporalData);
        jB_ShowTemporalData.setBounds(810, 140, 160, 30);

        jButton2.setText("Build HTML");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton2);
        jButton2.setBounds(975, 140, 100, 30);

        jCB_SeparateSignal.setText("separate the signal?");
        jPanel16.add(jCB_SeparateSignal);
        jCB_SeparateSignal.setBounds(500, 50, 180, 30);

        jP_NetGeneration.add(jPanel16, java.awt.BorderLayout.NORTH);

        jP_GeneratedData.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jP_GeneratedData.setLayout(new java.awt.BorderLayout(5, 5));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel23.setForeground(new java.awt.Color(0, 0, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Generated Data");
        jLabel23.setOpaque(true);
        jLabel23.setPreferredSize(new java.awt.Dimension(40, 30));
        jP_GeneratedData.add(jLabel23, java.awt.BorderLayout.NORTH);

        jSP_GeneratedData.setAutoscrolls(true);

        jT_GeneratedData.setModel(new javax.swing.table.DefaultTableModel(
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
        jSP_GeneratedData.setViewportView(jT_GeneratedData);

        jP_GeneratedData.add(jSP_GeneratedData, java.awt.BorderLayout.CENTER);

        jButton9.setFont(new java.awt.Font("Tahoma", 1, 12));
        jButton9.setForeground(new java.awt.Color(0, 0, 255));
        jButton9.setText("Save Generated Data");
        jButton9.setPreferredSize(new java.awt.Dimension(163, 30));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jP_GeneratedData.add(jButton9, java.awt.BorderLayout.SOUTH);

        jP_NetGeneration.add(jP_GeneratedData, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Generate Predictors File", jP_NetGeneration);

        jP_NetResults.setEnabled(false);
        jP_NetResults.setLayout(new java.awt.BorderLayout());

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel14.setPreferredSize(new java.awt.Dimension(100, 300));
        jPanel14.setLayout(null);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel31.setText("Result File:");
        jPanel14.add(jLabel31);
        jLabel31.setBounds(20, 70, 120, 30);

        jTF_InputResultFile.setText("D:\\doutorado\\dimreduction\\dados-cancer-eduardo\\resultados-medianas-noobs\\resultados-medianas-sffs-q2-39intronicos-max10-noobs\\resultado-mediana-sffs-q2-39intronicos-max10-noobs.txt");
        jPanel14.add(jTF_InputResultFile);
        jTF_InputResultFile.setBounds(150, 70, 630, 30);

        jButton20.setText("File");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });
        jPanel14.add(jButton20);
        jButton20.setBounds(780, 70, 80, 30);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel32.setText("Labels File:");
        jPanel14.add(jLabel32);
        jLabel32.setBounds(20, 110, 120, 30);

        jTF_InputLabelsFile.setText("D:\\doutorado\\dimreduction\\dados-cancer-eduardo\\geneid.txt");
        jPanel14.add(jTF_InputLabelsFile);
        jTF_InputLabelsFile.setBounds(150, 110, 630, 30);

        jButton21.setText("File");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });
        jPanel14.add(jButton21);
        jButton21.setBounds(780, 110, 80, 30);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("Spot Type:");
        jPanel14.add(jLabel33);
        jLabel33.setBounds(20, 150, 120, 30);

        jTF_InputSpotType.setText("D:\\doutorado\\dimreduction\\dados-cancer-eduardo\\spot-type.txt");
        jPanel14.add(jTF_InputSpotType);
        jTF_InputSpotType.setBounds(150, 150, 630, 30);

        jButton22.setText("File");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });
        jPanel14.add(jButton22);
        jButton22.setBounds(780, 150, 80, 30);

        jButton19.setText("Show Graph");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });
        jPanel14.add(jButton19);
        jButton19.setBounds(750, 190, 110, 60);

        jButton23.setText("HTML");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });
        jPanel14.add(jButton23);
        jButton23.setBounds(650, 190, 90, 60);

        jButton24.setText("Comparar Resultados");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });
        jPanel14.add(jButton24);
        jButton24.setBounds(460, 190, 180, 60);

        jButton29.setText("File");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });
        jPanel14.add(jButton29);
        jButton29.setBounds(780, 30, 80, 30);

        jTF_InputExpressionFile.setText("D:\\doutorado\\dimreduction\\dados-cancer-eduardo\\dados-medianas.txt");
        jPanel14.add(jTF_InputExpressionFile);
        jTF_InputExpressionFile.setBounds(150, 30, 630, 30);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel36.setText("Expression File:");
        jPanel14.add(jLabel36);
        jLabel36.setBounds(20, 30, 120, 30);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel37.setText("Quantization:");
        jPanel14.add(jLabel37);
        jLabel37.setBounds(20, 190, 120, 30);

        jTF_QuantizationResults.setText("2");
        jPanel14.add(jTF_QuantizationResults);
        jTF_QuantizationResults.setBounds(150, 190, 80, 30);

        jCB_HasLabels.setText("The last column stores the labels of the classes.");
        jCB_HasLabels.setToolTipText("If data file has labels of the classes, these labels must be at last column and you must mark this check box.");
        jCB_HasLabels.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jPanel14.add(jCB_HasLabels);
        jCB_HasLabels.setBounds(150, 230, 310, 30);

        jP_NetResults.add(jPanel14, java.awt.BorderLayout.NORTH);

        jPanel15.setLayout(null);

        jButton30.setText("Filtrar genes com mesmo sinal e gerar a rede.");
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });
        jPanel15.add(jButton30);
        jButton30.setBounds(500, 20, 290, 50);

        jButton31.setText("Geracao das Redes com empates");
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });
        jPanel15.add(jButton31);
        jButton31.setBounds(250, 20, 193, 60);

        jP_NetResults.add(jPanel15, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Results View", jP_NetResults);

        getContentPane().add(jTabbedPane1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1092)/2, (screenSize.height-670)/2, 1092, 670);
    }// </editor-fold>//GEN-END:initComponents

    private void jB_ReadData1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_ReadData1ActionPerformed
        int nr_nodes = Integer.valueOf(jTF_NrGenes.getText());
        int nr_obs = Integer.valueOf(jTF_NrObs.getText());
        float avg_edges = Float.valueOf(jTF_AvgEdges.getText());
        int quantization = Integer.valueOf(jTF_Quantization.getText());
        int maxconcat = Integer.valueOf(jTF_NrInitializations.getText());

        boolean allbf = false;
        if (jCB_AllBooleanFunctions.isSelected()) {
            allbf = true;
        }

        boolean separatesignal = false;
        if (jCB_SeparateSignal.isSelected()) {
            separatesignal = true;
        }

        String networkmodel = ((String) jCB_NetType.getSelectedItem()).substring(0, 2);
        int rulestype = jCB_RulesType.getSelectedIndex();
        if (rulestype == 0) {//Boolean
            if (quantization != 2) {
                JOptionPane.showMessageDialog(this, "The rules type is "
                        + "incompatible with quantization.",
                        "Problem with parameters", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        float prob = 0;
        if (networkmodel.equalsIgnoreCase("WS")) {
            String strv = JOptionPane.showInputDialog("Select the probability" +
                    " from 0 to 1 of redistribution edges, used by WS topology:", "0.1");
            if (strv != null) {
                prob = Float.valueOf(strv);
            }
        }
        agn = Topologies.CreateNetwork(nr_nodes, nr_obs, maxconcat,
                avg_edges, quantization, networkmodel, allbf, prob,
                false // intrinsically multivariate prediction only
                );
        if (agn == null) {
            JOptionPane.showMessageDialog(this, "The number of nodes and edges "
                    + "are incompatible. Try to reduce the number of edges or "
                    + "increase the number of nodes.", "Problem with parameters",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            AGNRoutines.CreateTemporalSignalq(agn);
            AGNRoutines.CreateSignalInitializations(agn, separatesignal);
            ShowTemporalExpressionProfile();
        }
    }//GEN-LAST:event_jB_ReadData1ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        if (agn != null) {
            AGNRoutines.ViewAGNMA(agn, null, true, 1);
        } else {
            JOptionPane.showMessageDialog(null, "Execution Error:Click " + "Generate Rules and Data" + " first.", "Application Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        String path = IOFile.SaveFile();
        if (path != null) {
            IOFile.WriteAGNtoFile(agn, path);
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        String path = IOFile.OpenAGNFile();
        if (path != null) {
            jTF_InputRules.setText(path);
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jB_ReadData2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_ReadData2ActionPerformed
        agn = IOFile.ReadAGNfromFile(jTF_InputRules.getText());
    }//GEN-LAST:event_jB_ReadData2ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        if (agn != null) {
            lines = agn.getNrgenes();
            columns = agn.getSignalsize();
            int[] indegrees = new int[lines];
            int[] outdegrees = new int[lines];
            for (int i = 0; i < lines; i++) {
                indegrees[i] += agn.getGenes()[i].getPredictors().size();
                for (int p = 0; p < agn.getGenes()[i].getPredictors().size(); p++) {
                    int predictor = (Integer) agn.getGenes()[i].getPredictors().get(p);
                    outdegrees[predictor]++;
                }
            }
            float[] avg = CNMeasurements.Average(indegrees, outdegrees);
            JOptionPane.showMessageDialog(this, "Average input degree: " + avg[0] + "\n"
                    + "Average output degree: " + avg[1] + ".", "Average degrees", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Load or Generate rules first.", "Error Graph Measurements", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        //chamada do metodo para comparacoes entre as redes original e recuperada.
        if (agn != null && recoverednetwork != null) {
            CNMeasurements.ConfusionMatrix(agn, agn, 1);
            CNMeasurements.ConfusionMatrix(recoverednetwork, recoverednetwork, 1);
            CNMeasurements.ConfusionMatrix(agn, recoverednetwork, 1);
        } else {
            JOptionPane.showMessageDialog(this, "Generate rules and execute algorithm first.", "Error Graph Measurements", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        String path = IOFile.OpenPath();
        if (path != null) {
            IOFile.WriteAGNtoFile(recoverednetwork, path);
        }
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        IOFile.SaveTable(jT_GeneratedData);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        String path = IOFile.OpenPath();
        if (path != null) {
            jTF_InputResultFile.setText(path);
        }
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        String path = IOFile.OpenPath();
        if (path != null) {
            jTF_InputLabelsFile.setText(path);
        }
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        String path = IOFile.OpenPath();
        if (path != null) {
            jTF_InputSpotType.setText(path);
        }
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        int qv = Integer.valueOf(jTF_QuantizationResults.getText());
        //leitura dos dados de expressao.
        String datafile = jTF_InputExpressionFile.getText();
        //ReadInputData(datafile);
        int has_labels = 0;
        if (jCB_HasLabels.isSelected()) {
            has_labels = 1;
        }
        Preprocessing.quantizecolumnsavg(Md, qv, true, has_labels);

        //gerar pagina html para cada um dos arquivos da pasta recebida.
        String pathresult = jTF_InputResultFile.getText();
        //"D:/doutorado/dimreduction/dados_cancer/resultados_poor_obs/res_razoes_nregulados_nciclicos_q2_sffs_maxfeatures_1.txt";
        String pathlabels = jTF_InputLabelsFile.getText();
        //"D:/doutorado/dimreduction/dados_cancer/labels2.txt";
        String pathspottype = jTF_InputSpotType.getText();
        File file = new File(pathresult);
        String pasta = file.getParent() + "\\";//"D:/doutorado/dimreduction/dados-cancer-eduardo/resultados-sfs-q2-39intronicos/";

        String pastaimg = file.getParent() + "\\html-imgs\\";//"D:/doutorado/dimreduction/dados-cancer-eduardo/resultados-sfs-q2-39intronicos/html-imgs/";

        File diretorioimg = new File(pastaimg);
        if (!diretorioimg.exists()) {
            diretorioimg.mkdir();
        }
        String pathinfo = "D:/doutorado/dimreduction/dados-cancer-eduardo/info.txt";

        Vector spot_type;
        Vector info;
        try {
            datatitles = IOFile.ReadDataFirstCollum(pathlabels, 0, delimiter);
            spot_type = IOFile.ReadDataFirstCollum(pathspottype, 0, delimiter);
            info = IOFile.ReadDataLine(pathinfo);
        } catch (IOException error) {
            throw new FSException("Error when reading input file. " + error, false);
        }

        File folder = new File(pasta);
        if (!folder.isDirectory()) {
            System.out.println("Problems!");
            return;
        }

        /*
        String[] files = folder.list();
        for (int i = 0; i < files.length; i++) {
        file = new File(folder, files[i]);
        if (file.isFile() && !file.getName().substring(0, 5).equalsIgnoreCase("saida")) {
        float[][] R;
        try {
        R = IOFile.ReadMatrix(file.getAbsolutePath(), 0, 0, delimiter);
        //Vector rules = Network.MakeRulesFromResults(R);

        //prepara lista de frequencias dos preditores.
        Vector ordenado = new Vector();
        StringBuffer list = Preprocessing.MakeResultList(R, ordenado);
        String nmlist = file.getName().substring(0, (int) file.getName().length() - 3) + "txt";
        String listoutfile = pastaimg + nmlist;
        IOFile.SaveFile(list.toString(), listoutfile);

        //Vector re = Network.MakeGraphWithRules(rules, datatitles, spot_type, false, false);
        //JGraph graph = (JGraph) re.elementAt(0);
        String nmimg = file.getName().substring(0, (int) file.getName().length() - 3) + "png";
        String imgoutfile = pastaimg + nmimg;
        OutputStream out = new BufferedOutputStream(new FileOutputStream(imgoutfile));
        BufferedImage img = graph.getImage(graph.getBackground(), 0);
        ImageIO.write(img, "png", out);
        out.flush();
        out.close();

        String htmloutfile = pastaimg + file.getName().substring(0, (int) file.getName().length() - 3) + "html";
        BufferedWriter bw = new BufferedWriter(new FileWriter(htmloutfile));
        bw.write("<HTML>\n<HEAD>");
        bw.write("<META HTTP-EQUIV='CONTENT-TYPE' CONTENT='text/html; charset=ISO-8859-1'>");
        bw.write("<TITLE></TITLE>\n</HEAD>\n<BODY>");
        bw.write("<center><img src='" + nmimg + "' border=0></center>\n<br>&nbsp;<p>");
        //bw.write("<table border='1' width='100%' align='left'>\n");
        bw.write("<table border='1' width='100%' cellspacing='0' height='1'>\n");
        bw.write("<thead><tr><th colspan='2'>Color Codification</th></tr></thead>\n");
        bw.write("<tbody>");
        bw.write("<tr><td BGCOLOR='#FF0033'></td><td>exonic</td></tr>\n");
        bw.write("<tr><td BGCOLOR='#3300FF'></td><td>intronic</td></tr>\n");
        bw.write("<tr><td BGCOLOR='#FFFF66'></td><td>house-keeping</td></tr>\n");
        bw.write("<tr><td BGCOLOR='#33FF33'></td><td>prostate-marker</td></tr>\n");
        bw.write("</tbody></table>\n<br><p>&nbsp;<p>");

        bw.write("<table border='1' width='100%' cellspacing='0' height='1'>\n");
        bw.write("<thead><tr><th colspan='5'><a href ='" + nmlist + "'><b>Predictor's List</b></a></th></tr></thead>\n");
        bw.write("<thead><tr><th>Index-Predictor</th><th>GeneID-Predictor</th><th>Signal</th><th>Entropy</th><th>Frequency</th></tr></thead>\n");
        //bw.write("<a href ='"+ nmlist +"'><b>Lista de Preditores</b></a><br><br>\n");

        //IOFile.PrintMatrix(R);

        /*
        //gera a imagem do preditor e dos sinais preditos na mesma imagem.
        DefaultCategoryDataset[] datasets1 = new DefaultCategoryDataset[maxg+1];
        DefaultCategoryDataset[] datasets2 = new DefaultCategoryDataset[maxg+1];
        //gera dataset para o preditor.
        datasets1[0] = new DefaultCategoryDataset();
        datasets2[0] = new DefaultCategoryDataset();
        double [] maxmin = new double[2];
        Preprocessing.MaxMin(Mo[preditor], maxmin);
        double valornormalizado = 0;
        for (int c = 0; c < columns; c++) {
        datasets1[0].addValue(Md[preditor][c], "preditor " + preditor, String.valueOf(c));
        valornormalizado = (Mo[preditor][c] - maxmin[1]) / (maxmin[0] - maxmin[1]);
        datasets2[0].addValue(valornormalizado, "preditor " + preditor, String.valueOf(c));
        }
        //gera os datasets dos preditos.
        for (int nrpreditos = 0; nrpreditos < maxg; nrpreditos++) {
        datasets1[nrpreditos+1] = new DefaultCategoryDataset();
        datasets2[nrpreditos+1] = new DefaultCategoryDataset();
        int amostra = (Integer) preditos.get(nrpreditos);

        Preprocessing.MaxMin(Mo[amostra], maxmin);
        for (int c = 0; c < columns; c++) {
        datasets1[nrpreditos+1].addValue(Md[amostra][c], "sample " + amostra, String.valueOf(c));
        valornormalizado = (qv-1) * (Mo[amostra][c] - maxmin[1]) / (maxmin[0] - maxmin[1]);
        datasets2[nrpreditos+1].addValue(valornormalizado, "sample " + amostra, String.valueOf(c));
        }
        }
        JFreeChart chart = Chart.MultipleStepChartOverlayed(datasets1, datasets2, "Time-Series Expression", "Time", "Value",
        true, maxvalue + 0.03f, -0.03f, true);
         */

        /*
        //escreve a tabela de saida no html e cria link para copiar tabela em formato texto.
        for (int p = 0; p < ordenado.size(); p++) {
        Vector item = (Vector) ordenado.get(p);
        double[] linha = (double[]) item.get(0);
        Vector preditos = (Vector) item.get(1);

        //gera a tabela de frequencias dos preditores.
        int preditor = (int) linha[0];
        float entropia = (float) linha[1];
        int count = (int) linha[2];
        String id = (String) datatitles.get(preditor);
        String su = "http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&orig_db=gene&term=" + id;
        int maxg = preditos.size();
        //limite para visualizacao do grafico.
        if (maxg > 6) {
        maxg = 6;                        //codigo para gerar os dados para o grafico MultipleStepChart - usando XYDataset
        }
        XYDataset[] datasets1 = new XYDataset[maxg + 1];
        XYDataset[] datasets2 = new XYDataset[maxg + 1];
        XYSeries serie1 = new XYSeries("predictor " + preditor);
        XYSeries serie2 = new XYSeries("predictor " + preditor);
        float[] maxmin = new float[2];
        Preprocessing.MaxMin(Mo[preditor], maxmin);
        double valornormalizado = 0;
        for (int c = 0; c < columns; c++) {
        serie1.add(c, Md[preditor][c]);
        valornormalizado = (qv - 1) * (Mo[preditor][c] - maxmin[1]) / (maxmin[0] - maxmin[1]);
        serie2.add(c, valornormalizado);
        }
        datasets1[0] = new XYSeriesCollection(serie1);
        datasets2[0] = new XYSeriesCollection(serie2);

        for (int nrpreditos = 0; nrpreditos < maxg; nrpreditos++) {
        int amostra = (Integer) preditos.get(nrpreditos);
        Preprocessing.MaxMin(Mo[amostra], maxmin);

        serie1 = new XYSeries("sample " + amostra);
        serie2 = new XYSeries("sample " + amostra);

        for (int c = 0; c < columns; c++) {
        serie1.add(c, Md[amostra][c]);
        valornormalizado = (qv - 1) * (Mo[amostra][c] - maxmin[1]) / (maxmin[0] - maxmin[1]);
        serie2.add(c, valornormalizado);
        }
        datasets1[nrpreditos + 1] = new XYSeriesCollection(serie1);
        datasets2[nrpreditos + 1] = new XYSeriesCollection(serie2);
        }

        nmimg = "sinal-gene-preditor" + id + "-index-" + (int) preditor + ".png";
        JFreeChart chart = Chart.MultipleStepChartOverlayed(datasets1, datasets2, "Time-Series Expression", "Time", "Value",
        true, (qv - 1 + 0.03f), -0.03f, false);
        imgoutfile = pastaimg + nmimg;
        img = chart.createBufferedImage(640, 480);
        out = new BufferedOutputStream(new FileOutputStream(imgoutfile));
        ImageIO.write(img, "png", out);
        out.flush();
        out.close();

        bw.write("<tr><td><center>" + preditor + "</center></td>");
        bw.write("<td><center><a href='" + su + "' >" + id + "</center></td>");
        bw.write("<td><center><a href=" + nmimg + ">signal</a></center></td><td><center>" + entropia + "</center></td>");
        bw.write("<td><center>" + count + "</center></td></tr>\n");
        }
        bw.write("</table>\n<br><p>&nbsp;<p>");
        bw.write("<table border='1' width='100%' cellspacing='0' height='1'>\n");
        bw.write("<thead><tr><th>Index</th><th>GeneID</th><th>Signal</th><th>Description</th></tr></thead>\n");
        bw.write("<tbody>");

        Vector nodes = (Vector) re.elementAt(1);
        for (int n = 0; n < nodes.size(); n++) {
        int sn = (Integer) nodes.get(n);
        String id = (String) datatitles.get(sn);
        //String su = "http://www.ncbi.nlm.nih.gov/sites/entrez?db=nucest&cmd=search&term=" + id;
        String su = "http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&orig_db=gene&term=" + id;

        bw.write("<tr><td><center>" + sn + "</center></td>\n");

        bw.write("<td><center>");
        bw.write("<A HREF='" + su + "' >" + id + "</center></td>\n");

        //gera a imagem do sinal de cada gene.
        XYDataset[] datasets1 = new XYDataset[1];
        XYDataset[] datasets2 = new XYDataset[1];
        XYSeries serie1 = new XYSeries("sample " + sn);
        XYSeries serie2 = new XYSeries("sample " + sn);
        float[] maxmin = new float[2];
        Preprocessing.MaxMin(Mo[sn], maxmin);
        double valornormalizado = 0;
        for (int c = 0; c < columns; c++) {
        serie1.add(c, Md[sn][c]);
        valornormalizado = (qv - 1) * (Mo[sn][c] - maxmin[1]) / (maxmin[0] - maxmin[1]);
        serie2.add(c, valornormalizado);
        }
        datasets1[0] = new XYSeriesCollection(serie1);
        datasets2[0] = new XYSeriesCollection(serie2);
        JFreeChart chart = Chart.MultipleStepChartOverlayed(datasets1, datasets2, "Time-Series Expression", "Time", "Value",
        true, (qv - 1 + 0.03f), -0.03f, false);
        nmimg = "sinal-gene-" + id + "-index-" + sn + ".png";
        imgoutfile = pastaimg + nmimg;
        img = chart.createBufferedImage(640, 480);
        out = new BufferedOutputStream(new FileOutputStream(imgoutfile));
        ImageIO.write(img, "png", out);
        out.flush();
        out.close();
        bw.write("<td><center><a href=" + nmimg + ">signal</a></center></td>\n");
        bw.write("<td>" + (String) info.get(sn) + "</td></tr>\n");

        /*
        Vector re = GenerateNetwork.MakeGraphWithRules(rules, datatitles, spot_type);
        Graph frame = (Graph) re.elementAt(0);

        imgoutfile = pastaimg + "sinal-gene-"+id+"-index-"+sn+ ".png";
        out = new BufferedOutputStream(new FileOutputStream(imgoutfile));
        frame.graph;
        Color bg = null;
        bg = frame.getBackground();
        BufferedImage img = graph1.getImage(bg, 0);
        ImageIO.write(img, "png", out);
        out.flush();
        out.close();
        frame.dispose();
         */
        /*
        }
        bw.write("</tbody></table><br><p><br>&nbsp;<p>\n");
        bw.write("</BODY>\n</HTML>\n");
        bw.flush();
        bw.close();
        } catch (IOException error) {
        throw new FSException("Error when reading input file. " + error, false);
        }
        }
        }*/
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        //gerar pagina html para cada um dos arquivos da pasta recebida.
        String pasta = "D:/doutorado/dimreduction/dados_cancer/resultados_poor_obs/";
        String pastaimg = "D:/doutorado/dimreduction/dados_cancer/resultados_poor_obs/comparacoes/";
        String pathinfo = "D:/doutorado/dimreduction/dados_cancer/info.txt";
        String pathresult = jTF_InputResultFile.getText();
        String pathlabels = jTF_InputLabelsFile.getText();
        String pathspottype = jTF_InputSpotType.getText();
        Vector spot_type;
        Vector info;

        /*
        StringBuffer file1 = new StringBuffer("D:/doutorado/dimreduction/dados_cancer/resultados_poor_obs/res_razoes_regulados_nciclicos_sffs_maxfeatures_");
        StringBuffer file2 = new StringBuffer("D:/doutorado/dimreduction/dados_cancer/resultados_poor_obs/res_razoes_nregulados_nciclicos_sffs_maxfeatures_");
        float[][] R1;
        float[][] R2;
        int ms = 11;
        String mss = String.valueOf(ms);
        for (int i = 1; i < ms; i++) {
        try {
        String nm = String.valueOf(i);
        while (nm.length() < mss.length()) {
        nm = "0" + nm;
        }
        nm += ".txt";

        R1 = IOFile.ReadMatrix(file1 + nm, 0, 0, delimiter);
        R2 = IOFile.ReadMatrix(file2 + nm, 0, 0, delimiter);

        Vector rules1 = Network.MakeRulesFromResults(R1);
        Vector rules2 = Network.MakeRulesFromResults(R2);

        Vector re = Network.MakeGraphWithRules(rules1, rules2, spot_type, datatitles);
        Graph frame = (Graph) re.elementAt(0);

        String imgoutfile = pastaimg + "comp_sffs_regulados_nregulados" + nm.subSequence(0, nm.length() - 3) + "png";
        OutputStream out = new BufferedOutputStream(new FileOutputStream(imgoutfile));
        JGraph graph1 = frame.graph;
        Color bg = null;
        bg = frame.getBackground();
        BufferedImage img = graph1.getImage(bg, 0);
        ImageIO.write(img, "png", out);
        out.flush();
        out.close();
        frame.dispose();

        } catch (IOException error) {
        throw new FSException("Error when reading input file. " + error, false);
        }
        }

        /*
        File folder = new File(pasta);
        if (!folder.isDirectory())
        System.out.println("Problems!");
        String [] files = folder.list();
        for (int i=0; i < files.length; i++){
        File file = new File(folder, files[i]);
        if (file.isFile()){
        double[][] R;
        try {
        R = IOFile.ReadMatrixDouble(file.getAbsolutePath(), 0);
        Vector rules = GenerateNetwork.MakeRulesFromResults(R);
        Vector re = GenerateNetwork.MakeGraphWithRules(rules, spot_type);
        Graph frame = (Graph) re.elementAt(0);
        String imgoutfile = pastaimg+file.getName().substring(0,(int)file.getName().length()-3)+"png";
        OutputStream out = new BufferedOutputStream(new FileOutputStream(imgoutfile));
        JGraph graph1 = frame.graph;
        Color bg = null;
        bg = frame.getBackground();
        BufferedImage img = graph1.getImage(bg, 0);
        ImageIO.write(img, "png", out);
        out.flush();
        out.close();
        frame.dispose();
        } catch (IOException error) {
        throw new FSException("Error when reading input file. " + error);
        }
        }
        }
         */
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        String path = IOFile.OpenPath();
        if (path != null) {
            jTF_InputExpressionFile.setText(path);
        }
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        int qv = Integer.valueOf(jTF_QuantizationResults.getText());
        columns = Md[0].length;
        int possiblevalues = (int) Math.pow(qv, columns);
        Vector[] indicespadroes = new Vector[possiblevalues];

        for (int i = 0; i < possiblevalues; i++) {
            indicespadroes[i] = new Vector();
            indicespadroes[i].add(i);
        }

        for (int lin = 0; lin < Md.length; lin++) {
            StringBuffer str = new StringBuffer();
            for (int col = 0; col < Md[0].length; col++) {
                str.append((int) Md[lin][col]);
            }
            int posicao = MathRoutines.Bin2Dec(str.toString());
            indicespadroes[posicao].add(lin);
        }

        //remover posicoes nao usadas-a primeira posicao indica o sinal cadificado em um numero decimal.
        int count = 0;
        for (int i = 0; i < possiblevalues; i++) {
            if (indicespadroes[i].size() > 1) {
                count++;
            }
        }

        Vector[] linhasvalidas = new Vector[count];
        float[][] Mr = new float[count][columns];

        count = 0;
        for (int i = 0; i < possiblevalues; i++) {
            if (indicespadroes[i].size() > 1) {
                linhasvalidas[count] = new Vector();
                for (int v = 0; v < indicespadroes[i].size(); v++) {
                    linhasvalidas[count].add(indicespadroes[i].get(v));
                }
                count++;
            }
        }

        for (int lin = 0; lin < count; lin++) {
            int indexsinal = (Integer) linhasvalidas[lin].get(1);
            for (int col = 0; col < columns; col++) {
                Mr[lin][col] = Md[indexsinal][col];
            }
        }
        //IOFile.PrintMatrix(Mr);
        float threshold_entropy = 0.5f;//Float.valueOf(jTF_ThresholdEntropy.getText());
        String type_entropy = "no_obs";//(String) jCB_EntropySE.getSelectedItem();
        float alpha = 1;//Float.valueOf(jTF_AlphaSE.getText());
        float q_entropy = 1;//Float.parseFloat(jTF_QEntropySE.getText());
        int search_alg = 0;
        //jTA_SelectedFeaturesSE.setText("");
        String path = null;//IOFile.SaveFile();

        StringBuffer txt = null;

        int maxf = 10;//Integer.parseInt(jTF_MaxSetSize_SE.getText());

        if (jRB_SFSSE.isSelected()) {
            search_alg = 1;
        } //SFS
        else if (jRB_ESSE.isSelected()) {
            search_alg = 2;
        }//Exhaustive
        else if (jRB_SFFSSE.isSelected()) {
            search_alg = 3;
        }//SFFS

        Vector<String> targets = null;

        if (!jTF_Target.getText().equalsIgnoreCase("")) {
            targets = new Vector<String>();
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
        //valor que o algoritmo ira ignorar, por se tratar de um separador (entre concatenacoes).
        //int ignoredvalue = 2;//Integer.MAX_VALUE;
        //if (!jTF_IgnoredValue.getText().equalsIgnoreCase(""))
        //    ignoredvalue = Integer.parseInt(jTF_IgnoredValue.getText());

        //recoveredrules = new Vector();

        //inverte os tempos de expressao, de forma que os targets passem
        //a ser os preditores, i.e. os preditores passem a considerar o valor
        //do target no instante de tempo posterior.
        //IOFile.PrintMatrix(Md);
        if (jCB_TargetsAsPredictors.isSelected()) {
            Md = Preprocessing.InvertColumns(Md);
            //IOFile.PrintMatrix(Md);
        }
        /*
        try {
        txt = Network.GenerateNetwork(Mr, false,
        threshold_entropy, type_entropy, alpha, 1, q_entropy, path, targets,
        true, recoverednetwork, maxf, search_alg, false, 10, -1, "NA", null, null);
        } catch (IOException error) {
        }
        //jTA_SelectedFeaturesSE.append(txt.toString());*/
    }//GEN-LAST:event_jButton30ActionPerformed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        String path = "D:/doutorado/dimreduction/dados-cancer-eduardo/resultados-ties/";
        String pathgeneid = "D:/doutorado/dimreduction/dados-cancer-eduardo/geneid.txt";
        Vector geneid = null;
        try {
            geneid = IOFile.ReadDataFirstCollum(pathgeneid, 0, delimiter);
        } catch (IOException error) {
            throw new FSException("Error when reading input file. " + error, false);
        }

        File folder = new File(path);
        String[] files = folder.list();
        for (int i = 0; i < files.length; i++) {
            File file = new File(folder, files[i]);
            if (file.isFile()) {
                try {
                    String pathresult = file.getAbsolutePath() + "-geneid.txt";
                    BufferedWriter bw = IOFile.OpenBufferedWriter(pathresult, false);
                    BufferedReader br = IOFile.OpenBufferedReader(file.getAbsolutePath());
                    while (br.ready()) {
                        StringTokenizer s = new StringTokenizer(br.readLine());
                        while (s.hasMoreTokens()) {
                            String palavra = s.nextToken();
                            int index = -1;
                            try {
                                index = Integer.valueOf(palavra);
                                int gid = (Integer.valueOf((String) geneid.get(index)));
                                System.out.println("Index: " + index + "  GeneID:" + gid);
                                bw.write(" " + gid + " (index " + index + ")     ");
                            } catch (NumberFormatException erro) {
                                //nao eh um numero.
                                bw.write(palavra);
                            } catch (ClassCastException erro) {
                                bw.write(palavra);
                            }
                        }
                        bw.write("\n");
                        bw.flush();
                    }
                    bw.close();
                    br.close();
                } catch (IOException error) {
                    throw new FSException("Error when reading input file. " + error, false);
                }
            }
        }
    }//GEN-LAST:event_jButton31ActionPerformed

    private void jRB_SFSSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_SFSSEActionPerformed
        jS_MaxSetSizeSE.setEnabled(false);
    }//GEN-LAST:event_jRB_SFSSEActionPerformed

    private void jRB_SFFSSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_SFFSSEActionPerformed
        jS_MaxSetSizeSE.setEnabled(true);
    }//GEN-LAST:event_jRB_SFFSSEActionPerformed

    private void jRB_ESSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_ESSEActionPerformed
        jS_MaxSetSizeSE.setEnabled(false);
    }//GEN-LAST:event_jRB_ESSEActionPerformed

    private void jCB_EntropySEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_EntropySEActionPerformed
        if (jCB_EntropySE.getSelectedIndex() == 0) {
            jS_AlphaSE.setEnabled(true);
            jSliderBetaSE.setEnabled(false);
        } else {
            jS_AlphaSE.setEnabled(false);
            jSliderBetaSE.setEnabled(true);
        }
    }//GEN-LAST:event_jCB_EntropySEActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String path = IOFile.OpenPath();
        if (path != null) {
            jTF_InputTestSE.setText(path);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_F1));
    }//GEN-LAST:event_jButton35ActionPerformed

    private void jCB_CriterionFucntionSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_CriterionFucntionSEActionPerformed
        if (jCB_CriterionFucntionSE.getSelectedIndex() == 1) {
            jS_QEntropySE.setEnabled(false);
        } else {
            jS_QEntropySE.setEnabled(true);
        }
    }//GEN-LAST:event_jCB_CriterionFucntionSEActionPerformed

    private void jSliderBetaSEStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderBetaSEStateChanged
        jLabelConfidenceSE.setText("Beta (value of confidence) " + jSliderBetaSE.getValue() + "% : ");
    }//GEN-LAST:event_jSliderBetaSEStateChanged

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jCB_TargetsAsPredictorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_TargetsAsPredictorsActionPerformed
        if (jCB_TargetsAsPredictors.isSelected()) {
            jLabel_TargetsPredictors.setText("Predictors: ");
        } else {
            jLabel_TargetsPredictors.setText("Targets: ");
        }
    }//GEN-LAST:event_jCB_TargetsAsPredictorsActionPerformed

    private void jCB_PeriodicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_PeriodicActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCB_PeriodicActionPerformed

    private void jTF_TargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTF_TargetActionPerformed
    }//GEN-LAST:event_jTF_TargetActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Adjacency Matrix
        int[][] am = CNMeasurements.AdjacencyMatrix(agn, 1);
        Object[] titles = new Object[am[0].length];
        Object[][] data = new Object[am.length][am[0].length];
        for (int i = 0; i < am.length; i++) {
            for (int j = 0; j < am[0].length; j++) {
                if (i == 0) {
                    titles[j] = j;
                }
                data[i][j] = am[i][j];
            }
        }
        jT_GeneratedData = new JTable(data, titles);
        jT_GeneratedData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jT_GeneratedData.setAlignmentX(JTable.CENTER_ALIGNMENT);
        jSP_GeneratedData.setViewportView(jT_GeneratedData);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jBHistogramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHistogramActionPerformed
        if (agn != null) {
            Chart.Histogram(agn);
            Chart.Distribution(agn);
        }
    }//GEN-LAST:event_jBHistogramActionPerformed

    private void jB_ShowTemporalDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_ShowTemporalDataActionPerformed
        jP_GeneratedData.remove(jT_GeneratedData);
        ShowTemporalExpressionProfile();
    }//GEN-LAST:event_jB_ShowTemporalDataActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (agn != null) {
            try {
                File fileagn = new File(jTF_InputRules.getText());
                BuildHTML.BuildIndexPage(agn, fileagn.getParent() + "/", fileagn.getName().substring(0, fileagn.getName().length() - 7), null);
                BuildHTML.BuildFiles(agn, fileagn.getParent() + "/", null);
            } catch (IOException error) {
                JOptionPane.showMessageDialog(null, "Error on building html files. " + error, "Application error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "AGN was not generated or loaded. ", "Application error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBHistogram;
    private javax.swing.JButton jB_ReadData1;
    private javax.swing.JButton jB_ReadData2;
    private javax.swing.JButton jB_ShowTemporalData;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCB_AllBooleanFunctions;
    private javax.swing.JComboBox jCB_CriterionFucntionSE;
    private javax.swing.JComboBox jCB_EntropySE;
    private javax.swing.JCheckBox jCB_HasLabels;
    private javax.swing.JComboBox jCB_NetType;
    private javax.swing.JCheckBox jCB_Periodic;
    private javax.swing.JComboBox jCB_RulesType;
    private javax.swing.JCheckBox jCB_SeparateSignal;
    private javax.swing.JCheckBox jCB_TargetsAsPredictors;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelConfidenceSE;
    private javax.swing.JLabel jLabel_TargetsPredictors;
    private javax.swing.JPanel jP_GeneratedData;
    private javax.swing.JPanel jP_NetGeneration;
    private javax.swing.JPanel jP_NetResults;
    private javax.swing.JPanel jP_SelectSearchAlgorithm;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JProgressBar jProgressBarSE;
    private javax.swing.JRadioButton jRB_ESSE;
    private javax.swing.JRadioButton jRB_SFFSSE;
    private javax.swing.JRadioButton jRB_SFSSE;
    private javax.swing.JScrollPane jSP_GeneratedData;
    private javax.swing.JSpinner jS_AlphaSE;
    private javax.swing.JSpinner jS_MaxResultListSE;
    private javax.swing.JSpinner jS_MaxSetSizeSE;
    private javax.swing.JSpinner jS_QEntropySE;
    private javax.swing.JSpinner jS_ThresholdEntropy;
    private javax.swing.JSlider jSliderBetaSE;
    private javax.swing.JTextField jTF_AvgEdges;
    private javax.swing.JTextField jTF_InputExpressionFile;
    private javax.swing.JTextField jTF_InputLabelsFile;
    private javax.swing.JTextField jTF_InputResultFile;
    private javax.swing.JTextField jTF_InputRules;
    private javax.swing.JTextField jTF_InputSpotType;
    private javax.swing.JTextField jTF_InputTestSE;
    private javax.swing.JTextField jTF_NrGenes;
    private javax.swing.JTextField jTF_NrInitializations;
    private javax.swing.JTextField jTF_NrObs;
    private javax.swing.JTextField jTF_Quantization;
    private javax.swing.JTextField jTF_QuantizationResults;
    private javax.swing.JTextField jTF_Target;
    private javax.swing.JTable jT_GeneratedData;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MainAGNWindow().setVisible(true);
            }
        });
    }
}
