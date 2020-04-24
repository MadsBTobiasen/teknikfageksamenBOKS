class PillAdder {
    
    int pillPixelX = 0;
    int pillPixelY = 0;
    int pillColorRangeMin = 0;
    int pillColorRangeMax = 0;
    int framesToTake = 100;

    boolean informationSeen = false;

    //Variabler til GUI.
    int camX = uielement.camX;
    int camY = uielement.camY;
    int camW = uielement.camW;
    int camH = uielement.camH;
    
    int seperatorW = 5;
    int backgroundC = #b4b4b4;

    int bttnHelpStartW = (camW-seperatorW*2)/2;    
    int bttnHelpStartH = (height-camH-3*seperatorW)/2;

    int bttnStartX = seperatorW;
    int bttnStartY = seperatorW;
    int bttnHelpX = seperatorW*2+bttnHelpStartW;
    int bttnHelpY = seperatorW;
    
    int pillColorFieldX = seperatorW;
    int pillColorFieldY = 2*seperatorW+bttnHelpStartH;
    int pillColorFieldW = camW - seperatorW;
    int pillColorFieldH = bttnHelpStartH;

    boolean drawColorBoxes = false;
    int pillTextX = camW+seperatorW*2;
    int pillTextY = seperatorW;
    int pillTextColorBoxW = 50; 
    int pillTextColorBoxX = width-seperatorW-pillTextColorBoxW;
    int pillTextW = width-seperatorW-pillTextX;
    int pillTextH = 50;
    int averageColor = 0;
    int textSize = 28;
    int boxSplitterW = 10;
    String xmlPillName = "";
    //Variabler til GUI slut.

    PillAdder() {

    }
    
    void start() {
        colorMode(RGB, 255, 255, 255);
        if (cam.available() == true) {
            cam.read();
        }

        drawUI();

        if (!informationSeen) {
            uielement.informationDialog("Hjælp", "Velkommen til Pill-Adder.\n\nHer kan du tilføje dine piller til systemet, så systemet kan genkende dem, og sende dig relevante notifikiationer.\nFor at starte, skal du anbringe pilleæsken indenfor den røde kasse, og dernæst trykke på pillen du ønsker at gemme. \nHerefter tryk på 'Start', og lad systemet arbejde. ", "information");
            informationSeen = true;
        }

        if (uielement.button(bttnHelpX, bttnHelpX+bttnHelpStartW, bttnHelpY, bttnHelpY+bttnHelpStartH)) {
            informationSeen = false;
        }

        if (uielement.button(bttnStartX, bttnStartX+bttnHelpStartW, bttnStartY, bttnStartY+bttnHelpStartH) && pillPixelX != 0 && pillPixelY != 0) { //Efter en pixel / pille (farve) er blevet valgt, begynd at læse farven. Kan kun blive trykket på, hvis der er blevet valgt en pille / pixel at analysere.
            
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

        if (uielement.button(uielement.scanAreaX, uielement.scanAreaX+uielement.scanAreaW, uielement.scanAreaY, uielement.scanAreaY+uielement.scanAreaH)) { //Tryk på en pille, så systemet ved hvilken pixel der skal identificeres.
        
            pillPixelX = mouseX;
            pillPixelY = mouseY;

            loadPixels();
            c = pixels[mouseY*width+mouseX];

            println(hex(c, 6));

        }    
    
    }

    void drawUI() {

        //Gør baggrunden grå.
        background(backgroundC);
        fill(200);
        rectMode(CORNER);
        rect(0, 0, camW, height);
        rect(camW, 0, seperatorW, height);
        //Kamera-området.
        uielement.drawCameraArea();
        //Knapper.
        stroke(0);
        
        fill(#1d60fe);
        rect(bttnStartX, bttnStartY, bttnHelpStartW, bttnHelpStartH);
        rect(bttnHelpX, bttnHelpY, bttnHelpStartW, bttnHelpStartH);
        fill(c);
        rect(pillColorFieldX, pillColorFieldY, pillColorFieldW, pillColorFieldH);

        textAlign(CENTER, CENTER);
        textSize(textSize);
        fill(225);
        text("Start", bttnStartX, bttnStartY, bttnStartX+bttnHelpStartW, bttnStartY+bttnHelpStartH-6);       
        text("Hjælp", bttnHelpX, bttnHelpY, bttnHelpX-15, bttnHelpY+bttnHelpStartH-6);
        fill(0);
        text("Farven på valgte pille", pillColorFieldX, pillColorFieldY, pillColorFieldX+pillColorFieldW, pillColorFieldY-10);

        line(camW+seperatorW, 0, camW+seperatorW, height);
        //Liste af piller.
        fill(188);
        rect(pillTextX, pillTextY, pillTextW, pillTextH);
        fill(162);
        rect(pillTextX, pillTextY+pillTextH+seperatorW, pillTextW, boxSplitterW);
        fill(0);
        textAlign(CENTER, CENTER);
        textSize(textSize);
        text("Gemte piller:", pillTextX, pillTextY, pillTextX-pillTextW-57, pillTextY+pillTextH-10);

        for (int i = 0; i < xmlHandler.children.length-1; ++i) {
            xmlHandler.load(i+1);
            xmlPillName = xmlHandler.outputName;
            xmlPillName = xmlPillName.substring(0,1).toUpperCase() + xmlPillName.substring(1).toLowerCase();
            averageColor = (xmlHandler.outputMinRange+xmlHandler.outputMaxRange)/2;
            
            int r=(averageColor>>16)&255;
            int g=(averageColor>>8)&255;
            int b=averageColor&255; 

            //println(hex(xmlHandler.outputMinRange, 6) + " " + hex(xmlHandler.outputMaxRange, 6));

            fill(188);
            rect(pillTextX, pillTextY+seperatorW+boxSplitterW+(i+1)*(pillTextH+seperatorW), pillTextW, pillTextH);
            fill(0);
            textAlign(LEFT, CENTER);
            textSize(textSize);
            text(xmlPillName, pillTextX+seperatorW, pillTextY+seperatorW+boxSplitterW+(i+1)*(pillTextH+seperatorW)+textSize/2+6);

            if(drawColorBoxes) {
                fill(r, g, b);
                rect(pillTextColorBoxX, pillTextY+seperatorW+boxSplitterW+(i+1)*(pillTextH+seperatorW), pillTextColorBoxW, pillTextH);
            }

        }

        rectMode(CENTER);
        noStroke();

    }


}