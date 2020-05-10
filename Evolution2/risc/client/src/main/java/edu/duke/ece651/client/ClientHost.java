package edu.duke.ece651.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.duke.ece651.shared.Message;

public class ClientHost implements ActionListener {
  private Message gameInfo;
  private ArrayList<Client> games; // stores all games this player is in or previous in
  private Client curGame;
  private Client gameChoosed;
  private String hostMasterIP;
  private int hostMasterPort;
  private Socket skt; // socket to Hostmaster
  private ArrayList<Message> availableGames;    // available games that is nt old games
  private Scanner in;
  private JFrame frame;
  private TextFieldPanel fieldPanel = null;
  private ButtonsPanel buttonsPanel = null;
  private ActionListener clientControllerListener;

  public ClientHost(String hostMasterIP, int hostMasterPort) {
    this.games = new ArrayList<>();
    this.hostMasterIP = hostMasterIP;
    this.hostMasterPort = hostMasterPort;
    this.skt = null;
    this.availableGames = new ArrayList<>();    
  }

  public ClientHost(String hostMasterIP, int hostMasterPort, JFrame frame) {
    this(hostMasterIP, hostMasterPort);
    this.frame = frame;
  }

  public ClientHost(String hostMasterIP, int hostMasterPort,
                    JFrame frame, ActionListener listener){
    this(hostMasterIP, hostMasterPort,frame);
    this.clientControllerListener = listener;
  }

  public ClientHost(String hostMasterIP, int hostMasterPort, Scanner in) {
    this(hostMasterIP, hostMasterPort);
    this.in = in;
    this.availableGames = new ArrayList<>();
  }

  public void setGames(ArrayList<Client> games) {
    this.games = games;
  }

  public ArrayList<Client> getGames() {
    return this.games;
  }

  private void connectToHM() throws IOException {
    System.out.println("Trying to connect to HostMaster...");
    this.skt = new Socket();
    skt.connect(new InetSocketAddress(this.hostMasterIP, this.hostMasterPort), 10000);
    System.out.println("Connected to HostMaster!");
  }

  private void sendMessage(Message msg) throws IOException {
    System.out.println(String.format(" -- clientHost sendMessage content %s",
                                     msg.getContent()));
    ObjectOutputStream os = new ObjectOutputStream(this.skt.getOutputStream());
    os.writeObject(msg);
  }

  private ArrayList<Message> getMessageArr() throws IOException, ClassNotFoundException {
    ObjectInputStream objInS = new ObjectInputStream(this.skt.getInputStream());
    ArrayList<Message> msgRecved = (ArrayList<Message>) objInS.readObject();
    return msgRecved;
  }

  // tell hostmaster I'm a client
  private void notifyHM() throws IOException {
    Message msg = new Message();
    msg.setContent("Client");
    sendMessage(msg);
  }

  // this function judge if the game in msg is old game
  private boolean isOldGame(Message msg) {
    // for test
    System.out.println("msg gameserver ip: " + msg.getGameServerIP());
    System.out.println("msg gameserver port: " + msg.getGameServerPort());

    
    for (int i = 0; i < this.games.size(); i++) {
      Client clt = games.get(i);
      // for test
      System.out.println("clt" + Integer.toString(i) + " gameserver ip: " + clt.getServerIP());
      System.out.println("clt" + Integer.toString(i) + " gameserver port: " + clt.getPort());
      
      if (clt.getServerIP().equals(msg.getGameServerIP()) && clt.getPort() == msg.getGameServerPort()) {
        return true;
      }
    }
    return false;
  }
  
  private void getAvailableGames() throws IOException, ClassNotFoundException {
    ArrayList<Message> availGames = getMessageArr();
    this.availableGames.clear();
    for (int i = 0; i < availGames.size(); i++) {
      if (!isOldGame(availGames.get(i))) {
        this.availableGames.add(availGames.get(i));
      }
    }
  }

  private String availableGamesStr() {

    String ret = "";
    ret += "Available Games:\n";
    for (int i = 0; i < availableGames.size(); i++) {
      ret += "(" + Integer.toString(i) + ")\n";
      ret += "****************\n";
      ret += "Game " + Integer.toString(i) + "\n";
      ret += "Capacity: " + availableGames.get(i).getGameCapacity().toString() + "\n";
      ret += "Current Player Number: " + availableGames.get(i).getCurPlyNum().toString() + "\n";
      ret += "port: " + availableGames.get(i).getGameServerPort().toString() + "\n";
      ret += "****************\n";
    }
    return ret;
  }
  
  
  public void promptChoose(String prompt) {
    this.frame.getContentPane().removeAll();    
    ArrayList<String> opts = new ArrayList<>();
    for (int i = 0; i < availableGames.size(); i++) {
      opts.add("Game " + Integer.toString(i));
    }
    System.out.println(prompt + "\nPlease choose one game to join:");
    this.buttonsPanel = new ButtonsPanel(0, 0, "promptChoose", prompt, opts, this);
    buttonsPanel.generatePanel();
    this.frame.add(buttonsPanel.getPanel());
  }

