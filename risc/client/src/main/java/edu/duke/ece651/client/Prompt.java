package edu.duke.ece651.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;


public class Prompt implements ActionListener {
  
  protected String notifyStr;
  protected JFrame frame;
  protected ArrayList<Message> messages;
  protected TextFieldPanel tpanel;
  protected ButtonsPanel bpanel;
  protected Message message;
  protected ActionListener controlListener;
  protected Player player;
  protected GameMap gmap;
  protected GameMapPanel mapPanel;
  
  public Prompt(JFrame frame, ActionListener controlListener, String notifyStr, Player player){
    this.frame = frame;
    this.controlListener = controlListener;
    this.notifyStr = notifyStr;
    this.player = player;
    this.gmap = player.getGmap();
    messages = new ArrayList<>();
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    // stub method to be implemented
  }
  JDialog d;
  protected void throwException(Exception except){
    System.out.println("prompt - throw exception");
    if(d != null){
      d.setVisible(false);
    }
    // make new window and print errror
    d = new JDialog(this.frame, "Error"); 
    // create a label 
    JLabel l = new JLabel("<html>"+except.getMessage()+"</html>"); 
    d.add(l); 
    // setsize of dialog 
    d.setSize(400, 300); 
    // set visibility of dialog 
    d.setVisible(true);       
    except.printStackTrace();
  }

  protected String[] parseEvent(ActionEvent e){
    return e.getActionCommand().split(",");  
  }
  
  protected void selectTerritory(String promptString, String eventStr) {
    GameMap gmap = this.player.getGmap();
    ArrayList<String> ts = gmap.getPlayerTerritoryStrings(this.player.getName());
    selectTerritory(promptString, eventStr, ts);
  }

  protected void selectTerritory(String promptString, String eventStr, ArrayList<String> ts) {
    // Move units from one territory to another
    /*
    bpanel = new ButtonsPanel(0, 0, eventStr, promptString, ts, this);
    bpanel.generatePanel();
    this.frame.getContentPane().removeAll();        
    this.frame.add(bpanel.getPanel());
    this.frame.getContentPane().revalidate();       
    this.frame.repaint(); 
    */
    this.frame.getContentPane().removeAll();
    mapPanel = new GameMapPanel(0, 0, eventStr, promptString, ts, this,this.player);
    mapPanel.generatePanel();
    this.frame.add(mapPanel.getPanel());
    this.frame.getContentPane().revalidate();       
    this.frame.getContentPane().repaint(); 

    
  }
  
  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }
  
  public ArrayList<Message> getMessages() {
    return this.messages;
  }
  
  public void setMessages(ArrayList<Message> messages) {
    this.messages = messages;
  }
}
