package edu.duke.ece651.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;


public class UpgradeOrderPrompt extends Prompt {
  private UnitUpgradeOrderPrompt unitUpgradeOrderPrompt;
  private SpyUpgradeOrderPrompt spyUpgradeOrderPrompt;  
  private GameMapPanel gameMapPanel;
  
  public  UpgradeOrderPrompt(JFrame frame, ActionListener controlListener,
                             String notifyStr, Player player){
    super(frame,  controlListener,  notifyStr,player);
    this.gmap = player.getGmap();
  }
  
  @Override
  public void actionPerformed(ActionEvent event){
    System.out.println("UpgradeOrderPrompt  --- " + event.getActionCommand());    
    String cmds[] = this.parseEvent(event);
    try{
      /* Tech Upgrade */
      if( (cmds.length > 1) && cmds[2].equals("Tech Upgrade")){
        techUpgrade();
      }

      /* Unit Upgrade */
      if( (cmds.length > 1) && cmds[2].equals("Unit Upgrade")){
        unitUpgradeOrderPrompt = new UnitUpgradeOrderPrompt(this.frame,this,
                                                            "unit upgrade order finished",
                                                            this.player);
        unitUpgradeOrderPrompt.start();
      }

      if(cmds[0].equals("unit upgrade order finished")){
        this.messages.addAll(unitUpgradeOrderPrompt.getMessages());
        displayOrderPanel();
      }

      
      if( (cmds.length > 1) && cmds[2].equals("Spy Upgrade")){
        spyUpgradeOrderPrompt = new SpyUpgradeOrderPrompt(this.frame,this,
                                                            "spy upgrade order finished",
                                                            this.player);
        spyUpgradeOrderPrompt.start();        

      }
      
      if (cmds[0].equals("spy upgrade order finished")) {
        this.messages.addAll(spyUpgradeOrderPrompt.getMessages());

        displayOrderPanel();
      }
      
      if( (cmds.length > 1) && cmds[2].equals("Done(Go back to make order)")){
        displayOrderPanel();
      }
    
    } catch(Exception except){
      throwException(except);
    }
  }  
  

  public void start(){

    gameMapPanel = new GameMapPanel(0, 0, "panel", null,
                                    this.player.getGmap().getTerritoryNames(),
                                    this,this.player);
    gameMapPanel.generatePanel();
    
    ArrayList<String> buttonStrings = new ArrayList<>();
    String promptString = "What would you like to upgrade?";
    buttonStrings.add("Unit Upgrade");
    if(this.player.upgradetech_flag == 0){    
      buttonStrings.add("Tech Upgrade");
    }
    buttonStrings.add("Spy Upgrade");    
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
 
  public void techUpgrade() throws Exception{
    if(this.player.upgradetech_flag == 1){
      throw new Exception("You cannot upgrade your tech twice in one turn");
    }
    Message m = new Message();
    m.setMessageType("Execute");
    m.setType("Upgrade_Tech");
    m.setPlayerName(player.getName());
    System.out.print("( Selected (t)ech_Upgrade order)\n");
    int curr_tech = player.getGmap().getPly_resource().getTechLevel(player.getName());
    curr_tech += 1;
    if (curr_tech > 6) {
      throw new Exception("You already reach the highest level");
    }
    int tech_cost = 25 * (curr_tech - 1) * (curr_tech - 2) / 2 + 50;
    if (player.getGmap().getPly_resource().getTechRsc(player.getName()) < tech_cost) {
      throw new Exception("There is not enough techology resource"+
                          " for use to upgrade technology");
    }
    player.upgradetech_flag = 1;
    player.getGmap().getPly_resource().consumeTech(player.getName(), tech_cost);
    messages.add(m);
    displayOrderPanel();
  } 
}
