package Controller;

import Model.Server;
import View.Logger;

import java.net.Socket;

public class RunnableDelete implements Runnable{
    private Socket socket;
    private Logger logger;
    private Server server;

    public RunnableDelete(Logger logger, Socket socket, Server server) {
        this.socket = socket;
        this.logger = logger;
    }

    public void run() {
        logger.logMessage("Running!");
    }
}
