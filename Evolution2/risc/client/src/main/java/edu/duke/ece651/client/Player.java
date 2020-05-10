package edu.duke.ece651.client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;

public class Player implements ActionListener{
  
  private String name;
  //  private Message msg;
  private GameMap gmap;
  private Socket skt;
  private BufferedReader inB;
  private Scanner scanner;
  private int groupID;
  private ActionListener listener;
  
  private JFrame frame;
  private OrderPanel orderPanel;
  private TextFieldPanel tpanel;
  private Message message;

  public ArrayList<Message> messages;

  private int upgradetech_flag;// = 1 if already upgrade tech once
  
  public Player(String name){
    
    this.name = name;
    gmap = new GameMap(1);    
  }

  public Player(String name,GameMap gmap){
    this(name);
    this.gmap = gmap;
    this.upgradetech_flag = 0;    
    //  msg = new Message("");   // initialize msg to be empty
    gmap = new GameMap(1);
    this.upgradetech_flag = 0;
  }

  public Player(String name,InputStream stream){
    messages = new ArrayList<Message>();
    this.name = name;
    if (stream != null) {
      this.scanner = new Scanner(stream);
    }
    // initialize JFrame
    this.frame = new JFrame("Game");
    this.frame.setSize(800, 800);    
    this.frame.setLayout(null);
    this.frame.setVisible(true);
    // initialize order controller
    this.upgradetech_flag = 0;
  }

  
  public Player(String name, InputStream stream, int debug) {
    this.name = name;
    gmap = new GameMap(debug);
    this.scanner = new Scanner(stream);
    this.upgradetech_flag = 0;
  }

  public Player(String name,GameMap gmap,InputStream stream){
    this(name, gmap);
    
    this.scanner = new Scanner(stream);    
    this.upgradetech_flag = 0;
  }

  public Player(String name,GameMap gmap,
                InputStream stream,
                ActionListener listener){
    this(name, gmap,stream);
    this.listener = listener;
  }
  
  
  @Override
  public void actionPerformed(ActionEvent event){
    String command = event.getActionCommand();
    System.out.println("-- Player ---- "+command);    
    String cmds[] = command.split(",");
    try{
      if(cmds[0].equals("AssignTerritoryGroup")){

        int groupId = Integer.valueOf(cmds[2]);
      
        message = new Message("AssignTerritoryGroup",this.name,
                              "Assign", groupId, this.gmap);
        promptAssignUnits(Integer.valueOf(groupId));
        messages.add(message);
      }
      if(cmds[0].equals("AssignedUnits")){
        ArrayList<String> textFields = tpanel.getTextFields();
        ArrayList<Message> tempMsgs = new ArrayList<>();
        ArrayList<Territory> terrGroup = this.gmap.getPlayerTerritories(this.name);
        int totalNumUnits = terrGroup.size()*5;
        int sum = 0;

        for (String s : textFields) {
          int numUnits = Integer.valueOf(tpanel.getInput(s));
          sum = sum+numUnits;
          if(sum > totalNumUnits){
            throw new Exception("You assigned more units than you have");
          }

          Message m = new Message("AssignUnits", this.name, "Assign", s, numUnits, null);
          tempMsgs.add(m);
        }

        if(sum != totalNumUnits){
          // player has less than # units
          throw new Exception(String.format("You can still assign %s  units",
                                            totalNumUnits - sum));
        }
        // add all messages to array list
        messages.addAll(tempMsgs);
        this.listener.actionPerformed(new ActionEvent(this,0,"finishedAssignUnits"));
        this.frame.getContentPane().removeAll();        
      }
    
      if (cmds[0].equals("MakeOrder")){
        // makeOrderListener for parsing (m)ove (a)ttack and (u)pgrades
        makeOrderListener(cmds);
      }
      
      if(command.equals("actionCommand")){
        selectTerritory("Please select source territory");
        message = new Message();      
      }
    }
    catch(Exception e){
      // make new window and print errror
      JDialog d = new JDialog(this.frame, "Error"); 
      // create a label 
      JLabel l = new JLabel(e.getMessage()); 
      d.add(l); 
      // setsize of dialog 
      d.setSize(300, 300); 
      // set visibility of dialog 
      d.setVisible(true);       
      e.printStackTrace();
    }
  }
  
