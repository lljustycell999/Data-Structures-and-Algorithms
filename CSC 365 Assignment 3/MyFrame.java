package csc.pkg365.assignment.pkg3.pkg1;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class MyFrame extends JFrame implements ActionListener{
    public JButton button;
    public TextArea textArea;
    public TextArea URLsToChoose;
    private final JTextField firstTextField;
    private final JTextField secondTextField;
    private final JLabel srcLabel;
    private final JLabel dstLabel;
    String URLOne = null;
    String URLTwo = null;
    
    MyFrame(){
        
        this.setTitle("Wikipedia Mini Graph Traversal");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new FlowLayout());
         
        button = new JButton("Submit");
        
        button.addActionListener(this);
        
        srcLabel = new JLabel("Source URL:");
        firstTextField = new JTextField();
        firstTextField.setPreferredSize(new Dimension(250, 20));
        
        dstLabel = new JLabel("Destination URL:");
        secondTextField = new JTextField();
        secondTextField.setPreferredSize(new Dimension(250, 20));
        
        textArea = new TextArea();
        textArea.setEditable(false);
        
        URLsToChoose = new TextArea();
        URLsToChoose.setEditable(false);
        
        this.add(button);
        this.add(srcLabel);
        this.add(firstTextField);
        this.add(dstLabel);
        this.add(secondTextField);
        this.add(textArea);
        this.add(URLsToChoose);
        this.pack();
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == button){
            URLOne = firstTextField.getText();
            URLTwo = secondTextField.getText();
        }
    }
}
