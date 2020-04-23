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




Capture cam;
UIElements uielement;
XMLHandler xmlHandler;
PillAdder pillAdder;
int c;
int currentScene = 3; 
/* 

currentScene er en variable der kontrollere hvilken menu der bliver vist. 
0 = Startmenu.
1 = Program kører og status indikatører bliver vist.
2 = Opsætningsmenu, hvor brugeren indstiller IP-adresse på enheden.
3 = "Pill-Adder", altså den menu hvor brugeren kan tilføje nye piller til systemet.

*/

int pillPixelX = 0;
int pillPixelY = 0;
int pillColorRangeMin = 0;
int pillColorRangeMax = 0;

public void setup() {
    

    cam = new Capture(this);
    cam.start();

    uielement = new UIElements();
    xmlHandler = new XMLHandler();
    pillAdder = new PillAdder(); 

    c = (0);
}

public void draw() {

    if (currentScene == 0) {
        println(uielement.optionBox("name", "name", "1", "2", "3"));
    }
    
    if (currentScene == 1) {

    }

    if (currentScene == 2) {

    }

    if (currentScene == 3) {

        pillAdder.start();

    }

}

public void stop() {

}
class PillAdder {
    
    int pillPixelX = 0;
    int pillPixelY = 0;
    int pillColorRangeMin = 0;
    int pillColorRangeMax = 0;

    PillAdder() {

    }
    
    public void start() {

        if (cam.available() == true) {
            cam.read();
        }
    
        image(cam, -100, 240);

        fill(c);
        rect(0, 0, 100, 100);
        fill(0);
        rect(100, 0, 100, 100);

        //Tegner en boks om det område som pilleæsken skal ligge i.
        stroke(255, 0, 0);
        line(50, 340, 490, 340);
        line(50, 500, 490, 500);
        line(50, 340, 50, 500);
        line(490, 340, 490, 500);
        stroke(0);

        if (uielement.button(100, 200, 0, 100) && pillPixelX != 0 && pillPixelY != 0) { //Efter en pixel / pille (farve) er blevet valgt, begynd at læse farven. Kan kun blive trykket på, hvis der er blevet valgt en pille / pixel at analysere.
            
            //Sørger for at alle variablerne er reset til standard, så at værdier fra en tidligere analyse, ikke bære over til en ny.
            String pillName = null;
            String pillColor = null;

            //Opdateres pillColorRanges' således at værdierne kan sammenlignes, og sættes ind i variablerne.
            loadPixels(); 
            pillColorRangeMin = pixels[pillPixelY*width+pillPixelX];
            pillColorRangeMax = pixels[pillPixelY*width+pillPixelX];

            //Vi tager 1000 billeder af den angivne pixel, og checker dens farve. 
            for (int i = 0; i < 1; ++i) {
                
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
                
                showMessageDialog(null, "Afbrudt, pillen blev ikke gemt.");
            
            } else { //Brugeren har intastet et navn.
                
                pillName = nameInput; //Navnet bliver angivet i en variabel.
                String colorInput = showInputDialog("Angiv pillen's farve:"); //Spørger efter farven på pillen.
                
                //Hvis ingen farve er opgivet, får den bare en "ukendt"-label.
                if (colorInput == null) {

                    pillColor = "ukendt";

                } else { //Pillens farve bliver puttet ind i en variabel.

                    pillColor = colorInput;
                
                }

                xmlHandler.save(pillName, pillColor, pillColorRangeMin, pillColorRangeMax);
                pillPixelX = 0;
                pillPixelY = 0;
            
            }

        }

        if (uielement.button(50, 490, 340, 500)) { //Tryk på en pille, så systemet ved hvilken pixel der skal identificeres.
        
            pillPixelX = mouseX;
            pillPixelY = mouseY;

            loadPixels();
            c = pixels[mouseY*width+mouseX];

        }    
    
    }


}
class UIElements {
    
    //Constructor
    UIElements() {

    }

    public boolean button(int minX, int maxX, int minY, int maxY) { //Funkktion der opfører sig som en knap, ved at registrere om musen bliver klikket indenfor en given region.

        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY && mousePressed == true) {
            mousePressed = false;
            return true;
        } else {
            return false;
        }

    }


   /* boolean confirmBox(String, boxName, String boxInfo) {
        if(showConfirmDialog(null, boxName, boxInfo, YES_NO_OPTION) == 0) {
            //Boksen har modtaget et "Yes"-svar.
            return true;
        } else {
            //Boksen har modtaget et "Nej"-svar, eller boksen er blevet lukket.
            return false;
        }
    }*/

    public int optionBox(String boxName, String boxInfo, Object opt1, Object opt2, Object opt3) {
        Object[] options = {opt1, opt2, opt3};

        return showOptionDialog(
            frame, boxInfo, boxName,
            YES_NO_CANCEL_OPTION,
            QUESTION_MESSAGE,
            null, options, options[2]
        );

    }

}
class XMLHandler { 

    XML xml;
    XML[] children;

    XMLHandler() {
        xml = loadXML("data/pills.xml");
    }
    
    //Pillens egenskaber vil blive gemt her.
    public void save(String pillName, String pillColor, int minRange, int maxRange) {
        //Checker om pillen optræder i XML-filen med checkForPill funktionen.
        checkForPill(pillName, pillColor, minRange, maxRange);
    }
    
    //Både pille navnet og pillefarven bliver checket, hvis nu at en pille kan komme i forskellige farver.
    public void checkForPill(String pillName, String pillColor, int minRange, int maxRange) {
        
        int conflictingXMLrow = -1;
        boolean pillFound = false;
        xml = loadXML("data/pills.xml");
        children = xml.getChildren("pill");

        //Kigger XML-filen igennem for navne / farve-kombinationer.
        for (int i = 0; i < children.length; ++i) {
            
            String xmlEntryName = children[i].getContent().toLowerCase();
            String xmlEntryColor = children[i].getString("color").toLowerCase();

            //Checker pillens navn og farve mod pillerne i XML-filen.
            if (pillName.equals(xmlEntryName) && pillColor.equals(xmlEntryColor)) {
                
                conflictingXMLrow = i;
                pillFound = true;

            }

        }


        if(pillFound) {
            //Der er blevet fundet en pille med samme navn og farve-kombo.
            switch(uielement.optionBox("Pille eksistere allerede!", "boxInfo", "Overskriv", "Nyt Navn", "Afbryd")) {
                
                //Brugeren vil overskrive pillen.
                case '0':

                    children[conflictingXMLrow].setInt("minRange", minRange);
                    children[conflictingXMLrow].setInt("maxRange", maxRange);
                    children[conflictingXMLrow].setString("color", pillColor);
                    children[conflictingXMLrow].setContent(pillName);

                    saveXML(xml, "data/pills.xml");

                    break;

                //Brugeren vil give pillen et nyt navn / farve.
                case '1':

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
