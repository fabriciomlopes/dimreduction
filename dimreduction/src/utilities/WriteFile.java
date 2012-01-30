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
/*** This class implements methods for writing files containing a matrix.***/
/***************************************************************************/
package utilities;

import fs.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFile {
    private static FileWriter out = null;
    
    public WriteFile() {
    }
    
    public static void WriteFile(String path, double [][] M, int lines,
            int collumns){
        try{
            out = new FileWriter(new File(path), false);
            for (int i = 0; i < lines; i++ ){
                for (int j = 0; j < collumns; j++){
                    out.write(M[i][j]+" ");
                }
                out.write("\n");
                out.flush();
            }
            out.close();
        }catch(IOException error){
            throw new FSException("Error when save normalized file. "+error, false);
        }
    }
}
