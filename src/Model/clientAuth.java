package Model;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * multiple connection works, probably handled sequentially
 *  ----> ISSUE: is two clients at the same time wanna connect to the server, server won't be able
 *  to handle them not sequentially
 */

public class clientAuth implements Runnable{
    ServerSocket serverSocket;

    public clientAuth() {
        try {
            serverSocket = new ServerSocket(8189);
        } catch (IOException e) {
            throw new RuntimeException("Impossibile avviare il server sulla porta 8189", e);
        }
    }

    private String unpack(String jsonAuth, Socket incoming){
        JsonObject jsonObject = JsonParser.parseString(jsonAuth).getAsJsonObject();
        System.out.println(jsonObject.toString());
        return jsonObject.get("typed_mail_user").getAsString();
    }

    private String pack(String emailAddress){
        //organizza che deve mandare
        //packa
        //manda in base all'unpack
        return "";
    }

    public void run(){
        try {
            Socket incoming = serverSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            String clientReqString = reader.readLine();
            String typedMail = unpack(clientReqString, incoming);

            String line;
            try (BufferedReader file_reader = new BufferedReader(new FileReader("src/Model/users.txt"))) {
                boolean authenticated = false;
                while(!authenticated && (line = file_reader.readLine()) != null) {
                    if(line.equalsIgnoreCase(typedMail)) {
                        authenticated = true;
                    }
                }
                PrintWriter writer = new PrintWriter(incoming.getOutputStream(), true); // true for auto-flushing
                if(authenticated) {
                    writer.println("authenticated");
                    System.out.println("Conferma inviata al client.");
                } else {
                    writer.println("not_authenticated");
                    System.out.println("Errore inviato al client");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            //chiusura socket
            incoming.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
