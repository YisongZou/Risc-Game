package edu.duke.ece651.client;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import java.awt.event.*;
import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;

// class for controlling view to get player outputs
public class ActionController implements ActionListener {
  private Message gameInfo;
  private ArrayList<Message> messages;
  private JFrame frame;

  private TextFieldPanel fieldPanel = null;
  private ButtonsPanel buttonsPanel = null;
  private Player player;
  private GameMap gmap;
  private ErrorView errorView;

  private String state;

  
  public ActionController(Player p,GameMap gmap){
    this.messages = new ArrayList<>();
    this.player = p;
    this.gmap = gmap;
    
    this.frame = new JFrame();    
    this.frame.setSize(400, 800);    
    this.frame.setLayout(null);
    this.frame.setVisible(true);
    this.errorView = new ErrorView();
    state = "";
  }

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
  }

  
  public String promptGenerator(){
    String currPlayerName = this.player.getName();
    String prompt = "";
    
    for (String playerName: this.gmap.getPlayerNames()){
      prompt = promptGeneratorHelper(prompt, playerName);
    }
    
    prompt = prompt + "\nYou are the " + currPlayerName +
      " player, what would you like to do?";
    
    return prompt;
  }  
  

  public void getOrder(){
    ArrayList<String> buttonStrings = new ArrayList<>();
    buttonStrings.add("Move");
    buttonStrings.add("Attack");
    buttonStrings.add("Unit Upgrade");
    buttonStrings.add("Attack Upgrade");
    buttonStrings.add("Done(Finish Turn)");    
    
    String promptString= "You are the " + this.player.getName() +
      " player, what would you like to do?";
    this.buttonsPanel = new ButtonsPanel(0, 0, "actionCommand",
                                         promptString, buttonStrings, this);
    buttonsPanel.generatePanel();
    
    this.frame.add(buttonsPanel.getPanel());
  }

  public void selectTerritory(String promptString){
    String name = this.player.getName();
    ArrayList<String> ts  = this.player.getGmap().getPlayerTerritoryStrings(name);
    
    ButtonsPanel buttonsPanel = new ButtonsPanel(400,0,"actionCommand",
                                                 promptString, ts,this);
    buttonsPanel.generatePanel();
    this.frame.add(buttonsPanel.getPanel());
  }
  

  @Override
  public void actionPerformed(ActionEvent e){
    String command = e.getActionCommand();
    String cmds[] = command.split(",");

    // for debugging
    System.out.println(String.format("==== %s", command));    
    try{
      if(command.equals("actionCommand")){
      }

    }
    catch (Exception exception){
      exception.printStackTrace();
    }
    
  }
}
