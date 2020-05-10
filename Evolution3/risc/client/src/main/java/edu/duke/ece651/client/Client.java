package edu.duke.ece651.client;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

// import edu.duke.ece651.server.Parser;
import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;

public class Client extends Thread {
  public ActionListener clientHostListener;
  public ActionListener clientControllerListener;
  private Player player;

  private Socket skt;
  private int port;
  private String serverIP; // IP OF GAME SERVER
  private boolean isInRoom;
  private boolean isDebug;
  
  private String password;
  private Object locker; // use to lock operations that read/write isInRoom
  JFrame frame;

  private boolean isGameEnd;
  
  public Client(String playerName,  GameMap gmap) {
    GameMap gm = gmap;
    player = new Player("Green", gm);
  }
  
  public Client(String playerName, int port, String serverIP, JFrame frame) {
    this.port = port;
    this.serverIP = serverIP;
    
    this.isInRoom = true;
    this.isGameEnd = false;
    
    this.locker = new Object();
    this.frame = frame;
    this.player = new Player(playerName);
  } 

  public Client(String playerName, int port, String serverIP, JFrame frame,
                boolean isDebug) {
    this(playerName, port,  serverIP,frame);
    this.isDebug = isDebug;

  }


  public Client(String playerName, int port, String serverIP, JFrame frame,
                ActionListener clientListener, boolean isDebug){
    this(playerName, port, serverIP, frame, isDebug);
    this.clientControllerListener = clientListener;
  }  

  /*
    thread entry point
  */  
  public void run() {
    System.out.println("Client - run");
    try {
      connectToServer(this.serverIP);      
      iniStage();
      // this.getPlayer().getGmap().setTerritoryGroupPlayer(1, this.getPlayer().getName());      
      waitThenAction();      
    } catch (Exception exp) {
      exp.printStackTrace();
    }
  }


  public void connectToServer(String serverIP) throws IOException,
                                                      InterruptedException {
    System.out.println("Trying to connect to server...");
    if (this.isDebug == false) {
        Thread.sleep(500);      
      while (true) {
        try {
          this.iniSocket(serverIP, port); // what will happen if server is not available?????
        } catch (IOException exp) {
          throw exp;
        }
        System.out.println("Connected to Server!");

        break;
      }
    } else{
        System.out.println("Debugging - not connected to Server!");
    }
  }
  
  public void iniSocket(String host, int port) throws IOException {
    skt = new Socket();
    skt.connect(new InetSocketAddress(host, port), 10000000); // times out after 1000 ms
  }

  private void iniStage() {
    System.out.println("Client - iniStage");    
    try {    
      if (this.isDebug == true) {
        // debugging mode
        GameMap gmap = new GameMap(5);
        gmap.setTerritoryGroupPlayer(0, "Adam");
        gmap.setTerritoryGroupPlayer(1, player.getName());
        gmap.setTerritoryGroupPlayer(2, "Alex");
        gmap.setTerritoryGroupPlayer(3, "Joe");
        gmap.setTerritoryGroupPlayer(4, "Jane");
        this.player.setName(player.getName());
        this.player.setGmap(gmap);
        this.frame.getContentPane().removeAll();
        
        System.out.println(this.player.getGmap().getPlayerNames());
      }
      else{
        // live deployment mode
        this.frame.getContentPane().removeAll();
        JLabel label = new JLabel("Waiting for all other players to join");
        label.setBounds(0,0, 100, 40);
        this.frame.add(label);
        
        System.out.println("client inistage() waiting to recieve message");        
        Message iniMsg = recvMessage();
        this.player.setGmap(iniMsg.getGmap(),"init");
        this.frame.getContentPane().removeAll();
        System.out.println("client inistage() recieved message");                
        System.out.println("current players connected: "+ this.player.getGmap().getPlayerNames());
      }
      ActionEvent ae = new ActionEvent(this, 0, "client: inform clientController assign units");
      this.clientControllerListener.actionPerformed(ae);
      // this.player.displayAssignUnits(this);
    } catch (Exception exp) {
      System.out.println("Exception inside iniStage");
      exp.printStackTrace();
    }
  }

  public void waitThenAction() throws IOException, ClassNotFoundException {
    System.out.println("client - waitThenAction");
    Message iniMsg;
    if(this.isDebug == true){
      iniMsg = new Message();
      this.player.getGmap().add_player(this.getPlayer().getName());
      this.player.getGmap().init_ply_resource();      
      this.player.getGmap().addFoodForPlayer();
      this.player.getGmap().addTechForPlayer();
      this.player.getGmap().getPly_resource().setTechlevel(this.getPlayer().getName(), 1);;            
      iniMsg.setGmap(this.player.getGmap());
      iniMsg.setContent("");
    }
    else {
      System.out.println("Client:  waiting for new message from server");
      iniMsg = recvMessage();
      /*
      System.out.println(" received messages from server");
      // notify ready to start displaying orders
      String s = "client : inform mainController start game";
      ActionEvent ae = new ActionEvent(this, 0, s);
      this.clientControllerListener.actionPerformed(ae);  
      */
    }
    actionStage(iniMsg);
  }