  public void makeOrderListener(String[] cmds) throws Exception{
    // move order
    if(cmds[3].equals("Move") || cmds[3].equals("Attack") ){
      if(cmds[1].equals("selectSrc")){
        message = new Message();
        message.setPlayerName(this.name);
        message.setType(cmds[3]);          
        selectTerritory("Select source territory","MakeOrder,selectedSrc,0,"+cmds[3]);
      }        
      if(cmds[1].equals("selectedSrc")){
        // store source territory input
        String srcTerrStr = cmds[5];
        message.setSrcTerritory(srcTerrStr);

        ArrayList<String> ts  = this.gmap.getPlayerTerritoryStrings(name);
        if(cmds[3].equals("Attack")){
          List<Territory> terrs = this.gmap.getEnemyNeighbors(this.gmap.
                                                              getTerritory(srcTerrStr),
                                                              this.name);
          ts = new ArrayList<String>();            
          for (Territory t: terrs){
            ts.add(t.getTerritoryName());
          }
          message.setSrcTerritory(srcTerrStr);
          // display prompt for destination
          selectTerritory("Select destination territory", "MakeOrder,selectedDest,0,Attack", ts);
        }
        else {
          ts.remove(cmds[5]);
          message.setSrcTerritory(srcTerrStr);
          // display prompt for destination
          selectTerritory("Select destination territory", "MakeOrder,selectedDest,0,Move", ts);
        }
          
      }
      if(cmds[1].equals("selectedDest")){
        // store dest territory input
        message.setDestTerritory(cmds[5]);
        displayLevelUnitsPanel("MakeOrder,selectedNumUnits,0,"+cmds[3]);
      }
      if(cmds[1].equals("selectedNumUnits")){
        // array list of ints for attack
        ArrayList<Integer> troops = new ArrayList<>();
        // iterate through all unit levels
        for (String unitLvlStr : tpanel.getTextFields()) {
          Message m = new Message();
          String numUnitsStr = tpanel.getInput(unitLvlStr);
          int numUnits = Integer.valueOf(numUnitsStr);
          m.setMessageType("Execute");
          m.setPlayerName(this.name);
          m.setType(cmds[3]);
          m.setLevel(Integer.valueOf(unitLvlStr));
          m.setSrcTerritory(message.getSrcTerritory());
          m.setDestTerritory(message.getDestTerritory());
          System.out.println(String.format("-- lvlunit: %s num: %s", unitLvlStr, numUnits));
          
          // store num units for each unit level, for attack
          troops.add(Integer.valueOf(numUnits));
          if (cmds[3].equals("Move")) {
            Territory srcTerr = this.gmap.getTerritory(message.getSrcTerritory());
            Territory destTerr = this.gmap.getTerritory(message.getDestTerritory());
            
            int cost = numUnits * this.gmap.getCostBetweenTerritories(srcTerr, destTerr);
            if(this.gmap.getPly_resource().consumeFood(this.name,cost) == false){
              throw new Exception("There is not enough food resource to for you to do the move!");
            }
            System.out.println(message.getSrcTerritory());
            System.out.println(message.getDestTerritory());            
            m.setUnitNum(numUnits); // update message    x
            System.out.println(this.gmap.getTerritory(message.getDestTerritory()).getUnit(Integer.valueOf(unitLvlStr)).getNumUnits());
            srcTerr.removeUnit(Integer.valueOf(unitLvlStr), numUnits);
            destTerr.addUnit(Integer.valueOf(unitLvlStr), numUnits);
            System.out.println(this.gmap.getTerritory(message.getDestTerritory()).getUnit(Integer.valueOf(unitLvlStr)).getNumUnits());            
            // if move order, add each move to the messages
            messages.add(m);
          }
        }

        // if attack order, make a new message to store
        // attack commands
        if(cmds[3].equals("Attack")){
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
        displayOrderPanel(this);          
      }
    }
    // unit upgrade
    if(cmds[3].equals("Unit Upgrade")){
      selectTerritory("Select you want to upgrade units from"
                      ,"MakeOrder,selectedSrc,0,"+cmds[3]);        
      if(cmds[1].equals("selectedSrc")){
        // store source territory input
        String desTerrStr = cmds[5];
        message.setDestTerritory(desTerrStr);
        displayLevelUnitsPanel("MakeOrder,selectedNumUnits,0,"+cmds[3]);
      }
      if(cmds[1].equals("selectedNumUnits")){
        Message m = new Message();
        
        // Move units from one territory to another
        m.setMessageType("Execute");
        m.setType("Upgrade_Unit");
        m.setPlayerName(this.name);
        
        // get number of units they wanted upgraded
        ArrayList<Message> result =  getUnitLevelsListener(cmds);
        
        m.setMessageType("Execute");
        m.setType("Upgrade_Unit");
        m.setPlayerName(this.name);
      }
        
    }
    // Tech upgrade
    if(cmds[3].equals("Tech Upgrade")){

      if(upgradetech_flag == 1){
        throw new Exception("You cannot upgrade your tech twice in one turn");
      }
      Message m = new Message();
      m.setMessageType("Execute");
      m.setType("Upgrade_Tech");
      m.setPlayerName(this.name);
      System.out.print("( Selected (t)ech_Upgrade order)\n");
      int curr_tech = this.gmap.getPly_resource().getTechLevel(this.name);
      curr_tech += 1;
      if (curr_tech > 6) {
        throw new Exception("You already reach the highest level");
      }
      int tech_cost = 25 * (curr_tech - 1) * (curr_tech - 2) / 2 + 50;
      if (this.gmap.getPly_resource().getTechRsc(name) < tech_cost) {
        throw new Exception("There is not enough techology resource for use to upgrade technology");
      }
      upgradetech_flag = 1;      
      this.gmap.getPly_resource().consumeTech(name, tech_cost);
      messages.add(m);

      
    }
    // done(finish turn)
    if(cmds[3].equals("Done(Finish Turn)")){
      finishTurnListener();
    }

    if(cmds[3].equals("Leave Game")){
      // player leaving game
      upgradetech_flag = 0;      
      System.out.println("==== player - leavegame event");
      this.frame.getContentPane().removeAll();
      this.clearMessages();
      this.listener.actionPerformed(new ActionEvent(this,0,"playerLeftGame"));
      // this.listener.actionPerformed(new ActionEvent(this,0,"finishedTurn"));      
    }
    
  }

  public void finishTurnListener(){
      upgradetech_flag = 0;      
      System.out.println("==== client finished turn");
      this.frame.getContentPane().removeAll();       
      this.listener.actionPerformed(new ActionEvent(this,0,"finishedTurn"));
  }
  
  public void setFrameVisible(){
    this.frame.setVisible(true);
  }
  
  public void clearMessages(){
    this.messages = new ArrayList<>();
  }
  
  public GameMap getGmap() {
    return gmap;
  }

  public void setGmap(GameMap gmap) {
    this.gmap = gmap;
  }
  
  private ArrayList<Message> getUnitLevelsListener(String[] cmds) throws Exception{
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
      System.out.println(String.format("-- lvlunit: %s num: %s %s %s", name, message.getDestTerritory(), levelWanted,
          Integer.valueOf(numUnits)));
      if (Integer.valueOf(numUnits) > 0) {
        if (this.gmap.upgradeUnit(name, message.getDestTerritory(), levelWanted, Integer.valueOf(numUnits)) == false) {
          displayOrderPanel(this);
          throw new Exception(
              "You do not have enough tech resource to upgrade unit or" + "your tech level is too low!");
        }

        // valid order, add to messages
        messages.add(m);
        this.frame.getContentPane().removeAll();        
      }
      
      displayOrderPanel(this);
    }

    return result;
  }

  
  public void promptAssignUnits(int groupId){
    this.frame.getContentPane().removeAll();
      
    GameMap gmap = this.gmap;
    ArrayList<Territory> terrGroup = gmap.getTerritoryGroup(groupId);
    // for making text field panel
    ArrayList<String> tfStrings = new ArrayList<>();
        
    for (Territory terrTemp : terrGroup) {
      terrTemp.setPlayerName(this.name);
      tfStrings.add(terrTemp.getTerritoryName());
    }
    messages = new ArrayList<>();
    int totalNumUnits = terrGroup.size() * 5;

    String prompt = String.format("You have %s territories. You have %s "
                                  +" units you can assign.",terrGroup.size(),totalNumUnits);

    // now make panel
    tpanel = new TextFieldPanel(0,0, "AssignedUnits",
                                prompt,tfStrings,this);
    tpanel.generatePanel();
    this.frame.add(tpanel.getPanel());
    
  }


