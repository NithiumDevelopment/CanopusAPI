package id.nithium.api.canopus.webservice.proxy;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import id.nithium.api.canopus.webservice.model.UserMover;
import id.nithium.api.exception.NithiumException;
import id.nithium.api.canopus.webservice.Server;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Orchestrator {

    private WebServiceProxy webServiceProxy = WebServiceProxy.getInstance();
    private int minimumServer = 10;
    private int maximumPlayerSizeEachServer = 50;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

    public void automaticallyRegisterServer() {
        executorService.scheduleAtFixedRate(() -> {
            for (RegisteredServer registeredServer : webServiceProxy.getProxyServer().getAllServers()) {
                if (webServiceProxy.getServerManager().getServer(registeredServer.getServerInfo().getName()) == null) {
                    webServiceProxy.getProxyServer().unregisterServer(registeredServer.getServerInfo());
                }
            }

            for (Server server : webServiceProxy.getServerManager().getServers()) {
                if (webServiceProxy.getProxyServer().getServer(server.getName()).isEmpty()) {
                    ServerInfo serverInfo = new ServerInfo(server.getName(), new InetSocketAddress("0.0.0.0", server.getPort()));
                    webServiceProxy.getProxyServer().createRawRegisteredServer(serverInfo);
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void processCreation() {
        executorService.scheduleAtFixedRate(() -> {
            if (!isRequirementsLengthVerified()) {
                int requirementLength = webServiceProxy.getServerManager().getServers().size() - minimumServer;

                if ((webServiceProxy.getServerManager().getServers().size() + requirementLength) > minimumServer) {
                    for (int i = 0; i < requirementLength; i++) {
                        Server server = automaticallyCreateServer();
                        assert server != null;

                        InetSocketAddress address = new InetSocketAddress("0.0.0.0", server.getPort());
                        ServerInfo serverInfo = new ServerInfo(server.getName(), address);
                        webServiceProxy.getProxyServer().registerServer(serverInfo);
                    }
                } else {
                    int totalPlayers = webServiceProxy.getProxyServer().getPlayerCount();
                    int totalServers = webServiceProxy.getServerManager().getServers().size();

                    double usedServers = (double) totalPlayers / maximumPlayerSizeEachServer;
                    double emptyServers = totalServers - usedServers;

                    double requirementLength1 = usedServers - emptyServers - (usedServers - minimumServer);
                    int fixedRequirementLength = (int) Math.round(requirementLength1);
                    if (requirementLength > 0) {
                        System.out.println("Creating " + fixedRequirementLength + " server...");

                        int serverSuccessfullyCreated = 0;
                        for (int i = 0; i < fixedRequirementLength; i++) {
                            if (automaticallyCreateServer() != null) {
                                serverSuccessfullyCreated++;
                                System.out.println("Successfully created");
                            } else {
                                System.out.println("Unable to create");
                            }
                        }

                        System.out.println("Created server: " + serverSuccessfullyCreated);
                    }
                }
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    public void processDestruction() {
        executorService.scheduleAtFixedRate(() -> {
            int totalPlayers = webServiceProxy.getProxyServer().getPlayerCount();
            int totalServers = webServiceProxy.getServerManager().getServers().size();

            double usedServers = (double) totalPlayers / maximumPlayerSizeEachServer;
            double emptyServers = totalServers - usedServers;

            if (usedServers < minimumServer) {
                int amountToDelete = (int) emptyServers - minimumServer;
                int predictionTotalServers = totalServers - amountToDelete;

                if ((predictionTotalServers - usedServers) == minimumServer) {
                    Random random = new Random();
                    AtomicInteger serverSuccessfullyDeleted = new AtomicInteger();

                    for (int i = 0; i < amountToDelete; i++) {
                        int randomServer = random.nextInt(webServiceProxy.getServerManager().getServers().size());

                        Server server = webServiceProxy.getServerManager().getServers().get(randomServer);
                        webServiceProxy.getProxyServer().getServer(server.getName()).ifPresent(registeredServer -> {
                            if (registeredServer.getPlayersConnected().isEmpty()) {
                                webServiceProxy.getServerManager().deleteServer(server.getName());
                                webServiceProxy.getProxyServer().unregisterServer(registeredServer.getServerInfo());

                                System.out.println("Successfully created.");
                                serverSuccessfullyDeleted.getAndIncrement();
                            } else {
                                List<Integer> playersEachServer = new ArrayList<>();
                                for (RegisteredServer regi : webServiceProxy.getProxyServer().getAllServers()) {
                                    playersEachServer.add(regi.getPlayersConnected().size());
                                }

                                double calculate = 0;
                                for (int players : playersEachServer) {
                                    calculate = calculate + players;
                                }

                                int average = (int) calculate / playersEachServer.size();
                                int lowest = (average / 15) * 5;
                                if (lowest == average) { // 5 2
                                    lowest -= 5;
                                } else if (lowest < average){
                                    lowest += 5;
                                }

                                int tolerance = 1;
                                int abs = Math.abs(registeredServer.getPlayersConnected().size() - lowest);
                                if (abs <= tolerance) {
                                    for (Player player : registeredServer.getPlayersConnected()) {
                                        int i1 = random.nextInt(webServiceProxy.getServerManager().getServers().size());
                                        Server server1 = webServiceProxy.getServerManager().getServers().get(i1);

                                        webServiceProxy.getProxyServer().getServer(server1.getName()).ifPresent(registeredServer1 -> {
                                            UserMover userMover = new UserMover();
                                            userMover.setUuid(player.getUniqueId());
                                            userMover.setFrom(registeredServer.getServerInfo().getName());
                                            userMover.setTo(registeredServer1.getServerInfo().getName());

                                            webServiceProxy.getUserMoveEntries().getEntries().add(userMover);
                                            webServiceProxy.getUserMoveEntries().update();

                                            player.createConnectionRequest(registeredServer1).fireAndForget();
                                        });
                                    }
                                }
                            }
                        });
                    }

                    System.out.println("Deleted server: " + serverSuccessfullyDeleted);
                }
            }
        }, 0, 10, TimeUnit.MINUTES);
    }

    public boolean isRequirementsLengthVerified() {
        return (webServiceProxy.getServerManager().getServers().size() > minimumServer);
    }

    public Server automaticallyCreateServer() {
        Random random = new Random();

        int randomI = random.nextInt(9999);
        String formattedI = String.valueOf(randomI);

        if (randomI < 999) {
            formattedI = String.format("%04d", randomI);
        }

        String serverName = "s" + formattedI;
        if (serverName.length() != 5) throw new NithiumException("Unable to automatically create server because server name is not 5 length.");
        if (webServiceProxy.getServerManager().getServer(serverName) != null) throw new NithiumException("Unable to automatically create server because already exists.");

        return automaticallyCreateServer(serverName);
    }

    public Server automaticallyCreateServer(String name) {
        Random random = new Random();

        int serverPort = 10000 + random.nextInt(29999);
        if (webServiceProxy.getServerManager().getServer(serverPort) != null) throw new NithiumException("Unable to automatically create server because already exists.");

        Server server = new Server();
        server.setName(name);
        server.setPort(serverPort);
        server.setEnvironment(0);
        server.setServerType(0);
        server.setModeType("LOBBY_SERVER");
        server.setMaximumSize(maximumPlayerSizeEachServer);
        server.setRestricted(false);

        webServiceProxy.getServerManager().createServer(server);
        return webServiceProxy.getServerManager().getServer(name);
    }
}
