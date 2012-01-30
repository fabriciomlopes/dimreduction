/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agn;

import java.util.Random;
import utilities.MathRoutines;

/**
 * @author Fabricio
 */
public class Simulation {
    public static Random [] rbfs = null;
    
    public static void InitializeRandomGenerator(AGN agn){
        rbfs = new Random[agn.getNrgenes()];
        for (int i = 0; i < agn.getNrgenes(); i++){
            rbfs[i] = new Random(System.nanoTime());//definicao de geradores aleatorios de numeros, um para cada gene da rede.
        }
    }

    public static int ApplyBooleanFunction(boolean[] pv, int[] booleanf) {
        boolean booleanv = pv[0];
        for (int i = 0; i < booleanf.length; i++) {
            if (booleanf[i] == 0 || booleanf[i] == 9) {
                if (pv[i] && pv[i + 1]) {//0  == A AND B
                    booleanv = true;
                } else {
                    booleanv = false;
                }
                if (booleanf[i] == 9)//9  == A NAND B
                {
                    booleanv = !booleanv;
                }
            } else if (booleanf[i] == 1) {
                if (pv[i] && !pv[i + 1]) {//1  == A AND NOT B
                    booleanv = true;
                } else {
                    booleanv = false;
                }
            } else if (booleanf[i] == 2) {
                if (!pv[i] && pv[i + 1]) {//2  == NOT A AND B
                    booleanv = true;
                } else {
                    booleanv = false;
                }
            } else if (booleanf[i] == 4 || booleanf[i] == 5) {
                if (pv[i] || pv[i + 1]) {//4  == A OR B
                    booleanv = true;
                } else {
                    booleanv = false;
                }
                if (booleanf[i] == 5)//5  == A NOR B
                {
                    booleanv = !booleanv;
                }
            } else if (booleanf[i] == 3 || booleanf[i] == 6) {
                if ((pv[i] && !pv[i + 1]) || (!pv[i] && pv[i + 1])) {//3  == A XOR B
                    booleanv = true;
                } else {
                    booleanv = false;
                }
                if (booleanf[i] == 6)//6  == A XNOR B
                {
                    booleanv = !booleanv;
                }
            } else if (booleanf[i] == 7) {
                if (pv[i] || !pv[i + 1]) {//7  == A OR NOT B
                    booleanv = true;
                } else {
                    booleanv = false;
                }
            } else if (booleanf[i] == 8) {
                if (!pv[i] || pv[i + 1]) {//8  == NOT A OR B
                    booleanv = true;
                } else {
                    booleanv = false;
                }
            } else if (booleanf[i] == 10) {//10 == CONTRADICTION (always false)
                booleanv = false;

            } else if (booleanf[i] == 11) {//11 == A
                booleanv = pv[i];
            } else if (booleanf[i] == 12) {//12 == B
                booleanv = pv[i + 1];
            } else if (booleanf[i] == 13) {//13 == NOT B
                booleanv = !pv[i + 1];
            } else if (booleanf[i] == 14) {//14 == NOT A
                booleanv = !pv[i];
            } else if (booleanf[i] == 15) {//15 == TAUTOLOGY (always true)
                booleanv = true;
            } else {
                System.out.println("Error: Unknown Boolean Function " + booleanf[i]);
                System.exit(3);
            }
            //atualiza o vetor para o valor do ultimo preditor [i+1] ser
            //o resultado da funcao Booleana aplicada aos dois preditores
            //anteriores.
            if (pv.length > 1) {
                pv[i + 1] = booleanv;
            }
        }
        if (booleanv) {
            return (1);
        } else {
            return (0);
        }
    }

    public static boolean ApplyBooleanFunction(boolean predictor1,
            boolean predictor2, int booleanfunction) {
        boolean result = predictor1;
        if (booleanfunction == 0 || booleanfunction == 9) {
            if (predictor1 && predictor2) {//0  == A AND B
                result = true;
            } else {
                result = false;
            }
            if (booleanfunction == 9)//9  == A NAND B
            {
                result = !result;
            }
        } else if (booleanfunction == 1) {
            if (predictor1 && !predictor2) {//1  == A AND NOT B
                result = true;
            } else {
                result = false;
            }
        } else if (booleanfunction == 2) {
            if (!predictor1 && predictor2) {//2  == NOT A AND B
                result = true;
            } else {
                result = false;
            }
        } else if (booleanfunction == 4 || booleanfunction == 5) {
            if (predictor1 || predictor2) {//4  == A OR B
                result = true;
            } else {
                result = false;
            }
            if (booleanfunction == 5)//5  == A NOR B
            {
                result = !result;
            }
        } else if (booleanfunction == 3 || booleanfunction == 6) {
            if ((predictor1 && !predictor2) || (!predictor1 && predictor2)) {//3  == A XOR B
                result = true;
            } else {
                result = false;
            }
            if (booleanfunction == 6)//6  == A XNOR B
            {
                result = !result;
            }
        } else if (booleanfunction == 7) {
            if (predictor1 || !predictor2) {//7  == A OR NOT B
                result = true;
            } else {
                result = false;
            }
        } else if (booleanfunction == 8) {
            if (!predictor1 || predictor2) {//8  == NOT A OR B
                result = true;
            } else {
                result = false;
            }
        } else if (booleanfunction == 10) {//10 == CONTRADICTION (always false)
            result = false;
        } else if (booleanfunction == 11) {//11 == A
            result = predictor1;
        } else if (booleanfunction == 12) {//12 == B
            result = predictor2;
        } else if (booleanfunction == 13) {//13 == NOT B
            result = !predictor2;
        } else if (booleanfunction == 14) {//14 == NOT A
            result = !predictor1;
        } else if (booleanfunction == 15) {//15 == TAUTOLOGY (always true)
            result = true;
        } else {
            System.out.println("Error: Unknown Boolean Function " + booleanfunction);
            System.exit(3);
        }
        return (result);
    }

