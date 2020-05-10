package edu.duke.ece651.client;

public class Game {
  private String playerName;
  private String password;
  private Client client;

  public Game(Client client, String playerName, String password) {
    this.playerName = playerName;
    this.password = password;
    this.client = client;
  }

  public void setPlayerName(String plyName) {
    this.playerName = plyName;
  }

  public String getPlayerName() {
    return this.playerName;
  }

  public void setPassword(String psd) {
    this.password = psd;
  }

  public String getPassword() {
    return this.password;
  }

  public void setClient(Client clt) {
    this.client = clt;
  }

  public Client getClient() {
    return this.client;
  }
}
