package hu.lae.util;

import java.time.LocalDate;

/**
 * Clock class for being able to test date dependent classes. 
 * By default returns the current date, but can be set to return a static date too. 
 *
 */
public class Clock {

    private Clock(){}
    
    private static LocalDate staticDate; 
    
    public static void setStaticDate(LocalDate date) {
        staticDate = date;
    }
    
    public static LocalDate date() {
        if(staticDate == null) {
            return LocalDate.now();
        } else {
            return staticDate;
        }
    }
    
}