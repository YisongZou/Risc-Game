package edu.duke.ece651.server;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;

public class Server {
  //initialize socket and input stream 
  private ArrayList<Socket> socketArr;
  private ServerSocket serverSkt = null;
  private Parser parser;
  private Message msg;
  private int playerNum;
  private ArrayList<Thread> threads;
  private Socket sktToHM;
  private String hostMasterIP;
  private int hostMasterPort;
  private int listenPort;

  public Server(GameMap gmap) {
    parser = new Parser(gmap);
  }
  
  // constructor with port 
  public Server(int port, int playerNum) throws IOException {
     parser = new Parser(playerNum);
    this.playerNum = playerNum;
    // starts server and waits for a connection
    socketArr = new ArrayList<Socket>();
    serverSkt = new ServerSocket(port);
    this.listenPort = port;
    threads = new ArrayList<>();
    sktToHM = null;
    this.hostMasterPort = 8888;
    System.out.println("Server started");
  }

  public void setHMIP(String hmIP) {
    this.hostMasterIP = hmIP;
  }
  
  public void connectToHostMaster() throws IOException {
    sktToHM = new Socket();
    sktToHM.connect(new InetSocketAddress(this.hostMasterIP, this.hostMasterPort));
  }

  private void sendMessage(Message msg) throws IOException {
    ObjectOutputStream objOutS = new ObjectOutputStream(this.sktToHM.getOutputStream());
    objOutS.writeObject(msg);
  }
  
  public void sendGameInfo() throws IOException {
    Message msg = new Message();
    msg.setGameCapacity(this.playerNum);
    msg.setCurPlyNum(0);
    msg.setContent("Server");
    msg.setGameServerPort(this.listenPort); 
    sendMessage(msg);
  }
  
  public void sendIniMsg() {
    parser.parseMessage(msg);
  }

  public void sendMsg(Message message) {
    parser.parseMessage(message);
  }
  
  public GameMap getCurrentMap() {
    return parser.getMap();
  }  

  public void closeSocket() throws IOException {
    for (Socket skt : socketArr) {
      skt.close();
    }
    serverSkt.close();
  }

  public void connectToPlayer() throws IOException, ClassNotFoundException, InterruptedException {
    int iniPlyNum = playerNum;
    while (iniPlyNum != 0) {
      String clientIdx = Integer.toString(playerNum - iniPlyNum);
      System.out.println("Waiting for client " + clientIdx);
      Socket socket = serverSkt.accept();
      socketArr.add(socket);
      System.out.println("Client " + clientIdx + " accepted");

      // create a new thread to be responsible fro this specific player
      ServerHandler servHan = new ServerHandler(socket, parser);
      ServerHandler.assignPlayerNum(playerNum);
      Thread handler = new Thread(servHan, "Handler_" + clientIdx);
      threads.add(handler);
      handler.start();

      iniPlyNum--;
    }

    // after game over, collect all threads
    for (Thread serverHandler : threads) {
      serverHandler.join();
    }
    closeSocket();     // close all sockets
  }

  public Parser getParser() {
    return this.parser;
  }
  
}
