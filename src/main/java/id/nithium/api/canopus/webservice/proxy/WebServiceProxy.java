package id.nithium.api.canopus.webservice.proxy;

import com.velocitypowered.api.proxy.ProxyServer;
import id.nithium.api.canopus.webservice.ServerManager;
import id.nithium.api.canopus.webservice.event.UserMoveEntries;
import lombok.Getter;

@Getter
public class WebServiceProxy {

    @Getter
    private static WebServiceProxy instance;
    private final ProxyServer proxyServer;


    private ServerManager serverManager;
    private Orchestrator orchestrator;
    private UserMoveEntries userMoveEntries;

    public WebServiceProxy(ProxyServer proxyServer) {
        instance = this;

        this.proxyServer = proxyServer;
        userMoveEntries = new UserMoveEntries();
    }
}
