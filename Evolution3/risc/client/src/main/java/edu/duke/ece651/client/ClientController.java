package edu.duke.ece651.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.duke.ece651.shared.Message;


/*
  Class responsible for managing client
 */
public class ClientController implements ActionListener {
  private ButtonsPanel buttonsPanel = null;
  private TextFieldPanel fieldPanel = null;

  private BackToGamePrompt backToGamePrompt;
  
  private ActionListener mainControllerListener;
  private ClientHost clientHost;
  private Client client;

  private boolean isDebug;

  private Thread th;

  private AssignUnitsPrompt auPrompt;  
  Client gameChoosed;
  // private GameMapView gmView;
  JFrame frame;
  int i;

  public ClientController(JFrame frame, ActionListener mainControllerListener) {
    i = 0;
    this.isDebug = false;
    this.frame = frame;
    this.mainControllerListener = mainControllerListener;
  }

  public ClientController(JFrame frame, ActionListener mainControllerListener, boolean isDebug) {
    this(frame, mainControllerListener);
    this.isDebug = isDebug;
  }
  
  private void refreshPanel(){
    this.frame.getContentPane().removeAll();
    this.frame.getContentPane().repaint();
    this.frame.getContentPane().revalidate();
  }

  private void waitLabelPanel(){
    JLabel label = new JLabel("Please wait...");
    label.setBounds(10, 10, 100, 100);
    this.frame.add(label);
  }
  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    System.out.println("ClientController listener : " + command);
    String cmds[] = command.split(",");

    try {
      // 1. client host listeenr, client connected to HM
      if (cmds[0].equals("clientConnectHostMaster")) {
        refreshPanel();
        waitLabelPanel();        
        parseHostMaster(cmds);
      }
      
      // 2. client host listener, check if client wants to connect to a old game

      /*
      if(cmds[0].equals("wantToComeBack listener")){
        wantToComebackListener(cmds);
      }
      */
      /* Methods for letting player choose from new available games */
      // 3. Games are now available, display prompt to let player choose
      if (cmds[0].equals("clienthost refreshAvailableGames games are available")) {
        String prompt = this.clientHost.getAvailableGamesStr();
        ArrayList<Message> availableGames = this.clientHost.getAvailableGames();
        refreshPanel();
        waitLabelPanel();                
        displayChooseGamePanel(prompt, availableGames);
      }

      // 4. Player chooses available game
      if (cmds[0].equals("chooseGamePanel game chosen")) {
        refreshPanel();
        waitLabelPanel();                
        int plyChoosen = Integer.parseInt(cmds[1]);
        if ((plyChoosen >= 0 && plyChoosen < this.clientHost.getAvailableGames().size()) == false) {
          throw new Exception();
        }
        Message gameChosen = clientHost.getAvailableGames().get(plyChoosen);
        if (this.isDebug == false) { // only send message if not debugging
          // notify server which game client is joining
          this.clientHost.sendMessage(gameChosen);
        }
        // update fields in client host
        this.clientHost.setGameInfo(gameChosen);
        // let player input user and pass
        this.displayLoginPanel(gameChosen, "loginPanel player registered");
      }
      
      // 5. Let player connect to game and register
      if (cmds[0].equals("loginPanel player registered")) {
        refreshPanel();
        waitLabelPanel();                
        // update client host with joined game
        String playerName = this.fieldPanel.getInput(0);
        String password = this.fieldPanel.getInput(1);
        this.client = new Client(playerName, clientHost.getGameInfo().getGameServerPort(),
                                 clientHost.getGameInfo().getGameServerIP(), this.frame,
                                 this,this.isDebug);
        this.client.setPassword(password);
        // add client to games joined
        this.clientHost.addToGames(this.client);
        System.out.println("PlayerController: startClient()");
        int idxString = clientHost.getGames().indexOf(clientHost.getCurGame());
        Thread th = new Thread(this.client,
                               "game_" + Integer.toString(idxString));
        th.start();
      }

      // 6. Assign Units and territories
      if(cmds[0].equals("client: inform clientController assign units")){
        refreshPanel();
        waitLabelPanel();                
        auPrompt = new AssignUnitsPrompt(this.frame, this.client.getPlayer(),this,
                                                     "finishedAssignUnits");
        auPrompt.start();
      }
      
      // 7. Player finished assigning units, now handover to player controller
      if(cmds[0].equals("finishedAssignUnits")){
        refreshPanel();
        waitLabelPanel();                
        auPrompt.getMessages();
        System.out.println("--- client controller - sending messages:");
        for(Message m: auPrompt.getMessages()){
          System.out.println(m.getPlayerName()+" "+m.getType()+" "+m.getGroupId()+" "+m.getUnitNum());
        }
        if (this.isDebug == false) {
          this.client.sendMessage(auPrompt.getMessages());
        }
        // send ini stage messages, let
        System.out.println("Client controller: sent ini messages, "+
                           "now waiting for wait for action to display prompt");
      }
      if(cmds[0].equals("client: inform ClientController displayOrder")){
        this.frame.getContentPane().removeAll();        
        ActionEvent ae = new ActionEvent(this, 0, "ClientController: inform mainController start game");
        this.mainControllerListener.actionPerformed(ae);
      }
      // when player selects 'leave game'
      if(cmds[0].equals("display want to come back")){
        refreshPanel();
        waitLabelPanel();                
        backToGamePrompt = new BackToGamePrompt(this.frame,this,"backToGame game chosen",
                                                this.client,this.clientHost);
        backToGamePrompt.displayWantToComebackPanel();
      }
      if(cmds[0].equals("backToGamePrompt Finished")){
        refreshPanel();
        waitLabelPanel();
        
        Client selectedClient = backToGamePrompt.getClient();
        this.client = selectedClient;          
        clientHost.setCurGame(this.client);
        this.client.setIsInRoom(true);
        // this line will inform player controller to run
        this.client.waitThenAction();
      }

      if(cmds[0].equals("displayConnectToHostMaster")){
        refreshPanel();
        waitLabelPanel();
        
        this.displayConnectToHostMaster();
      }
      
    }
    
