package edu.duke.ece651.client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;



public class BackToGamePrompt extends Prompt {
  private Client client;
  private Client gameChosen;  
  private ClientHost clientHost;

  public BackToGamePrompt(JFrame frame, ActionListener controlListener,
                          String notifyStr, Client client,ClientHost clientHost){
    super(frame,  controlListener,  notifyStr,client.getPlayer());
    this.client = client;
    this.clientHost = clientHost;
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    try{
      String command = e.getActionCommand();
      System.out.println("=== BackToGamePrompt listener : " + command);
      String cmds[] = command.split(",");

      if(cmds[0].equals("wantToComeBack listener")){
        wantToComebackListener(cmds);
      }


      if(cmds[0].equals("gameToBackListener")){
        gameChosen = gameToBackListener(cmds);
        // authenticate the game, and call authenticateListener
        System.out.println("--  clientHost name "+gameChosen.getPlayer().getName());
        System.out.println("--  clientHost password "+gameChosen.getPassword());
        authenticate(gameChosen);
      }

      if(cmds[0].equals("authenticateListener")){
        authenticateListener(gameChosen,tpanel.getTextFields());
        // this.frame.setVisible(false);
      }

      /* Methods for listening to player return to previous game */
      if(cmds[0].equals("gameToBackListener")){
        gameChosen = gameToBackListener(cmds);
        // authenticate the game, and call authenticateListener
        System.out.println("--  client Host name "+gameChosen.getPlayer().getName());
        System.out.println("--  clientHost password "+gameChosen.getPassword());
        authenticate(gameChosen);        
      }

      if(cmds[0].equals("authenticateListener")){
        authenticateListener(gameChosen,tpanel.getTextFields());
        // this.frame.setVisible(false);
        this.client = gameChosen;
        ActionEvent ae = new ActionEvent(this, 0, "backToGamePrompt Finished");
        this.controlListener.actionPerformed(ae);
        
      }
      
    } catch(Exception except){
      except.printStackTrace();
    }
      
    
  }
  
  /*
    Ask player if they want to join a previous game
   */
  public void displayWantToComebackPanel()  {
    this.frame.getContentPane().removeAll();        
    this.frame.setVisible(true);
    System.out.println("Want to come back panel displayed:");
    ArrayList<String> options = new ArrayList<>();
    options.add("Yes");
    options.add("No");
    bpanel = new ButtonsPanel(0, 0, "wantToComeBack listener",
                                    "Do you want to go back to a previous game?",
                                    options, this);
    bpanel.generatePanel();
    this.frame.add(bpanel.getPanel());
    this.frame.getContentPane().revalidate();
    this.frame.getContentPane().repaint();        
  }
  
  private void wantToComebackListener(String[] cmds) throws Exception{
    System.out.println("playerLeftGameListener");
    if (cmds[2].equals("Yes")){
      promptChoosePreviousGame();
    }

    if (cmds[2].equals("No")){
      // join a brand new game
      // prompt client host to refresh and wait for available games
      this.clientHost.refreshAndWaitAvailableGames();
    }    
  }

  private Client gameToBackListener(String[] cmds){
    int oldgameIdx = Integer.valueOf(cmds[1]);
    return this.clientHost.getGames().get(oldgameIdx);
  }
  

  
  private void  promptChoosePreviousGame() {
    System.out.println("backtogameprompt - prompt choose previous game");
    this.frame.getContentPane().removeAll();    
    System.out.println("Choosing game:");
    ArrayList<String> gameOptions = new ArrayList<>();
    for (int i = 0; i < this.clientHost.getGames().size(); i++) {
      gameOptions.add("Game " + Integer.toString(i) + "\n");
    }
    bpanel = new ButtonsPanel(0, 0, "gameToBackListener",
                                    this.clientHost.getOldGamesStr(), gameOptions, this);
    bpanel.generatePanel();
    this.frame.getContentPane().removeAll();
    // add new panel
    this.frame.add(bpanel.getPanel());
    this.frame.getContentPane().revalidate();
    this.frame.getContentPane().repaint();        

  }

  private void authenticate(Client clt) {
    this.frame.getContentPane().removeAll();    
    ArrayList<String> tfStrings = new ArrayList<>();
    tfStrings.add("Please type in PlayerName:");
    tfStrings.add("Please type in Password:");
    tpanel = new TextFieldPanel(0,0, "authenticateListener",
                                "Please authenticate",tfStrings,this);
    tpanel.generatePanel();
    this.frame.add(tpanel.getPanel());
    this.frame.getContentPane().revalidate();
    this.frame.getContentPane().repaint();        
    
  }

  private void authenticateListener(Client clt, ArrayList<String> tfields) throws Exception {
    String playerName = tpanel.getInput(0);
    String password = tpanel.getInput(1);
    System.out.println("authenticateListener - playerName: " + playerName + ": " +clt.getPlayer().getName());
    System.out.println("authenticateListener - password: " + password + ": " +clt.getPassword());     
    if (!clt.getPlayer().getName().equals(playerName) || !clt.getPassword().equals(password)) {
      throw new Exception("Wrong name or password. Please try again");
    }
  }

  public Client getClient(){
    return this.client;
  }

}
