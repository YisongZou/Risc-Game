package edu.duke.ece651.client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;


public class MoveSpyPrompt extends Prompt {

  public MoveSpyPrompt(JFrame frame, ActionListener controlListener,
                         String notifyStr, Player player){
    super(frame,  controlListener,  notifyStr, player);
    this.message = new Message();
  }
  
  @Override
  public void actionPerformed(ActionEvent event){
    System.out.println("MoveSpyPrompt - " + event.getActionCommand());
    String cmds[] = this.parseEvent(event);
    
    // 1 
    if(cmds[0].equals("selected source territory")){
      String srcTerrStr = cmds[2];
      message.setSrcTerritory(srcTerrStr);
      selectDestTerr(cmds);
    }

    // 2
    if(cmds[0].equals("selected dest territory")){
      String destTerrStr = cmds[2];
      message.setDestTerritory(destTerrStr);
      message.setType("Move_Spy");
      message.setMessageType("Execute");
      message.setPlayerName(this.player.getName());
      this.messages.add(message);
      this.controlListener.actionPerformed(new ActionEvent(this,0,this.notifyStr));
    }    
  }

  public void start() throws Exception{
    ArrayList<String> ts = new ArrayList<>();
    for(String terrName:this.gmap.getTerritoryNames()){
      int numSpies = this.gmap.getSpyNum(this.player.getName(), terrName);
      if(numSpies > 0){
        ts.add(terrName);
      }
    }
    if(ts.size() < 1){
      throw new Exception("You do not have any spies to move");
    }
    message.setPlayerName(this.player.getName());
    message.setMessageType("Execute");    
    message.setType("Move");
    selectTerritory("Please select source territory","selected source territory",ts);
  }

  // 2 Prompt user to select territory
  // 3 Select destination territory
  private void selectDestTerr(String[] cmds){
    String srcTerrStr = cmds[2];
    Territory srcTerr = this.gmap.getTerritory(srcTerrStr);
    ArrayList<String>ts = this.gmap.getNeighborStrings(srcTerr);
    ts.remove(srcTerrStr);
    // message.setSrcTerritory(srcTerrStr);
    // display prompt for destination
    selectTerritory("Select destination territory", "selected dest territory", ts);
  }  
  
}

