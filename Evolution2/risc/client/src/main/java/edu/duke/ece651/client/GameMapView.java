package edu.duke.ece651.client;

import java.util.HashMap;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;

public class GameMapView implements View{
  Panel panel;
  public GameMapView(){
  }
  
  public Panel makeInitPanel(ActionListener listener){
    panel= new Panel();
    panel.setBounds(0, 0, 400, 400);
    
    JButton b = new JButton("Narnia");
    b.setActionCommand("Narnia");
    b.setBounds(10,20, 100,100);
    b.addActionListener(listener);
    panel.add(b);

    JButton b3 = new JButton("Oz");
    b3.setActionCommand("Oz");
    b3.setBounds(10,120, 100,100);
    b3.addActionListener(listener);
    panel.add(b3);


    JButton b2 = new JButton("Midkemia");
    b2.setActionCommand("Midkemia");
    b2.setBounds(110,40, 120,80);
    b2.addActionListener(listener);
    panel.add(b2);



    JButton b4 = new JButton("Gondor");
    b4.setActionCommand("Gondor");
    b4.setBounds(110,120, 120,80);
    b4.addActionListener(listener);
    panel.add(b4);    
    
    return panel;
  }

  
  public Panel makeInitPanel(ActionListener listener,int groupId){
    panel= new Panel();
    panel.setBounds(0, 0, 400, 400);
    panel.setLayout(null);


    JButton b = new JButton("Narnia");
    b.setActionCommand("AssignUnits,Narnia");
    b.setBounds(10,20, 100,100);
    b.addActionListener(listener);

    panel.add(b);

    JButton b3 = new JButton("Oz");
    b3.setActionCommand("AssignUnits,Oz");
    b3.setBounds(10,120, 100,100);
    b3.addActionListener(listener);

    panel.add(b3);
    
    
    JButton b2 = new JButton("Midkemia");
    b2.setActionCommand("AssignUnits,Midkemia");
    b2.setBounds(110,40, 120,80);
    b2.addActionListener(listener);
    panel.add(b2);

    JButton b4 = new JButton("Gondor");
    b4.setActionCommand("AssignUnits,Gondor");
    b4.setBounds(110,120, 120,80);
    b4.addActionListener(listener);
    panel.add(b4);

    if(groupId == 1){
      b.setEnabled(false);
      b3.setEnabled(false);      
    }
    else{
      b2.setEnabled(false);
      b4.setEnabled(false);      
    }
    
    return panel;
  }

  
  public HashMap<String, String> getInputs() {
    return new HashMap<String, String>();
  }

  public Panel getPanel(){
    return this.panel;
  }
}
