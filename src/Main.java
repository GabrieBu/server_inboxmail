import Controller.NetworkController;
import Controller.ServerController;
import Model.Server;
import View.Logger;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        Logger logger = new Logger();
        ServerController serverController = new ServerController(server, logger);
        NetworkController networkController = new NetworkController(server, logger);

        try{
            networkController.startServer();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}