  /* CODE FOR ASSIGNING UNITS */
  public void displayAssignUnits(ActionListener listener){
    System.out.println("Player - displayAssignUnits");
    this.listener = listener;
    String promptString = "Please select one of the following territory groups:\n";
    ArrayList<Integer> unclaimedGroups = this.gmap.getUnclaimedGroupIds();
    System.out.println(unclaimedGroups);
    ArrayList<String> buttonStrings = new ArrayList<>();
    String prompt = "";
    for(Integer groupId : unclaimedGroups){
      prompt = "";
      ArrayList<Territory> ts = this.gmap.getTerritoryGroup(groupId);
      prompt = prompt + String.format("%s, ",groupId);
      int i = 0;
      for(Territory t: ts){
        if(i>0){
          prompt = prompt + ", ";
        }
        prompt = prompt+t.getTerritoryName();
        i++;
      }
      buttonStrings.add(prompt);
    }
    System.out.println(buttonStrings);
    ButtonsPanel buttonsPanel = new ButtonsPanel(0,0,"AssignTerritoryGroup",
                                                 promptString, buttonStrings,this);
    buttonsPanel.generatePanel();
    this.frame.add(buttonsPanel.getPanel());
  }  


  public void assignUnitsListener(){
    
  }
  public void displayOrderPanel() {
    this.frame.getContentPane().removeAll();
    System.out.println("player - display order panel");
    ArrayList<String> buttonStrings = new ArrayList<>();
    buttonStrings.add("Move");
    buttonStrings.add("Attack");
    buttonStrings.add("Unit Upgrade");
    buttonStrings.add("Tech Upgrade");
    buttonStrings.add("Done(Finish Turn)");
    buttonStrings.add("Leave Game");        
    
    String promptString = promptGenerator(); // promptGeneratorHelper("",this.name);
    ButtonsPanel buttonsPanel = new ButtonsPanel(0,0,
                                                 "MakeOrder,selectSrc",
                                                 promptString, buttonStrings,this);
    buttonsPanel.generatePanel();
    this.frame.add(buttonsPanel.getPanel());
  }
  /* CODE FOR MAKING ORDERS */
  public void displayOrderPanel(ActionListener listener) {
    displayOrderPanel();

  }


