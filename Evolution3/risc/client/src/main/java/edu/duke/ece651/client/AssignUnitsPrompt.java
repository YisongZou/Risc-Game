package edu.duke.ece651.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;

public class AssignUnitsPrompt extends Prompt{

  private String name;
  private GameMap gmap;    

  public AssignUnitsPrompt(JFrame frame,Player player,
                           ActionListener controlListener,String notifyStr){
    super(frame, controlListener, notifyStr, player);
    this.name = player.getName();
    this.gmap = player.getGmap();
  }


    
  private void refreshPanel(){
    this.frame.getContentPane().removeAll();
    this.frame.getContentPane().repaint();
    this.frame.getContentPane().revalidate();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // 1. assign territory group
    String cmds[] = e.getActionCommand().split(",");    
    System.out.println("AssignUnitsFlow: " + e.getActionCommand());
    if (cmds[0].equals("AssignTerritoryGroup")){
      int groupId = Integer.valueOf(cmds[2]);
      message = new Message("AssignTerritoryGroup",this.name,
                            "Assign", groupId, this.gmap);
      promptAssignUnits(Integer.valueOf(groupId));
      messages.add(message);
    }
    // 2. assign units in territory group    
    if(cmds[0].equals("AssignedUnits")){
      try{
        ArrayList<String> textFields = tpanel.getTextFields();
        ArrayList<Message> tempMsgs = new ArrayList<>();
        ArrayList<Territory> terrGroup = this.gmap.getPlayerTerritories(this.name);
        int totalNumUnits = terrGroup.size()*5;
        int sum = 0;
      
        for (String s : textFields) {
          int numUnits = Integer.valueOf(tpanel.getInput(s));
          System.out.println(this.name +" "+ s + " "+numUnits); 
          sum = sum+numUnits;
          if(sum > totalNumUnits){
            throw new Exception("You assigned more units than you have");
          }
          Message m = new Message("AssignUnits", this.name, "Assign", s, numUnits, null);
          tempMsgs.add(m);
        }
        if(sum != totalNumUnits){
          // throw new Exception("You can still assign " + (totalNumUnits - sum) + " units");
        }
        messages.addAll(tempMsgs);
        this.controlListener.actionPerformed(new ActionEvent(this, 0, this.notifyStr));
      } catch (Exception except) {
        throwException(except);
        // add all messages to array list
      }

    }
  }
  

  public void start(){
    promptGroup();
  }

  
  public void promptGroup(){
    refreshPanel();
    String promptString = "Please select one of the following territory groups:\n";
    ArrayList<Integer> unclaimedGroups = this.gmap.getUnclaimedGroupIds();
    System.out.println(unclaimedGroups);
    ArrayList<String> buttonStrings = new ArrayList<>();
    String prompt = "";
    for (Integer groupId : unclaimedGroups) {
      prompt = "";
      ArrayList<Territory> ts = this.gmap.getTerritoryGroup(groupId);
      prompt = prompt + String.format("%s, ", groupId);
      int i = 0;
      for (Territory t : ts) {
        if (i > 0) {
          prompt = prompt + ", ";
        }
        prompt = prompt + t.getTerritoryName();
        i++;
      }
      buttonStrings.add(prompt);
    }
    System.out.println(buttonStrings);
    ButtonsPanel buttonsPanel = new ButtonsPanel(0, 0,
                                                 "AssignTerritoryGroup",
                                                 promptString, buttonStrings, this);
    buttonsPanel.generatePanel();
    this.frame.add(buttonsPanel.getPanel());
    this.frame.getContentPane().revalidate();
    this.frame.getContentPane().repaint();        
  }

  public void promptAssignUnits(int groupId){
    refreshPanel();
      
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
    this.frame.getContentPane().revalidate();
    this.frame.repaint();          
    
  }

  
  
}
