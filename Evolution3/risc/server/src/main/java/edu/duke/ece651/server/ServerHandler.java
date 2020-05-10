package edu.duke.ece651.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import edu.duke.ece651.shared.*;

public class ServerHandler extends Thread {
  private Socket skt;
  private InputStream inStr;
  private OutputStream outStr;
  private Parser pars;
  private static int playerNum;
  private String playerName;
  
  private static int count = 0;   // use count to know if all players finish certain step
  private static ArrayList<Message> actMessages = new ArrayList<Message>();
  private static Object locker = new Object();

  public ServerHandler(Socket socket, Parser pars) throws IOException {
    this.skt = socket;
    this.pars = pars;
    inStr = skt.getInputStream();
    outStr = skt.getOutputStream();
  }

  public static synchronized void assignPlayerNum(int plyNum) {
    playerNum = plyNum;
  }

  public static synchronized int getPlayerNum() {
    return playerNum;
  }

  public static synchronized void decreasePlayerNum() {
    playerNum--;
  }

  public static synchronized void addToActMessages(ArrayList<Message> adder) {
    actMessages.addAll(adder);
  }

  // return arrayList that store all actions message
  public static synchronized ArrayList<Message> getActMessages() {
    return actMessages;
  }

  public static synchronized void clearActMessages() {
    actMessages.clear();
  }

  public static synchronized void clearCount() {
    count = 0;
  }
  
  public static synchronized void increaseCount() {
    count++;
  }

  public static synchronized int getCount() {
    return count;
  }

  private Message getMessage() throws IOException, ClassNotFoundException {
    System.out.println("WAIT FOR MESSAGE");
    ObjectInputStream objInS = new ObjectInputStream(inStr);
    Message msgRecved = (Message) objInS.readObject();
    System.out.println("Message received");
    return msgRecved;
  }

  private ArrayList<Message> getArrMessages() throws IOException, ClassNotFoundException {
    //    System.out.println(Thread.currentThread().getName() + ": WAIT FOR MESSAGE ARRAYLIST");
    ObjectInputStream objInS = new ObjectInputStream(inStr);
    ArrayList<Message> msgRecved = (ArrayList<Message>) objInS.readObject();
    //    System.out.println(Thread.currentThread().getName() + ": Message ArrayList received");
    return msgRecved;
  }

  private void sendMessage(Message msg) throws IOException {
    ObjectOutputStream objOutS = new ObjectOutputStream(outStr);
    objOutS.writeObject(msg);
  }
  
  public void iniStage() throws IOException, ClassNotFoundException {
    synchronized (pars) {
      Message iniMsg = pars.initialPrompt();

      sendMessage(iniMsg);
      ArrayList<Message> iniRes = getArrMessages(); // aRRAYlistMsg iniRes
      
      // input first item to parser to assign group
      pars.executeAssignTerritoryGroup(iniRes.get(0));
      Message assignGP = iniRes.remove(0); // remove assign group item, leaving remaining assign units
      pars.executeAssignUnit(iniRes);
      
      // store playerName
      this.playerName = assignGP.getPlayerName();
        
      System.out.println(Thread.currentThread().getName() + ": PLAYER " + playerName + " CHOOSE " + assignGP.getGroupId().toString());
    }
  }

  public void actionStage() throws IOException, ClassNotFoundException, InterruptedException {
    System.out.println(Thread.currentThread().getName() + ": INSIDE ACTIONSTAGE");
    
    // send newly updated gmap to player
    Message updated = new Message();
    updated.setContent("Normal Round");   // indicate that game keep going
    updated.setGmap(pars.getMap());
    sendMessage(updated);

    
    ArrayList<Message> actMsges = getArrMessages();

    // check the first message of actMsges (if there is one) if the content is "leave room", the player leaves the room
    
    addToActMessages(actMsges);   // add actions to the whole arrayList

    synchronized (locker) {
      increaseCount();
      if (getCount() != getPlayerNum()) {
        System.out.println(Thread.currentThread().getName() + ": BEFORE WAIT()");
        locker.wait();
      }
      else {
        System.out.println(Thread.currentThread().getName() + ": BEFORE NOTIFYALL()");
        pars.messageExecutor(getActMessages());   //comment out for test
        locker.notifyAll();
        clearCount();      // assign count to 0
        clearActMessages();     // clear arrayList of actMessages
      }
    }
  }

