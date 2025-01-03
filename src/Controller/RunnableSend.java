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
import java.util.HashMap;

import java.nio.channels.*;
import java.io.RandomAccessFile;

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


    public void updateFile(String emailAddress, JsonObject emailToBeSent) {
        String filePathName = "src/Storage/inboxes/" + emailAddress + ".txt";
        RandomAccessFile file = null;
        FileChannel channel = null;
        FileLock lock = null;

        try {
            file = new RandomAccessFile(filePathName, "rw");
            channel = file.getChannel();


            lock = channel.lock();

            String fileContent = Files.readString(Paths.get(filePathName));
            JsonObject jsonObject = JsonParser.parseString(fileContent).getAsJsonObject();
            JsonArray inbox = jsonObject.getAsJsonArray("inbox");
            inbox.add(emailToBeSent);
            Files.writeString(Paths.get(filePathName), jsonObject.toString());

        } catch (IOException e) {
            throw new RuntimeException("Error reading or writing to inbox file: " + e.getMessage());
        } finally {
            try {
                if (lock != null) {
                    lock.release();
                }
                if (channel != null) {
                    channel.close();
                }
                if (file != null) {
                    file.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        try {
            JsonObject jsonObjectReq = JsonParser.parseString(clientReqString).getAsJsonObject();
            JsonObject mail = jsonObjectReq.get("mail").getAsJsonObject();
            JsonArray allMails = mail.getAsJsonArray("to");

            for (int i = 0; i < allMails.size(); i++) {
                String emailAddress = allMails.get(i).getAsString();
                if(checkEmailInFileNames(emailAddress)) {
                    if(this.server.hasKey(emailAddress)) {
                        int clientPort = server.getPort(emailAddress);
                        Socket clientSocket = new Socket("localhost", clientPort);
                        sendFile(jsonObjectReq, clientSocket);
                        clientSocket.close();
                        logger.logSuccess("Mail sent to " + emailAddress + " correctly on port " + clientPort);
                    }
                    updateFile(emailAddress, mail);
                }
                else{
                    String from = mail.get("from").getAsString();
                    if(this.server.hasKey(from)) {
                        int clientPortFrom = server.getPort(from);
                        Socket clientSocket = new Socket("localhost", clientPortFrom);
                        sendError(clientSocket, emailAddress);
                        logger.logError("Mail hasn't be sent. Address [" + emailAddress + "] does not exist");
                        clientSocket.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendError(Socket socket, String to){
        JsonObject jsonObjectError = new JsonObject();
        jsonObjectError.addProperty("type", "send_error");
        jsonObjectError.addProperty("to", to);
        try{
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true); // Auto-flushing enabled
            writer.println(jsonObjectError);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(JsonObject emailToBeSent, Socket socket) {
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
