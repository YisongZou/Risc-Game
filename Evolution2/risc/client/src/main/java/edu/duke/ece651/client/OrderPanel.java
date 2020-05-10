package edu.duke.ece651.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;

public class OrderPanel implements ActionListener {
  Message m;
  GameMap gmap;
  JFrame frame;

  Player player;
  ButtonsPanel buttonsPanel;
  ActionListener controlListener;
  
  public OrderPanel(JFrame frame,Player player,
                             GameMap gmap, ActionListener controlListener){
    m = new Message();
    this.frame = frame;
    this.player = player;
    this.gmap = gmap;
    this.controlListener = controlListener;
  }

  public void getOrder(){
    selectTerritory("Select source territory",
                    "selectSource",
                    this.gmap.getPlayerTerritoryStrings(this.player.getName()));
  }
  
  public void selectTerritory(String promptString,
                              String eventString,
                              ArrayList<String> ts){
    buttonsPanel = new ButtonsPanel(400,0,eventString,
                                    promptString, ts,this);
    buttonsPanel.generatePanel();
    this.frame.add(buttonsPanel.getPanel());
    
  }
  
  @Override
  public void actionPerformed(ActionEvent e){
    String command = e.getActionCommand();
    String cmds[] = command.split(",");

    try{
      // try connecting to the server
      if(cmds[0].equals("selectSource")){
        String srcTerr= cmds[2];
        m.setSrcTerritory(srcTerr);        
        selectTerritory("Select dest territory",
                        "selectDest",
                        this.gmap.getPlayerTerritoryStrings(this.player.getName()));
      }
      if(cmds[0].equals("selectDest")){
        String destTerritory = cmds[2];
        m.setDestTerritory(destTerritory);
        ActionEvent ae = new ActionEvent(this,0,"finishedOrder");        
        this.controlListener.actionPerformed(ae);
      }
    }
    catch(Exception except){
      except.printStackTrace();
    }      
  } 
}

