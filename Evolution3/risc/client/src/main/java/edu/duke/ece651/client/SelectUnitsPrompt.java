package edu.duke.ece651.client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;

public class SelectUnitsPrompt extends Prompt {
  private GameMap gmap;
  private String name;
  private String type;
  private GameMapPanel mapPanel;
  
  
  public SelectUnitsPrompt(JFrame frame, ActionListener controlListener,
                           String notifyStr, Player player, Message message,String type){
    super(frame,  controlListener,  notifyStr,player);
    this.gmap = player.getGmap();
    this.name = player.getName();
    this.message = message;
    this.type = "";    

    this.type = type;
  }
  
  @Override
  public void actionPerformed(ActionEvent event){
    System.out.println("SelectUnitsPrompt - " + event.getActionCommand());
    String cmds[] = this.parseEvent(event);
    try{
      if(cmds[0].equals("selected num units")){
        selectedUnitsParser(cmds);
      }
    }
    catch(Exception except){
      throwException(except);
    }
    
  }

  /*
    Method for making sure number of units is valid
  */  
  protected boolean validNumUnits(String territoryStr) throws Exception{
    System.out.println(territoryStr);
    Territory territory = this.gmap.getTerritory(territoryStr);
    ArrayList<String> textFields = tpanel.getTextFields();
    for (int i=0; i< textFields.size(); i++){
      int selectedNumUnits = Integer.valueOf(tpanel.getInput(textFields.get(i)));
      if(selectedNumUnits > territory.getUnit(i).getNumUnits() ){
        throw new Exception("You assigned more units than you have");
      }
    }
    return true;
  }
  
  // 1
  public void promptLevelUnitsPanel() {
    this.frame.getContentPane().removeAll();
    String p = "Please select a level of units from (0~6)";
    ArrayList<String> fields = new ArrayList<String>();
  
    for (int i = 0; i <= 6; i++) {
      fields.add(String.valueOf(i));
  
    }
    // now make panels
    // add map panel
    mapPanel = new GameMapPanel(0, 0, "map", "",
                                this.gmap.getPlayerTerritoryStrings(this.player.getName()),
                                this,this.player);
    mapPanel.generatePanel();
    this.frame.add(mapPanel.getPanel());    
    
    // add selection panel
    tpanel = new TextFieldPanel(600, 0, "selected num units", p, fields, this);
    tpanel.generatePanel(100, 30);
    this.frame.add(tpanel.getPanel());
    this.frame.getContentPane().revalidate();
    this.frame.repaint();          
  
  }
  
  // 2
  private void selectedUnitsParser(String[] cmds) throws Exception{    
    System.out.println("======= current type "+this.type);    
    if (this.type == "Attack"){
      selectedUnitsAttackParser(cmds);
    }
    if(this.type == "Move"){
      selectedUnitsMoveParser(cmds);
    }    
    if(this.type=="Upgrade_Unit"){
      selectedUnitsUpgradeParser(cmds);
    }      
    ActionEvent ae = new ActionEvent(this,0,this.notifyStr);
    this.controlListener.actionPerformed(ae);      
    
  }
  
  /*
    For parsing unit upgrade. Precondition this.message has been initialized correctly
   */  
  private void selectedUnitsUpgradeParser(String [] cmds) throws Exception{
    this.validNumUnits(message.getDestTerritory());        
    ArrayList<Message> result = new ArrayList<Message>();
    Message m;
    for (String unitLvlStr : tpanel.getTextFields()) {
      m = new Message();
      m.setMessageType("Execute");
      m.setType("Upgrade_Unit");
      m.setPlayerName(this.name);
      
      String numUnits = tpanel.getInput(unitLvlStr);
      if(numUnits.equals(null) || numUnits.equals("")){
        numUnits = "0";
      }
      m.setLevel(Integer.valueOf(unitLvlStr));
      m.setDestTerritory(message.getDestTerritory());
      m.setUnitNum(Integer.valueOf(numUnits));
      
      int levelWanted = Integer.valueOf(unitLvlStr);
      //                                  lvlunit: aa num: Oz 1 0
      System.out.println(String.format("-- lvlunit: %s num: %s %s %s",
                                       name, message.getDestTerritory(), levelWanted,
                                       Integer.valueOf(numUnits)));
      if (Integer.valueOf(numUnits) > 0) {
        if (this.gmap.upgradeUnit(name, message.getDestTerritory(),
                                  levelWanted, Integer.valueOf(numUnits)) == false) {
          throw new Exception(
              "You do not have enough tech resource to upgrade unit or" +
              "your tech level is too low!");
        }
        result.add(m);
        this.frame.getContentPane().removeAll();        
      }
    }
    messages.addAll(result);
      
  }
  
