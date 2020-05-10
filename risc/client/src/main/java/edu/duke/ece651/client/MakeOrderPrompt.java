package edu.duke.ece651.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;

public class MakeOrderPrompt extends Prompt{
  private Client client;
  private CloakOrderPrompt cloakOrderPrompt;
  private UpgradeOrderPrompt upgradeOrderPrompt;
  private WeaponOrderPrompt weaponOrderPrompt;  
  private MoveOrderPrompt moveOrderPrompt;
  private AttackOrderPrompt attackOrderPrompt;
  private MoveSpyPrompt moveSpyPrompt;
  private GameMapPanel gameMapPanel;

  
  public MakeOrderPrompt(JFrame frame, ActionListener controlListener,
                         String notifyStr, Client client){
    super(frame,  controlListener,  notifyStr, client.getPlayer());
    this.client = client;

  }
  
  @Override
  public void actionPerformed(ActionEvent event) {
    try{

      // stub method to be implemented
      System.out.println("PlayerController - " + event.getActionCommand());
      String cmds[] = this.parseEvent(event); 

      if(cmds[0].equals("displayOrderPanel")){
        displayOrderPanel();
      }
      
      /* Move Order  */
      if((cmds.length > 1) && cmds[2].equals("Move")){
        moveOrderPrompt = new MoveOrderPrompt(this.frame,this,
                                              "MoveOrderPrompt finished",
                                              this.player);
        moveOrderPrompt.start();
      }

      if(cmds[0].equals("MoveOrderPrompt finished")){
        this.player.movedFlag = 1;
        this.messages.addAll(moveOrderPrompt.getMessages());
        displayOrderPanel();
      }
    
      /* Attack Order */
      if((cmds.length > 1) && cmds[2].equals("Attack")){      
        attackOrderPrompt = new AttackOrderPrompt(this.frame,this,
                                                  "AttackOrderPrompt finished",
                                                  this.player);
        attackOrderPrompt.start();
      }
      if(cmds[0].equals("AttackOrderPrompt finished")){
        this.player.movedFlag = 1;        
        this.messages.addAll(attackOrderPrompt.getMessages());
        displayOrderPanel();
      }

      // Upgrade orders
      if((cmds.length > 1) && cmds[2].equals("Make upgrade")){
        upgradeOrderPrompt = new UpgradeOrderPrompt(this.frame,this,
                                                    "UpgradeOrder finished",
                                                    this.player);
        upgradeOrderPrompt.start();
      }
      if(cmds[0].equals( "UpgradeOrder finished")){
        this.player.upgradeFlag = 1;            
        this.messages.addAll(upgradeOrderPrompt.getMessages());
        displayOrderPanel();
      }
      /* Supeprweapons button */
      if((cmds.length > 1) && cmds[2].equals("Superweapon")){
        weaponOrderPrompt = new WeaponOrderPrompt(this.frame,this,
                                                    "WeaponOrder finished",
                                                    this.player);
        weaponOrderPrompt.start();
      }
      if(cmds[0].equals( "WeaponOrder finished")){
        this.messages.addAll(weaponOrderPrompt.getMessages());
        displayOrderPanel();
      }
      
      /* Move spy button pressed */
      if( (cmds.length > 1) && cmds[2].equals("Move Spy")){
        moveSpyPrompt = new MoveSpyPrompt(this.frame,this,
                                          "MoveSpy finished",this.player);
        moveSpyPrompt.start();
      }
      if( cmds[0].equals("MoveSpy finished")){
        this.player.movedFlag = 1;        
        this.messages.addAll(moveSpyPrompt.getMessages());
        displayOrderPanel();        
      }
      
      if( (cmds.length > 1) && cmds[2].equals("Research Cloak")){
        researchCloak();
      }

      if((cmds.length > 1) && cmds[2].equals("Use Cloak")){                
        cloakOrderPrompt = new CloakOrderPrompt(this.frame, this, "Usecloakfinished", this.player);
        cloakOrderPrompt.start();
      }
      if( cmds[0].equals("Usecloakfinished")){
        this.messages.addAll(cloakOrderPrompt.getMessages());
        displayOrderPanel();
      }      
            
      if((cmds.length > 1) && cmds[2].equals("Done(Finish Turn)")){        
        this.client.sendMessage(this.messages);
        this.messages = new ArrayList<>();
        this.client.waitThenAction();        
      }
      
      if((cmds.length > 1) && cmds[2].equals("Leave Game")){
        this.client.sendMessage(this.player.messages);
        this.messages = new ArrayList<>();          
        this.frame.getContentPane().removeAll();
        this.clearMessages();
        this.controlListener.actionPerformed(new ActionEvent(this,0,"playerLeftGame"));
      }
      
    }
    catch(Exception except){
      throwException(except);
    }
  }

  
  