  public Message getGameChosed(String plyChoosenStr) throws Exception {
    int plyChoosen = Integer.parseInt(plyChoosenStr);
    if ((plyChoosen >= 0 && plyChoosen < availableGames.size()) == false) {
      throw new Exception();
    }
    return this.availableGames.get(plyChoosen);
  }

  private void askForAvailableGames() throws IOException {
    Message msg = new Message();
    msg.setContent("Ask for available games");
    sendMessage(msg);
  }

  private void addToGames(Message gameInfo) {
    int idx = this.games.size();

    ArrayList<String> s = new ArrayList<>();
    s.add("Please type in player name for ROOM " + Integer.toString(idx) + ":");
    s.add("Please type in password for ROOM " + Integer.toString(idx) + ":");
    this.fieldPanel = new TextFieldPanel(0, 0, "addToGames", "", s, this);
    this.fieldPanel.generatePanel(100, 30);
    this.gameInfo = gameInfo;
    this.frame.getContentPane().removeAll();
    this.frame.add(this.fieldPanel.getPanel());

  }

  private void addToGames(String[] cmds, Message gameInfo) {
    System.out.println("ClientHost.addToGames");
    String playerName = this.fieldPanel.getInput(0);
    String password = this.fieldPanel.getInput(1);
    Client client = new Client(playerName, gameInfo.getGameServerPort(),
                               gameInfo.getGameServerIP(), this.frame);
    System.out.println("clientHost addToGames name ---- "+playerName);  
    System.out.println("clientHost addToGames pass ---- "+password);    
    client.clientHostListener = this;
    client.setPassword(password);
    games.add(client);
    client.setIsInRoom(true);
    this.curGame = client;
  }
  
  // check if there is need to ask for new games
  public boolean notInRoom() {
    for (Client clt : this.games) {
      if (clt.getIsInRoom()) {
        return false;
      }
    }
    return true;
  }
  
  private void displayWantToComebackPanel()  {
    this.frame.getContentPane().removeAll();        
    this.frame.setVisible(true);
    System.out.println("Want to come back panel displayed:");
    ArrayList<String> options = new ArrayList<>();
    options.add("Yes");
    options.add("No");
    buttonsPanel = new ButtonsPanel(0, 0, "wantToComeBack listener",
                                    "Do you want to go back to a previous game?",
                                    options, this);
    buttonsPanel.generatePanel();
    this.frame.add(buttonsPanel.getPanel());
  }
  
