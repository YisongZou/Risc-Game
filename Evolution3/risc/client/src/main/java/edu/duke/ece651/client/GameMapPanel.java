package edu.duke.ece651.client;


import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Territory;

public class GameMapPanel extends PromptPanel implements MouseListener, MouseMotionListener{
  private static Color narniaColor = new Color(54,255,0);
  private static Color midkemiaColor = new Color(255,0,0); 
  private static Color gondorColor = new Color(252,255,0);
  private static Color ozColor = new Color(0,42,255);

  private static Color elantrisColor = new Color(176,65,255);
  private static Color rosharColor = new Color(65,252,255);
  private static Color hogwartsColor = new Color(255,198,65);
  
  private static Color mordorColor = new Color(85,159,60);
  private static Color scadrialColor = new Color(220,255,65);
  private static Color hothColor = new Color(81,65,255);
  
  private BufferedImage img;
  private BufferedImage textureImg;
  
  private String path;
  private String texturePath;
  
  private JLabel mouseLabel;
  private JLabel imageLabel;
  private JLabel imageFeaturesLabel;
  
  private ImageIcon imageMap;
  private ImageIcon imageFeatures;
  
  private HashMap<String, Color> nameToColors;
  private HashMap<Color,String> colorToNames;
  // territory, visiblity
  private HashMap<String, String> territoryVisibility;
  
  private ActionListener listener;
  
  private String[][] clickMappings;
  private Player player;
  private GameMap gmap;

  private String filePath;  
  
  public GameMapPanel(int x, int y, String panelString, String promptString,
                      ArrayList<String> promptButtons,ActionListener listener,
                      Player player) {
    super(x, y, panelString, promptString, promptButtons, listener);
    this.listener = listener;
    this.player = player;
    this.gmap = this.player.getGmap();

    this.w = 600;
    this.h = 600;        
    
    this.panel = new JLayeredPane();
    this.panel.setBounds(this.x, this.y, this.w, this.h);
    this.panel.setLayout(null);    
    this.panel.addMouseListener(this);
    this.panel.addMouseMotionListener(this);
    
    initResources();
    initColorMaps();
  }
  
  /*
    Method for initializaing resources
   */  
  protected void initResources() {
    /* Load filepath */
    filePath = new File("").getAbsolutePath();
    int numGroups = this.gmap.getNumGroups();
    path = filePath +"/../images/gamemap-"+numGroups+".png";      
    texturePath = filePath + "/../images/gamemap-"+numGroups+"-features.png";
    System.out.println(path);
    try {
      this.img = ImageIO.read(new File(path));
      this.textureImg = ImageIO.read(new File(texturePath));
    } catch(Exception except){
      except.printStackTrace();
    }
  }
  
  /*
    Method for assigning players to colors
   */  
  protected void initColorMaps(){
    territoryVisibility = new HashMap<>();
    
    String thisPlayerName = this.player.getName();
    for (String territoryName : this.gmap.getTerritoryNames()) {
      Territory territory = this.gmap.getTerritory(territoryName);
      String visibilityTemp = this.gmap.getVisibility(thisPlayerName, territoryName);
      // if they consume cloak is true, and the territory does NOT belong to player
      if(territory.consumeCloakTurn() && 
         territory.getPlayerName().equals(thisPlayerName)==false){
        visibilityTemp = "Never";
      }            
      System.out.println(String.format("%s %s %s", thisPlayerName,territoryName, visibilityTemp));
      System.out.println("gamemappanel initColorMaps getcloakturn: "+territory.getCloakTurn());      
      territoryVisibility.put(territoryName,visibilityTemp);
    }

    
    /* Initialize territory to color name hash maps */    
    colorToNames = new HashMap<>();
    
    colorToNames.put(narniaColor,"Narnia");
    colorToNames.put(midkemiaColor,"Midkemia");
    colorToNames.put(gondorColor,"Gondor");
    colorToNames.put(ozColor,"Oz");
    
    colorToNames.put(elantrisColor,"Elantris");
    colorToNames.put(rosharColor,"Roshar");
    colorToNames.put(hogwartsColor,"Hogwarts");
    
    colorToNames.put(mordorColor,"Mordor");
    colorToNames.put(scadrialColor,"Scadrial");
    colorToNames.put(hothColor,"Hoth");
    
    /* Initialize color to territory name hash map */
    nameToColors = new HashMap<>();
    // Color[] colors = new Color[]{ Color.blue, Color.cyan, Color.MAGENTA,Color.green, Color.ORANGE};
    Color[] colors = new Color[]{ new Color(64,79,36),new Color(219,202,105),
                                  new Color(129,108,91),new Color(102,141,60)};    
    int i = 0;
    // Iterate through player names
    for (String playerName : this.gmap.getPlayerNames()) {
      // iterate through each players colors
      for(String territoryName : this.gmap.getPlayerTerritoryStrings(playerName)){
        System.out.println("====");
        System.out.println(territoryName);
        System.out.println(playerName);
        
        String visibility = this.territoryVisibility.get(territoryName);
        if(this.promptButtons.contains(territoryName) == false){
          // otherwise show up as gray
          nameToColors.put(territoryName,Color.gray);
        }
        else if(visibility.equals("Nrefresh") || visibility.equals("Never")){
          // if territory is not visible show up as gray
          nameToColors.put(territoryName,colors[i].darker()); 
        }
        else if(this.promptButtons.contains(territoryName)){
          // put only colors included in included territories (buttons)
          nameToColors.put(territoryName,colors[i]);
        } 
      }
      i++;
    }    
    this.clickMappings = mapColorsToArray(this.img, this.nameToColors, this.colorToNames);    
  }
  
