package edu.duke.ece651.client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;

public class MoveOrderPrompt extends Prompt  {

  private SelectUnitsPrompt selectUnitsPrompt;

  public MoveOrderPrompt(JFrame frame, ActionListener controlListener,
                         String notifyStr, Player player){
    super(frame,  controlListener,  notifyStr, player);
  }
  
  @Override
  public void actionPerformed(ActionEvent event){
    System.out.println("MoveOrderPrompt - " + event.getActionCommand());
    String cmds[] = this.parseEvent(event);
    try{
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
        selectDestTerr(cmds);
        selectUnitsPrompt =
          new SelectUnitsPrompt(this.frame,this,
                                "selectunitsprompt: selected units",
                                this.player,this.message, "Move");      
        selectUnitsPrompt.promptLevelUnitsPanel();
      }
      // 3
      if(cmds[0].equals("selectunitsprompt: selected units")){
        messages.addAll(selectUnitsPrompt.getMessages());
        ActionEvent ae = new ActionEvent(this,0,this.notifyStr);
        this.controlListener.actionPerformed(ae);
      }
    }
    catch(Exception exception){
      throwException(exception);
      exception.printStackTrace();
    }
  }

  // 1 Set up move
  public void start(){
    message = new Message();
    message.setPlayerName(this.player.getName());
    message.setMessageType("Execute");    
    message.setType("Move");
    selectTerritory("Please select source territory","selected source territory");
  }
  
  // 2 Prompt user to select territory
  // 3 Select destination territory
  private void selectDestTerr(String[] cmds) throws Exception{
    String srcTerrStr = cmds[2];
    ArrayList<String> ts  = this.player.getGmap().
        getPlayerTerritoryStrings(this.player.getName());
    ts.remove(srcTerrStr);
    if(ts.size() < 1){
      throw new Exception("You cannot move your troops anywhere from this territory");
    }
    // message.setSrcTerritory(srcTerrStr);
    // display prompt for destination
    selectTerritory("Select destination territory", "selected dest territory", ts);
  }
  
}
