/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.duke.ece651.server;

import java.io.IOException;
import java.util.Scanner;

import edu.duke.ece651.client.MainController;
import edu.duke.ece651.hostmaster.*;


public class App {
  public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
    // for debugging
    //debugStart();
    // uncomment this for a normal start
     normalStart();
  }


  /* Methods below are for starting debug mode, or normal start */ 
  public static void debugStart() throws IOException, ClassNotFoundException, InterruptedException {
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
    Thread.sleep(2000);
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
    Thread.sleep(1000);

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


  }

  public static void normalStart() throws IOException, ClassNotFoundException, InterruptedException {
    int playerNum = 0;
    int listenPort = 0;
    String hostMasterIP;
    Scanner in = new Scanner(System.in);
    while (true) {
      try {
        System.out.println("Please enter player number:");
        playerNum = Integer.parseInt(in.nextLine());
        if (playerNum >= 2 && playerNum <= 5) {
          break;
        }
        System.out.println("INVALID INPUT, TRY AGAIN");
      } catch (Exception exp) {
        System.out.println("INVALID INPUT, TRY AGAIN");
      }
    }
    while (true) {
      try {
        System.out.println("Please enter HostMaster IP:");
        hostMasterIP = in.nextLine();
        if (hostMasterIP.length() >= 1) {
          break;
        }
        System.out.println("INVALID INPUT, TRY AGAIN");
      } catch (Exception exp) {
        System.out.println("INVALID INPUT, TRY AGAIN");
      }
    }
    while (true) {
      try {
        System.out.println("Please enter listening port:");
        listenPort = Integer.parseInt(in.nextLine());
        if (listenPort > 0) {
          break;
        }
        System.out.println("INVALID INPUT, TRY AGAIN");
      } catch (Exception exp) {
        System.out.println("INVALID INPUT, TRY AGAIN");
      }
    }


    
    in.close();            
    Server server = new Server(listenPort, playerNum); // port num & player num & HMIP
    server.setHMIP(hostMasterIP);
    server.connectToHostMaster();
    server.sendGameInfo();
    server.connectToPlayer();    

  }
}
