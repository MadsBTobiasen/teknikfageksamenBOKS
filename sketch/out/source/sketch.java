import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import gab.opencv.*; 
import processing.video.*; 
import static javax.swing.JOptionPane.*; 
import oscP5.*; 
import netP5.*; 

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
Time time;
Startmenu startmenu;
Scanner scanner;
Setup setup;
PillAdder pillAdder;
int r = 0;
int g = 0;
int b = 0;
int c = 0xffb4b4b4;
int currentScene = 0; 

OscP5 oscP5;
OscMessage msg;
NetAddress unitAddress; 
String ip = "192.168.10.100";
int port = 59867;

// VARIABLER TIL GUI START.
    //Vindue.
    int sW = 800;
    int sH = 600;

    //Kamera.
    int camX = 0;
    int camY = 240;
    int camW = 540;    
    int camH = 360;

    //Scanningsområde.
    int scanAreaSeperationX = 135-1;
    int scanAreaSeperationY = 100-1;
    int scanAreaX = camX + scanAreaSeperationX;
    int scanAreaY = camY + scanAreaSeperationY;
    int scanAreaW = camW - 2*scanAreaSeperationX;    
    int scanAreaH = camH - 2*scanAreaSeperationY;
    
    //Misc.
    int seperatorW = 5;
    int backgroundC = 0xffb4b4b4;

    //Knapper.
    int bttnWidth = (camW-seperatorW*2)/2;    
    int bttnHeight = (camY-3*seperatorW)/2;
    int bttnLeftX = seperatorW;
    int bttnLeftY = seperatorW;
    int bttnRightX = seperatorW*2+bttnWidth;
    int bttnRightY = seperatorW;

    //Farvefelt i Pilladder.
    int longbarFieldX = seperatorW;
    int longbarFieldY = 2*seperatorW+bttnHeight;
    int longbarFieldW = camW - seperatorW;
    int longbarFieldH = bttnHeight;

    //Liste ved siden af kameraet i Scanneren og Pill-Adder.
    int listTextX = camW+seperatorW*2;
    int listTextY = seperatorW;
    int listColorBoxW = 50; 
    int listColorBoxX = sW-seperatorW-listColorBoxW;
    int listTextW = sW-listTextX-seperatorW;
    int listTextH = 50;
    int listSplitterW = 10;

    //Text size.    
    int textSize = 28;
    
// VARIABLER TIL GUI SLUT.
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

public void settings() {

    size(sW, sH);

}

public void setup() {

    font = createFont("Arial", 32);
    textFont(font);

    oscP5 = new OscP5(this, port);
    unitAddress = new NetAddress(ip,port);
    cam = new Capture(this);
    cam.start();

    uielement = new UIElements();
    xmlHandler = new XMLHandler();
    time = new Time();

    startmenu = new Startmenu();
    scanner = new Scanner();
    setup = new Setup();
    pillAdder = new PillAdder(); 

}

