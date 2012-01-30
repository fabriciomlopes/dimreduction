/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agn;

import java.util.Random;
import java.util.Vector;

/**
 *
 * @author Fabricio
 */
public class Topologies {
    //usando espaco usado para geracao do modelo geografico.

    public static final int graphheight = 500;
    public static final int graphwidth = 500;
    public static final String[] BooleanFunctions = {
        "A AND B",//0
        "A AND NOT B",//1
        "NOT A AND B",//2
        "A XOR B",//3 //IMP
        "A OR B",//4
        "A NOR B",//5
        "A XNOR B",//6
        "A OR NOT B",//7
        "NOT A OR B",//8
        "A NAND B",//9
        "CONTRADICTION (always false)",//10
        "A",//11
        "B",//12
        "NOT B",//13
        "NOT A",//14
        "TAUTOLOGY (always true)"//15
    };

    public static void RemoveEdges(AGN agn) {
        Random rn = new Random(System.nanoTime());
        for (int it = 0; it < agn.getNrgenes(); it++) {
            Gene target = agn.getGenes()[it];
            for (int ip = 0; ip < target.getPredictors().size();) {
                int indexpredictor = (Integer) target.getPredictors().get(ip);
                Gene predictor = agn.getGenes()[indexpredictor];
                if (rn.nextDouble() > 0.5d) {
                    //remove aresta entre target e predictor.
                    target.removePredictor(indexpredictor);
                    predictor.removeTarget(it);
                } else {
                    ip++;
                }
            }
        }
    }

    //nset = numero de conjuntos de funcoes booleanas que sera criado para cada
    //gene alvo.
    public static void CreateProbabilisticLogicalCircuit(AGN agn,
            int booleansetsize, boolean imp) {
        //inicializa a geracao de pseudo-aleatorios utilizando a hora do sistema como semente.
        Random rnbf = new Random(System.nanoTime());//funcao booleana
        float[] bfp = new float[booleansetsize];
        //define valores iniciais
        if (booleansetsize == 1) {
            bfp[0] = 1;
        } else {
            bfp[0] = 0.98f;
            float increment = (1 - bfp[0]) / (booleansetsize - 1);
            for (int fs = 1; fs < booleansetsize; fs++) {
                bfp[fs] = bfp[fs - 1] + increment;
            }
        }
        for (int target = 0; target < agn.getNrgenes(); target++) {
            //for para o numero de conjunto de funcoes booleanas possivel...PBN
            if (agn.getGenes()[target].getPredictors().size() > 0) {
                for (int bset = 0; bset < booleansetsize; bset++) {
                    //funcoes Booleanas que serao definidas entre cada dois preditores.
                    Vector booleanfunctions = new Vector(agn.getGenes()[target].getPredictors().size() - 1);
                    //atribui uma funcao booleana ao gene alvo, se ele tive apenas um preditor.
                    if (agn.getGenes()[target].getPredictors().size() == 1) {
                        booleanfunctions = new Vector(1);
                        int ch;
                        if (agn.isAllbooleanfunctions()) {
                            //escolhe uma entre 4 possiveis...caso especial.
                            ch = rnbf.nextInt(4);
                        } else {
                            //escolhe uma entre 2 possiveis...caso especial.
                            ch = rnbf.nextInt(2);
                        }
                        //escolha 0 == A, 1 == NOT A, 2 == CONTRADICTION, 3 == TAUTOLOGY
                        if (ch == 0) {
                            booleanfunctions.add(0, 11);
                        } else if (ch == 1) {
                            booleanfunctions.add(0, 14);
                        } else if (ch == 2) {
                            booleanfunctions.add(0, 10);
                        } else {
                            booleanfunctions.add(0, 15);
                        }
                    } else if (agn.getGenes()[target].getPredictors().size() > 1) {
                        if (!imp) {
                            for (int i = 0; i < agn.getGenes()[target].getPredictors().size() - 1; i++) {
                                //definicao da funcao Booleana para cada par de preditores de forma aleatoria.
                                if (agn.isAllbooleanfunctions()) {
                                    booleanfunctions.add(i, rnbf.nextInt(16));
                                } else {
                                    booleanfunctions.add(i, rnbf.nextInt(10));
                                }
                            }
                        } else {
                            //IMP
                            //no caso de gerar apenas relacionamentos IMP entre os genes.
                            //testando inicialmente apenas a porta XOR para
                            for (int i = 0; i < agn.getGenes()[target].getPredictors().size() - 1; i++) {
                                if (i%2 == 0){
                                    booleanfunctions.add(i, 3);//XOR
                                }else{
                                    booleanfunctions.add(i, 0);//AND
                                }
                            }
                        }
                        //0  == A AND B
                        //1  == A AND NOT B
                        //2  == NOT A AND B
                        //3  == A XOR B
                        //4  == A OR B
                        //5  == A NOR B
                        //6  == A XNOR B
                        //7  == A OR NOT B
                        //8  == NOT A OR B
                        //9  == A NAND B
                        //10 == CONTRADICTION (always false)
                        //11 == A
                        //12 == B
                        //13 == NOT B
                        //14 == NOT A
                        //15 == TAUTOLOGY (always true)
                    }
                    agn.getGenes()[target].addBooleanfunctions(bset, booleanfunctions);
                    agn.getGenes()[target].addBooleanfunctionsprobability(bset, bfp[bset]);
                    //A execucao das funcoes booleanas aos preditores e definicao
                    //dos valores aos targets eh realizada na classe Simulation,
                    //metodo ApplyLogicalCircuit() e ApplyBooleanFunction().
                    //Estes metodos sao chamados a partir da classe AGN, metodo CreateTemporalSignal().
                }

            }
        }
    }

