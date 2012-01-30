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
/*** Contact: David Correa Martins Junior - davidjr@vision.ime.usp.br    ***/
/***          Fabricio Martins Lopes - fabriciolopes@vision.ime.usp.br   ***/
/***          Roberto Marcondes Cesar Junior - cesar@vision.ime.usp.br   ***/
/***************************************************************************/
/***************************************************************************/
/*** This class implements conditional entropy.                          ***/
/*** There are two types of mean conditional entropy:                    ***/
/*** no_obs (penalty for non-observed instances) and                     ***/
/*** poor_obs (penalty for poorly observed instances).                   ***/
/*** The Tsallis entropy (q-entropy) is adopted here, and q is a         ***/
/*** parameter. The traditional Shannon entropy is obtained for q = 1.   ***/
/***************************************************************************/
package fs;

import agn.AGN;
import agn.AGNRoutines;
import agn.CNMeasurements;
import agn.PredictorSet;
import agn.Topologies;
import java.io.Serializable;
import java.util.Vector;
import utilities.IOFile;

public class FS implements Serializable {

    public static final float deltae = 0.05f;
    public Vector I;
    public Vector probtable;
    public int columns;
    public float hGlobal;
    public char[][] A;
    public int n;
    public int c;
    public String type;
    public float alpha;
    public float beta;
    public float q;
    public int itmax;
    public Vector<Vector> resultlist;
    public int resultlistsize;
    public float maxresultvalue;
    public float[] bestentropy;//armazena as entropias dos melhores conjuntos de caracteristicas,
    //o indice representa a cardinalidade do conjunto.
    public Vector[] bestset;    //armazena os melhores conjuntos de caracteristicas.
    public float[] tiesentropy;//armazena a entropia dos empates ocorridos em cada cardinalidade.
    public Vector[] ties;       //armazena os empates ocorridos em cada cardinalidade.
    public float[] jointentropiesties;       //armazena as entropias conjuntas dos preditores empatados.
    //atributos usados para execucao do SFFS-Stack
    public Vector exestack;
    public Vector expandedestack;

    public FS(char[][] samples, int npv, int nc, String typeMCE_COD,
            float alphaPenalty, float betaConfidence, float qentropy,
            int maxresultlistsize) {
        I = new Vector();
        hGlobal = 1.0f;
        A = samples;
        columns = A[0].length;
        n = npv;
        c = nc;
        type = typeMCE_COD;
        alpha = alphaPenalty;
        beta = betaConfidence;
        q = qentropy;
        resultlistsize = maxresultlistsize;
        maxresultvalue = 1;
        resultlist = new Vector();
        itmax = (int) Math.floor(Math.log(A.length) / Math.log(n));
        probtable = new Vector();
        exestack = new Vector();
        expandedestack = new Vector();
    }

    public void InsertinResultList(Vector I, float hmin) {
        Vector item = new Vector();
        item.add(hmin);//adiciona o valor da funcao criterio na posicao 0
        //adiciona os indices das caracteristicas selecionadas como um vetor
        //na posicao 1.
        Vector features = new Vector();
        for (int i = 0; i < I.size(); i++) {
            int f = (Integer) I.get(i);
            features.add(f);
        }
        item.add(features);
        if (resultlist.size() < resultlistsize) {
            resultlist.add(item);
            //Preprocessing.QuickSort(resultlist, 0, resultlist.size() - 1);
            if (resultlist.size() > 1) {
                Preprocessing.SelectionSort(resultlist);
            }
        } else {
            float vi = (Float) item.get(0);
            float vs = (Float) resultlist.get(resultlistsize - 1).get(0);
            if (vi < vs) {
                resultlist.set(resultlistsize - 1, item);
                //Preprocessing.QuickSort(resultlist, 0, resultlistsize - 1);
                if (resultlist.size() > 1) {
                    Preprocessing.SelectionSort(resultlist);
                }
            }
        }
        /*
        //just for confering
        for (int i = 0; i < resultlist.size(); i++) {
        Vector ri = resultlist.get(i);
        float fsvalue = (Float) ri.get(0);
        System.out.print("index = " + i + "  FS Value = " + fsvalue + " features = ");
        Vector ritem = (Vector) ri.get(1);
        for (int j = 0; j < ritem.size(); j++) {
        System.out.print((Integer) ritem.get(j) + "-");
        }
        System.out.println("\n");
        }
        System.out.println("\n\n");
         */
    }

    //implementar o metodo de desempate entre os preditores observando a entropia conjunta dos preditores.
    public void BreakTies(int i) {//i == cardinalidade de preditores
        if (ties[i] == null || tiesentropy[i] == 1) {
            return;//algum problema...
        }
        jointentropiesties = new float[ties[i].size()];
        float maxjointentropy = 0;
        int maxjointentropyposition = 0;
        for (int p = 0; p < ties[i].size(); p++) {
            Vector predictors = (Vector) ties[i].get(p);
            jointentropiesties[p] = Criteria.jointentropy(n, predictors, A, c);
            if (jointentropiesties[p] > maxjointentropy) {
                maxjointentropy = jointentropiesties[p];
                maxjointentropyposition = p;
                /*
                //debug
                if (p > 1) {
                System.out.println("houve alteracao!");
                System.out.println("max entropy = " + maxjointentropy);
                System.out.print("Predictors: ");
                for (int pred = 0; pred < predictors.size(); pred++) {
                System.out.print(predictors.get(pred) + "  ");
                }
                System.out.print("\n\n");
                //fim debug
                }
                 */
            }
        }
        I = (Vector) ties[i].get(maxjointentropyposition);
    }

    //encontra o valor minimo de entropia entre os conjuntos de tamanho i, i=1...maxfeatures.
    public void Minimal(int maxsetsize, float delta, int targetindex) {
        int posminimal = 0;
        hGlobal = 1.1f;
        //recupera o conjunto de preditores com MENOR cardinalidade e que
        //apresente a menor entropia == resposta da funcao criterio utilizada.
        //heuristica, para aumentar a cardinalidade,
        //deve melhorar (diminuir) a funcao criterio pelo menos delta.
        for (int i = 1; i <= maxsetsize; i++) {
            if (bestentropy[i] < hGlobal && Math.abs(bestentropy[i] - hGlobal) > delta) {
                //ROTINA PARA DEFINIR UM THERSHOLD ADAPTATIVO
                //valor adaptativo de acordo com o tamanho do sinal e o numero de erros permitido.
                //float deltac = 0;
                //float[] pYdX = new float[c];
                //float pX = A.length - 1;//observacao de todos.
                //int lines = A.length;
                //pYdX[0] = 1;
                //pYdX[1] = (pX - 1);//menos o observado na posicao 0, e assumindo outra combinacao com entropia 0.
                //deltac = Criteria.instanceCriterion(pYdX, pX, type, alpha, beta, lines, n, bestset[i].size(), c, q);
                //FIM-ROTINA THRESHOLD ADAPTATIVO.
                //if (Math.abs(bestentropy[i] - hGlobal) >= deltac) {
                //if (bestentropy[i] <= deltac) {
                hGlobal = bestentropy[i];
                I = bestset[i];
                posminimal = i;
                //}else{
                //    System.out.println("erro recusado pelo delta de variacao.");
                //}
            }
        }
        //select the set with higher joint entropy value among tie sets.
        //if (ties[posminimal] != null && ties[posminimal].size() > 1) {
        //    BreakTies(posminimal);
        //}
        float cfvalue = Criteria.MCE_COD(type, alpha, beta, n, c, I, A, q);
        //keep current conditional probability table
        probtable = (Vector) Criteria.probtable.clone();

        System.out.print("Preditores escolhidos: ");
        IOFile.PrintPredictors(I, cfvalue, targetindex);
    }
    