  public void displayLevelUnitsPanel(String eventStr){
    this.frame.getContentPane().removeAll();    
    String p = "Please select a level of units from (0~6) that you want to move";
    ArrayList<String> fields = new ArrayList<String>();

    for (int i = 0; i <=6; i++) {
      fields.add(String.valueOf(i));

    }  
    // now make panel
    tpanel = new TextFieldPanel(0,0, eventStr,
                                p,fields,this);
    tpanel.generatePanel(100,30);
    this.frame.add(tpanel.getPanel());
  }

  public Message displayLevelUnitsPanelListener(String cmds[]){    
    /*
    // update message contents
    m.setLevel(levelWanted);
    m.setSrcTerritory(srcTerr.getTerritoryName());
    m.setDestTerritory(destTerr.getTerritoryName());
    // precondition: num of units is valid
    int numUnits = promptGetNumUnits(srcTerr, levelWanted);
    m.setUnitNum(numUnits); // update message    
    srcTerr.removeUnit(levelWanted, numUnits);
    destTerr.addUnit(levelWanted, numUnits);    
    return m;
    */  

    return new Message();
  }
  
  
  public void displayNumUnitsPanel(String prompt,String eventStr,
                                   ArrayList<String> fields){
    this.frame.getContentPane().removeAll();        
    // now make panel
    tpanel = new TextFieldPanel(0,0, eventStr,prompt,fields,this);
    tpanel.generatePanel();
    this.frame.add(tpanel.getPanel());
  }
  
  public void selectTerritory(String promptString){
    selectTerritory(promptString, "actionCommmand");
  }
  
  public void selectTerritory(String promptString,String eventStr){
    ArrayList<String> ts  = this.gmap.getPlayerTerritoryStrings(name);
    selectTerritory(promptString, eventStr, ts);
  }
  public void selectTerritory(String promptString,String eventStr,
                              ArrayList<String> ts) {
    ButtonsPanel buttonsPanel = new ButtonsPanel(0,0,eventStr,
                                                 promptString, ts,this);
    buttonsPanel.generatePanel();
    this.frame.getContentPane().removeAll();    
    this.frame.add(buttonsPanel.getPanel());
    // Move units from one territory to another
    message.setMessageType("Execute");
    message.setType("Move");  
  }
  /*
    prompts user with List<Territory> ts options, returns int selection
    indexed to List
  */  
  public Integer promptSelectTerritory(List<Territory> ts,String prompt){
    HashMap<Integer, Territory> srcTerritories = new HashMap<>();    
    int i = 0;
    for (Territory t : ts){
      prompt += String.valueOf(i)+", " + t.getTerritoryName()+"\n";
      srcTerritories.put(i,t);
      i++;
    }
    System.out.print(prompt);    
    String input = this.scanner.nextLine();
    return Integer.valueOf(input);
  }