    /*
    public static void CreateDeterministicLogicalCircuit(AGN agn) {
    //inicializa a geracao de pseudo-aleatorios utilizando a hora do sistema como semente.
    Random rnbf = new Random(System.nanoTime());//funcao booleana
    for (int target = 0; target < agn.getNrgenes(); target++) {
    if (agn.getGenes()[target].getPredictors().size() > 0) {
    //funcoes Booleanas que serao definidas entre cada dois preditores.
    Vector booleanfunctions = new Vector(agn.getGenes()[target].getPredictors().size() - 1);
    //atribui uma funcao booleana ao gene alvo, se ele tive apenas um preditor.
    if (agn.getGenes()[target].getPredictors().size() == 1) {
    booleanfunctions = new Vector(1);
    int ch;
    if (agn.isAllbooleanfunctions()) {
    //escolhe uma entre 4 possiveis...caso especial.
    ch = rnbf.nextInt(4);
    } else {
    //escolhe uma entre 2 possiveis...caso especial.
    ch = rnbf.nextInt(2);
    }
    //escolha 0 == A, 1 == NOT A, 2 == CONTRADICTION, 3 == TAUTOLOGY
    if (ch == 0) {
    booleanfunctions.add(0, 11);
    } else if (ch == 1) {
    booleanfunctions.add(0, 14);
    } else if (ch == 2) {
    booleanfunctions.add(0, 10);
    } else {
    booleanfunctions.add(0, 15);
    }
    } else {
    for (int i = 0; i < agn.getGenes()[target].getPredictors().size() - 1; i++) {
    //definicao da funcao Booleana para cada par de preditores de forma aleatoria.
    if (agn.isAllbooleanfunctions()) {
    booleanfunctions.add(i, rnbf.nextInt(16));
    } else {
    booleanfunctions.add(i, rnbf.nextInt(10));
    }
    //0  == A AND B
    //1  == A AND NOT B
    //2  == NOT A AND B
    //3  == A XOR B
    //4  == A OR B
    //5  == A NOR B
    //6  == A XNOR B
    //7  == A OR NOT B
    //8  == NOT A OR B
    //9  == A NAND B
    //10 == CONTRADICTION (always false)
    //11 == A
    //12 == B
    //13 == NOT B
    //14 == NOT A
    //15 == TAUTOLOGY (always true)
    }
    }
    agn.getGenes()[target].setBooleanfunctions(booleanfunctions);

    //probabilidade do conjunto de funcoes booleanas ser escolhido.
    Vector vpbf = new Vector();
    vpbf.add(1);
    agn.getGenes()[target].setBooleanfunctionsprobability(vpbf);
    //A execucao das funcoes booleanas aos preditores e definicao
    //dos valores aos targets eh realizada na classe Simulation,
    //metodo ApplyLogicalCircuit() e ApplyBooleanFunction().
    //Estes metodos sao chamados a partir da classe AGN, metodo CreateTemporalSignal().
    }
    }
    }
     *
     */