    //encontra o valor minimo de entropia entre os conjuntos de tamanho i, i=1...maxfeatures.
    public void Minimal_Gensips2011(int maxsetsize, float delta, AGN goldstandard, int targetindex, float weight) {
        int posminimal = 0;
        hGlobal = 1.1f;
        //recupera o conjunto de preditores com MENOR cardinalidade e que
        //apresente a menor entropia == resposta da funcao criterio utilizada.
        //heuristica, para aumentar a cardinalidade,
        //deve melhorar (diminuir) a funcao criterio pelo menos delta.
        for (int i = 1; i <= maxsetsize; i++) {
            if (bestentropy[i] < hGlobal && Math.abs(bestentropy[i] - hGlobal) > delta) {
                //ROTINA PARA DEFINIR UM THERSHOLD ADAPTATIVO
                //valor adaptativo de acordo com o tamanho do sinal e o numero de erros permitido.
                //float deltac = 0;
                //float[] pYdX = new float[c];
                //float pX = A.length - 1;//observacao de todos.
                //int lines = A.length;
                //pYdX[0] = 1;
                //pYdX[1] = (pX - 1);//menos o observado na posicao 0, e assumindo outra combinacao com entropia 0.
                //deltac = Criteria.instanceCriterion(pYdX, pX, type, alpha, beta, lines, n, bestset[i].size(), c, q);
                //FIM-ROTINA THRESHOLD ADAPTATIVO.
                //if (Math.abs(bestentropy[i] - hGlobal) >= deltac) {
                //if (bestentropy[i] <= deltac) {
                hGlobal = bestentropy[i];
                I = bestset[i];
                posminimal = i;
                //}else{
                //    System.out.println("erro recusado pelo delta de variacao.");
                //}
            }
        }
        //select the set with higher joint entropy value among tie sets.
        //if (ties[posminimal] != null && ties[posminimal].size() > 1) {
        //    BreakTies(posminimal);
        //}
        float cfvalue = Criteria.MCE_COD_Gensips2011(type, alpha, beta, n, c, I, A, q, goldstandard, targetindex, weight);
        //keep current conditional probability table
        probtable = (Vector) Criteria.probtable.clone();
        System.out.print("Preditores escolhidos: ");
        IOFile.PrintPredictors(I, cfvalue, targetindex);
    }    

    //encontra o valor minimo de entropia entre os conjuntos de tamanho i, i=1...maxfeatures.
    public void MinimalMA(int maxsetsize) {
        int posminimal = 0;
        //recupera o conjunto de preditores com MAIOR cardinalidade e que apresente a menor entropia == resposta da funcao criterio utilizada.
        for (int i = 1; i <= maxsetsize; i++) {
            if (bestentropy[i] <= hGlobal) {
                hGlobal = bestentropy[i];
                I = bestset[i];
                posminimal = i;
            }
        }
        //if (ties[posminimal] != null && ties[posminimal].size() > 1) {
        //    BreakTies(posminimal);
        //}
        float cfvalue = Criteria.MCE_COD(type, alpha, beta, n, c, I, A, q);
        //keep current conditional probability table
        probtable = (Vector) Criteria.probtable.clone();
    }

    //inicializa os valores dos conjuntos utilizados pelo SFFS.
    public void Inicialize(int maxfeatures) {
        bestentropy = new float[maxfeatures];//a posicao 0 nao eh usada.
        bestset = new Vector[maxfeatures];//a posicao 0 nao eh usada.
        tiesentropy = new float[maxfeatures];//a posicao 0 nao eh usada.
        ties = new Vector[maxfeatures];//a posicao 0 nao eh usada.
        for (int i = 0; i < bestentropy.length; i++) {
            bestentropy[i] = 1;
            tiesentropy[i] = 1;
            bestset[i] = new Vector();
            ties[i] = new Vector();
        }
    }
    // runs the SFS algorithm
    //o target eh recebido apenas para impressao de informacao para o usuario (quando o codigo estiver habilitado).

    public synchronized void runSFS(boolean calledByExhaustive, int maxfeatures) {
        int collumns = A[0].length;
        for (int i = 0; i < collumns - 1; i++) // for each dimension
        {
            float hMin = 1.1f;
            int fMin = -1;
            float H = 1.1f;
            I.addElement(-1);
            for (int f = 0; f < collumns - 1; f++) // for each feature
            {
                if (I.contains(f)) {// || !CNMeasurements.hasVariation(A, f)) {
                    continue; // the feature was included in the subspace already
                    // substituting the last element included by a new feature for
                    // evaluation with the subspace included previously
                }
                I.remove(I.lastElement());
                I.addElement(f);
                // calculating the mean conditional entropy for the current
                // subspace

                H = Criteria.MCE_COD(type, alpha, beta, n, c, I, A, q);

                //System.out.println(I.size()+" "+H);
                if (H < hMin)// if the new entropy is the smallest of all
                // features previously evaluated with the subspace
                // previously included, sets the new entropy as the
                // lowest and the new feature as canditate to be
                // included in the final subspace
                {
                    fMin = f;
                    hMin = H;

                    //codigo para armazenar os melhores resultados.
                    InsertinResultList(I, H);

                    //}else if (H < hMin && CoD > CoDMin){
                    //    System.out.println("Caso que a entropia nao melhora a tx de acertos.");
                }
                if (H == 0) // if the new entropy is the lowest possible (0), stops
                // and returns the subspace obtained
                {
                    break;
                }
            }
            //if (I.size() <= maxfeatures)
            if (hMin < hGlobal)// if the entropy of the subspace with dimension
            // "dim" is smaller than that of the subspace included before
            // (dim - 1)
            {
                I.remove(I.lastElement()); // remove the last element included...
                I.addElement(fMin); // adds the feature with lowest entropy
                /*
                System.out.println("entropia global de "+hGlobal+" para "+hMin+" usando preditor: ");
                for (int e = 0; e < I.size(); e++)
                if ((Integer) I.get(e) < target)
                System.out.print((Integer) I.get(e)+" ");
                else
                System.out.print((((Integer) I.get(e))+1)+" ");
                System.out.print("\n");
                 */
                hGlobal = hMin; // sets entropy of the new subspace as hMin
                if (hGlobal == 0 || I.size() >= maxfeatures) // if the new subspace has the lowest possible
                // entropy, stops and returns it
                {
                    break;
                }
            } else // if the entropy of the subspace with dimension "dim" is greater
            // than that of the subspace included before (dim - 1)
            {
                I.remove(I.lastElement()); // it is time to stop and return the
                // subspace obtained before (dim - 1)
                break;
            }
        }
        if (calledByExhaustive) // if it is running Exhaustive Search, it is made a call to SFS in order to
        // get the ideal dimension
        {
            itmax = I.size();
        } else {
            /*
            System.out.println(hGlobal);
            for (int i = 0; i < I.size(); i++)
            System.out.print(I.elementAt(i)+" ");
            System.out.println();
             */
        }
    }

