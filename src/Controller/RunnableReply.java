package Controller;

import Model.Server;
import View.Logger;

import java.net.Socket;

public class RunnableReply implements Runnable {
    private final Logger logger;

    public RunnableReply(Logger logger) {
        this.logger = logger;
    }

    public void run() {
        logger.logMessage("Running Reply!");
    }
}
