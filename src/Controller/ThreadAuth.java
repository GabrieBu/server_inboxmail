package Controller;

import View.Logger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.io.*;
import java.net.Socket;


/*
 * multiple connection works, probably handled sequentially
 *  ----> ISSUE: is two clients at the same time wanna connect to the server, server won't be able
 *  to handle them not sequentially
 */

public class ThreadAuth implements Runnable{
    Logger logger;
    String clientReqString;
    Socket sock;

    public ThreadAuth(Logger logger, String clientReqString, Socket socket) {
        this.logger = logger;
        this.clientReqString = clientReqString;
        this.sock = socket;
    }

    private String unpack(String jsonAuth){
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
                response.addProperty("type", "response_auth");
                response.addProperty("authenticated", true);
                response.addProperty("inbox", inbox.toString());
                logger.logSuccess("Client authenticated [" + userMail + "]");
            } else {
                logger.logError("Not authenticated [" + userMail + "]");
                response.addProperty("authentication", false);
            }
            writer.println(response);
        } catch (IOException e) {
            throw new RuntimeException("Error reading inbox file: " + e.getMessage());
        }
    }

    public void run() {
        try {
            logger.logMessage("Starting client authentication...");
            String typedMail = unpack(this.clientReqString);
            String line;
            try (BufferedReader file_reader = new BufferedReader(new FileReader("src/Storage/users.txt"))) {
                boolean authenticated = false;
                while(!authenticated && (line = file_reader.readLine()) != null) {
                    if(line.equalsIgnoreCase(typedMail)) {
                        authenticated = true;
                    }
                }
                PrintWriter writer = new PrintWriter(this.sock.getOutputStream(), true);
                sendData(writer, typedMail);// true for auto-flushing
            } catch (IOException e) {
                throw new IOException(e.getMessage());
            }
            sock.close();
        } catch (IOException e) {
            logger.logError("Error trying to connect with client");
            throw new RuntimeException();
        }
    }
}
