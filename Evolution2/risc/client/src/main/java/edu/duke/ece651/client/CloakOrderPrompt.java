package edu.duke.ece651.client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import edu.duke.ece651.shared.Message;

public class CloakOrderPrompt extends Prompt{
  private SelectUnitsPrompt selectUnitsPrompt;  
  private String name;
  
  public CloakOrderPrompt(JFrame frame, ActionListener controlListener,
                           String notifyStr, Player player){
    super(frame,  controlListener,  notifyStr, player);
    this.player = player;
  }

  @Override
  public void actionPerformed(ActionEvent event){
    System.out.println("CloakOrderPrompt  --- " + event.getActionCommand());
    String cmds[] = this.parseEvent(event);     
    try{
      if(cmds[0].equals("selected src terr")){        
        // store source territory input
        String desTerrStr = cmds[2];
        message.setDestTerritory(desTerrStr);
        if(gmap.hasCloak(this.player.getName()) == false){
          throw new Exception("You do not have cloak resource");
        }
        
        if( gmap.getPly_resource().consumeTech(this.player.getName(), 100) == false ){
          throw new Exception("You do not have enough resources");
        }
        this.messages.add(message);
        
        ActionEvent ae = new ActionEvent(this,0,this.notifyStr);
        this.controlListener.actionPerformed(ae);        
      }
    }catch(Exception except){
        ActionEvent ae = new ActionEvent(this,0,"displayOrderPanel");
        this.controlListener.actionPerformed(ae);              
      throwException(except);
    }    
    
  }
  
  // 1
  public void start() throws Exception{
    message = new Message();
    message.setType("Use_Cloak");
    message.setMessageType("Execute");
    message.setPlayerName(this.player.getName());
    selectTerritory("Select territory you want to cloak","selected src terr");    
  }




}