  // ask if player want to come back to one of previous game
  private boolean wantToComeback() throws InterruptedException {
    String answer;
    while (true) {
      Thread.sleep(2000);
      try {
        System.out.println("Do you want to go back to previous game? (\"Yes\" or \"No\")");
        answer = this.in.nextLine();
        if (!answer.equals("Yes") && !answer.equals("No")) {
          throw new Exception("Wrong Input. Please try again");
        }
        break;
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
    return answer.equals("Yes");
  }

  private String oldGamesStr() {
    String show = "";
    show += "Previous Games: \n";
    for (int i = 0; i < this.games.size(); i++) {
      show += "****************\n";
      show += "Game " + Integer.toString(i) + "\n";
      show += "****************\n";
    }
    return show;
  }

  private void authenticateListener(Client clt, ArrayList<String> tfields) throws Exception {
    String playerName = fieldPanel.getInput(0);
    String password = fieldPanel.getInput(1);
    System.out.println("authenticateListener - playerName: " + playerName + ": " +clt.getPlayer().getName());
    System.out.println("authenticateListener - password: " + password + ": " +clt.getPassword());     
    if (!clt.getPlayer().getName().equals(playerName) || !clt.getPassword().equals(password)) {
      throw new Exception("Wrong name or password. Please try again");
    }
  }

  private void authenticate(Client clt) {
    this.frame.getContentPane().removeAll();    
    ArrayList<String> tfStrings = new ArrayList<>();
    tfStrings.add("Please type in PlayerName:");
    tfStrings.add("Please type in Password:");
    fieldPanel = new TextFieldPanel(0,0, "authenticateListener",
                                "Please authenticate",tfStrings,this);
    fieldPanel.generatePanel();
    this.frame.add(fieldPanel.getPanel());
  }
  
  private Client gameToBackListener(String[] cmds){
    int oldgameIdx = Integer.valueOf(cmds[1]);
    return this.games.get(oldgameIdx);
  }
  
  // ask which old game player want to come back
  private void  gameToBack() {
    System.out.println("client host gameToBack");
    this.frame.getContentPane().removeAll();    
    System.out.println("Choosing game:");
    ArrayList<String> gameOptions = new ArrayList<>();
    for (int i = 0; i < this.games.size(); i++) {
      gameOptions.add("Game " + Integer.toString(i) + "\n");
    }
    buttonsPanel = new ButtonsPanel(0, 0, "gameToBackListener", oldGamesStr(), gameOptions, this);
    buttonsPanel.generatePanel();
    this.frame.getContentPane().removeAll();    
    this.frame.add(buttonsPanel.getPanel());
  }
  
  public void promptChooseListener(String[] cmds) throws Exception {
    this.frame.getContentPane().removeAll();
    Message gameChosen = getGameChosed(cmds[1]); 
    sendMessage(gameChosen);  
    addToGames(gameChosen);
    this.gameInfo = gameChosen;
  }

  public void addToGamesListener(String[] cmds) {
    System.out.println("clienthost.addToGameslistener");
    this.frame.getContentPane().removeAll();
    addToGames(cmds, this.gameInfo);
    Thread th = new Thread(this.curGame, "game_" + Integer.toString(this.games.indexOf(this.curGame)));
    th.start();

  }

  // remove the finished game from arraylist
  private void clearFinishedRoom() throws IOException {
    if (curGame != null && curGame.getIsGameEnd()) {
      this.games.remove(this.curGame);
      Message msg = new Message();
      msg.setContent("this game ends");
      msg.setGameServerIP(this.curGame.getServerIP());
      msg.setGameServerPort(this.curGame.getPort());
      sendMessage(msg);
      this.curGame = null;
    }
  }

  public void displayJoinGamePanel() throws IOException, ClassNotFoundException,
                                            InterruptedException{
    this.frame.getContentPane().removeAll();
    int enterloopCnt = 0;
    while (true) {
      Thread.sleep(1000);      
      System.out.println(String.format("--- clienthost process enterLoopCnt: %s",
                                       enterloopCnt));
      enterloopCnt++;
      askForAvailableGames();
      getAvailableGames();
      if (this.availableGames.size() == 0) {
        if (enterloopCnt == 1) {
          System.out.println("No new game available, keep waiting please");
        }
        Message msg = new Message();
        msg.setContent("I will keep ask");
        sendMessage(msg);
        System.out.println("sent message asking");
        continue;      // if no new game available, keep asking hostmaster
      }
      else {
        // get available games and ask player to choose
        String availGameStr = availableGamesStr();
        promptChoose(availGameStr);
        break;
      }
    }
  }

  
  
  // main job of ClientHost
  public void proccess() throws IOException,
                                ClassNotFoundException, InterruptedException {
    connectToHM();
    notifyHM();
    //while (true) {
    if (notInRoom()) {
      clearFinishedRoom();
      // ask if player want to join old one
      displayWantToComebackPanel();
            if (this.games.size() > 0 && wantToComeback()) {
        System.out.println(oldGamesStr());
        promptChoose(hostMasterIP);
        System.out.println("Wait for next round!");        

      } else {
        displayJoinGamePanel();
      }
    }
  }

  public void displayJoinGameOption(){
    this.frame.getContentPane().removeAll();            
    ArrayList<String> gameOptions = new ArrayList<>();
    gameOptions.add("Yes");
    gameOptions.add("No");    
    buttonsPanel = new ButtonsPanel(0, 0, "playerLeftGameListener",
                                    "Do you want to join a previous game?",
                                    gameOptions, this);
    buttonsPanel.generatePanel();
    this.frame.add(buttonsPanel.getPanel());
    
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    System.out.println(String.format("== client host  == %s", command));        
    String cmds[] = command.split(",");
    // for debugging
    try {
      if (cmds[0].equals("promptChoose")) {
        promptChooseListener(cmds);
      }
      if (cmds[0].equals("addToGames")) {
        addToGamesListener(cmds);
      }
      if (cmds[0].equals("playerLeftGame")) {
        System.out.println("displayJoinGameOption()");
        displayJoinGameOption();
      }

      if(cmds[0].equals("playerLeftGameListener")){
        System.out.println("playerLeftGameListener");
        if (cmds[2].equals("Yes")){
          System.out.println("here");
          gameToBack();
        }

        if (cmds[2].equals("No")){
          displayJoinGamePanel();
        }

      }
      
      if(cmds[0].equals("gameToBackListener")){
        // select game player wants to return to
        gameChoosed = gameToBackListener(cmds);
        // authenticate the game, and call authenticateListener
        System.out.println("--  clientHost name "+gameChoosed.getPlayer().getName());
        System.out.println("--  clientHost password "+gameChoosed.getPassword());
        authenticate(gameChoosed);
      }      
      if(cmds[0].equals("authenticateListener")){
        authenticateListener(gameChoosed,fieldPanel.getTextFields());
        // this.frame.setVisible(false);
        gameChoosed.setFrameVisible();
        gameChoosed.setIsInRoom(true);
        gameChoosed.waitThenAction();
      }
      if (cmds[0].equals("wantToComeBack listener")){
        if (this.games.size() > 0 && cmds[1] == "No"){
          System.out.println(oldGamesStr());
          promptChoose(hostMasterIP);
          System.out.println("Wait for next round!");        
        }
        else{
          // displayJoinGamePanel();
        }
      }

    } catch (Exception exception) {
      exception.printStackTrace();
      // make new window and print errror
      JDialog d = new JDialog(this.frame, "Error"); 
      // create a label 
      JLabel l = new JLabel(exception.getMessage()); 
      d.add(l); 
  
      // setsize of dialog 
      d.setSize(300, 300); 
  
      // set visibility of dialog 
      d.setVisible(true);       

    }
  }
}

