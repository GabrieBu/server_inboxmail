package Controller;

import Model.Server;
import View.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RunnableSend implements Runnable {
    private Logger logger;
    private final Server server;
    String clientReqString;

    public RunnableSend(Logger logger,String clientReqString, Server server) {
        this.logger = logger;
        this.server = server;
        this.clientReqString=clientReqString;
    }

    public void run() {
        try {
            logger.logMessage(clientReqString);
            JsonObject jsonObjectReq = JsonParser.parseString(clientReqString).getAsJsonObject();
            JsonObject mail = jsonObjectReq.get("mail").getAsJsonObject();
            String toSend = mail.get("to").getAsString();
            toSend=toSend.replaceAll("[\"]", "");
            String[] allMails = toSend.split(",");

            for (String email : allMails) {
                int clientPort = server.getPort(email);
                if (clientPort != -1) { // check if the port exists for this email
                    Socket clientSocket = new Socket("localhost", clientPort);
                    sendFile(jsonObjectReq, email, clientSocket);
                    clientSocket.close();
                    logger.logSuccess("Mail sent to " + email + " correctly on port " + clientPort);
                } else {
                    logger.logSuccess("Client is not online. DB overwritten!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(JsonObject emailToBeSent, String email, Socket socket) {
        try {
            logger.logMessage("Sending email: " + emailToBeSent);
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true); // Auto-flushing enabled
            writer.println(email);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error while sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
