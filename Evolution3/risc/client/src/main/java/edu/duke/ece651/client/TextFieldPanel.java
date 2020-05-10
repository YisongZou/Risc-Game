package edu.duke.ece651.client;



import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;


public class TextFieldPanel extends PromptPanel{
  private int w;
  private int h;


  private ArrayList<String> textFields;
  private HashMap<String, JTextField> fields;
  

  public TextFieldPanel(int x, int y,String panelString,
                      String promptString,
                      ArrayList<String> textFields,
                      ActionListener listener){
    super(x, y, panelString, promptString, null, listener);
    
    this.textFields = textFields;

    this.w = 600;
    this.h = 750;

    this.fields = new HashMap<>();
  }
  
  public void generatePanel(){
    int promptHeight = 200;
    int buttonHeight = 30;
    generatePanel(promptHeight, buttonHeight);
  }
  

  public void generatePanel(int promptHeight, int buttonHeight){
    panel = new JLayeredPane();
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
  


  
}
