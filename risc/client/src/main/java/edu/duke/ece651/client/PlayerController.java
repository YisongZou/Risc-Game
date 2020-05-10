package edu.duke.ece651.client;


import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

/*
  Class responsible for managing player game views (make order, leave game)
 */
public class PlayerController implements ActionListener {

  private Client client;
  private Player player;
  private JFrame frame;
  private boolean isDebug;
  private ActionListener mainListener;

  private MakeOrderPrompt makeOrderPrompt;

  public PlayerController(JFrame frame, Client client){
    this.frame = frame;
    this.client = client;
    this.player = client.getPlayer();
    this.isDebug = false;
  }  
  
  public PlayerController(JFrame frame, Client client,ActionListener mainListener,
                          boolean isDebug){
    this(frame, client);
    this.mainListener = mainListener;
    this.isDebug = isDebug;
  }
  
  @Override
  public void actionPerformed(ActionEvent event){
    System.out.println("PlayerController - " + event.getActionCommand());
    String cmds[] = this.parseEvent(event);
    // player leave game
    if(cmds[0].equals("playerLeftGame")){    
      client.setIsInRoom(false);
      ActionEvent ae = new ActionEvent(this,0,"PlayerController: player left game");
      this.mainListener.actionPerformed(ae);
    }
  }
   

  
  private String[] parseEvent(ActionEvent e){
    return e.getActionCommand().split(",");  
  }
  
  
  /*
    Display order panel and start making orders
   */
  public void start(){
    System.out.println("PlayerController - start()");
    // this.player.displayOrderPanel(this);
    makeOrderPrompt = new MakeOrderPrompt(this.frame,this,
                                          "finishedMakingOrder",this.client);
    makeOrderPrompt.displayOrderPanel();
  }
  
  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }
  
}
