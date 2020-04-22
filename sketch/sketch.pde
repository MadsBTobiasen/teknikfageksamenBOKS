import processing.video.*;
import static javax.swing.JOptionPane.*;

Capture cam;
UIElements uielement;
PillAdder pillAdder;
color c;
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

void setup() {
    size(800, 600);

    cam = new Capture(this);
    cam.start();

    uielement = new UIElements();
    pillAdder = new PillAdder(cam); 

    c = (0);
}

void draw() {

    if (currentScene == 0) {

    }
    
    if (currentScene == 1) {

    }

    if (currentScene == 2) {

    }

    if (currentScene == 3) {

        pillAdder.start();

    }












}