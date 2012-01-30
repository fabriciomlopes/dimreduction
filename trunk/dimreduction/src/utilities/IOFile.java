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
/*** Contact: David Corrêa Martins Junior - davidjr@vision.ime.usp.br    ***/
/***          Fabrício Martins Lopes - fabriciolopes@vision.ime.usp.br   ***/
/***          Roberto Marcondes Cesar Junior - cesar@vision.ime.usp.br   ***/
/***************************************************************************/
/***************************************************************************/
/*** This class implements methods for reading of files containing a     ***/
/*** matrix                                                              ***/
/***************************************************************************/
package utilities;

import agn.AGN;
import agn.Topologies;
import fs.FSException;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

public class IOFile {

    private static FileWriter out = null;

    /* ROTINA PARA LOCALIZAR UM ARQUIVO NO DISCO E DEVOLVER SEU CAMINHO
    (PATH) ABSOLUTO. */
    public static String OpenPath() {
        JFileChooser dialogo;
        int opcao;
        String pastainicial = System.getProperty("user.dir").toString();
        dialogo = new JFileChooser(pastainicial);
        opcao = dialogo.showOpenDialog(null);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            try {
                return (dialogo.getSelectedFile().getAbsolutePath());
            } catch (Exception error) {
                throw new FSException("Error when selecting file. " + error, false);
            }
        } else {
            return (null);
        }
    }

    public static String OpenAGNFile() {
        JFileChooser dialogo;
        int opcao;
        String pastainicial = System.getProperty("user.dir").toString();
        dialogo = new JFileChooser(pastainicial);
        FileNameExtensionFilter agnFilter = new FileNameExtensionFilter("AGN Artificial Gene Network", "agn");
        dialogo.addChoosableFileFilter(agnFilter);
        dialogo.setFileFilter(agnFilter);
        opcao = dialogo.showOpenDialog(null);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            return (dialogo.getSelectedFile().getAbsolutePath());
        } else {
            return (null);
        }
    }

    public static boolean SaveFile(String texto) {
        JFileChooser dialogo;
        int opcao;
        String pastainicial = System.getProperty("user.dir").toString();

        dialogo = new JFileChooser(pastainicial);
        opcao = dialogo.showSaveDialog(null);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedWriter fw = new BufferedWriter(new FileWriter(
                        dialogo.getSelectedFile().getAbsolutePath(), true));
                fw.write(texto);
                fw.flush();
                fw.close();
                return (true);
            } catch (Exception error) {
                throw new FSException("Error when selecting file. " + error, false);
            }
        }
        return (false);
    }

    public static boolean SaveFile(String texto, String path) {
        try {
            BufferedWriter fw = new BufferedWriter(new FileWriter(path, false));
            fw.write(texto);
            fw.flush();
            fw.close();
            return true;
        } catch (Exception error) {
            throw new FSException("Error when selecting file. " + error, false);
        }
    }

    public static String SaveFile() {
        JFileChooser dialogo;
        int opcao;
        String pastainicial = System.getProperty("user.dir").toString();
        dialogo = new JFileChooser(pastainicial);
        opcao = dialogo.showSaveDialog(null);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            return (dialogo.getSelectedFile().getAbsolutePath());
        } else {
            return (null);
        }
    }

    /*
     * Get the extension of a file.
     */
    public static String getExtension(String filename) {
        String ext = null;
        int i = filename.lastIndexOf('.');
        if (i > 0 && i < filename.length() - 1) {
            ext = filename.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static String SaveAGNFile() {
        JFileChooser dialogo;
        int opcao;
        String pastainicial = System.getProperty("user.dir").toString();
        dialogo = new JFileChooser(pastainicial);
        FileNameExtensionFilter agnFilter = new FileNameExtensionFilter("AGN Artificial Gene Network", "agn");
        dialogo.addChoosableFileFilter(agnFilter);
        dialogo.setFileFilter(agnFilter);
        opcao = dialogo.showSaveDialog(null);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            String filename = dialogo.getSelectedFile().getAbsolutePath();
            String ext = getExtension(filename);
            if (ext == null) {
                FileNameExtensionFilter filter = (FileNameExtensionFilter) dialogo.getFileFilter();
                filename = filename + "." + filter.getExtensions()[0];
            }
            return (filename);
        } else {
            return (null);
        }
    }

    public static String SaveIMGFile() {
        JFileChooser dialogo;
        int opcao;
        String pastainicial = System.getProperty("user.dir").toString();
        dialogo = new JFileChooser(pastainicial);
        FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG Compressed Image Files", "jpg");
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Portable Network Graphics", "png");
        FileNameExtensionFilter gifFilter = new FileNameExtensionFilter("GIF Graphics Interchange Format", "gif");
        FileNameExtensionFilter bmpFilter = new FileNameExtensionFilter("BMP file format", "bmp");
        dialogo.addChoosableFileFilter(pngFilter);
        dialogo.addChoosableFileFilter(jpgFilter);
        dialogo.addChoosableFileFilter(gifFilter);
        dialogo.addChoosableFileFilter(bmpFilter);
        dialogo.setFileFilter(pngFilter);
        opcao = dialogo.showSaveDialog(null);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            String filename = dialogo.getSelectedFile().getAbsolutePath();
            String ext = getExtension(filename);
            if (ext == null) {
                FileNameExtensionFilter filter = (FileNameExtensionFilter) dialogo.getFileFilter();
                filename = filename + "." + filter.getExtensions()[0];
            }
            return (filename);
        } else {
            return (null);
        }
    }

    public static boolean SaveTable(JTable table) {
        JFileChooser dialogo;
        int opcao;
        String pastainicial = System.getProperty("user.dir").toString();
        dialogo = new JFileChooser(pastainicial);
        opcao = dialogo.showSaveDialog(null);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedWriter fw = new BufferedWriter(new FileWriter(
                        dialogo.getSelectedFile().getAbsolutePath(), false));
                for (int i = 0; i < table.getRowCount(); i++) {
                    if (i == 0) {
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            fw.write(table.getColumnName(j) + ";");
                        }
                        fw.write("\n");
                    }
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        String cn = table.getColumnName(j);
                        Object valor = table.getValueAt(i, j);
                        fw.write(valor + ";");
                        //if (j < table.getColumnCount() - 1) {
                        //    fw.write(";");
                        //}
                    }
                    if (i < table.getRowCount() - 1) {
                        fw.write("\n");
                    }
                    fw.flush();
                }
                fw.close();
                return (true);
            } catch (Exception error) {
                throw new FSException("Error when selecting file. " + error, false);
            }
        }
        return (false);
    }

    public static BufferedReader OpenBufferedReader(String path) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException error) {
            throw new FSException("Error on File Open. " + error, false);
        }
        return (br);
    }

    public static BufferedWriter OpenBufferedWriter(String path, boolean append) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(path, append));
        } catch (IOException error) {
            throw new FSException("Error on File Open. " + error, false);
        }
        return (bw);
    }
