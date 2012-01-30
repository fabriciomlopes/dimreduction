package agn;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import utilities.IOFile;

/**
 * @author Fabricio
 */
public class Tsallis {

    public static int mediana(float[][] M) {
        int ini = 0, fim = M.length;
        for (int i = 0; i < M.length; i++) {
            if (M[i][0] > 0 && ini == 0) {
                ini = i;
                i = M.length;
            }
        }
        int mediana = (ini + fim) / 2;
        return (((Float) M[mediana][1]).intValue());
    }

    public static float media(float[][] M) {
        int count = 0;
        float soma = 0;
        for (int i = 0; i < M.length; i++) {
            if (M[i][0] > 0) {
                count++;
                soma += M[i][0];
            }
        }
        float media = soma / count;
        return (media);
    }

    public static void main(String[] args) {
        String path = args[0];//arquivo de entrada .csv
        File result = new File(path);
        int MAXEXE = 100;
        int exe = 0;
        if (result.exists()) {
            float[][][] besttpr = new float[MAXEXE][100][9];
            float[][][] bestq1 = new float[MAXEXE][100][9];//EXECUCAO/TARGET/VALORES
            int[][][] preditoresmediosq = new int[5][31][2];
            try {
                BufferedReader br = IOFile.OpenBufferedReader(path);
                float qvalue = 0;
                float tpr = 0;
                float tnr = 0;
                float tp = 0;
                float fp = 0;
                float fn = 0;
                float tn = 0;
                float npreditoresoriginais = 0;
                while (br.ready()) {
                    StringTokenizer s = new StringTokenizer(br.readLine(), ";");
                    int columns = s.countTokens();
                    int target = -1;
                    int avgedges = -1;
                    String topology = null;
                    int[] originalpredictors = null;
                    float cfvalue = -1;
                    int haveties = -1;
                    int nties = -1;
                    int[] inferredpredictors = null;
                    float similarity = -1;
                    for (int i = 0; i < columns; i++) {
                        String str = s.nextToken();
                        if (str.equalsIgnoreCase("target")) {
                            exe++;
                            //linha de cabecalho...
                            i = columns;//descarta linha
                        } else if (i == 0) {
                            //target == int
                            target = Integer.valueOf(str);
                        } else if (i == 1) {
                            //k - avg-edges == int
                            avgedges = Integer.valueOf(str);
                        } else if (i == 2) {
                            //topology == String == ER/BA
                            topology = str;
                        } else if (i == 3) {
                            //Original predictors == int []
                            StringTokenizer sub = new StringTokenizer(str);
                            int np = sub.countTokens();//numero de preditores inferidos.
                            if (np > 0) {
                                originalpredictors = new int[np];
                                for (int p = 0; p < np; p++) {
                                    int predictor = Integer.valueOf(sub.nextToken());
                                    if (predictor >= 0) {
                                        originalpredictors[p] = predictor;
                                        npreditoresoriginais++;
                                    } else {
                                        originalpredictors = null;
                                        p = np;
                                    }
                                }
                            }
                        } else if (i == 4) {
                            //criterion function value = float
                            cfvalue = Float.valueOf(str);
                        } else if (i == 5) {
                            qvalue = Float.valueOf(str);

                            //q-value == float
                            if (qvalue > 0.11) {
                                qvalue = Float.valueOf(str);
                            } else if (Float.valueOf(str) != qvalue) {
                                //alterou o valor do qvalue
                                //fazer um sumario...
                                /*
                                fn = npreditoresoriginais - tp;
                                System.out.println("true positives=" + tp);
                                System.out.println("false negatives=" + fn);
                                tpr = tp / (tp + fn);
                                System.out.println("true positives rate=" + tpr);

                                System.out.println("false positives=" + fp);
                                tn = 10000 - fp - tp - fn;
                                System.out.println("true negatives" + tn);
                                tnr = tn / (tn + fp);
                                System.out.println("true negatives rate=" + tnr+"\n\n");

                                if (besttpr[target][0] < tpr) {
                                besttpr[target][0] = tpr;
                                besttpr[target][1] = avgedges;
                                besttpr[target][2] = qvalue;
                                }

                                tpr = 0;
                                tnr = 0;
                                tp = 0;
                                fp = 0;
                                fn = 0;
                                tn = 0;
                                npreditoresoriginais = 0;
                                qvalue = Float.valueOf(str);
                                 */
                            }
                        } else if (i == 6) {
                            //ties == 0/1
                            haveties = Integer.valueOf(str);
                        } else if (i == 7) {
                            //#ties == int
                            nties = Integer.valueOf(str);
                        } else if (i == 8) {
                            //predictors inferred = int []
                            StringTokenizer sub = new StringTokenizer(str);
                            int nip = sub.countTokens();//numero de preditores inferidos.
                            inferredpredictors = new int[nip];
                            for (int p = 0; p < nip; p++) {
                                inferredpredictors[p] = Integer.valueOf(sub.nextToken());
                            }

                            if (inferredpredictors.length > 0) {
                                float valor = qvalue;
                                int posicao = 0;
                                while (valor > 0.11f) {
                                    valor = valor - 0.1000000000000000f;
                                    posicao++;
                                }
                                preditoresmediosq[avgedges - 1][posicao][0] += inferredpredictors.length;
                                preditoresmediosq[avgedges - 1][posicao][1]++;
                            }

                        } else if (i == 9) {
                            //TP == float
                            //tp = Float.valueOf(str);
                        } else if (i == 10) {
                            //FP == float
                            //fp = Float.valueOf(str);
                        } else if (i == 11) {
                            //TPRate == float
                            int achou = 0;
                            for (int ip = 0; ip < inferredpredictors.length; ip++) {
                                if (originalpredictors != null) {
                                    for (int op = 0; op < originalpredictors.length; op++) {
                                        if (originalpredictors[op] == inferredpredictors[ip]) {
                                            achou++;
                                            tp++;
                                        }
                                    }
                                } else {
                                    fp += inferredpredictors.length;
                                    ip = inferredpredictors.length;
                                    achou = inferredpredictors.length;
                                }
                            }
                            fp += inferredpredictors.length - achou;

                            //fechou a linha de execucao.
                            fn = npreditoresoriginais - tp;
                            if ((tp + fn) > 0) {
                                tpr = tp / (tp + fn);
                            } else {
                                tpr = 0;
                            }
                            tn = 100 - tp - fp - fn;
                            tnr = tn / (tn + fp);
                            similarity = ((Double) Math.sqrt(tnr * tpr)).floatValue();
                            /*
                            System.out.println("true positives=" + tp);
                            System.out.println("false negatives=" + fn);
                            System.out.println("true positives rate=" + tpr);
                            System.out.println("false positives=" + fp);
                            System.out.println("true negatives=" + tn);
                            System.out.println("true negatives rate=" + tnr + "\n\n");
                            System.out.println("similarity=" + similarity + "\n\n");
                             */
                            if (besttpr[exe][target][0] < similarity && avgedges == 5) {
                                besttpr[exe][target][0] = similarity;
                                besttpr[exe][target][1] = avgedges;
                                besttpr[exe][target][2] = qvalue;
                                besttpr[exe][target][4] = tp;
                                besttpr[exe][target][5] = fp;
                                besttpr[exe][target][6] = tn;
                                besttpr[exe][target][7] = fn;
                                besttpr[exe][target][8] = inferredpredictors.length;
                            }/*else if (besttpr[exe][target][0] == similarity && tp > besttpr[exe][target][4]) {// && avgedges == 4){
                            besttpr[exe][target][0] = similarity;
                            besttpr[exe][target][1] = avgedges;
                            besttpr[exe][target][2] = qvalue;
                            besttpr[exe][target][4] = tp;
                            besttpr[exe][target][5] = fp;
                            besttpr[exe][target][6] = tn;
                            besttpr[exe][target][7] = fn;
                            besttpr[exe][target][8] = inferredpredictors.length;
                            }*/
                            if (bestq1[exe][target][0] < similarity && qvalue < 1.1 && qvalue > 0.99 && avgedges == 5) {
                                bestq1[exe][target][0] = similarity;
                                bestq1[exe][target][1] = avgedges;
                                bestq1[exe][target][2] = qvalue;
                                bestq1[exe][target][4] = tp;
                                bestq1[exe][target][5] = fp;
                                bestq1[exe][target][6] = tn;
                                bestq1[exe][target][7] = fn;
                            }
                            tpr = 0;
                            tnr = 0;
                            tp = 0;
                            fp = 0;
                            fn = 0;
                            tn = 0;
                            npreditoresoriginais = 0;
                            qvalue = -1;
                        } else {
                            //faz a leitura e descarta...
                        }
                    }
                }
                br.close();
            } catch (IOException error) {
                System.out.println("Erro na leitura do arquivo de entrada. Erro: " + error);
            }
            //System.out.println("Melhores resultados:");
            int posicaomax = 0;
            float[][] bestsimilarity = new float[MAXEXE][2];
            for (exe = 0; exe < MAXEXE; exe++) {
                float tp = 0;
                float fp = 0;
                float tn = 0;
                float fn = 0;
                float tpq1 = 0;
                float fpq1 = 0;
                float tnq1 = 0;
                float fnq1 = 0;
                for (int i = 0; i < 100; i++) {
                    //System.out.println("bq - target " + i + "   " + besttpr[i][0] + "   " + besttpr[i][1] + "   " + besttpr[i][2]);
                    //System.out.println("q1 - target " + i + "   " + bestq1[i][0] + "   " + bestq1[i][1] + "   " + bestq1[i][2]);
                    //System.out.println("Vetor sem ordenacao:");
                    //System.out.println(bestq[exe]);
                    tp += besttpr[exe][i][4];
                    fp += besttpr[exe][i][5];
                    tn += besttpr[exe][i][6];
                    fn += besttpr[exe][i][7];

                    tpq1 += bestq1[exe][i][4];
                    fpq1 += bestq1[exe][i][5];
                    tnq1 += bestq1[exe][i][6];
                    fnq1 += bestq1[exe][i][7];
                    //if (besttpr[exe][i][2] > 0){
                    //    System.out.println(besttpr[exe][i][1] + ";" + besttpr[exe][i][2]);
                    //}
                }
                if (tn == 0) {
                    bestsimilarity[exe][0] = 0;
                } else {
                    bestsimilarity[exe][0] = (float) Math.sqrt((tn / (tn + fp)) * (tp / (tp + fn)));
                }
                bestsimilarity[exe][1] = exe;
            }
            //exibe a mediana dos resultados:
            //ordernar vetor bestq;
            fs.Preprocessing.QuickSort(bestsimilarity, 0, MAXEXE - 1);
            //fs.Preprocessing.QuickSort(bestsimilarity, 0, MAXEXE - 1);
            //System.out.println("Vetor ordenado:");
            //for (exe = 0; exe < MAXEXE; exe++) {
            //    System.out.println(bestsimilarity[exe][0]);
            //}
            //exibir valor mediano, desconsiderando as posicoes com valores 0;
            //int posicaodamediana = mediana(bestsimilarity);
            posicaomax = (int) bestsimilarity[99][1];
            //System.out.println(besttpr[posicaodamediana][i][1] + ";" + besttpr[posicaodamediana][i][2]);

            float tp = 0;
            float fp = 0;
            float tn = 0;
            float fn = 0;
            float tpq1 = 0;
            float fpq1 = 0;
            float tnq1 = 0;
            float fnq1 = 0;
            for (int i = 0; i < 100; i++) {
                tp += besttpr[posicaomax][i][4];
                fp += besttpr[posicaomax][i][5];
                tn += besttpr[posicaomax][i][6];
                fn += besttpr[posicaomax][i][7];

                tpq1 += bestq1[posicaomax][i][4];
                fpq1 += bestq1[posicaomax][i][5];
                tnq1 += bestq1[posicaomax][i][6];
                fnq1 += bestq1[posicaomax][i][7];
            }

            System.out.println("\nResultados totais melhor q para cada preditor entre todas as execucoes:");
            System.out.println("TP = " + tp);
            System.out.println("FP = " + fp);
            System.out.println("TN = " + tn);
            System.out.println("FN = " + fn);
            System.out.println("TPR = " + (tp / (tp + fn)));
            System.out.println("TNR = " + (tn / (tn + fp)));
            System.out.println("Similatiry = " + Math.sqrt((tn / (tn + fp)) * (tp / (tp + fn))));

            System.out.println("\nResultados totais melhor q1 (considerando todos os k):");
            System.out.println("TP = " + tpq1);
            System.out.println("FP = " + fpq1);
            System.out.println("TN = " + tnq1);
            System.out.println("FN = " + fnq1);
            System.out.println("TPR = " + (tpq1 / (tpq1 + fnq1)));
            System.out.println("TNR = " + (tnq1 / (tnq1 + fpq1)));
            System.out.println("Similatiry = " + Math.sqrt((tnq1 / (tnq1 + fpq1)) * (tpq1 / (tpq1 + fnq1))));

            /*System.out.println("\n\nResultados kmedio inferido para cada q:");
            for (int k = 0; k < 5; k++) {
            System.out.println("k==" + (k + 1));
            float valorq = 0.1f;
            for (int q = 0; q < 31; q++) {
            System.out.println("q == " + valorq + ";" + (float) preditoresmediosq[k][q][0] / (float) preditoresmediosq[k][q][1]);
            valorq += 0.1f;
            }
            }*/
        }
    }
}