  /*
  public String promptGenerator(){
    String currPlayerName = this.name;
    String prompt = "";
    
    for (String playerName: this.gmap.getPlayerNames()){
    prompt = promptGeneratorHelper(prompt, playerName);
    }
  
    `    prompt = prompt + "\nYou are the " + currPlayerName +
    " player, what would you like to do?\n(M)ove\n(A)ttack\n(D)one\n";
    
    return prompt;
    }   */

  private String promptGeneratorHelper(String prompt, String name) {
    prompt = prompt + name + " player:\n-----------\n";
    List<Territory> territoryList = gmap.getPlayerTerritories(name);
    for (int i = 0; i < territoryList.size(); i++) {
      //get number of unit for each level
      Integer temp0 = territoryList.get(i).getUnit(0).getNumUnits();
      Integer temp1 = territoryList.get(i).getUnit(1).getNumUnits();
      Integer temp2 = territoryList.get(i).getUnit(2).getNumUnits();
      Integer temp3 = territoryList.get(i).getUnit(3).getNumUnits();
      Integer temp4 = territoryList.get(i).getUnit(4).getNumUnits();
      Integer temp5 = territoryList.get(i).getUnit(5).getNumUnits();
      Integer temp6 = territoryList.get(i).getUnit(6).getNumUnits();
      prompt = prompt +"( LV0:" +  temp0.toString() + " LV1:"+ temp1.toString()+ " LV2:" +  temp2.toString() + " LV3:" +  temp3.toString() + " LV4:" +  temp4.toString() + " LV5:" +  temp5.toString() +" LV6:" +  temp6.toString() +  " ) units in " + territoryList.get(i).getTerritoryName() + " (next to:";
      ArrayList<String> neighborArray = gmap.getNeighborStrings(territoryList.get(i));
      int j = 0;
      for (j = 0; j < neighborArray.size() - 1; j++) {
        prompt = prompt + " " + neighborArray.get(j) + ",";
      }
      prompt = prompt + " " + neighborArray.get(j) + ")\n";
    }
    return prompt;
  }

  
    //New verion for evolution2 used for generating the prompt before user selecting
  // an option from move attack and done and Upgrade_Tech and Upgrade_Unit
  public String promptGenerator(){
    String currPlayerName = this.name;
    String prompt = "";
    
    for (String playerName: this.gmap.getPlayerNames()){
      prompt = promptGeneratorHelper(prompt, playerName);
    }
    prompt = prompt + "\nYou have:\nFood Resource:" + this.gmap.getPly_resource().getFoodRsc(currPlayerName).toString()
        + "\nTech Resource:" + this.gmap.getPly_resource().getTechRsc(currPlayerName).toString()
        + "\nYour tech Level is:" + this.gmap.getPly_resource().getTechLevel(currPlayerName).toString();
    prompt = prompt + "\nYou are the " + currPlayerName +
      " player, what would you like to do?\n(M)ove\n(A)ttack\n(U)nit_Upgrade\n(T)ech_Upgrade\n(D)one\n";
    
    return prompt;
  }

  

  
  /*
    method for prompting user which territory group they would like to select
   */  
  public ArrayList<Message> promptInitializerAction(){
    ArrayList<Message> ms = new ArrayList<>();
    // now prompt user for input
    while (true) {    
      String prompt = "Please select one of the following territory groups:\n";
    
      ArrayList<Integer> unclaimedGroups = this.gmap.getUnclaimedGroupIds();

      for(Integer groupId : unclaimedGroups){
        ArrayList<Territory> ts = this.gmap.getTerritoryGroup(groupId);
        prompt = prompt + String.format("(%s) ",groupId);
        int i = 0;
        for(Territory t: ts){
          if(i>0){
            prompt = prompt + ", ";
          }
          prompt = prompt+t.getTerritoryName();
          i++;
        }
        prompt = prompt + "\n";
      }
      System.out.print(prompt);
    
      try {
        String input = this.scanner.nextLine();
        if (input.length() < 1){
          throw new Exception("Please enter a number");
        }
        int groupId = Integer.parseInt(input);

        if(unclaimedGroups.contains(groupId) == false){
          throw new Exception("Invalid group ID selected.");
        }
        
        Message message = new Message("AssignTerritoryGroup", this.name,
                                      "Assign", groupId, this.getGmap());
        
        System.out.print(String.format("( Player %s assigned territory group %s )",
                                       this.name, groupId));        
        ms.add(message);
        
        // now get unit assignments ,and add to messagees arraylist
        ArrayList<Territory> terrGroup = this.gmap.getTerritoryGroup(groupId);
        
        for (Territory terrTemp : terrGroup) {
          terrTemp.setPlayerName(this.name);
        }
        ms.addAll(promptAssignUnits(terrGroup));

        break;
        
      } catch (Exception e) {
        System.out.println("Exception occured: " + e.getMessage());
      }
    }
    // now return group id sassignments
    return ms;
  }

  
  public ArrayList<Message> promptAssignUnits(ArrayList<Territory> ts){
    ArrayList<Territory> tsTemp = ts;    
    ArrayList<Message> ms = new ArrayList<>();
    int totalNumUnits = ts.size()*5;

    String input;
    // keep iterating while there is a valid number of units    
    while (totalNumUnits > 0){
      // iterate through each territory
      for (Territory t : ts){
        String prompt = promptGeneratorHelper("", this.name);
    
        prompt = prompt + String.format("You have %s territories. You have %s units you can assign.\n",ts.size(),totalNumUnits);
        prompt = prompt + "-----------\n";
        System.out.print(prompt);
        
        boolean validInput = false;
        
        while (validInput == false) {
          // if the user does not have anymore units, break
          if (totalNumUnits < 1) {
            break;
          }
          
          // inform user which territory and num units left
          String s = String.format("How many units would you like to assign for Territory %s\n",
                                   t.getTerritoryName());
          // s = s + String.format("You have %s units you can assign:\n", totalNumUnits);
          System.out.print(s);
          
          // get input from user for num units
          input = this.scanner.nextLine();
          int numUnits = Integer.valueOf(input);
          // make sure valid input
          int temp = totalNumUnits - numUnits;
          if (numUnits >= 0 && numUnits <= totalNumUnits) {
            totalNumUnits = totalNumUnits - numUnits;
            Message m = new Message("AssignUnits", this.name,
                                    "Assign", t.getTerritoryName(), numUnits, null);
            m.setUnitNum(numUnits);
            ms.add(m);
            
            validInput = true;
          }
          else{
            System.out.print("Sorry, that is not a valid selection\n");
          }
        }
      }
    }
    return ms;
    
  }
  /*
  public String promptGeneratorHelper(String prompt, String name) {
    prompt = prompt + name + " player:\n-----------\n";
    List<Territory> territoryList = gmap.getPlayerTerritories(name);
    for (int i = 0; i < territoryList.size(); i++) {
      Integer temp = territoryList.get(i).getUnit().getNumUnits();
      prompt = prompt + temp.toString() + " units in " +
        territoryList.get(i).getTerritoryName() + " (next to:";
      ArrayList<String> neighborArray = gmap.getNeighborStrings(territoryList.get(i));
      int j = 0;
      for (j = 0; j < neighborArray.size() - 1; j++) {
        prompt = prompt + " " + neighborArray.get(j) + ",";
      }
      prompt = prompt + " " + neighborArray.get(j) + ")\n";
    }
    return prompt;
    }  */


