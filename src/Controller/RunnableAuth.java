package Controller;

import Model.Server;
import View.Logger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;


/*
 * multiple connection works, probably handled sequentially
 *  ----> ISSUE: is two clients at the same time wanna connect to the server, server won't be able
 *  to handle them not sequentially
 */

public class RunnableAuth implements Runnable{
    Logger logger;
    String clientReqString;
    Server server;

    public RunnableAuth(Logger logger, String clientReqString, Server server) {
        this.logger = logger;
        this.clientReqString = clientReqString;
        this.server = server;
    }

    private String unpackMail(String jsonAuth){
        JsonObject jsonObject = JsonParser.parseString(jsonAuth).getAsJsonObject();
        return jsonObject.get("typed_mail_user").getAsString();
    }

    private int unpackPort(String jsonAuth){
        JsonObject jsonObject = JsonParser.parseString(jsonAuth).getAsJsonObject();

        return Integer.parseInt(jsonObject.get("port").getAsString());
    }

    private void sendData(int clientPort, String userMail) {
        try {
            Socket clientSocket = new Socket("localhost", clientPort);
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            JsonObject response = new JsonObject();
            logger.logSuccess(userMail);
            if (checkEmailInFileNames(userMail)) {
                String filePathName = "src/Storage/inboxes/" + userMail + ".txt";
                try {
                    String fileContent = Files.readString(Paths.get(filePathName));
                    JsonObject jsonObject = JsonParser.parseString(fileContent).getAsJsonObject();
                    response.addProperty("type", "response_auth");
                    response.addProperty("authenticated", true);
                    JsonArray inbox = jsonObject.getAsJsonArray("inbox");
                    response.addProperty("inbox", inbox.toString());
                    // Store the client's port for future communications
                    server.putPort(userMail, clientPort);  // Storing the email-port mapping

                } catch (IOException e) {
                    throw new RuntimeException("Error reading inbox file: " + e.getMessage());
                }
            } else {
                response.addProperty("type", "response_auth");
                response.addProperty("authenticated", false);
            }
            writer.println(response); //in ogni caso manda false o true
            clientSocket.close(); //forse va messo in un finally
        }
        catch( IOException e){
            logger.logError("Can't open the client socket!");
            throw new RuntimeException("Error reading inbox file: " + e.getMessage());
        }
    }

    private static boolean checkEmailInFileNames(String email) {
        File directory = new File("src/Storage/inboxes/");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("No .txt files found in the directory.");
            return false;
        }

        for (File file : files) {
            String fileNameWithoutExtension = getFileNameWithoutExtension(file.getName());
            if (fileNameWithoutExtension.equals(email)) {
                return true;
            }
        }
        return false;
    }

    private static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);  // Remove the extension
    }

    public void run() {
        String typedMail = unpackMail(this.clientReqString);
        int clientPort = unpackPort(this.clientReqString);
        logger.logMessage("ClientPort: " + clientPort);
        sendData(clientPort, typedMail);// true for auto-flushing
    }
}
