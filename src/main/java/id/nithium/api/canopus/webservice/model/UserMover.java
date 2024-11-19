package id.nithium.api.canopus.webservice.model;

import lombok.Data;

import java.util.UUID;

@Data
public class UserMover {

    private UUID uuid;
    private String serverName;

}
