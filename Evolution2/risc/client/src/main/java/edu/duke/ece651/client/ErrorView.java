package edu.duke.ece651.client;

import java.util.HashMap;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


public class ErrorView implements View {
  public String messageStr;
  private Panel p;
  
  public ErrorView(){
    messageStr = "";
  }
  public Panel makeInitPanel(ActionListener listener, String m){
    this.messageStr = m;
    return makeInitPanel(listener);
  }
  public Panel makeInitPanel(ActionListener listener){
    p = new Panel();
    p.setBounds(40, 100, 400, 100);
    p.setBackground(Color.white);    
    p.setLayout(null);
    
    JLabel errLabel = new JLabel("Error occured");
    errLabel.setBounds(5, 5, 200, 20);
    p.add(errLabel);

    JLabel messageLabel = new JLabel(this.messageStr);
    messageLabel.setBounds(5,30, 400, 20);
    p.add(messageLabel);

    JButton b = new JButton("OK");
    b.setActionCommand("acknowledgeError");
    b.setBounds(130, 55, 100,30);
    b.addActionListener(listener);
    p.add(b);
    return p;
  }

  public Panel getPanel(){
    return p;
  }

  public HashMap<String, String> getInputs() {
    HashMap<String, String> inputs = new HashMap<>();
    return inputs;
  }
}
