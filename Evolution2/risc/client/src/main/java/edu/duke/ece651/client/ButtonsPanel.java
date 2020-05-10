package edu.duke.ece651.client;

import java.awt.Panel;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionListener;


public class ButtonsPanel {
  private int x;
  private int y;
  private String panelString;

  private int w;
  private int h;

  // Strings to be displayed
  private String promptString;
  private ArrayList<String> promptButtons;
  
  Panel panel;
  ActionListener listener;
  
  public ButtonsPanel(int x, int y,String panelString,
                      String promptString,
                      ArrayList<String> promptButtons,
                      ActionListener listener){
    this.x = x;
    this.y = y;
    
    this.panelString = panelString;
    this.promptString = promptString;

    this.promptButtons = promptButtons;

    this.w = 400;
    this.h = 700;
    
    this.listener = listener;
  }

  public void generatePanel(){
    int promptHeight = 400;
    int buttonHeight = 30;
    generatePanel(promptHeight, buttonHeight);
  }
  
  public void generatePanel(int promptHeight, int buttonHeight){
    panel = new Panel();
    panel.setBounds(this.x, this.y, this.w, this.h);
    panel.setLayout(null);

    JLabel nameLabel = new JLabel("<html>"+this.promptString.replace("\n","<br/>")+"</html");
    nameLabel.setBounds(0, 0, this.w, promptHeight);
    panel.add(nameLabel);
    
    int i = 0;

    for (String s: promptButtons){
      JButton b = new JButton(s);
      b.setBounds(0, promptHeight+buttonHeight*(i+1), this.w,buttonHeight);
      b.setActionCommand(String.format("%s,%s,%s",this.panelString,i,s));
      b.addActionListener(this.listener);
      panel.add(b);
      i++;
    }
  }

  public Panel getPanel(){
    return this.panel;
  }
  
}
