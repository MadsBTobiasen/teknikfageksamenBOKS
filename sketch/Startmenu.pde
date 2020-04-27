class Startmenu {

    int titlePixelsFromEdgeX = 125;
    int titlePixelsFromEdgeY = 35;
    int titleH = 6*titlePixelsFromEdgeY;
    int titleW = sW - 2*titlePixelsFromEdgeX;
    int titleX = titlePixelsFromEdgeX;
    int titleY = titlePixelsFromEdgeY;

    int optionsW = 175;
    int optionsH = 50;
    int optionsSeperationFromTitle = 100;
    int optionsSeperation = 15;
    int optionsX = sW/2;
    int optionsY = titleY+titleH+optionsSeperationFromTitle;
    color optionsC = 100;

    int bttnBoxColor = #1d60fe;
    int textBttnColor = color(225);

    //Constructor
    Startmenu() {

    }

    void start() {
        drawUI();
        menuHandler();
    }

    //Tegner UI til startmenuen.
    void drawUI() {

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
    void menuHandler() {
        int xformedX = optionsX - optionsW/2;
        int xformedY = optionsY - optionsH/2;

        if (uielement.button(xformedX, xformedX+optionsW, xformedY+(optionsSeperation+optionsH)*0, xformedY+optionsH+(optionsSeperation+optionsH)*0, bttnBoxColor, textBttnColor, textSize, "Start", RADIUS)) currentScene=1;
        if (uielement.button(xformedX, xformedX+optionsW, xformedY+(optionsSeperation+optionsH)*1, xformedY+optionsH+(optionsSeperation+optionsH)*1, bttnBoxColor, textBttnColor, textSize, "Opsætning", RADIUS)) currentScene=3;
        if (uielement.button(xformedX, xformedX+optionsW, xformedY+(optionsSeperation+optionsH)*2, xformedY+optionsH+(optionsSeperation+optionsH)*2, bttnBoxColor, textBttnColor, textSize, "Luk EPBox", RADIUS)) exit();

    }
    
}