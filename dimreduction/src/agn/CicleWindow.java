
package agn;

/**
 *
 * @author fpereira
 */

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.table.*;
import utilities.IOFile;

public class CicleWindow extends JFrame{
  
    public CicleWindow(TableModel model){
        super("Cycles found");       
        setLocationRelativeTo(null);
        buildWindow(model);
    }

    private void buildWindow(TableModel model) {
        final JTable jT_Table = new JTable(model);
        jT_Table.setPreferredScrollableViewportSize(new 
             Dimension(350, 180));

        TableRowSorter<TableModel> sorter;
        sorter = new TableRowSorter<TableModel>(model);
        jT_Table.setRowSorter(sorter);	

        Container c = getContentPane();
        c.setLayout(new FlowLayout());
        
        JLabel jL_Size = new JLabel("Number of cycles found:  " + jT_Table.getRowCount());
        c.add(jL_Size); 
        
        JScrollPane scrollPane = new JScrollPane(jT_Table);
        c.add(scrollPane);
        
        JButton jB_ExportCSV = new JButton("Export Data");
        c.add(jB_ExportCSV);
        jB_ExportCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IOFile.saveTableInFile(jT_Table);
            }
        });     

        JButton jB_Exit = new JButton("Exit");
        c.add(jB_Exit);
        jB_Exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });           

        setSize(400, 300);
        setVisible(true);
    }
}