import Controller.ServerController;
import Model.clientAuth;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ServerController serverController = new ServerController();
        try {
            ScheduledExecutorService exec = Executors.newScheduledThreadPool(4);
            exec.scheduleAtFixedRate(new clientAuth(), 0, 5, TimeUnit.SECONDS);
            System.out.println("Continuo con la mia esecuzione");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}