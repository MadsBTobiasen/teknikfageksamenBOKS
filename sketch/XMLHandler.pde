class XMLHandler { 

    XML xml;
    XML[] children;

    XMLHandler() {
        xml = loadXML("data/pills.xml");
    }
    
    //Pillens egenskaber vil blive gemt her.
    void save(String pillName, String pillColor, int minRange, int maxRange) {
        //Checker om pillen optræder i XML-filen med checkForPill funktionen.
        checkForPill(pillName, pillColor, minRange, maxRange);
    }
    
    //Både pille navnet og pillefarven bliver checket, hvis nu at en pille kan komme i forskellige farver.
    void checkForPill(String pillName, String pillColor, int minRange, int maxRange) {
        
        int conflictingXMLrow = -1;
        boolean pillFound = false;
        xml = loadXML("data/pills.xml");
        children = xml.getChildren("pill");

        //Kigger XML-filen igennem for navne / farve-kombinationer.
        for (int i = 0; i < children.length; ++i) {
            
            String xmlEntryName = children[i].getContent().toLowerCase();
            String xmlEntryColor = children[i].getString("color").toLowerCase();

            //Checker pillens navn og farve mod pillerne i XML-filen.
            if (pillName.equals(xmlEntryName) && pillColor.equals(xmlEntryColor)) {
                
                conflictingXMLrow = i;
                pillFound = true;

            }

        }


        if(pillFound) {
            //Der er blevet fundet en pille med samme navn og farve-kombo.
            switch(uielement.optionBox("Pille eksistere allerede!", "boxInfo", "Overskriv", "Nyt Navn", "Afbryd")) {
                
                //Brugeren vil overskrive pillen.
                case '0':

                    children[conflictingXMLrow].setInt("minRange", minRange);
                    children[conflictingXMLrow].setInt("maxRange", maxRange);
                    children[conflictingXMLrow].setString("color", pillColor);
                    children[conflictingXMLrow].setContent(pillName);

                    saveXML(xml, "data/pills.xml");

                    break;

                //Brugeren vil give pillen et nyt navn / farve.
                case '1':

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
        }

    }

}