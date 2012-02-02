/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agn;

import charts.Chart;
import charts.GraphDR;
import charts.PrefuseFrame;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import com.jgraph.layout.organic.JGraphOrganicLayout;
import fs.FS;
import fs.Main;
import fs.Preprocessing;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import prefuse.Visualization;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import utilities.IOFile;
import utilities.LogExecucao;
import utilities.MathRoutines;

/**
 * @author Fabricio
 */
public class AGNRoutines {

    public static final Random randomcolor;
    public static final long seed = 1878;

    static {
        randomcolor = new Random(seed);
    }

    public static Color getRandomColor() {
        return (new Color(randomcolor.nextInt(256),
                randomcolor.nextInt(256),
                randomcolor.nextInt(256)));
    }

    public static Color getRandomGray() {
        int intensity = randomcolor.nextInt(256);
        return (new Color(intensity, intensity, intensity));
    }

    public static int FindFilteredIndex(Vector mapindexes, int originalindex) {
        for (int i = 0; i < mapindexes.size(); i++) {
            MapIndex map = (MapIndex) mapindexes.get(i);
            for (int im = 0; im < map.getOriginalindex().size(); im++) {
                int mappedindex = (Integer) map.getOriginalindex().get(im);
                if (originalindex == mappedindex) {
                    return (map.getNewindex());
                }
            }
        }
        return (-1);
    }

    public static int FindOriginalIndex(AGN network, Gene g) {
        for (int i = 0; i < network.getGenes().length; i++) {
            if (network.getGenes()[i].equals(g)) {
                return (i);
            }
        }
        return (-1);
    }

    public static void CreateFilteredQuantizedData(
            Vector mapindexes,
            int[][] newquantizeddata) {
        for (int i = 0; i < mapindexes.size(); i++) {
            MapIndex map = (MapIndex) mapindexes.get(i);
            newquantizeddata[i] = map.getQuantizedrow().clone();
        }
    }

    public static void RefreshGeneIndexes(AGN agn, Vector mapindexes) {
        for (int i = 0; i < mapindexes.size(); i++) {
            MapIndex map = (MapIndex) mapindexes.get(i);
            for (int j = 0; j < map.getOriginalindex().size(); j++) {
                int originalindex = (Integer) map.getOriginalindex().get(j);
                agn.getGenes()[originalindex].setIndex(map.getNewindex());
            }
        }
    }

    public static boolean RefreshGraphPositionsonAGN(AGN agn, JGraph graph) {
        Object[] vertices = graph.getGraphLayoutCache().getCells(false, true, false, false);
        for (int i = 0; i < vertices.length; i++) {
            DefaultGraphCell node = (DefaultGraphCell) vertices[i];
            Map attr = node.getAttributes();
            Rectangle2D rect = GraphConstants.getBounds(attr);
            //laco para cada vertice do grafo
            int geneindex = (Integer) attr.get("index");
            agn.getGenes()[geneindex].setX((float) rect.getX());
            agn.getGenes()[geneindex].setY((float) rect.getY());
        }
        return (true);
    }

    public static boolean RefreshGraphPositionsonAGN(AGN agn, Visualization vis) {
        Iterator nodes = vis.items(PrefuseFrame.NODES);
        while (nodes.hasNext()) {
            NodeItem node = (NodeItem) nodes.next();
            int id = node.getInt("id");
            float x = (float) node.getX();
            float y = (float) node.getY();
            Gene gene = agn.getGenes()[id];
            gene.setX(x);
            gene.setY(y);
        }
        return (true);
    }

    public static boolean RefreshNodeColoronAGN(AGN agn, Visualization vis) {
        Iterator nodes = vis.items(PrefuseFrame.NODES);
        while (nodes.hasNext()) {
            NodeItem node = (NodeItem) nodes.next();
            int id = node.getInt("id");
            int color = node.getFillColor();
            Gene gene = agn.getGenes()[id];
            gene.setColor(ColorLib.getColor(color));
        }
        return (true);
    }

