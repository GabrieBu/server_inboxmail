package Controller;

import Model.Server;
import View.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RunnableDelete implements Runnable{
    private Socket socket;
    private Logger logger;
    String clientReqString;
    private Server server;

    public RunnableDelete(Logger logger, String cientReqString, Socket socket, Server server) {
        this.socket = socket;
        this.logger = logger;
        this.clientReqString = cientReqString;
    }

    public void run() {
        try {
            logger.logMessage(clientReqString);
            socket.close();
            JsonObject jsonObjectReq = JsonParser.parseString(clientReqString).getAsJsonObject();
            String mailUser = jsonObjectReq.get("user").getAsString();
            String filePathName = "src/Storage/inboxes/" + mailUser + ".txt";

            String fileContent = Files.readString(Paths.get(filePathName));
            JsonObject jsonObjectFile = JsonParser.parseString(fileContent).getAsJsonObject();
            JsonArray inbox = jsonObjectFile.getAsJsonArray("inbox");
            int indexToRemove = jsonObjectReq.get("index_to_remove").getAsInt();
            inbox.remove(indexToRemove);
            rewriteFile(inbox, filePathName);
            logger.logSuccess("Email deleted from server correctly [" + mailUser + "]");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rewriteFile(JsonArray inbox, String filePath) {
        JsonObject newContentFile = new JsonObject();
        newContentFile.add("inbox", inbox);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(newContentFile.toString());
            System.out.println("File successfully overwritten.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
