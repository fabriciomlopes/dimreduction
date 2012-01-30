/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agn;

import fs.Preprocessing;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import utilities.IOFile;
import utilities.MathRoutines;

/**
 * @author fabricio@utfpr.edu.br
 */
public class MainMarieAnne {
    //usado para coloracao dos vertices do grafo

    public static final int[] RGBColors = new int[]{
        //new Color(250, 250, 250).getRGB(),//-1 == sem classe == cinza claro
        //new Color(250, 250, 250).getRGB(),//0 == sem classe == cinza claro
        Color.PINK.getRGB(), //1 == controle == rosa claro
        Color.GREEN.getRGB(), //2 == fotossintese == verde
        Color.BLUE.getRGB(), //3 == respiracao == azul
        Color.RED.getRGB(), //4 == tiamina == vermelho
        Color.ORANGE.getRGB(), //5 == glicolise == laranja
        Color.LIGHT_GRAY.getRGB()};       //6 == sem classe definida == cinza escuro
    public static final Color[] colors = new Color[]{
        new Color(250, 250, 250),//0 == sem classe == cinza claro
        Color.PINK, //1 == controle == rosa claro
        Color.GREEN, //2 == fotossintese == verde
        Color.BLUE, //3 == respiracao == azul
        Color.RED, //4 == tiamina == vermelho
        Color.ORANGE, //5 == glicolise == laranja
        Color.LIGHT_GRAY};       //6 == sem classe definida == cinza escuro
    public static final String[] classenames = new String[]{
        "sem classe",
        "controle",
        "fotossintese",
        "respiracao",
        "sintese de tiamina",
        "glicolise",
        "sem classe definida"};
    public static final String delimiter = String.valueOf(' ') + String.valueOf('\t') + String.valueOf('\n') + String.valueOf('\r') + String.valueOf('\f') + String.valueOf(';');

    public static Color getBGColor(Gene g) {
        return getBGColor(g.getClassnumber());
    }

    public static Color getBGColor(int classe) {
        if (classe >= 0 && classe < colors.length) {
            return colors[classe];
        } else {
            return (new Color(250, 250, 250));
        }
    }

    public static Color getFGColor(int classe) {
        if (classe >= 0 && classe < colors.length) {
            return (colors[classe]);
        } else {
            return (Color.BLACK);
        }
    }

    public static String getClassName(int classe) {
        if (classe >= 0 && classe < classenames.length) {
            return (classenames[classe]);
        } else {
            return ("sem classe");
        }
    }

    public static int getRGB(Gene g) {
        int classe = g.getClassnumber();
        if (classe >= 0 && classe < RGBColors.length) {
            return (RGBColors[classe]);
        } else {
            return (Color.white.getRGB());
        }
    }

    public static Vector[] RefreshGeneIDs(Vector remaingenes, Vector[] geneids) {
        Vector[] newgeneids = new Vector[geneids.length];
        newgeneids[0] = new Vector();
        newgeneids[1] = new Vector();
        for (int i = 0; i < remaingenes.size(); i++) {
            int lin = (Integer) remaingenes.get(i);
            newgeneids[0].add(i, geneids[0].get(lin));//atribui os novos ids que passaram pelo filtro.
            newgeneids[1].add(i, geneids[1].get(lin));//atribui os novos ids que passaram pelo filtro.
        }
        return newgeneids;
    }

    public static int getMetabolicPathway(Gene g) {
        int classe = 0;
        for (int i = 0; i < g.getPathway().size(); i++) {
            String pws = (String) g.getPathway().get(i);
            if (pws.equalsIgnoreCase("00730")) {//CODIGO KEGG PARA TIAMINA
                classe = 4;
            } else if (pws.equalsIgnoreCase("00010")) {//CODIGO KEGG PARA GLICOLISE
                classe = 5;
            } else if (pws.equalsIgnoreCase("03420")) {//CODIGO KEGG PARA CONTROLE
                classe = 1;
            }
        }
        return (classe);
    }

