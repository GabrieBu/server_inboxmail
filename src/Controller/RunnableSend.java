package Controller;

import Model.Server;
import View.Logger;

import java.net.Socket;

public class RunnableSend implements Runnable {
    private Socket socket;
    private Logger logger;
    private Server server;

    public RunnableSend(Logger logger, Socket socket, Server server) {
        this.socket = socket;
        this.logger = logger;
        this.server = server;
    }

    public void run() {
        logger.logMessage("Starting sending email!");
        //como cazzo lo mando al client ---> cioè client come ascolta sempre?!
    }
}