  protected String[][] mapColorsToArray(BufferedImage img,
                                HashMap<String, Color> nameToColor,
                                HashMap<Color,String> colorToName){
    int width = img.getWidth(); 
    int height = img.getHeight();
    String[][] mappings = new String[width][height];
    
    Color color;
    for (int y = 0; y < height; y++)  { 
      for (int x = 0; x < width; x++) {
        color = new Color(img.getRGB(x, y), true);
        String name = colorToName.get(color);
        mappings[x][y] = name;
      }
    }
    return mappings;
  }
  
  @Override
  public void mouseClicked(MouseEvent event){    
    Point p = event.getPoint();
    int px =  (int) (p.getX());
    int py = (int) (p.getY());
    System.out.println(  String.format("%s %s",px,py) );
    String s = clickMappings[px][py];
    if(s != null && this.promptButtons.contains(s)){
      System.out.println(s);      
      ActionEvent ae = new ActionEvent(this,0,this.panelString+",0,"+s);
      System.out.println(clickMappings[px][py]);
      this.listener.actionPerformed(ae);
    }
  }
  
  @Override
  public void mousePressed(MouseEvent event){
  
  }
  @Override
  public void mouseReleased(MouseEvent event){
  
  }
  
  @Override
  public void mouseEntered(MouseEvent event){
  
  }
  
  @Override
  public void mouseExited(MouseEvent event){

  }
  
  @Override
  public void mouseMoved(MouseEvent e) {
    int px = e.getX();
    int py = e.getY();
    generatePanel(px, py);
  }
    
  @Override
  public void mouseDragged(MouseEvent e){
    x = e.getX();
    y = e.getY();
  }

  protected void generateImageMap(){
    generateImageMap(0,0);
  }

  protected Color getTerritoryColor(int mx, int my, int px, int py){

    String selectedTerrName = clickMappings[mx][my];
    Territory selectedTerr = this.gmap.getTerritory(selectedTerrName);
    ArrayList<String> playerTerrs = this.gmap.getPlayerTerritoryStrings(this.player.getName());

    Boolean spyOccupying = false;
    
    /*
    if(playerTerrs.contains(selectedTerrName) == false &&
       (selectedTerr.consumeCloakTurn() )){
      return Color.lightGray;
    }
    */
    
    Color pxColor = new Color(img.getRGB(px, py), true);
    String pxTerrName = colorToNames.get(pxColor);
    
    if (pxTerrName != null) {
      Color newColor = nameToColors.get(pxTerrName);
      if (selectedTerrName != null && selectedTerrName.equals(pxTerrName)) {
        // mouse is hovering over
        return newColor.darker();
      }      
      else {
        // mouse not hovering over
        return newColor;
      }
    }
    return null;
  }
  
  protected void generateImageMap(int mx, int my){
    // selectedTerrName is territory mouse hoving over
    String selectedTerrName = clickMappings[mx][my];
    // display info prompt
    if (selectedTerrName != null) {
      if (this.territoryVisibility.get(selectedTerrName).equals("Refresh")) {
        generateMouseLabel(selectedTerrName, mx, my);
      }
      if (this.territoryVisibility.get(selectedTerrName).equals("Nrefresh")) {
        generateMouseLabel(selectedTerrName, mx, my, Color.lightGray);
      }
    }
    generateImage(mx, my);


    
    /* Add image map */
    this.imageMap = new ImageIcon(this.img);
    this.imageLabel = new JLabel(this.imageMap);
    this.imageLabel.setBounds(0, 0, 600, 600);
    
    /* Add image feature map */
    this.imageFeatures = new ImageIcon(this.textureImg);  // transform it back
    this.imageFeaturesLabel = new JLabel(this.imageFeatures);
    this.imageFeaturesLabel.setBounds(0, 0, 600, 600);
    
    this.panel.add(this.imageFeaturesLabel,1);
    this.panel.add(this.imageLabel,2);
  }

  public void generateMouseLabel(String selectedTerrName, int mx, int my){
    generateMouseLabel(selectedTerrName, mx, my,Color.white);
  }
  
  public void generateMouseLabel(String selectedTerrName, int mx, int my,Color color){
    if (selectedTerrName != null){
      String terrInfo = this.player.promptTerritoryString(selectedTerrName);
      mouseLabel = new JLabel("<html>" + terrInfo + "</html>");
      mouseLabel.setBounds(mx, my, 150, 180);
      mouseLabel.setBackground(color);
      mouseLabel.setOpaque(true);
      panel.add(this.mouseLabel,0);
    }
    

    if (selectedTerrName == null && mouseLabel != null) {
      this.panel.remove(mouseLabel);
    }
  }

  public void generateImage(int mx, int my){
    try {
      this.img = ImageIO.read(new File(path));
      this.textureImg = ImageIO.read(new File(texturePath));
    } catch(Exception except){
      except.printStackTrace();
    }
    // set territory colors to respective player colors
    for (int y = 0; y < this.img.getHeight(); y++) {
      for (int x = 0; x < this.img.getWidth(); x++) {
        Color newColor = getTerritoryColor(mx,my, x,y);
        if(newColor != null){
          img.setRGB(x, y, newColor.getRGB());

        }
      }
    }

  }
  
  public void generatePanel(int px,int py) {
    
    this.panel.removeAll();
    generateImageMap(px,py);
    this.panel.revalidate();    
    this.panel.repaint();
  }

  public void generatePanel() {
    initResources();
    generatePanel(0, 0);
  }  
  

  public JLayeredPane getPanel(){
    return this.panel;
  }
}
    


