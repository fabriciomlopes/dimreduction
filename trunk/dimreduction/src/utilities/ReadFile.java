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

import fs.FSException;
import java.io.*;
import java.util.StringTokenizer;

public class ReadFile {
    
    public static BufferedReader openFile(String arch_name){
        BufferedReader fp = null;
        try {
            fp = new BufferedReader(new FileReader(arch_name));
        } catch(FileNotFoundException error) {
            throw new FSException("Error on File Open. "+error, false);
        }
        return(fp);
    }
    
    // receives a file name and returns a matrix of double values
    public static double [][] readMatrixDouble(String arch_name)
    throws IOException {
        // obtaining the number of lines and collumns of the matrix
        BufferedReader fp = openFile(arch_name);
        if ( fp == null )
            return (null);
        StringTokenizer s = new StringTokenizer(fp.readLine());
        int collumns = s.countTokens();
        int lines;
        for (lines = 1; fp.readLine() != null; lines++);
        // reading the matrix
        double [][] A = new double [lines][collumns];
        fp = openFile(arch_name);
        if ( fp == null )
            return (null);
        for (int i = 0; i < lines; i++) {
            s = new StringTokenizer(fp.readLine());
            for (int j = 0; s.hasMoreTokens(); 
            A[i][j++] = Double.parseDouble(s.nextToken()));
        }
        fp.close();
        return A;
    }
    
    // receives a file name and returns a matrix of chars (small integers for
    // memory space economy)
    public static char [][] readMatrix(String arch_name) throws IOException {
        BufferedReader fp = openFile(arch_name);
        if ( fp == null )
            return (null);
        // obtaining the number of lines and collumns of the matrix
        StringTokenizer s = new StringTokenizer(fp.readLine());
        int collumns = s.countTokens();
        int lines;
        for (lines = 1; fp.readLine() != null; lines++);
        char [][] A = new char [lines][collumns];
        fp = openFile(arch_name);
        if ( fp == null )
            return (null);
        // reading the matrix
        for (int i = 0; i < lines; i++) {
            s = new StringTokenizer(fp.readLine());
            for (int j = 0; s.hasMoreTokens(); 
            A[i][j++] = (char) Integer.parseInt(s.nextToken()));
        }
        fp.close();
        return A;
    }
}
