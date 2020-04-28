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

    boolean informationSeen = false;

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

        if (!informationSeen) {
            uielement.informationDialog("Velkommen til Opsætning.\n\nHer i opsætning kan du indstille de funktioner der skal til at systemet kan finde dine piller.\n\nIP-Adresse:\nFor at du kan modtage notifikationer på EPBox til Patienter, skal du indtaste IP-adressen, som man finder i menuen på EPBox til Patienter.\n\nPill-Adder:\nI Pill-Adder kan du tilføje din piller til systemet, så at de kan blive genkendt af systemet, og give dig relevante notifikationer.");
            informationSeen = true;
        }

        //Laver en tilbage knap, der går tilbage til menuen
        uielement.infoHelpBttn("Her i opsætning kan du indstille de funktioner der skal til at systemet kan finde dine piller.\n\nIP-Adresse:\nFor at du kan modtage notifikationer på EPBox til Patienter, skal du indtaste IP-adressen, som man finder i menuen på EPBox til Patienter.\nPill-Adder:\nI Pill-Adder kan du tilføje din piller til systemet, så at de kan blive genkendt af systemet, og give dig relevante notifikationer.");
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
                xmlHandler.writeIP("ip=" + newIP);
                unitAddress = new NetAddress(newIP,port);
            }
        }

        image(settings, optionsX+optionsW-ipChangeBoxW+imagePush/2, optionsY-optionsH/2+imagePush/2, ipChangeBoxW-imagePush, optionsH-imagePush);

        //Knap til at tilgå Pill-adder.
        if (uielement.button(xformedX, xformedX+optionsW, xformedY+(optionsSeperation+optionsH)*1, xformedY+optionsH+(optionsSeperation+optionsH)*1, bttnBoxColor, textBttnColor, textSize, "Tilføj en pille", RADIUS)) currentScene=3;

    }
    
}