    public void BestSet(Vector bestset, float[] bestentropy, Vector other,
            float entropy) {
        int size = other.size();
        if (entropy < bestentropy[size]) {
            bestentropy[size] = entropy;
            bestset.clear();
            for (int i = 0; i < size; i++) {
                bestset.add(other.elementAt(i));
            }
        }
    }

    // runs the SFFS algorithm
    public synchronized void runSFFS(
            int maxfeatures,
            int targetindex,
            AGN agn) {
        if (maxfeatures >= columns) {
            maxfeatures = columns - 1;
        }
        Inicialize(maxfeatures + 1);//Inicializa os atributos usados para armazenar os conjuntos e entropias empatadas.
        while (I.size() < maxfeatures) {
            float hMin = 1.1f;
            int fMin = -1;
            float H = 1.1f;
            I.addElement(-1);
            for (int f = 0; f < columns - 1; f++) {
                // for each feature
                if (agn != null) {
                    int predictorindex = f;
                    if (predictorindex >= targetindex) {
                        predictorindex++;
                    }
                    //do not consider array controls.
                    if (agn.getGenes()[predictorindex].isControl()) {
                        continue;
                    }
                }
                if (I.contains(f)) {// || !CNMeasurements.hasVariation(A, f)) {
                    continue;
                    // the feature was included in the subspace already
                    // substituting the last element included by a new feature for
                    // evaluation with the subspace included previously
                }
                I.remove(I.lastElement());
                I.addElement(f);
                 H = Criteria.MCE_COD(type, alpha, beta, n, c, I, A, q);

                if (H < hMin) {
                    //if the new entropy is the smallest of all
                    // features previously evaluated with the subspace
                    // previously included, sets the new entropy as the
                    // lowest and the new feature as canditate to be
                    // included in the final subspace
                    fMin = f;
                    hMin = H;
                    //codigo para armazenar os melhores resultados.
                    InsertinResultList(I, H);
                }
                //se houve empate ou reducao de entropia
                //os empates armazenam tambem a resposta do algoritmo e nao soh os empates.
                if (Math.abs(H - hMin) < deltae) {
                    if (ties[I.size()] == null) {
                        ties[I.size()] = new Vector();                    //para os casos de empate.
                    }
                    if (H < tiesentropy[I.size()]) {
                        //houve reducao de entropia.
                        ties[I.size()].clear();
                        tiesentropy[I.size()] = H;
                    }
                    //adiciona o conjunto empatado no vetor.
                    Vector titem = new Vector();
                    for (int cc = 0; cc < I.size(); cc++) {
                        titem.add(I.get(cc));
                    }
                    ties[I.size()].add(titem);
                }
            }
            // if the entropy of the subspace with dimension
            // "dim" is smaller than that of the subspace included before
            // (dim - 1)
            if (I.size() < maxfeatures && fMin != -1) {
                I.remove(I.lastElement()); // remove the last element included...
                I.addElement(fMin); // adds the feature with lowest entropy
                //concidional para armazenar o melhor conjunto de cada tamanho <= maxfeatures.
                if (bestset[I.size()] == null) {
                    bestset[I.size()] = new Vector();
                    for (int s = 0; s < I.size(); s++) {
                        bestset[I.size()].add(I.elementAt(s));
                    }
                    bestentropy[I.size()] = hMin;
                } else {
                    BestSet(bestset[I.size()], bestentropy, I, hMin);
                }
                //heuristica para para execucao, no caso de nao melhorar os resultados.
                int repeticoes = 0;
                for (int be = 1; be < bestentropy.length - 1; be++) {
                    if (bestentropy[be] < 1 && Math.abs(bestentropy[be] - bestentropy[be + 1]) < deltae) {
                        repeticoes++;
                    }
                }
                if (repeticoes > 1) {
                    break;
                }
                boolean again = true;
                while (I.size() > 2 && again) {
                    //inicio do passo 2 e 3 do sffs: exclusao condicional e
                    //continuacao da exclusao condicional, enquanto os conjuntos
                    //selecionados forem melhores que os seus predecessores
                    int combinations = I.size();
                    float le = 1; //melhor entropia encontrada com as exclusoes
                    int lmf = -1; //caracteristica menos importante
                    for (int comb = 0; comb < combinations; comb++) {
                        Vector xk = new Vector();
                        //monta o vetor com -1 caracteristica e testa todas as combinacoes.
                        for (int nc = 0; nc < combinations; nc++) {
                            if (nc != comb) {
                                xk.add(I.elementAt(nc));
                            }
                        }
                        float nh = Criteria.MCE_COD(type, alpha, beta, n, c, xk, A, q);
                        //BestSet(bestset[xk.size()], bestentropy, xk, nh[0]);
                        //armazena a melhor solucao e qual foi a caracteristica excluida.
                        if (nh < le) {
                            le = nh;
                            lmf = (Integer) I.elementAt(comb);
                        }
                    }
                    if (le < bestentropy[I.size() - 1] && lmf != fMin) {
                        I.remove((Integer) lmf);
                        BestSet(bestset[I.size()], bestentropy, I, le);
                        //codigo para armazenar os melhores resultados.
                        InsertinResultList(I, le);
                        again = true;
                        fMin = -1;//a verificacao entre lmf e fMin so vale para
                        //o passo 2, logo eh verificado apenas na primeira passagem.
                        //houve reducao de entropia, atualiza o vetor e empates.
                        ties[I.size()].clear();
                        tiesentropy[I.size()] = le;
                        //adiciona o conjunto empatado no vetor.
                        Vector titem = new Vector();
                        for (int cc = 0; cc < I.size(); cc++) {
                            titem.add(I.get(cc));
                        }
                        ties[I.size()].add(titem);
                    } else {
                        again = false;
                    }
                }
                if (hMin <= deltae) {
                    //achou o minimo global e seus empates (se houver).
                    break;
                }
            } else {
                // if the entropy of the subspace with dimension "dim" is greater
                // than that of the subspace included before (dim - 1)
                // subspace obtained before (dim - 1)
                I.remove(I.lastElement()); // it is time to stop and return the
                break;
            }
        }
        Minimal(maxfeatures, 0.01f, targetindex);//returns the feature set with minimal result.
    }
    