  /*
    This method is for managing player action, whether they choose
    (m)ove or (a)ttack
    @returns a message of their action of choice
   */
  public ArrayList<Message> getOrder(){
      ArrayList<Message> ms = new ArrayList<>();
      
    /*
      ArrayList<Message> ms = new ArrayList<>();
      boolean validInput = false;
      String input;
      while (validInput == false) {
      // give player options      
      String prompt = promptGenerator();    
      System.out.print("\n\n\n"+prompt);
    
      input = this.scanner.nextLine().toLowerCase();
      try{
        if (input.equals("m")) {
          ms.add(this.promptMoveOrder());
        }
        else if (input.equals("a")) {
          ms.add( this.promptAttackOrder());
        }
        else if (input.equals("u")) {
          ms.add( this.promptUpgradeUnits());
        }
        else if(input.equals("t") && upgradetech_flag != 1) {
          ms.add(this.promptUpgade_Tech());
          upgradetech_flag = 1;
        }
        
        else if (input.equals("d")){
          upgradetech_flag = 0;
          System.out.print("Turn finished.\n");
          validInput = true;
        }
        
        else{
          throw new Exception("Invalid selection. Please choose 'm' or 'a' or 'u' or 't' for a order" +
                              " or 'd' when done inputting orders.\n");
        }
      }
      catch (Exception e){
        //e.printStackTrace();
        System.out.println("\n\n\n==== Error occured: ===== \n"+e.getMessage());
        System.out.println("==== Please try again. (Enter any key to continue) ==== ");
        this.scanner.nextLine();        // wait for player to confirm
      }
      }
      
    */
      return ms;      
  }
  
