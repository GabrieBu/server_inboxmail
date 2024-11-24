package Controller;

import View.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


/*
 * multiple connection works, probably handled sequentially
 *  ----> ISSUE: is two clients at the same time wanna connect to the server, server won't be able
 *  to handle them not sequentially
 */

public class clientAuth implements Runnable{
    ServerSocket serverSocket;
    Logger logger;
    public clientAuth(Logger logger, int port) throws IOException {
        this.logger = logger;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.logError("Unable to open socket on port " + port + "[" + e.getMessage() + "]");
            throw new IOException();
        }
    }

    private String unpack(String jsonAuth, Socket incoming){
        JsonObject jsonObject = JsonParser.parseString(jsonAuth).getAsJsonObject();
        return jsonObject.get("typed_mail_user").getAsString();
    }

    private void sendData(PrintWriter writer, String userMail) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("src/Storage/inboxes.txt"))) {

            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = fileReader.readLine()) != null) {
                fileContent.append(line);
            }

            JsonObject jsonObject = JsonParser.parseString(fileContent.toString()).getAsJsonObject();
            JsonObject response = new JsonObject();

            if (jsonObject.has(userMail)) {
                JsonArray inbox = jsonObject.getAsJsonArray(userMail);
                response.addProperty("authentication", true);
                response.addProperty("inbox", inbox.toString());
                logger.logSuccess("Client authenticated [" + userMail + "]");
            } else {
                logger.logError("Not authenticated [" + userMail + "]");
                response.addProperty("authentication", false);
            }
            logger.logMessage(response.toString());
            writer.println(response);
        } catch (IOException e) {
            throw new RuntimeException("Error reading inbox file: " + e.getMessage());
        }
    }

    public void run() {
        try {
            Socket incoming = serverSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            String clientReqString = reader.readLine();
            String typedMail = unpack(clientReqString, incoming);

            String line;
            try (BufferedReader file_reader = new BufferedReader(new FileReader("src/Storage/users.txt"))) {
                boolean authenticated = false;
                while(!authenticated && (line = file_reader.readLine()) != null) {
                    if(line.equalsIgnoreCase(typedMail)) {
                        authenticated = true;
                    }
                }
                PrintWriter writer = new PrintWriter(incoming.getOutputStream(), true);
                sendData(writer, typedMail);// true for auto-flushing
            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
            incoming.close();
        } catch (IOException e) {
            logger.logError("Error trying to connect with client");
            throw new RuntimeException();
        }
    }
}
