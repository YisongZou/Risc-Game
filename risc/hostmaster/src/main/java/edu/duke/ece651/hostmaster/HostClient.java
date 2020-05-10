package edu.duke.ece651.hostmaster;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import edu.duke.ece651.shared.Message;

public class HostClient extends Thread {
  private Socket skt;    // socket with player
  private InputStream inStr;
  private OutputStream outStr;
  private boolean isInGame;
  private static ArrayList<Message> serverInfo = new ArrayList<>();

  public static synchronized void setServerInfo(ArrayList<Message> serverInfosArr) {
    serverInfo = serverInfosArr;
  }

  public HostClient(Socket socket) throws IOException {
    this.skt = socket;
    inStr = skt.getInputStream();
    outStr = skt.getOutputStream();
    this.isInGame = false;
  }

  private void sendMessage(Message msg) throws IOException {
    ObjectOutputStream objOutS = new ObjectOutputStream(outStr);
    objOutS.writeObject(msg);
  }

  private void sendMessageArr(ArrayList<Message> msgs) throws IOException {
    ObjectOutputStream objOutS = new ObjectOutputStream(outStr);
    objOutS.writeObject(msgs);
  }

  private Message getMessage() throws IOException, ClassNotFoundException {
    System.out.println(Thread.currentThread().getName() + ": WAIT FOR MESSAGE");
    ObjectInputStream objInS = new ObjectInputStream(inStr);
    Message msgRecved = (Message) objInS.readObject();
    System.out.println(Thread.currentThread().getName() + ": Message received");
    return msgRecved;
  }

  private ArrayList<Message> getMessageArr() throws IOException, ClassNotFoundException {
    System.out.println(Thread.currentThread().getName() + ": WAIT FOR MESSAGEARR");
    ObjectInputStream objInS = new ObjectInputStream(inStr);
    ArrayList<Message> msgRecved = (ArrayList<Message>) objInS.readObject();
    System.out.println(Thread.currentThread().getName() + ": MessageARR received");
    return msgRecved;
  }
  
  private void sendAvailableGames() {
    // display available games
    synchronized (serverInfo) {
      try {
        sendMessageArr(serverInfo); 
      }
      catch(Exception exp) {
        exp.printStackTrace();
      }
    }
  }

  
  private void updateServerInfo(Message msg) {
    synchronized (serverInfo) {
      Message choosed = null;
      for (Message info : serverInfo) {
        if (info.getGameServerIP().equals(msg.getGameServerIP()) && info.getGameServerPort().equals(msg.getGameServerPort())) {
          choosed = info;
        }
      }  
      choosed.setCurPlyNum(choosed.getCurPlyNum()+1);
      this.isInGame = true;
    }
  }

  private void removeFinishedGame(Message msg) {
    synchronized (serverInfo) {
      Message choosed = null;
      for (Message info : serverInfo) {
        if (info.getGameServerIP().equals(msg.getGameServerIP())
            && info.getGameServerPort().equals(msg.getGameServerPort())) {
          choosed = info;
        }
      }
      serverInfo.remove(choosed);
    }    
  }
  
  public void run() {
    while (true) {
      try {
        Message msg = getMessage();
        if (msg.getContent() != null && msg.getContent().equals("Ask for available games")) {
          sendAvailableGames();
          Message playerChoice = getMessage();
          // for test
          System.out.println("playerchoice receive content: " + playerChoice.getContent());
          if (playerChoice.getContent().equals("I will keep ask")) {
            continue;
          }
          updateServerInfo(playerChoice);
        }
        if (msg.getContent() != null && msg.getContent().equals("this game ends")) {
          removeFinishedGame(msg);
        }
      }
      catch(Exception exp) {
        exp.printStackTrace();
        break;
      }      
    }
  }

  
}