    /*Metodo para definicao da arquitetura da rede, baseada no modelo de
    Erdos-Renyi (paper Luciano) */
    public static void MakeErdosRenyiTopology(AGN agn) {
        //inicializa a geracao de pseudo-aleatorios utilizando a hora do sistema como semente.
        Random rn = new Random(System.nanoTime());


        double prob = (double) agn.getAvgedges() / (agn.getNrgenes() - 1);
        //continuacao da definicao das regras.
        //laco para sortear os preditores de cada um dos genes target 't'.


        for (int target = 0; target
                < agn.getNrgenes(); target++) {
            //int target = i;//rn.nextInt(nrnodes);
            for (int predictor = 0; predictor
                    < agn.getNrgenes(); predictor++) {
                //assumido que o numero de preditores para o target 't' tem que ser pelo menos 1.
                if (predictor != target && !agn.getGenes()[target].getPredictors().contains(predictor)) {
                    if (rn.nextDouble() < prob) {
                        //cria duas arestas target <--> predictor.
                        agn.getGenes()[target].addPredictor(predictor);
                        agn.getGenes()[predictor].addTarget(target);

                        agn.getGenes()[predictor].addPredictor(target);
                        agn.getGenes()[target].addTarget(predictor);


                    }
                }
            }
            //assumido que o target nao pode ser predito por ele mesmo.
            //nao permitida a auto-predicao e nem a duplicidade de predicao.
        }
    }

    /*Metodo para definicao da arquitetura da rede, baseada no modelo de
    Small-World de Watts-Strogatz (WS) (paper Luciano)
    prob == probability of redistribution edges, used by WS topology.*/
    public static void MakeSmallWorldTopology(AGN agn, float prob) {
        //inicializa a geracao de pseudo-aleatorios utilizando a hora do sistema como semente.
        Random rn = new Random(System.nanoTime());


        float k = (float) agn.getAvgedges();
        //continuacao da definicao das regras.
        //laco para sortear os preditores de cada um dos genes target 't'.

        //ETAPA DETERMINISTICA.
        /*WS Small-World Model Algorithm
        1) Start with order: Begin with a nearest-neighbor
        coupled network consisting of N nodes
        arranged in a ring, where each node i is adjacent to
        its neighbor nodes, i = 1,2,...,K/2, with K being even.
         *
         */


        int countedgestotal = 0;


        int ii = 0;


        int fi = agn.getNrgenes() - 1;


        for (int target = 0; target
                < agn.getNrgenes(); target++) {
            int itarget = target;


            if (target % 2 == 0) {
                itarget = ii;
                ii++;

            } else {
                itarget = fi;
                fi--;

            }


            int jump = 1;


            int countedge = 1;
            //while (agn.getGenes()[target].getTargets().size() < k){


            for (int edge = 1; countedge
                    <= k; edge++) {
                //modulo do numero de genes para assumir forma continua (anel).
                int ipredictor = -1;


                if (edge % 2 == 0) {
                    //aresta para a esquerda do indice do target (targetindex - 1)
                    ipredictor = (agn.getNrgenes() + itarget - jump) % agn.getNrgenes();
                    jump++;

                } else {
                    //aresta para a direita do indice do target (targetindex + 1)
                    ipredictor = (itarget + jump) % agn.getNrgenes();


                } //assumido que o numero de preditores para o target 't' tem que ser pelo menos 1.
                if (ipredictor != itarget && !agn.getGenes()[itarget].getPredictors().contains(ipredictor)) {
                    /*2) Randomization: Randomly rewire each edge
                    of the network with probability p; varying p in such
                    a way that the transition between order (p = 0) and
                    randomness (p = 1) can be closely monitored.*/
                    int newtarget = itarget;


                    int newpredictor = ipredictor;


                    if (rn.nextFloat() < prob) {
                        newtarget = rn.nextInt(agn.getNrgenes());
                        newpredictor = rn.nextInt(agn.getNrgenes());


                        while (newtarget == newpredictor) {
                            newpredictor = rn.nextInt(agn.getNrgenes());


                        }
                    }
                    //problema....
                    if (newpredictor < 0 || newpredictor >= agn.getNrgenes()) {
                        newpredictor = rn.nextInt(agn.getNrgenes());


                    }

                    //cria duas arestas target <--> predictor.
                    agn.getGenes()[newtarget].addPredictor(newpredictor);
                    agn.getGenes()[newpredictor].addTarget(newtarget);

                    agn.getGenes()[newpredictor].addPredictor(newtarget);
                    agn.getGenes()[newtarget].addTarget(newpredictor);
                    countedge++;

                    countedgestotal +=
                            2;


                }//else if (agn.getGenes()[target].getPredictors().size() == agn.getNrgenes()-1){
                //    countedge++;
                //    countedgestotal+=2;
                //}
            }
            //assumido que o target nao pode ser predito por ele mesmo.
            //nao permitida a auto-predicao e nem a duplicidade de predicao.
            //System.out.println("Total of edges = "+countedgestotal);
        }
    }

