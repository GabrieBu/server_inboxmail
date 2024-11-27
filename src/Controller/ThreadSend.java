package Controller;

import View.Logger;

import java.net.Socket;

public class ThreadSend implements Runnable {
    private Socket socket;
    private Logger logger;

    public ThreadSend(Logger logger, Socket socket) {
        this.socket = socket;
        this.logger = logger;
    }

    public void run() {
        logger.logMessage("Running!");
    }
}
