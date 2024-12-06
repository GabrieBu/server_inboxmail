import Controller.NetworkController;
import Model.Server;
import View.Logger;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        Logger logger = new Logger();
        NetworkController networkController = new NetworkController(server, logger);

        try{
            networkController.startServer();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}