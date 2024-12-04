package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.io.*;
import java.util.Map;

public class Server{
    //private static int currentIdMail; //to_check static
    private static Map<String, Integer> clientsInfo = new HashMap<>();

    public Server(){
        //currentIdMail = Integer.parseInt(readConfig());
    }

    public int getPort(String emailAddress){
        return clientsInfo.get(emailAddress);
    }

    public void putPort(String emailAddress, int port){
        clientsInfo.put(emailAddress, port);
    }
}
