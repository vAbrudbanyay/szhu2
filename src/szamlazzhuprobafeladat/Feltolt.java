package szamlazzhuprobafeladat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
 
public class Feltolt extends JFrame {
 
    private JPanel pnFelso = new JPanel();
    private JPanel pnKozepso = new JPanel();
    private JPanel pnAlso = new JPanel();
    private JTextField tfEmail = new JTextField("Email cím", 20);
    private JPasswordField tfJelszo = new JPasswordField("Jelszó", 20);
    private JTextField termek = new JTextField("termék név", 20);
    private JTextField ar = new JTextField("ár", 20);
    private JButton btAdd = new JButton("+");
    private JButton btTorol = new JButton("X");
    private JButton btMegse = new JButton("Töröl");
    private JButton btSzamla = new JButton("Számláz");
    static Feltolt myFrame;
    static int countMe = 0;
    JPanel mainPanel;
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            huiMutat();
        });
    }
 
    private static void huiMutat() {
        myFrame = new Feltolt();
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.uiElokeszit();
        myFrame.pack();
        myFrame.setResizable(true);
        myFrame.setVisible(true);
    }
 
    private void uiElokeszit() {
 
      // középső panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(termek);
        mainPanel.add(ar);
        mainPanel.add(btAdd);
        btAdd.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.add(new panelAdd());
                myFrame.pack();
            }
        });
         
        // felső panel
        pnFelso.add(tfEmail);
        pnFelso.add(tfJelszo);
        
        // alsó panel
        pnAlso.add(btMegse); btMegse.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed(ActionEvent e) {  
                tfEmail.setText("");
                tfJelszo.setText("");
                termek.setText("");
                ar.setText("");
            }
          });
          pnAlso.add(btSzamla);btSzamla.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed(ActionEvent e) {  
                szamlaEll();
            }
          });
 
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(pnFelso, BorderLayout.PAGE_START);
        getContentPane().add(pnAlso, BorderLayout.PAGE_END);
    }
 
    private class panelAdd extends JPanel {
         
        panelAdd ez;
 
        public panelAdd() {
            super();
            ez = this;
            add(new JTextField("termék név", 20));
            add(new JTextField("ár", 20));
            add(btAdd);
            JButton btTorol = new JButton("X");
              btTorol.addActionListener(new ActionListener(){

                  @Override
                  public void actionPerformed(ActionEvent e) {
                      ez.getParent().remove(ez);
                      myFrame.pack();
                  }
              });
            add(btTorol);
        }
    }
    
    
private void szamlaEll (){
    QueryMng mng = new QueryMng();
    // String msg = mng.feltolt(tfEmail.getText(), tfJelszo.getText(), termek.getText(), ar.getText());
    //JOptionPane.showMessageDialog(this, msg, "Visszajelzés", JOptionPane.INFORMATION_MESSAGE);
  }

    
}