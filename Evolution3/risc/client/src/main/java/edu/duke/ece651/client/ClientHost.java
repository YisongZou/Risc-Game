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

public class ClientHost {
  private Message gameInfo;
  private ArrayList<Client> games; // stores all games this player is in or previous in
  private Client curGame;
  private Client gameChoosed;
  private String hostMasterIP;
  private int hostMasterPort;
  private Socket skt; // socket to Hostmaster
  private ArrayList<Message> availableGames; // available games that is nt old games
  private Scanner in;

  private TextFieldPanel fieldPanel = null;
  private ButtonsPanel buttonsPanel = null;
  private ActionListener clientControllerListener;

  private boolean isDebug;

  public ClientHost(String hostMasterIP, int hostMasterPort) {
    this.games = new ArrayList<>();
    this.hostMasterIP = hostMasterIP;
    this.hostMasterPort = hostMasterPort;
    this.skt = null;
    this.availableGames = new ArrayList<>();
  }

  public ClientHost(String hostMasterIP, int hostMasterPort, ActionListener listener) {
    this(hostMasterIP, hostMasterPort);
    this.clientControllerListener = listener;
    isDebug = false;
  }

  public ClientHost(String hostMasterIP, int hostMasterPort, ActionListener clientListener, Boolean isDebug) {
    this(hostMasterIP, hostMasterPort, clientListener);
    availableGames = new ArrayList<>();
    this.isDebug = isDebug;
  }

  // main job of ClientHost
  public void proccess() throws IOException, ClassNotFoundException, InterruptedException {
    connectToHM();
    notifyHM();
    //while (true) {
    if (notInRoom()) {
      clearFinishedRoom();
      // ask if player want to join old one
      /*
      displayWantToComebackPanel();
      if (this.games.size() > 0 && wantToComeback()) {
        System.out.println(oldGamesStr());
        promptChoose(hostMasterIP);
        System.out.println("Wait for next round!");

      } else {
        displayJoinGamePanel();
      }
      */
      refreshAndWaitAvailableGames();
    }
  }

  /*
    Method responsible for connecting to server
   */
  public void process() throws IOException, ClassNotFoundException, InterruptedException {
    System.out.println("Client host - process");
    System.out.println(this.isDebug);
    if (this.isDebug == false) {
      connectToHM();
      notifyHM();
      if (notInRoom()) {
        clearFinishedRoom();
        // ask if player want to join old one
        refreshAndWaitAvailableGames();
        ActionEvent ae = new ActionEvent(this, 0,
                                         "clienthost refreshAvailableGames games are available");
        clientControllerListener.actionPerformed(ae);
      }
    }
    else if (this.isDebug == true) {
      System.out.println("debug");
      Message msg = new Message();
      msg.setGameCapacity(2);
      msg.setCurPlyNum(0);
      msg.setContent("Server");
      msg.setGameServerPort(1234);
      this.availableGames.add(msg);
      String eventStr = "clienthost refreshAvailableGames games are available";
      ActionEvent ae = new ActionEvent(this, 0, eventStr);
      this.clientControllerListener.actionPerformed(ae);
    }
  }

  private void connectToHM() throws IOException {
    System.out.println("Trying to connect to HostMaster...");
    this.skt = new Socket();
    skt.connect(new InetSocketAddress(this.hostMasterIP, this.hostMasterPort), 10000);
    System.out.println("Connected to HostMaster!");
  }

  private void notifyHM() throws IOException {
    Message msg = new Message();
    msg.setContent("Client");
    sendMessage(msg);
  }

  public boolean notInRoom() {
    for (Client clt : this.games) {
      if (clt.getIsInRoom()) {
        return false;
      }
    }
    return true;
  }

  // remove the finished game from arraylist
  private void clearFinishedRoom() throws IOException {
    /*
      if (curGame != null && curGame.getIsGameEnd()) {
      this.games.remove(this.curGame);
      Message msg = new Message();
      msg.setContent("this game ends");
      msg.setGameServerIP(this.curGame.getServerIP());
      msg.setGameServerPort(this.curGame.getPort());
      sendMessage(msg);
      this.curGame = null;
      }
    */
  }

  private ArrayList<Message> getMessageArr() throws IOException, ClassNotFoundException {
    ObjectInputStream objInS = new ObjectInputStream(this.skt.getInputStream());
    ArrayList<Message> msgRecved = (ArrayList<Message>) objInS.readObject();
    return msgRecved;
  }

  
  public void recieveAvailableGames() throws IOException, ClassNotFoundException {
    if (!this.isDebug) {
      ArrayList<Message> availGames = getMessageArr();
      this.availableGames.clear();
      for (int i = 0; i < availGames.size(); i++) {
        if (!isOldGame(availGames.get(i))) {
          this.availableGames.add(availGames.get(i));
        }
      }
    }
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
  
  
  public void refreshAndWaitAvailableGames()
    throws IOException, ClassNotFoundException,
                                                    InterruptedException{
    // this.frame.getContentPane().removeAll();
    int enterloopCnt = 0;
    while (true) {

      System.out.println(String.format("--- clienthost process enterLoopCnt: %s",
                                       enterloopCnt));
      enterloopCnt++;
      askForAvailableGames();
      recieveAvailableGames();
      if (this.availableGames.size() == 0) {
        if (enterloopCnt == 1) {
          System.out.println("No new game available, keep waiting please");
        }
        Message msg = new Message();
        msg.setContent("I will keep ask");
        sendMessage(msg);
        System.out.println("sent message asking");
        Thread.sleep(1000);              
        continue;      // if no new game available, keep asking hostmaster
      }
      else {
        // promptChoose(availGameStr);
        String s =  "clienthost refreshAvailableGames games are available";
        ActionEvent ae = new ActionEvent(this, 0,s);
        this.clientControllerListener.actionPerformed(ae);
        break;
      }
    }
  }

  
  private void askForAvailableGames() throws IOException {
    Message msg = new Message();
    msg.setContent("Ask for available games");
    sendMessage(msg);
  }
  
  
  /*
  Returns string of available games
  */
  public String getAvailableGamesStr() {
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

  public String getOldGamesStr() {
    String show = "";
    show += "Previous Games: \n";
    for (int i = 0; i < this.games.size(); i++) {
      show += "****************\n";
      show += "Game " + Integer.toString(i) + "\n";
      show += "****************\n";
    }
    return show;
  }

  

  public void addToGames(Client client) {
    games.add(client);
    client.setIsInRoom(true);
    this.curGame = client;
  }
  
  public void sendMessage(Message msg) throws IOException {
    System.out.println(String.format(" -- clientHost sendMessage content %s", msg.getContent()));    
    if(this.isDebug){
      System.out.println("=== DEBUG MODE ===");
    }
    else {
      ObjectOutputStream os = new ObjectOutputStream(this.skt.getOutputStream());
      os.writeObject(msg);
    }
  }

  public ArrayList<Message> getAvailableGames() {
    return this.availableGames;
  }

  public ArrayList<Client> getGames() {
    return this.games;
  }

  public void setGameInfo(Message gameInfo) {
    this.gameInfo = gameInfo;
  }

  public Message getGameInfo() {
    return this.gameInfo;
  }

  public Client getGameChoosed() {
    return gameChoosed;
  }

  public void setGameChoosed(Client gameChoosed) {
    this.gameChoosed = gameChoosed;
  }

  public void setGames(ArrayList<Client> games) {
    this.games = games;
  }

  public Client getCurGame() {
    return curGame;
  }

  public void setCurGame(Client curGame) {
    this.curGame = curGame;
  }

  

}
