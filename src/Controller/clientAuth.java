package Controller;

import View.Logger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

    private String pack(String emailAddress){
        //organizza che deve mandare
        //packa
        //manda in base all'unpack
        return "";
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
                PrintWriter writer = new PrintWriter(incoming.getOutputStream(), true); // true for auto-flushing
                if(authenticated) {
                    writer.println("authenticated");
                    logger.logSuccess("Authenticated [" + typedMail + "]");
                } else {
                    writer.println("not_authenticated");
                    logger.logError("Not authenticated [" + typedMail + "]");
                }
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
