package agn;

import org.htmlparser.beans.StringBean;
import java.util.StringTokenizer;

public class OnlineSearch {

    /**
     *
     * @author Gabriel Rubino
     */
    static public String getUrlContentsAsText(String url) {
        String content = "";
        StringBean stringBean = new StringBean();
        stringBean.setURL(url);
        content = stringBean.getStrings();
        return content;
    }

    static public int getCompare(String str, String token) {
        int b = 1;
        if (str.length() == token.length() - 1) {
            for (int i = 0; i < str.length() - 1; i++) {
                if (str.charAt(i) != token.charAt(i)) {
                    b = 0;
                    i = str.length();
                }
            }
        } else {
            b = 0;
        }
        return b;
    }

    public static void AddNCBIInformation(Gene gene) {
        boolean sumario = false;
        String delimiter = String.valueOf('\n'),
                token = "",
                symbol = "Gene symbol",
                description = "Gene description",
                source = "Primary source",
                locus = "Locus tag",
                type = "Gene type",
                rna = "RNA name",
                refseq = "RefSeq status",
                organism = "Organism",
                lineage = "Lineage",
                known = "Also known as";
        //codigo = "817344";

        StringTokenizer paginat = new StringTokenizer(getUrlContentsAsText("http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=search&term=" + gene.getGeneid()), delimiter);
        //StringTokenizer paginat = new StringTokenizer(getUrlContentsAsText("file:///media/arquivos/doutorado/dimreduction/teste-on-line/CPN60A%20chaperonin-60alpha%20[Arabidopsis%20thaliana]%20-%20Gene%20-%20NCBI.html"), delimiter);

        while (paginat.hasMoreTokens()) {
            token = paginat.nextToken();
            //System.out.println(token);

            //if (getCompare(symbol, token) == 1) {
            if (token.startsWith("Gene symbol")) {
                token = paginat.nextToken();
                System.out.println("Gene symbol >>" + token);
                gene.setName(token);
            }

            //if (getCompare(description, token) == 1) {
            if (token.startsWith(description)) {
                token = paginat.nextToken();
                System.out.println("Gene description >> " + token);
                gene.setDescription(token);
            }

            //if (getCompare(source, token) == 1) {
            if (token.startsWith(source)) {
                token = paginat.nextToken();
                System.out.println("Primary source >> " + token);
                //gene.setSource(token);
            }

            //if (getCompare(locus, token) == 1) {
            if (token.startsWith(locus)) {
                token = paginat.nextToken();
                System.out.println("Locus tag >> " + token);
                gene.setLocus(token);
            }

            //if (getCompare(type, token) == 1) {
            if (token.startsWith(type)) {
                token = paginat.nextToken();
                System.out.println("Gene type >> " + token);
                gene.setType(token);
            }

            //if (getCompare(rna, token) == 1) {
            if (token.startsWith(rna)) {
                token = paginat.nextToken();
                System.out.println("RNA name >> " + token);
                //gene.setRnaname(token);
            }

            /* não precisa     
            if(getCompare(refseq,token)==1){
            token = paginat.nextToken();
            System.out.println("RefSeq status >> "+token);
            }
             */

            //if(getCompare(lineage,token)==1){
            if (token.startsWith(lineage)) {
                token = paginat.nextToken();
                System.out.println("Lineage >> " + token);
                gene.setDescription(gene.getDescription() + "\n" + token);
            }

            //if (getCompare(organism, token) == 1) {
            if (token.startsWith(organism)) {
                token = paginat.nextToken();
                System.out.println("Organism >> " + token);
                gene.setOrganism(token);
            }

            //if (getCompare(known, token) == 1) {
            if (token.startsWith(known)) {
                token = paginat.nextToken();
                System.out.println("Also known as >> " + token);
                gene.setSynonyms(token);
                sumario = true;
            }

            if (sumario && token.startsWith("Summary")) {
                sumario = false;
                token = paginat.nextToken();
                System.out.println("Summary >> " + token);
                gene.setDescription(gene.getDescription() + "\n" + token);

                //finaliza a procura no arquivo.
                return;
            }
        }
    }

    public static void main(String[] args) {
        String codigo = "",
                delimiter = String.valueOf('\n'),
                token = "",
                symbol = "Gene symbol",
                description = "Gene description",
                source = "Primary source",
                locus = "Locus tag",
                type = "Gene type",
                rna = "RNA name",
                refseq = "RefSeq status",
                organism = "Organism",
                lineage = "Lineage",
                known = "Also known as";
        codigo = "817344";
        boolean sumario = false;

        StringTokenizer paginat = new StringTokenizer(getUrlContentsAsText("http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=search&term=" + codigo), delimiter);
        //StringTokenizer paginat = new StringTokenizer(getUrlContentsAsText("file:///media/arquivos/doutorado/dimreduction/teste-on-line/CPN60A%20chaperonin-60alpha%20[Arabidopsis%20thaliana]%20-%20Gene%20-%20NCBI.html"), delimiter);

        Gene gene = new Gene();
        while (paginat.hasMoreTokens()) {
            token = paginat.nextToken();
            System.out.println(token);

            if (token.startsWith("Summary")) {
                System.out.println(token);
            }

            //if (getCompare(symbol, token) == 1) {
            if (token.startsWith("Gene symbol")) {
                token = paginat.nextToken();
                System.out.println("Gene symbol >>" + token);
                gene.setName(token);
            }

            //if (getCompare(description, token) == 1) {
            if (token.startsWith(description)) {
                token = paginat.nextToken();
                System.out.println("Gene description >> " + token);
                gene.setDescription(token);
            }

            //if (getCompare(source, token) == 1) {
            if (token.startsWith(source)) {
                token = paginat.nextToken();
                System.out.println("Primary source >> " + token);
                //gene.setSource(token);
            }

            //if (getCompare(locus, token) == 1) {
            if (token.startsWith(locus)) {
                token = paginat.nextToken();
                System.out.println("Locus tag >> " + token);
                gene.setLocus(token);
            }

            //if (getCompare(type, token) == 1) {
            if (token.startsWith(type)) {
                token = paginat.nextToken();
                System.out.println("Gene type >> " + token);
                gene.setType(token);
            }

            //if (getCompare(rna, token) == 1) {
            if (token.startsWith(rna)) {
                token = paginat.nextToken();
                System.out.println("RNA name >> " + token);
                //gene.setRnaname(token);
            }

            /* não precisa     
            if(getCompare(refseq,token)==1){
            token = paginat.nextToken();
            System.out.println("RefSeq status >> "+token);
            }
            
            if(getCompare(lineage,token)==1){
            token = paginat.nextToken();
            System.out.println("Lineage >> "+token);
            }
             */
            //if (getCompare(organism, token) == 1) {
            if (token.startsWith(organism)) {
                token = paginat.nextToken();
                System.out.println("Organism >> " + token);
                gene.setOrganism(token);
            }

            //if (getCompare(known, token) == 1) {
            if (token.startsWith(known)) {
                token = paginat.nextToken();
                System.out.println("Also known as >> " + token);
                gene.setSynonyms(token);
                sumario = true;
            }

            if (sumario && token.startsWith("Summary")) {
                sumario = false;
                token = paginat.nextToken();
                System.out.println("Summary >> " + token);
                gene.setDescription(token);

                //finaliza a procura no arquivo.
                return;
            }
        }
    }
}
