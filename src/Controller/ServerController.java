package Controller;

import Model.Server;
import View.Logger;

public class ServerController {
    private final Server server;
    private final Logger logger;

    public ServerController(Server server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    public Server getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    //methods
}
