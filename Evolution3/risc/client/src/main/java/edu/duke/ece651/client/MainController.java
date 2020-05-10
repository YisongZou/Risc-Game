package edu.duke.ece651.client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;



import edu.duke.ece651.shared.Message;

public class MainController implements ActionListener{
  boolean isDebug;
  
  JFrame frame;

  ClientController clientController;
  PlayerController playerController;
  
  int i;
  
  public MainController(boolean isDebug){
    // change this boolean to enable debug mode //
    this.isDebug = isDebug;
    i = 0;
    this.frame = new JFrame("Game");
    // initialize frame
    this.frame.setSize(1200, 800);
    this.frame.setLayout(null);
    this.frame.setVisible(true);

    // initialize controllers
    clientController = new ClientController(this.frame,this,
                                            this.isDebug);
    // playerController = new PlayerController(this.frame, this.isDebug);
    playerController = null; // this to be initialized after connecting
  }

  public MainController(){
    this(false);
  }
  
  public void start(){
    // start with connect stage
    connectHostMaster();
  }
  
  public void connectHostMaster(){
    // display user to connect to hostmaster
    this.clientController.displayConnectToHostMaster();
  }

  
  @Override
  public void actionPerformed(ActionEvent event){
    String command = event.getActionCommand();
    System.out.println("MainController - " + command);    
    String cmds[] = command.split(",");
    try{
      if(cmds[0].equals("Hostmaster selected")){
        clientController.start();
      }
      if(cmds[0].equals("ClientController: inform mainController start game")){
        playerController = new PlayerController(this.frame, clientController.getClient(),
                                                this,this.isDebug);
        playerController.start();
      }
      if(cmds[0].equals("PlayerController: player left game")){
        this.frame.getContentPane().removeAll();
        // inform client controller to display want to come back panel
        ActionEvent ae = new ActionEvent(this, 0, "display want to come back");
        this.clientController.actionPerformed(ae);
        // clientController.displayWantToComebackPanel();
      }
      
    }
    catch(Exception except){
      except.printStackTrace();
    }
  }
  
}
