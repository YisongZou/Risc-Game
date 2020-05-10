package edu.duke.ece651.client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

import edu.duke.ece651.shared.Message;

public class SpyUpgradeOrderPrompt extends Prompt {
  private SelectUnitsPrompt selectUnitsPrompt;  
  private String name;
  
  public SpyUpgradeOrderPrompt(JFrame frame, ActionListener controlListener,
                           String notifyStr, Player player){
    super(frame,  controlListener,  notifyStr, player);
    this.player = player;
    this.name = this.player.getName();
  }
  
  @Override
  public void actionPerformed(ActionEvent event){
    System.out.println("SpyUpgradeOrderPrompt  --- " + event.getActionCommand());
    String cmds[] = this.parseEvent(event);     
    
    if(cmds[0].equals("selected src terr")){
      // store source territory input
      String desTerrStr = cmds[2];
      this.message.setMessageType("Execute");      
      this.message.setType("Upgrade_Spy");
      this.message.setPlayerName(this.player.getName());
      this.message.setDestTerritory(desTerrStr);
      if(player.getGmap().getPly_resource().getTechRsc(player.getName()) < 20){
        this.controlListener.actionPerformed(new ActionEvent(this,0,this.notifyStr));                
        throwException(new Exception("You do not have enough resources"));

      }
      if(player.getGmap().getTerritory(desTerrStr).getUnit(1).getNumUnits() < 1){
        this.controlListener.actionPerformed(new ActionEvent(this,0,this.notifyStr));        
        throwException(new Exception("You do not have level 1 units (Only level 1 units can become spies)"));

      }
      messages.add(this.message);
      this.player.getGmap().getSpy(this.player.getName()).addSpy(message.getDestTerritory());
      this.controlListener.actionPerformed(new ActionEvent(this,0,this.notifyStr));
    }
        
  }

  // 1
  public void start(){
    message = new Message();
    // Move units from one territory to another

    selectTerritory("Select you want to upgrade units from","selected src terr");
  }

}
