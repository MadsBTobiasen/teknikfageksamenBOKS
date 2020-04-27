class Time {
    
    String[] stringTimeSlots = {"Nat", "Morgen", "Middag", "Aften"};
    int[] intTimeSlots = {7, 12, 18, 23};
    //Constructor
    Time () {

    }

    String time(int entriesToReturn) {
        Object[] timeStuffs = {hour(), minute(), second()};
        String outputString = "";

        for (int i = 0; i < entriesToReturn; ++i) {
            if(hour() < 10) timeStuffs[0] = nf(hour(), 2);
            if(minute() < 10) timeStuffs[1] = nf(minute(), 2);
            if(second() < 10) timeStuffs[2] = nf(second(), 2);

            outputString += timeStuffs[i];
            if(i+1 != entriesToReturn ) {
                outputString += ":";
            }
        }

        return outputString;
    }

    int getCurrentTimeSlotInt() {
        int output = 0;

        if (intTimeSlots[0] > hour()) output = 0; //Return nat.
        else if (intTimeSlots[1] > hour()) output = 1; //Return morgen.
        else if (intTimeSlots[2] > hour()) output = 2; //Return middag.
        else if (intTimeSlots[3] >= hour()) output = 3; //Return aften.

        return output;

    }
    
    String getCurrentTimeSlotString() {

        return stringTimeSlots[getCurrentTimeSlotInt()];

    }

    void timeSlotColor(int currentItteration) {
        if(currentItteration == getCurrentTimeSlotInt() && scanner.timeslotBools[currentItteration]) fill(#00ff00);
        else if(currentItteration == getCurrentTimeSlotInt()) fill(#ffff00);
        else if(currentItteration < getCurrentTimeSlotInt()) fill(#00ff00);
        else fill(#ff0000);
    }

}