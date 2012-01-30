/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agn;

import charts.Chart;
import charts.GraphDR;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import utilities.IOFile;

/**
 *
 * @author fabricio
 */
public class BuildHTML {
    public static String tairlink = "http://arabidopsis.org/servlets/TairObject?type=locus&name=";//inserir apenas o locus no final
    public static String tigrlink = "http://www.tigr.org/tigr-scripts/euk_manatee/shared/ORF_infopage.cgi?db=ath1&orf=";//inserir apenas o locus no final
    public static String kegglink = "http://www.genome.jp/dbget-bin/www_bget?ath:";//inserir apenas o locus no final
    public static String ncbilink = "http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=search&term=";//inserir apenas o locus no final
    public static String ncbiplink = "http://www.ncbi.nlm.nih.gov/sites/entrez?db=protein&cmd=search&term=";//inserir apenas o protein id.

    public static String getImageMap(AGN agnnew, JGraph graph, String imgname){
        StringBuffer str= new StringBuffer();
        str.append("<img src='images/"+imgname+"' style='border: none;' type='image/png' alt='Result Graph' usemap='#graph'/>\n");
        str.append("<map name='graph' id='graph'>\n");
        //getCells(boolean groups, boolean vertices, boolean ports, boolean edges)
        //A helper method to return various arrays of cells that are visible in this cache.
        Object [] vertices = graph.getGraphLayoutCache().getCells(false, true, false, false);
        for (int i = 0; i < vertices.length; i++){
            DefaultGraphCell node = (DefaultGraphCell) vertices[i];
            Map attr = node.getAttributes();
            Rectangle2D rect = GraphConstants.getBounds(attr);
            //laco para cada vertice do grafo
            //coords="x1,y1,x2,y2"
            str.append("<area shape='rect' coords='"+rect.getX()+","+rect.getY()+","+rect.getMaxX()+","+rect.getMaxY()+"' href='"+attr.get("probsetname")+".html' title='"+attr.get("description")+"' alt='"+attr.get("description")+"'/>\n");
        }
        str.append("</map>");
        return (str.toString());
    }


    public static synchronized void BuildIndexPage(AGN agn,
            String destinationfolder, String filename, Vector targetindexes) throws IOException {
        File imgfolder = new File(destinationfolder + "images/");
        if (!imgfolder.exists()) {
            imgfolder.mkdir();
        }
        JGraph graph = AGNRoutines.ViewAGNMA(agn, targetindexes, false, 1);
        String imgoutputfile = destinationfolder+"images/"+filename+".png";
        OutputStream out = new BufferedOutputStream(new FileOutputStream(imgoutputfile));
        BufferedImage img = graph.getImage(graph.getBackground(), 0);
        ImageIO.write(img, "png", out);
        out.flush();
        out.close();
        String imagemap = getImageMap(agn, graph, filename+".png");
        //frame.dispose();
        String htmloutputfile = destinationfolder+filename+".html";
        File htmlfile = new File(htmloutputfile);
        //if (!htmlfile.exists()) {
        htmlfile.createNewFile();
        //}
        BufferedWriter bw = new BufferedWriter(new FileWriter(htmlfile, false));//sobrescreve o arquivo se ele existir.
        bw.write("<html>\n<head>\n");
        bw.write("<meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1'>\n");
        bw.write("<title>Gene Connections - ["+filename+"]</title>\n</head>\n<body>\n");
        bw.write(imagemap);
        bw.write("</body>\n</html>\n");
        bw.flush();
        bw.close();
    }