  private void keepPace(boolean isInitial) {
    synchronized (locker) {
      increaseCount();
      if (getCount() != getPlayerNum()) {
        //System.out.println(Thread.currentThread().getName() + ": BEFORE WAIT() " + Integer.toString(getCount()));
        try {
          locker.wait();
        }
        catch (Exception exp) {
          System.out.println("exp by wait():" + exp);
        }
      }
      else {
         System.out.println(Thread.currentThread().getName() + ": BEFORE NOTIFYALL()");
        if (isInitial) {
          pars.getMap().init_ply_resource();
    //Update the FogInfo field inside map
    HashMap<String, HashMap<String, String>> FogInfo = new HashMap<String, HashMap<String, String>>();
    FogInfo = pars.getMap().getFogInfo();
    
    
    System.out.println("---------------------------");
    //For test only
    for(String playername :pars.getMap().getPlayerNames()){
      HashMap<String,String> terrInfo = new  HashMap<String,String>();
      terrInfo  = FogInfo.get(playername);
      for (String terrName : pars.getMap().getTerritoryNames()) {
        System.out.println(terrName);
        System.out.println(terrInfo.get(terrName));
      }
    }
    System.out.println("---------------------------");
    
    //Set the Nrefresh
    for(String playername : pars.getMap().getPlayerNames()){
      HashMap<String,String> terrInfo = new  HashMap<String,String>();
      terrInfo  = FogInfo.get(playername);
      for (String terrName : pars.getMap().getTerritoryNames()) {
        if(terrInfo.get(terrName).equals("Refresh")){
          terrInfo.replace(terrName, "Nrefresh");
        }
      }
    }
    //Set the Refresh
    for(String playername : pars.getMap().getPlayerNames()){
      for (String own : pars.getMap().getPlayerTerritoryStrings(playername)) {
        FogInfo.get(playername).replace(own, "Refresh");
        for (Territory neighbor : pars.getMap().getEnemyNeighbors(pars.getMap().getTerritory(own), playername) )   {
          String neighName = neighbor.getTerritoryName();
          FogInfo.get(playername).replace(neighName, "Refresh");
        }
        for(String temp : pars.getMap().getSpy(playername).getValidSpyLocation()){
          FogInfo.get(playername).replace(temp, "Refresh");
        }
      }
    }
        }
        locker.notifyAll();
        clearCount();
      }
    }    
  }
  
  public void run() {
    // wait every players connected
    keepPace(false);
    
    // initialization part
    try {
      iniStage();
    }catch (Exception exp) {
      System.out.println("EXCEPTION INSIDE INIT STAGE: " + exp);
      exp.printStackTrace();
    }
    
    
    keepPace(true);    
    System.out.println("INI PART FINISHED, PROVIDED BY " + Thread.currentThread().getName());

    // for test
    
    // action part
    while (true) {
      try {
        if(pars.getMap().gameOver(this.playerName)) {
          Message msg = new Message();
          msg.setContent("You lose!");
          sendMessage(msg);
          decreasePlayerNum();
          break;
        }
        if (pars.getMap().hasWon(this.playerName)) {
          Message msg = new Message();
          msg.setContent("You win!");
          sendMessage(msg);
          break;
        }
        actionStage();
      }    
      catch (Exception exp) {
        System.out.println("EXCEPTION INSIDE ACT STAGE: " + exp);
        exp.printStackTrace();
        break;
      }
    } 
  }
}
