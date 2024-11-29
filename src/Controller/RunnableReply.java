package Controller;

import Model.Server;
import View.Logger;

import java.net.Socket;

public class RunnableReply implements Runnable {
    private Socket socket;
    private Logger logger;
    private Server server;

    public RunnableReply(Logger logger, Socket socket, Server server) {
        this.socket = socket;
        this.logger = logger;
        this.server = server;
    }

    public void run() {
        logger.logMessage("Running!");
    }
}