  /*
    For parsing selected move units
   */  
  private void selectedUnitsMoveParser(String [] cmds) throws Exception{
    this.validNumUnits(message.getSrcTerritory());
    // iterate through all unit levels
    for (String unitLvlStr : tpanel.getTextFields()) {    
      Message m = new Message();
      String numUnitsStr = tpanel.getInput(unitLvlStr);
      int numUnits = Integer.valueOf(numUnitsStr);
      m.setMessageType("Execute");
      m.setPlayerName(this.name);
      m.setType("Move");
      m.setLevel(Integer.valueOf(unitLvlStr));    
      m.setSrcTerritory(message.getSrcTerritory());
      m.setDestTerritory(message.getDestTerritory());
      
      Territory srcTerr = this.gmap.getTerritory(this.message.getSrcTerritory());
      Territory destTerr = this.gmap.getTerritory(this.message.getDestTerritory());
        
      int cost = numUnits * this.gmap.getCostBetweenTerritories(srcTerr, destTerr);
      if(this.gmap.getPly_resource().consumeFood(this.name,cost) == false){
        // go back to main order panel        
        ActionEvent ae = new ActionEvent(this,0,this.notifyStr);
        this.controlListener.actionPerformed(ae);          
        throw new Exception("There is not enough food resource to for you to do the move!");
      }      
      m.setUnitNum(numUnits); // update message
      srcTerr.removeUnit(Integer.valueOf(unitLvlStr), numUnits);
      destTerr.addUnit(Integer.valueOf(unitLvlStr), numUnits);
      System.out.println(String.format("-- attack lvlunit: %s num: %s : %s %s %s",
                                       unitLvlStr, numUnits,
                                       message.getSrcTerritory(),m.getType(),m.getDestTerritory()));      
      // if move order, add each move to the messages
      messages.add(m);
    }
    
  }
  
  /*
    For parsing selected attack units
  */
  private void selectedUnitsAttackParser(String[] cmds) throws Exception{
    this.validNumUnits(message.getSrcTerritory());    
    // array list of ints for attack
    ArrayList<Integer> troops = new ArrayList<>();
    // iterate through all unit levels
    for (String unitLvlStr : tpanel.getTextFields()) {
      Message m = new Message();
      String numUnitsStr = tpanel.getInput(unitLvlStr);
      int numUnits = Integer.valueOf(numUnitsStr);
      m.setMessageType("Execute");
      m.setPlayerName(this.name);
      m.setType("Move");
      m.setLevel(Integer.valueOf(unitLvlStr));
      m.setSrcTerritory(message.getSrcTerritory());
      m.setDestTerritory(message.getDestTerritory());
      System.out.println(String.format("-- move lvlunit: %s num: %s", unitLvlStr, numUnits));
      troops.add(Integer.valueOf(numUnits));          
    }    

    Territory srcTerritory = this.gmap.findTerritory(message.getSrcTerritory());          
    Message m = new Message();
    m.setType("Attack");
    m.setMessageType("Execute");
    m.setPlayerName(this.name);
    m.setSrcTerritory(message.getSrcTerritory());
    m.setDestTerritory(message.getDestTerritory());
    m.setAttackLevels(troops); // update message
    atkTmpRemove(srcTerritory, troops);                    
    int totalNum = 0;
    for(Integer i: troops){
      totalNum += i;
    }
         
    if (this.gmap.getPly_resource().consumeFood(name, totalNum) == false) {
      throw new Exception("You do not have enough food resources to do this attack!");
    }          
    messages.add(m);
  }

  private void atkTmpRemove(Territory srcTerr, ArrayList<Integer> troops) {
    for (int i = 0; i < 7; i++) {
      srcTerr.removeUnit(i,troops.get(i));
    }
  }
  
}
