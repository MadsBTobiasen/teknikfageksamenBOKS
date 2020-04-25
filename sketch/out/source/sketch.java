import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 
import static javax.swing.JOptionPane.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch extends PApplet {




PFont font;
Capture cam;
UIElements uielement;
XMLHandler xmlHandler;
//
Scanner scanner;
//
PillAdder pillAdder;
int r = 0;
int g = 0;
int b = 0;
int c = 0xffb4b4b4;
int currentScene = 1; 
/* 

currentScene er en variable der kontrollere hvilken menu der bliver vist. 
0 = Startmenu.
1 = Program kører og status indikatører bliver vist.
2 = Opsætningsmenu, hvor brugeren indstiller IP-adresse på enheden.
3 = "Pill-Adder", en menu hvor brugeren kan tilføje nye piller til systemet.

*/

int pillPixelX = 0;
int pillPixelY = 0;
int pillColorRangeMin = 0;
int pillColorRangeMax = 0;

public void setup() {
    

    font = createFont("Arial", 32);
    textFont(font);

    cam = new Capture(this);
    cam.start();

    uielement = new UIElements();
    xmlHandler = new XMLHandler();
    //
    scanner = new Scanner();
    //
    pillAdder = new PillAdder(); 

}

public void draw() {

    //Startmenu
    if (currentScene == 0) {

    }
    
    //Scanner
    if (currentScene == 1) {

        scanner.start();

    }

    //Opsætning
    if (currentScene == 2) {

    }

    //Pill-Adder
    if (currentScene == 3) {

        pillAdder.start();

    }

}

public void mousePressed() {
    println(mouseX + " " + mouseY);
}
class PillAdder {
    
    int pillPixelX = 0;
    int pillPixelY = 0;
    int pillColorRangeMin = 0;
    int pillColorRangeMax = 0;
    int framesToTake = 100;

    boolean informationSeen = false;

    //Variabler til GUI.
    int camX = uielement.camX;
    int camY = uielement.camY;
    int camW = uielement.camW;
    int camH = uielement.camH;
    
    int seperatorW = 5;
    int backgroundC = 0xffb4b4b4;

    int bttnHelpStartW = (camW-seperatorW*2)/2;    
    int bttnHelpStartH = (height-camH-3*seperatorW)/2;

    int bttnStartX = seperatorW;
    int bttnStartY = seperatorW;
    int bttnHelpX = seperatorW*2+bttnHelpStartW;
    int bttnHelpY = seperatorW;
    
    int pillColorFieldX = seperatorW;
    int pillColorFieldY = 2*seperatorW+bttnHelpStartH;
    int pillColorFieldW = camW - seperatorW;
    int pillColorFieldH = bttnHelpStartH;

    boolean drawColorBoxes = false;
    int pillTextX = camW+seperatorW*2;
    int pillTextY = seperatorW;
    int pillTextColorBoxW = 50; 
    int pillTextColorBoxX = width-seperatorW-pillTextColorBoxW;
    int pillTextW = width-seperatorW-pillTextX;
    int pillTextH = 50;
    int averageColor = 0;
    int textSize = 28;
    int boxSplitterW = 10;
    String xmlPillName = "";
    //Variabler til GUI slut.

    PillAdder() {

    }
    
    public void start() {
        colorMode(RGB, 255, 255, 255);

        drawUI();

        if (!informationSeen) {
            uielement.informationDialog("Hjælp", "Velkommen til Pill-Adder.\n\nHer kan du tilføje dine piller til systemet, så systemet kan genkende dem, og sende dig relevante notifikiationer.\nFor at starte, skal du anbringe pilleæsken indenfor den røde kasse, og dernæst trykke på pillen du ønsker at gemme. \nHerefter tryk på 'Start', og lad systemet arbejde. ", "information");
            informationSeen = true;
        }

        //Åbner hjælp knappen via en boolean.
        if (uielement.button(bttnHelpX, bttnHelpX+bttnHelpStartW, bttnHelpY, bttnHelpY+bttnHelpStartH)) {
            informationSeen = false;
        }

        //Start-knap, til at starte analysering af pixel.
        if (uielement.button(bttnStartX, bttnStartX+bttnHelpStartW, bttnStartY, bttnStartY+bttnHelpStartH) && pillPixelX != 0 && pillPixelY != 0) { //Efter en pixel / pille (farve) er blevet valgt, begynd at læse farven. Kan kun blive trykket på, hvis der er blevet valgt en pille / pixel at analysere.
            
            //Sørger for at alle variablerne er reset til standard, så at værdier fra en tidligere analyse, ikke bære over til en ny.
            String pillName = null;
            String pillColor = null;

            //Opdateres pillColorRanges' således at værdierne kan sammenlignes, og sættes ind i variablerne.
            loadPixels(); 
            pillColorRangeMin = pixels[pillPixelY*width+pillPixelX];
            pillColorRangeMax = pixels[pillPixelY*width+pillPixelX];

            //Vi tager 1000 billeder af den angivne pixel, og checker dens farve. 
            for (int i = 0; i < framesToTake; ++i) {
                
                delay(200);
                cam.read();
                image(cam, -100, 240); //Den nye frame bliver tegnet.
                loadPixels(); //Pixels'array'en opdateres, således at pixels'ne kan blive læst.
                
                //If-statement der spørger om den nuværende værdi i pillColorRangeMin er mindre end den læste på skærmen.
                if (pixels[pillPixelY*width+pillPixelX] < pillColorRangeMin) {
                    pillColorRangeMin = pixels[pillPixelY*width+pillPixelX];
                }                    

                //If-statement der spørger om den nuværende værdi i pillColorRangeMax er større end den læste på skærmen.
                if (pixels[pillPixelY*width+pillPixelX] > pillColorRangeMax) {
                    pillColorRangeMax = pixels[pillPixelY*width+pillPixelX];
                }

                loadPixels();
                println(i + ": current color: " + pixels[pillPixelY*width+pillPixelX]);
            
            }

            println("min range: " + pillColorRangeMin);
            println("max range: " + pillColorRangeMax);

            //Nu hvor pillen er blevet registreret, og givet en farverækkevidde, spørger vi brugeren om at given pillen et navn.
            String nameInput = showInputDialog("Færdig! Giv Pillen et navn:\n\nHøjeste Værdi: " + pillColorRangeMax + "\nLaveste Værdi: " + pillColorRangeMin + "\n\nTryk OK for at fortsætte, tryk Cancel for at afbryde");
            
            //Checker om brugeren har givet pillen et navn, eller afbrudt processen. Hvis nameInput ikke fik et navn / blev afbrudt vil nameInput være lig med null.
            if (nameInput == null) { //Ikke noget input eller afbrudt.
                
                uielement.informationDialog("Afbrudt, pillen blev ikke gemt.");
            
            } else { //Brugeren har intastet et navn.
                
                pillName = nameInput; //Navnet bliver angivet i en variabel.
                String colorInput = showInputDialog("Angiv pillen's farve:"); //Spørger efter farven på pillen.
                
                //Hvis ingen farve er opgivet, får den bare en "ukendt"-label.
                if (colorInput == null) {
                    //Brugeren har ikke angivet en farve.
                    pillColor = "ukendt";

                } else { //Pillens farve bliver puttet ind i en variabel.
                    //Brugeren har angivet en farve.
                    pillColor = colorInput;
                
                }

                xmlHandler.save(pillName, pillColor, pillColorRangeMin, pillColorRangeMax);
                pillPixelX = 0;
                pillPixelY = 0;
            
            }

        }

        //Vælg-pixel-boks.
        if (uielement.button(uielement.scanAreaX, uielement.scanAreaX+uielement.scanAreaW, uielement.scanAreaY, uielement.scanAreaY+uielement.scanAreaH)) { //Tryk på en pille, så systemet ved hvilken pixel der skal identificeres.
        
            pillPixelX = mouseX;
            pillPixelY = mouseY;

            loadPixels();
            c = pixels[mouseY*width+mouseX];

            println(hex(c, 6));

        }    
    
    }

    public void drawUI() {

        //Gør baggrunden grå.
        background(backgroundC);
        fill(200);
        rectMode(CORNER);
        rect(0, 0, camW, height);
        rect(camW, 0, seperatorW, height);
        //Kamera-området.
        uielement.drawCameraArea();
        //Knapper.
        stroke(0);
        
        fill(0xff1d60fe);
        rect(bttnStartX, bttnStartY, bttnHelpStartW, bttnHelpStartH);
        rect(bttnHelpX, bttnHelpY, bttnHelpStartW, bttnHelpStartH);
        fill(c);
        rect(pillColorFieldX, pillColorFieldY, pillColorFieldW, pillColorFieldH);

        textAlign(CENTER, CENTER);
        textSize(textSize);
        fill(225);
        text("Start", bttnStartX, bttnStartY, bttnStartX+bttnHelpStartW, bttnStartY+bttnHelpStartH-6);       
        text("Hjælp", bttnHelpX, bttnHelpY, bttnHelpX-15, bttnHelpY+bttnHelpStartH-6);
        fill(0);
        text("Farven på valgte pille", pillColorFieldX, pillColorFieldY, pillColorFieldX+pillColorFieldW, pillColorFieldY-10);

        line(camW+seperatorW, 0, camW+seperatorW, height);
        //Liste af piller.
        fill(188);
        rect(pillTextX, pillTextY, pillTextW, pillTextH);
        fill(162);
        rect(pillTextX, pillTextY+pillTextH+seperatorW, pillTextW, boxSplitterW);
        fill(0);
        textAlign(CENTER, CENTER);
        textSize(textSize);
        text("Gemte piller:", pillTextX, pillTextY, pillTextX-pillTextW-57, pillTextY+pillTextH-10);

        for (int i = 0; i < xmlHandler.children.length-1; ++i) {
            xmlHandler.load(i+1);
            xmlPillName = xmlHandler.outputName;
            xmlPillName = xmlPillName.substring(0,1).toUpperCase() + xmlPillName.substring(1).toLowerCase();
            averageColor = (xmlHandler.outputMinRange+xmlHandler.outputMaxRange)/2;
            
            int r=(averageColor>>16)&255;
            int g=(averageColor>>8)&255;
            int b=averageColor&255; 

            //println(hex(xmlHandler.outputMinRange, 6) + " " + hex(xmlHandler.outputMaxRange, 6));

            fill(188);
            rect(pillTextX, pillTextY+seperatorW+boxSplitterW+(i+1)*(pillTextH+seperatorW), pillTextW, pillTextH);
            fill(0);
            textAlign(LEFT, CENTER);
            textSize(textSize);
            text(xmlPillName, pillTextX+seperatorW, pillTextY+seperatorW+boxSplitterW+(i+1)*(pillTextH+seperatorW)+textSize/2+6);

            if(drawColorBoxes) {
                fill(r, g, b);
                rect(pillTextColorBoxX, pillTextY+seperatorW+boxSplitterW+(i+1)*(pillTextH+seperatorW), pillTextColorBoxW, pillTextH);
            }

        }

        rectMode(CENTER);
        noStroke();

    }


}
class Scanner {

    Scanner() {

    }

    public void start() {
        drawUI();
    }



    public void drawUI() {
        uielement.drawCameraArea();
    }

}
class UIElements {
    
    //Kamera
    int camX = 0;
    int camY = 240;
    int camW = 540;    
    int camH = 360;
    int scanAreaSeperationX = 50;
    int scanAreaSeperationY = 100;
    int scanAreaX = camX + scanAreaSeperationX;
    int scanAreaY = camY + scanAreaSeperationY;
    int scanAreaW = camW - 2*scanAreaSeperationX;    
    int scanAreaH = camH - 2*scanAreaSeperationY;

    //Constructor
    UIElements() {

    }

    public void drawCameraArea() {

        //Læser en frame fra kameraet.
        if (cam.available() == true) {
            cam.read();
        }

        //Tegner kameraets syn.
        image(cam, camW - 639, 240);
        
        //Line ser sådan ud: line(x1, y1, x2, y2);
        stroke(255, 0, 0); //Giver boksen en rød farve.
        //Vertikale linjer.
        line(scanAreaX, scanAreaY, scanAreaX+scanAreaW, scanAreaY);
        line(scanAreaX, scanAreaY+scanAreaH, scanAreaX+scanAreaW, scanAreaY+scanAreaH);
        //Horisontale linjer.
        line(scanAreaX, scanAreaY, scanAreaX, scanAreaY+scanAreaH);
        line(scanAreaX+scanAreaW, scanAreaY, scanAreaX+scanAreaW, scanAreaY+scanAreaH);
        noStroke();

    }

    public boolean button(int minX, int maxX, int minY, int maxY) { //Funkktion der opfører sig som en knap, ved at registrere om musen bliver klikket indenfor en given region.

        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY && mousePressed == true) {
            mousePressed = false;
            return true;
        } else {
            return false;
        }

    }

    public int optionDialog(String paneName, String paneMessage, Object opt1, Object opt2, Object opt3) {
        Object[] options = {opt1, opt2, opt3};

        //showOptionDialog laver en dialog box der viser 3 muligheder, disse 3 muligheder er blevet angivet i options-array'et.
        //showOptionDialog retunere en værdi mellem 0-2, alt efter hvilken mulighed der blev valgt
        return showOptionDialog(
            frame, paneMessage, paneName,
            YES_NO_CANCEL_OPTION,
            QUESTION_MESSAGE,
            null, options, options[2]
        );

    }

    //Information boks med kun en besked.
    public void informationDialog(String paneMessage) {
        showMessageDialog(frame, paneMessage);
    }

    public void informationDialog(String paneName, String paneMessage, String messageType) {
        int[] messageTypes = {PLAIN_MESSAGE, INFORMATION_MESSAGE, ERROR_MESSAGE, WARNING_MESSAGE};
        int arrayEntry = 0;

        //Switch case til at håndtere hvilket dialog-type der skal vises.
        switch(messageType.toLowerCase()) {
            case "plain":
                arrayEntry = 0;
                break;

            case "information":
                arrayEntry = 1;
                break;

            case "error":
                arrayEntry = 2;
                break;

            case "warning":
                arrayEntry = 3;
                break;     

            default:
                arrayEntry = 0;
                break;                           
        }
        
        showMessageDialog(
            frame, paneMessage, paneName,
            messageTypes[arrayEntry]
        );

    }

}
class XMLHandler { 

