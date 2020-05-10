package edu.duke.ece651.client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import edu.duke.ece651.shared.Message;

public class UnitUpgradeOrderPrompt extends Prompt {
  private SelectUnitsPrompt selectUnitsPrompt;  
  private String name;
  
  public UnitUpgradeOrderPrompt(JFrame frame, ActionListener controlListener,
                           String notifyStr, Player player){
    super(frame,  controlListener,  notifyStr, player);
    this.player = player;
  }
  
  @Override
  public void actionPerformed(ActionEvent event){
    System.out.println("UnitUpgradeOrderPrompt  --- " + event.getActionCommand());
    String cmds[] = this.parseEvent(event);     
    
    if(cmds[0].equals("selected src terr")){
      // store source territory input
      String desTerrStr = cmds[2];
      message.setDestTerritory(desTerrStr);      
      selectUnitsPrompt =
        new SelectUnitsPrompt(this.frame,this,
                              "selectunitsprompt selected units",
                              this.player,this.message,"Upgrade_Unit");      
      selectUnitsPrompt.promptLevelUnitsPanel();
    }
    
    if(cmds[0].equals("selectunitsprompt selected units")){
      messages.addAll(selectUnitsPrompt.getMessages());
      ActionEvent ae = new ActionEvent(this,0,this.notifyStr);
      this.controlListener.actionPerformed(ae);
    }
    
  }

  // 1
  public void start(){
    message = new Message();
    // Move units from one territory to another
    message.setMessageType("Execute");
    message.setType("Upgrade_Unit");
    message.setPlayerName(this.name);    
    selectTerritory("Select you want to upgrade units from","selected src terr");
  }
  
}