    public static boolean addDuplicateRow(int decimal, int originalindex, Vector mapindexes) {
        for (int i = 0; i < mapindexes.size(); i++) {
            if (((MapIndex) mapindexes.get(i)).getDecimalvalue() == decimal) {
                ((MapIndex) mapindexes.get(i)).addOriginalindex(originalindex);
                return (true);
            }
        }
        return (false);
    }

    public static void main(String[] args) throws IOException {
        String[] entrada = {
            "dados-com-clusters-log2.csv",//0 == dados Juliana
            "dados-root-frio.csv",//0
            "dados-root-normal.csv",//1
            "dados-shoot-frio.csv",//2
            "dados-shoot-normal.csv",//3
            "dados-razoes-frio-normal-root.csv",//4
            "dados-razoes-frio-normal-shoot.csv"};//5
        String[] saida = {
            "agn-dados-com-clusters-log2-t0.agn",//0
            "agn-dados-root-frio.agn",
            "agn-dados-root-normal.agn",
            "agn-dados-shoot-frio.agn",
            "agn-dados-shoot-normal.agn",
            "agn-dados-razoes-frio-normal-root.agn",
            "agn-dados-razoes-frio-normal-shoot.agn"};
        //nohup ~/jdk1.6.0_16/bin/java -jar dimreductionMA.jar -v ../../Marie-Anne/dados-frio/ ../../Marie-Anne/html-files/ 1 > ../../Marie-Anne/html-files/saida-exe-dados-root-normal.txt &
        //nohup ~/jdk1.6.0_16/bin/java -jar dimreductionMA.jar -v ../../Marie-Anne/dados-frio/ resultadosMA-normalization/root-cold/ 0 > resultadosMA-normalization/root-cold/saida-exe-root-frio.txt &

        //parameters of the method.
        String type_entropy = "no_obs"; //tipo de penalizacao aplicada. no_obs ou poor_obs
        float q_entropy = 1;            //if selected criterion function is CoD, q_entropy = 0. Any else float value == Entropy.
        float alpha = 1;
        float beta = 0.8f;
        int sizeresultlist = 1;         //MAXIMO DE RESPOSTAS DA SELACAO DE CARACTERISTICAS.
        int quantization = 2;
        int datatype = 1; //datatype: 1==temporal, 2==steady-state.
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-v")) {
                String pathinput = args[1]; //pasta contendo os dados de entrada.
                String pathoutput = args[2];//pasta para escrita dos resultados.
                int exe = Integer.valueOf(args[3]);//file index
                int maxfeatures = Integer.valueOf(args[4]);//max features to expand the search
                //boolean log2 = false;//Boolean.parseBoolean(args[4]);//apply log2 on microarray values.
                String pathagndatafile = pathoutput + "data-" + saida[exe];
                File agndatafile = new File(pathagndatafile);
                AGN recoverednetwork = null;
                if (!agndatafile.exists()) {
                    String pathinputfile = pathinput + entrada[exe];
                    //read data
                    float[][] expressiondata = IOFile.ReadMatrix(pathinputfile, 1, 2, delimiter);
                    Vector featurestitles = IOFile.ReadDataFirstRow(pathinputfile, 0, 2, delimiter);
                    Vector collumns = new Vector(2);
                    collumns.add(0);
                    collumns.add(1);
                    Vector[] geneids = IOFile.ReadDataCollumns(pathinputfile, 1, collumns, delimiter);

                    //Vector removedgenes = new Vector();
                    //Vector remaingenes = new Vector();
                    //remove missing values on data.
                    //retira os genes que apresentem algum spot com valor 0.
                    //float[][] filtereddata = Preprocessing.FilterMA(expressiondata, geneids, remaingenes, removedgenes);
                    //IOFile.WriteMatrix(pathoutput + "filtered-" + entrada[exe], filtereddata, ";");
                    //refresh geneIDs information, removing filtered rows.
                    //geneids = RefreshGeneIDs(remaingenes, geneids);
                    //apply log2 function on filtered data.
                    //if (log2) {
                    //expressiondata = Preprocessing.ApplyLog2(expressiondata);
                    //    filtereddata = Preprocessing.ApplyLog2(filtereddata);
                    //}
                    //int nrgenes = filtereddata.length;//expressiondata.length;
                    //int signalsize = filtereddata[0].length;//expressiondata[0].length;

                    int nrgenes = expressiondata.length;//expressiondata.length;
                    int signalsize = expressiondata[0].length;//expressiondata[0].length;

                    float[] mean = new float[signalsize];
                    float[] std = new float[signalsize];
                    float[] lowthreshold = new float[signalsize];
                    float[] hithreshold = new float[signalsize];
                    int[][] quantizeddata = new int[nrgenes][signalsize];
                    //data quantization
                    float[][] normalizeddata = Preprocessing.quantizecolumnsMAnormal(
                            expressiondata,//filtereddata
                            quantizeddata,
                            3,
                            mean,
                            std,
                            lowthreshold,
                            hithreshold);
                    IOFile.WriteMatrix(pathoutput + "original-" + entrada[exe], expressiondata, ";");
                    IOFile.WriteMatrix(pathoutput + "normalized-" + entrada[exe], normalizeddata, ";");
                    IOFile.WriteMatrix(pathoutput + "quantized-" + entrada[exe], quantizeddata, ";");
                    //REMOVER DADOS QUE APRESENTAM PADRAO CONSTANTE NA MATRIZ...
                    //nova filtragem apos a quantizacao...

                    //create data structures and keep used informations about the data.
                    recoverednetwork = new AGN(nrgenes, signalsize, quantization, datatype);
                    recoverednetwork.setMean(mean);
                    recoverednetwork.setStd(std);
                    //recoverednetwork.setRemovedgenes(removedgenes);
                    AGNRoutines.setPSNandClass(recoverednetwork, geneids);

                    recoverednetwork.setTemporalSignal(expressiondata, featurestitles);
                    //recoverednetwork.setTemporalSignal(filtereddata, featurestitles);

                    recoverednetwork.setTemporalsignalquantized(quantizeddata);
                    recoverednetwork.setTemporalsignalnormalized(normalizeddata);
                    recoverednetwork.setLowthreshold(lowthreshold);
                    recoverednetwork.setHithreshold(hithreshold);

                    //add biological  and structural informations about the genes from three sources.
                    AGNRoutines.AddAffymetrixInformation(recoverednetwork, pathinput + "affy_ATH1_array_elements-2009-7-29.txt");
                    AGNRoutines.AddNCBIInformation(recoverednetwork, pathinput + "NC_003070-003076.gbk");
                    AGNRoutines.AddKEEGInformation(recoverednetwork, pathinput + "ath_gene_map.tab", pathinput + "map_title.tab");

                    //adjust the name of the genes
                    //AGNRoutines.AdjustGeneNames(recoverednetwork);
                    IOFile.WriteAGNtoFile(recoverednetwork, pathoutput + "data-" + saida[exe]);
                    Vector genenames = AGNRoutines.getGeneNames(recoverednetwork);
                    IOFile.WriteFile(pathoutput + "genenames-" + entrada[exe], genenames, false);
                } else {
                    recoverednetwork = IOFile.ReadAGNfromFile(pathagndatafile);
                    System.out.println("Lendo agn-data do arquivo: " + pathagndatafile);
                    float[][] expressiondata = recoverednetwork.getTemporalsignal();
                    //TESTE
                    //expressiondata = Preprocessing.ApplyLog2(expressiondata);
                    int nrgenes = expressiondata.length;
                    int signalsize = expressiondata[0].length;
                    float[] mean = new float[signalsize];
                    float[] std = new float[signalsize];
                    float[] lowthreshold = new float[signalsize];
                    float[] hithreshold = new float[signalsize];
                    int[][] quantizeddata = new int[nrgenes][signalsize];

                    //data quantization
                    float[][] normalizeddata = Preprocessing.quantizecolumnsMAnormal(
                            expressiondata,
                            quantizeddata,
                            3,
                            mean,
                            std,
                            lowthreshold,
                            hithreshold);

                    recoverednetwork.setMean(mean);
                    recoverednetwork.setStd(std);
                    recoverednetwork.setTemporalsignalquantized(quantizeddata);
                    recoverednetwork.setTemporalsignalnormalized(normalizeddata);
                    recoverednetwork.setLowthreshold(lowthreshold);
                    recoverednetwork.setHithreshold(hithreshold);

                    IOFile.WriteMatrix(pathoutput + "original-" + entrada[exe], expressiondata, ";");
                    IOFile.WriteMatrix(pathoutput + "normalized-" + entrada[exe], normalizeddata, ";");
                    IOFile.WriteMatrix(pathoutput + "quantized-" + entrada[exe], quantizeddata, ";");
                    AGNRoutines.AdjustGeneNames(recoverednetwork);
                    Vector genenames = AGNRoutines.getGeneNames(recoverednetwork);
                    IOFile.WriteFile(pathoutput + "genenames-" + entrada[exe], genenames, false);
                }

                /*
                //******* PRE-PROCESSAMENTO PARA REMOVER LINHAS QUE ESTEJAM
                //******* IGUAIS, CONSIDERANDO OS DADOS QUANTIZADOS *******
                int[][] quantizeddata = recoverednetwork.getTemporalsignalquantized();
                int nrgenes = quantizeddata.length;
                int signalsize = quantizeddata[0].length;
                Vector indices = new Vector();
                int ni = 0;
                Vector mapindexes = new Vector();
                for (int row = 0; row < nrgenes; row++) {
                    int decimal = MathRoutines.Bin2Dec(quantizeddata[row]);
                    if (indices.contains(decimal)) {
                        //essa linha ja existe e foi armazenada.
                        boolean res = addDuplicateRow(decimal, row, mapindexes);
                        if (!res) {//se a resposta for negativa...algo de errado aconteceu.
                            System.out.println("Erro = indice nao encontrado...");
                            System.exit(9);//termina a execucao do aplicativo.
                        }
                    } else {
                        indices.addElement(decimal);
                        //essa linha ainda nao foi armazenada.
                        MapIndex map = new MapIndex(row, ni, decimal, quantizeddata[row]);
                        mapindexes.add(ni, map);
                        ni++;
                    }
                }
                 
                //atualiza os indices dos genes na agn e
                //monta a matriz contendo apenas os dados quantizados sem repeticao.
                int[][] filteredqd = new int[mapindexes.size()][signalsize];
                AGNRoutines.CreateFilteredQuantizedData(mapindexes, filteredqd);
                 * 
                 */

                String[] targetlocus = {
                    "AT5G54770",
                    "AT4G34200",
                    "AT2G36530",
                    "AT5G41370",
                    "AT1G05055",
                    "AT1G03190",
                    "AT3G05210",
                    "AT1G14030",
                    "AT1G67090",
                    "AT2G28000",
                    "AT2G34590",
                    "AT3G55410",
                    "AT4G24620",
                    "AT2G22480",
                    "AT2G01140",
                    "AT1G74030",
                    "AT4G37870",
                    "AT3G04080",
                    "AT1G22940",
                    "AT3G24030",
                    "AT5G65720",
                    "AT1G09430",
                    "AT2G47510",
                    "AT1G01090"};

                Vector targetindexes = new Vector();
                for (int tl = 0; tl < targetlocus.length; tl++) {
                    Vector v = new Vector();
                    v.add(-1);//index
                    v.add(-1);//class
                    v.add(-1);//original index
                    v.add("name");//name
                    targetindexes.add(v);//ORIGINAL
                }

                //recover index of the target genes taking into account the Locus.
                AGNRoutines.FindIndexes(
                        recoverednetwork,
                        targetlocus,
                        targetindexes,
                        null);

                //define the seed genes, which will be used to recover the network.
                String[] targetPSNs = {
                    "248128_AT",
                    "253274_AT",
                    "263924_AT",
                    "249307_S_AT",
                    "265218_AT",
                    "264356_AT",
                    "259304_AT",
                    "262648_AT",
                    "264474_S_AT",
                    "264069_AT",
                    "266904_AT",
                    "251787_AT",
                    "254141_AT",
                    "264044_AT",
                    "265735_AT",
                    "260392_AT",
                    "253041_AT",
                    "258567_AT",
                    "264771_AT",
                    "256907_AT",
                    "247164_AT",
                    "264504_AT",
                    "248461_S_AT",
                    "261583_AT"};

                //recover index of the target genes taking into account the ProbSetNames.
                AGNRoutines.FindIndexes(
                        recoverednetwork,
                        targetPSNs,
                        targetindexes,
                        null);

                System.out.println("Numero Total de targets = " + targetindexes.size());

                Vector targets = new Vector();
                for (int gt = 0; gt < targetindexes.size();) {
                    int tindex = (Integer) ((Vector) targetindexes.get(gt)).get(0);
                    //imprime informacoes do vetor
                    System.out.println(targetlocus[gt] + " => "
                            + targetPSNs[gt] + " => "
                            + (String) ((Vector) targetindexes.get(gt)).get(3) + " => "
                            + "Class: " + (Integer) ((Vector) targetindexes.get(gt)).get(1) + " => "
                            + "Index: " + tindex);
                    targets.add(tindex);
                    //remove os targets nao encontrados.
                    if (tindex < 0) {
                        //vtargets.add(String.valueOf(tindex));
                        targetindexes.remove(gt);
                        System.out.println("removed index " + tindex);
                    } else {
                        gt++;
                    }
                }

                System.out.println("Numero de targets encontrados = " + targetindexes.size());

                //identification of network from target genes.
                /*
                AGNRoutines.RecoverNetworkfromTemporalExpressionMA(
                recoverednetwork,
                filteredqd, //filtered quantized data
                1, //datatype: 1==temporal, 2==steady-state.
                false,
                1,//threshold_entropy
                type_entropy,
                alpha,
                beta,
                q_entropy,
                targetindexes, //targets
                maxfeatures,
                2,//1==SFS, 2==SFFS,3==SFFS_stack(expandindo todos os empates encontrados), 9==Exhaustive.
                false,//jCB_TargetsAsPredictors.isSelected()
                sizeresultlist,
                mapindexes,
                3//stacksize, used only for SFFS_Stack (option 4) == tamanho da expansao dos empatados.
                );
                 */
                recoverednetwork.setTargets(targets);
                IOFile.WriteAGNtoFile(recoverednetwork, pathoutput + "targets-" + saida[exe]);
                
                AGNRoutines.RecoverNetworkfromExpressions(
                        recoverednetwork,
                        null,
                        datatype, //datatype: 1==temporal, 2==steady-state.
                        false,//Is the signal periodic?
                        0,//Threshold value
                        type_entropy,//Penalization Method
                        alpha,//alpha value for penalization method
                        beta,//beta value for penalization method
                        q_entropy,//q-entropy value for penalization method (0 == CoD, 1 == Entropy)
                        targets, //target indexes, null == all genes are considered as targets
                        maxfeatures,//max features == dimension considered by search method
                        3,//1==SFS, 2==SFFS,3==SFFS_stack(expandindo todos os empates encontrados), 9==Exhaustive.
                        false,//The targets are considered as predictors?
                        sizeresultlist,
                        null,
                        3//stacksize, used only for SFFS_Stack (option 4) == tamanho da expansao dos empatados.
                        );
                //armazenamento dos resultados
                IOFile.WriteAGNtoFile(recoverednetwork, pathoutput + "complete-" + saida[exe]);
                /*
                //INICIO-DEBUG
                //AGN recoverednetwork = IOFile.ReadAGNnewfromFile(pathoutput + "complete-" + saida[exe]);
                //FIM-DEBUG
                BuildHTML.BuildIndexPage(
                recoverednetwork,
                pathoutput,
                entrada[exe].substring(0, entrada[exe].length() - 4),
                vtargets);
                BuildHTML.BuildFiles(
                recoverednetwork,
                pathoutput,
                vtargets);
                 */
            } else {
                //EXECUCAO COM INTERFACE GRAFICA.
                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        new MainAGNWindow().setVisible(true);
                    }
                });
            }
        } else {
            //EXECUCAO COM INTERFACE GRAFICA.
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    new MainAGNWindow().setVisible(true);
                }
            });
        }
    }
}
