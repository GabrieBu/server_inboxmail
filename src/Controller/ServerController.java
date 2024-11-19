package Controller;

import Model.Server;
import View.Logger;

public class ServerController {
    private final Server server;
    private final Logger logger;

    public ServerController() {
        this.server = new Server();
        this.logger = new Logger();
    }

    public Server getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }
}
