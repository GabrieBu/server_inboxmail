package Model;

import java.util.HashMap;
import java.util.Map;

public class Server{
    private static Map<String, Integer> clientsInfo;

    public Server(){
        clientsInfo = new HashMap<>();
    }

    public int getPort(String emailAddress){
        return clientsInfo.get(emailAddress);
    }

    public void putPort(String emailAddress, int port){
        clientsInfo.put(emailAddress, port);
    }

    public void deletePort(String emailAddress){
        clientsInfo.remove(emailAddress);
    }
}
