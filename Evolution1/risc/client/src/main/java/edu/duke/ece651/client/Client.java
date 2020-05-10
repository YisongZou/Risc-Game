package edu.duke.ece651.client;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;

public class Client {
  private Player player;
  private InputStream stream;
  private Scanner in;
  
  private Socket skt;  
  private int port;
  private String serverIP;
  
  public Client() {
    player = new Player("Green");
  }

  public Client(String playerName, InputStream stream) {
    player = new Player(playerName,stream);
    this.stream = stream;  
  }

  public Client(String playerName, InputStream stream, GameMap gmap) {
    GameMap gm = gmap;
    player = new Player("Green",gm,stream);
    this.stream = stream;
  }
  
  public Client(String playerName, int port, InputStream stream, String serverIP) {
    //    GameMap gm = new GameMap(2);   // ???????????????????????????????
    player = new Player(playerName, stream);
    this.port = port;    
    this.stream = stream;
    this.serverIP = serverIP;
  }
  
  public void play() throws IOException {
    connectToServer(this.serverIP);
    iniStage();
    actionStage();
  }

  public void iniSocket(String host, int port) throws IOException {
    skt = new Socket();
    skt.connect(new InetSocketAddress(host, port),10000000); // times out after 1000 ms
  }

  private Message recvMessage() throws IOException, ClassNotFoundException {
    ObjectInputStream objIn = new ObjectInputStream(skt.getInputStream());
    Message iniMsg = (Message) objIn.readObject();
    return iniMsg;
  }
  
  public void connectToServer(String serverIP) throws IOException {
    System.out.println("Trying to connect to server...");
    while (true) {
      try {
        this.iniSocket(serverIP, port);    // what will happen if server is not available?????
      }
      catch(IOException exp) {
        throw exp;
      }
      System.out.println("Connected to Server!");
      break;
    }
  }

  public void sendMessage(ArrayList<Message> msg) throws IOException {
    ObjectOutputStream os = new ObjectOutputStream(skt.getOutputStream());
    os.writeObject(msg);
  }

  public void closeSocket() throws IOException {
    skt.close();
  }  

  private void iniStage() {
    try {
      // player.chooseGroup(in);
      Message iniMsg = recvMessage();
      // select group id, and assign units
      ArrayList<Message> ms = initializationTurn(iniMsg);
      this.sendMessage(ms);
    }
    catch (Exception exp) {
      System.out.println("Exception inside iniStage");
      exp.printStackTrace();
    }
  }


  private void actionStage(){
    try {
      while(true){
        System.out.println("BEFORE RECVMSG IN ACTIONSTAGE");
        Message iniMsg = recvMessage();
        System.out.println("AFTER RECVMSG IN ACTIONSTAGE");
        if(iniMsg.getContent().equals("You lose!") ||
           iniMsg.getContent().equals("You win!")) {
          System.out.println(iniMsg.getContent());   // tell player the result
          break;
        }
        ArrayList<Message> ms = playTurn(iniMsg);
        sendMessage(ms);
      }  
    }
    catch(Exception e){
      System.out.println("Inside actionStage");
      System.out.println(e);
    }
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

  public void playTurn(){
    // get input from server
    // THIS IS STUB FOR TESTING
    this.playTurn(new Message());    
  }
  
  public ArrayList<Message> playTurn(Message serverMsg){
    // update player game map
    this.player.setGmap(serverMsg.getGmap());
    
    // get move or attack order from user
    ArrayList<Message> m = this.getPlayer().getOrder();
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
