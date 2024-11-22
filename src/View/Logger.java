package View;

public class Logger {
    public static final String RESET = "\u001B[0m"; //default color
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String WHITE = "\u001B[37m";

    public void logError(String message) {
        System.out.println(RED + message + RESET);
    }

    public void logSuccess(String message) {
        System.out.println(GREEN  + message  +  RESET);
    }

    public void logMessage(String message) {
        System.out.println(WHITE + message + RESET);
    }
}
