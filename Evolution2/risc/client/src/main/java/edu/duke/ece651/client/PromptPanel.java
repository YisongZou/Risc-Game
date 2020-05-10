package edu.duke.ece651.client;

import javax.swing.JLayeredPane;
import java.util.ArrayList;
import java.awt.event.ActionListener;

public class PromptPanel {
  protected int x;
  protected int y;
  protected String panelString;

  protected int w;
  protected int h;

  // Strings to be displayed
  protected String promptString;
  protected ArrayList<String> promptButtons;
  
  protected JLayeredPane panel;
  protected ActionListener listener;

  public PromptPanel( int x, int y,String panelString,
                      String promptString,
                      ArrayList<String> promptButtons,
                      ActionListener listener){
    this.x = x;
    this.y = y;
    
    this.panelString = panelString;
    this.promptString = promptString;

    this.promptButtons = promptButtons;

    this.w = 500;
    this.h = 800;
    this.listener = listener;
  }
  
  public JLayeredPane getPanel(){
    return this.panel;
  }
  
}
