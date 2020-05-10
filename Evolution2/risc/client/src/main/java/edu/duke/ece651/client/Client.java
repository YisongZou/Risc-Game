package edu.duke.ece651.client;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;

import java.awt.event.*;
import java.awt.*;

public class Client extends Thread implements ActionListener {
  public ActionListener clientHostListener;
  private Player player;
  private InputStream stream;
  private Scanner in;

  private Socket skt;
  private int port;
  private String serverIP; // IP OF GAME SERVER
  private boolean isInRoom;
  private String password;
  private Object locker; // use to lock operations that read/write isInRoom
  
  private Message iniMsg;

  JFrame frame;

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    System.out.println(String.format("== Client == %s", command));
    String cmds[] = command.split(",");
    
    try {
      if (cmds[0].equals("AssignUnits")) {
        iniStage(this.player.getOrder());
      }
      
      if(cmds[0].equals("finishedAssignUnits")){
        sendMessage(this.player.getMessages());
        this.player.clearMessages();                
        // playTurn(new Message());
      }
      
      if(cmds[0].equals("finishedTurn")){
        sendMessage(this.player.getMessages());
        // clear messages for next turn;
        this.player.clearMessages();
        this.waitThenAction();
      }
      
      if(cmds[0].equals("playerLeftGame")){
        // send all messages in current game and clear messages
        sendMessage(this.player.getMessages());
        // clear messages for next turn;
        this.player.clearMessages();                
        setIsInRoom(false);        
        this.clientHostListener.actionPerformed(new ActionEvent(this,0,"playerLeftGame"));
        System.out.println(String.format("set is in room %s",this.isInRoom));
        /*
        while (isInRoom == false){
          System.out.println("Player not in room, sending empty actions");
          sendMessage(new ArrayList<Message>());
          recvMessage();          
          Thread.sleep(2000);
        }
        */
      }
      
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private boolean isGameEnd;
  
  public Client() {
    player = new Player("Green");
  }

  public Client(String playerName, InputStream stream) {
    player = new Player(playerName, stream);
    this.stream = stream;
  }

  public Client(String playerName, InputStream stream, GameMap gmap) {
    GameMap gm = gmap;
    player = new Player("Green", gm, stream);
    this.stream = stream;
  }

  public Client(String playerName, int port, InputStream stream, String serverIP) {
    player = new Player(playerName, stream);
    this.port = port;
    this.stream = stream;
    this.serverIP = serverIP;
    if (this.stream != null) {
      this.in = new Scanner(this.stream);
    }
    this.isInRoom = true;
    this.locker = new Object();
    this.isGameEnd = false;
  }

  
  public String getServerIP() {
    return this.serverIP;
  }

  public int getPort() {
    return this.port;
  }

  public Client(String playerName, int port, String serverIP, JFrame frame) {
    this(playerName, port, null, serverIP);
    this.frame = frame;
  }

  public void setPassword(String psd) {
    this.password = psd;
  }

  public String getPassword() {
    return this.password;
  }

  public void setIsInRoom(boolean inRoom) {
    synchronized (locker) {
      this.isInRoom = inRoom;
    }
  }

  public boolean getIsInRoom() {
    boolean res = true;
    synchronized (locker) {
      res = this.isInRoom;
    }
    return res;
  }

  public void play() throws IOException, Exception {
    connectToServer(this.serverIP);
    iniStage();
    actionStage();
  }


  public void setFrameVisible(){
    this.frame.setVisible(true);
    this.player.setFrameVisible();
  }

  public void actionStageListener(){
    System.out.println("client - actionStageListener"); 
    try {
      actionStage();
    } catch (Exception exp) {
      exp.printStackTrace();
    }
  }

  public void waitThenAction() throws IOException,ClassNotFoundException {
    System.out.println(" waiting for new message from server");
    Message iniMsg = recvMessage();
    System.out.println(" received messages from server");        
    actionStage(iniMsg);
  }
  public void run() {    
    try {      
      connectToServer(this.serverIP);
      iniStage();
      waitThenAction();
    } catch (Exception exp) {
      exp.printStackTrace();
    }
  }

  
  public void iniSocket(String host, int port) throws IOException {
    skt = new Socket();
    skt.connect(new InetSocketAddress(host, port), 10000000); // times out after 1000 ms
  }

  private Message recvMessage() throws IOException, ClassNotFoundException {
    ObjectInputStream objIn = new ObjectInputStream(skt.getInputStream());
    Message iniMsg = (Message) objIn.readObject();
    return iniMsg;
  }

  public void connectToServer(String serverIP) throws IOException,
                                                      InterruptedException {
    System.out.println("Trying to connect to server...");
    while (true) {
      Thread.sleep(1000);      
      try {
        this.iniSocket(serverIP, port); // what will happen if server is not available?????
      } catch (IOException exp) {
        throw exp;
      }
      System.out.println("Connected to Server!");
      break;
    }
  }

  public void sendMessage(ArrayList<Message> msg) throws IOException {
    // print messages list for debugging
    for (Message m : this.player.getMessages()){
      String s = String.format("%s %s %s",m.getType(),
                               m.getPlayerName(),
                               m.getSrcTerritory());
      System.out.println(m.getType());
    }
    if (skt != null) {
      ObjectOutputStream os = new ObjectOutputStream(skt.getOutputStream());
      os.writeObject(msg);
    }
  }

  public void closeSocket() throws IOException {
    skt.close();
  }

  /* Thread entry point */
  private void iniStage() {
    try {
      // unomment to get server functionality
      Message iniMsg = recvMessage();
      //Message iniMsg = new Message();

      // initialize gamemap for testing
      // GameMap gmap = new GameMap(2);
      // gmap.setTerritoryGroupPlayer(0, "Bob");
      // iniMsg.setGmap(gmap);
      ///// end of debugging
      
      this.player.setGmap(iniMsg.getGmap());
      this.frame.getContentPane().removeAll();      
      System.out.println("client - iniStage");
      System.out.println(this.player.getGmap().getPlayerNames());
      this.player.displayAssignUnits(this);
      
      // generate panels to assign units
      // select group , and assign units
      // ArrayList<Message> ms = initializationTurn(iniMsg);
      // this.sendMessage(ms);
    } catch (Exception exp) {
      System.out.println("Exception inside iniStage");
      exp.printStackTrace();
    }
  }
  
  private void iniStage(ArrayList<Message> ms) throws IOException {
    this.sendMessage(ms);
  }

  private boolean askForLeave() {
    String answer = "";
    /*
      while (true) {
      try {
      System.out.println("Do you want to leave this room? (\"Yes\" or \"No\")");
      answer = this.in.nextLine();
      if (!answer.equals("Yes") && !answer.equals("No")) {
      throw new Exception("Wrong Input. Please try again");
      }
      break;
      } catch (Exception e) {
      System.out.println(e.getMessage());
      }
      }
    */
    return answer.equals("Yes");
  }
  public boolean getIsGameEnd() {
    return this.isGameEnd;
  }

  
  public void actionStage(Message iniMsg)  throws IOException{
    System.out.println("client - actionStage(iniMsg)");    
    if (iniMsg.getContent().equals("You lose!") ||
        iniMsg.getContent().equals("You win!")) {
      System.out.println(iniMsg.getContent()); // tell player the result
      this.isGameEnd = true;
      // this.player.getFrame().setVisible(false);
      this.frame.setVisible(true);      
    }
    // ask player if he want to leave room, if he is in
    if (getIsInRoom()) {
      boolean wantToLeave = askForLeave();
      if (wantToLeave) { // if playr wants to leave, he is not in room
        setIsInRoom(false);
      }
    }
    
    if(getIsInRoom()) {
      System.out.println("client - actionStage , in room, now playTurn");
      playTurn(iniMsg);
    }
    else if(getIsInRoom() == false){
      sendMessage(new ArrayList<>());
    }
    
  }

  public void actionStage() throws Exception {
    /*
    System.out.println("BEFORE RECVMSG IN ACTIONSTAGE");
    Message iniMsg = recvMessage();
    System.out.println("AFTER RECVMSG IN ACTIONSTAGE");
    if (iniMsg.getContent().equals("You lose!") || iniMsg.getContent().equals("You win!")) {
      System.out.println(iniMsg.getContent()); // tell player the result
    }
    // ask player if he want to leave room, if he is in

    if (getIsInRoom()) {
      boolean wantToLeave = askForLeave();
      if (wantToLeave) { // if playr wants to leave, he is not in room
        setIsInRoom(false);
      }
      if (getIsInRoom()) {
        sendMessage(new ArrayList<>());
      }
    }
    */
    playTurn(new Message());
  }
  

  public void OLDactionStage() throws Exception {

    System.out.println("BEFORE RECVMSG IN ACTIONSTAGE");
    Message iniMsg = recvMessage();
    System.out.println("AFTER RECVMSG IN ACTIONSTAGE");
    if (iniMsg.getContent().equals("You lose!") || iniMsg.getContent().equals("You win!")) {
      System.out.println(iniMsg.getContent()); // tell player the result
      this.isGameEnd = true;
      this.player.getFrame().setVisible(false);
      this.frame.setVisible(true);
    }
    // ask player if he want to leave room, if he is in

    if (getIsInRoom()) {
      boolean wantToLeave = askForLeave();
      if (wantToLeave) { // if playr wants to leave, he is not in room
        setIsInRoom(false);
      }
      if (getIsInRoom()) {
        sendMessage(new ArrayList<>());
      }
    }

    playTurn(iniMsg);
  }

  public void actionStageListener(ArrayList<Message> ms) throws Exception{
    // ms = playTurn(iniMsg);
    // ms = new ArrayList<>();
    sendMessage(ms);
  }

public void actionStageOld(){
/*
  public boolean getIsGameEnd() {
    return this.isGameEnd;
  }

  private void actionStage(){
    try {
      while(true){
        if(getIsInRoom()) {
          System.out.println("BEFORE RECVMSG IN ACTIONSTAGE");
        }
        Message iniMsg = recvMessage();
        if (getIsInRoom()) {
          System.out.println("AFTER RECVMSG IN ACTIONSTAGE");
        }
        if(iniMsg.getContent().equals("You lose!") ||
            iniMsg.getContent().equals("You win!")) {
          System.out.println(iniMsg.getContent()); // tell player the result
          this.isGameEnd = true;
          setIsInRoom(false);
          break;
        }

        // ask player if he want to leave room, if he is in
        if (getIsInRoom()) {
          boolean wantToLeave = askForLeave();
          if (wantToLeave) {    // if playr wants to leave, he is not in room
            setIsInRoom(false);
          }
        }

        // if player not in room, send an empty arraylist of messages
        ArrayList<Message> ms;
        if (getIsInRoom()) {
          ms = playTurn(iniMsg);
        }
        else {
          ms = new ArrayList<>();
        }
        
        sendMessage(ms);
      }  
    }
    catch(Exception e){
      System.out.println("Inside actionStage");
      System.out.println(e);
    }
    */
  }

  
  public ArrayList<Message> initializationTurn(){
    ArrayList<Message> ms = this.initializationTurn(new Message());
    return ms;
  }
  
  public ArrayList<Message> initializationTurn(Message serverMsg){
    this.player.setGmap(serverMsg.getGmap());
    // prompt user for selecting which territory they want to claim
    ArrayList<Message> m = this.player.promptInitializerAction();
    //    String prompt = this.player.promptGeneratorHelper("", this.player.getName());
    return m;
  }

  public void playTurn() throws Exception{
    // get input from server
    // THIS IS STUB FOR TESTING
    this.playTurn(new Message());    
  }


  public void playTurn(Message serverMsg) {
    // update player game map
    this.player.setGmap(serverMsg.getGmap());
    
    // make panel here
    this.player.displayOrderPanel(this);
  }

  
  public ArrayList<Message> playTurnOld(Message serverMsg){
    // update player game map
    this.player.setGmap(serverMsg.getGmap());
    
    // get move or attack order from user
    ArrayList< Message> m = this.getPlayer().getOrder();
    // over socket, send server arraylist of messages
    
    return m;
  }
  
  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

}
