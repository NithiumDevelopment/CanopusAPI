package id.nithium.api.canopus.webservice;

import id.nithium.api.NithiumAPI;
import id.nithium.api.NithiumHttpResponse;
import id.nithium.api.canopus.webservice.manager.ServerManager;
import id.nithium.api.type.DataType;
import id.nithium.api.canopus.webservice.exception.BadCodeException;
import id.nithium.api.canopus.webservice.model.Status;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

@Getter
@Setter
public class CanopusAPI {

    @Getter
    private static CanopusAPI api;
    private NithiumAPI nithiumAPI;
    private DataType dataType = DataType.DATA_3;
    private final ServerManager serverManager;
    private final String API_KEY;

    public CanopusAPI(final CloseableHttpClient httpClient, String API_KEY) {
        api = this;
        this.API_KEY = API_KEY;
        nithiumAPI = new NithiumAPI(httpClient);
        serverManager = new ServerManager();
    }

    public Status getStatus() throws BadCodeException {
        NithiumHttpResponse<Status> response = nithiumAPI.GET(dataType, "status", API_KEY, Status.class);

        if (response.response().getCode() == 200) {
            return response.obj();
        } else {
            throw new BadCodeException(dataType, response.response().getCode());
        }
    }

    public void setStatus(@NonNull Status status) {
        nithiumAPI.PUT(dataType, "status", API_KEY, status);
    }
}