    public static boolean ApplyDeterministicLogicalCircuit(AGN agn, int target, int time,
            float [][] temporalsignal) {
        //se nao existe preditores para o target, ele assume o proprio valor.
        //recupera o valor do target e o transforma em binario.
        boolean targetvalue = MathRoutines.Int2Boolean((int) temporalsignal[target][time]);
        if (agn.getGenes()[target].getPredictors().size() == 1) {
            //existe apenas um preditor para o gene.
            //verifica qual o indice do preditor.
            int ip = (Integer) agn.getGenes()[target].getPredictors().get(0);
            //recupera o seu valor
            int pv = (int) temporalsignal[ip][time];
            //transforma seu valor em binario.
            boolean predictorvalue = MathRoutines.Int2Boolean(pv);
            //verifica qual funcao booleana esta associada ao gene
            int booleanfunction = (Integer) agn.getGenes()[target].getBooleanfunctions().get(0);
            //aplica o valor da funcao booleana ao gene, considerando apenas 1 preditor.
            targetvalue = ApplyBooleanFunction(predictorvalue, false, booleanfunction);
        } else if (agn.getGenes()[target].getPredictors().size() > 1) {
            //existem 2 ou mais gene preditores.
            //o valor do primeiro preditor eh atribuido ao targetvalue
            //para que isto possa se repetir dentro do laco.
            int ip = (Integer) agn.getGenes()[target].getPredictors().get(0);
            int pv = (int) temporalsignal[ip][time];
            targetvalue = MathRoutines.Int2Boolean(pv);
            for (int gp = 1; gp < agn.getGenes()[target].getPredictors().size(); gp++) {
                ip = (Integer) agn.getGenes()[target].getPredictors().get(gp);
                pv = (int) temporalsignal[ip][time];
                boolean predictorvalue = MathRoutines.Int2Boolean(pv);
                int booleanfunction = (Integer) agn.getGenes()[target].getBooleanfunctions().get(gp - 1);
                targetvalue = ApplyBooleanFunction(targetvalue, predictorvalue, booleanfunction);
            }
        }
        return targetvalue;
    }

    public static boolean ApplyProbabilisticLogicalCircuit(AGN agn,
            int target, int time, int [][] temporalsignal) {
        Gene targetgene = agn.getGenes()[target];
        
        //inicializa a geracao de pseudo-aleatorios utilizando a hora do sistema como semente.
        Random rng = new Random(System.nanoTime());

        int cbf = 0;
        if (rbfs == null || agn.getNrgenes() != rbfs.length){
            InitializeRandomGenerator(agn);
        }

        float rn = rbfs[target].nextFloat();
        //escolha do conjunto de funcoes booleanas que sera usado de forma probabilistica.
        for (int bf = 0; bf < targetgene.getBooleanfunctionsprobability().size(); bf++){
            //eh armazenada a soma das probabilidades ate o conjunto de funcoes booleanas.
            //ultimo elemento sempre == 1.
            float pbf = targetgene.getBooleanfunctionsprobability(bf);
            if (rn < pbf){
                cbf = bf;
                break;
            }
        }

        //se nao existe preditores para o target, ele assume o proprio valor.
        //recupera o valor do target e o transforma em binario.
        //boolean targetvalue = MathRoutines.Int2Boolean(temporalsignal[target][time]);
        //ou define um valor aleatorio para o gene.
        boolean targetvalue = MathRoutines.Int2Boolean(rng.nextInt(agn.getQuantization()));
        
        if (targetgene.getPredictors().size() == 1) {
            //existe apenas um preditor para o gene.
            //verifica qual o indice do preditor.
            int ip = (Integer) targetgene.getPredictors().get(0);
            //recupera o seu valor
            int pv = (int) temporalsignal[ip][time];
            //transforma seu valor em binario.
            boolean predictorvalue = MathRoutines.Int2Boolean(pv);
            //verifica qual funcao booleana esta associada ao gene
            int booleanfunction = (Integer) targetgene.getBooleanfunctions(cbf).get(0);
            //aplica o valor da funcao booleana ao gene, considerando apenas 1 preditor.
            targetvalue = ApplyBooleanFunction(predictorvalue, false, booleanfunction);
        } else if (targetgene.getPredictors().size() > 1) {
            //existem 2 ou mais gene preditores.
            //o valor do primeiro preditor eh atribuido ao targetvalue
            //para que isto possa se repetir dentro do laco.
            int ip = (Integer) targetgene.getPredictors().get(0);
            int pv = (int) temporalsignal[ip][time];
            targetvalue = MathRoutines.Int2Boolean(pv);
            for (int gp = 1; gp < targetgene.getPredictors().size(); gp++) {
                ip = (Integer) targetgene.getPredictors().get(gp);
                pv = (int) temporalsignal[ip][time];
                boolean predictorvalue = MathRoutines.Int2Boolean(pv);
                int booleanfunction = (Integer) targetgene.getBooleanfunctions(cbf).get(gp-1);
                targetvalue = ApplyBooleanFunction(targetvalue, predictorvalue, booleanfunction);
            }
        }
        return targetvalue;
    }
}