    public static String ProbabilityTable(AGN network, Gene targetgene) {
        StringBuffer table = new StringBuffer("<table width='100%' border='1' cellspacing='0'>\n");
        table.append("\n<tr>\n\t<td align='center' colspan='" + targetgene.getPredictors().size() + "'><b>Predictors</b></td>\n");
        table.append("\t<td align='center' colspan='2'><b>Target = " + targetgene.getName() + "</b></td>\n</tr>\n");
        table.append("<tr>\n");
        for (int i = 0; i < targetgene.getPredictors().size(); i++) {
            int predictorindex = (Integer) targetgene.getPredictors().get(i);
            Gene predictorgene = network.getGenes()[predictorindex];
            table.append("\t<td align='center'><a href = " + predictorgene.getProbsetname() + ".html>" + predictorgene.getName() + "</a></td>\n");
        }
        table.append("\t<td align='center'>0</td>\n\t<td align='center'>1</td>\n</tr>\n");

        //DEBUG
        int probtablesize = (int) (Math.log(targetgene.getProbtable().size())/Math.log(2));
        while (probtablesize < targetgene.getPredictors().size()){
            System.out.println("ERRO, TABELA DE PROBABILIDADES CONDICIONAIS MENOR QUE O NUMERO DE COMBINACOES.");
            int predictorindex = (Integer) targetgene.getPredictors().remove(probtablesize);
            targetgene.getCfvalues().removeElementAt(probtablesize);
            network.getGenes()[predictorindex].getTargets().removeElement(targetgene.getIndex());//remove uma ocorrencia
            System.out.println("ERRO, TABELA DE PROBABILIDADES CONDICIONAIS MENOR QUE O NUMERO DE COMBINACOES.");
        }
        //FIM-DEBUG

        int combinations = (int) Math.pow(2, targetgene.getPredictors().size());
        for (int c = 0; c < combinations; c++) {
            table.append("<tr>\n");
            String binnumber = utilities.MathRoutines.Dec2BaseN(c, 2, targetgene.getPredictors().size());
            //monta tabela de combinacoes entre os preditores.
            for (int n = 0; n < binnumber.length(); n++) {
                if (binnumber.charAt(n) != ' '){
                    table.append("\t<td align='center'>" + binnumber.charAt(n) + "</td>\n");
                }
            }
            //recupera cada linha da tabela de frequencia acumulada.
            float[] probtable = (float[]) targetgene.getProbtable().get(c);
            for (int n = 0; n < probtable.length; n++) {//cada coluna da tabela de valores do target.
                table.append("\t<td align='center'>" + probtable[n] + "</td>\n");
                //preenche cada coluna com a frequencia para cada valor do target.
            }
        }
        table.append("</tr>\n<tr>\n");
        table.append("\t<td colspan='"+ (targetgene.getPredictors().size()+2) +"' align='center'><b>Criterion Function Value = " + (Float)targetgene.getCfvalues().get(0) + "</b></td>\n");
        table.append("</tr>\n");
        table.append("</table>");
        return (table.toString());
    }

