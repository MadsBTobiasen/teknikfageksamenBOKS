class Scanner {

    int minX = uielement.scanAreaX+1;
    int maxX = minX + uielement.scanAreaW-2;
    int x = minX;

    int minY = uielement.scanAreaY+1;
    int maxY = minY + uielement.scanAreaH-2;
    int y = minY;

    int timeBetweenScan = 5100; //Int der angiver hvor lang tid der skal gå mellem hver komplette scanning.
    int scanTimer;
    boolean scannerInactive = true;

    int ccc = color(0, 255, 0);
    //Constructor
    Scanner() {

    }

    void start() {
        drawUI();
        scanFrame();
    }

    void scanFrame() {

        //Scanner inaktiv, så scanTimeren kan blive angivet i millisekunder.
        if (scannerInactive) {
            scanTimer = millis();
        }

        //Scanner bliver sat til aktiv, når timeren har nået den værdi angivet i timeBetweenScan.
        if (scanTimer % timeBetweenScan > timeBetweenScan-100) {
            scanTimer = timeBetweenScan-99;
            scannerInactive = false;

            println(maxY);


            //Checker først om scanneren har ikke overskredet både X og Y-grænsen.
            if (y >= maxY && x >= maxX) {

                x = minX;
                y = minY;

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
            
            loadPixels();
            println("currentscan @frame: " + x + " " + y + " : currentcolor: " + pixels[y*width+x]);
            for (int i = 0; i < xmlHandler.children.length-1; ++i) {
                xmlHandler.load(i+1);
                if(pixels[y*width+x] > xmlHandler.outputMinRange && pixels[y*width+x] < xmlHandler.outputMaxRange && pixels[y*width+x] != -3355444) {
                    uielement.informationDialog("oki");
                    scannerInactive = true;
                    x = minX;
                    y = minY;
                }
            }

            rectMode(RADIUS);
            noFill();
            stroke(ccc);
            rect(x, y, 10, 10);
            
        }

    }

    void drawUI() {

        uielement.drawCameraArea();

    }

}