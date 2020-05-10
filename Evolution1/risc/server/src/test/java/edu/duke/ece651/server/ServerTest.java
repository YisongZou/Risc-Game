package edu.duke.ece651.server;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.duke.ece651.client.Client;
import edu.duke.ece651.shared.BasicUnit;
import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;

import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class ServerTest {

  /*
  public String sendRequest(String rString) throws IOException {
    Socket s = new Socket("localhost", 12345);
    s.getOutputStream().write(rString.getBytes());
    s.getOutputStream().flush();
    s.shutdownOutput();
    BufferedReader br =
        new BufferedReader(new InputStreamReader(s.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String str = br.readLine();
    while (str != null) {
      System.out.println("Read: " + str);
      sb.append(str);
      sb.append("\n"); // gets stripped off by br.readLine()
      str = br.readLine();
    }
    return sb.toString();
  }

  @Test
  public void testServer() throws IOException, InterruptedException {
    Thread th = new Thread() {
      @Override()
      public void run() {
        try {
          String[] args = { "a", "b" };
          App.main(args);
        } catch (Exception e) {
        }
      }
    };
    th.start();
    Thread.sleep(100); // this is a bit of a hack.

    String actual = sendRequest("green\5\n5\n5\n");
    th.interrupt();
    th.join();
  }
  */
  
  @Test
  public void testGameMap() {
    GameMap gmap = new GameMap();
    Territory t1 = new Territory("America");
    Territory t2 = new Territory("Mexico");
    Territory t3 = new Territory("Canada");
    Territory t4 = new Territory("China", "Mao", new BasicUnit());
    Territory t5 = new Territory("Europe", "Napoleon");
    
    // test territory was added correctly
    assertEquals(true,gmap.addTerritory(t1));
    // test whether getTerritoryNames
    ArrayList<String> tnames = gmap.getTerritoryNames();
    for (String name: tnames){
      assertEquals("America", name);
    }

    // test find territory
    assertEquals(t1,gmap.findTerritory("America"));
    // territory that doesnt exist
    assertEquals(null,gmap.findTerritory("Narnia"));

    // test adding a territory
    gmap.addTerritory(t2,t1);
    assertEquals(t2,gmap.findTerritory("Mexico"));

    // test creating a connection between two territories
    gmap.addTerritory(t3);
    assertEquals(t3,gmap.findTerritory("Canada"));
    
    // confirm that a newly created territory has player
    // name set to null
    assertEquals(null,gmap.findTerritory("Canada").getPlayerName());
    gmap.getMapGraph();
    
    // check that squad is being init properly
    BasicUnit s2 = new BasicUnit(2);
    assertEquals(2, s2.getNumUnits());
    
    BasicUnit s =  (BasicUnit) t1.getUnit();
    assertEquals(0,s.getNumUnits());
    assertEquals(0,s.getNumUnits());
    // check if set squad properly
    t2.setUnit(s);
    assertEquals(t2.getUnit(), s);
    // check if add unit method works
    s.addUnit(11);
    assertEquals(11,t2.getUnit().getNumUnits());

    assertEquals(true,s.removeUnit(2));
    assertEquals(9,t2.getUnit().getNumUnits());
    t2.addUnit(3);
    assertEquals(12,s.getNumUnits());
    // number of units in squad cannot be less than 0
    assertEquals(false,s.removeUnit(13));
    assertEquals(false,t2.removeUnit(13));
    assertEquals(t4.getPlayerName(), "Mao");
    
    // test set player name    
    t4.setPlayerName("Xi");
    assertEquals(t4.getPlayerName(), "Xi");


    // test get territory that returns null
    assertEquals(null, gmap.getTerritory("Mars"));
    
    // to do: add case to make sure territory name does NOT
    // already exist!
    gmap.connectTerritory(gmap.getTerritory("America"), gmap.getTerritory("Canada"));
    gmap.connectTerritory(gmap.getTerritory("America"), gmap.getTerritory("Mexico"));
    gmap.connectTerritory(t1, t3);
    
    // test get neighbor territories getNeighbors()
    List<Territory> neighbors = gmap.getNeighbors(t1);
    assertEquals(true, neighbors.contains(gmap.getTerritory("Canada")));
    assertEquals(true, neighbors.contains(gmap.getTerritory("Mexico")));
    
    // test get neighbor strings given a territory getNeighborStrings()
    ArrayList<String> stringNs = gmap.getNeighborStrings(t1);
    assertEquals(true, stringNs.contains("Canada"));
    assertEquals(true, stringNs.contains("Mexico"));
        
    // test getPlayerTerritories()
    gmap.addTerritory(t4);
    assertEquals(true,gmap.getPlayerTerritories("Xi").contains(gmap.getTerritory("China")));
    
    // test getTerritory
  }

  @Test
  public void testGraph(){
    GameMap gmap = new GameMap(3);

    assertEquals(3,gmap.getUnclaimedGroupIds().size());
    
    gmap.setTerritoryGroupPlayer(0, "Green");
    gmap.setTerritoryGroupPlayer(1, "Green");
    gmap.setTerritoryGroupPlayer(2, "Red");
    gmap.findTerritory("Roshar").setPlayerName("Green");

    // these territories are not connected
    assertEquals(true, gmap.isConnected(gmap.getTerritory("Narnia"),
                                         gmap.getTerritory("Midkemia")));
    
     
    assertEquals(true, gmap.isConnected(gmap.getTerritory("Narnia"),
                                         gmap.getTerritory("Roshar")));

  }  
  
  @Test
  public void testServerClient() throws Exception {
    System.out.println("=========");
    File currentDirFile = new File("");
    String helper = currentDirFile.getAbsolutePath();
    InputStream inputstream = new FileInputStream(helper + "/resources/test.txt");
    
    Server server = new Server(new GameMap(3));
    Client client = new Client("Green", inputstream, server.getParser().getMap());
    
    // now let client assign territory
    Message serverMsg = new Message();
    serverMsg.setGmap(server.getCurrentMap());
    ArrayList<Message> m = client.initializationTurn(serverMsg);
    
    // server.getParser().parseMessage(m);
    // first item in list is assign groupid
    server.getParser().executeAssignTerritoryGroup(m.get(0)); 
    m.remove(m.get(0));
    server.getParser().executeAssignUnit(m); // the rest are unit num assignments
    
    assertEquals(5,
                 server.getParser().getMap().
                 getTerritory("Elantris").getUnit().getNumUnits());
    
    // Nowo play client turn
    serverMsg = new Message();
    serverMsg.setGmap(server.getCurrentMap());
    m = client.playTurn(serverMsg);
    server.getParser().parseMessage(m);
    
    // client plays again
    serverMsg = new Message();
    serverMsg.setGmap(server.getCurrentMap());
    m = client.playTurn(serverMsg);
    server.getParser().messageExecutor(m);
    System.out.println("=========");


    //File currentDirFile = new File("");
    InputStream input = new FileInputStream(currentDirFile.getAbsolutePath() + "/resources/test.txt");
    Client myclient = new Client("Green", 12344, input, "127.0.0.1");
    ServerSocket skt = new ServerSocket(12344);
    Parser psr = new Parser(1);
    myclient.connectToServer("127.0.0.1");
    Socket socket = skt.accept();
    ServerHandler handler = new ServerHandler(socket, psr);
    myclient.sendMessage(m);
    handler.assignPlayerNum(1);
    System.out.println("PLAYER NUM IS " + handler.getPlayerNum());
    handler.actionStage();
  }
  /*
  @Test
  public void server_handler() throws IOException{
    File currentDirFile = new File("");
    InputStream input = new FileInputStream(currentDirFile.getAbsolutePath() + "/resources/test.txt");
    Client myclient = new Client("Green", 12344, input, "127.0.0.1");
    ServerSocket skt = new ServerSocket(12344);
    Parser psr = new Parser(3);
    myclient.connectToServer("127.0.0.1");
    Socket socket = skt.accept();
    ServerHandler handler = new ServerHandler(socket, psr);
    myclient.sendMessage(null);
    handler.run();
  }
  */
  
  @Test
  public void test_sever_connect() throws Exception{
    File currentDirFile = new File("");
    InputStream input = new FileInputStream(currentDirFile.getAbsolutePath() + "/resources/test.txt");
    testplayer p1 = new testplayer("Green", input);
    testplayer p2 = new testplayer("Purple", input);
    testserver t1= new testserver();
    Thread ply1 = new Thread(p1);
    Thread ply2 = new Thread(p2);
    Thread svr1 = new Thread(t1);
    ply1.sleep(100);
    ply2.sleep(100);
    svr1.start();
    ply1.start();
    ply2.start();
  }

  class testplayer implements Runnable {
    Client myclient;
    public testplayer(String name, InputStream input) {
      myclient = new Client(name, 12345, input, "127.0.0.1");
    }
    public void run(){
      try{
        myclient.connectToServer("127.0.0.1");
        //myclient.playTurn();
        //myclient.sendMessage();
      }
      catch (IOException exp) {
      };
    }
  }

  class testserver implements Runnable {
    Server myserver;

    public testserver() throws Exception{
      myserver = new Server(12345, 2);
    }

    public void run() {
      try{
        //myserver.sendIniMsg();
        //myserver.sendMsg(new Message());
        myserver.connectToPlayer();
        //myserver.sendIniMsg();
      //myserver.closeSocket();
      }
      catch(Exception exp) {
      };
    }
  }
}
