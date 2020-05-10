package edu.duke.ece651.hostmaster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import edu.duke.ece651.shared.*;

public class HostServer extends Thread {
  private Socket skt;
  private InputStream inStr;
  private OutputStream outStr;
  private static ArrayList<Socket> clientSkts = new ArrayList<>();

  public static synchronized void setClientSkts(ArrayList<Socket> clientSktsArr) {
    clientSkts = clientSktsArr;
  }

  public HostServer(Socket socket) throws IOException {
    this.skt = socket;
    inStr = skt.getInputStream();
    outStr = skt.getOutputStream();
  }

  
}
