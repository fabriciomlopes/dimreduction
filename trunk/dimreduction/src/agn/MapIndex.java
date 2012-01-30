/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agn;

import java.util.Vector;

/**
 * @author Fabricio
 */
public class MapIndex {
    private int newindex;
    private int decimalvalue;
    private int[] quantizedrow;
    private Vector originalindexes;

    public MapIndex(int originalindex, int newindex, int decimalvalue, int [] quantizedrow){
        this.newindex = newindex;
        this.decimalvalue = decimalvalue;
        this.quantizedrow = quantizedrow.clone();
        this.originalindexes = new Vector();
        this.originalindexes.addElement(originalindex);
    }

    /**
     * @return the originalindex
     */
    public Vector getOriginalindex() {
        return originalindexes;
    }

    public void addOriginalindex(int originalindex) {
        this.originalindexes.addElement(originalindex);
    }

    /**
     * @param originalindex the originalindex to set
     */
    public void setOriginalindex(Vector originalindexes) {
        this.originalindexes = originalindexes;
    }

    /**
     * @return the newindex
     */
    public int getNewindex() {
        return newindex;
    }

    /**
     * @param newindex the newindex to set
     */
    public void setNewindex(int newindex) {
        this.newindex = newindex;
    }

    /**
     * @return the decimalvalue
     */
    public int getDecimalvalue() {
        return decimalvalue;
    }

    /**
     * @param decimalvalue the decimalvalue to set
     */
    public void setDecimalvalue(int decimalvalue) {
        this.decimalvalue = decimalvalue;
    }

    /**
     * @return the quantizedrow
     */
    public int[] getQuantizedrow() {
        return quantizedrow;
    }

    /**
     * @param quantizedrow the quantizedrow to set
     */
    public void setQuantizedrow(int[] quantizedrow) {
        this.quantizedrow = quantizedrow;
    }


}
