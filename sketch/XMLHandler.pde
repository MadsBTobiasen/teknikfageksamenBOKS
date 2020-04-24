class XMLHandler { 

    XML xml;
    XML[] children;
    String outputName = "";
    int outputMinRange = 0;
    int outputMaxRange = 0;

    XMLHandler() {
        xml = loadXML("data/pills.xml");
        children = xml.getChildren("pill");
    }

    void load(int columnToRead) {
        //Farverækkevider fra XML-filen, bliver hentet og lagt ind i et array, der kan tilgås af scanneren.
        xml = loadXML("data/pills.xml");
        children = xml.getChildren("pill");

        outputName = children[columnToRead].getContent();
        outputMinRange = children[columnToRead].getInt("minRange");
        outputMaxRange = children[columnToRead].getInt("maxRange");
    }

    //Pillens egenskaber vil blive gemt her.
    void save(String pillName, String pillColor, int minRange, int maxRange) {
        //Checker om pillen optræder i XML-filen med checkForPill funktionen.
        checkForPill(pillName, pillColor, minRange, maxRange);
    }

    //Både pille navnet og pillefarven bliver checket, hvis nu at en pille kan komme i forskellige farver.
    void checkForPill(String pillName, String pillColor, int minRange, int maxRange) {
        xml = loadXML("data/pills.xml");
        children = xml.getChildren("pill");

        int conflictingXMLrow = -1;
        boolean pillFound = false;

        //Kigger XML-filen igennem for navne / farve-kombinationer.
        for (int i = 0; i < children.length-1; ++i) {
            
            String xmlEntryName = children[i+1].getContent().toLowerCase();
            String xmlEntryColor = children[i+1].getString("color").toLowerCase();

            //Checker pillens navn og farve mod pillerne i XML-filen.
            if (pillName.equals(xmlEntryName) && pillColor.equals(xmlEntryColor)) {
                
                conflictingXMLrow = i+1;
                pillFound = true;

            }

        }

        if(pillFound) {
            //Der er blevet fundet en pille med samme navn og farve-kombo.
            switch(uielement.optionDialog("Pille eksistere allerede!", "Pillen eksistere allerede.\nSkal den eksisterende pille erstattes, eller vil du give din pille et nyt navn?\n", "Erstat", "Nyt Navn", "Afbryd")) {
                
                //Brugeren vil overskrive pillen.
                case 0:

                    children[conflictingXMLrow].setInt("minRange", minRange);
                    children[conflictingXMLrow].setInt("maxRange", maxRange);
                    children[conflictingXMLrow].setString("color", pillColor);
                    children[conflictingXMLrow].setContent(pillName);

                    saveXML(xml, "data/pills.xml");

                    break;

                //Brugeren vil give pillen et nyt navn / farve.
                case 1:

                    String nameInput = showInputDialog("Giv pillen et nyt navn:");
                    String colorInput = showInputDialog("Angiv pillens farve:");

                    if (colorInput == null) {
                        //Brugeren har ikke angivet en farve.
                        save(nameInput, "ukendt", minRange, maxRange);

                    } else { 
                        //Brugeren har angivet en farve.
                        save(nameInput, colorInput, minRange, maxRange);
                    
                    }

                    break;

                //Brugeren afbryder.
                default:

                    break;

            }
        } else {
            //Pillen eksistere ikke, og skrives ind i XML filen.
            XML newChild = xml.addChild("pill");
            newChild.setInt("minRange", minRange);
            newChild.setInt("maxRange", maxRange);
            newChild.setString("color", pillColor);
            newChild.setContent(pillName);    

            saveXML(xml, "data/pills.xml");
            uielement.informationDialog("Pille gemt", "Success!!\nDin pille blev gemt.", "information");
        }

    }

}