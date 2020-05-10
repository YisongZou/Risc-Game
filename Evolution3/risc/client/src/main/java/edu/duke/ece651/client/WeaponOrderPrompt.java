package edu.duke.ece651.client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;

public class WeaponOrderPrompt extends Prompt{
  private UnitUpgradeOrderPrompt unitUpgradeOrderPrompt;
  private SpyUpgradeOrderPrompt spyUpgradeOrderPrompt;  
  private GameMapPanel gameMapPanel;
  private int techCostVirus;
  private int techCostBomb;
  
  public  WeaponOrderPrompt(JFrame frame, ActionListener controlListener,
                             String notifyStr, Player player){
    super(frame,  controlListener,  notifyStr,player);
    this.gmap = player.getGmap();
    this.techCostVirus = 500;
    this.techCostBomb = 500;
  }
  
  @Override
  public void actionPerformed(ActionEvent event){
    System.out.println("WeaponOrderPrompt  --- " + event.getActionCommand());    
    String cmds[] = this.parseEvent(event);
    try{
      /* Special weapons methods */
      if( (cmds.length > 1) && cmds[2].equals("Bioweapon")){
        Message message = new Message();
        message.setType("Virus");
        message.setMessageType("Execute");
        message.setPlayerName(this.player.getName());
        if (player.getGmap().getPly_resource().getTechRsc(player.getName()) < this.techCostVirus) {
          throw new Exception("There is not enough techology resource"+
                              " for bioweapon technology");
        }
        messages.add(message);
        this.controlListener.actionPerformed(new ActionEvent(this,0,this.notifyStr));                
      }

      if( (cmds.length > 1) && cmds[2].equals("Nuclear")){
        Message message = new Message();
        message.setType("Nuclear");
        message.setMessageType("Execute");
        message.setPlayerName(this.player.getName());
        if (player.getGmap().getPly_resource().getTechRsc(player.getName()) < this.techCostBomb) {
          throw new Exception("There is not enough techology resource"+
                              " for nuclear technology");
        }
        messages.add(message);
        this.controlListener.actionPerformed(new ActionEvent(this,0,this.notifyStr));
      }
      if( (cmds.length > 1) && cmds[2].equals("Done(Go back to make order)")){
        displayOrderPanel();
      }
      
    }catch(Exception except){
      displayOrderPanel();
      throwException(except);
      
    }
  }

  public void start(){
    gameMapPanel = new GameMapPanel(0, 0, "panel", null,
                                    this.player.getGmap().getTerritoryNames(),
                                    this,this.player);
    gameMapPanel.generatePanel();

    
    ArrayList<String> buttonStrings = new ArrayList<>();
    String promptString = "What would you like to launch?";
    buttonStrings.add("Nuclear");
    buttonStrings.add("Bioweapon");    
    buttonStrings.add("Done(Go back to make order)");

    this.bpanel = new ButtonsPanel(650, 0, "MakeOrderPrompt",
                                   promptString, buttonStrings, this);
    this.bpanel.generatePanel();

    this.frame.getContentPane().removeAll();
    this.frame.add(this.gameMapPanel.getPanel());    
    this.frame.add(this.bpanel.getPanel());
    this.frame.getContentPane().revalidate();       
    this.frame.getContentPane().repaint();         
  }

  public void displayOrderPanel(){
    this.controlListener.actionPerformed(new ActionEvent(this, 0, this.notifyStr));
  }
  
  
}
