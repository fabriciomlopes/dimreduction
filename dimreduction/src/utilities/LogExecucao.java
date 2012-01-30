/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.FileWriter;
import javax.swing.JOptionPane;

/**
 *
 * @author fpereira
 */
public class LogExecucao {
    public static void gravar(String texto){
      String conteudo = texto;
      try{
         // o true significa q o arquivo será constante
         FileWriter x = new FileWriter("tempos.txt", true);


         conteudo += "\n\r"; // criando nova linha e recuo no arquivo
         x.write(conteudo); // armazena o texto no objeto x, que aponta para o arquivo
         x.close(); // cria o arquivo
//         JOptionPane.showMessageDialog(null,"Arquivo gravado com sucesso","Concluído",JOptionPane.INFORMATION_MESSAGE);
      }
      // em caso de erro apreenta mensagem abaixo
      catch(Exception e){
         JOptionPane.showMessageDialog(null,e.getMessage(),"Atenção",JOptionPane.WARNING_MESSAGE);
      }
    }

    public static void gravarResultado(String texto){
      String conteudo = texto;
      try{
         // o true significa q o arquivo será constante
         FileWriter x = new FileWriter("resultado.txt", true);


         conteudo += "\n\r"; // criando nova linha e recuo no arquivo
         x.write(conteudo); // armazena o texto no objeto x, que aponta para o arquivo
         x.close(); // cria o arquivo
//         JOptionPane.showMessageDialog(null,"Arquivo gravado com sucesso","Concluído",JOptionPane.INFORMATION_MESSAGE);
      }
      // em caso de erro apreenta mensagem abaixo
      catch(Exception e){
         JOptionPane.showMessageDialog(null,e.getMessage(),"Atenção",JOptionPane.WARNING_MESSAGE);
      }
    }
}
