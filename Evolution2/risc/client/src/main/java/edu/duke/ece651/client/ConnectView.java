package edu.duke.ece651.client;

import java.util.HashMap;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ConnectView implements View {
  JFrame frame;
  JTextField ipText;
  JTextField nameText;
  JTextField passwordText;  

  JLabel ipLabel;
  JLabel nameLabel;
  JLabel passwordLabel;
  Panel panel;


  public ConnectView(){
  }

  public Panel makeInitPanel(ActionListener listener){
    panel= new Panel();
    panel.setBounds(0, 0, 400, 400);
    panel.setLayout(null);
    // panel.setPreferredSize(new Dimension(400,400));
    
    ipLabel = new JLabel("IP Address:");
    ipLabel.setBounds(130, 20, 100, 20); 
    panel.add(ipLabel);
    
    ipText = new JTextField("127.0.0.1");
    ipText.setBounds(130, 50, 100, 20);
    panel.add(ipText);

    // name label
    nameLabel = new JLabel("Player Name:");
    nameLabel.setBounds(130, 80, 100, 20);
    panel.add(nameLabel);
    
    nameText = new JTextField("John");
    nameText.setBounds(130, 100, 100, 20);    
    panel.add(nameText);

    // password
    passwordLabel = new JLabel("Password:");
    passwordLabel.setBounds(130, 120, 100, 20);                                              
    panel.add(passwordLabel);
    
    passwordText = new JTextField("password");
    passwordText.setBounds(130, 150, 100, 20);                                        
    panel.add(passwordText);

    JButton b = new JButton("Connect");
    b.setActionCommand("Connect");
    b.setBounds(130, 180, 100,30);
    b.addActionListener(listener);
    panel.add(b);
    return panel;
  }

  public HashMap<String, String> getInputs() {
    HashMap<String, String> inputs = new HashMap<>();
    inputs.put("ipText", ipText.getText());            
    inputs.put("playerName", nameText.getText());
    inputs.put("passwordText", passwordText.getText());
    
    return inputs;
  }
  public  Panel getPanel(){
    return this.panel;
  }
}
