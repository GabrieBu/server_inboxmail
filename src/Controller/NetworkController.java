package Controller;

import Model.Server;
import View.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NetworkController {
    private final Server server;
    private final Logger logger;
    private final int port = 8189;

    public NetworkController(Server server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    public void startServer(){
        try {
            ScheduledExecutorService exec = Executors.newScheduledThreadPool(4);
            exec.scheduleAtFixedRate(new clientAuth(logger, port), 0, 5, TimeUnit.SECONDS);
            logger.logMessage("Server listening on port " + port);
        }
        catch (Exception e) {
            logger.logError("Server is not listening. Error occured: " + e.getMessage());
        }
    }
}
