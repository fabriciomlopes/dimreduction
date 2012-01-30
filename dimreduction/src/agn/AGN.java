/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agn;

import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

/**
 * @author Fabricio
 */
public class AGN implements Serializable {
    //tipo de topologia da rede
    private String topology = null;
    //numero de genes da rede
    private int nrgenes;
    //tamanho do sinal temporal que sera gerado
    private int signalsize;
    //numero de inicializacoes (concatenacoes)
    private int nrinitializations;
    //quantizacao assumida pelos genes da rede
    private int quantization;
    //numero medio de arestas incidentes por gene da rede
    private float avgedges;
    //tipo de regra que sera aplicada, 0 == Boolean e 1 == Random
    //private int rulestype;
    //sera usada todas as 2^2^k funcoes booleanas == true, ou apenas conjunto reduzido com 10 funcoes booleanas == false
    private boolean allbooleanfunctions;
    //conjunto de genes da rede
    private Gene[] genes;
    //sinal temporal de expressao
    private float[][] temporalsignal;
    //sinal temporal de expressao normalizado
    private float[][] temporalsignalnormalized;
    //sinal temporal de expressao quantizado
    private int[][] temporalsignalquantized;
    //labels of temporal signal
    private Vector labelstemporalsignal;
    //data analysis
    //keep the mean for each instant of time (column).
    private float [] mean;
    //keep the standard deviation for each instant of time (column).
    private float [] std;
    //keep the lowlevel threshold for each istant of time (column).
    private float [] lowthreshold;
    //keep the hilevel threshold for each istant of time (column).
    private float [] hithreshold;
    //removed elements by filter
    private Vector removedgenes;
    //keep the indexes of the target genes used in the inference process.
    private Vector targets;
    //cores usadas para as classes dos genes da rede.
    private int [] palette;
    //datatype: 1==temporal, 2==steady-state.
    private int datatype;

    //metodo construtor de uma AGN
    public AGN(String topology, int nrgenes, int signalsize, int nrinitializations,
            int quantization, float avgedges, boolean allbooleanfunctions) {
        this.topology = topology;
        this.nrgenes = nrgenes;
        this.signalsize = signalsize;
        this.nrinitializations = nrinitializations;
        this.quantization = quantization;
        this.avgedges = avgedges;
        this.allbooleanfunctions = allbooleanfunctions;
        genes = new Gene[nrgenes];
        Random rn = new Random(System.nanoTime());
        for (int i = 0; i < nrgenes; i++) {
            genes[i] = new Gene();
            genes[i].setName("g"+i);
            genes[i].setProbsetname("g"+i);
            genes[i].setDescription("g"+i);
            genes[i].setIndex(i);
            genes[i].setGeneid(i);
            genes[i].setValue(rn.nextInt(quantization));
        }
        this.temporalsignalquantized = null;
    }

    public AGN(int nrgenes, int signalsize, int quantization, int datatype) {
        this.nrgenes = nrgenes;
        this.signalsize = signalsize;
        this.quantization = quantization;
        this.datatype = datatype;
        genes = new Gene[nrgenes];
        Random rn = new Random(System.nanoTime());
        for (int i = 0; i < nrgenes; i++) {
            genes[i] = new Gene();
            genes[i].setName("g"+i);
            genes[i].setClassname("null");
            genes[i].setClassnumber(0);
            genes[i].setProbsetname("g"+i);
            genes[i].setDescription("g"+i);
            genes[i].setIndex(i);
            genes[i].setGeneid(i);
            genes[i].setValue(rn.nextInt(quantization));
        }
    }

    public float[] getInitialValues() {
        float[] initialvalues = new float[nrgenes];
        for (int i = 0; i < nrgenes; i++) {
            initialvalues[i] = getGenes()[i].getValue();
        }
        return (initialvalues);
    }

    public void setInitialValues(float[] initialvalues) {
        for (int i = 0; i < nrgenes; i++) {
            getGenes()[i].setValue(initialvalues[i]);
        }
    }

    /**
     * @return the topology
     */
    public String getTopology() {
        return topology;
    }

    /**
     * @param topology the topology to set
     */
    public void setTopology(String topology) {
        this.topology = topology;
    }

    /**
     * @return the nrgenes
     */
    public int getNrgenes() {
        return nrgenes;
    }

    /**
     * @param nrgenes the nrgenes to set
     */
    public void setNrgenes(int nrgenes) {
        this.nrgenes = nrgenes;
    }

    /**
     * @return the signalsize
     */
    public int getSignalsize() {
        return signalsize;
    }

    /**
     * @param signalsize the signalsize to set
     */
    public void setSignalsize(int signalsize) {
        this.signalsize = signalsize;
    }

    /**
     * @return the quantization
     */
    public int getQuantization() {
        return quantization;
    }

    /**
     * @param quantization the quantization to set
     */
    public void setQuantization(int quantization) {
        this.quantization = quantization;
    }

    /**
     * @return the avgedges
     */
    public float getAvgedges() {
        return avgedges;
    }

