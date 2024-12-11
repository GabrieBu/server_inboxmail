package Controller;

import Model.Server;
import View.Logger;


public class RunnableForward implements Runnable {
    private final Logger logger;
    private final Server server;
    private final String clientReqString;

    public RunnableForward(Logger logger,String clientReqString, Server server) {
        this.logger = logger;
        this.server = server;
        this.clientReqString = clientReqString;
    }
    public void run() {
        logger.logMessage("Forward!");
    }
}
