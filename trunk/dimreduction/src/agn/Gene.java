package agn;

import java.awt.Color;
import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author fabricio@utfpr.edu.br
 */
public class Gene implements Serializable {
    //this is the name of the element on an array as given by the manufacturer. For Affymetrix chips, it corresponds to the probe set name.
    private String probsetname;
    //id of the gene
    private int geneid;
    //array position == identification on network.
    private int index;
    //for each arrayed element this field indicates whether the element is an
    //internal control (yes) or not (no).
    private boolean control;
    //gene name
    private String name;
    //this is the unique identifier for a locus
    private String locus;
    //classe string a que o gene pertence
    private String classname;
    //classe numerica a que o gene pertence
    private int classnumber;
    //describes the type of molecule from which an array element was derived. For Affymetrix array elements, it is an oligonucleotide.
    private String elementtype;
    //this is the description of the locus, General protein information
    private String description;
    //the name of the organism from which the microarray element sequence was derived.
    private String function;
    //the name of the organism from which the microarray element sequence was derived.
    private String organism;
    //chromosome - chromosome number, 'M' for mitochondria, or 'C' for chloroplast.
    private String chromosometype;
    //chromosome number of gene localization
    private int chromosome;
    //chromosome coordinates of the best probe set match to the Transcripts dataset.
    private int start;
    //chromosome coordinates of the best probe set match to the Transcripts dataset.
    private int stop;
    //also known as
    private String synonyms;
    //Gene Model Type (array_element_type)
    private String type;
    //product of the gene (protein name with short description)
    private String product;
    //id of the protein
    private String proteinid;
    //vector to keep the metabolic pathways (KEEG annotation) related to the gene.
    private Vector pathway;
    //vector to keep the metabolic pathways descriptions (KEEG annotation) related to the gene.
    private Vector pathwaydescription;
    //posicao x, usado apenas para as redes geograficas.
    private float x;
    //posicao y, usado apenas para as redes geograficas.
    private float y;
    //valor de expressao do gene.
    private float value;
    //vetor contendo os indices dos preditores selecionados para o gene.
    private Vector predictors;
    //vetor contendo os indices dos targets selecionados para o gene.
    private Vector targets;
    //indice que guarda quais funcoe booleanas foram escolhidas para o gene.
    //0  == A AND B, 1  == A AND NOT B, 2  == NOT A AND B, 3  == A XOR B
    //4  == A OR B, 5  == A NOR B, 6  == A XNOR B, 7  == A OR NOT B
    //8  == NOT A OR B, 9  == A NAND B, 10 == CONTRADICTION (always false)
    //11 == A, 12 == B, 13 == NOT B, 14 == NOT A, 15 == TAUTOLOGY (always true)
    private Vector booleanfunctions;
    //probability of the Boolean functions set to be chosen to generate the network dynamics.
    private Vector booleanfunctionsprobability;
    //valor atribuido pela funcao criterio para cada um dos preditores
    private Vector cfvalues;
    //vetor para armazenar os preditores que empataram ao predizer o target.
    //cada posicao do vetor, tambem eh um vetor contendo um conjunto de preditores (indices inteiros).
    private Vector[] predictorsties;
    //vector to keep the conditional probability distribution of the predictors given a target gene.
    private Vector probtable;
    //define the color used for the gene plot.
    private Color color;

    public Gene() {
        predictors = new Vector();
        targets = new Vector();
        probsetname = null;
        booleanfunctions = new Vector();
        booleanfunctionsprobability = new Vector();
        cfvalues = new Vector();
        classname = "null";
        classnumber = 0;
        probtable = null;
        pathway = null;
        color = new Color(128,128,128);
    }

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return the value
     */
    public float getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * @return the predictors
     */
    public Vector getPredictors() {
        return predictors;
    }

    /**
     * @param predictors the predictors to set
     */
    public void setPredictors(Vector predictors) {
        this.predictors = predictors;

    }

    public void addPredictor(int predictor, float cfvalue) {
        this.getPredictors().add(predictor);
        this.getCfvalues().add(cfvalue);
    }

    public void addPredictor(int predictor) {
        this.getPredictors().add(predictor);
    }

    public void removePredictor(int predictor) {
        if (this.getPredictors().size() > 0) {
            for (int p = 0; p < this.getPredictors().size(); p++) {
                if ((Integer) this.getPredictors().get(p) == predictor) {
                    this.getPredictors().remove(p);
                }
            }
        }
    }

