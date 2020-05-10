package edu.duke.ece651.server;

import org.junit.jupiter.api.Test;

import edu.duke.ece651.client.MainController;
import edu.duke.ece651.hostmaster.HostMaster;

public class ServerTest {

  @Test
  public void serverTest() throws Exception{
    System.setProperty("java.awt.headless","true/false");    
    Thread hostmasterThread = new Thread() {
      @Override()
      public void run() {
        try {
          int hmPort = 8888;
          HostMaster hm = new HostMaster(hmPort);
          hm.process();
        }
        catch(Exception except){
          except.printStackTrace();
        }        
      }
    };
    hostmasterThread.start();
    Thread.sleep(1000);
    Thread serverThread = new Thread() {
      @Override()
      public void run() {
        try{
        Server server = new Server(1234, 2); // port num & player num & HMIP
        server.setHMIP("127.0.0.1");
        server.connectToHostMaster();
        server.sendGameInfo();
        server.connectToPlayer();
        }
        catch(Exception except){
          except.printStackTrace();
        }
      }
    };
    serverThread.start();
    Thread.sleep(5000);
    
    Thread clientThread1 = new Thread() {
      @Override()
      public void run() {
        try{
          System.out.println("TESTING HERE");
          MainController mainController = new MainController();
          mainController.start();
          
       } 
        catch(Exception except){
          except.printStackTrace();
        }
      }

      };
    clientThread1.start();
    Thread.sleep(2000);
    clientThread1.stop();

    
  }


}