    /*Metodo para definicao da arquitetura da rede, baseada no modelo de
    Barabasi-Albert (paper Luciano) */
    public static void MakeBarabasiAlbertTopology(AGN agn) {
        int n0 = (int) (0.1 * agn.getNrgenes());//numero de nos para inicializacao da rede.
        //definicao do vetor para contar o nr de vezes que um gene eh selecionado como preditor de outro gene.
        Vector verticeslist = new Vector();
        //inicializa a geracao de pseudo-aleatorios utilizando a hora do sistema como semente.
        Random rn = new Random(System.nanoTime());


        if (agn.getNrgenes() < n0) {
            return;
            //vetor de preditores para os targets.


        }
        double prob = (double) agn.getAvgedges() / (n0 - 1);
        //inicializacao do grafo com m0 nos de forma aleatoria (Erdos-Renyi)


        for (int target = 0; target < n0; target++) {
            //int target = i;//rn.nextInt(nr_genes);
            for (int predictor = 0; predictor < n0; predictor++) {
                //assumido que o numero de preditores para o target 't' tem que ser pelo menos 1.
                //int predictor = j;//rn.nextInt(nr_genes);
                if (predictor != target && !agn.getGenes()[target].getPredictors().contains(predictor)) {
                    if (rn.nextDouble() < prob) {
                        //cria duas arestas target <--> predictor.
                        agn.getGenes()[target].addPredictor(predictor);
                        agn.getGenes()[predictor].addTarget(target);

                        agn.getGenes()[predictor].addPredictor(target);
                        agn.getGenes()[target].addTarget(predictor);

                        //adiciona predictor e target na lista de escolha
                        verticeslist.add(predictor);
                        verticeslist.add(target);
                    }
                }
            }
        }
        for (int target = n0; target < agn.getNrgenes(); target++) {
            while ((agn.getGenes()[target].getPredictors().size()
                    + agn.getGenes()[target].getTargets().size()) < 2 * agn.getAvgedges()) {
                int position = rn.nextInt(verticeslist.size());
                int predictor = (Integer) verticeslist.get(position);

                if (predictor != target && !agn.getGenes()[target].getPredictors().contains(predictor)) {
                    //cria duas arestas target <--> predictor.
                    agn.getGenes()[target].addPredictor(predictor);
                    agn.getGenes()[predictor].addTarget(target);

                    agn.getGenes()[predictor].addPredictor(target);
                    agn.getGenes()[target].addTarget(predictor);

                    //adiciona predictor e target na lista de escolha
                    verticeslist.add(predictor);
                    verticeslist.add(target);
                    //assumido que o target nao pode ser predito por ele mesmo.
                    //nao permitida a auto-predicao e nem a duplicidade de predicao.
                }
            }
        }
    }

