class UIElements {
    
    //Constructor
    UIElements() {

    }

    boolean button(int minX, int maxX, int minY, int maxY) { //Funkktion der opfører sig som en knap, ved at registrere om musen bliver klikket indenfor en given region.

        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY && mousePressed == true) {
            mousePressed = false;
            return true;
        } else {
            return false;
        }

    }

    int optionBox(String boxName, String boxInfo, Object opt1, Object opt2, Object opt3) {
        Object[] options = {opt1, opt2, opt3};

        return showOptionDialog(
            frame, boxInfo, boxName,
            YES_NO_CANCEL_OPTION,
            QUESTION_MESSAGE,
            null, options, options[2]
        );

    }

}