    public static synchronized void BuildPage(AGN agnnew, Gene genenew,
            String destinationfolder) throws IOException {
        Chart.defaultwidth = 800;
        Chart.defaultheight = 360;
        Vector window = AGNRoutines.SinalPlotMA(genenew.getIndex(),
                false, 1.1f, 2, agnnew, false);
       ((JFrame) window.get(1)).dispose();
       ChartPanel jpanel = (ChartPanel) window.get(2);
       JFreeChart jfreechart = (JFreeChart) jpanel.getChart();
        //JFreeChart chart = (JFreeChart) window.get(0);
        //JFrame chartwindow = (JFrame) window.get(1);
        BufferedImage img = jfreechart.createBufferedImage(800, 330);
        String nmimg = genenew.getProbsetname() + ".png";
        File imgfolder = new File(destinationfolder + "images/");
        if (!imgfolder.exists()) {
            imgfolder.mkdir();
        }
        String imgoutfile = destinationfolder + "images/" + nmimg;
        FileOutputStream fos = new FileOutputStream(imgoutfile);
        OutputStream out = new BufferedOutputStream(fos);
        ImageIO.setUseCache(true);
        ImageIO.write(img, "png", out);
        fos.close();
        out.flush();
        out.close();
        //chartwindow.dispose();
        //img.flush();
        File htmlfile = new File(destinationfolder, genenew.getProbsetname() + ".html");
        if (!htmlfile.exists()) {
            htmlfile.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(htmlfile, false));//sobrescreve o arquivo se ele existir.
        bw.write("<html>\n<head>\n");
        bw.write("<meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1'>\n");
        bw.write("<title>" + genenew.getName() + " [" + genenew.getOrganism() + "]</title>\n</head>\n<body>\n");

        bw.write("<table width='100%' border='0' cellspacing='0'>\n");
        bw.write("<tr><td colspan='2'><b><font size='+2'>" + genenew.getName() + " [<i>" + genenew.getOrganism() + "</i>]</font></b></td></tr>\n");

        bw.write("<tr bgcolor=#F1EFEC valign='top'>\n");
        bw.write("<td width='15%'><b>Gene name</b></td>\n<td width='85%'>" + genenew.getName() + "</td>\n</tr>\n");

        bw.write("<tr valign='top'>\n");
        bw.write("<td><b>Prob Set Name</b></td>\n<td>" + genenew.getProbsetname() + "</td>\n</tr>\n");

        bw.write("<tr bgcolor=#F1EFEC valign='top'>\n");
        bw.write("<td><b>Index (internal control)</b></td>\n<td>" + genenew.getIndex() + "</td>\n</tr>\n");

        bw.write("<tr valign='top'>\n");
        bw.write("<td><b>Chromosome</b></td>\n<td>" + genenew.getChromosome() + "</td>\n</tr>\n");

        bw.write("<tr bgcolor=#F1EFEC valign='top'>\n");
        bw.write("<td><b>Region</b></td>\n<td>" + genenew.getStart() + "..." + genenew.getStop() + "</td>\n</tr>\n");

        bw.write("<tr valign='top'>\n");
        bw.write("<td colspan='2'><a href='" + tairlink + genenew.getLocus() + "' target='_blank'><b>TAIR: " + genenew.getLocus() + "</b></a></td>\n</tr>\n");

        bw.write("<tr bgcolor=#F1EFEC valign='top'>\n");
        bw.write("<td colspan='2'><a href='" + tigrlink + genenew.getLocus() + "' target='_blank'><b>TIGR: " + genenew.getLocus() + "</b></a></td>\n</tr>\n");

        bw.write("<tr valign='top'>\n");
        bw.write("<td colspan='2'><a href='" + kegglink + genenew.getLocus() + "' target='_blank'><b>KEEG: " + genenew.getLocus() + "</b></a></td>\n</tr>\n");

        bw.write("<tr bgcolor=#F1EFEC valign='top'>\n");
        bw.write("<td colspan='2'><a href='" + ncbilink + genenew.getGeneid() + "' target='_blank'><b>NCBI: " + genenew.getGeneid() + "</b></a></td>\n</tr>\n");

        bw.write("<tr valign='top'>\n");
        bw.write("<td><b>Also known as</b></td>\n<td>" + genenew.getSynonyms() + "</td>\n</tr>\n");

        bw.write("<tr bgcolor=#F1EFEC valign='top'>\n");
        bw.write("<td><b>Function</b></td>\n<td>" + genenew.getFunction() + "</td>\n</tr>\n");

        bw.write("<tr valign='top'>\n");
        bw.write("<td><b>Products</b></td>\n<td>" + genenew.getProduct() + "</td>\n</tr>\n");

        bw.write("<tr bgcolor=#F1EFEC valign='top'>\n");
        bw.write("<td><b>General protein information</b></td>\n<td>" + genenew.getDescription() + "</td>\n</tr>\n");

        bw.write("<tr valign='top'>\n");
        bw.write("<td colspan='2'><a href='" + ncbiplink + genenew.getProteinid() + "' target='_blank'><b>NCBI-Protein: " + genenew.getProteinid() + "</b></a></td>\n</tr>\n");
        bw.write("</table>\n<br><br>\n");

        bw.write("<table width='100%' border='0' cellspacing='0'>\n");
        bw.write("<tr>\n<td><b><font size='+1'>Gene Identification</font></b></td>\n");
        bw.write("<td><b><font size='+1'><center>Original and Quantized Temporal Signal</center></font></b></td>\n");
        bw.write("<td><b><font size='+1'><center>Table of probability distribution</center></font></b></td>\n</tr>\n");
        bw.write("<tr bgcolor=#F1EFEC>\n");
        bw.write("<td valign='middle'><b>Gene " + genenew.getName() + "</b></td>\n");
        bw.write("<td><img src='images/" + nmimg + "' border=0></td>\n");
        bw.write("<td align='top'>\n");
        //DEBUG
        //if (genenew.getName().equalsIgnoreCase("AT2G01140")){
        //    System.out.println("Erro na geracao da tabela de frequencias...");
        //}
        //END-DEBUG
        if (genenew.getPredictors().size() > 0) {
            bw.write(ProbabilityTable(agnnew, genenew));
        }
        bw.write("</td>\n</tr>\n");

        for (int p = 0; p < genenew.getPredictors().size(); p++) {
            if (p % 2 == 0) {
                bw.write("<tr>\n");
            } else {
                bw.write("<tr bgcolor=#F1EFEC>\n");
            }
            int pgindex = (Integer) genenew.getPredictors().get(p);
            Gene predictorgene = agnnew.getGenes()[pgindex];
            bw.write("<td valign='middle'><b>Predictor " + (p + 1) + ": <a href = " + predictorgene.getProbsetname() + ".html>" + predictorgene.getName() + "</a></td>\n");
            nmimg = predictorgene.getProbsetname() + ".png";
            bw.write("<td><img src='images/" + nmimg + "' border=0></td>\n");
            bw.write("<td></td>\n</tr>\n");
        }
        if (genenew.getPredictorsties() != null) {
            bw.write("<tr>\n<td colspan='3'><hr width=100%></td>\n</tr>\n");
            bw.write("<tr>\n<td colspan='3'><b><font size='+1'>Predictors Tied</font></b></td>");
            for (int tiescount = 1; tiescount < genenew.getPredictorsties().length; tiescount++) {
                bw.write("<tr>\n<td colspan='3'><hr width=100%></td>\n</tr>\n");
                bw.write("<tr>\n<td colspan='3'><b><font size='+1'>Tie "+tiescount+"</font></b></td>");
                Vector predictorset = genenew.getPredictorsties()[tiescount];
                for (int pc = 0; pc < predictorset.size(); pc++) {
                    int predictorindex = (Integer) predictorset.get(pc);
                    Gene predictorgene = agnnew.getGenes()[predictorindex];
                    File predictorhtmlfile = new File(destinationfolder, predictorgene.getProbsetname() + ".html");
                    if (!predictorhtmlfile.exists()) {
                        //chamada recursiva para gerar paginas dos genes empatados como preditores.
                        BuildPage(agnnew, predictorgene, destinationfolder);
                        //System.gc();
                    }
                    nmimg = predictorgene.getProbsetname() + ".png";
                    bw.write("<tr>\n");
                    bw.write("<td><b>Predictor "+ (pc+1) +": <a href = " + predictorgene.getProbsetname() + ".html>" + predictorgene.getName() + "</a></b></td>");
                    bw.write("<td><img src='images/" + nmimg + "' border=0></td>\n");
                    bw.write("<td></td>\n</tr>\n");
                }
                //bw.write("</td>\n<td></td>\n<td></td>\n</tr>\n");
            }
        }
        bw.write("</table>\n</body>\n</html>\n");
        bw.flush();
        bw.close();
    }

