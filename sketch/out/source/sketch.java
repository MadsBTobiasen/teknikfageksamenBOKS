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

    c = (0);
}

public void draw() {

    if (currentScene == 0) {
        String text = showInputDialog("yeetaaaaaaaaaaa\naaaaaaaaaaaaaaaaaaaaaayeetaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaayeetaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        println(text);
        exit();
    }
    
    if (currentScene == 1) {

    }

    if (currentScene == 2) {

    }

    if (currentScene == 3) {

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
            
            String pillName = null;
            String pillColor = null;

            //Opdateres pillColorRanges' således at værdierne kan sammenlignes, og sættes ind i variablerne.
            loadPixels(); 
            pillColorRangeMin = pixels[pillPixelY*width+pillPixelX];
            pillColorRangeMax = pixels[pillPixelY*width+pillPixelX];

            //Vi tager 1000 billeder af den angivne pixel, og checker dens farve. 
            for (int i = 0; i < 100; ++i) {
                
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
            String nameInput = showInputDialog("Færdig!\nGiv Pillen et navn:\n\nHøjeste Værdi: " + pillColorRangeMax + "\nLaveste Værdi: " + pillColorRangeMin + "\n\nTryk OK for at fortsætte, tryk Cancel for at afbryde");
            
            //Checker om brugeren har givet pillen et navn, eller afbrudt processen. Hvis nameInput ikke fik et navn / blev afbrudt vil nameInput være lig med null.
            if (nameInput == null) { //Ikke noget input eller afbrudt.
                
                println("NOT SAVED!");
            
            } else { //Navn input, og gemmes.
                
                pillName = nameInput;
                String colorInput = showInputDialog("Angiv pillen's farve:");
                
                if (colorInput == null) {
                    pillColor = "ukendt";
                } else {
                    pillColor = colorInput;
                }
            
            }

            println(pillName + "\n" + pillColor);

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