public void draw() {
    
    background(backgroundC);
    
    //Startmenu
    if (currentScene == 0) {
        startmenu.start();
    }
    
    //Scanner
    if (currentScene == 1) {

        scanner.start();

    }

    //Opsætning
    if (currentScene == 2) {
        
        setup.start();

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

    int bttnBoxColor = 0xff1d60fe;
    int textBttnColor = color(225);

    boolean informationSeen = false;
    boolean drawColorBoxes = false;

    String xmlPillName = "";

    //Constructor
    PillAdder() {

    }
    
    public void start() {
        drawUI();
        pillAdder();    
    }

    public void drawUI() {

         //Tegner baggrunden.
        uielement.backgroundForScannerAndPillAdder();   

        //Laver en tilbage knap, der går tilbage til menuen
        uielement.returnBttn();

        //Kamera-området.
        uielement.drawCameraArea();
        
        stroke(0);
        textAlign(CENTER, CENTER);
        textSize(textSize);
        
        fill(c);
        rect(longbarFieldX, longbarFieldY, longbarFieldW, longbarFieldH);
        fill(0);
        text("Farven på valgte pille", longbarFieldX, longbarFieldY, longbarFieldW, longbarFieldH);

        //Seperator.
        line(camW+seperatorW, 0, camW+seperatorW, height);
        //Liste af piller.
        fill(200);
        rect(listTextX, listTextY, listTextW, listTextH);
        fill(162);
        rect(listTextX, listTextY+listTextH+seperatorW, listTextW, listSplitterW);
        fill(0);
        text("Gemte piller:", listTextX, listTextY, listTextW, listTextY+listTextH-10);

        //Indhenter elementer fra XML-filen, og indskriver det i pillelisten.
        for (int i = 0; i < xmlHandler.children.length-1; ++i) {
            xmlHandler.load(i+1);
            xmlPillName = xmlHandler.outputName;
            xmlPillName = xmlPillName.substring(0,1).toUpperCase() + xmlPillName.substring(1).toLowerCase();

            /*averageColor = (xmlHandler.outputMinRange+xmlHandler.outputMaxRange)/2;
            
            int r=(averageColor>>16)&255;
            int g=(averageColor>>8)&255;
            int b=averageColor&255; 

            println(hex(xmlHandler.outputMinRange, 6) + " " + hex(xmlHandler.outputMaxRange, 6));*/

            fill(188);
            rect(listTextX, listTextY+seperatorW+listSplitterW+(i+1)*(listTextH+seperatorW), listTextW, listTextH);
            fill(0);
            textAlign(LEFT, CENTER);
            text(xmlPillName, listTextX+seperatorW, listTextY+seperatorW+listSplitterW+(i+1)*(listTextH+seperatorW)+textSize/2+6);

            if(drawColorBoxes) {
                fill(r, g, b);
                rect(listColorBoxX, listTextY+seperatorW+listSplitterW+(i+1)*(listTextH+seperatorW), listColorBoxW, listTextH);
            }

        }

        //Reset.
        rectMode(CENTER);
        noStroke();

    }

    //Kører pill-adder-systemet.
    public void pillAdder() {

        //Hvis boolean til hjælpe-boksen er true, så vises en besked.
        if (!informationSeen) {
            uielement.informationDialog("Hjælp", "Velkommen til Pill-Adder.\n\nHer kan du tilføje dine piller til systemet, så systemet kan genkende dem, og sende dig relevante notifikiationer.\nFor at starte, skal du anbringe dine piller indenfor den røde kasse, og dernæst trykke på den pille du ønsker at gemme. \nHerefter tryk på 'Start', og lad systemet arbejde. ", "information");
            informationSeen = true;
        }

        //Åbner hjælp-boksen via en boolean.
        if (uielement.button(bttnRightX, bttnRightX+bttnWidth, bttnRightY, bttnRightY+bttnHeight, bttnBoxColor, textBttnColor, textSize, "Hjælp")) {
            informationSeen = false;
        }

        //Start-knap, til at starte analysering af pixel.
        if (uielement.button(bttnLeftX, bttnLeftX+bttnWidth, bttnLeftY, bttnLeftY+bttnHeight, bttnBoxColor, textBttnColor, textSize, "Start") && pillPixelX != 0 && pillPixelY != 0) { //Efter en pixel / pille (farve) er blevet valgt, begynd at læse farven. Kan kun blive trykket på, hvis der er blevet valgt en pille / pixel at analysere.
            
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
                image(cam, camW - 639, 240); //Den nye frame bliver tegnet.
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
        if (uielement.button(scanAreaX, scanAreaX+scanAreaW, scanAreaY, scanAreaY+scanAreaH)) { //Tryk på en pille, så systemet ved hvilken pixel der skal identificeres.
        
            pillPixelX = mouseX;
            pillPixelY = mouseY;

            loadPixels();
            c = pixels[mouseY*width+mouseX];

            println(hex(c, 6));

        }

    }


}
class Scanner {

    int minX = scanAreaW/4*time.getCurrentTimeSlotInt()+scanAreaX+1;
    int maxX = scanAreaW/4*(time.getCurrentTimeSlotInt()+1)+scanAreaX-2;
    int x = minX;

    int bttnBoxColor = 0xff1d60fe;
    int textBttnColor = color(225);

    int minY = scanAreaY+1;
    int maxY = minY + scanAreaH-2;
    int y = minY;

    int scannerColor = color(0, 255, 0);
    int timeBetweenScan = 5100; //Int der angiver hvor lang tid der skal gå mellem hver komplette scanning.
    int scanTimer;
    int statusColor = color(255, 0, 0); 
    String stringScannerStatus = "Start | Scanner Status: Inaktiv";
    boolean scannerInactive = true;
    boolean informationSeen = false;
    boolean[] timeslotBools = {false, false, false, false};



    //Constructor
    Scanner() {

    }

    public void start() {
        drawUI();
        scanFrame();
    }

    //Funktion til at scanne webkameraets input.
    public void scanFrame() {

        //Scanner inaktiv, så scanTimeren kan blive angivet i millisekunder.
        if (scannerInactive) {
            scanTimer = millis();
        }

        //Scanner bliver sat til aktiv, når timeren har nået den værdi angivet i timeBetweenScan.
        if (!scannerInactive) {
            /*
            if (scanTimer % timeBetweenScan > timeBetweenScan-100 && millis() > 10000) {
            scanTimer = timeBetweenScan-99;
            scannerInactive = false;*/
            
            //Minx og maxX biver sat alt efter hvilken zone der skal scannes, baseret på et timeslot.
            minX = scanAreaW/4*time.getCurrentTimeSlotInt()+scanAreaX+1;
            maxX = scanAreaW/4*(time.getCurrentTimeSlotInt()+1)+scanAreaX-2;

            //Checker først om scanneren har ikke overskredet både X og Y-grænsen.
            //En pille blev
            if (y >= maxY && x >= maxX) {
                //Ingen piller blev i scanningsloopet.
                //Her resettes scannerens X og Y til standard, og vi sender en OSC-besked til enheden, at der ingen piller er.
                x = minX;
                y = minY;
                
                timeslotBools[time.getCurrentTimeSlotInt()] = true;

                //oscBesked
                msg = new OscMessage("/pill");
                msg.add(false);
                oscP5.send(msg, unitAddress);

            } else { //Ingen af koordinaterne har ramt deres max.

                if (y >= maxY) {
                    //Hvis Y-koordinaten har ramt kanten af boksen, resetter vi dens værdi, og begynde på næste række, X.
                    x += 5;
                    y = minY;
                } else {
                    //Ingen af checks'ne var succesfulde, så det antages at ingen maks-grænse er noget, og den næste Y-koordinat kan læses.
                    y += 5;
                }

            }
            
            //Pixels'ne indenfor scannings-regionen bliver tjekket imod pillerne i XML-filen.
            loadPixels();
            println("currentscan @frame: " + x + " " + y + " : currentcolor: " + pixels[y*width+x]);
            for (int i = 0; i < xmlHandler.children.length-1; ++i) {
                xmlHandler.load(i+1);
                if(pixels[y*width+x] > xmlHandler.outputMinRange && pixels[y*width+x] < xmlHandler.outputMaxRange) {
                    //Pille er blevet genkendet.
                    uielement.informationDialog("pille ramt: " + pillTimeSlot(x));
                    timeslotBools[time.getCurrentTimeSlotInt()] = false;
                    x = minX;
                    y = minY;
                    //oscBesked
                    msg = new OscMessage("/pill");
                    msg.add(true);
                    oscP5.send(msg, unitAddress);
                }
            }

            //Tegner en boks om den pixel der bliver scannet.
            rectMode(RADIUS);
            noFill();
            stroke(scannerColor);
            rect(x, y, 10, 10);
            
        }

    }

    public void drawUI() {

        //Tegner baggrunden.
        uielement.backgroundForScannerAndPillAdder();
        
        //Laver en tilbage knap, der går tilbage til menuen
        uielement.returnBttn();

        //Kamera-området.
        uielement.drawCameraArea();       

        //Hvis boolean til hjælpe-boksen er true, så vises en besked.
        if (!informationSeen) {
            uielement.informationDialog("Hjælp", "Velkommen til EPBox-Scanner.\n\nFor at starte systemet, skal du blot trykke på start knappen, og scanneren vil begynde at lede efter gemte piller i systemet.\nDu kan også pause scanneren ved at trykke på samme knap.", "information");
            informationSeen = true;
        }
        
        //Registrerer knappetryk.
        //Åbner hjælp-boksen via en boolean.
        if (uielement.button(bttnRightX, bttnRightX+bttnWidth, bttnRightY, bttnRightY+bttnHeight, bttnBoxColor, textBttnColor, textSize, "Hjælp")) {
            informationSeen = false;
        }
        
        //Starter og pauser scannings-funktionen.
        if (uielement.button(longbarFieldX, longbarFieldX+longbarFieldW, longbarFieldY, longbarFieldY+longbarFieldH, bttnBoxColor, statusColor, textSize, stringScannerStatus)) {
            
            if (scannerInactive) {
                //Stop scanneren.
                stringScannerStatus = "Stop | Scanner Status: Aktiv";
                scannerInactive = false;
                statusColor = scannerColor;
                x = minX;
                y = minY;
            } else {
                //Start scanneren.
                stringScannerStatus = "Start | Scanner Status: Inaktiv";
                scannerInactive = true;
                statusColor = color(255, 0, 0);
            }

        }



        //Boks der viser klokken, og nuværendee tidspunkt.
        textAlign(CENTER, CENTER);
        textSize(textSize);

        fill(bttnBoxColor);
        rect(bttnLeftX, bttnLeftY, bttnWidth, bttnHeight);

        fill(textBttnColor);
        text(time.time(3) + " | " + time.getCurrentTimeSlotString(), bttnLeftX, bttnLeftY, bttnWidth, bttnLeftY+bttnHeight-6);       

        //Seperator.
        line(camW+seperatorW, 0, camW+seperatorW, height);

        //Liste til status over time-slots.
        fill(200);
        rect(listTextX, listTextY, listTextW, listTextH);
        fill(162);
        rect(listTextX, listTextY+listTextH+seperatorW, listTextW, listSplitterW);
        fill(0);
        textAlign(CENTER, CENTER);
        textSize(textSize);
        text("Status:", listTextX, listTextY, listTextX-listTextW-57, listTextY+listTextH-10);

        //Tegner kasserne der skal indgå på listen.
        for (int i = 0; i < time.stringTimeSlots.length; ++i) {

            fill(200);
            rect(listTextX, listTextY+seperatorW+listSplitterW+(i+1)*(listTextH+seperatorW), listTextW, listTextH);
            time.timeSlotColor(i);
            rect(listColorBoxX, listTextY+seperatorW+listSplitterW+(i+1)*(listTextH+seperatorW), listColorBoxW, listTextH);  

            fill(0);
            textAlign(LEFT, CENTER);
            textSize(textSize);
            text(time.stringTimeSlots[i], listTextX+seperatorW, listTextY+seperatorW+listSplitterW+(i+1)*(listTextH+seperatorW)+textSize/2+6);

        }

        //Reset.
        rectMode(CENTER);
        noStroke();

    }

    //Funktion der retunere hvilken zone pillen er i. Zonerne er; Nat, morgen, middag, aften.
    public int pillTimeSlot(int xx) {

        //Pillen bliver checket om den er indenfor den fø
        if (xx < scanAreaW/4*1+scanAreaX) {
            //Pillen er i nat-zonen.
            return 1;
        } else if (xx < scanAreaW/4*2+scanAreaX) {
            //Pillen er i morgen-zonen.
            return 2;
        } else if (xx < scanAreaW/4*3+scanAreaX) {
            //Pillen er i middag-zonen.
            return 3;
        } else if (xx < scanAreaW/4*4+scanAreaX) {
            //Pillen er i aften-zonen.
            return 4;
        } else {
            //Default værdi, hvis ingen af zonerne er blevet ramt. Dette burde ikke være muligt.
            println("PILL NOT IN ZONE!");
            return 0;
        }

    }

}
class Setup {

    PImage settings;

    int titlePixelsFromEdgeX = 125;
    int titlePixelsFromEdgeY = 35;
    int titleH = 4*titlePixelsFromEdgeY;
    int titleW = sW - 2*titlePixelsFromEdgeX;
    int titleX = titlePixelsFromEdgeX;
    int titleY = titlePixelsFromEdgeY;

    int optionsW = 225;
    int optionsH = 50;
    int optionsSeperationFromTitle = 100;
    int optionsSeperation = 15;
    int optionsX = sW/2;
    int optionsY = titleY+titleH+optionsSeperationFromTitle;
    int optionsC = 100;

    int bttnBoxColor = 0xff1d60fe;
    int textBttnColor = color(225);

    //Constructor
    Setup() {
        settings = loadImage("settings.png");
    }

    public void start() {
        drawUI();
        setup();
    }

    public void drawUI() {

        stroke(0);
        textSize(titleH/3);
        textAlign(CENTER, CENTER);
        rectMode(CORNER);
 
        noFill();
        rect(titleX, titleY, titleW, titleH);
        fill(0);
        text("Opsætning", titleX, titleY, titleW, titleH-15);

    }

    public void setup() {

        //Laver en tilbage knap, der går tilbage til menuen
        uielement.returnBttn();
        
        //
        int xformedX = optionsX - optionsW/2;
        int xformedY = optionsY - optionsH/2;
        int ipChangeBoxW = 50;
        int imagePush = 10;

        //Knap der sætter IP-addresse på enheden.
        rectMode(RADIUS);
        textSize(textSize);
        textAlign(LEFT, CENTER);

        fill(200);
        rect(optionsX, optionsY, optionsW, optionsH/2);
        fill(0);
        text("Enheds IP: " + ip, optionsX+10, optionsY, optionsW, optionsH/2-6);

        if (uielement.button(optionsX+optionsW-ipChangeBoxW, optionsX+optionsW-ipChangeBoxW+ipChangeBoxW, optionsY-optionsH/2, optionsY-optionsH/2+ipChangeBoxW, 200, 0, textSize, "")) {
            String newIP = showInputDialog("Indtast en ny IP-Adresse: "); 
            if (newIP != null) {
                ip = newIP;
                unitAddress = new NetAddress(newIP,port);
            }
        }

        image(settings, optionsX+optionsW-ipChangeBoxW+imagePush/2, optionsY-optionsH/2+imagePush/2, ipChangeBoxW-imagePush, optionsH-imagePush);

        //Knap til at tilgå Pill-adder.
        if (uielement.button(xformedX, xformedX+optionsW, xformedY+(optionsSeperation+optionsH)*1, xformedY+optionsH+(optionsSeperation+optionsH)*1, bttnBoxColor, textBttnColor, textSize, "Tilføj en pille", RADIUS)) currentScene=3;

    }
    
}
class Startmenu {

    int titlePixelsFromEdgeX = 125;
    int titlePixelsFromEdgeY = 35;
    int titleH = 6*titlePixelsFromEdgeY;
    int titleW = sW - 2*titlePixelsFromEdgeX;
    int titleX = titlePixelsFromEdgeX;
    int titleY = titlePixelsFromEdgeY;

    int optionsW = 200;
    int optionsH = 50;
    int optionsSeperationFromTitle = 100;
    int optionsSeperation = 15;
    int optionsX = sW/2;
    int optionsY = titleY+titleH+optionsSeperationFromTitle;
    int optionsC = 100;

    int bttnBoxColor = 0xff1d60fe;
    int textBttnColor = color(225);

    //Constructor
    Startmenu() {

    }

    public void start() {
        drawUI();
        menuHandler();
    }

    //Tegner UI til startmenuen.
    public void drawUI() {

        stroke(0);
        textSize(titleH/3);
        textAlign(CENTER, CENTER);
        rectMode(CORNER);
 
        noFill();
        rect(titleX, titleY, titleW, titleH);
        fill(0);
        text("EPBox", titleX, titleY, titleW, titleH-15);

    }

    //En menuHandler, til at styre hvilken scene der skal skiftes til, når brugeren klikker på en.
    public void menuHandler() {
        int xformedX = optionsX - optionsW/2;
        int xformedY = optionsY - optionsH/2;

        if (uielement.button(xformedX, xformedX+optionsW, xformedY+(optionsSeperation+optionsH)*0, xformedY+optionsH+(optionsSeperation+optionsH)*0, bttnBoxColor, textBttnColor, textSize, "Start", RADIUS)) currentScene=1;
        if (uielement.button(xformedX, xformedX+optionsW, xformedY+(optionsSeperation+optionsH)*1, xformedY+optionsH+(optionsSeperation+optionsH)*1, bttnBoxColor, textBttnColor, textSize, "Opsætning", RADIUS)) currentScene=2;
        if (uielement.button(xformedX, xformedX+optionsW, xformedY+(optionsSeperation+optionsH)*2, xformedY+optionsH+(optionsSeperation+optionsH)*2, bttnBoxColor, textBttnColor, textSize, "Luk EPBox", RADIUS)) exit();

    }
    
}
class Time {
    
    String[] stringTimeSlots = {"Nat", "Morgen", "Middag", "Aften"};
    int[] intTimeSlots = {7, 12, 18, 23};
    //Constructor
    Time () {

    }

    public String time(int entriesToReturn) {
        Object[] timeStuffs = {hour(), minute(), second()};
        String outputString = "";

        for (int i = 0; i < entriesToReturn; ++i) {
            if(hour() < 10) timeStuffs[0] = nf(hour(), 2);
            if(minute() < 10) timeStuffs[1] = nf(minute(), 2);
            if(second() < 10) timeStuffs[2] = nf(second(), 2);

            outputString += timeStuffs[i];
            if(i+1 != entriesToReturn ) {
                outputString += ":";
            }
        }

        return outputString;
    }

    public int getCurrentTimeSlotInt() {
        int output = 0;

        if (intTimeSlots[0] > hour()) output = 0; //Return nat.
        else if (intTimeSlots[1] > hour()) output = 1; //Return morgen.
        else if (intTimeSlots[2] > hour()) output = 2; //Return middag.
        else if (intTimeSlots[3] >= hour()) output = 3; //Return aften.

        return output;

    }
    
    public String getCurrentTimeSlotString() {

        return stringTimeSlots[getCurrentTimeSlotInt()];

    }

    public void timeSlotColor(int currentItteration) {
        if(currentItteration == getCurrentTimeSlotInt() && scanner.timeslotBools[currentItteration]) fill(0xff00ff00);
        else if(currentItteration == getCurrentTimeSlotInt()) fill(0xffffff00);
        else if(currentItteration < getCurrentTimeSlotInt()) fill(0xff00ff00);
        else fill(0xffff0000);
    }

}
class UIElements {

    int returnBttnW = (sW - camW)/2;
    int returnBttnH = 50;
    int returnBttnX = camW + returnBttnW;
    int returnBttnY = sH - returnBttnH;

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
        //Tidspunkt linjer.
        line(scanAreaW/4*1+scanAreaX, scanAreaY, scanAreaW/4*1+scanAreaX, scanAreaY+scanAreaH);
        line(scanAreaW/4*2+scanAreaX, scanAreaY, scanAreaW/4*2+scanAreaX, scanAreaY+scanAreaH);
        line(scanAreaW/4*3+scanAreaX, scanAreaY, scanAreaW/4*3+scanAreaX, scanAreaY+scanAreaH);
        //Tekst der indikirer hvilken boks, svarer til hvilket tidspunkt.
        textSize(16);
        fill(255, 0, 0);
        textAlign(CENTER, CENTER);
        text("Nat", scanAreaX, scanAreaY, scanAreaW/4*1, -50);
        text("Morgen", scanAreaX+scanAreaW/4*1, scanAreaY, scanAreaW/4*1, -50);
        text("Middag", scanAreaX+scanAreaW/4*2, scanAreaY, scanAreaW/4*1, -50);
        text("Aften", scanAreaX+scanAreaW/4*3, scanAreaY, scanAreaW/4*1, -50);
        
        noStroke();

    }
    
    //Gør baggrunden grå.
    public void backgroundForScannerAndPillAdder() {
        noStroke();
        fill(200);
        rectMode(CORNER);
        rect(0, 0, camW, height);
        rect(camW, 0, seperatorW, height);
    }

    //Sender brugeren tilbage til start-menuen, når "Tilbage" bliver trykket på.
    public void returnBttn() {

        if(button(returnBttnX, returnBttnX+returnBttnW, returnBttnY, returnBttnY+returnBttnH, 200, 0, textSize, "Tilbage")) {
            currentScene = 0;
        }

    }
    
    //Knap-funktion, hvor der ikke bliver tegnet en boks, men kun registrere museklik indenfor de angivede parametre.
    public boolean button(int minX, int maxX, int minY, int maxY) {
        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY && mousePressed == true) {
            mousePressed = false;
            return true;
        } else {
            return false;
        }
    }

    //Knap-funktion, der tegner og registrere museklik indenfor de angivede parametre.
    public boolean button(int minX, int maxX, int minY, int maxY, int rectColor, int textColor, int bTextSize, String textString) { //Funkktion der opfører sig som en knap, ved at registrere om musen bliver klikket indenfor en given region.

        textSize(bTextSize);
        textAlign(CENTER, CENTER);
        rectMode(CORNER);
        stroke(0);

        fill(rectColor);
        rect(minX, minY, dist(minX, 0, maxX, 0), dist(0, minY, 0, maxY));
        fill(textColor);
        text(textString, minX, minY, dist(minX, 0, maxX, 0), dist(0, minY, 0, maxY));

        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY && mousePressed == true) {
            mousePressed = false;
            return true;
        } else {
            return false;
        }

    }

    //Knap-funktion, der tegner og registrere museklik indenfor de angivede parametre. Tagers også en rectMode, hvis der er brug for det.
    public boolean button(int minX, int maxX, int minY, int maxY, int rectColor, int textColor, int bTextSize, String textString, int rectMode) { //Funkktion der opfører sig som en knap, ved at registrere om musen bliver klikket indenfor en given region.

        boolean output = false;

        textSize(bTextSize);
        textAlign(CENTER, CENTER);
        stroke(0);

        if (rectMode == RADIUS) {

            rectMode(rectMode);

            fill(rectColor);
            rect(minX+dist(minX, 0, maxX, 0)/2, minY+dist(minY, 0, maxY, 0)/2, dist(minX, 0, maxX, 0), dist(minY, 0, maxY, 0)/2);
            fill(textColor);
            text(textString, minX+dist(minX, 0, maxX, 0)/2, minY+dist(minY, 0, maxY, 0)/2, dist(minX, 0, maxX, 0), dist(minY, 0, maxY, 0)/2);

            if (mouseX > minX-dist(minX, 0, maxX, 0)/2 && mouseX < maxX*2 && mouseY > minY && mouseY < maxY && mousePressed == true) {
                mousePressed = false;
                output = true;
            }
        
        }

        return output;

    }    

    //Mulighed boks der returnere en værdi, tilsvarende af den valgte mulighed af brugeren.
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

    //Information boks, med navn, besked og beskedtype.
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
