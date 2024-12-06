package Controller;

import Model.Server;
import View.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RunnableSend implements Runnable {
    private final Logger logger;
    private final Server server;
    private final String clientReqString;

    public RunnableSend(Logger logger,String clientReqString, Server server) {
        this.logger = logger;
        this.server = server;
        this.clientReqString=clientReqString;
    }

    private static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);  // remove the extension
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

    // @TODO
    public void updateFile(){
        /*if (checkEmailInFileNames(userMail)) {
            String filePathName = "src/Storage/inboxes/" + userMail + ".txt";
            try {
                String fileContent = Files.readString(Paths.get(filePathName));
                JsonObject jsonObject = JsonParser.parseString(fileContent).getAsJsonObject();
                JsonArray inbox = jsonObject.getAsJsonArray("inbox");
                server.putPort(userMail, clientPort);  // storing the email-port mapping
            } catch (IOException e) {
                throw new RuntimeException("Error reading inbox file: " + e.getMessage());
            }
        }*/
    }

    public void run() {
        try {
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
                }
                updateFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(JsonObject emailToBeSent, String email, Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true); // Auto-flushing enabled
            writer.println(emailToBeSent.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