    catch (Exception exception) {
      exception.printStackTrace();
      // make new window and print errror
      JDialog d = new JDialog(this.frame, "Error");
      // create a label 
      JLabel l = new JLabel(exception.getMessage());
      d.add(l);
      // setsize of dialog 
      d.setSize(500, 300);
      // set visibility of dialog 
      d.setVisible(true);
      displayConnectToHostMaster();
    }
    
  }
  ClientHost ch;
  public void start()throws Exception{
    // get hostmaster IP
    String hostMasterIP = this.getFieldPanel().
      getInput("Please type in HostMaster IP:");
    // hostmaster port num is 8888
    ch = new ClientHost(hostMasterIP, 8888,this,this.isDebug);
    ch.process();
  }


  /*
    Function responsible for displaying prompt to connect to hostmaster
  */
  public void displayConnectToHostMaster() {
    refreshPanel();
    ArrayList<String> s = new ArrayList<>();
    s.add("Please type in HostMaster IP:");
    this.fieldPanel = new TextFieldPanel(0, 0, "clientConnectHostMaster", "", s, this);
    this.fieldPanel.generatePanel();
    this.frame.add(fieldPanel.getPanel());
    this.frame.getContentPane().revalidate();
    this.frame.getContentPane().repaint();    
  }

  /*
    Once connected to host master
   */
  public void displayChooseGamePanel(String prompt, ArrayList<Message> availableGames) {
    refreshPanel();
    ArrayList<String> opts = new ArrayList<>();
    for (int i = 0; i < availableGames.size(); i++) {
      opts.add("Game " + Integer.toString(i));
    }
    System.out.println(prompt + "\nPlease choose one game to join:");
    this.buttonsPanel = new ButtonsPanel(0, 0, "chooseGamePanel game chosen", prompt, opts, this);
    buttonsPanel.generatePanel();
    this.frame.add(buttonsPanel.getPanel());
    this.frame.getContentPane().revalidate();
    this.frame.getContentPane().repaint();        
  }
  
  /*
    Connect to host master
   */
  public void parseHostMaster(String[] cmds) throws Exception {
    refreshPanel();
    String hostMasterIP = this.fieldPanel.getInput("Please type in HostMaster IP:");
    if (hostMasterIP.length() < 1) {
      throw new Exception("HostMaster IP cannot be empty. Please try again");
    }
    clientHost = new ClientHost(hostMasterIP, 8888, this, this.isDebug);
    clientHost.process();
  }
  
  /*
    Prompt player to login to game
  */
  public void displayLoginPanel(Message gameInfo, String prompt) {
    refreshPanel();    
    int idx = this.clientHost.getGames().size();
    ArrayList<String> s = new ArrayList<>();
    s.add("Please type in player name for ROOM " + Integer.toString(idx) + ":");
    s.add("Please type in password for ROOM " + Integer.toString(idx) + ":");

    this.fieldPanel = new TextFieldPanel(0, 0, prompt, "", s, this);
    this.fieldPanel.generatePanel(100, 30);
    this.frame.getContentPane().removeAll();
    this.frame.add(this.fieldPanel.getPanel());
    this.frame.getContentPane().revalidate();
    this.frame.getContentPane().repaint();    
  }

  
  
  
  public TextFieldPanel getFieldPanel() {
    return fieldPanel;
  }

  public void setFieldPanel(TextFieldPanel fieldPanel) {
    this.fieldPanel = fieldPanel;
  }

  public ClientHost getClientHost() {
    return clientHost;
  }


  public void setClientHost(ClientHost clientHost) {
    this.clientHost = clientHost;
  }

  public Client getClient(){
    return this.client;
  }

  public void setClient(Client client){
    this.client = client;
  }
  
}
  
