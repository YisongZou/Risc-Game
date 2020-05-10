package edu.duke.ece651.client;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class ClientController implements ActionListener {
  private TextFieldPanel fieldPanel = null;
  // private GameMapView gmView;
  JFrame frame;
  int i;

  public ClientController() {
    i = 0;
    this.frame = new JFrame("Client Controller");
    // initialize frame
    this.frame.setSize(400, 800);
    this.frame.setLayout(null);
    this.frame.setVisible(true);
    connectToHostMaster();
  }

  public void connectToHostMaster(){
    ArrayList<String> s = new ArrayList<>();

    s.add("Please type in HostMaster IP:");
    this.fieldPanel = new TextFieldPanel(0, 0, "clientConnectHostMaster","",s,this);
    this.fieldPanel.generatePanel();
    this.frame.add(fieldPanel.getPanel());
  }

public void parseHostMaster(String[] cmds) throws Exception{

    String hostMasterIP = this.fieldPanel.
      getInput("Please type in HostMaster IP:");
    if(hostMasterIP.length() < 1){
      throw new Exception("HostMaster IP cannot be empty. Please try again");
    }
    // hostmaster port num is 8888
    ClientHost ch = new ClientHost(hostMasterIP, 8888,frame,this);
    ch.proccess();
    
  }
  
  @Override
  public void actionPerformed(ActionEvent e){
    String command = e.getActionCommand();
    String cmds[] = command.split(",");
    
    // for debugging
    // System.out.println(String.format("==== %s", command));    
    
    try {
      if (cmds[0].equals("clientConnectHostMaster")) {
        this.frame.getContentPane().removeAll();        
        parseHostMaster(cmds);
      }

    }
    catch (Exception exception){
      exception.printStackTrace();
      // make new window and print errror
      JDialog d = new JDialog(this.frame, "Error"); 
      // create a label 
      JLabel l = new JLabel(exception.getMessage()); 
      d.add(l); 
      // setsize of dialog 
      d.setSize(500, 300);
      // set visibility of dialog 
      d.setVisible(true);
      connectToHostMaster();
      
    }
  }
}
  