    /* Metodo para definicao da arquitetura da rede, baseada no modelo de
    Geografico */
    public static void MakeGeographicalTopology(AGN agn) {//, int maxdistance) {
        //densidade == quantidade de vertices em cada pto do espaco.
        float density = (float) agn.getNrgenes() / (graphwidth * graphheight);
        //area quadrada estimada que serah ocupada por cada vertice da rede.
        //float areaquadradaporvertice = 1.0f / density;
        //distancia maxima para ligacao de arestas entre os vertices.
        //estah multiplicando por 2 pq esta gerando arestas bidirecionais que depois serao removidas.


        float maxdistance = (float) Math.sqrt((agn.getAvgedges() * 2) / (Math.PI * density));

        //sorteio das posicoes que serao ocupadas pelos vertices da rede.
        Random rnx = new Random(System.currentTimeMillis());


        try {
            Thread.sleep(20);


        } catch (InterruptedException error) {
            //
        }
        Random rny = new Random(System.currentTimeMillis());


        for (int g = 0; g
                < agn.getNrgenes(); g++) {
            agn.getGenes()[g].setY((rny.nextFloat() * graphheight));
            agn.getGenes()[g].setX((rnx.nextFloat() * graphwidth));


        }
        for (int target = 0; target
                < agn.getNrgenes(); target++) {
            for (int predictor = 0; predictor
                    < agn.getNrgenes(); predictor++) {
                double distance = CNMeasurements.EuclideanDistance(
                        agn.getGenes()[target],
                        agn.getGenes()[predictor]);


                if (distance <= maxdistance
                        && predictor != target
                        && !agn.getGenes()[target].getPredictors().contains(predictor)) {

                    //cria duas arestas target <--> predictor.
                    agn.getGenes()[target].addPredictor(predictor);
                    agn.getGenes()[predictor].addTarget(target);

                    agn.getGenes()[predictor].addPredictor(target);
                    agn.getGenes()[target].addTarget(predictor);


                }
            }
        }
    }

    /* max_predictors == numero maximo de vezes que um determinado gene pode
    ser preditor de outros genes.
    quantization == quantizacao dos valores assumidos para os features.
    net_architecture == "BA" -> Barabasi-Albert (scale-free)
    net_architecture == "ER" -> Erdos-Renyi (random)
    net_architecture == "GG" -> Geographical
    net_architecture == "WS" -> Small-World de Watts-Strogatz (WS)
    prob == used only for MakeSmallWorldTopology method == probability of
    redistribution edges, used by WS topology.
    imp == generates only intrinsically multivariate prediction (imp) relationships
    among target's predictor.
     */
    public static AGN CreateNetwork(int nrgenes, int signalsize,
            int nrinitializations, float avgedges, int quantization,
            String topology, boolean allbooleanfunctions, float prob,
            boolean imp) {

        //calculate the maximum number of edges per node.
        int maxedges = (nrgenes * (nrgenes - 1)) / 2;

        int networkedges = (int) (avgedges * nrgenes);
        //System.out.println("maxedges = "+maxedges);
        //System.out.println("network edges = "+ networkedges);

        if (networkedges >= maxedges) {
            return null;
        }
        AGN agn = new AGN(topology, nrgenes, signalsize, nrinitializations,
                quantization, avgedges, allbooleanfunctions);
        //define a topologia usando arestas bi-direcionais.

        if (topology.equalsIgnoreCase("BA")) {
            MakeBarabasiAlbertTopology(agn);
        } else if (topology.equalsIgnoreCase("ER")) {
            MakeErdosRenyiTopology(agn);
        } else if (topology.equalsIgnoreCase("GG")) {
            MakeGeographicalTopology(agn);
        } else if (topology.equalsIgnoreCase("WS")) {
            MakeSmallWorldTopology(agn, prob);
        } else {
            //modelo de rede nao especificado.
            return (null);
        }
        float[] iodegree = CNMeasurements.AverageDegrees(agn.getGenes());
        System.out.println("Grau medio = " + iodegree[0]);
        //AGNRoutines.ViewAGN(agn,true,1);

        //remove as arestas com probabilidade de 50%
        RemoveEdges(agn);

        iodegree = CNMeasurements.AverageDegrees(agn.getGenes());
        System.out.println("Novo grau medio = " + iodegree[0]);
        //AGNRoutines.ViewAGN(agn,true,1);
        //soma += iodegree[0];

        //define as funcoes de transicao para cada gene
        //CreateDeterministicLogicalCircuit(agn);
        CreateProbabilisticLogicalCircuit(agn, 3, imp);

        //}
        //System.out.println("Execucoes = " + i);
        //System.out.println("Media = " + soma / i);
        return (agn);
    }
}
