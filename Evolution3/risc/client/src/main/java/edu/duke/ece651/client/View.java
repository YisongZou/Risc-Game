package edu.duke.ece651.client;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;


interface View {

  public HashMap<String, String> getInputs();
  public Panel makeInitPanel(ActionListener listener);

  public Panel getPanel();
}