// receives a file name and returns a matrix of double values

    @SuppressWarnings("empty-statement")
    public static float[][] ReadMatrix(String arch_name, int startrow,
            int startcolumn, String delimiter)
            throws IOException, NumberFormatException {
        // obtaining the number of lines and collumns of the matrix
        BufferedReader fp = OpenBufferedReader(arch_name);
        if (fp == null) {
            return (null);
        }
        StringTokenizer s = new StringTokenizer(fp.readLine(), delimiter);
        int collumns = s.countTokens();
        int lines;
        for (lines = 1; fp.readLine() != null; lines++) {
            ;
        }
        // reading the matrix
        float[][] A = new float[lines - startrow][collumns - startcolumn];
        fp = OpenBufferedReader(arch_name);
        if (fp == null) {
            return (null);
        }

        for (int l = 0; l < startrow; l++) {
            s = new StringTokenizer(fp.readLine(), delimiter);//desconsidera linha
        }
        for (int i = 0; i < lines - startrow; i++) {
            s = new StringTokenizer(fp.readLine(), delimiter);

            for (int j = 0; j < startcolumn; j++) {
                s.nextToken();//desconsidera coluna
            }
            for (int j = 0; s.hasMoreTokens();
                    A[i][j++] = Float.parseFloat(s.nextToken())) {
                ;
            }
            //System.out.println("linha " + i);
        }
        fp.close();
        return A;
    }
