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
            response.addProperty("type", "response_auth");
            if (checkEmailInFileNames(userMail)) {
                String filePathName = "src/Storage/inboxes/" + userMail + ".txt";
                try {
                    String fileContent = Files.readString(Paths.get(filePathName));
                    JsonObject jsonObject = JsonParser.parseString(fileContent).getAsJsonObject();
                    response.addProperty("authenticated", true);
                    JsonArray inbox = jsonObject.getAsJsonArray("inbox");
                    response.addProperty("inbox", inbox.toString());
                    server.putPort(userMail, clientPort);  // storing the email-port mapping
                    logger.logSuccess("User [" + userMail + "] " + "authenticated");
                } catch (IOException e) {
                    throw new RuntimeException("Error reading inbox file: " + e.getMessage());
                }
            } else {
                response.addProperty("authenticated", false);
            }
            writer.println(response);
            clientSocket.close(); //forse va messo in un finally
        }
        catch( IOException e){
            logger.logError("Can't open the client socket!" + e.getMessage());
        }
    }

    private static boolean checkEmailInFileNames(String email) {
        File directory = new File("src/Storage/inboxes/");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) {
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
        return fileName.substring(0, lastDotIndex);  // remove the extension
    }

    public void run() {
        String typedMail = unpackMail(this.clientReqString);
        int clientPort = unpackPort(this.clientReqString);
        sendData(clientPort, typedMail);
    }
}