    // runs the SFFS algorithm
    public synchronized void runSFFS_Gensips2011(
            int maxfeatures,
            int targetindex,
            AGN goldstandard,
            float weight) {
        if (maxfeatures >= columns) {
            maxfeatures = columns - 1;
        }
        Inicialize(maxfeatures + 1);//Inicializa os atributos usados para armazenar os conjuntos e entropias empatadas.
        while (I.size() < maxfeatures) {
            float hMin = 1.1f;
            int fMin = -1;
            float H = 1.1f;
            I.addElement(-1);
            for (int f = 0; f < columns - 1; f++) {
                // for each feature
                if (goldstandard != null) {
                    int predictorindex = f;
                    if (predictorindex >= targetindex) {
                        predictorindex++;
                    }
                    //do not consider array controls.
                    if (goldstandard.getGenes()[predictorindex].isControl()) {
                        continue;
                    }
                }
                if (I.contains(f)) {// || !CNMeasurements.hasVariation(A, f)) {
                    continue;
                    // the feature was included in the subspace already
                    // substituting the last element included by a new feature for
                    // evaluation with the subspace included previously
                }
                I.remove(I.lastElement());
                I.addElement(f);
                H = Criteria.MCE_COD_Gensips2011(type, alpha, beta, n, c, I, A, q, goldstandard, targetindex, weight);

                if (H < hMin) {
                    //if the new entropy is the smallest of all
                    // features previously evaluated with the subspace
                    // previously included, sets the new entropy as the
                    // lowest and the new feature as canditate to be
                    // included in the final subspace
                    fMin = f;
                    hMin = H;
                    //codigo para armazenar os melhores resultados.
                    InsertinResultList(I, H);
                }
                //se houve empate ou reducao de entropia
                //os empates armazenam tambem a resposta do algoritmo e nao soh os empates.
                if (Math.abs(H - hMin) < deltae) {
                    if (ties[I.size()] == null) {
                        ties[I.size()] = new Vector();                    //para os casos de empate.
                    }
                    if (H < tiesentropy[I.size()]) {
                        //houve reducao de entropia.
                        ties[I.size()].clear();
                        tiesentropy[I.size()] = H;
                    }
                    //adiciona o conjunto empatado no vetor.
                    Vector titem = new Vector();
                    for (int cc = 0; cc < I.size(); cc++) {
                        titem.add(I.get(cc));
                    }
                    ties[I.size()].add(titem);
                }
            }
            // if the entropy of the subspace with dimension
            // "dim" is smaller than that of the subspace included before
            // (dim - 1)
            if (I.size() <= maxfeatures && fMin != -1) {
                I.remove(I.lastElement()); // remove the last element included...
                I.addElement(fMin); // adds the feature with lowest entropy
                //concidional para armazenar o melhor conjunto de cada tamanho <= maxfeatures.
                if (bestset[I.size()] == null) {
                    bestset[I.size()] = new Vector();
                    for (int s = 0; s < I.size(); s++) {
                        bestset[I.size()].add(I.elementAt(s));
                    }
                    bestentropy[I.size()] = hMin;
                } else {
                    BestSet(bestset[I.size()], bestentropy, I, hMin);
                }
                //heuristica para para execucao, no caso de nao melhorar os resultados.
                int repeticoes = 0;
                for (int be = 1; be < bestentropy.length - 1; be++) {
                    if (bestentropy[be] < 1 && Math.abs(bestentropy[be] - bestentropy[be + 1]) < deltae) {
                        repeticoes++;
                    }
                }
                if (repeticoes > 1) {
                    break;
                }
                boolean again = true;
                while (I.size() > 2 && again) {
                    //inicio do passo 2 e 3 do sffs: exclusao condicional e
                    //continuacao da exclusao condicional, enquanto os conjuntos
                    //selecionados forem melhores que os seus predecessores
                    int combinations = I.size();
                    float le = 1; //melhor entropia encontrada com as exclusoes
                    int lmf = -1; //caracteristica menos importante
                    for (int comb = 0; comb < combinations; comb++) {
                        Vector xk = new Vector();
                        //monta o vetor com -1 caracteristica e testa todas as combinacoes.
                        for (int nc = 0; nc < combinations; nc++) {
                            if (nc != comb) {
                                xk.add(I.elementAt(nc));
                            }
                        }
                        float nh = Criteria.MCE_COD_Gensips2011(type, alpha, beta, n, c, xk, A, q, goldstandard, targetindex, weight);
                        //BestSet(bestset[xk.size()], bestentropy, xk, nh[0]);
                        //armazena a melhor solucao e qual foi a caracteristica excluida.
                        if (nh < le) {
                            le = nh;
                            lmf = (Integer) I.elementAt(comb);
                        }
                    }
                    if (le < bestentropy[I.size() - 1] && lmf != fMin) {
                        I.remove((Integer) lmf);
                        BestSet(bestset[I.size()], bestentropy, I, le);
                        //codigo para armazenar os melhores resultados.
                        InsertinResultList(I, le);
                        again = true;
                        fMin = -1;//a verificacao entre lmf e fMin so vale para
                        //o passo 2, logo eh verificado apenas na primeira passagem.
                        //houve reducao de entropia, atualiza o vetor e empates.
                        ties[I.size()].clear();
                        tiesentropy[I.size()] = le;
                        //adiciona o conjunto empatado no vetor.
                        Vector titem = new Vector();
                        for (int cc = 0; cc < I.size(); cc++) {
                            titem.add(I.get(cc));
                        }
                        ties[I.size()].add(titem);
                    } else {
                        again = false;
                    }
                }
                if (hMin <= deltae) {
                    //achou o minimo global e seus empates (se houver).
                    break;
                }
            } else {
                // if the entropy of the subspace with dimension "dim" is greater
                // than that of the subspace included before (dim - 1)
                // subspace obtained before (dim - 1)
                I.remove(I.lastElement()); // it is time to stop and return the
                break;
            }
        }
        Minimal_Gensips2011(maxfeatures, 0.01f, goldstandard, targetindex, weight);//returns the feature set with minimal result.
    }    

