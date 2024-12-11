package Model;

import java.util.HashMap;
import java.util.Map;

public class Server{
    private static Map<String, Integer> clientsInfo;

    public Server(){
        clientsInfo = new HashMap<>();
    }

    public HashMap<String, Integer> getClientsInfo(){
        return (HashMap<String, Integer>) clientsInfo;
    }

    public boolean hasKey(String key){
        return clientsInfo.get(key) != null;
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
