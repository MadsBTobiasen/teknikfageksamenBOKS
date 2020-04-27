class Scanner {

    int minX = scanAreaW/4*time.getCurrentTimeSlotInt()+scanAreaX+1;
    int maxX = scanAreaW/4*(time.getCurrentTimeSlotInt()+1)+scanAreaX-2;
    int x = minX;

    int bttnBoxColor = #1d60fe;
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

    void start() {
        drawUI();
        scanFrame();
    }

    //Funktion til at scanne webkameraets input.
    void scanFrame() {

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
            if (y >= maxY && x >= maxX) {

                x = minX;
                y = minY;
                
                timeslotBools[time.getCurrentTimeSlotInt()] = true;

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
                //Pille er blevet genkendet.
                xmlHandler.load(i+1);
                if(pixels[y*width+x] > xmlHandler.outputMinRange && pixels[y*width+x] < xmlHandler.outputMaxRange) {
                    uielement.informationDialog("pille ramt: " + pillTimeSlot(x));
                    timeslotBools[time.getCurrentTimeSlotInt()] = false;
                    x = minX;
                    y = minY;
                }
            }

            //Tegner en boks om den pixel der bliver scannet.
            rectMode(RADIUS);
            noFill();
            stroke(scannerColor);
            rect(x, y, 10, 10);
            
        }

    }

    void drawUI() {

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
    int pillTimeSlot(int xx) {

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