    XML xml;
    XML[] children;
    String outputName = "";
    int outputMinRange = 0;
    int outputMaxRange = 0;

    XMLHandler() {
        xml = loadXML("data/pills.xml");
        children = xml.getChildren("pill");
    }

    //Void loader XML-filens variabler ind i nogle variabler, med et tal angivet som hvilken række der skal skannes.
    public void load(int columnToRead) {
        //Farverækkevider fra XML-filen, bliver hentet og lagt ind i et array, der kan tilgås af scanneren.
        xml = loadXML("data/pills.xml");
        children = xml.getChildren("pill");

        outputName = children[columnToRead].getContent();
        outputMinRange = children[columnToRead].getInt("minRange");
        outputMaxRange = children[columnToRead].getInt("maxRange");
    }

    //Pillens egenskaber vil blive gemt her.
    public void save(String pillName, String pillColor, int minRange, int maxRange) {
        //Checker om pillen optræder i XML-filen med checkForPill funktionen.
        checkForPill(pillName.toLowerCase(), pillColor.toLowerCase(), minRange, maxRange);
    }

    //Både pille navnet og pillefarven bliver checket, hvis nu at en pille kan komme i forskellige farver.
    public void checkForPill(String pillName, String pillColor, int minRange, int maxRange) {
        xml = loadXML("data/pills.xml");
        children = xml.getChildren("pill");

        int conflictingXMLrow = -1;
        boolean pillFound = false;

        //Kigger XML-filen igennem for navne / farve-kombinationer.
        for (int i = 0; i < children.length-1; ++i) {
            
            String xmlEntryName = children[i+1].getContent().toLowerCase();
            String xmlEntryColor = children[i+1].getString("color").toLowerCase();

            //Checker pillens navn og farve mod pillerne i XML-filen.
            if (pillName.equals(xmlEntryName) && pillColor.equals(xmlEntryColor)) {
                
                conflictingXMLrow = i+1;
                pillFound = true;

            }

        }

        if(pillFound) {
            //Der er blevet fundet en pille med samme navn og farve-kombo.
            switch(uielement.optionDialog("Pille eksistere allerede!", "Pillen eksistere allerede.\nSkal den eksisterende pille erstattes, eller vil du give din pille et nyt navn?\n", "Erstat", "Nyt Navn", "Afbryd")) {
                
                //Brugeren vil overskrive pillen.
                case 0:

                    children[conflictingXMLrow].setInt("minRange", minRange);
                    children[conflictingXMLrow].setInt("maxRange", maxRange);
                    children[conflictingXMLrow].setString("color", pillColor);
                    children[conflictingXMLrow].setContent(pillName);

                    saveXML(xml, "data/pills.xml");

                    break;

                //Brugeren vil give pillen et nyt navn / farve.
                case 1:

                    String nameInput = showInputDialog("Giv pillen et nyt navn:");
                    String colorInput = showInputDialog("Angiv pillens farve:");

                    if (colorInput == null) {
                        //Brugeren har ikke angivet en farve.
                        save(nameInput, "ukendt", minRange, maxRange);

                    } else { 
                        //Brugeren har angivet en farve.
                        save(nameInput, colorInput, minRange, maxRange);
                    
                    }

                    break;

                //Brugeren afbryder.
                default:

                    break;

            }
        } else {
            //Pillen eksistere ikke, og skrives ind i XML filen.
            XML newChild = xml.addChild("pill");
            newChild.setInt("minRange", minRange);
            newChild.setInt("maxRange", maxRange);
            newChild.setString("color", pillColor);
            newChild.setContent(pillName);    

            saveXML(xml, "data/pills.xml");
            uielement.informationDialog("Pille gemt", "Success!!\nDin pille blev gemt.", "information");
        }

    }

}
  public void settings() {  size(800, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