  /*
  Prompt user for source territory  
  */  
  public Territory promptGetTerritory(List<Territory> ts,String prompt){
    int selection = this.promptSelectTerritory(ts,prompt);
    Territory srcTerr = ts.get(selection);
    return srcTerr;
  }
  
  // prompt user to enter units to send with diff levels
  private ArrayList<Integer> promptGetAtkNumUnits(Territory srcT) {
    ArrayList<Integer> ret = new ArrayList<>();
    for (int i = 0; i <= 6; i++) {
      int tmplevel = promptGetNumUnits(srcT, i);
      ret.add(tmplevel);
    }
    return ret;
  }
  
  /*
    Prompt user to get number of units they want
  */  
  public int promptGetNumUnits(Territory t, int level) {
    // ask how many units
    int maxNum = t.getUnit(level).getNumUnits();
    System.out.print(String.format("How many units of level " + Integer.toString(level) + "? (You can use %s units)\n", maxNum));
  
    // get inpuct from user...
    int num = -1;
    while (true) {
      try{
        num = Integer.parseInt(this.scanner.nextLine());
        if(num < 0 || num > maxNum){
          throw new Exception();
        }
        break;
      }
      catch(Exception e){
        System.out.println("Wrong Input. Please try again");
      }
    }
    return num;
  }

  public Message promptUpgade_Tech() throws Exception{
    Message m = new Message();
    m.setMessageType("Execute");
    m.setType("Upgrade_Tech");
    m.setPlayerName(this.name);
    System.out.print("( Selected (t)ech_Upgrade order)\n");
    int curr_tech = this.gmap.getPly_resource().getTechLevel(this.name);
    curr_tech += 1;
    if (curr_tech > 6) {
      throw new Exception("You already reach the highest level");
    }
    int tech_cost = 25 * (curr_tech - 1) * (curr_tech - 2) / 2 + 50;
    if (this.gmap.getPly_resource().getTechRsc(name) < tech_cost) {
      throw new Exception("There is not enough techology resource for use to upgrade technology");
    }
    this.gmap.getPly_resource().consumeTech(name, tech_cost);
    return m;
  }
  
  public Message promptMoveOrder() throws Exception{
    // init msg to be returned
    Message m = new Message();
    
    // Move units from one territory to another
    m.setMessageType("Execute");
    m.setType("Move");
    m.setPlayerName(this.name);
  
    System.out.print("( Selected (m)ove order )\n");
    
    List<Territory> ts = this.gmap.getPlayerTerritories(this.name);    
    System.out.print(String.format("=== territory size in prompt move order %s\n",ts.size()));
    Territory srcTerr = promptGetTerritory(ts,"Please select source territory\n");
    System.out.println(String.format("( Player %s moving units from territory %s )\n",this.name,
                                     srcTerr.getTerritoryName()));
    
    ts.remove(srcTerr); // remove the selected territory from options
    Territory destTerr = promptGetTerritory(ts,"Please select destination territory\n");
    System.out.print(String.format("( Player %s moving units to territory %s )\n",this.name,
                                     destTerr.getTerritoryName()));
    
    if(this.gmap.isConnected(srcTerr, destTerr)==false){
      throw new Exception("There is no connection between these territories");
    }  

    int levelWanted = promptGetMoveLevel(srcTerr);
    
    // update message contents
    m.setLevel(levelWanted);
    m.setSrcTerritory(srcTerr.getTerritoryName());
    m.setDestTerritory(destTerr.getTerritoryName());
    // precondition: num of units is valid
    int numUnits = promptGetNumUnits(srcTerr, levelWanted);
    int cost = numUnits * this.gmap.getCostBetweenTerritories(srcTerr, destTerr);
    if(this.gmap.getPly_resource().consumeFood(this.name,cost) == false){
      throw new Exception("There is not enough food resource to for you to do the move!");
    }
    m.setUnitNum(numUnits); // update message    
    srcTerr.removeUnit(levelWanted, numUnits);
    destTerr.addUnit(levelWanted, numUnits);    
    return m;
  }

  
  public int promptGetUpgradeUnitLevel(Territory srcTerr) {
    System.out.println("Please select a level of units from (0~6) that you want to upgrade for one level");
    int level = -1;
    while (true) {
      try{
        level = Integer.parseInt(this.scanner.nextLine());
        if(level < 0 || level > 6){
          throw new Exception();
        }
        break;
      }
      catch(Exception e){
        System.out.println("Wrong Input. Please try again");
      }
    }
    return level;
  }
  