// receives a file name and returns a matrix of chars (small integers for
// memory space economy)

    @SuppressWarnings("empty-statement")
    public static char[][] ReadMatrix(String arch_name, String delimiter)
            throws IOException {
        BufferedReader fp = OpenBufferedReader(arch_name);
        if (fp == null) {
            return (null);
        }
        // obtaining the number of lines and collumns of the matrix
        StringTokenizer s = new StringTokenizer(fp.readLine(), delimiter);
        int collumns = s.countTokens();
        int lines;
        for (lines = 1; fp.readLine() != null; lines++) {
            ;
        }
        char[][] A = new char[lines][collumns];
        fp = OpenBufferedReader(arch_name);
        if (fp == null) {
            return (null);
        }
        // reading the matrix
        for (int i = 0; i < lines; i++) {
            s = new StringTokenizer(fp.readLine(), delimiter);
            for (int j = 0; s.hasMoreTokens();
                    A[i][j++] = (char) Integer.parseInt(s.nextToken(delimiter))) {
                ;
            }
        }
        fp.close();
        return A;
    }

    public static Vector ReadDataFirstCollum(String arch_name, int startrow,
            String delimiter)
            throws IOException {
        BufferedReader fp = OpenBufferedReader(arch_name);
        if (fp == null) {
            return (null);
        }
        Vector vetout = new Vector();
        StringTokenizer s;

        if (fp.ready()) {
            for (int l = 0; l < startrow; l++) {
                s = new StringTokenizer(fp.readLine(), delimiter);//desconsidera linha
            }
        }
        while (fp.ready()) {
            // reading the matrix
            s = new StringTokenizer(fp.readLine(), delimiter);
            vetout.add(s.nextToken());
        }
        fp.close();
        return vetout;
    }

    /*
     * METODO ABAIXO PARA LER CLASSE (PRIMEIRA COLUNA) E NOME DO GENE (SEGUNDA COLUNA)
     * DE UM ARQUIVO ASCII.
     */
    public static Vector[] ReadDataCollumns2(String arch_name, int startrow,
            Vector collumns, String delimiter) {
        try {
            BufferedReader fp = OpenBufferedReader(arch_name);
            if (fp == null) {
                return (null);
            }
            int numtargets = 0;
            for (int lines = 1; fp.ready(); lines++) {
                StringTokenizer s = new StringTokenizer(fp.readLine(), delimiter);
                if (!s.nextToken().startsWith(">")) {
                    numtargets++;
                }
            }
            Vector[] vetout = new Vector[numtargets];
            for (int i = 0; i < vetout.length; i++) {
                vetout[i] = new Vector();
            }

            fp = OpenBufferedReader(arch_name);
            StringTokenizer s;
            if (fp.ready()) {
                for (int l = 0; l < startrow; l++) {
                    s = new StringTokenizer(fp.readLine(), delimiter);//desconsidera linha
                }
            }
            String classe = null;
            int pos = 0;
            while (fp.ready()) {
                s = new StringTokenizer(fp.readLine(), delimiter);
                int col = 0;
                while (s.hasMoreTokens()) {
                    String token = s.nextToken();
                    if (token.startsWith(">")) {
                        classe = s.nextToken();
                    } else {
                        if (collumns.contains(col)) {
                            vetout[pos].add(classe);//gene classe
                            vetout[pos].add(token);//gene name
                            pos++;
                        } else {
                            s.nextToken();//token of the collumn
                        }
                    }
                    col++;
                }
            }
            fp.close();
            return vetout;
        } catch (IOException error) {
            System.out.println("Error on ReadDataCollumns. " + error);
            return (null);
        }
    }

    public static Vector[] ReadDataCollumns(String arch_name, int startrow,
            Vector collumns, String delimiter) {
        BufferedReader fp = OpenBufferedReader(arch_name);
        if (fp == null) {
            return (null);
        }
        Vector[] vetout = new Vector[collumns.size()];
        for (int i = 0; i < collumns.size(); i++) {
            vetout[i] = new Vector();
        }

        try {
            StringTokenizer s;
            if (fp.ready()) {
                for (int l = 0; l < startrow; l++) {
                    s = new StringTokenizer(fp.readLine(), delimiter);//desconsidera linha
                }
            }
            while (fp.ready()) {
                s = new StringTokenizer(fp.readLine(), delimiter);
                int pos = 0;
                int col = 0;
                while (s.hasMoreTokens()) {
                    if (collumns.contains(col)) {
                        vetout[pos++].add(s.nextToken());//token of the collumn
                    } else {
                        s.nextToken();//token of the collumn
                    }
                    col++;
                }
            }
            fp.close();
            return vetout;
        } catch (IOException error) {
            System.out.println("Error on ReadDataCollumns. " + error);
            return (null);
        }
    }

    public static Vector ReadDataFirstRow(String arch_name, int startrow,
            int startcolumn, String delimiter) throws IOException {
        BufferedReader fp = OpenBufferedReader(arch_name);
        if (fp == null) {
            return (null);
        }
        Vector vetout = new Vector();
        StringTokenizer s;
        if (fp.ready()) {
            for (int l = 0; l < startrow; l++) {
                s = new StringTokenizer(fp.readLine(), delimiter);//desconsidera linha
            }
            // reading the matrix
            s = new StringTokenizer(fp.readLine(), delimiter);
            for (int j = 0; j < startcolumn; j++) {
                s.nextToken();//desconsidera coluna
            }
            while (s.hasMoreTokens()) {
                vetout.add(s.nextToken());
            }
        }
        fp.close();
        return vetout;
    }

    public static Vector ReadDataLine(String arch_name)
            throws IOException {
        BufferedReader fp = OpenBufferedReader(arch_name);
        if (fp == null) {
            return (null);
        }
        Vector out = new Vector();
        while (fp.ready()) // reading the matrix
        {
            out.add(fp.readLine());
        }
        fp.close();
        return out;
    }

    public static AGN ReadAGNfromFile(String path) {
        File arquivo = null;
        FileInputStream fluxoentrada = null;
        ObjectInputStream obj = null;
        AGN network = null;
        try {
            arquivo = new File(path);
            if (arquivo != null) {
                fluxoentrada = new FileInputStream(arquivo);
            } else {
                return null;
            }
            obj = new ObjectInputStream(fluxoentrada);
            network = (AGN) obj.readObject();
            fluxoentrada.close();
            obj.close();
        } catch (IOException error) {
            System.out.println("Error on create the File Inpu Stream. " + error);
            return null;
        } catch (ClassNotFoundException error) {
            System.out.println("Error on AGN read. " + error);
            return null;
        }
        return network;
    }

    public static boolean WriteAGNtoFile(AGN network, String path) {
        File arquivo = null;
        FileOutputStream fluxosaida = null;
        ObjectOutputStream obj = null;
        try {
            arquivo = new File(path);
            if (arquivo != null) {
                fluxosaida = new FileOutputStream(arquivo, false);//append == true == continua gravando no arquivo
                obj = new ObjectOutputStream(fluxosaida);
                obj.writeObject(network);
            } else {
                return false;
            }
        } catch (IOException e) {
            System.out.println("Erro na criacao do FileOutputStream. " + e);
            return false;
        }
        return true;
    }

    /*Constructs a string tokenizer for each matrix row. The character
    in the delimiter argument are the delimiters for separating tokens.*/
    public static void WriteMatrix(String path, float[][] M,
            String delimiter) {
        try {
            out = new FileWriter(new File(path), false);
            for (int i = 0; i < M.length; i++) {
                for (int j = 0; j < M[0].length; j++) {
                    if (j < M[0].length - 1) {
                        out.write(M[i][j] + delimiter);
                    } else {
                        out.write(M[i][j] + "\n");
                    }
                }
                //out.write("\n");
                out.flush();
            }
            out.close();
        } catch (IOException error) {
            throw new FSException("Error when save adjacency matrix. " + error, false);
        }
    }

    public static void WriteMatrix(String path, int[][] M,
            String delimiter) {
        try {
            out = new FileWriter(new File(path), false);
            for (int i = 0; i < M.length; i++) {
                for (int j = 0; j < M[0].length; j++) {
                    if (j < M[0].length - 1) {
                        out.write(M[i][j] + delimiter);
                    } else {
                        out.write(M[i][j] + "\n");
                    }
                }
                //out.write("\n");
                out.flush();
            }
            out.close();
        } catch (IOException error) {
            throw new FSException("Error when save adjacency matrix. " + error, false);
        }
    }

    public static void WriteFile(String path, double[][] M) {
        try {
            out = new FileWriter(new File(path), false);
            for (int i = 0; i < M.length; i++) {
                for (int j = 0; j < M[0].length; j++) {
                    out.write(M[i][j] + " ");
                }
                out.write("\n");
                out.flush();
            }
            out.close();
        } catch (IOException error) {
            throw new FSException("Error when save normalized file. " + error, false);
        }
    }

    public static void WriteFile(String path, int[] M) {
        try {
            out = new FileWriter(new File(path), false);
            for (int i = 0; i < M.length; i++) {
                out.write(i + ";" + M[i]);
                out.write("\n");
                out.flush();
            }
            out.close();
        } catch (IOException error) {
            throw new FSException("Error when save normalized file. " + error, false);
        }
    }

    public static void WriteFile(String path, Vector vet, boolean append) {
        try {
            out = new FileWriter(new File(path), append);
            for (int i = 0; i < vet.size(); i++) {
                out.write(vet.get(i) + "\n");
                out.flush();
            }
            out.close();
        } catch (IOException error) {
            throw new FSException("Error when save normalized file. " + error, false);
        }
    }

    public static void WriteTwoColumnsFile(String path, Vector col1, Vector col2, boolean append) {
        try {
            out = new FileWriter(new File(path), append);
            for (int i = 0; i < col1.size(); i++) {
                out.write(col1.get(i) + "; " + col2.get(i) + " \n");
                out.flush();
            }
            out.close();
        } catch (IOException error) {
            throw new FSException("Error when save normalized file. " + error, false);
        }
    }

    public static void WriteIndividualResults(
            AGN originalagn,
            Vector originalpredictors,
            Vector inferredpredictors,
            StringBuffer outline) throws IOException {

        float TP = 0, FP = 0, FN = 0, TN = 0;
        float PPV = 0;//precision
        float Sensitivity = 0;//recall
        float Specificity = 0;
        if (originalagn != null) {
            for (int i = 0; i < inferredpredictors.size(); i++) {
                int ip = (Integer) inferredpredictors.get(i);
                if (originalpredictors.contains(ip)) {
                    TP++;
                } else {
                    FP++;
                }
                outline.append(ip + " ");
            }
            for (int i = 0; i < originalpredictors.size(); i++) {
                int op = (Integer) originalpredictors.get(i);
                if (!inferredpredictors.contains(op)) {
                    FN++;
                }
            }
            TN = (originalagn.getNrgenes() - (TP + FN + 1));
            outline.append(";");//fecha a celula de predictores

            if ((TP + FP) > 0) {
                PPV = (TP / (TP + FP));//precision
            }

            if ((TP + FN) > 0) {
                Sensitivity = (TP / (TP + FN));//recall
            }

            if ((TN + FP) > 0) {
                Specificity = (TN / (TN + FP));
            }
        }
        outline.append(TP).append(";");
        outline.append(FP).append(";");
        outline.append(FN).append(";");
        outline.append(TN).append(";");
        outline.append(PPV).append(";");
        outline.append(Sensitivity).append(";");
        outline.append(Specificity).append(";");
        outline.append((float) Math.pow(PPV * Sensitivity * Specificity, 1f / 3f)).append(";");
        //outline.append(Math.sqrt(PPV * Sensitivity) + ";");
    }

    public static void IndividualResults(
            AGN originalagn,
            Vector originalpredictors,
            Vector ties,
            StringBuffer outline) throws IOException {

        for (int tp = 0; tp < ties.size(); tp++) {
            //para cada conj de predictores empatados sao gerados os resultados abaixo.
            Vector inferredpredictors = (Vector) ties.get(tp);
            WriteIndividualResults(
                    originalagn,
                    originalpredictors,
                    inferredpredictors,
                    outline);
        }
    }

    public static void WriteCP(String path, Vector predictors, float cfv, int targetindex) {
        try {
            out = new FileWriter(new File(path), true);
            StringBuilder outline = new StringBuilder();

            outline.append("Preditores escolhidos == ");
            for (int i = 0; i < predictors.size(); i++) {
                int predictorindex = (Integer) predictors.get(i);
                if (predictorindex >= targetindex) {
                    predictorindex++;
                }
                outline.append(predictorindex).append(";");
            }
            outline.append("cfv == ").append(cfv).append(";");
            outline.append("\n");
            out.write(outline.toString());
            out.flush();
            out.close();
        } catch (IOException error) {
            throw new FSException("Erro when save IMP results on file. " + error, false);
        }
    }

    public static void WriteStackSize(AGN original, String path, int size, int targetindex) {
        try {
            out = new FileWriter(new File(path), true);
            StringBuilder outline = new StringBuilder();
            outline.append("avg-edges == ").append(";");
            outline.append(original.getAvgedges()).append(";");

            outline.append("signal size == ").append(";");
            outline.append(original.getSignalsize()).append(";");

            outline.append("stack size == ").append(";");
            outline.append(size).append(";");

            outline.append("target index == ").append(";");
            outline.append(targetindex).append(";");

            outline.append("\n");
            out.write(outline.toString());
            out.flush();
            out.close();
        } catch (IOException error) {
            throw new FSException("Erro when save IMP results on file. " + error, false);
        }
    }

    public static void WriteIMP(AGN original, String path, int targetindex, int predictorindex, float cfvalue, int booleanfunction) {
        try {
            out = new FileWriter(new File(path), true);
            StringBuilder outline = new StringBuilder();
            outline.append("avg-edges == ").append(";");
            outline.append(original.getAvgedges()).append(";");

            outline.append("signal size == ").append(";");
            outline.append(original.getSignalsize()).append(";");

            outline.append("target index == ").append(";");
            outline.append(targetindex).append(";");

            outline.append("predictor index == ").append(";");
            outline.append(predictorindex).append(";");

            outline.append("boolean function == ").append(";");
            outline.append(booleanfunction).append(";");

            outline.append("cfvalue == ").append(";");
            outline.append(cfvalue).append(";");

            outline.append("\n");
            out.write(outline.toString());
            out.flush();
            out.close();
        } catch (IOException error) {
            throw new FSException("Erro when save IMP results on file. " + error, false);
        }
    }

    public static void WriteTies(
            AGN originalagn,
            String path,
            int target,
            int avgedges,
            int signalsize,
            String networktopology,
            Vector originalpredictors,
            float qvalue,
            int searchmethod,
            Vector inferredpredictors,
            Vector ties,
            float cfvalue,//valor obtido pela funcao criterio
            boolean initialization) {

        try {
            out = new FileWriter(new File(path), true);
            StringBuffer outline = new StringBuffer();
            if (initialization) {
                outline.append("target;");
                outline.append("avg-edges(k);");
                outline.append("topology;");
                outline.append("original-predictors;");
                outline.append("Boolean-functions;");
                outline.append("CF Value;");
                outline.append("q-value;");
                outline.append("search-method;");
                outline.append("signal-size;");
                outline.append("ties?;");
                outline.append("#ties;");
                outline.append("recovered-predictors;");
                outline.append("TP;");
                outline.append("FP;");
                outline.append("FN;");
                outline.append("TN;");
                outline.append("PPV;");
                outline.append("Sensitivity;");
                outline.append("Specificity;");
                outline.append("Similarity;");
                outline.append("recovered-predictors;");
                outline.append("TP;");
                outline.append("FP;");
                outline.append("FN;");
                outline.append("TN;");
                outline.append("PPV;");
                outline.append("Sensitivity;");
                outline.append("Specificity;");
                outline.append("Similarity;");
                outline.append("...;");
            } else {
                outline.append(target + ";");
                outline.append(avgedges + ";");
                outline.append(networktopology + ";");
                //predictores originais
                int[] op = null;
                if (originalpredictors != null) {
                    op = new int[originalpredictors.size()];
                    for (int i = 0; i < originalpredictors.size(); i++) {
                        op[i] = (Integer) originalpredictors.get(i);
                        outline.append(op[i] + " ");
                    }
                }
                outline.append(";");

                //Boolean functions
                if (originalagn != null && originalagn.getGenes()[target].getBooleanfunctions().size() > 0) {
                    //ARMAZENA O CONJUNTO DE FUNCOES BOOLEANAS COM MAIOR PROBABILIDADE DE OCORREN NA PBN.
                    Vector vbf = (Vector) originalagn.getGenes()[target].getBooleanfunctions().get(0);
                    if (vbf != null) {
                        for (int i = 0; i < vbf.size(); i++) {
                            int bf = (Integer) vbf.get(i);
                            outline.append(bf + " (" + Topologies.BooleanFunctions[bf] + ") ");
                        }
                    }
                }
                outline.append(";");

                //criterion function value
                outline.append(cfvalue + ";");

                //entropic parameter q applied in the predictors recover
                outline.append(qvalue + ";");

                //search-method used in the inference process
                outline.append(searchmethod + ";");

                //signal-size used in the inference process

                outline.append(signalsize + ";");

                if (ties != null && ties.size() > 1) {
                    outline.append("1;");//ties?
                    outline.append(ties.size() + ";");//#ties
                    WriteIndividualResults(
                            originalagn,
                            originalpredictors,
                            inferredpredictors,
                            outline);
                    IndividualResults(
                            originalagn,
                            originalpredictors,
                            ties,
                            outline);
                } else if (inferredpredictors != null) {
                    outline.append("0;");//ties?
                    outline.append("0;");//#ties
                    WriteIndividualResults(
                            originalagn,
                            originalpredictors,
                            inferredpredictors,
                            outline);
                } else {
                    outline.append("0;");//ties?
                    outline.append("0;");//#ties
                    outline.append(";");//TP
                    outline.append(";");//FP
                    outline.append(";");//FN
                    outline.append(";");//TN
                    outline.append(";");//PPV
                    outline.append(";");//Sensitivity
                    outline.append(";");//Specificity
                    outline.append(";");//Similarity
                }
            }
            outline.append("\n");
            out.write(outline.toString());
            out.flush();
            out.close();
        } catch (IOException error) {
            throw new FSException("Error when save net comparisons file. " + error, false);
        }
    }

    public static void WriteFile(String path, int ini, int searchmethod, int nr_nodes,
            int avg_edges, int nr_obs, int quantization, float[] CM, int concat,
            float q_tsallis, boolean append) {
        try {
            out = new FileWriter(new File(path), append);
            StringBuffer outline = new StringBuffer();
            if (ini == 1) {
                outline.append("search");
                while (outline.length() < 9) {
                    outline.append(" ");
                }

                outline.append("nr-nodes");
                while (outline.length() < 18) {
                    outline.append(" ");
                }

                outline.append("avg-edges");
                while (outline.length() < 28) {
                    outline.append(" ");
                }

                outline.append("nr-obs");
                while (outline.length() < 35) {
                    outline.append(" ");
                }

                outline.append("concat");
                while (outline.length() < 42) {
                    outline.append(" ");
                }

                outline.append("quant");
                while (outline.length() < 48) {
                    outline.append(" ");
                }

                outline.append("TP");
                while (outline.length() < 54) {
                    outline.append(" ");
                }

                outline.append("FN");
                while (outline.length() < 60) {
                    outline.append(" ");
                }

                outline.append("TN");
                while (outline.length() < 69) {
                    outline.append(" ");
                }

                outline.append("FP");
                while (outline.length() < 75) {
                    outline.append(" ");
                }

                outline.append("PPV");
                while (outline.length() < 87) {
                    outline.append(" ");
                }

                outline.append("SENSITIVITY");
                while (outline.length() < 100) {
                    outline.append(" ");
                }

                outline.append("SPECIFICITY");
                while (outline.length() < 113) {
                    outline.append(" ");
                }

                outline.append("SIMILARITY");
                while (outline.length() < 126) {
                    outline.append(" ");
                }

                outline.append("TP-H");
                while (outline.length() < 132) {
                    outline.append(" ");
                }

                outline.append("FN-H");
                while (outline.length() < 138) {
                    outline.append(" ");
                }

                outline.append("TN-H");
                while (outline.length() < 144) {
                    outline.append(" ");
                }

                outline.append("FP-H");
                while (outline.length() < 150) {
                    outline.append(" ");
                }

                outline.append("PPV-H");
                while (outline.length() < 162) {
                    outline.append(" ");
                }

                outline.append("SENSITIVITY-H");
                while (outline.length() < 179) {
                    outline.append(" ");
                }

                outline.append("SPECIFICITY-H");
                while (outline.length() < 194) {
                    outline.append(" ");
                }

                outline.append("SIMILARITY-H");
                while (outline.length() < 209) {
                    outline.append(" ");
                }

                outline.append("q-Tsallis");
            } else {
                outline.append(searchmethod);
                while (outline.length() < 9) {
                    outline.append(" ");
                }

                outline.append(nr_nodes);
                while (outline.length() < 18) {
                    outline.append(" ");
                }

                outline.append(avg_edges);
                while (outline.length() < 28) {
                    outline.append(" ");
                }

                outline.append(nr_obs);
                while (outline.length() < 35) {
                    outline.append(" ");
                }

                outline.append(concat);
                while (outline.length() < 42) {
                    outline.append(" ");
                }

                outline.append(quantization);
                while (outline.length() < 48) {
                    outline.append(" ");
                }

                outline.append((int) CM[0]);//true positive
                while (outline.length() < 54) {
                    outline.append(" ");
                }

                outline.append((int) CM[3]);//false negative
                while (outline.length() < 60) {
                    outline.append(" ");
                }

                outline.append((int) CM[2]);//true negative
                while (outline.length() < 69) {
                    outline.append(" ");
                }

                outline.append((int) CM[1]);//false positive
                while (outline.length() < 75) {
                    outline.append(" ");
                }

                outline.append(CM[4]);//PPV
                while (outline.length() < 87) {
                    outline.append(" ");
                }

                outline.append(CM[5]);//SENSITIVITY
                while (outline.length() < 100) {
                    outline.append(" ");
                }

                outline.append(CM[6]);//SPECIFICITY
                while (outline.length() < 113) {
                    outline.append(" ");
                }

                outline.append(CM[7]);//SIMILARITY
                while (outline.length() < 126) {
                    outline.append(" ");
                }

                outline.append((int) CM[8]);//TP-Hub
                while (outline.length() < 132) {
                    outline.append(" ");
                }

                outline.append((int) CM[11]);//FN-Hub
                while (outline.length() < 138) {
                    outline.append(" ");
                }

                outline.append((int) CM[10]);//TN-Hub
                while (outline.length() < 144) {
                    outline.append(" ");
                }

                outline.append((int) CM[9]);//FP-Hub
                while (outline.length() < 150) {
                    outline.append(" ");
                }

                outline.append(CM[12]);//PPV-Hub
                while (outline.length() < 162) {
                    outline.append(" ");
                }

                outline.append(CM[13]);//SENSITIVITY-Hub
                while (outline.length() < 179) {
                    outline.append(" ");
                }

                outline.append(CM[14]);//SPECIFICITY-Hub
                while (outline.length() < 194) {
                    outline.append(" ");
                }

                outline.append(CM[15]);//SIMILARITY-Hub
                while (outline.length() < 209) {
                    outline.append(" ");
                }

                outline.append(q_tsallis);//q-entropy used in criterion function
            }

            outline.append("\n");
            out.write(outline.toString());
            out.flush();
            out.close();
        } catch (IOException error) {
            throw new FSException("Error when save net comparisons file. " + error, false);
        }

    }

    public static synchronized void WriteFile(BufferedWriter out, int target, Vector predictors, double entropy) {
        if (out != null) {
            try {
                //BufferedWriter out = new BufferedWriter(new FileWriter(path, true));
                for (int i = 0; i
                        < predictors.size(); i++) {
                    //passo 2 pq sao armazenados o indice do preditor e sua entropia em sequencia.
                    out.write((Integer) predictors.get(i) + "       " + target + "       " + entropy);
                    out.write("\n");
                    out.flush();
                }
//out.close();

            } catch (IOException error) {
                throw new FSException("Error when save network ascii file. " + error, false);
            }

        }
    }

    //prints the content of a matrix
    public static void PrintMatrix(char[][] M) {
        int lines = M.length;
        int collumns = M[0].length;
        for (int i = 0; i
                < lines; i++) {
            for (int j = 0; j
                    < collumns; j++) {
                System.out.print((int) M[i][j] + " ");
            }

            System.out.println();
        }
        System.out.println();
    }

    public static void PrintMatrix(int[][] M) {
        int lines = M.length;
        int collumns = M[0].length;
        for (int i = 0; i < lines; i++) {
            System.out.print(i + ") ");
            for (int j = 0; j < collumns; j++) {
                System.out.print(M[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    //prints the content of a matrix
    public static void PrintMatrix(float[][] M) {
        int lines = M.length;
        int collumns = M[0].length;
        for (int i = 0; i
                < lines; i++) {
            for (int j = 0; j
                    < collumns; j++) {
                System.out.print(M[i][j] + " ");
            }

            System.out.println();
        }
        System.out.println();
    }

    //prints the content of an array
    public static void PrintArray(float[] M) {
        int rows = M.length;
        for (int i = 0; i < rows; i++) {
            System.out.print(M[i] + " ");
        }
        System.out.println();
    }

    //prints the content of an array
    public static void PrintArray(int[] M) {
        int rows = M.length;
        for (int i = 0; i < rows; i++) {
            System.out.print(M[i] + " ");
        }
        System.out.println();
    }

    public static void PrintMatrix(int[][] Mo, int[][] Mr) {
        int lines = Mo.length;
        int collumns = Mo[0].length;
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < collumns; j++) {
                if (Mo[i][j] == 1) {
                    System.out.print(Mr[i][j] + "* ");
                } else {
                    System.out.print(Mr[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void MakeHeaderStatistics(String output) {
        try {
            BufferedWriter fw = new BufferedWriter(new FileWriter(output, false));
            StringBuffer out = new StringBuffer();
            out.append("#");
            while (out.length() < 5) {
                out.append(" ");
            }

            out.append("nrnos");
            while (out.length() < 12) {
                out.append(" ");
            }

            out.append("maxpred");
            while (out.length() < 22) {
                out.append(" ");
            }

            out.append("iter");
            while (out.length() < 29) {
                out.append(" ");
            }

            out.append("obs");
            while (out.length() < 35) {
                out.append(" ");
            }

            out.append("concat");
            while (out.length() < 44) {
                out.append(" ");
            }

            out.append("certos/total");
            while (out.length() < 59) {
                out.append(" ");
            }

            out.append("falsosp");
            while (out.length() < 69) {
                out.append(" ");
            }

            out.append("negativos");
            out.append("\n");
            fw.write(out.toString());
            fw.close();
        } catch (Exception error) {
            throw new FSException("Error when creating statistics model file. " + error, false);
        }

    }

    public static void CloseBufferedWriter(BufferedWriter out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException error) {
                throw new FSException("Error when close file writer. " + error, false);
            }
        }
    }

    public static void PrintVectorofPredictors(Vector<Vector> vpredictors, int targetindex) {
        for (int i = 0; i < vpredictors.size(); i++) {
            PrintPredictors(vpredictors.get(i), -1, targetindex);
        }
    }

    public static void PrintPredictors(Vector predictors, float cfv, int targetindex) {
        System.out.print("( ");
        for (int i = 0; i < predictors.size(); i++) {
            int predictorindex = (Integer) predictors.get(i);
            if (predictorindex >= targetindex) {
                predictorindex++;
            }
            System.out.print(predictorindex + " ");
        }
        if (cfv >= 0) {
            System.out.print(") cfv = " + cfv + "\n");
        } else {
            System.out.print(")\n");
        }
    }

    public static void saveDreamResults(AGN network, String path) throws IOException {
        BufferedWriter bw = IOFile.OpenBufferedWriter(path, false);
        for (int i = 0; i < network.getNrgenes(); i++) {
            //Vector targets = network.getGenes()[i].getTargets();
            Vector predictors = network.getGenes()[i].getPredictors();
            for (int t = 0; t < network.getNrgenes(); t++) {
                if (i != t) {//nao considera auto-relacionamentos.
                    StringBuffer str = new StringBuffer();
                    str.append(network.getGenes()[t].getName() + "\t " + network.getGenes()[i].getName() + "\t ");
                    if (predictors.contains(t)) {
                        str.append("1");
                    } else {
                        str.append("0");
                    }
                    str.append("\n");
                    bw.write(str.toString());
                }
            }
        }
        bw.flush();
        bw.close();
    }
}
