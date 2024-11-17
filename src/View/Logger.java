package View;

public class Logger {
    public static final String RESET = "\u001B[0m"; //default color
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public void printError(String message, String address_mail) {
        System.out.println(RED + "Error: " + message + " [" + address_mail + "]" + RESET);
    }

    public void printSuccess(String message, String address_mail) {
        System.out.println(GREEN + "Error: " + message + " [" + address_mail + "]" +  RESET);
    }

    public void logMessage(String message) {
        System.out.println(WHITE + message + RESET);
    }
}
