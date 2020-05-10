package edu.duke.ece651.client;


import java.awt.Panel;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class TextFieldPanel {
  private int x;
  private int y;
  private String panelString;

  private int w;
  private int h;

  // Strings to be displayed
  private String promptString;
  private ArrayList<String> textFields;

  private HashMap<String, JTextField> fields;
  
  Panel panel;
  ActionListener listener;

  public TextFieldPanel(int x, int y,String panelString,
                      String promptString,
                      ArrayList<String> textFields,
                      ActionListener listener){
    this.x = x;
    this.y = y;
    
    this.panelString = panelString;
    this.promptString = promptString;

    this.textFields = textFields;

    this.w = 280;
    this.h = 750;
    
    this.listener = listener;

    this.fields = new HashMap<>();
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

    JLabel nameLabel = new JLabel("<html>"+this.promptString.replace("\n","<br/>")+"</html>");
    nameLabel.setBounds(0, 0, this.w, promptHeight);
    panel.add(nameLabel);

    int i = promptHeight;
    for (String s: textFields){
      JLabel label = new JLabel(s);
      label.setBounds(0, i, this.w, buttonHeight);
      i += buttonHeight;
      panel.add(label);
      
      JTextField field = new JTextField("");
      field.setBounds(0,i, this.w, buttonHeight);
      i += buttonHeight;
      panel.add(field);
      this.fields.put(s, field);
      i += buttonHeight;

      
    }
    
    JButton button = new JButton("Ok");
    button.setBounds(0, i,this.w,buttonHeight);
    button.setActionCommand(String.format("%s,%s",this.panelString,"Ok"));
    button.addActionListener(this.listener);
    panel.add(button);


    
  }

  public HashMap<String, JTextField> getFieldsMap(){
      return this.fields;
  }
  public ArrayList<String> getTextFields(){
    return this.textFields;
  }

  public String getInput(int i){
    return this.fields.get(this.textFields.get(i)).getText();
  }
  
  public String getInput(String s){
    return this.fields.get(s).getText();
  }
  

  public Panel getPanel(){
    return this.panel;
  }

  
}
