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
    color optionsC = 100;

    int bttnBoxColor = #1d60fe;
    int textBttnColor = color(225);

    //Constructor
    Setup() {
        settings = loadImage("settings.png");
    }

    void start() {
        drawUI();
        setup();
    }

    void drawUI() {

        stroke(0);
        textSize(titleH/3);
        textAlign(CENTER, CENTER);
        rectMode(CORNER);
 
        noFill();
        rect(titleX, titleY, titleW, titleH);
        fill(0);
        text("Opsætning", titleX, titleY, titleW, titleH-15);

    }

    void setup() {

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