  /*
    Methods after game has started
   */
  public boolean getIsInRoom() {
    boolean res = true;
    synchronized (locker) {
      res = this.isInRoom;
    }
    return res;
  }

  String winString = "Winner winner chicken dinner! Returning you to connect screen";
  JDialog d;
  protected void throwException(Exception except){
    // make new window and print errror
    d = new JDialog(this.frame, "Game Over");
    
    System.out.println("prompt - throw exception");
    if(d != null){
      d.setVisible(false);
    }

    if (    except.getMessage().equals(winString)){
      String filePath = new File("").getAbsolutePath();
      String path = filePath +"/../images/winnerwinnerchickendinnner.png";      
      System.out.println(path);
      BufferedImage img = null;
      try {
        img = ImageIO.read(new File(path));
      } catch(Exception exception){
        exception.printStackTrace();
      }
      // load image panel
      ImageIcon imageMap = new ImageIcon(img);
      JLabel imageLabel = new JLabel(imageMap);
      imageLabel.setBounds(30, 50, 173, 213);
      d.add(imageLabel);
    }    

    // setsize of dialog 
    d.setLayout(null);
    d.setSize(300, 300);
    // create a label
    JLabel l = new JLabel("<html>"+except.getMessage()+"</html>"); 
    l.setBounds(0,0,300,50);
    d.add(l); 


    // set visibility of dialog 
    d.setVisible(true);       
    except.printStackTrace();
  }
  

  public void actionStage(Message iniMsg)  throws IOException{
    System.out.println("client - actionStage(iniMsg)");
    try{
      if (iniMsg.getContent().equals("You lose!") ||
          iniMsg.getContent().equals("You win!")) {
        System.out.println(iniMsg.getContent()); // tell player the result
        this.isGameEnd = true;
        // This.player.getFrame().setVisible(false);
        this.clientControllerListener.actionPerformed(new ActionEvent(this,0,"displayConnectToHostMaster"));
        if (iniMsg.getContent().equals("You win!")) {
          throw( new Exception(winString));
        } else{
          throw(new Exception("You lost! ): Returning you to connect screen"));
        }
      }
      // ask player if he want to leave room, if he is in

      if(getIsInRoom()) {
        System.out.println("client - actionStage , in room, now playTurn");
        System.out.println("client actionstage iniMsg gamemap terrs: "+iniMsg.getGmap().getTerritoryNames());          
        playTurn(iniMsg);
      }
      else if(getIsInRoom() == false){
        sendMessage(new ArrayList<>());
      }
    } catch (Exception except){
      throwException(except);
    }
  }

  public void playTurn(Message serverMsg) {
    this.player.movedFlag = 0;
    this.player.upgradeFlag = 0;        
    this.player.upgradetech_flag = 0;                  
    
    if (this.isDebug == false) {
      // update player game map
      this.player.setGmap(serverMsg.getGmap());
    } else{
    }    
    // make panel here
    // now start game
    ActionEvent ae = new ActionEvent(this,0,"client: inform ClientController displayOrder");
    this.clientControllerListener.actionPerformed(ae);
  }
  
  /*
    Methods below are for server socket communication
   */
  public Message recvMessage() throws IOException, ClassNotFoundException {
    System.out.println("recieving messages");
    ObjectInputStream objIn = new ObjectInputStream(skt.getInputStream());
    Message iniMsg = (Message) objIn.readObject();
    return iniMsg;
  }

  public void sendMessage(ArrayList<Message> msg) throws IOException {
    // print messages list for debugging
    System.out.println("=== Client : sendMessage() ===");
    if (this.isDebug == false) {
      for (Message m : this.player.getMessages()) {
        String s = String.format("%s %s %s", m.getType(),
                                 m.getPlayerName(), m.getSrcTerritory());
        System.out.println(s);
      }
      if (skt != null) {
        ObjectOutputStream os = new ObjectOutputStream(skt.getOutputStream());
        os.writeObject(msg);
      }
    }
    else{
      System.out.println("====== DEBUG MODE =======");
    }
    System.out.println("Client - sent message");
  }

  public void leaveGame(){
    
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

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

 
  public String getServerIP() {
    return this.serverIP;
  }

  public int getPort() {
    return this.port;
  }

  
  
}
