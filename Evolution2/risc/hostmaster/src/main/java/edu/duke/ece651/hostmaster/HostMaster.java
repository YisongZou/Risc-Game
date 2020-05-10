package edu.duke.ece651.hostmaster;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import edu.duke.ece651.shared.Message;

public class HostMaster {
  private ArrayList<Socket> clientSkts;
  private ArrayList<Message> serverInfo;      // store info of all availabe games
  private HashMap<String, String> userInfo;   // store <username, password>
  private ServerSocket hostServerSkt = null;
  private ArrayList<Thread> clientThreads;
  private ArrayList<Thread> serverThreads;
  private int clientNum;
  private int serverNum;

  public HostMaster(int port) throws IOException {
    clientSkts = new ArrayList<>();
    serverInfo = new ArrayList<>();
    userInfo = new HashMap<>();
    clientThreads = new ArrayList<>();
    serverThreads = new ArrayList<>();
    hostServerSkt = new ServerSocket(port);    // port should be 8888
    clientNum = 0;
    serverNum = 0;
  }

  private Message getMessage(Socket skt) throws IOException, ClassNotFoundException {
    System.out.println("WAIT FOR MESSAGE");
    ObjectInputStream objInS = new ObjectInputStream(skt.getInputStream());
    Message msgRecved = (Message) objInS.readObject();
    System.out.println("Message received");
    return msgRecved;
  }
  
  public void process() throws IOException, ClassNotFoundException {
    while (true) {
      System.out.println("Waiting for client & game server");
      Socket skt = hostServerSkt.accept();
      Message typeMsg = getMessage(skt);
      
      if (typeMsg.getContent().equals("Client")) {   // new player connected
        System.out.println("Client coming");
        clientSkts.add(skt);
        HostClient hc = new HostClient(skt);  // hc: hostClient
        if (this.clientNum == 0) {
          synchronized (this.serverInfo) {     // initiaize serverInfo shared by all HC
            HostClient.setServerInfo(this.serverInfo);
          }
        }
        Thread hcHandler = new Thread(hc, "HCHandler_" + this.clientNum);
        clientThreads.add(hcHandler);
        this.clientNum++;
        hcHandler.start();
      }
      else {    // new game server connected
        System.out.println("GameServer coming");
        typeMsg.setGameServerIP(skt.getInetAddress().getHostAddress());

        // for test
        System.out.println("GameServerIP: " + typeMsg.getGameServerIP());
        System.out.println("GameServerPort: " + typeMsg.getGameServerPort());
        
        synchronized (serverInfo) {
          serverInfo.add(typeMsg);
        }
        /*
        HostServer hs = new HostServer(skt);  // hs: hostServer
        HostServer.setClientSkts(this.clientSkts);
        Thread hsHandler = new Thread(hs, "HSHandler_" + this.serverNum);
        serverThreads.add(hsHandler);
        this.serverNum++;
        hsHandler.start();  */
      }
    }
  }
}
