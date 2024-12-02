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
    private Socket socket;
    private Logger logger;
    private Server server;
    String clientReqString;

    public RunnableSend(Logger logger,String clientReqString, Socket socket, Server server) {
        this.socket = socket;
        this.logger = logger;
        this.server = server;
        this.clientReqString=clientReqString;
    }

    public void run() {

        try {
            logger.logMessage(clientReqString);
            socket.close();
            JsonObject jsonObjectReq = JsonParser.parseString(clientReqString).getAsJsonObject();
            System.out.println(jsonObjectReq.toString());
            JsonObject mail = jsonObjectReq.get("mail").getAsJsonObject();
            String ToSend = mail.get("to").getAsString();
            ToSend=ToSend.replaceAll("[\"]", "");
            String[] AllMails = ToSend.split(",");
            sendFile(jsonObjectReq,AllMails);
            logger.logSuccess("Mail sended to " + ToSend + " correctly");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(JsonObject reply, String[] AllMails) {
        try {
            Socket socket = new Socket("localhost", 8190);
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true); // true for auto-flushing
            writer.write(reply.toString());

            System.out.println(reply.toString());
            for (String mail : AllMails) {
                System.out.println(mail);
                writer.write(reply.toString());
                System.out.println("File successfully overwritten.");

            }
            System.out.println("File successfully overwritten.");
            socket.close();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }


}
