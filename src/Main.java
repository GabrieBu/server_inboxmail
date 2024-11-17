import View.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = new Logger();
        logger.printSuccess("message", "a@b.com");
        logger.printError("message", "a@b.com");
        logger.logMessage("log_message");
        // done
    }
}