  public Message promptUpgradeUnits() throws Exception{
    // init msg to be returned
    Message m = new Message();
    
    // Move units from one territory to another
    m.setMessageType("Execute");
    m.setType("Upgrade_Unit");
    m.setPlayerName(this.name);
  
    System.out.print("( Selected (U)nit_Upgrade order )\n");
    
    List<Territory> ts = this.gmap.getPlayerTerritories(this.name);    
    //ts.remove(srcTerr); // remove the selected territory from options
    Territory destTerr = promptGetTerritory(ts,"Please select the territory that you want to upgrade the units inside\n");
    System.out.print(String.format("( Player %s selected territory %s )\n",this.name,
                                     destTerr.getTerritoryName()));
    
    int levelWanted = promptGetUpgradeUnitLevel(destTerr);

    // update message contents
    m.setLevel(levelWanted);
    m.setDestTerritory(destTerr.getTerritoryName());
    // precondition: num of units is valid
    int numUnits = promptGetNumUnits(destTerr, levelWanted);
    m.setUnitNum(numUnits); // update message


    // destTerr.upgradeUnit(levelWanted, numUnits);   
    if (this.gmap.upgradeUnit(name, destTerr.getTerritoryName(), levelWanted, numUnits) == false) {
      throw new Exception("You do not have enough tech resource to upgrade unit or your tech level is too low!");
    }
    return m;
  }

  public int promptGetMoveLevel(Territory srcTerr) {
    System.out.println("Please select a level of units from (0~6) that you want to move");
    int level = -1;
    while (true) {
      try{
        level = Integer.parseInt(this.scanner.nextLine());
        if(level < 0 || level > 6){
          throw new Exception();
        }
        break;
      }
      catch(Exception e){
        System.out.println("Wrong Input. Please try again");
      }
    }
    return level;
  }
  
  /*
    method for giving player choices for what action they want to take
   */
  public Message promptAttackOrder() throws Exception{
    // init msg to be returned
    Message m = new Message();
    
    // Move units from one territory to another
   
    
    List<Territory> ts = this.gmap.getPlayerTerritories(this.name);
    
    System.out.println("( Selected (a)ttack order )");

    Territory srcTerr = this.promptGetTerritory(ts, "Please select source territory to attack from\n");
    System.out.println(String.format("( Player %s attacking from territory %s )",this.name,
                                     srcTerr.getTerritoryName()));
    
    // get adjacent territories that do not belong to player
    List<Territory> neighbors = gmap.getEnemyNeighbors(srcTerr, this.name);
    // now ask user for destination territory
    Territory destTerr = this.promptGetTerritory(neighbors,
                                                 "Please select territory you want to attack \n");
    System.out.println(String.format("( Units from %s attacking %s )",
                                     srcTerr.getTerritoryName(),
                                     destTerr.getTerritoryName()));
    
    // update message contents
    m.setType("Attack");
    m.setMessageType("Execute");
    m.setPlayerName(this.name);
    m.setSrcTerritory(srcTerr.getTerritoryName());
    m.setDestTerritory(destTerr.getTerritoryName());

    // precondition: this numUnits is valid
    ArrayList<Integer> troops = promptGetAtkNumUnits(srcTerr);

    int totalNum = 0;
    for(Integer i: troops){
      totalNum += i;
    }
    if (this.gmap.getPly_resource().consumeFood(name, totalNum) == false) {
      throw new Exception("You do not have enough food resources to do this attack!");
    }
    
    m.setAttackLevels(troops); // update message    
    atkTmpRemove(srcTerr, troops);
    return m;
  }

  // this function will temporarily remove the troop sent out, helping show the prompt of gmap
  private void atkTmpRemove(Territory srcTerr, ArrayList<Integer> troops) {
    for (int i = 0; i < 7; i++) {
      srcTerr.removeUnit(i,troops.get(i));
    }
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public void setFrame(JFrame frame){
    this.frame = frame;
  }


  public JFrame getFrame(){
    return this.frame;
  }
  

  public ArrayList<Message> getMessages(){
    return messages;
  }
}
