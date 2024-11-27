package Controller;

import Model.Server;
import View.Logger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkController {
    private final Server server;
    private final Logger logger;
    private final int port = 8189;
    private final int poolSize = 4;

    public NetworkController(Server server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    private String unpack(String jsonReq){
        JsonObject jsonObject = JsonParser.parseString(jsonReq).getAsJsonObject();
        return jsonObject.get("type").getAsString();
    }

    public void startServer() throws IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) { //check how to stop server
                logger.logMessage("Server listening on port " + port);
                Socket sock = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String clientReqString = reader.readLine();
                String typeRequestString = unpack(clientReqString);
                if(typeRequestString.equals("authentication")){
                    threadPool.execute(new ThreadAuth(logger, clientReqString, sock));
                }
                else if(typeRequestString.equals("send")){
                    threadPool.execute(new ThreadSend(logger, sock));
                }
                else if (typeRequestString.equals("delete")) {
                    //threadPool.execute(new clientAuth(logger, sock));
                }
                else{
                    //threadPool.execute(new clientAuth(logger, sock));
                }
            }
        }
        catch (IOException e) {
            logger.logError("Server is not listening. Error occured: " + e.getMessage());
        }
        finally {
            logger.logMessage("Server is going to be stopped!");
            threadPool.shutdown();
        }
    }
}