    public void removeTarget(int target) {
        if (this.getTargets().size() > 0) {
            for (int t = 0; t < this.getTargets().size(); t++) {
                if ((Integer) this.getTargets().get(t) == target) {
                    this.getTargets().remove(t);
                }
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

    public void addTarget(int target) {
        this.getTargets().add(target);
    }

    /**
     * @return the cfvalue
     */
    public Vector getCfvalues() {
        return cfvalues;
    }

    /**
     * @param cfvalue the cfvalue to set
     */
    public void setCfvalue(float[] cfvalue) {
        this.setCfvalue(cfvalue);
    }

    public boolean setCfvalue(float cfvalue, int position) {
        if (this.getCfvalues() != null && position >= 0) {
            this.getCfvalues().add(position, cfvalue);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the booleanfunctions
     */
    public Vector getBooleanfunctions() {
        return booleanfunctions;
    }

    /**
     * @param booleanfunctions the booleanfunctions to set
     */
    public void setBooleanfunctions(Vector booleanfunctions) {
        this.booleanfunctions = booleanfunctions;
    }

    public void addBooleanfunctions(int indexbf, Vector booleanfunctions) {
        this.booleanfunctions.add(indexbf, booleanfunctions);
    }

    public Vector getBooleanfunctions(int indexbf) {
        return((Vector)this.booleanfunctions.get(indexbf));
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the locus
     */
    public String getLocus() {
        return locus;
    }

    /**
     * @param locus the locus to set
     */
    public void setLocus(String locus) {
        this.locus = locus;
    }

    /**
     * @return the organism
     */
    public String getOrganism() {
        return organism;
    }

    /**
     * @param organism the organism to set
     */
    public void setOrganism(String organism) {
        this.organism = organism;
    }

    /**
     * @return the elementtype
     */
    public String getElementtype() {
        return elementtype;
    }

    /**
     * @param elementtype the elementtype to set
     */
    public void setElementtype(String elementtype) {
        this.elementtype = elementtype;
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the stop
     */
    public int getStop() {
        return stop;
    }

    /**
     * @param stop the stop to set
     */
    public void setStop(int stop) {
        this.stop = stop;
    }

    /**
     * @param cfvalues the cfvalues to set
     */
    public void setCfvalues(Vector cfvalues) {
        this.cfvalues = cfvalues;
    }

    /**
     * @return the synonyms
     */
    public String getSynonyms() {
        return synonyms;
    }

    /**
     * @param synonyms the synonyms to set
     */
    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    /**
     * @return the function
     */
    public String getFunction() {
        return function;
    }

    /**
     * @param function the function to set
     */
    public void setFunction(String function) {
        this.function = function;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the probsetname
     */
    public String getProbsetname() {
        return probsetname;
    }

    /**
     * @param probsetname the probsetname to set
     */
    public void setProbsetname(String probsetname) {
        this.probsetname = probsetname;
    }

    /**
     * @return the geneid
     */
    public int getGeneid() {
        return geneid;
    }

    /**
     * @param geneid the geneid to set
     */
    public void setGeneid(int geneid) {
        this.geneid = geneid;
    }

    /**
     * @return the product
     */
    public String getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * @return the proteinid
     */
    public String getProteinid() {
        return proteinid;
    }

    /**
     * @param proteinid the proteinid to set
     */
    public void setProteinid(String proteinid) {
        this.proteinid = proteinid;
    }

    /**
     * @return the chromosome
     */
    public int getChromosome() {
        return chromosome;
    }

    /**
     * @param chromosome the chromosome to set
     */
    public void setChromosome(int chromosome) {
        this.chromosome = chromosome;
    }

    /**
     * @return the predictorsties
     */
    public Vector[] getPredictorsties() {
        return predictorsties;
    }

    /**
     * @param predictorsties the predictorsties to set
     */
    public void setPredictorsties(Vector[] predictorsties) {
        this.predictorsties = predictorsties;
    }

    /**
     * @return the control
     */
    public boolean isControl() {
        return control;
    }

    /**
     * @param control the control to set
     */
    public void setControl(boolean control) {
        this.control = control;
    }

    /**
     * @return the chromosometype
     */
    public String getChromosometype() {
        return chromosometype;
    }

    /**
     * @param chromosometype the chromosometype to set
     */
    public void setChromosometype(String chromosometype) {
        this.chromosometype = chromosometype;
    }

    /**
     * @return the probtable
     */
    public Vector getProbtable() {
        return probtable;
    }

    /**
     * @param probtable the probtable to set
     */
    public void setProbtable(Vector probtable) {
        this.probtable = probtable;
    }

    /**
     * @return the pathway
     */
    public Vector getPathway() {
        return pathway;
    }

    /**
     * @param pathway the pathway to set
     */
    public void setPathway(Vector pathway) {
        this.pathway = pathway;
    }

    /**
     * @return the pathwaydescription
     */
    public Vector getPathwaydescription() {
        return pathwaydescription;
    }

    /**
     * @param pathwaydescription the pathwaydescription to set
     */
    public void setPathwaydescription(Vector pathwaydescription) {
        this.pathwaydescription = pathwaydescription;
    }

    /**
     * @return the booleanfunctionsprobability
     */
    public Vector getBooleanfunctionsprobability() {
        return booleanfunctionsprobability;
    }

    public float getBooleanfunctionsprobability(int indexbf) {
        return ((Float)booleanfunctionsprobability.get(indexbf));
    }

    /**
     * @param booleanfunctionsprobability the booleanfunctionsprobability to set
     */
    public void setBooleanfunctionsprobability(Vector booleanfunctionsprobability) {
        this.booleanfunctionsprobability = booleanfunctionsprobability;
    }

    public void addBooleanfunctionsprobability(int indexbf, float bfp) {
        this.booleanfunctionsprobability.add(indexbf, bfp);
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the classname
     */
    public String getClassname() {
        return classname;
    }

    /**
     * @param classname the classname to set
     */
    public void setClassname(String classname) {
        this.classname = classname;
    }

    /**
     * @return the classnumber
     */
    public int getClassnumber() {
        return classnumber;
    }

    /**
     * @param classnumber the classnumber to set
     */
    public void setClassnumber(int classnumber) {
        this.classnumber = classnumber;
    }
}
