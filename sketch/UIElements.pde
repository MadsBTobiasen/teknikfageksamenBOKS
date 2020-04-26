class UIElements {
    
    //Kamera
    int camX = 0;
    int camY = 240;
    int camW = 540;    
    int camH = 360;
    int scanAreaSeperationX = 125-1;
    int scanAreaSeperationY = 140-1;
    int scanAreaX = camX + scanAreaSeperationX;
    int scanAreaY = camY + scanAreaSeperationY;
    int scanAreaW = camW - 2*scanAreaSeperationX;    
    int scanAreaH = camH - 2*scanAreaSeperationY;

    //Constructor
    UIElements() {

    }

    void drawCameraArea() {

        //Læser en frame fra kameraet.
        if (cam.available() == true) {
            cam.read();
        }

        //Tegner kameraets syn.
        image(cam, camW - 639, 240);
        
        //Line ser sådan ud: line(x1, y1, x2, y2);
        stroke(255, 0, 0); //Giver boksen en rød farve.
        //Vertikale linjer.
        line(scanAreaX, scanAreaY, scanAreaX+scanAreaW, scanAreaY);
        line(scanAreaX, scanAreaY+scanAreaH, scanAreaX+scanAreaW, scanAreaY+scanAreaH);
        //Horisontale linjer.
        line(scanAreaX, scanAreaY, scanAreaX, scanAreaY+scanAreaH);
        line(scanAreaX+scanAreaW, scanAreaY, scanAreaX+scanAreaW, scanAreaY+scanAreaH);
        noStroke();

    }

    boolean button(int minX, int maxX, int minY, int maxY) { //Funkktion der opfører sig som en knap, ved at registrere om musen bliver klikket indenfor en given region.

        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY && mousePressed == true) {
            mousePressed = false;
            return true;
        } else {
            return false;
        }

    }

    int optionDialog(String paneName, String paneMessage, Object opt1, Object opt2, Object opt3) {
        Object[] options = {opt1, opt2, opt3};

        //showOptionDialog laver en dialog box der viser 3 muligheder, disse 3 muligheder er blevet angivet i options-array'et.
        //showOptionDialog retunere en værdi mellem 0-2, alt efter hvilken mulighed der blev valgt
        return showOptionDialog(
            frame, paneMessage, paneName,
            YES_NO_CANCEL_OPTION,
            QUESTION_MESSAGE,
            null, options, options[2]
        );

    }

    //Information boks med kun en besked.
    void informationDialog(String paneMessage) {
        showMessageDialog(frame, paneMessage);
    }

    void informationDialog(String paneName, String paneMessage, String messageType) {
        int[] messageTypes = {PLAIN_MESSAGE, INFORMATION_MESSAGE, ERROR_MESSAGE, WARNING_MESSAGE};
        int arrayEntry = 0;

        //Switch case til at håndtere hvilket dialog-type der skal vises.
        switch(messageType.toLowerCase()) {
            case "plain":
                arrayEntry = 0;
                break;

            case "information":
                arrayEntry = 1;
                break;

            case "error":
                arrayEntry = 2;
                break;

            case "warning":
                arrayEntry = 3;
                break;     

            default:
                arrayEntry = 0;
                break;                           
        }
        
        showMessageDialog(
            frame, paneMessage, paneName,
            messageTypes[arrayEntry]
        );

    }

}