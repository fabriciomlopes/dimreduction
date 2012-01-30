/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agn;

import java.util.Vector;

/**
 *
 * @author fabricio
 */
public class PredictorSet {

    protected int target;
    protected Vector pset;
    protected float cfvalue;
    protected Vector psetold;
    protected float cfvalueold;
    protected float improvement;
    protected Vector probtable;
    protected Vector probtableold;
    protected Vector ties;
    private Vector exestack;
    private Vector expandedestack;


    public PredictorSet(int target) {
        this.target = target;
        pset = null;
        cfvalue = 1;
        psetold = null;
        cfvalueold = 1;
        improvement = 1;
        probtable = null;
        exestack = null;
        expandedestack = null;
    }

    public float getImprovement() {
        return (improvement);
    }

    /**
     * @return the pset
     */
    public Vector getPset() {
        return pset;
    }

    /**
     * @param pset the pset to set
     */
    public void setPset(
            Vector pset,
            float cfvalue,
            Vector probtable,
            Vector ties,
            Vector exestack,
            Vector expandedestack) {
        //soh atualiza se o novo conjunto for melhor...
        if (this.cfvalue - cfvalue > 0) {
            //melhorou o resultado anterior.
            setPsetold(this.pset);
            setCfvalueold(this.cfvalue);
            setProbtableold(this.probtable);

            this.pset = pset;
            this.cfvalue = cfvalue;
            setProbtable(probtable);
            setTies(ties);
            setExestack(exestack);
            setExpandedestack(expandedestack);
            //atualiza o improvement
            if (cfvalue > 0.05) {
                setImprovement(this.cfvalueold - this.cfvalue);
            } else {
                setImprovement(0);
            }
        } else {
            //para os que nao tiveram variacao ou tiveram variacao negativa...
            setImprovement(this.cfvalue - cfvalue);
        }
    }
    /**
     * @return the cfvalue
     */
    public float getCfvalue() {
        return cfvalue;
    }

    /**
     * @return the psetold
     */
    public Vector getPsetold() {
        return psetold;
    }

    /**
     * @param psetold the psetold to set
     */
    public void setPsetold(Vector psetold) {
        this.psetold = psetold;
    }

    /**
     * @return the cfvalueold
     */
    public float getCfvalueold() {
        return cfvalueold;
    }

    /**
     * @param cfvalueold the cfvalueold to set
     */
    public void setCfvalueold(float cfvalueold) {
        this.cfvalueold = cfvalueold;
    }

    /**
     * @return the target
     */
    public int getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(int target) {
        this.target = target;
    }

    /**
     * @param improvement the improvement to set
     */
    public void setImprovement(float improvement) {
        this.improvement = improvement;
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
     * @return the probtableold
     */
    public Vector getProbtableold() {
        return probtableold;
    }

    /**
     * @param probtableold the probtableold to set
     */
    public void setProbtableold(Vector probtableold) {
        this.probtableold = probtableold;
    }

    /**
     * @return the ties
     */
    public Vector getTies() {
        return ties;
    }

    /**
     * @param ties the ties to set
     */
    public void setTies(Vector ties) {
        this.ties = ties;
    }

    /**
     * @return the exestack
     */
    public Vector getExestack() {
        return exestack;
    }

    /**
     * @param exestack the exestack to set
     */
    public void setExestack(Vector exestack) {
        this.exestack = exestack;
    }

    /**
     * @return the expandedestack
     */
    public Vector getExpandedestack() {
        return expandedestack;
    }

    /**
     * @param expandedestack the expandedestack to set
     */
    public void setExpandedestack(Vector expandedestack) {
        this.expandedestack = expandedestack;
    }
}
