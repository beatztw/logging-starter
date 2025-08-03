package ru.chugunov.loggingstarter;

public class LoggingStarterAutoConfiguration {

    public static void println(String input){
        System.out.println("Выведено из logging стартера gradle: " + input);
    }
}
