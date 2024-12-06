package Controller;

import Model.Server;
import View.Logger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RunnableHandshakeDisconnect implements Runnable{
    private final Logger logger;
    private final String clientReqString;
    private final Server server;
    private final String typeReqString;

    public RunnableHandshakeDisconnect(Logger logger, String clientReqString, Server server, String typeReqString) {
        this.logger = logger;
        this.clientReqString = clientReqString;
        this.server = server;
        this.typeReqString = typeReqString;
    }

    private String unpackMail(String jsonAuth){
        JsonObject jsonObject = JsonParser.parseString(jsonAuth).getAsJsonObject();
        return jsonObject.get("typed_mail_user").getAsString();
    }

    private int unpackPort(String jsonAuth){
        JsonObject jsonObject = JsonParser.parseString(jsonAuth).getAsJsonObject();
        return Integer.parseInt(jsonObject.get("port").getAsString());
    }

    public void run() {
        String typedMail = unpackMail(this.clientReqString);
        int clientPort = unpackPort(this.clientReqString);

        if(this.typeReqString.equals("handshake"))
            this.server.putPort(typedMail, clientPort);
        else if(this.typeReqString.equals("disconnect"))
            this.server.deletePort(typedMail);
    }
}