    // runs the SFFS algorithm.
    //A stack was implemented to expand all predictors set tied.
    public synchronized void runSFFS_stack(
            int maxfeatures,
            int targetindex,
            int originaltargetindex,
            AGN recovered,//recovered
            AGN original,//used for simulations only
            int stacksize,//used to keep the tie values on the next execution
            //is also used to keep the higher criterion function values,
            //normaly envolved in XOR relations.
            String pathimp,
            String resnroots) {
        float maxentropy = 1;
        if (Math.abs(1 - q) > deltae) {
            maxentropy = (float) ((1 - c * Math.pow((float) 1 / c, q)) / (q - 1));
        }
        float impthresholdpredictor = 0.7f * maxentropy;
        float[] hMax = new float[stacksize];
        int[] piMax = new int[stacksize];
        if (maxfeatures >= columns) {
            maxfeatures = columns - 1;
        }
        Inicialize(maxfeatures + 1);//Inicializa os atributos usados para armazenar os conjuntos e entropias empatadas.
        //IMPLEMENTAR PILHA PARA TODOS OS PREDITORES EMPATADOS COM TAMANHO DO CONJUNTO == I.size()-1
        //TAMBEM SEREM CONSIDERADOS PELO ALGORITMO SFFS COMO POSSIVEIS RESPOSTAS == CRESCIMENTO DA REDE.
        if (exestack.isEmpty()) {
            Vector init = new Vector();
            init.add(-1);
            exestack.add(init);
        }
        while (!exestack.isEmpty() && ((Vector) exestack.get(0)).size() <= maxfeatures) {
            float hMin = 1.1f;
            int fMin = -1;
            float H = 1.1f;
            //recover a tied predictor set
            I = (Vector) exestack.remove(0);
            expandedestack.add(I.clone());
            System.out.print("\nExpanded tied predictors: ");
            IOFile.PrintPredictors(I, -1, targetindex);
            for (int f = 0; f < columns - 1; f++) {
                // for each feature
                if (recovered != null) {
                    int predictorindex = f;
                    if (predictorindex >= targetindex) {
                        predictorindex++;
                    }
                    //nao considera os controles do array.
                    if (recovered.getGenes()[predictorindex].isControl()) {
                        continue;
                    }
                }
                if (I.contains(f)) {// || !CNMeasurements.hasVariation(A, f)) {
                    continue; // the feature was included in the subspace already
                    // substituting the last element included by a new feature for
                    // evaluation with the subspace included previously
                }
                I.remove(I.lastElement());
                I.addElement(f);
                // calculating the mean conditional entropy for the current
                // subspace
                //DEBUG
                //if (targetindex == 6 && (f == 9 || f == 13)) {
                //    System.out.println("debug-predictor");
                //}
                //FIM-DEBUG
                H = Criteria.MCE_COD(type, alpha, beta, n, c, I, A, q);
                //System.out.println(I.size()+" "+H);
                if (H < hMin) {
                    //if the new entropy is the smallest of all
                    // features previously evaluated with the subspace
                    // previously included, sets the new entropy as the
                    // lowest and the new feature as canditate to be
                    // included in the final subspace
                    fMin = f;
                    hMin = H;
                    //codigo para armazenar os melhores resultados.
                    InsertinResultList(I, H);
                }

                //ARMAZENA OS CANDIDATOS PREDITORES COM MAIORES ENTROPIAS (IMP GENE)
                //PARA CONSIDERA-LOS NA PROXIMA EXECUCAO DO ALGORITMO.
                if (H >= impthresholdpredictor && I.size() == 1 && H < 1) {
                    int pos = -1;
                    float maxdiff = 0;
                    for (int ii = 0; ii < hMax.length; ii++) {
                        if (H > hMax[ii]) {
                            if (Math.abs(hMax[ii] - H) > maxdiff) {
                                maxdiff = Math.abs(hMax[ii] - H);
                                pos = ii;
                            }
                        }
                    }
                    if (pos >= 0) {
                        hMax[pos] = H;
                        piMax[pos] = (Integer) I.get(0);
                    }
                }
                //se houve empate ou reducao de entropia
                //os empates armazenam tambem a resposta do algoritmo e nao soh os empates.
                if (Math.abs(H - hMin) < deltae) {
                    if (H < tiesentropy[I.size()]) {
                        //houve reducao de entropia.
                        ties[I.size()].clear();
                        tiesentropy[I.size()] = H;
                    }
                    if (Math.abs(H - tiesentropy[I.size()]) < deltae) {
                        //adiciona o conjunto empatado no vetor se ainda nao foi inserida sua combinacao de preditores.
                        Vector titem = new Vector();
                        for (int cc = 0; cc < I.size(); cc++) {
                            titem.add(I.get(cc));
                        }
                        if (!ContainPredictorSet(ties[I.size()], titem)) {
                            ties[I.size()].add(titem);
                        }
                    }
                }
            }
            // if the entropy of the subspace with dimension
            // "dim" is smaller than that of the subspace included before
            // (dim - 1)
            if (I.size() <= maxfeatures && fMin != -1) {
                I.remove(I.lastElement()); // remove the last element included...
                I.addElement(fMin); // adds the feature with lowest entropy
                //RETIRADO PQ O METODO RESPONDE COM APENAS UM CONJUNTO DE PREDITORES
                //SE FOR NECESSARIO TER N CONJUNTOS RESPOSTA, ENTAO DESCOMENTE
                //AS LINHAS ABAIXO.
                //concidional para armazenar o melhor conjunto de cada tamanho <= maxfeatures.
                //if (bestset[I.size()] == null) {
                //    bestset[I.size()] = new Vector();
                //    for (int s = 0; s < I.size(); s++) {
                //        bestset[I.size()].add(I.elementAt(s));
                //    }
                //    bestentropy[I.size()] = hMin;
                //} else {
                //atualiza os melhores resultados obtidos no vetor bestset (separados pelo tamanho do conjunto).
                BestSet(bestset[I.size()], bestentropy, I, hMin);
                //}
                //heuristica para para execucao, no caso de nao melhorar os resultados.
                int repeticoes = 0;
                for (int be = 1; be < bestentropy.length - 1; be++) {
                    if (bestentropy[be] < 1 && Math.abs(bestentropy[be] - bestentropy[be + 1]) < deltae) {
                        repeticoes++;
                    }
                }
                if (repeticoes > 1) {
                    break;
                }
                boolean again = true;
                while (I.size() > 2 && again) {
                    //inicio do passo 2 e 3 do sffs: exclusao condicional e
                    //continuacao da exclusao condicional, enquanto os conjuntos
                    //selecionados forem melhores que os seus predecessores
                    int combinations = I.size();
                    float le = 1; //melhor entropia encontrada com as exclusoes
                    int lmf = -1; //caracteristica menos importante
                    for (int comb = 0; comb < combinations; comb++) {
                        Vector xk = new Vector();
                        //monta o vetor com -1 caracteristica e testa todas as combinacoes.
                        for (int nc = 0; nc < combinations; nc++) {
                            if (nc != comb) {
                                xk.add(I.elementAt(nc));
                            }
                        }
                        float nh = Criteria.MCE_COD(type, alpha, beta, n, c, xk, A, q);
                        //BestSet(bestset[xk.size()], bestentropy, xk, nh[0]);
                        //armazena a melhor solucao e qual foi a caracteristica excluida.
                        if (nh < le) {
                            le = nh;
                            lmf = (Integer) I.elementAt(comb);
                        }
                    }
                    if (le < bestentropy[I.size() - 1] && lmf != fMin) {
                        I.remove((Integer) lmf);
                        BestSet(bestset[I.size()], bestentropy, I, le);
                        //codigo para armazenar os melhores resultados.
                        InsertinResultList(I, le);
                        again = true;
                        fMin = -1;//a verificacao entre lmf e fMin so vale para
                        //o passo 2, logo eh verificado apenas na primeira passagem.
                        //houve reducao de entropia, atualiza o vetor e empates.
                        ties[I.size()].clear();
                        tiesentropy[I.size()] = le;
                        //adiciona o conjunto empatado no vetor.
                        Vector titem = new Vector();
                        for (int cc = 0; cc < I.size(); cc++) {
                            titem.add(I.get(cc));
                        }
                        ties[I.size()].add(titem);
                    } else {
                        again = false;
                    }
                }
                //ATUALIZA A PILHA COM OS EMPATES (t == 0) foi o primeiro grupo
                //de preditores jah expandido pelo SFFS.
                //DEBUG
                System.out.println("Preditores escolhidos com cardinalidade == " + I.size());
                System.out.print("Preditores escolhidos: ");
                IOFile.PrintPredictors(I, hMin, targetindex);
                //System.out.println("Entropia == " + hMin);
                //FIM-DEBUG
                System.out.println("Preditores empatados empilhados:");
                int contp = 0;
                //POR UMA QUESTAO DE DESEMPENHO, INSERE APENAS OS DOIS PRIMEIROS EMPATES NA PILHA.
                for (int t = 0; t < ties[I.size()].size() && contp < 1; t++) {
                    Vector predictorset = (Vector) ((Vector) ties[I.size()].get(t)).clone();
                    predictorset.addElement(-1);
                    if (!ContainPredictorSet(exestack, predictorset)//a combinacao de preditores nao esta empilhada.
                            && !ContainPredictorSet(expandedestack, predictorset)) {//a combinacao de preditores nao foi expandida.
                        //&& predictorset.size() <= 2) {//nao atingiu o limite na cardinalidade do conjunto de preditores.
                        exestack.add(predictorset);
                        IOFile.PrintPredictors(predictorset, tiesentropy[I.size()], targetindex);
                        contp++;
                    }
                }
                for (int t = 0; t < hMax.length && I.size() == 1; t++) {
                    if (hMax[t] > 0) {
                        Vector IMPpredictorset = new Vector();
                        IMPpredictorset.addElement(piMax[t]);
                        IMPpredictorset.addElement(-1);
                        if (!ContainPredictorSet(exestack, IMPpredictorset)//a combinacao de preditores nao esta empilhada.
                                && !ContainPredictorSet(expandedestack, IMPpredictorset)) {//a combinacao de preditores nao foi expandida.
                            //&& IMPpredictorset.size() <= 2) {//nao atingiu o limite na cardinalidade do conjunto de preditores.
                            exestack.add(IMPpredictorset);
                            IOFile.PrintPredictors(IMPpredictorset, hMax[t], targetindex);
                        }
                    }
                }
                System.out.println("# empilhados == " + contp);
                System.out.println("Tamanho da pilha == " + exestack.size());

                if (hMin <= deltae) {
                    //achou o minimo global e seus empates (se houver).
                    break;
                } else if (original != null && resnroots != null && I.size() == 1) {
                    //armazena o tamanho da pilha utilizada, apenas se ela for utilizada nas proximas iteracoes.
                    IOFile.WriteStackSize(original, resnroots, exestack.size(), targetindex);
                }
            } else {
                // if the entropy of the subspace with dimension "dim" is greater
                // than that of the subspace included before (dim - 1)
                // subspace obtained before (dim - 1)
                I.remove(I.lastElement()); // it is time to stop and return the
                break;
            }
        }
        System.out.println("Numero de conjuntos de preditores expandidos == "
                + expandedestack.size());
        IOFile.PrintVectorofPredictors(expandedestack, targetindex);

        System.out.print("\nTarget index == " + targetindex + "\n");
        PrintOriginalPredictors(original, targetindex);
        System.out.print("\n");
        Minimal(maxfeatures, 0.05f, targetindex);//returns the feature set with minimal result.
        //if (pathimp != null) {
        //    IOFile.WriteCP(pathimp, I, hGlobal, targetindex);
        //}

        //verifica se o predictor escolhido esta ou nao no conjunto resposta.
        //verificacao do valor do threshold e do numero de piores raizes para encontrar os preditores IMP.
        if (pathimp != null && I.size() == 1) {
            int predictorindex = (Integer) I.get(0);
            if (predictorindex >= targetindex) {
                predictorindex++;
            }
            if (original.getGenes()[targetindex].getPredictors().size() > 0
                    && !original.getGenes()[targetindex].getPredictors().contains(predictorindex)) {
                //o preditor eh um true positivo, eh verificado sua porta logica e o valor de entropia.
                //IOFile.WriteIMP(original, pathimp, targetindex, predictorindex, hGlobal, 99);
                int maxpi = -1;
                float maxh = -1;
                int maxibf = -1;
                //o preditor escolhido nao faz parte do resultado do algoritmo.
                if (original.getGenes()[targetindex].getBooleanfunctions() != null
                        && !original.getGenes()[targetindex].getBooleanfunctions().isEmpty()) {
                    Vector vbf = (Vector) original.getGenes()[targetindex].getBooleanfunctions().get(0);

                    for (int p = 0; p < original.getGenes()[targetindex].getPredictors().size(); p++) {
                        int pio = (Integer) original.getGenes()[targetindex].getPredictors().get(p);
                        int pi = pio;
                        //preditor
                        if (p < vbf.size()) {
                            int ibf = (Integer) vbf.get(p);
                            //funcao booleana
                            Vector nI = new Vector();
                            if (pio >= targetindex) {
                                pi--;
                            }
                            nI.add(pi);

                            float hh = Criteria.MCE_COD(type, alpha, beta, n, c, nI, A, q);
                            if (hh > maxh) {
                                maxh = hh;
                                maxpi = pio;
                                maxibf = ibf;
                            }
                        }
                    }
                    //escreve no arquivo apenas o valor maximo obtido por um dos preditores...
                    if (maxh > 0) {
                        IOFile.WriteIMP(original, pathimp, targetindex, maxpi, maxh, maxibf);
                    }
                }
            }
        }
    }

    
    // runs the SFFS algorithm.
    //A stack was implemented to expand all predictors set tied.
    public synchronized void runSFFS_stack_Gensips2011(
            int maxfeatures,
            int targetindex,
            int originaltargetindex,
            AGN recovered,//recovered
            AGN goldstandard,//used for simulations only
            int stacksize,//used to keep the tie values on the next execution
            //is also used to keep the higher criterion function values,
            //normaly envolved in XOR relations.
            String pathimp,
            String resnroots,
            float weight) {
        float maxentropy = 1;
        if (Math.abs(1 - q) > deltae) {
            maxentropy = (float) ((1 - c * Math.pow((float) 1 / c, q)) / (q - 1));
        }
        float impthresholdpredictor = 0.7f * maxentropy;
        float[] hMax = new float[stacksize];
        int[] piMax = new int[stacksize];
        if (maxfeatures >= columns) {
            maxfeatures = columns - 1;
        }
        Inicialize(maxfeatures + 1);//Inicializa os atributos usados para armazenar os conjuntos e entropias empatadas.
        //IMPLEMENTAR PILHA PARA TODOS OS PREDITORES EMPATADOS COM TAMANHO DO CONJUNTO == I.size()-1
        //TAMBEM SEREM CONSIDERADOS PELO ALGORITMO SFFS COMO POSSIVEIS RESPOSTAS == CRESCIMENTO DA REDE.
        if (exestack.isEmpty()) {
            Vector init = new Vector();
            init.add(-1);
            exestack.add(init);
        }
        while (!exestack.isEmpty() && ((Vector) exestack.get(0)).size() <= maxfeatures) {
            float hMin = 1.1f;
            int fMin = -1;
            float H = 1.1f;
            //recover a tied predictor set
            I = (Vector) exestack.remove(0);
            expandedestack.add(I.clone());
            System.out.print("\nExpanded tied predictors: ");
            IOFile.PrintPredictors(I, -1, targetindex);
            for (int f = 0; f < columns - 1; f++) {
                // for each feature
                if (recovered != null) {
                    int predictorindex = f;
                    if (predictorindex >= targetindex) {
                        predictorindex++;
                    }
                    //nao considera os controles do array.
                    if (recovered.getGenes()[predictorindex].isControl()) {
                        continue;
                    }
                }
                if (I.contains(f)) {// || !CNMeasurements.hasVariation(A, f)) {
                    continue; // the feature was included in the subspace already
                    // substituting the last element included by a new feature for
                    // evaluation with the subspace included previously
                }
                I.remove(I.lastElement());
                I.addElement(f);
                // calculating the mean conditional entropy for the current
                // subspace
                //DEBUG
                //if (targetindex == 6 && (f == 9 || f == 13)) {
                //    System.out.println("debug-predictor");
                //}
                //FIM-DEBUG
                H = Criteria.MCE_COD_Gensips2011(type, alpha, beta, n, c, I, A, q, goldstandard, targetindex, weight);
                //System.out.println(I.size()+" "+H);
                if (H < hMin) {
                    //if the new entropy is the smallest of all
                    // features previously evaluated with the subspace
                    // previously included, sets the new entropy as the
                    // lowest and the new feature as canditate to be
                    // included in the final subspace
                    fMin = f;
                    hMin = H;
                    //codigo para armazenar os melhores resultados.
                    InsertinResultList(I, H);
                }

                //ARMAZENA OS CANDIDATOS PREDITORES COM MAIORES ENTROPIAS (IMP GENE)
                //PARA CONSIDERA-LOS NA PROXIMA EXECUCAO DO ALGORITMO.
                if (H >= impthresholdpredictor && I.size() == 1 && H < 1) {
                    int pos = -1;
                    float maxdiff = 0;
                    for (int ii = 0; ii < hMax.length; ii++) {
                        if (H > hMax[ii]) {
                            if (Math.abs(hMax[ii] - H) > maxdiff) {
                                maxdiff = Math.abs(hMax[ii] - H);
                                pos = ii;
                            }
                        }
                    }
                    if (pos >= 0) {
                        hMax[pos] = H;
                        piMax[pos] = (Integer) I.get(0);
                    }
                }
                //se houve empate ou reducao de entropia
                //os empates armazenam tambem a resposta do algoritmo e nao soh os empates.
                if (Math.abs(H - hMin) < deltae) {
                    if (H < tiesentropy[I.size()]) {
                        //houve reducao de entropia.
                        ties[I.size()].clear();
                        tiesentropy[I.size()] = H;
                    }
                    if (Math.abs(H - tiesentropy[I.size()]) < deltae) {
                        //adiciona o conjunto empatado no vetor se ainda nao foi inserida sua combinacao de preditores.
                        Vector titem = new Vector();
                        for (int cc = 0; cc < I.size(); cc++) {
                            titem.add(I.get(cc));
                        }
                        if (!ContainPredictorSet(ties[I.size()], titem)) {
                            ties[I.size()].add(titem);
                        }
                    }
                }
            }
            // if the entropy of the subspace with dimension
            // "dim" is smaller than that of the subspace included before
            // (dim - 1)
            if (I.size() <= maxfeatures && fMin != -1) {
                I.remove(I.lastElement()); // remove the last element included...
                I.addElement(fMin); // adds the feature with lowest entropy
                //RETIRADO PQ O METODO RESPONDE COM APENAS UM CONJUNTO DE PREDITORES
                //SE FOR NECESSARIO TER N CONJUNTOS RESPOSTA, ENTAO DESCOMENTE
                //AS LINHAS ABAIXO.
                //concidional para armazenar o melhor conjunto de cada tamanho <= maxfeatures.
                //if (bestset[I.size()] == null) {
                //    bestset[I.size()] = new Vector();
                //    for (int s = 0; s < I.size(); s++) {
                //        bestset[I.size()].add(I.elementAt(s));
                //    }
                //    bestentropy[I.size()] = hMin;
                //} else {
                //atualiza os melhores resultados obtidos no vetor bestset (separados pelo tamanho do conjunto).
                BestSet(bestset[I.size()], bestentropy, I, hMin);
                //}
                //heuristica para para execucao, no caso de nao melhorar os resultados.
                int repeticoes = 0;
                for (int be = 1; be < bestentropy.length - 1; be++) {
                    if (bestentropy[be] < 1 && Math.abs(bestentropy[be] - bestentropy[be + 1]) < deltae) {
                        repeticoes++;
                    }
                }
                if (repeticoes > 1) {
                    break;
                }
                boolean again = true;
                while (I.size() > 2 && again) {
                    //inicio do passo 2 e 3 do sffs: exclusao condicional e
                    //continuacao da exclusao condicional, enquanto os conjuntos
                    //selecionados forem melhores que os seus predecessores
                    int combinations = I.size();
                    float le = 1; //melhor entropia encontrada com as exclusoes
                    int lmf = -1; //caracteristica menos importante
                    for (int comb = 0; comb < combinations; comb++) {
                        Vector xk = new Vector();
                        //monta o vetor com -1 caracteristica e testa todas as combinacoes.
                        for (int nc = 0; nc < combinations; nc++) {
                            if (nc != comb) {
                                xk.add(I.elementAt(nc));
                            }
                        }
                        float nh = Criteria.MCE_COD_Gensips2011(type, alpha, beta, n, c, xk, A, q, goldstandard, targetindex, weight);
                        //BestSet(bestset[xk.size()], bestentropy, xk, nh[0]);
                        //armazena a melhor solucao e qual foi a caracteristica excluida.
                        if (nh < le) {
                            le = nh;
                            lmf = (Integer) I.elementAt(comb);
                        }
                    }
                    if (le < bestentropy[I.size() - 1] && lmf != fMin) {
                        I.remove((Integer) lmf);
                        BestSet(bestset[I.size()], bestentropy, I, le);
                        //codigo para armazenar os melhores resultados.
                        InsertinResultList(I, le);
                        again = true;
                        fMin = -1;//a verificacao entre lmf e fMin so vale para
                        //o passo 2, logo eh verificado apenas na primeira passagem.
                        //houve reducao de entropia, atualiza o vetor e empates.
                        ties[I.size()].clear();
                        tiesentropy[I.size()] = le;
                        //adiciona o conjunto empatado no vetor.
                        Vector titem = new Vector();
                        for (int cc = 0; cc < I.size(); cc++) {
                            titem.add(I.get(cc));
                        }
                        ties[I.size()].add(titem);
                    } else {
                        again = false;
                    }
                }
                //ATUALIZA A PILHA COM OS EMPATES (t == 0) foi o primeiro grupo
                //de preditores jah expandido pelo SFFS.
                //DEBUG
                System.out.println("Preditores escolhidos com cardinalidade == " + I.size());
                System.out.print("Preditores escolhidos: ");
                IOFile.PrintPredictors(I, hMin, targetindex);
                //System.out.println("Entropia == " + hMin);
                //FIM-DEBUG
                System.out.println("Preditores empatados empilhados:");
                int contp = 0;
                //POR UMA QUESTAO DE DESEMPENHO, INSERE APENAS OS DOIS PRIMEIROS EMPATES NA PILHA.
                for (int t = 0; t < ties[I.size()].size() && contp < 1; t++) {
                    Vector predictorset = (Vector) ((Vector) ties[I.size()].get(t)).clone();
                    predictorset.addElement(-1);
                    if (!ContainPredictorSet(exestack, predictorset)//a combinacao de preditores nao esta empilhada.
                            && !ContainPredictorSet(expandedestack, predictorset)) {//a combinacao de preditores nao foi expandida.
                        //&& predictorset.size() <= 2) {//nao atingiu o limite na cardinalidade do conjunto de preditores.
                        exestack.add(predictorset);
                        IOFile.PrintPredictors(predictorset, tiesentropy[I.size()], targetindex);
                        contp++;
                    }
                }
                for (int t = 0; t < hMax.length && I.size() == 1; t++) {
                    if (hMax[t] > 0) {
                        Vector IMPpredictorset = new Vector();
                        IMPpredictorset.addElement(piMax[t]);
                        IMPpredictorset.addElement(-1);
                        if (!ContainPredictorSet(exestack, IMPpredictorset)//a combinacao de preditores nao esta empilhada.
                                && !ContainPredictorSet(expandedestack, IMPpredictorset)) {//a combinacao de preditores nao foi expandida.
                            //&& IMPpredictorset.size() <= 2) {//nao atingiu o limite na cardinalidade do conjunto de preditores.
                            exestack.add(IMPpredictorset);
                            IOFile.PrintPredictors(IMPpredictorset, hMax[t], targetindex);
                        }
                    }
                }
                System.out.println("# empilhados == " + contp);
                System.out.println("Tamanho da pilha == " + exestack.size());

                if (hMin <= deltae) {
                    //achou o minimo global e seus empates (se houver).
                    break;
                } else if (goldstandard != null && resnroots != null && I.size() == 1) {
                    //armazena o tamanho da pilha utilizada, apenas se ela for utilizada nas proximas iteracoes.
                    IOFile.WriteStackSize(goldstandard, resnroots, exestack.size(), targetindex);
                }
            } else {
                // if the entropy of the subspace with dimension "dim" is greater
                // than that of the subspace included before (dim - 1)
                // subspace obtained before (dim - 1)
                I.remove(I.lastElement()); // it is time to stop and return the
                break;
            }
        }
        System.out.println("Numero de conjuntos de preditores expandidos == "
                + expandedestack.size());
        IOFile.PrintVectorofPredictors(expandedestack, targetindex);

        System.out.print("\nTarget index == " + targetindex + "\n");
        PrintOriginalPredictors(goldstandard, targetindex);
        System.out.print("\n");
        Minimal_Gensips2011(maxfeatures, 0.05f, goldstandard, targetindex, weight);//returns the feature set with minimal result.
        //if (pathimp != null) {
        //    IOFile.WriteCP(pathimp, I, hGlobal, targetindex);
        //}

        //verifica se o predictor escolhido esta ou nao no conjunto resposta.
        //verificacao do valor do threshold e do numero de piores raizes para encontrar os preditores IMP.
        if (pathimp != null && I.size() == 1) {
            int predictorindex = (Integer) I.get(0);
            if (predictorindex >= targetindex) {
                predictorindex++;
            }
            if (goldstandard.getGenes()[targetindex].getPredictors().size() > 0
                    && !goldstandard.getGenes()[targetindex].getPredictors().contains(predictorindex)) {
                //o preditor eh um true positivo, eh verificado sua porta logica e o valor de entropia.
                //IOFile.WriteIMP(original, pathimp, targetindex, predictorindex, hGlobal, 99);
                int maxpi = -1;
                float maxh = -1;
                int maxibf = -1;
                //o preditor escolhido nao faz parte do resultado do algoritmo.
                if (goldstandard.getGenes()[targetindex].getBooleanfunctions() != null
                        && !goldstandard.getGenes()[targetindex].getBooleanfunctions().isEmpty()) {
                    Vector vbf = (Vector) goldstandard.getGenes()[targetindex].getBooleanfunctions().get(0);

                    for (int p = 0; p < goldstandard.getGenes()[targetindex].getPredictors().size(); p++) {
                        int pio = (Integer) goldstandard.getGenes()[targetindex].getPredictors().get(p);
                        int pi = pio;
                        //preditor
                        if (p < vbf.size()) {
                            int ibf = (Integer) vbf.get(p);
                            //funcao booleana
                            Vector nI = new Vector();
                            if (pio >= targetindex) {
                                pi--;
                            }
                            nI.add(pi);

                            float hh = Criteria.MCE_COD_Gensips2011(type, alpha, beta, n, c, nI, A, q, goldstandard, targetindex, weight);
                            if (hh > maxh) {
                                maxh = hh;
                                maxpi = pio;
                                maxibf = ibf;
                            }
                        }
                    }
                    //escreve no arquivo apenas o valor maximo obtido por um dos preditores...
                    if (maxh > 0) {
                        IOFile.WriteIMP(goldstandard, pathimp, targetindex, maxpi, maxh, maxibf);
                    }
                }
            }
        }
    }    
    
