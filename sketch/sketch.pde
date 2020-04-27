import gab.opencv.*;
import processing.video.*;
import static javax.swing.JOptionPane.*;

PFont font;
Capture cam;
UIElements uielement;
XMLHandler xmlHandler;
Time time;
//
Scanner scanner;
//
PillAdder pillAdder;
int r = 0;
int g = 0;
int b = 0;
color c = #b4b4b4;
int currentScene = 1; 

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
    int backgroundC = #b4b4b4;

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

void settings() {

    size(sW, sH);

}

void setup() {

    font = createFont("Arial", 32);
    textFont(font);

    cam = new Capture(this);
    cam.start();

    uielement = new UIElements();
    xmlHandler = new XMLHandler();
    time = new Time();

    //
    scanner = new Scanner();
    //
    pillAdder = new PillAdder(); 

}

void draw() {

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

void mousePressed() {
    println(mouseX + " " + mouseY);
}
