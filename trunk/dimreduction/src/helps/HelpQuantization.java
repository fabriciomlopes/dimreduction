package helps;

import fs.FSException;
import java.awt.Desktop;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent.EventType;

public class HelpQuantization extends javax.swing.JFrame {
    
    public HelpQuantization() {
        initComponents();
        try{
            jTextPane1.setPage(getClass().getResource("/helps/quantization.html"));
            jTextPane1.setCaretPosition(0);//visualize the beginning of the help text.
        }catch(IOException error){
            throw new FSException("Error on Help Panel." + error, false);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Help - Input Data Panel");

        jPanel1.setBackground(new java.awt.Color(154, 200, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(500, 40));
        jPanel1.setLayout(new java.awt.CardLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(5, 75, 4));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Help - Quantization Panel");
        jPanel1.add(jLabel1, "card2");

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(182, 480));

        jTextPane1.setContentType("text/html");
        jTextPane1.setEditable(false);
        jTextPane1.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                jTextPane1HyperlinkUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(jTextPane1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-700)/2, (screenSize.height-600)/2, 700, 600);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextPane1HyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_jTextPane1HyperlinkUpdate
        try{
            if (evt.getEventType() == EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(evt.getURL().toURI());
                } catch (Exception error) {

                }
            }
        }catch(NoClassDefFoundError error){
            JOptionPane.showMessageDialog(this, "Open URL is available only " +
                    "in Java Runtime Environment (JRE) 1.6 or higher. You can " +
                    "copy the URL and paste in your favorite browser.","Open " +
                    "URL", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jTextPane1HyperlinkUpdate

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}