    public static void PrintOriginalPredictors(AGN original, int targetindex) {
        //DEBUG
        if (original != null) {
            System.out.print("Original Predictors: ");
            if (original.getGenes()[targetindex].getBooleanfunctions() != null
                    && !original.getGenes()[targetindex].getBooleanfunctions().isEmpty()) {
                Vector vbf = (Vector) original.getGenes()[targetindex].getBooleanfunctions().get(0);
                for (int p = 0; p < original.getGenes()[targetindex].getPredictors().size(); p++) {
                    int pi = (Integer) original.getGenes()[targetindex].getPredictors().get(p);
                    System.out.print(pi);
                    if (p < vbf.size()) {
                        int ibf = (Integer) vbf.get(p);
                        System.out.print(" (" + Topologies.BooleanFunctions[ibf] + ") ");
                    }
                }
            } else {
                System.out.print("target nao possui preditores.");
            }
        }
        //END-DEBUG
    }

    public boolean ContainPredictorSet(Vector stack, Vector predictorset) {
        for (int i = 0; i < stack.size(); i++) {
            Vector stackset = (Vector) stack.get(i);
            int count = 0;
            for (int j = 0; j < predictorset.size(); j++) {
                int predictor = (Integer) predictorset.get(j);
                if (stackset.contains(predictor)) {
                    count++;
                }
            }
            if (count == predictorset.size()) {
                return true;
            }
        }
        return false;
    }

    public void runExhaustive(int it, int f, Vector tempI) {
        if (itmax == 1) {
            for (int i = 0; i < columns - 1; i++) {
                tempI.add(i);
                float H = Criteria.MCE_COD(type, alpha, beta, n, c, tempI, A, q);
                if (H < hGlobal) {
                    I = new Vector(tempI);
                    hGlobal = H;
                    //codigo para armazenar os melhores resultados.
                    InsertinResultList(tempI, H);
                }
                tempI.remove(tempI.lastElement());
            }
            return;
        }
        tempI.add(f);
        if (it >= itmax - 1) {
            float H = Criteria.MCE_COD(type, alpha, beta, n, c, tempI, A, q);
            if (H < hGlobal) {
                I = new Vector(tempI);
                hGlobal = H;
                //System.out.println(f+" "+hGlobal);
                //codigo para armazenar os melhores resultados.
                InsertinResultList(tempI, H);
            }
            return;
        }
        for (int i = f + 1; i < columns - 1; i++) {
            runExhaustive(it + 1, i, tempI);
            tempI.setSize(tempI.size() - 1);
        }
        if (it == 0 && f < columns - itmax) {
            tempI.removeAllElements();
            runExhaustive(0, f + 1, tempI);
        }
    }
}