    /**
     * @param avgedges the avgedges to set
     */
    public void setAvgedges(float avgedges) {
        this.avgedges = avgedges;
    }

    /**
     * @return the allbooleanfunctions
     */
    public boolean isAllbooleanfunctions() {
        return allbooleanfunctions;
    }

    /**
     * @param allbooleanfunctions the allbooleanfunctions to set
     */
    public void setAllbooleanfunctions(boolean allbooleanfunctions) {
        this.allbooleanfunctions = allbooleanfunctions;
    }

    /**
     * @return the nrinitializations
     */
    public int getNrinitializations() {
        return nrinitializations;
    }

    /**
     * @param nrinitializations the nrinitializations to set
     */
    public void setNrinitializations(int nrinitializations) {
        this.nrinitializations = nrinitializations;
    }

    /**
     * @return the labelstemporalsignal
     */
    public Vector getLabelstemporalsignal() {
        return labelstemporalsignal;
    }

    /**
     * @param labelstemporalsignal the labelstemporalsignal to set
     */
    public void setLabelstemporalsignal(Vector labelstemporalsignal) {
        this.labelstemporalsignal = labelstemporalsignal;
    }

    /**
     * @return the genes
     */
    public Gene[] getGenes() {
        return genes;
    }

    /**
     * @param genes the genes to set
     */
    public void setGenes(Gene[] genes) {
        this.genes = genes;
    }

    /**
     * @return the temporalsignal
     */
    public float[][] getTemporalsignal() {
        return temporalsignal;
    }

    /**
     * @param temporalsignal the temporalsignal to set
     */
    public void setTemporalsignal(float[][] temporalsignal) {
        this.temporalsignal = temporalsignal;
    }

    public void setTemporalSignal(float[][] temporalsignal, Vector labels) {
        this.setTemporalsignal(temporalsignal);
        this.setLabelstemporalsignal(labels);
    }


    /**
     * @return the meanint
     */
    public float[] getMean() {
        return mean;
    }

    /**
     * @param mean the mean to set
     */
    public void setMean(float[] mean) {
        this.mean = mean;
    }

    /**
     * @return the std
     */
    public float[] getStd() {
        return std;
    }

    /**
     * @param std the std to set
     */
    public void setStd(float[] std) {
        this.std = std;
    }

    /**
     * @return the removedgenes
     */
    public Vector getRemovedgenes() {
        return removedgenes;
    }

    /**
     * @param removedgenes the removedgenes to set
     */
    public void setRemovedgenes(Vector removedgenes) {
        this.removedgenes = removedgenes;
    }

    /**
     * @return the lowthreshold
     */
    public float[] getLowthreshold() {
        return lowthreshold;
    }

    /**
     * @param lowthreshold the lowthreshold to set
     */
    public void setLowthreshold(float[] lowthreshold) {
        this.lowthreshold = lowthreshold;
    }

    /**
     * @return the hithreshold
     */
    public float[] getHithreshold() {
        return hithreshold;
    }

    /**
     * @param hithreshold the hithreshold to set
     */
    public void setHithreshold(float[] hithreshold) {
        this.hithreshold = hithreshold;
    }

    /**
     * @return the temporalsignalnormalized
     */
    public float[][] getTemporalsignalnormalized() {
        return temporalsignalnormalized;
    }

    /**
     * @param temporalsignalnormalized the temporalsignalnormalized to set
     */
    public void setTemporalsignalnormalized(float[][] temporalsignalnormalized) {
        this.temporalsignalnormalized = temporalsignalnormalized;
    }

    /**
     * @return the temporalsignalquantized
     */
    public int[][] getTemporalsignalquantized() {
        return temporalsignalquantized;
    }

    /**
     * @param temporalsignalquantized the temporalsignalquantized to set
     */
    public void setTemporalsignalquantized(int[][] temporalsignalquantized) {
        this.temporalsignalquantized = temporalsignalquantized;
    }

    public void setTemporalsignalquantized(float[][] temporalsignalquantized) {
        this.temporalsignalquantized = new int[temporalsignalquantized.length][temporalsignalquantized[0].length];
        for (int i = 0; i < temporalsignalquantized.length; i++){
            for (int j = 0; j < temporalsignalquantized[0].length; j++){
                this.temporalsignalquantized[i][j] = (int) temporalsignalquantized[i][j];
            }
        }
    }

    /**
     * @return the targets
     */
    public Vector getTargets() {
        return targets;
    }

    /**
     * @param targets the targets to set
     */
    public void setTargets(Vector targets) {
        this.targets = targets;
    }

    /**
     * @return the palette
     */
    public int[] getPalette() {
        return palette;
    }

    /**
     * @param palette the palette to set
     */
    public void setPalette(int[] palette) {
        this.palette = palette;
    }

    /**
     * @return the datatype
     */
    public int getDatatype() {
        return datatype;
    }

    /**
     * @param datatype the datatype to set
     */
    public void setDatatype(int datatype) {
        this.datatype = datatype;
    }

}
