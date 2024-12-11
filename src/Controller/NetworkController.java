package Controller;

import Model.Server;
import View.Logger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkController {
    private final Server server;
    private final Logger logger;
    private final int SERVER_PORT = 8189;
    private final int POOL_SIZE = 16;

    public NetworkController(Server server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    private String unpack(String jsonReq){
        JsonObject jsonObject = JsonParser.parseString(jsonReq).getAsJsonObject();
        return jsonObject.get("type").getAsString();
    }

    public void startServer(){
        ExecutorService threadPool = Executors.newFixedThreadPool(POOL_SIZE);
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            while(true) {
                logger.logMessage("Server listening on port " + SERVER_PORT);
                Socket sock = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String clientReqString = reader.readLine();
                String typeRequestString = unpack(clientReqString);
                sock.close();

                switch (typeRequestString) {
                    case "authentication":
                        threadPool.execute(new RunnableAuth(logger, clientReqString, server));
                        break;
                    case "send":
                        threadPool.execute(new RunnableSend(logger, clientReqString, server));
                        break;
                    case "delete":
                        threadPool.execute(new RunnableDelete(logger, clientReqString));
                        break;
                    case "reply":
                    case "reply_all":
                        threadPool.execute(new RunnableReply(logger, clientReqString, server, typeRequestString));
                        break;
                    case "forward":
                        threadPool.execute(new RunnableForward(logger, clientReqString, server));
                    case "handshake":
                        threadPool.execute(new RunnableHandshakeDisconnect(logger, clientReqString, server, typeRequestString));
                        break;
                    case "disconnect":
                        threadPool.execute(new RunnableHandshakeDisconnect(logger, clientReqString, server, typeRequestString));
                    default:
                        logger.logError("Unrecognized type " + typeRequestString);
                }
            }
        }
        catch (Exception e) {
            logger.logError("Server is not listening. Error occured: " + e.getMessage());
            //non so che fare qui
        }
        finally {
            logger.logMessage("Server is going to be stopped!");
            threadPool.shutdown();
        }
    }
}


