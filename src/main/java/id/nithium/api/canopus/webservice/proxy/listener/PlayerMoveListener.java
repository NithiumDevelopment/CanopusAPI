package id.nithium.api.canopus.webservice.proxy.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import id.nithium.api.canopus.webservice.Server;
import id.nithium.api.canopus.webservice.model.UserMover;
import id.nithium.api.canopus.webservice.proxy.WebServiceProxy;

import java.net.InetSocketAddress;
import java.util.Optional;

public class PlayerMoveListener {

    private final WebServiceProxy webServiceProxy = WebServiceProxy.getInstance();

    @Subscribe
    public void onKicked(KickedFromServerEvent event) {
        Player player = event.getPlayer();
        RegisteredServer registeredServer = event.getServer();

        Server server = webServiceProxy.getServerManager().getServer(registeredServer.getServerInfo().getName());
        assert server != null;

        UserMover userMover = new UserMover();
        userMover.setUuid(player.getUniqueId());
        userMover.setFrom(server.getName());
        userMover.setTo(null);

        UserMover userMover1 = webServiceProxy.getUserMoveEntries().getEntry(player.getUniqueId());
        if (userMover1 != null) webServiceProxy.getUserMoveEntries().getEntries().remove(userMover1);

        webServiceProxy.getUserMoveEntries().getEntries().add(userMover);
        webServiceProxy.getUserMoveEntries().update();
    }

    @Subscribe
    public void onJoin(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();
        UserMover userMover = webServiceProxy.getUserMoveEntries().getEntry(player.getUniqueId());

        assert userMover != null;

        Server server = webServiceProxy.getServerManager().getServer(userMover.getFrom());

        assert server != null;

        Optional<RegisteredServer> registeredServerOptional = webServiceProxy.getProxyServer().getServer(server.getName());

        registeredServerOptional.ifPresentOrElse(event::setInitialServer, () -> {
            ServerInfo serverInfo = new ServerInfo(server.getName(), new InetSocketAddress("0.0.0.0", server.getPort()));
            webServiceProxy.getProxyServer().createRawRegisteredServer(serverInfo);
        });
    }

    @Subscribe
    public void onChange(ServerConnectedEvent event) {
        Player player = event.getPlayer();

        Optional<RegisteredServer> fromOptional = event.getPreviousServer();
        RegisteredServer to = event.getServer();

        assert fromOptional.isPresent() && to != null;

        RegisteredServer from = fromOptional.get();

        UserMover userMover = new UserMover();
        userMover.setUuid(player.getUniqueId());
        userMover.setTo(to.getServerInfo().getName());
        userMover.setFrom(from.getServerInfo().getName());

        UserMover userMover1 = webServiceProxy.getUserMoveEntries().getEntry(player.getUniqueId());
        if (userMover1 != null) webServiceProxy.getUserMoveEntries().getEntries().remove(userMover1);

        webServiceProxy.getUserMoveEntries().getEntries().add(userMover);
        webServiceProxy.getUserMoveEntries().update();
    }
}
