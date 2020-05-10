package edu.duke.ece651.client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;

public class AttackOrderPrompt extends Prompt{

  private SelectUnitsPrompt selectUnitsPrompt;  
  private String name;
  private GameMap gmap;
  
  public AttackOrderPrompt(JFrame frame, ActionListener controlListener,
                         String notifyStr, Player player){
    super(frame,  controlListener,  notifyStr,player);
    this.name = player.getName();
    this.gmap = player.getGmap();    
  }


  @Override
  public void actionPerformed(ActionEvent event){
    System.out.println("AttackOrderPrompt - " + event.getActionCommand());
    String cmds[] = this.parseEvent(event);
    // 1 
    if(cmds[0].equals("selected source territory")){
      // parse source territory string
      String srcTerrStr = cmds[2];
      message.setSrcTerritory(srcTerrStr);      
      selectDestTerr(cmds);
    }
    // 2
    if(cmds[0].equals("selected dest territory")){
      // parse destination territory string
      String destTerrStr = cmds[2];
      message.setDestTerritory(destTerrStr); 
      selectDestTerr(cmds);
      selectUnitsPrompt =
        new SelectUnitsPrompt(this.frame,this,
                              "selectunitsprompt: selected units",
                              this.player,this.message,"Attack");      
      selectUnitsPrompt.promptLevelUnitsPanel();
    }
    // 3
    if(cmds[0].equals("selectunitsprompt: selected units")){
      messages.addAll(selectUnitsPrompt.getMessages());
      ActionEvent ae = new ActionEvent(this,0,this.notifyStr);
      this.controlListener.actionPerformed(ae);
    }
    
  }

  // 1 Set up move
  public void start(){
    message = new Message();
    message.setPlayerName(this.player.getName());
    message.setMessageType("Execute");    
    message.setType("Attack");
    selectTerritory("Please select source territory","selected source territory");
  }
  
  
  // 3 Select destination territory
  private void selectDestTerr(String[] cmds){
    // store source territory input
    String srcTerrStr = cmds[2];
    //message.setDestTerritory(srcTerrStr);
  
    ArrayList<String> ts  = this.gmap.getPlayerTerritoryStrings(name);

    List<Territory> terrs = this.gmap.getEnemyNeighbors(this.gmap.
                                                        getTerritory(srcTerrStr),
                                                        this.name);
    ts = new ArrayList<String>();            
    for (Territory t: terrs){
      ts.add(t.getTerritoryName());
    }
    // message.setSrcTerritory(srcTerrStr);
    
    // display prompt for destination
    selectTerritory("Select destination territory", "selected dest territory", ts);
  }
  
}
