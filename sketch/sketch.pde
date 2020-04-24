import processing.video.*;
import static javax.swing.JOptionPane.*;

PFont font;
Capture cam;
UIElements uielement;
XMLHandler xmlHandler;
PillAdder pillAdder;
int r = 0;
int g = 0;
int b = 0;
color c = #b4b4b4;
int currentScene = 0; 
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

void setup() {
    size(800, 600);

    font = createFont("Arial", 32);
    textFont(font);

    cam = new Capture(this);
    cam.start();

    uielement = new UIElements();
    xmlHandler = new XMLHandler();
    pillAdder = new PillAdder(); 

}

void draw() {

    //Startmenu
    if (currentScene == 0) {

    }
    
    //Scanner
    if (currentScene == 1) {

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