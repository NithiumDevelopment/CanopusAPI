package id.nithium.api.canopus.webservice.manager;

import id.nithium.api.NithiumHttpResponse;
import id.nithium.api.canopus.webservice.CanopusAPI;
import id.nithium.api.canopus.webservice.model.Server;
import id.nithium.api.exception.NithiumException;
import id.nithium.api.type.DataType;
import id.nithium.api.canopus.webservice.exception.BadCodeException;

import java.util.List;

public class ServerManager {

    private final CanopusAPI api = CanopusAPI.getApi();

    public List<Server> getServers() {
        NithiumHttpResponse<List> response = api.getNithiumAPI().GET(DataType.DATA_3, "servers", api.getAPI_KEY(), List.class);
        if (response.response().getCode() == 200) {
            return response.obj();
        } else {
            throw new BadCodeException(api.getDataType(), response.response().getCode());
        }
    }

    public Server getServer(String name) {
        NithiumHttpResponse<Server> response = api.getNithiumAPI().GET(DataType.DATA_3, "servers/" + name, api.getAPI_KEY(), Server.class);

        if (response.response().getCode() == 200) {
            Server server = response.obj();

            return server;
        } else {
            if (response.response().getCode() == 404) {
                return null;
            } else {
                throw new BadCodeException(api.getDataType(), response.response().getCode());
            }
        }
    }

    public Server getServer(int port) {
        NithiumHttpResponse<Server> response = api.getNithiumAPI().GET(DataType.DATA_3, "servers/" + port, api.getAPI_KEY(), Server.class);

        if (response.response().getCode() == 200) {
            Server server = response.obj();

            return server;
        } else {
            if (response.response().getCode() == 404) {
                return null;
            } else {
                throw new BadCodeException(api.getDataType(), response.response().getCode());
            }
        }
    }

    public void createServer(Server server) {
        if (getServer(server.getName()) != null) throw new NithiumException("Unable to create new server, because the server is already exists (" + server.getName() + ")");

        api.getNithiumAPI().POST(api.getDataType(), "servers", api.getAPI_KEY(), server);
    }

    public void changeServer(Server server) {
        if (getServer(server.getName()) == null) throw new NithiumException("Unable to change server, because the server is not exists (" + server.getName() + ")");

        api.getNithiumAPI().PUT(api.getDataType(), "servers/" + server.getName(), api.getAPI_KEY(), server);
    }

    public void deleteServer(String name) {
        if (getServer(name) == null) throw new NithiumException("Unable to delete server, because the server is not exists (" + name + ")");

        api.getNithiumAPI().DELETE(api.getDataType(), "servers/" + name, api.getAPI_KEY());
    }

    public void startServer(String name) {
        if (getServer(name) == null) throw new NithiumException("Unable to start server, because the server is not exists (" + name + ")");

        api.getNithiumAPI().POST(api.getDataType(), "servers/" + name + "/start", api.getAPI_KEY(), name);
    }

    public void stopServer(String name) {
        if (getServer(name) == null) throw new NithiumException("Unable to stop server, because the server is not exists (" + name + ")");

        api.getNithiumAPI().POST(api.getDataType(), "servers/" + name + "/stop", api.getAPI_KEY(), name);
    }
}
