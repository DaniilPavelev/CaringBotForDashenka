package f1.CaringBotForDashenka.service;

import java.util.Date;

public class Helper {

    public static int returnClosestTime(){
        Date date = new Date();
        int currentHour = date.getHours();
        if(currentHour<=3) return 0;
        if(currentHour<=6) return 3;
        if(currentHour<=9) return 6;
        if(currentHour<=12) return 9;
        if(currentHour<=15) return 12;
        if(currentHour<=18) return 15;
        if(currentHour<=21) return 18;
        if(currentHour<=24) return 21;

        return -1;
    }



}