  public void displayOrderPanel() {
    try{
      this.frame.getContentPane().removeAll();
      printMessagesList();
      System.out.println("player - display order panel");      
      // generate game map panel
      gameMapPanel = new GameMapPanel(0, 0, "panel", null,
                                      this.player.getGmap().getTerritoryNames(),
                                      this,this.player);
      gameMapPanel.generatePanel();
      this.frame.getContentPane().add(this.gameMapPanel.getPanel());
      
      // generate buttons panel
      ArrayList<String> buttonStrings = new ArrayList<>();
      if (this.player.upgradeFlag == 0) {
        buttonStrings.add("Move");
        buttonStrings.add("Move Spy");
        buttonStrings.add("Attack");
      }
      if (this.player.movedFlag == 0) {
        buttonStrings.add("Make upgrade");
      }
      
      // Cloak Option, 2: cloak researched
      System.out.println("-------------PRINT HAS_CLOAK ARRAY-----------------");
      for (String pl : this.gmap.getPlayerNames()) {
         System.out.println("player: " + pl);
         if (this.gmap.hasCloak(pl)) {
           System.out.println("True");
         }
         else {
           System.out.println("False");
         }
      }
      
      if(this.gmap.hasCloak(this.player.getName())){
        buttonStrings.add("Use Cloak");                  
      } else if(this.player.researchCloak == 0){
        buttonStrings.add("Research Cloak");
      }
      
      buttonStrings.add("Superweapon");
      buttonStrings.add("Done(Finish Turn)");
      buttonStrings.add("Leave Game");
      
      String promptString = this.player.promptGenerator();
      this.bpanel = new ButtonsPanel(650, 0, "MakeOrderPrompt",
                                     promptString, buttonStrings, this);
      this.bpanel.generatePanel();
      this.frame.add(this.bpanel.getPanel());
      this.frame.getContentPane().revalidate();
      this.frame.repaint();      
      } 
    catch(Exception except){
      //throwException(except);
      except.printStackTrace();
    }
  }

  public void researchCloak() throws Exception{
    Message m = new Message();
    m.setType("Research_Cloak");
    m.setMessageType( "Execute");
    m.setPlayerName( this.player.getName());
    int curr_tech = player.getGmap().getPly_resource().getTechLevel(player.getName());    
    if(curr_tech < 3){
      throw new Exception("You do not meet the minimum tech requirement");
    }
    this.player.getGmap().getPly_resource().consumeTech(this.player.getName(), 100);
    this.player.researchCloak = 1;
    messages.add(m);
    displayOrderPanel();
  }

  
  public void printMessagesList(){
    System.out.println("=== Current messages list ===");
    for (Message m: this.messages){
      String s = String.format("%s %s %s %s",m.getType(),m.getSrcTerritory(),
                               m.getDestTerritory(),m.getUnitNum());
      System.out.println(s);
   } 
    System.out.println("==========");
  }

  public void clearMessages(){
    this.messages = new ArrayList<>();
  }
  
}
