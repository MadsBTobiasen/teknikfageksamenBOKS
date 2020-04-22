import processing.video.*;
import static javax.swing.JOptionPane.*;

Capture cam;
UIElements uielement;
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

    c = (0);
}

void draw() {

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
            
            } else { //Brugeren har intastet et navn.
                
                pillName = nameInput; //Navnet bliver angivet i en variabel.
                String colorInput = showInputDialog("Angiv pillen's farve:"); //Spørger efter farven på pillen.
                
                //Hvis ingen farve er opgivet, bliver får den bare en "ukendt"-label.
                if (colorInput == null) {

                    pillColor = "ukendt";

                } else { //Pillens farve bliver puttet ind i en variabel.

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