    public static synchronized void BuildFiles(
            AGN agn,
            String destinationfolder,
            Vector targetindexes)
            throws IOException {
        for (int i = 0; i < agn.getNrgenes(); i++) {
            Gene genenew = agn.getGenes()[i];
            if (
                    (targetindexes != null && targetindexes.contains(i)) ||
                    !(genenew.getTargets().isEmpty() && genenew.getPredictors().isEmpty())
                    ) {
                BuildPage(agn, genenew, destinationfolder);
                System.gc();
            }

        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-v")) {
                AGN agnnew = IOFile.ReadAGNfromFile(args[1]);
                BuildFiles(agnnew, args[2],null);
                /*
                AGN agn = IOFile.ReadAGNfromFile("D:/doutorado/Marie-Anne/www/resultados-redes-frio/agn-razoes-frio-normal-root.agn");
                AGN agnnew = new AGN(agn.getNrgenes(), agn.getSignalsize(), agn.getQuantization());
                agnnew.setTemporalsignalq(agn.getTemporalsignal());
                agnnew.setLabelstemporalsignal(agn.getLabelstemporalsignal());
                float[][] Mo = IOFile.ReadMatrix("D:/doutorado/Marie-Anne/www/resultados-redes-frio/dados-razoes-frio-normal-root.csv",
                1,
                2,
                MainAGNWindow.delimiter);
                agnnew.setTemporalsignal(Mo);

                MainWindow.Mo = Mo;
                MainWindow.Md = agn.getTemporalsignal();
                MainWindow.featurestitles = agn.getLabelstemporalsignal();

                for (int i = 0; i < agn.getNrgenes(); i++) {
                Gene gene = agn.getGenes()[i];
                Gene genenew = agnnew.getGenes()[i];
                //copia dos dados existentes na nova estrutura de dados genenew.
                genenew.setIndex(i);
                genenew.setProbsetname(gene.getId());
                genenew.setName(gene.getName());
                genenew.setLocus(gene.getLocus());
                genenew.setType(gene.getType());
                genenew.setDescription(gene.getDescription());
                genenew.setOrganism(gene.getOrganism());
                genenew.setX(gene.getX());
                genenew.setY(gene.getY());
                genenew.setValue(gene.getValue());
                genenew.setPredictors(gene.getPredictors());
                genenew.setTargets(gene.getTargets());
                genenew.setBooleanfunctions(gene.getBooleanfunctions());
                genenew.setCfvalues(gene.getCfvalues());
                genenew.setPredictorsties(gene.getPredictorsties());
                String genelocus = genenew.getLocus();
                StringTokenizer st = new StringTokenizer(genelocus, String.valueOf(' ') + String.valueOf(';'));
                genelocus = st.nextToken();
                genenew.setLocus(genelocus);

                if (!(genenew.getTargets().isEmpty() && genenew.getPredictors().isEmpty())) {
                if (!genelocus.equalsIgnoreCase("no_match")) {
                boolean answer = geneteste.ReadGene(genelocus);
                if (answer) {
                genenew.setOrganism("Arabidopsis thaliana");
                genenew.setStart(geneteste.posini);
                genenew.setStop(geneteste.posfim);
                genenew.setName(geneteste.genename);
                genenew.setSynonyms(geneteste.synonyms);
                genenew.setFunction(geneteste.function);
                genenew.setLocus(geneteste.locus);
                genenew.setGeneid(geneteste.geneid);
                genenew.setDescription(geneteste.notes);
                genenew.setProduct(geneteste.product);
                genenew.setProteinid(geneteste.proteinid);
                genenew.setChromosome(geneteste.chromosome);
                } else {
                System.out.println("Gene " + genenew.getLocus() + " not found.");
                }
                }
                int[] list = {i};
                Chart.defaultwidth = 800;
                Chart.defaultheight = 330;
                Vector window = MainWindow.SinalLinePlot(list, false, 2);
                JFreeChart chart = (JFreeChart) window.get(0);
                JFrame chartwindow = (JFrame) window.get(1);
                BufferedImage img = chart.createBufferedImage(800, 330);
                String nmimg = genenew.getProbsetname() + ".png";
                String imgoutfile = folder + "/images/" + nmimg;
                OutputStream out = new BufferedOutputStream(new FileOutputStream(imgoutfile));
                ImageIO.write(img, "png", out);
                out.flush();
                out.close();
                chartwindow.dispose();
                } else {
                System.out.println("Gene " + genenew.getLocus() + " has no links.");
                }
                }
                IOFile.WriteAGNnewtoFile(agnnew, "D:/doutorado/Marie-Anne/html-files/agn-razoes-frio-normal-root-new.agn");

                //AGN agnnew = IOFile.ReadAGNnewfromFile("D:/doutorado/Marie-Anne/html-files/agn-razoes-frio-normal-root-new.agn");
                //MainWindow.Mo = agnnew.getTemporalsignal();
                //MainWindow.Md = agn.getTemporalsignal();
                //MainWindow.featurestitles = agn.getLabelstemporalsignal();
                //float [][] teste = agn.getTemporalsignal();
                float[][] expressiondata = IOFile.ReadMatrix("D:/doutorado/Marie-Anne/www/resultados-redes-frio/dados-razoes-frio-normal-root.csv", 1, 2, MainMarieAnne.delimiter);
                IOFile.PrintMatrix(expressiondata);
                Preprocessing.quantizecolumns(expressiondata, 3, true, 0);
                IOFile.PrintMatrix(expressiondata);

                for (int i = 0; i < agnnew.getNrgenes(); i++) {
                Gene genenew = agnnew.getGenes()[i];
                if (!(genenew.getTargets().isEmpty() && genenew.getPredictors().isEmpty())) {
                int[] list = {i};
                Chart.defaultwidth = 800;
                Chart.defaultheight = 330;
                Vector window = MainWindow.SinalPlot(list, false, 1.1f, 2);
                JFreeChart chart = (JFreeChart) window.get(0);
                JFrame chartwindow = (JFrame) window.get(1);
                BufferedImage img = chart.createBufferedImage(800, 330);
                String nmimg = genenew.getProbsetname() + "q.png";
                String imgoutfile = folder + "/images/" + nmimg;
                OutputStream out = new BufferedOutputStream(new FileOutputStream(imgoutfile));
                ImageIO.write(img, "png", out);
                out.flush();
                out.close();
                chartwindow.dispose();

                BuildFile(agnnew, genenew, folder);
                }
                }*/
            }
        }
    }
}
