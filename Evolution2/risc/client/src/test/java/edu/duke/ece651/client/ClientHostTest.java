package edu.duke.ece651.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class ClientHostTest {
  @Test
  public void test_() {
    Scanner in = new Scanner(System.in);
    ClientHost ch = new ClientHost("127.0.0.1", 8888, in);
    ArrayList<Client> games = new ArrayList<Client>();
    ch.setGames(games);
    boolean res = ch.notInRoom(); 
    assertEquals(res, true);
  }

}