    //int edgetype == 1 - ARROW_SIMPLE
    //int edgetype == 2 - ARROW_NONE
    public static JGraph ViewAGNMA(AGN agn, Vector targetindexes, boolean show, int edgetype) {
        boolean dolayout = false;
        // Construct Model and GraphDR
        GraphModel model = new DefaultGraphModel();
        GraphLayoutCache view = new GraphLayoutCache(model, new DefaultCellViewFactory());
        JGraph graph = new JGraph(model, view);

        //JGraph graph = new JGraph(model);
        // Control-drag should clone selection
        graph.setCloneable(true);
        // Enable edit without final RETURN keystroke
        graph.setInvokesStopCellEditing(true);
        // When over a cell, jump to its default port (we only
        // have one, anyway)
        graph.setJumpToDefaultPort(true);
        // Insert all three cells in one call, so we need an
        // array to store them
        List<DefaultGraphCell> vertex = new ArrayList<DefaultGraphCell>();
        List<DefaultGraphCell> edges = new ArrayList<DefaultGraphCell>();
        //List<DefaultGraphCell> blueedges = new ArrayList<DefaultGraphCell>();
        //List<DefaultGraphCell> rededges = new ArrayList<DefaultGraphCell>();
        //List<DefaultGraphCell> orangeedges = new ArrayList<DefaultGraphCell>();

        double rX = (GraphDR.w - 100) / 2;
        double rY = (GraphDR.h - 100) / 2;
        double theta = 0;
        double delta = 2 * Math.PI / agn.getGenes().length;

        int count = 0;
        for (int gt = 0; gt < agn.getGenes().length; gt++) {
            String name = "g" + String.valueOf(gt);
            if (agn.getGenes()[gt].getName() != null) {
                name = agn.getGenes()[gt].getName();// + "(" + String.valueOf(gt) + ")";
            }
            float x = agn.getGenes()[gt].getX();
            float y = agn.getGenes()[gt].getY();
            if (x == 0 && y == 0) {//atribui posicoes iniciais para os genes (anel circular)
                x = (float) (rX + 10 + (rX * Math.cos(theta)));
                y = (float) (rY + 10 + (rY * Math.sin(theta)));
                agn.getGenes()[gt].setX(x);
                agn.getGenes()[gt].setY(y);
                count++;
            }
            theta += delta;
            DefaultGraphCell node = GraphDR.createVertex(name, x, y, 0, 0, Color.white, false, true);
            //node.setUserObject(agn.getGenes()[gt].getProbsetname());
            vertex.add(node);
            Color cor = Color.BLACK;//MainMarieAnne.getBGColor(agn.getGenes()[gt]);

            GraphConstants.setGradientColor(vertex.get(gt).getAttributes(), cor);
        }
        if (count > 0.5 * agn.getNrgenes()) {//mais da metade dos genes da rede esta com coordenada 0,0
            dolayout = true;
        }
        dolayout = false;
        Vector nodes = new Vector();
        for (int gt = 0; gt < agn.getGenes().length; gt++) {
            for (int gp = 0; gp < agn.getGenes()[gt].getPredictors().size(); gp++) {
                if (!nodes.contains(gt)) {
                    nodes.add(gt);
                }
                int predictor = (Integer) agn.getGenes()[gt].getPredictors().get(gp);
                if (!nodes.contains(predictor)) {
                    nodes.add(predictor);
                }
                DefaultEdge edge;
                if (agn.getGenes()[gt].getCfvalues().size() > gp) {
                    edge = new DefaultEdge(agn.getGenes()[gt].getCfvalues().get(gp));
                } else {
                    edge = new DefaultEdge();
                }
                edge.setSource(vertex.get(predictor).getChildAt(0));
                edge.setTarget(vertex.get(gt).getChildAt(0));
                if (edgetype == 1) {
                    GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_SIMPLE);
                } else {
                    GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_NONE);
                }
                GraphConstants.setRouting(edge.getAttributes(), GraphConstants.ROUTING_DEFAULT);
                GraphConstants.setLineStyle(edge.getAttributes(), GraphConstants.STYLE_SPLINE);
                GraphConstants.setLineWidth(edge.getAttributes(), 2);
                GraphConstants.setLineColor(edge.getAttributes(), Color.BLACK);
                edges.add(edge);
            }
        }

        for (int i = 0; i < vertex.size(); i++) {
            if (nodes.contains(i) || (targetindexes != null && targetindexes.contains(i))) {
                DefaultGraphCell node = vertex.get(i);
                AttributeMap attr = node.getAttributes();
                //Rectangle2D rect = GraphConstants.getBounds(attr);
                //float x = agn.getGenes()[i].getX();
                //float y = agn.getGenes()[i].getY();
                //rect.setRect(x, y, rect.getWidth(), rect.getHeight());
                //GraphConstants.setBounds(attr, rect);
                GraphConstants.setAutoSize(attr, true);
                GraphConstants.setConstrained(attr, true);
                attr.put("name", agn.getGenes()[i].getName());
                attr.put("probsetname", agn.getGenes()[i].getProbsetname());
                attr.put("description", agn.getGenes()[i].getDescription());
                attr.put("index", agn.getGenes()[i].getIndex());
                node.setAttributes(attr);
                edges.add(node);
            }
        }

        graph.getGraphLayoutCache().insert(edges.toArray());
        //graph.getGraphLayoutCache().insert(vertex.toArray());
        //graph.getGraphLayoutCache().insert(blueedges.toArray());
        //graph.getGraphLayoutCache().insert(rededges.toArray());
        //graph.getGraphLayoutCache().insert(orangeedges.toArray());
        //graph.setBackground(Color.BLUE);
        //graph.getGraphLayoutCache().insert(edgesvertex.toArray());
        graph.clearSelection();

        String title = "Gene Network";
        if (agn.getTopology() != null) {
            title = title + " (" + agn.getTopology() + " topology)";
        }

        if (dolayout) {
            JGraphFacade facade = new JGraphFacade(graph); // Pass the facade the JGraph instance
            facade.setIgnoresUnconnectedCells(true);
            facade.setDirected(true);
            Rectangle2D area = new Rectangle2D.Float(0, 0, GraphDR.w, GraphDR.h);
            //JGraphLayout layout = new JGraphSelfOrganizingOrganicLayout();
            //JGraphLayout layout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_TILT,GraphDR.w-15,GraphDR.h-100);
            //JGraphLayout layout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_RANDOM,GraphDR.w-15,GraphDR.h-100);
            //JGraphLayout layout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE,GraphDR.w-15,GraphDR.h-150);
            //JGraphLayout layout = new JGraphRadialTreeLayout(); // Create an instance of the appropriate layout
            //JGraphLayout layout = new JGraphFastOrganicLayout(); // Create an instance of the appropriate layout
            JGraphLayout layout = new JGraphOrganicLayout(area); // Create an instance of the appropriate layout
            //JGraphLayout layout = new JGraphTreeLayout(); // Create an instance of the appropriate layout
            layout.run(facade); // Run the layout on the facade.
            facade.scale(area);
            Map nested = facade.createNestedMap(true, true); // Obtain a map of the resulting attribute changes from the facade
            graph.getGraphLayoutCache().edit(nested); // Apply the results to the actual graph
            RefreshGraphPositionsonAGN(agn, graph);
        }
        graph.setBounds(new Rectangle(15, 15, GraphDR.w - 15, GraphDR.h - 100));
        if (show) {
            GraphDR frame = new GraphDR(title, graph, agn);
            frame.setVisible(true);
        }
        return (graph);
    }

    //int edgetype == 1 - ARROW_SIMPLE
    //int edgetype == 2 - ARROW_NONE
    public static JGraph ViewAGN(AGN agn, boolean show, int edgetype) {
        // Construct Model and GraphDR
        GraphModel model = new DefaultGraphModel();
        GraphLayoutCache view = new GraphLayoutCache(model, new DefaultCellViewFactory());
        JGraph graph = new JGraph(model, view);
        // Control-drag should clone selection
        graph.setCloneable(true);
        // Enable edit without final RETURN keystroke
        graph.setInvokesStopCellEditing(true);
        // When over a cell, jump to its default port (we only
        // have one, anyway)
        graph.setJumpToDefaultPort(true);
        // Insert all three cells in one call, so we need an
        // array to store them
        List<DefaultGraphCell> vertex = new ArrayList<DefaultGraphCell>();

        for (int gt = 0; gt < agn.getGenes().length; gt++) {
            String name = String.valueOf(gt);
            if (agn.getGenes()[gt].getName() != null) {
                name = agn.getGenes()[gt].getName() + "(" + String.valueOf(gt) + ")";
            }
            float x = agn.getGenes()[gt].getX();
            float y = agn.getGenes()[gt].getY();
            DefaultGraphCell node = GraphDR.createVertex(name, x, y, 0, 0, Color.WHITE, false, true);
            AttributeMap attr = node.getAttributes();
            attr.put("index", gt);
            node.setAttributes(attr);
            vertex.add(node);
            GraphConstants.setGradientColor(vertex.get(gt).getAttributes(), Color.YELLOW);
        }
        int countedges = 0;
        for (int gt = 0; gt < agn.getGenes().length; gt++) {
            if (agn.getGenes()[gt].getPredictorsties() == null) {
                for (int gp = 0; gp < agn.getGenes()[gt].getPredictors().size(); gp++) {
                    int predictor = (Integer) agn.getGenes()[gt].getPredictors().get(gp);
                    DefaultEdge edge;
                    countedges++;
                    if (agn.getGenes()[gt].getCfvalues().size() > gp) {
                        edge = new DefaultEdge(agn.getGenes()[gt].getCfvalues().get(gp));
                    } else {
                        edge = new DefaultEdge();
                    }
                    edge.setSource(vertex.get(predictor).getChildAt(0));
                    edge.setTarget(vertex.get(gt).getChildAt(0));
                    if (edgetype == 1) {
                        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_SIMPLE);
                    } else {
                        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_NONE);
                    }
                    GraphConstants.setRouting(edge.getAttributes(), GraphConstants.ROUTING_DEFAULT);
                    GraphConstants.setLineStyle(edge.getAttributes(), GraphConstants.STYLE_SPLINE);
                    GraphConstants.setLineWidth(edge.getAttributes(), 2);
                    GraphConstants.setLineColor(edge.getAttributes(), Color.BLACK);
                    vertex.add(edge);
                }
            } else {
                for (int tie = 0; tie < agn.getGenes()[gt].getPredictorsties().length; tie++) {
                    Vector predictorstied = agn.getGenes()[gt].getPredictorsties()[tie];
                    for (int gp = 0; gp < predictorstied.size(); gp++) {
                        int predictor = (Integer) predictorstied.get(gp);
                        DefaultEdge edge;
                        countedges++;
                        if (agn.getGenes()[gt].getCfvalues().size() > gp) {
                            edge = new DefaultEdge(agn.getGenes()[gt].getCfvalues().get(gp));
                        } else {
                            edge = new DefaultEdge();
                        }
                        edge.setSource(vertex.get(predictor).getChildAt(0));
                        edge.setTarget(vertex.get(gt).getChildAt(0));
                        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_SIMPLE);
                        GraphConstants.setRouting(edge.getAttributes(), GraphConstants.ROUTING_DEFAULT);
                        GraphConstants.setLineStyle(edge.getAttributes(), GraphConstants.STYLE_SPLINE);
                        GraphConstants.setLineWidth(edge.getAttributes(), 2);
                        GraphConstants.setLineColor(edge.getAttributes(), Color.BLACK);
                        vertex.add(edge);
                    }
                }
            }
        }

        graph.getGraphLayoutCache().insert(vertex.toArray());
        graph.clearSelection();

        String title = "Gene Network";
        if (agn.getTopology() != null) {
            title = title + " (" + agn.getTopology() + " topology)";
        }
        //layout do grafo, apenas se a rede for != da geografica
        if ((agn.getTopology() == null || !agn.getTopology().equalsIgnoreCase("GG")) && countedges > 0) {
            //Object roots = getRoots(); // replace getRoots with your own
            //Object array of the cell tree roots. NOTE: these are the root cell(s) of the tree(s), not the roots of the graph model.
            JGraphFacade facade = new JGraphFacade(graph); // Pass the facade the JGraph instance
            facade.setIgnoresUnconnectedCells(true);
            facade.setDirected(true);
            Rectangle2D area = new Rectangle2D.Float(15, 15, GraphDR.w - 15, GraphDR.h - 100);
            //JGraphLayout layout = new JGraphSelfOrganizingOrganicLayout();
            //JGraphLayout layout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_TILT,GraphDR.w-15,GraphDR.h-100);
            //JGraphLayout layout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_RANDOM,GraphDR.w-15,GraphDR.h-100);
            JGraphLayout layout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE, GraphDR.w - 15, GraphDR.h - 150);
            //JGraphLayout layout = new JGraphRadialTreeLayout(); // Create an instance of the appropriate layout
            //JGraphLayout layout = new JGraphFastOrganicLayout(); // Create an instance of the appropriate layout
            //JGraphLayout layout = new JGraphOrganicLayout(area); // Create an instance of the appropriate layout
            //JGraphLayout layout = new JGraphTreeLayout(); // Create an instance of the appropriate layout

            layout.run(facade); // Run the layout on the facade.
            facade.scale(area);
            Map nested = facade.createNestedMap(true, true); // Obtain a map of the resulting attribute changes from the facade
            graph.getGraphLayoutCache().edit(nested); // Apply the results to the actual graph
        }
        graph.setBounds(new Rectangle(15, 15, GraphDR.w - 15, GraphDR.h - 100));
        if (countedges > 0) {
            if (show) {
                GraphDR frame = new GraphDR(title, graph, agn);
                frame.setVisible(show);
            }
        } else {
            JOptionPane.showMessageDialog(null, "The method found no relationship to the selected targets.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        return (graph);
    }

    public static float[][] ConcatenateSignalSeparating(float[][] m1, float[][] m2) {
        int lines = m1.length;
        int columns1 = m1[0].length;
        int columns2 = m2[0].length;

        float[][] nm = new float[lines][columns1 + columns2 + 1];

        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < columns1; j++) {
                nm[i][j] = m1[i][j];
            }

            nm[i][columns1] = -999;

            for (int j = columns1; j < (columns1 + columns2); j++) {
                nm[i][j + 1] = m2[i][j - columns1];
            }
        }
        return (nm);
    }

    public static int[][] ConcatenateSignalSeparatingq(int[][] m1, int[][] m2) {
        int lines = m1.length;
        int columns1 = m1[0].length;
        int columns2 = m2[0].length;

        int[][] nm = new int[lines][columns1 + columns2 + 1];

        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < columns1; j++) {
                nm[i][j] = m1[i][j];
            }

            nm[i][columns1] = -999;

            for (int j = columns1; j < (columns1 + columns2); j++) {
                nm[i][j + 1] = m2[i][j - columns1];
            }
        }
        return (nm);
    }

    public static float[][] ConcatenateSignal(float[][] m1, float[][] m2) {
        int lines = m1.length;
        int columns1 = m1[0].length;
        int columns2 = m2[0].length;

        float[][] nm = new float[lines][columns1 + columns2];

        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < columns1; j++) {
                nm[i][j] = m1[i][j];
            }
            for (int j = columns1; j < (columns1 + columns2); j++) {
                nm[i][j] = m2[i][j - columns1];
            }
        }
        return (nm);
    }

    public static int[][] ConcatenateSignalq(int[][] m1, int[][] m2) {
        int lines = m1.length;
        int columns1 = m1[0].length;
        int columns2 = m2[0].length;

        int[][] nm = new int[lines][columns1 + columns2];

        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < columns1; j++) {
                nm[i][j] = m1[i][j];
            }
            for (int j = columns1; j < (columns1 + columns2); j++) {
                nm[i][j] = m2[i][j - columns1];
            }
        }
        return (nm);
    }

    public static void setNameandType(AGN agn, Vector[] nameandtype) {
        for (int g = 0; g < agn.getGenes().length; g++) {
            String name = (String) nameandtype[0].get(g);
            String type = (String) nameandtype[1].get(g);
            agn.getGenes()[g].setName(name);
            agn.getGenes()[g].setType(type);
            //usando o gene new
            //agn.getGenes()[g].setClasse(Integer.valueOf(type));
        }
    }

    public static void setNameandClass(AGN agn, Vector[] nameandclasse) {
        for (int g = 0; g < agn.getGenes().length; g++) {
            String name = (String) nameandclasse[0].get(g);
            String classe = (String) nameandclasse[1].get(g);
            agn.getGenes()[g].setName(name);
            agn.getGenes()[g].setClassname(classe);
        }
    }

    public static void setNameandDescription(AGN agn, Vector[] nameanddescription) {
        for (int g = 0; g < agn.getGenes().length; g++) {
            String name = (String) nameanddescription[0].get(g);
            String description = (String) nameanddescription[1].get(g);
            agn.getGenes()[g].setName(name);
            agn.getGenes()[g].setDescription(description);
        }
    }

    public static void setGeneNames(AGN agn, Vector names) {
        if (agn.getNrgenes() == names.size()) {
            for (int g = 0; g < agn.getGenes().length; g++) {
                String name = (String) names.get(g);
                agn.getGenes()[g].setName(name);
                agn.getGenes()[g].setProbsetname(name);
            }
        } else {
            System.out.println("Error on labeling genes, size does not match.");
        }
    }

    public static void setGeneIds(AGN agn, Vector ids) {
        if (agn.getNrgenes() == ids.size()) {
            try {
                for (int g = 0; g < agn.getGenes().length; g++) {
                    String strid = (String) ids.get(g);
                    int iid = Integer.parseInt(strid);
                    agn.getGenes()[g].setGeneid(iid);
                }
            } catch (NumberFormatException erro) {
                System.out.println("Error when converting labels to Gene Id.");
            }
        } else {
            System.out.println("Error on labeling genes, size does not match.");
        }
    }

    public static Vector getGeneNames(AGN agn) {
        Vector names = new Vector();
        for (int g = 0; g < agn.getGenes().length; g++) {
            String name = agn.getGenes()[g].getName();
            names.add(g, name);
        }
        return (names);
    }

    public static void setPSNandClass(AGN agn, Vector[] nameandtype) {
        for (int g = 0; g < agn.getGenes().length; g++) {
            String psn = (String) nameandtype[0].get(g);
            StringTokenizer s = new StringTokenizer(psn, String.valueOf('"'));
            psn = s.nextToken();
            int classe = Integer.valueOf(((String) nameandtype[1].get(g)));
            agn.getGenes()[g].setProbsetname(psn);
            agn.getGenes()[g].setClassnumber(classe);
        }
    }

    public static void setNameandPSN(AGN agn, Vector[] nameandtype) {
        for (int g = 0; g < agn.getGenes().length; g++) {
            String psn = (String) nameandtype[0].get(g);
            String name = (String) nameandtype[1].get(g);
            agn.getGenes()[g].setProbsetname(psn);
            agn.getGenes()[g].setName(name);
        }
    }

    /*METODO PARA GERAR ARQUIVO DE TREINAMENTO, ASSUMINDO QUE:
    CADA GENE ESTA EM UMA COLUNA.
    AS OBSERVACOES DOS GENES (INSTANTES DE TEMPO) ESTAO DISPOSTOS NAS LINHAS.
    O METODO RETIRA O GENE DA COLUNA ONDE ESTA ALOCADO E O INSERE NA ULTIMA
    COLUNA, SENDO O VALOR DA SUA PRIMEIRA LINHA DESLOCADO PARA A ULTIMA LINHA.*/
    public static char[][] MakeTemporalTrainingSet(
            int[][] quantizeddata,
            int target,
            boolean isPeriodic) {
        int rowsoriginal = quantizeddata.length;
        int colsoriginal = quantizeddata[0].length;
        int rowsts = colsoriginal;

        //verificacao se o sinal eh periodico.
        if (!isPeriodic) {
            rowsts--;//desconsidera a observacao da ultima coluna tentando predizer a primeira.
        }

        char[][] trainingset = new char[rowsts][rowsoriginal];

        //fill the columns before target row
        for (int row = 0; row < target; row++) {
            for (int col = 0; col < rowsts; col++) {
                int temp = (int) quantizeddata[row][col];
                trainingset[col][row] = (char) temp;
            }
        }

        //fill the columns after target row
        for (int row = target + 1; row < rowsoriginal; row++) {
            for (int col = 0; col < rowsts; col++) {
                int temp = (int) quantizeddata[row][col];
                trainingset[col][row - 1] = (char) temp;
            }
        }

        //fill the target row in the last column of the training set.
        for (int col = 1; col <= rowsts; col++) {
            int temp = (int) quantizeddata[target][col % colsoriginal];
            trainingset[col - 1][rowsoriginal - 1] = (char) temp;
        }

        //verificar quais linhas possuem caracter -999 e exclui-las da matriz.
        Vector rowsfr = new Vector();
        for (int i = 0; i < trainingset.length; i++) {
            boolean remove = false;
            for (int j = 0; j < trainingset[0].length && !remove; j++) {
                if (trainingset[i][j] == (char) Preprocessing.skipvalue) {
                    remove = true;
                }
            }
            if (remove) {
                rowsfr.add(i);
            }
        }

        if (rowsfr.size() > 0) {
            char[][] newtrainingset = new char[trainingset.length - rowsfr.size()][trainingset[0].length];
            int newrow = 0;
            for (int i = 0; i < trainingset.length; i++) {
                if (rowsfr.contains(i)) {
                    newrow++;
                    continue;
                }
                for (int j = 0; j < trainingset[0].length; j++) {
                    newtrainingset[i - newrow][j] = trainingset[i][j];
                }
            }
            trainingset = newtrainingset;
        }
        return (trainingset);
    }

    /*METODO PARA GERAR ARQUIVO DE TREINAMENTO, ASSUMINDO QUE:
    CADA GENE ESTA EM UMA LINHA.
    AS OBSERVACOES DOS GENES (INSTANTES DE TEMPO) ESTAO DISPOSTOS NAS COLUNAS.
    O METODO RETIRA O GENE DA LINHA ONDE ESTA ALOCADO E O INSERE NA ULTIMA
    COLUNA, SENDO O VALOR DA SUA PRIMEIRA LINHA DESLOCADO PARA A ULTIMA LINHA.*/
    public static char[][] MakeSteadyStateTrainingSet(int[][] quantizeddata, int target) {
        int rowsoriginal = quantizeddata.length;
        int colsoriginal = quantizeddata[0].length;
        int rowsts = colsoriginal;
        char[][] trainingset = new char[rowsts][rowsoriginal];
        //fill the columns before target row
        for (int row = 0; row < target; row++) {
            for (int col = 0; col < rowsts; col++) {
                int temp = (int) quantizeddata[row][col];
                trainingset[col][row] = (char) temp;
            }
        }
        //fill the columns after target row
        for (int row = target + 1; row < rowsoriginal; row++) {
            for (int col = 0; col < rowsts; col++) {
                int temp = quantizeddata[row][col];
                trainingset[col][row - 1] = (char) temp;
            }
        }
        //fill the target row in the last column of the training set.
        for (int col = 0; col < rowsts; col++) {
            int temp = quantizeddata[target][col];
            trainingset[col][rowsoriginal - 1] = (char) temp;
        }

        //verificar quais linhas possuem caracter -999 e exclui-las da matriz.
        Vector rowsfr = new Vector();
        for (int i = 0; i < trainingset.length; i++) {
            boolean remove = false;
            for (int j = 0; j < trainingset[0].length && !remove; j++) {
                if (trainingset[i][j] == (char) Preprocessing.skipvalue) {
                    remove = true;
                }
            }
            if (remove) {
                rowsfr.add(i);
            }
        }

        if (rowsfr.size() > 0) {
            char[][] newtrainingset = new char[trainingset.length - rowsfr.size()][trainingset[0].length];
            int newrow = 0;
            for (int i = 0; i < trainingset.length; i++) {
                if (rowsfr.contains(i)) {
                    newrow++;
                    continue;
                }
                for (int j = 0; j < trainingset[0].length; j++) {
                    newtrainingset[i - newrow][j] = trainingset[i][j];
                }
            }
            trainingset = newtrainingset;
        }
        return (trainingset);
    }

    //adiciona um novo sinal a um sinal menor jah existente na rede.
    public static void addTemporalSignalq(AGN agn) {
        int[][] temporalsignalq = agn.getTemporalsignalquantized();
        if (temporalsignalq == null) {
            //cria a matriz para armazenar os dados temporais
            CreateTemporalSignalq(agn);
        } else {
            //cria a matriz para armazenar os dados temporais
            int[][] newtemporalsignalq = new int[agn.getNrgenes()][agn.getSignalsize()];
            int currentsize = temporalsignalq[0].length;
            //copia os dados atuais.
            for (int time = 0; time < currentsize; time++) {
                for (int gene = 0; gene < agn.getNrgenes(); gene++) {
                    newtemporalsignalq[gene][time] = temporalsignalq[gene][time];
                }
            }
            //cria a adicao do sinal, observando os preditores e as funcoes booleanas associadas a ele.
            for (int time = currentsize; time < agn.getSignalsize(); time++) {
                for (int target = 0; target < agn.getNrgenes(); target++) {
                    //gera o valor do gene target no tempo time, observando seus preditores
                    //e funcoes booleanas associadas a ele no instante de tempo time-1.
                    boolean genevalue = Simulation.ApplyProbabilisticLogicalCircuit(agn, target, time - 1, newtemporalsignalq);
                    int gv = MathRoutines.Boolean2Int(genevalue);
                    newtemporalsignalq[target][time] = gv;
                }
            }
            agn.setTemporalsignalquantized(newtemporalsignalq);
        }
    }

    //cria um novo sinal
    public static void CreateTemporalSignalq(AGN agn) {
        //se existe um sinal ja gerado, continua a partir dele.
        int[][] temporalsignal = agn.getTemporalsignalquantized();

        if (temporalsignal == null) {
            //senao cria a matriz para armazenar os dados temporais
            temporalsignal = new int[agn.getNrgenes()][agn.getSignalsize()];
            //inicializa o primeiro instante de tempo com os valores atuais dos genes.
            for (int i = 0; i < agn.getNrgenes(); i++) {
                temporalsignal[i][0] = (int) agn.getGenes()[i].getValue();
            }
            //cria o sinal, observando os preditores e as funcoes booleanas associadas a ele.
            for (int time = 1; time < agn.getSignalsize(); time++) {
                for (int target = 0; target < agn.getNrgenes(); target++) {
                    //gera o valor do gene target no tempo time, observando seus preditores
                    //e funcoes booleanas associadas a ele no instante de tempo time-1.
                    boolean genevalue = Simulation.ApplyProbabilisticLogicalCircuit(agn, target, time - 1, temporalsignal);
                    int gv = MathRoutines.Boolean2Int(genevalue);
                    temporalsignal[target][time] = gv;
                }
            }
        } else {
            int[][] newtemporalsignal = new int[agn.getNrgenes()][agn.getSignalsize()];
            for (int time = 0; time < temporalsignal[0].length; time++) {
                for (int target = 0; target < agn.getNrgenes(); target++) {
                    newtemporalsignal[target][time] = temporalsignal[target][time];
                }
            }
            for (int time = temporalsignal[0].length; time < agn.getSignalsize(); time++) {
                for (int target = 0; target < agn.getNrgenes(); target++) {
                    boolean genevalue = Simulation.ApplyProbabilisticLogicalCircuit(agn, target, time - 1, newtemporalsignal);
                    int gv = MathRoutines.Boolean2Int(genevalue);
                    newtemporalsignal[target][time] = gv;
                }
            }
            temporalsignal = newtemporalsignal;
        }
        agn.setTemporalsignalquantized(temporalsignal);
    }

    public static void CreateSignalInitializations(AGN agn, boolean separatesignal) {
        //armazena os valores iniciais dos genes da rede.
        float[] initialvalues = agn.getInitialValues();
        //cria o sinal temporal a partir dos valores iniciais dos genes e
        //aplicacao das funcoes e preditores associados a eles.
        //AGNRoutines.CreateTemporalSignal(agn);
        //gera o sinal de expressao simulado.
        int[][] generated_data = agn.getTemporalsignalquantized();
        //inicializa a geracao de pseudo-aleatorios utilizando a hora do sistema como semente.
        Random rn = new Random(System.nanoTime());
        //gera novas inicializacoes (concatenacoes de sinal).
        for (int concat = 1; concat <= agn.getNrinitializations(); concat++) {
            float[] newinitialvalues = new float[agn.getNrgenes()];
            for (int i = 0; i < agn.getNrgenes(); i++) {
                newinitialvalues[i] = rn.nextInt(agn.getQuantization());
            }
            agn.setInitialValues(newinitialvalues);
            AGNRoutines.CreateTemporalSignalq(agn);
            int[][] otherinicializationdata = agn.getTemporalsignalquantized();
            //concatena os dados gerados pelo estado inicial anterior e o novo estado inicial.
            if (separatesignal) {
                generated_data = AGNRoutines.ConcatenateSignalSeparatingq(generated_data, otherinicializationdata);
            } else {
                generated_data = AGNRoutines.ConcatenateSignalq(generated_data, otherinicializationdata);
            }
        }
        //retorna os valores iniciais originais aos genes.
        agn.setInitialValues(initialvalues);
        agn.setTemporalsignalquantized(generated_data);
    }

    //datatype: 1==temporal, 2==steady-state.
    //stacksize, used only for SFFS_stack
    public static StringBuffer RecoverNetworkfromExpressions(
            AGN recoveredagn, AGN originalagn, int datatype,
            boolean isPeriodic, float threshold_entropy,
            String type_entropy, float alpha, float beta, float q_entropy,
            Vector targets, int maxfeatures, int searchalgorithm,
            boolean targetaspredictors, int resultsetsize,
            String tiesout, int stacksize) {
        StringBuffer txt = new StringBuffer();
        StringBuffer txtParcial = new StringBuffer();
        int rows = recoveredagn.getTemporalsignalquantized().length;
        txtParcial.append("\n\n"); 
        System.out.print(txtParcial);
        txt.append(txtParcial);
        //Caso o vetor targets seja nulo, e assumido que todas
        //as linhas da matriz serao targets.
        if (targets == null) {
            targets = new Vector();
            for (int i = 0; i < rows; i++) {
                targets.add(i);
            }
        }

        //add target information to recovered AGN.
        recoveredagn.setTargets(targets);
        
        // aqui começa a execução em paralelo
        long init;  
        long end;  
        long diff;  
        
        init = System.currentTimeMillis();
        LogExecucao.gravar("\n" + init);
        
        

        int countvar = 0;
        int countnvar = 0;
        for (int ig = 0; ig < targets.size(); ig++) {
            //algoritmo para montar os conjuntos de treinamento assumindo que
            // os periodos de tempo sao continuos/periodicos.
            //IOFile.printMatrix(M);
            int targetindex = (Integer) targets.get(ig);
            Vector predictors = new Vector();
            Vector ties = new Vector();
            //IOFile.printMatrix(recoveredagn.getTemporalsignalquantized());
            char[][] strainingset;
            if (datatype == 1) {
                strainingset = MakeTemporalTrainingSet(recoveredagn.getTemporalsignalquantized(), targetindex, isPeriodic);
            } else {
                strainingset = MakeSteadyStateTrainingSet(recoveredagn.getTemporalsignalquantized(), targetindex);
            }
            //IOFile.printMatrix(strainingset);
            /* SELETOR DE CARACTERISTICAS PARA O TREINAMENTO. */
            FS fs = new FS(strainingset, recoveredagn.getQuantization(),
                    recoveredagn.getQuantization(),
                    type_entropy, alpha, beta, q_entropy, resultsetsize);
            if (CNMeasurements.hasVariation(strainingset)) {
                countvar++;
                //IOFile.printMatrix(strainingset);
                //System.out.println("\nTarget = " + gt);
                /*
                n: number of possible values for features
                c: number of possible classes
                NO CASO DA GERACAO DAS REDES, ASSUMIMOS QUE N=C=2.
                 */
                if (searchalgorithm == 1) {
                    fs.runSFS(false, maxfeatures);
                } else if (searchalgorithm == 2) {
                    fs.runSFFS(maxfeatures, targetindex, recoveredagn);
                } else if (searchalgorithm == 3) {
                    //implementacao do SFFS usando pilha de execucao para expandir todos os
                    //empates identificados.
                    fs.runSFFS_stack(maxfeatures, targetindex, targetindex, recoveredagn, originalagn, stacksize, null, null);
                } else if (searchalgorithm == 9) {
                    fs.runSFS(true, maxfeatures); /* a call to SFS is made in order to get the
                    //ideal dimension to run the exhaustive search;*/
                    int itmax = fs.itmax;
                    FS fsPrev = new FS(strainingset, recoveredagn.getQuantization(), recoveredagn.getQuantization(), type_entropy, alpha, beta,
                            q_entropy, resultsetsize);
                    for (int i = 1; i <= itmax; i++) {
                        fs = new FS(strainingset, recoveredagn.getQuantization(), recoveredagn.getQuantization(), type_entropy, alpha, beta,
                                q_entropy, resultsetsize);
                        fs.itmax = i;
                        fs.runExhaustive(0, 0, fs.I);
                        if (fs.hGlobal < fsPrev.hGlobal) {
                            fsPrev = fs;
                        } else {
                            fs = fsPrev;
                            break;
                        }
                    }
                }
                if (targetaspredictors) {
                    txt.append("Predictor: " + (targetindex) + " name:" + recoveredagn.getGenes()[targetindex].getName() + "\nTargets: ");
                    System.out.print("Predictor: " + (targetindex) + " name:" + recoveredagn.getGenes()[targetindex].getName() + "\nTargets: ");
                } else {
                    txt.append("Target: " + (targetindex) + " name:" + recoveredagn.getGenes()[targetindex].getName() + "\nPredictors: ");
                    System.out.print("\nTarget: " + (targetindex) + " name:" + recoveredagn.getGenes()[targetindex].getName() + "\nPredictors: ");
                }
                for (int i = 0; i < fs.I.size(); i++) {
                    int predictorindex = Integer.valueOf(fs.I.elementAt(i).toString());
                    if (predictorindex >= targetindex) {
                        predictorindex++;
                    }
                    if (fs.hGlobal <= threshold_entropy) {
                        //int predictor_gene = Integer.valueOf(fs.I.elementAt(i).toString());
                        //if (geneid != null)
                        //    predictor_gene = (Integer) geneid[predictor_gene].get(1);
                        txt.append(predictorindex + " name:" + recoveredagn.getGenes()[predictorindex].getName() + " ");
                        System.out.print(predictorindex + " name:" + recoveredagn.getGenes()[predictorindex].getName() + " ");
                        predictors.add(predictorindex);

                        //armazena os preditores encontrados para os targets na rede recuperada.
                        recoveredagn.getGenes()[targetindex].addPredictor(predictorindex, fs.hGlobal);
                        recoveredagn.getGenes()[predictorindex].addTarget(targetindex);
                        recoveredagn.getGenes()[targetindex].setProbtable(fs.probtable);

                    } else {
                        if (targetaspredictors) {
                            txt.append("\ntarget " + predictorindex + " excluded by threshold. Criterion Function Value = " + fs.hGlobal);
                            System.out.print("\ntarget " + predictorindex + " excluded by threshold. Criterion Function Value = " + fs.hGlobal);
                        } else {
                            txt.append("\npredictor " + predictorindex + " excluded by threshold. Criterion Function Value = " + fs.hGlobal);
                            System.out.print("\npredictor " + predictorindex + " excluded by threshold. Criterion Function Value = " + fs.hGlobal);
                        }
                    }
                }
                int s = fs.I.size();
                if (fs.hGlobal <= threshold_entropy) {
                    if (fs.ties != null && fs.ties[s] != null && fs.ties[s].size() > 1) {
                        //detectou empate entre grupos de preditores.
                        txt.append("\nPredictors Ties: ");
                        System.out.print("\nPredictors Ties: ");

                        //vetor para armazenar os preditores que empataram ao predizer o target.
                        //cada posicao do vetor, tambem eh um vetor contendo um conjunto de preditores (indices inteiros).
                        Vector[] predictorsties = new Vector[fs.ties[s].size()];
                        //LIMITACAO DE IMPRESSAO DOS 30 PRIMEIROS CONJUNTOS DE PREDITORES EMPADATOS
                        for (int j = 0; j < fs.ties[s].size() && j < 30; j++) {
                            //inicializacao de cada posicao do vetor com um novo vetor.
                            predictorsties[j] = new Vector();

                            //armazena os valores empatados apenas com entropia
                            //igual a melhor resposta.
                            Vector item = (Vector) fs.ties[s].get(j);
                            Vector tie = new Vector();
                            for (int k = 0; k < item.size(); k++) {
                                int predictorindex = (Integer) item.get(k);
                                if (predictorindex >= targetindex) {
                                    predictorindex++;
                                }
                                //armazena os targets no registro dos seus preditores.
                                recoveredagn.getGenes()[predictorindex].addTarget(targetindex);
                                //adiciona o indice do preditor empatado ao conjunto.
                                predictorsties[j].add(k, predictorindex);
                                txt.append(predictorindex + " name:" + recoveredagn.getGenes()[predictorindex].getName() + " ");
                                System.out.print(predictorindex + " name:" + recoveredagn.getGenes()[predictorindex].getName() + " ");
                                tie.add(predictorindex);
                            }
                            //System.out.print(" (" + fs.jointentropiesties[j] + ") ");
                            txt.append("\t");
                            System.out.print("\t");
                            ties.add(tie);
                        }
                        recoveredagn.getGenes()[targetindex].setPredictorsties(predictorsties);
                    }
                }
            } else {
                if (targetaspredictors) {
                    System.out.print("Predictor " + targetindex + " name " + recoveredagn.getGenes()[targetindex].getName() + ", has no variation on its values.");
                    txt.append("Predictor " + targetindex + " name " + recoveredagn.getGenes()[targetindex].getName() + ", has no variation on its values.");
                } else {
                    System.out.print("Target " + targetindex + " name " + recoveredagn.getGenes()[targetindex].getName() + ", has no variation on its values.");
                    txt.append("Target " + targetindex + " name " + recoveredagn.getGenes()[targetindex].getName() + ", has no variation on its values.");
                }
                countnvar++;
                int lastcol = strainingset[0].length - 1;
                int genestate = (int) strainingset[0][lastcol];
                System.out.print("\nEstado constante do Gene = " + genestate);
            }
            if (tiesout != null) {
                //CODIGO PARA GERACAO DOS RESULTADOS EMPATADOS.
                Vector originalpredictors = null;
                int avgedges = 0;
                int signalsize = recoveredagn.getSignalsize();
                String topology = "";
                if (originalagn != null) {
                    originalpredictors = originalagn.getGenes()[targetindex].getPredictors();
                    avgedges = (int) originalagn.getAvgedges();
                    topology = originalagn.getTopology();
                }
                //tratamento dos resultados com empates.
                IOFile.writeTies(
                        originalagn,
                        tiesout,
                        targetindex,
                        avgedges,
                        signalsize,
                        topology,
                        originalpredictors,
                        q_entropy,
                        searchalgorithm,
                        predictors,
                        ties,
                        fs.hGlobal,
                        false);
            }
            System.out.println("\nCriterion Function Value: " + fs.hGlobal);
            txt.append("\nCriterion Function Value: " + fs.hGlobal + "\n");
            System.out.print("\n");
            txt.append("\n");
        }
        //System.out.print("\n");
        //txt.append("\n");
        System.out.println("Nr de genes alvos considerados = " + countvar);
        System.out.println("Nr de genes alvos NAO considerados = " + countnvar);
        
        end = System.currentTimeMillis();  
        diff = end - init;  
        LogExecucao.gravar("\t" + end);
        LogExecucao.gravar("\t" + diff + " milisegundos\t " + (diff / 1000) + " segundos\n");
        System.out.println("Demorou " + diff + " milisegundos");  
        
        return (txt);
    }

    public static int[] FindHubs(AGN agn) {
        int nrhubs = agn.getNrgenes() / 10; //assuming that 10% of genes are hubs.
        int[] ihubs = new int[nrhubs];

        int[] genesdegree = new int[agn.getNrgenes()];
        for (int i = 0; i < agn.getNrgenes(); i++) {
            genesdegree[i] = agn.getGenes()[i].getPredictors().size() + agn.getGenes()[i].getTargets().size();
        }
        int[] orderedindexes = Preprocessing.BubbleSortDEC(genesdegree);
        for (int i = 0; i < nrhubs; i++) {
            ihubs[i] = orderedindexes[i];
        }
        return (ihubs);
    }

    //a informacao do target pode ser o Locus ou o probsetname.
    public static void FindIndexesandSetClasses(
            AGN agn,
            Vector[] namesandclasses,
            Vector targetindexes) {

        Vector cclasses = new Vector();
        cclasses.add(Color.GRAY);//cor dos genes que nao apresentam classes associadas.
        Vector sclasses = new Vector();
        sclasses.add("null");//para os genes que nao possuem classe associada a ele.

        for (int ind = 0; ind < namesandclasses.length; ind++) {
            boolean found = false;
            String tclasse = (String) namesandclasses[ind].get(0);//gene class name
            String tname = (String) namesandclasses[ind].get(1);//gene name
            int g = 0;
            int gindex = -1;
            String name = "name";
            int classnumber = -1;
            Color cor = null;
            while (!found && g < agn.getGenes().length) {
                if (tname.equalsIgnoreCase(agn.getGenes()[g].getProbsetname()) || tname.equalsIgnoreCase(agn.getGenes()[g].getLocus())
                        || tname.equalsIgnoreCase(agn.getGenes()[g].getName())) {
                    if (!sclasses.contains(tclasse)) {
                        classnumber = sclasses.size();
                        sclasses.add(tclasse);
                        cor = getRandomColor();
                        cclasses.add(cor);
                    } else {
                        for (int c = 0; c < sclasses.size(); c++) {
                            if (((String) sclasses.elementAt(c)).equalsIgnoreCase(tclasse)) {
                                classnumber = c;
                                cor = (Color) cclasses.elementAt(c);
                            }
                        }
                    }
                    if (agn.getGenes()[g].getClassname().equalsIgnoreCase("null")) {
                        //atribui uma classe ao gene uma unica vez
                        agn.getGenes()[g].setClassname(tclasse);
                        agn.getGenes()[g].setClassnumber(classnumber);
                        agn.getGenes()[g].setColor(cor);
                        gindex = agn.getGenes()[g].getIndex();
                    }
                    found = true;
                } else {
                    g++;
                }
            }
            int index = (Integer) ((Vector) targetindexes.get(ind)).get(0);
            //int index = (Integer) targetindexes.get(ind);
            if (found && index == -1) {
                //targetindexes.setElementAt(g, ind);
                ((Vector) targetindexes.get(ind)).setElementAt(gindex, 0);
                ((Vector) targetindexes.get(ind)).setElementAt(tclasse, 1);
                ((Vector) targetindexes.get(ind)).setElementAt(classnumber, 2);
                ((Vector) targetindexes.get(ind)).setElementAt(tname, 3);
            }
        }

        int[] palette = new int[cclasses.size()];
        for (int i = 0; i < palette.length; i++) {
            palette[i] = ((Color) cclasses.get(i)).getRGB();
        }
        agn.setPalette(palette);
    }

    //a informacao do target pode ser o Locus ou o probsetname.
    public static void FindIndexes(
            AGN agn,
            String[] targetinfo,
            Vector targetindexes,
            Vector mapindexes) {
        for (int ind = 0; ind < targetinfo.length; ind++) {
            boolean found = false;
            int g = 0;
            String tinf = targetinfo[ind];
            int classe = -1;
            int gindex = -1;
            int originalindex = -1;
            String name = "name";
            while (!found && g < agn.getNrgenes()) {
                if (tinf.equalsIgnoreCase(agn.getGenes()[g].getProbsetname()) || tinf.equalsIgnoreCase(agn.getGenes()[g].getLocus())) {
                    found = true;
                    classe = agn.getGenes()[g].getClassnumber();
                    if (mapindexes != null) {
                        gindex = FindFilteredIndex(mapindexes, agn.getGenes()[g].getIndex());
                    } else {
                        gindex = agn.getGenes()[g].getIndex();
                    }
                    name = agn.getGenes()[g].getName();
                    originalindex = agn.getGenes()[g].getIndex();
                } else {
                    g++;
                }
            }
            int index = (Integer) ((Vector) targetindexes.get(ind)).get(0);
            //int index = (Integer) targetindexes.get(ind);
            if (found && index == -1) {
                //targetindexes.setElementAt(g, ind);
                ((Vector) targetindexes.get(ind)).setElementAt(gindex, 0);
                ((Vector) targetindexes.get(ind)).setElementAt(classe, 1);
                ((Vector) targetindexes.get(ind)).setElementAt(originalindex, 2);
                ((Vector) targetindexes.get(ind)).setElementAt(name, 3);
            }
        }
    }

    public static void AddAffymetrixInformation(AGN network, String pathinputfile) throws IOException {
        Vector collumns = new Vector(9);
        for (int i = 0; i < 9; i++) {
            collumns.add(i);
        }
        Vector[] geneinformations = IOFile.readDataCollumns(pathinputfile, 1, collumns, "\t");

        //debug
        //for (int i = 0; i < geneinformations.length; i++) {
        //    if (geneinformations[i].size() > 0) {
        //        System.out.println((String) geneinformations[i].get(0));
        //    }
        //}
        //fim-debug

        for (int g = 0; g < network.getNrgenes(); g++) {
            Gene gene = network.getGenes()[g];
            gene.setIndex(g);
            String geneprobsetname = gene.getProbsetname();
            for (int i = 0; i < geneinformations[0].size(); i++) {
                if (((String) geneinformations[0].get(i)).equalsIgnoreCase(geneprobsetname)) {
                    //System.out.println((String)geneinformations[0].get(i));
                    //achou a referencia
                    String probsetname = (String) geneinformations[0].get(i);
                    String arrayelementtype = (String) geneinformations[1].get(i);
                    String organism = (String) geneinformations[2].get(i);
                    String iscontrol = (String) geneinformations[3].get(i);
                    String locus = (String) geneinformations[4].get(i);
                    String description = (String) geneinformations[5].get(i);
                    String chromosometype = (String) geneinformations[6].get(i);
                    String start = (String) geneinformations[7].get(i);
                    String stop = (String) geneinformations[8].get(i);

                    gene.setProbsetname(probsetname);
                    gene.setType(arrayelementtype);
                    gene.setLocus(locus);
                    gene.setOrganism(organism);
                    gene.setDescription(description);
                    if (iscontrol.equalsIgnoreCase("no")) {
                        gene.setControl(false);
                    } else if (iscontrol.equalsIgnoreCase("yes")) {
                        gene.setControl(true);
                    }
                    if (isNumber(start)) {
                        gene.setStart(Integer.valueOf(start));
                    }
                    if (isNumber(stop)) {
                        gene.setStop(Integer.valueOf(stop));
                    }
                    gene.setChromosometype(chromosometype);

                    /*
                    if (!description.equalsIgnoreCase("no_match")) {
                    //quebrar a descricao e atribuir o inicio como nome do gene.
                    String breakdescription = String.valueOf(';') + String.valueOf(')');
                    StringTokenizer s = new StringTokenizer(description, breakdescription, true);
                    String name = s.nextToken();
                    boolean par = false;
                    for (int c = 0; c < name.length(); c++) {
                    if (name.charAt(c) == '(') {
                    par = true;
                    c = name.length();
                    }
                    }
                    if (par) {
                    name = name + ")";
                    }
                    //System.out.println(name);
                    gene.setName(name);
                    } else {
                    gene.setName(probsetname);
                    }*/
                    i = geneinformations[0].size();
                }
            }
        }
        //AGNRoutines.ViewAGNOLD(network);
        //IOFile.WriteAGNnewtoFile(network, pathnetwork + "new");
    }

    public static boolean isNumber(String str) {
        boolean bool = false;
        try {
            int num = Integer.parseInt(str);
            bool = true;
        } catch (NumberFormatException exception) {
            bool = false;
        }
        return bool;
    }

    public static void AddNCBIInformation(AGN network, String pathinputfile) throws IOException {
        String delimiter = String.valueOf(' ') + String.valueOf(',') + String.valueOf('=') + String.valueOf('.') + String.valueOf(')') + String.valueOf('(') + String.valueOf('\t') + String.valueOf('\n') + String.valueOf('\r') + String.valueOf('\f') + String.valueOf(';');
        String delimitersp = String.valueOf(' ') + String.valueOf('=') + String.valueOf('\t') + String.valueOf('\n') + String.valueOf('\r') + String.valueOf('\f') + String.valueOf(';');
        int start = -1;
        int stop = -1;
        String genename = null;
        String synonyms = "";
        String function = "";
        String locus = "";
        int geneid = -1;
        String notes = "";
        String product = "";
        String proteinid = "";
        int chromosome = -1;
        boolean found = false;
        int notetype = 0;
        int synonymscount = 0;
        int genecount = 0;
        Gene gene = null;
        BufferedReader br = IOFile.openBufferedReader(pathinputfile);
        while (br.ready()) {
            StringTokenizer s = new StringTokenizer(br.readLine(), delimiter);
            if (s.countTokens() > 0) {
                String token = s.nextToken();
                //DEBUG
                //System.out.println(token);
                //FIM-DEBUG

                if (token.equalsIgnoreCase("gene")) {
                    if (found) {
                        //complete the informations
                        System.out.println("Arabidopsis thaliana");
                        gene.setOrganism("Arabidopsis thaliana");
                        gene.setStart(start);
                        gene.setStop(stop);
                        gene.setName(genename);
                        gene.setSynonyms(synonyms);
                        gene.setFunction(function);
                        gene.setLocus(locus);
                        gene.setGeneid(geneid);
                        gene.setDescription(notes);
                        gene.setProduct(product);
                        gene.setProteinid(proteinid);
                        gene.setChromosome(chromosome);
                        //DEBUG
                        //System.out.println("Found Locus = " + gene.getLocus() + " | ProbSetName = " + gene.getProbsetname() + " | Name = " + gene.getName() + " | index = " + gene.getIndex());
                        //FIM-DEBUG
                        found = false;
                        start = -1;
                        stop = -1;
                        genename = null;
                        synonyms = "";
                        function = "";
                        locus = "";
                        geneid = -1;
                        notes = "";
                        product = "";
                        proteinid = "";
                    }
                    genecount++;
                    while (s.hasMoreTokens()) {
                        String subtoken = s.nextToken();
                        if (subtoken.equalsIgnoreCase("complement")) {
                            start = Integer.valueOf(s.nextToken());
                            stop = Integer.valueOf(s.nextToken());
                        } else if (isNumber(subtoken)) {
                            start = Integer.valueOf(subtoken);
                            if (s.hasMoreTokens()) {
                                subtoken = s.nextToken();
                                if (isNumber(subtoken)) {
                                    stop = Integer.valueOf(subtoken);
                                }
                            }
                        }
                    }
                } else if (token.equalsIgnoreCase("mRNA")) {
                    notetype = 1;
                } else if (token.equalsIgnoreCase("CDS")) {
                    notetype = 2;
                } else if (token.equalsIgnoreCase("/function")) {
                    function = s.nextToken(String.valueOf('='));
                    token = "";//inicializazao para entrar no while.
                    do {
                        s = new StringTokenizer(br.readLine(), delimitersp);
                        while (s.hasMoreTokens() && !token.startsWith("/")) {
                            token = s.nextToken();
                            if (!token.startsWith("/")) {
                                function += " " + token;
                            }
                        }
                    } while (!token.startsWith(String.valueOf("/")));
                    //System.out.println("Function == " + function);
                } else if (token.equalsIgnoreCase("/note") && notetype == 2) {
                    //considera apenas as anotacoes da CoDing Sequence (CDS).
                    notes = s.nextToken(String.valueOf('='));
                    token = "";//inicializazao para entrar no while.
                    do {
                        s = new StringTokenizer(br.readLine(), delimitersp);
                        while (s.hasMoreTokens() && !token.startsWith("/")) {
                            token = s.nextToken();
                            if (!token.startsWith("/")) {
                                notes += " " + token;
                            }
                        }
                    } while (!token.startsWith(String.valueOf("/")));
                    //System.out.println("Notes == " + notes);
                } else if (token.equalsIgnoreCase("/product")) {
                    product = s.nextToken(String.valueOf('='));
                    token = "";//inicializazao para entrar no while.
                    do {
                        s = new StringTokenizer(br.readLine(), delimitersp);
                        while (s.hasMoreTokens() && !token.startsWith("/")) {
                            token = s.nextToken();
                            if (!token.startsWith("/")) {
                                product += " " + token;
                            }
                        }
                    } while (!token.startsWith(String.valueOf("/")));
                    //System.out.println("Product == " + product);
                } else if (token.equalsIgnoreCase("/gene")) {
                    genename = s.nextToken(String.valueOf('"') + String.valueOf('"') + String.valueOf('='));
                    //System.out.println(genename);
                } else if (token.equalsIgnoreCase("/protein_id")) {
                    proteinid = s.nextToken(String.valueOf('"') + String.valueOf('"') + String.valueOf('='));
                    //System.out.println(proteinid);
                } else if (token.equalsIgnoreCase("/locus_tag")) {
                    locus = s.nextToken(String.valueOf('"') + String.valueOf('"') + String.valueOf('='));
                    if (!found) {
                        int g = 0;
                        while (g < network.getNrgenes() && !found) {
                            gene = network.getGenes()[g];
                            String genelocus = gene.getLocus();
                            if (genelocus != null) {
                                //correction of locus information
                                //in some cases, locus has two stick entries, separated by semicolon
                                StringTokenizer st = new StringTokenizer(genelocus, String.valueOf(' ') + String.valueOf(';'));
                                genelocus = st.nextToken();
                                gene.setLocus(genelocus);
                            }
                            if (locus.equalsIgnoreCase(gene.getLocus())) {
                                found = true;
                            } else {
                                g++;
                            }
                        }
                    }
                    //System.out.println(locus);
                    synonymscount = 0;
                } else if (token.equalsIgnoreCase("/gene_synonym")) {
                    if (synonymscount == 0) {
                        synonyms = s.nextToken(String.valueOf('"') + String.valueOf('"') + String.valueOf('='));
                    } else {
                        synonyms += ("; " + s.nextToken(String.valueOf('"') + String.valueOf('"') + String.valueOf('=')));
                    }
                    //System.out.println(synonyms);
                    synonymscount++;
                } else if (token.equalsIgnoreCase("/db_xref")) {
                    String subtoken = s.nextToken(String.valueOf(' ') + String.valueOf('"') + String.valueOf(':') + String.valueOf('='));
                    if (subtoken.equalsIgnoreCase("GeneID")) {
                        geneid = Integer.valueOf(s.nextToken());
                        //System.out.println(geneid);
                    }
                } else if (token.equalsIgnoreCase("DEFINITION")) {
                    while (s.hasMoreTokens()) {
                        token = s.nextToken();
                        if (token.equalsIgnoreCase("chromosome")) {
                            chromosome = Integer.valueOf(s.nextToken());
                        }
                    }
                }
            }
        }
        br.close();
    }

    public static String FindPathwayDescription(String pathwaydescription, String pathway) throws IOException {
        String delimiter = String.valueOf(' ') + String.valueOf(',') + String.valueOf('=') + String.valueOf('.') + String.valueOf(')') + String.valueOf('(') + String.valueOf('\t') + String.valueOf('\n') + String.valueOf('\r') + String.valueOf('\f') + String.valueOf(';');
        String res = null;
        BufferedReader br = IOFile.openBufferedReader(pathwaydescription);
        boolean found = false;
        while (br.ready() && !found) {
            StringTokenizer s = new StringTokenizer(br.readLine(), delimiter);
            String pathwayid = s.nextToken();
            if (pathwayid.equalsIgnoreCase(pathway)) {
                found = true;
                res = "";
                while (s.hasMoreTokens()) {
                    res += s.nextToken() + " ";
                }
                res = res.substring(0, res.length() - 1);//retirar o ultimo espaco em branco adicionado no laco acima.
            }
        }
        return (res);
    }

    public static void AddKEEGInformation(
            AGN network,
            String pathwaydata,
            String pathwaydescription) throws IOException {
        String delimiter = String.valueOf(' ') + String.valueOf(',') + String.valueOf('=') + String.valueOf('.') + String.valueOf(')') + String.valueOf('(') + String.valueOf('\t') + String.valueOf('\n') + String.valueOf('\r') + String.valueOf('\f') + String.valueOf(';');
        Gene gene = null;
        BufferedReader br = IOFile.openBufferedReader(pathwaydata);
        while (br.ready()) {
            StringTokenizer s = new StringTokenizer(br.readLine(), delimiter);
            if (s.countTokens() > 0) {
                String locus = s.nextToken();
                int g = 0;
                boolean found = false;
                while (g < network.getNrgenes() && !found) {
                    gene = network.getGenes()[g];
                    String genelocus = gene.getLocus();
                    if (genelocus != null) {
                        //correction of locus information
                        //in some cases, locus has two stick entries, separated by semicolon
                        StringTokenizer st = new StringTokenizer(genelocus, String.valueOf(' ') + String.valueOf(';'));
                        genelocus = st.nextToken();
                    }
                    gene.setLocus(genelocus);
                    if (locus.equalsIgnoreCase(gene.getLocus())) {
                        found = true;
                    } else {
                        g++;
                    }
                }
                if (found) {
                    Vector pathway = new Vector();
                    Vector description = new Vector();
                    while (s.hasMoreTokens()) {
                        String pw = s.nextToken();
                        String desc = FindPathwayDescription(pathwaydescription, pw);
                        pathway.add(pw);
                        description.add(desc);
                        System.out.println(pw + " == " + desc);
                    }
                    //complete the pathway information
                    gene.setPathway(pathway);
                    gene.setPathwaydescription(description);
                }
            }
        }
        br.close();
    }

    public static Vector SinalPlotMA(int row, boolean showlegend,
            float maxvalue, int startcol, AGN agn, boolean showchart) {
        //codigo para gerar os dados para o grafico MultipleStepChart - usando CategoryDataset
        DefaultCategoryDataset[] datasets = new DefaultCategoryDataset[5];
        float[][] Mo = agn.getTemporalsignal();
        float[][] Mn = agn.getTemporalsignalnormalized();
        int[][] Mq = agn.getTemporalsignalquantized();
        for (int i = 0; i < datasets.length; i++) {
            datasets[i] = new DefaultCategoryDataset();
        }
        Gene gene = agn.getGenes()[row];
        String genename = gene.getName();
        if (genename == null) {
            genename = gene.getLocus();
            if (genename == null || genename.equalsIgnoreCase("no_match")) {
                genename = gene.getProbsetname();
            }
        }

        //DADOS-FABIO-DEBUG
        //Vector featuretitles = agn.getLabelstemporalsignal();
        //for (int ft = 0; ft < featuretitles.size(); ft++){
        //    String strft = (String)featuretitles.get(ft);
        //    strft+=ft;
        //    featuretitles.set(ft, strft);
        //}
        //agn.setLabelstemporalsignal(featuretitles);
        //FIM-DEBUG

        for (int col = 0; col < Mo[0].length; col++) {
            if ((int) Mo[row][col] != Preprocessing.skipvalue) {
                String featuretitle = "x axis";
                if (agn.getLabelstemporalsignal() != null) {
                    featuretitle = (String) agn.getLabelstemporalsignal().get(col + startcol);
                }
                if (Mo != null) {
                    datasets[0].addValue(Mo[row][col], genename, featuretitle);
                } else {
                    datasets[0].addValue(0, genename, featuretitle);
                }
                if (Mq != null) {
                    datasets[1].addValue(Mq[row][col], genename, featuretitle);
                } else {
                    datasets[1].addValue(0, genename, featuretitle);
                }
                if (Mn != null) {
                    datasets[2].addValue(Mn[row][col], genename, featuretitle);
                } else {
                    datasets[2].addValue(0, genename, featuretitle);
                }
                if (agn.getLowthreshold() != null) {
                    datasets[3].addValue(agn.getLowthreshold()[col], genename, featuretitle);
                } else {
                    datasets[3].addValue(0, genename, featuretitle);
                }
                if (agn.getHithreshold() != null) {
                    datasets[4].addValue(agn.getHithreshold()[col], genename, featuretitle);
                } else {
                    datasets[4].addValue(0, genename, featuretitle);
                }

            }
        }
        return (Chart.MultipleStepChartOverlayedMA(datasets,
                "Data Series", "Time/Experiment", "Value",
                showlegend, maxvalue + 0.03f, -0.03f, showchart));
    }

    public static void AdjustGeneNames(AGN agn) {
        for (int i = 0; i < agn.getNrgenes(); i++) {
            Gene gene = agn.getGenes()[i];
            String defaultname = "g" + gene.getIndex();
            if (gene.getName() == null || gene.getName().equalsIgnoreCase(defaultname)) {
                if (gene.getLocus() != null && !gene.getLocus().equalsIgnoreCase("no_match")) {
                    gene.setName(gene.getLocus());
                } else {
                    gene.setName(gene.getProbsetname());
                }
            }
            //if (gene.getName().startsWith("CYCB1")){
            StringTokenizer s = new StringTokenizer(gene.getName(), Main.delimiter);
            String newname = s.nextToken();
            gene.setName(newname);
            //}
        }
    }

    public static void setTargetsonPredictorsTies(AGN network) {
        for (int g = 0; g < network.getNrgenes(); g++) {
            Gene gene = network.getGenes()[g];
            if (gene.getPredictorsties() != null && gene.getPredictorsties().length > 0) {
                for (int tie = 0; tie < gene.getPredictorsties().length; tie++) {
                    Vector predictors = gene.getPredictorsties()[tie];
                    for (int p = 0; p < predictors.size(); p++) {
                        int indexp = (Integer) predictors.get(p);
                        if (!network.getGenes()[indexp].getTargets().contains(gene.getIndex())) {
                            network.getGenes()[indexp].addTarget(gene.getIndex());
                        }
                    }
                }
            }
        }
    }

    public static int getGeneDegree(Gene gene) {
        int degree = 0;
        //verifica o grau do gene enquanto preditor
        if (gene.getTargets() != null) {
            degree += gene.getTargets().size();
        }
        //verifica o grau do gene enquanto target
        if (gene.getPredictorsties() != null && gene.getPredictorsties().length > 0) {
            Vector vp = new Vector();
            for (int tie = 0; tie < gene.getPredictorsties().length; tie++) {
                Vector predictors = gene.getPredictorsties()[tie];
                if (predictors != null) {
                    for (int p = 0; p < predictors.size(); p++) {
                        int indexp = (Integer) predictors.get(p);
                        if (!vp.contains(indexp)) {
                            vp.add(indexp);
                        }
                    }
                }
            }
            degree += vp.size();
        } else if (gene.getPredictors() != null && gene.getPredictors().size() > 0) {
            degree += gene.getPredictors().size();
        }
        return (degree);
    }

    public static int MaxGeneFrequency(AGN network, Gene gene) {
        int freq = 0;
        //verifica a frequencia desse gene nos seus targets
        for (int t = 0; t < gene.getTargets().size(); t++) {
            int indextarget = (Integer) gene.getTargets().get(t);
            Gene target = network.getGenes()[indextarget];
            int ftemp = EdgeFrequency(target, gene);
            if (ftemp > freq) {
                freq = ftemp;
            }
        }
        //verifica a frequencia dos preditores desse gene
        if (gene.getPredictorsties() != null && gene.getPredictorsties().length > 0) {
            for (int tie = 0; tie < gene.getPredictorsties().length && tie < 100; tie++) {
                //System.out.println("target == " + gene.getName() + ", tie == " + tie);
                Vector predictors = gene.getPredictorsties()[tie];
                if (predictors != null) {
                    for (int p = 0; p < predictors.size(); p++) {
                        int indexpredictortied = (Integer) predictors.get(p);
                        Gene predictor = network.getGenes()[indexpredictortied];
                        int ftemp = EdgeFrequency(gene, predictor);
                        if (ftemp > freq) {
                            freq = ftemp;
                        }
                    }
                }
            }
        } else if (gene.getPredictors() != null && gene.getPredictors().size() > 0) {
            for (int p = 0; p < gene.getPredictors().size(); p++) {
                int indexpredictortied = (Integer) gene.getPredictors().get(p);
                Gene predictor = network.getGenes()[indexpredictortied];
                int ftemp = EdgeFrequency(gene, predictor);
                if (ftemp > freq) {
                    freq = ftemp;
                }
            }
        }
        return (freq);
    }

    public static float MinEntropy(AGN network, Gene gene) {
        float minentropy = 1.1f;
        //verifica a menor entropia obtida para esse gene
        float cfv = 1.1f;
        if (gene.getCfvalues().size() > 0) {
            cfv = (Float) gene.getCfvalues().get(0);
        }
        if (minentropy > cfv) {
            minentropy = cfv;
        }
        //verifica a menor entropia dos alvos desse gene
        if (gene.getTargets() != null) {
            for (int t = 0; t < gene.getTargets().size(); t++) {
                int indextarget = (Integer) gene.getTargets().get(t);
                Gene targetgene = network.getGenes()[indextarget];
                if (targetgene.getCfvalues().size() > 0) {
                    cfv = (Float) targetgene.getCfvalues().get(0);
                }
                if (minentropy > cfv) {
                    minentropy = cfv;
                }
            }
        }
        return (minentropy);
    }

    public static int EdgeFrequency(Gene target, Gene predictor) {
        int freq = 1;
        if (target.getPredictorsties() != null && target.getPredictorsties().length > 0) {
            //CONSIDERA APENAS OS 20 PRIMEIROS EMPATES...
            for (int tie = 0; tie < target.getPredictorsties().length && tie < 100; tie++) {
                Vector predictors = target.getPredictorsties()[tie];
                if (predictors != null) {
                    for (int p = 0; p < predictors.size(); p++) {
                        int predictortied = (Integer) predictors.get(p);
                        if (predictortied == predictor.getIndex()) {
                            freq++;
                        }
                    }
                }
            }
        } else {
            freq = 25;
        }
        //se nao houve nenhum empate, preditor = 25, se houve empates,
        //se houve empates, preditor = sua frequencia como preditor do target selcionado.
        return (freq);
    }

    //conta as classes armazenadas na rede
    public static int CountClassName(AGN network) {
        Vector classes = new Vector();
        for (int i = 0; i < network.getGenes().length; i++) {
            if (!classes.contains(network.getGenes()[i].getClassname())) {
                classes.add(network.getGenes()[i].getClassname());
            }
        }
        /*
        if (v != null) {
        classes++;
        }
        for (int i = 0; i < v.size() - 1; i++) {
        if ((Integer) ((Vector) v.get(i)).get(1) != (Integer) ((Vector) v.get(i + 1)).get(1) && (Integer) ((Vector) v.get(i)).get(1) != 0) {
        classes++;
        }
        }
         *
         */
        return (classes.size());
    }
    //conta as classes armazenadas na rede

    public static int CountClassNumber(AGN network) {
        Vector classnumber = new Vector();
        for (int i = 0; i < network.getGenes().length; i++) {
            if (!classnumber.contains(network.getGenes()[i].getClassnumber())) {
                classnumber.add(network.getGenes()[i].getClassnumber());
            }
        }
        return (classnumber.size());
    }

    //conta as classes armazenadas num objeto vetor (matriz 1x2) ordenado pela coluna 2, as quais sao armazenadas na posicao 1 de cada linha da matriz
    public static int CountPredictors(Gene g) {
        Vector predictors = new Vector();
        if (g.getPredictorsties() != null) {
            for (int tie = 0; tie < g.getPredictorsties().length; tie++) {
                Vector predictorstie = g.getPredictorsties()[tie];
                for (int p = 0; p < predictorstie.size(); p++) {
                    int indexp = (Integer) predictorstie.get(p);
                    if (!predictors.contains(indexp)) {
                        predictors.addElement(indexp);
                    }
                }
            }
        } else if (g.getPredictors() != null) {
            predictors = g.getPredictors();
        }
        return (predictors.size());
    }

    //conta as classes armazenadas num objeto vetor (matriz 1x2) ordenado pela coluna 2, as quais sao armazenadas na posicao 1 de cada linha da matriz
    public static float CountGenesperClasses(Vector v, int classe) {
        float count = 0;
        for (int i = 0; i < v.size(); i++) {
            if ((Integer) ((Vector) v.get(i)).get(1) == classe) {
                count++;
            }
        }
        return (count);
    }

    //conta as classes armazenadas num objeto vetor (matriz 1x2) ordenado pela coluna 2, as quais sao armazenadas na posicao 1 de cada linha da matriz
    public static float CountGenesperClasses(AGN network, int classe) {
        float count = 0;
        for (int i = 0; i < network.getGenes().length; i++) {
            if (network.getGenes()[i].getClassnumber() == classe) {
                count++;
            }
        }
        return (count);
    }

    //return distinct patways that are inside network
    public static Vector Pathways(AGN agn) {
        Vector pathways = new Vector();
        for (int i = 0; i < agn.getGenes().length; i++) {
            Gene g = agn.getGenes()[i];
            if (g.getPathway() != null) {
                for (int p = 0; p < g.getPathway().size(); p++) {
                    String pathway = (String) g.getPathway().get(p);
                    if (!pathways.contains(pathway)) {
                        pathways.add(pathway);
                    }
                }
            }
        }
        return (pathways);
    }
    //return distinct patways that are inside network

    public static Vector PathwayDescrptions(AGN agn) {
        Vector pathways = new Vector();
        for (int i = 0; i < agn.getGenes().length; i++) {
            Gene g = agn.getGenes()[i];
            if (g.getPathwaydescription() != null) {
                for (int p = 0; p < g.getPathwaydescription().size(); p++) {
                    String pathway = (String) g.getPathwaydescription().get(p);
                    if (!pathways.contains(pathway)) {
                        pathways.add(pathway);
                    }
                }
            }
        }
        return (pathways);
    }

    public static void setGeneRelationshipsfromDream(AGN network, File goldstandard) throws IOException {
        BufferedReader br = IOFile.openBufferedReader(goldstandard.getAbsolutePath());
        while (br.ready()) {
            StringTokenizer s = new StringTokenizer(br.readLine());
            String predictorname = s.nextToken();
            int predictoridx = -1;
            for (int i = 0; i < network.getNrgenes(); i++) {
                if (network.getGenes()[i].getName().equalsIgnoreCase(predictorname)) {
                    predictoridx = i;
                    i = network.getNrgenes();
                }
            }
            String targetname = s.nextToken();
            int targetidx = -1;
            for (int i = 0; i < network.getNrgenes(); i++) {
                if (network.getGenes()[i].getName().equalsIgnoreCase(targetname)) {
                    targetidx = i;
                    i = network.getNrgenes();
                }
            }
            int cfvalue = Integer.valueOf(s.nextToken());
            if (predictoridx >= 0 && targetidx >= 0 && cfvalue == 1) {
                network.getGenes()[predictoridx].addTarget(targetidx);
                network.getGenes()[targetidx].addPredictor(predictoridx, 0);
            }
        }
    }

    public static void setGeneRelationshipsfromProtein(AGN network, File goldstandard) throws IOException {
        BufferedReader br = IOFile.openBufferedReader(goldstandard.getAbsolutePath());
        while (br.ready()) {
            StringTokenizer s = new StringTokenizer(br.readLine());
            String predictorname = s.nextToken();
            int predictoridx = -1;
            for (int i = 0; i < network.getNrgenes(); i++) {
                if (network.getGenes()[i].getName().equalsIgnoreCase(predictorname)) {
                    predictoridx = i;
                    i = network.getNrgenes();
                }
            }
            String targetname = s.nextToken();
            int targetidx = -1;
            for (int i = 0; i < network.getNrgenes(); i++) {
                if (network.getGenes()[i].getName().equalsIgnoreCase(targetname)) {
                    targetidx = i;
                    i = network.getNrgenes();
                }
            }

            //DEBUG
            //if (targetname.equalsIgnoreCase("PFE1050w") ||
            //        predictorname.equalsIgnoreCase("PFE1050w")){
            //    System.out.println("Achou PFE1050w");
            //}
            //FIM-DEBUG

            if (predictoridx >= 0 && targetidx >= 0 && predictoridx != targetidx) {
                network.getGenes()[predictoridx].addTarget(targetidx);
                network.getGenes()[targetidx].addPredictor(predictoridx, 0);

                //adicionado para comparacao - artigo gensips 2011.
                network.getGenes()[targetidx].addTarget(predictoridx);
                network.getGenes()[predictoridx].addPredictor(targetidx, 0);
            }
        }
    }

    public static void SortPredictorSetsbyImprovement(Vector list) {
        System.out.println("\nENTRADA");
        PrintPredictorSets(list);
        boolean troca;
        do {
            troca = false;
            for (int i = 0; i < list.size() - 1; i++) {
                PredictorSet ps1 = (PredictorSet) list.get(i);
                PredictorSet ps2 = (PredictorSet) list.get(i + 1);
                if (ps2.getImprovement() > ps1.getImprovement()) {
                    list.set(i, ps2);
                    list.set(i + 1, ps1);
                    troca = true;
                } else if (ps2.getImprovement() == ps1.getImprovement()) {
                    if (ps2.getCfvalue() > ps1.getCfvalue()) {
                        list.set(i, ps2);
                        list.set(i + 1, ps1);
                        troca = true;
                    }
                }
            }
        } while (troca);
        System.out.println("ORDENADO");
        PrintPredictorSets(list);
    }

    public static void SortPredictorSetsbyTargetndex(Vector list) {
        //System.out.println("ENTRADA");
        //PrintPredictorSets(list);
        boolean troca;
        do {
            troca = false;
            for (int i = 0; i < list.size() - 1; i++) {
                PredictorSet ps1 = (PredictorSet) list.get(i);
                PredictorSet ps2 = (PredictorSet) list.get(i + 1);
                if (ps2.getTarget() < ps1.getTarget()) {
                    list.set(i, ps2);
                    list.set(i + 1, ps1);
                    troca = true;
                }
            }
        } while (troca);
        //System.out.println("ORDENADO");
        //PrintPredictorSets(list);
    }

    public static void PrintPredictorSets(Vector list) {
        for (int i = 0; i < list.size(); i++) {
            PredictorSet ps = (PredictorSet) list.get(i);
            StringBuilder str = new StringBuilder();
            str.append(" Improvement = ");
            str.append(ps.getImprovement());
            str.append(", cfv = ");
            str.append(ps.getCfvalue());
            str.append(", cfv-old = ");
            str.append(ps.getCfvalueold());
            str.append(" Target = ");
            str.append(ps.getTarget());
            System.out.println(str.toString());
        }
        System.out.println("\n");
    }

    public static void setNumericalClasses(AGN network) {
        Vector sclasses = new Vector();
        Vector nclasses = new Vector();
        Vector cclasses = new Vector();
        for (int i = 0; i < network.getGenes().length; i++) {
            if (!sclasses.contains(network.getGenes()[i].getClassname())) {
                int nc = sclasses.size();
                nclasses.add(nc);
                sclasses.add(network.getGenes()[i].getClassname());
                Color cor = getRandomColor();
                cclasses.add(cor);
                network.getGenes()[i].setClassnumber(nc);
                network.getGenes()[i].setColor(cor);
            } else {
                for (int c = 0; c < sclasses.size(); c++) {
                    if (((String) sclasses.elementAt(c)).equalsIgnoreCase(network.getGenes()[i].getClassname())) {
                        Color cor = (Color) cclasses.elementAt(c);
                        network.getGenes()[i].setClassnumber(c);
                        network.getGenes()[i].setColor(cor);
                        c = sclasses.size();//finaliza o laco.
                    }
                }
            }
        }
        int[] palette = new int[cclasses.size()];
        for (int i = 0; i < palette.length; i++) {
            palette[i] = ((Color) cclasses.get(i)).getRGB();
        }
        network.setPalette(palette);
    }
}
