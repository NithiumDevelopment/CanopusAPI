package id.nithium.api.canopus.webservice.model;

import lombok.Data;

@Data
public class Server {

    private String name;
    private int port, environment, serverType;
    private String modeType;
    private int maximumSize;
    private boolean restricted;
}
